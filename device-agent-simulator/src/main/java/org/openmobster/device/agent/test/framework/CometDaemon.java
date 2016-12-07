/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.test.framework;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import org.openmobster.device.agent.Tools;
import org.openmobster.core.common.IOUtilities;
import org.openmobster.core.common.PerfLogInterceptor;

/**
 * @author openmobster@gmail.com
 */
public class CometDaemon 
{
	private static Logger log = Logger.getLogger(CometDaemon.class);
	
	private Thread cometWorker;
	private Configuration configuration;
	
	public CometDaemon()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
			
	public Configuration getConfiguration() 
	{
		return configuration;
	}

	public void setConfiguration(Configuration configuration) 
	{
		this.configuration = configuration;
	}

	public void startSubscription()
	{
		log.info("-------------------------------------------------------");
		log.info("Starting the Notification Daemon.........");
		log.info("-------------------------------------------------------");
		
		this.cometWorker = new Thread(new CometWorker());
		this.cometWorker.start();
		
		while(this.cometWorker.getState() == Thread.State.NEW);
	}
	
	public void waitforCometDaemon() throws Exception
	{
		this.cometWorker.join();
	}
	//-------------------------------------------------------------------------------------------------------------
	private class CometWorker implements Runnable
	{				
		CometWorker()
		{
		}
		
		public void run()
		{
			NetSession session = null;
			try
			{
				session = new NetSession();	
				
				String authHash = configuration.getAuthenticationHash();
				String deviceId = configuration.getDeviceId();
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
						"<value>twitterChannel</value>"+
					"</header>"+
				"</request>";
												
				IOUtilities.writePayLoad(command,session.os);																
				do
				{
					String data = IOUtilities.readServerResponse(session.is);
											
					if(data != null && data.trim().length()>0)
					{
						log.info("DeviceSide Push("+deviceId+")---------------------------------------------------------");
						log.info(data);
						
						if(data.indexOf("status=401") != -1)
						{
							break;
						}
					}
					else
					{
						log.info("keep-alive");
					}
					
				}while(true);
			}
			catch(Exception e)
			{
				log.error(this, e);
				
				//Log this
				PerfLogInterceptor.getInstance().logPushFailed();
			}
			finally
			{
				if(session != null)
				{
					try{session.close();}catch(Exception e){}
				}
			}
		}
	}
	//------------------------------------------------------------------------------------------------------------
	private static class NetSession
	{
		private Socket socket;
		private InputStream is;
		private OutputStream os;
		
		private NetSession() throws Exception
		{
			this.socket = Tools.getPlainSocket();
			this.is = this.socket.getInputStream();
			this.os = this.socket.getOutputStream();
		}
		
		private void close() throws Exception
		{
			this.is.close();
			this.os.close();
			this.socket.close();
		}
	}
}
