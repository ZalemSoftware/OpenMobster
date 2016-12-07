/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.connection;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;
import java.io.IOException;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;


import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.configuration.AppSystemConfig;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class NotificationListener extends Service
{
	private Worker worker;
	
	//pushThread
	private Thread pushThread;
	
	//poll timer
	private Timer pollTimer;
	
	private Date lastNotification;
	
	public NotificationListener()
	{
	}
	
	public static NotificationListener getInstance()
	{
		return (NotificationListener)Registry.
		getActiveInstance().lookup(NotificationListener.class);
	}
	
	public void start()
	{
		Context context = Registry.getActiveInstance().getContext();
		Configuration configuration = Configuration.getInstance(context);
		if(configuration.isActive())
		{			
			if(configuration.isInPushMode())
			{								
				//Making sure polling is deactiavted
				this.pollTimer = null; 
				
				//Starting the Push Thread
				this.worker = new Worker();
				this.pushThread = new Thread(this.worker);
				this.pushThread.start();
				
				DeadSocketScheduler.getInstance().schedule();
			}
			else
			{								
				//Making Sure Push Thread is deactivated
				this.pushThread = null;
				
				//Start Polling according to configuration
				this.worker = new Worker();
				
				//Schedule using the specified poll interval
				this.pollTimer = new Timer();				
				this.pollTimer.scheduleAtFixedRate(this.worker, 5000, configuration.getCometPollInterval());				
				
				//System.out.println("-------------------------------------------");
				//System.out.println("PollingThread set to run every: "+configuration.getCometPollInterval()+"(ms)");
				//System.out.println("-------------------------------------------");
			}
		}		
	}
	
	public void stop()
	{
		//Stop the listening worker thread
		Context context = Registry.getActiveInstance().getContext();
		if(this.worker != null)
		{
			try
			{
				this.worker.isContainerStopping = true;
				
				if(this.worker.notifySession != null)
				{
					this.worker.notifySession.close();
				}
				
				DeadSocketScheduler.getInstance().clear();
			}
			catch(Exception e)
			{
				//Nothing to do...maybe some cleanup failure here
				//shouldn't disrupt the application
			}
			finally
			{
				//Wait for the worker thread to cleanup
				if(Configuration.getInstance(context).isInPushMode())
				{
					//System.out.println("Waiting to kill the push daemon");
					//try{this.pushThread.join();}catch(Exception e){}
					//System.out.println("Push Daemon killed");
				}
				else
				{
					try{this.pollTimer.cancel();}catch(Exception e){}
				}
			}
		}		
		
		//Cleanup
		this.worker = null;
		this.lastNotification = null;
		this.pushThread = null;
		this.pollTimer = null;
	}
	
	public void restart()
	{
		try
		{			
			this.stop();
		}
		catch(Exception e)
		{
			//Nothing to do...maybe some cleanup failure here
			//shouldn't disrupt the application
		}
		finally
		{
			Registry.getActiveInstance().restart(new NotificationListener());
		}
	}
			
	public boolean isActive()
	{
		Context context = Registry.getActiveInstance().getContext();
		Configuration conf = Configuration.getInstance(context);
		if(conf.isInPushMode())
		{
			if(this.worker != null && this.worker.notifySession != null && !this.worker.isDead)
			{			
				return true;
			}
		}
		else
		{
			if(this.worker != null && this.pollTimer != null)
			{			
				return true;
			}
		}
		return false;
	}
				
	Date getLastNotificationTimestamp()
	{
		return this.lastNotification;
	}
	
	public void sendKeepAlivePacket() throws NetworkException
	{
		Log.d("org.openmobster.android", "Trying to send a keep-alive packet if possible...");
		NetSession notifySession = this.worker.notifySession;
		if(notifySession != null)
		{
			Log.d("org.openmobster.android", "Keep-Alive packet successfully sent");
			notifySession.sendOneWay("");
		}
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	private class Worker extends TimerTask
	{		
		private NetSession notifySession;
		private boolean isContainerStopping;
		private boolean isDead;
		
		private WifiLock wifiLock;
				
		public void run()
		{
			Context context = Registry.getActiveInstance().getContext();
			if(Configuration.getInstance(context).isInPushMode())
			{
				this.startPushDaemon();
			}
			else
			{				
				this.startPollSession();
			}
		}
		
		private void lock()
		{
			Context context = Registry.getActiveInstance().getContext();
			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			if(wifiManager.isWifiEnabled())
			{
				this.wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "org.openmobster.android");
				this.wifiLock.acquire();
			}
		}
		
		private void unlock()
		{
			if(this.wifiLock != null && this.wifiLock.isHeld())
			{
				this.wifiLock.release();
			}
		}
		
		private void startPushDaemon()
		{
			try
			{
				//acquire a wifilock
				this.lock();
				
				Context context = Registry.getActiveInstance().getContext();
				boolean secure = Configuration.getInstance(context).isSSLActivated();
				this.notifySession = NetworkConnector.getInstance().openSession(secure);				
				
				//Start the notification socket
				Configuration conf = Configuration.getInstance(context);
				String deviceId = conf.getDeviceId();
				String authHash = conf.getAuthenticationHash();
			    String channel = this.getActiveChannels();				
				
				String command = 
				"<request>" +
					"<header>" +
						"<name>device-id</name>"+
						"<value><![CDATA["+deviceId+"]]></value>"+
					"</header>"+
					"<header>" +
						"<name>nonce</name>"+
						"<value><![CDATA["+authHash+"]]></value>"+
					"</header>"+
					"<header>" +
						"<name>command</name>"+
						"<value>notify</value>"+
					"</header>"+
					"<header>" +
						"<name>channel</name>"+
						"<value>"+channel+"</value>"+
					"</header>"+
					"<header>" +
						"<name>platform</name>"+
						"<value>android</value>"+
					"</header>"+
						"<header>" +
						"<name>device</name>"+
						"<value>android</value>"+
					"</header>";
					
					/*
					 * Estrutura adicionada na versão 2.4-M3.1
					 * Se há um token de autenticação sendo utilizado atualmente, envia-o para o servidor. 
					 */
					if (conf.getAuthenticationToken() != null) {
						command +=
							"<header>" +
								"<name>authToken</name>"+
								"<value><![CDATA["+ conf.getAuthenticationToken() +"]]></value>"+
							"</header>";
					}
					
					command +=
				"</request>";
			    //String command = "processorid=/testdrive/push";
				
				//Used for debugging the daemon messages
				//System.out.println("Starting a Push Session---------------------------------------------");
				//System.out.println(command);
				//System.out.println("---------------------------------------------------------------------");
												
				this.notifySession.sendOneWay(command);
				//this.notifySession.sendOneWay("<push><caller name='android'/></push>");
				
																								
				//This is a blocking thread that receives data/commands pushed to it from the server				
				do
				{					
					String data = this.notifySession.waitForNotification();
					
					//System.out.println("PushDaemon---------------------------------------------");
					//System.out.println(data);
					//System.out.println("-------------------------------------------------------");
					
					if(data != null && data.trim().equals("status=401"))
					{
						//access denied..kill the push daemon
						return;
					}
																									
					if(this.isContainerStopping)
					{
						break;
					}
																														
					//Make sure a stopping of this thread was not ordered before consuming this notification
					lastNotification = new Date();
															
					//Process incoming data packets
					if(data.trim().length() != 0)
					{												
						String incomingNotification = data.trim();
						
						//send the packet for application processing							
						if(incomingNotification.indexOf(Constants.command) != -1)
						{														
							CommandProcessor.getInstance().process(data.trim());
						}						
					}					
				}while(true);			
			}			
			catch(Exception e)
			{
				Log.e("org.openmobster.android", "Push Socket Exception", e);
			}
			finally
			{
				this.isDead = true;
				//System.out.println("-------------------------------------------------------------");
				//System.out.println("Stopping the Push Session!!!");
				//System.out.println("-------------------------------------------------------------");	
				
				//release the wifilock
				this.unlock();
				
				try
				{
					//close the notification net session
					if(this.notifySession != null)
					{
						this.notifySession.close();
					}										
				}
				catch(Exception e)
				{							
				}
				
				//Set a one time executing AlarmManager with a Partial WakeLock to re-establish the connection, if this is not a network outage
				boolean isReconnectActivated = AppSystemConfig.getInstance().isPushReconnectActivated();
				if(isReconnectActivated)
				{
					ActivatePushSocketScheduler.getInstance().schedule();
				}
			}
		}
		
		private void startPollSession()
		{
			try
			{
				Context context = Registry.getActiveInstance().getContext();
				Configuration configuration = Configuration.getInstance(context);
				boolean secure = configuration.isSSLActivated();
				this.notifySession = NetworkConnector.getInstance().openSession(secure);				
				
				//Start the notification socket
				String deviceId = configuration.getDeviceId();
				String authHash = configuration.getAuthenticationHash();
			    String channel = this.getActiveChannels();				
				
				String command = 
				"<request>" +
					"<header>" +
						"<name>device-id</name>"+
						"<value><![CDATA["+deviceId+"]]></value>"+
					"</header>"+
					"<header>" +
						"<name>nonce</name>"+
						"<value><![CDATA["+authHash+"]]></value>"+
					"</header>"+
					"<header>" +
						"<name>command</name>"+
						"<value>notify</value>"+
					"</header>"+
					"<header>" +
						"<name>channel</name>"+
						"<value>"+channel+"</value>"+
					"</header>"+
					"<header>" +
						"<name>platform</name>"+
						"<value>android</value>"+
					"</header>"+
						"<header>" +
						"<name>device</name>"+
						"<value>android</value>"+
					"</header>";
					
					/*
					 * Estrutura adicionada na versão 2.4-M3.1
					 * Se há um token de autenticação sendo utilizado atualmente, envia-o para o servidor. 
					 */
					if (configuration.getAuthenticationToken() != null) {
						command +=
							"<header>" +
								"<name>authToken</name>" +
								"<value><![CDATA[" + configuration.getAuthenticationToken() + "]]></value>" +
							"</header>";
					}
					
					command +=
				"</request>";
				
				//Used for debugging the daemon messages
				//System.out.println("Starting a Poll Session---------------------------------------------");
				//System.out.println(command);
				//System.out.println("Platform Version: "+DeviceInfo.getPlatformVersion());
				//System.out.println("Software Version: "+DeviceInfo.getSoftwareVersion());
				//System.out.println("Device Name: "+DeviceInfo.getDeviceName());
				//System.out.println("---------------------------------------------------------------------");
												
				this.notifySession.sendOneWay(command);
																				
				//This is a blocking thread that receives data/commands pushed to it from the server				
				String data = this.notifySession.waitForPoll();
				
				if(this.isContainerStopping)
				{					
					return;
				}
																													
				//Make sure a stopping of this thread was not ordered before consuming this notification
				lastNotification = new Date();
														
				//Process incoming data packets
				if(data.trim().length() != 0)
				{
					//Used for debugging the daemon messages
					//System.out.println("PollSession---------------------------------------------");
					//System.out.println(data);
					//System.out.println("--------------------------------------------------------");
					
					String incomingNotification = data.trim();
															
					//send the packet for application processing							
					if(incomingNotification.indexOf(Constants.command) != -1)
					{							
						CommandProcessor.getInstance().process(data.trim());
					}						
				}				
			}			
			catch(Exception e)
			{				
			}
			finally
			{
				if(this.isContainerStopping)
				{
					this.cancel();
				}
				this.isDead = true;
				//System.out.println("-------------------------------------------------------------");
				//System.out.println("Stopping the Poll Session!!!");
				//System.out.println("-------------------------------------------------------------");			
				try
				{
					//close the notification net session
					if(this.notifySession != null)
					{
						this.notifySession.close();
					}										
				}
				catch(Exception e)
				{							
				}
			}						
		}
		
		private String getActiveChannels()
		{
			Context context = Registry.getActiveInstance().getContext();
			List<String> myChannels = Configuration.getInstance(context).
			getMyChannels();
			if(myChannels != null)
			{
				StringBuffer buffer = new StringBuffer();
				for(String channel:myChannels)
				{
					buffer.append(channel);
					buffer.append("|");					
				}
				return buffer.toString();								
			}
			return "";
		}
	}	
}
