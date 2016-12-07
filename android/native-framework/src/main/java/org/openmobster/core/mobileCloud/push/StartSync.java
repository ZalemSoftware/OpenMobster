/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.push;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import org.openmobster.core.mobileCloud.android.service.Registry;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/**
 *
 * @author openmobster@gmail.com
 */
public final class StartSync extends Service
{
	private static volatile WakeLock wakeLock;
	
	private boolean busy = false;
	
	public StartSync()
	{
	}
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
	}

	@Override
	public void onDestroy() 
	{
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		super.onStartCommand(intent, flags, startId);
		
		if(intent != null)
		{
			String channel = intent.getStringExtra("channel");
			String silent = intent.getStringExtra("silent");
			if(!busy)
			{
				busy = true;
				Thread t = new Thread(new Task(channel,silent));
				t.start();
			}
			else
			{
				//re-broadcast, and leave quickly
				this.sendSyncBroadcast(channel, silent);
			}
		}
		
		return Service.START_STICKY;
	}
	
	private void sendSyncBroadcast(String channel,String silent)
	{
		//Prepare the bundle
		Bundle bundle = new Bundle();
		bundle.putString("channel", channel);
		bundle.putString("silent", silent);
		
		//Prepare the intent
		Intent intent = new Intent("org.openmobster.sync.start");
		intent.putExtra("bundle", bundle);
		
		//Send the broadcast
		//create an alarm pending intent
		Context context = Registry.getActiveInstance().getContext();
		PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), alarmIntent);
	}
	
	private class Task implements Runnable
	{
		private String channel;
		private String silent;
		
		private Task(String channel,String silent)
		{
			this.channel = channel;
			this.silent = silent;
		}
		
		public void run()
		{
			try
			{
				if(channel == null)
				{
					return;
				}
				
				SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
						SyncInvocation.twoWay, channel);
								
				if(silent == null || silent.equals("false"))
				{
					syncInvocation.activateBackgroundSync();
				}
				else
				{
					syncInvocation.deactivateBackgroundSync();
				}
				
				Bus.getInstance().invokeService(syncInvocation);
			}
			catch(Throwable t)
			{
				t.printStackTrace(System.out);
				SystemException syse = new SystemException(this.getClass().getName(),"run",new Object[]{
					"Exception: "+t.toString(),
					"Message: "+t.getMessage()
				});
				ErrorHandler.getInstance().handle(syse);
			}
			finally
			{
				//Alterado na versão 2.4-M3.1 do OpenMobster
				releaseWakeLock();
				
				StartSync.this.busy = false;
			}
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------
	static synchronized void acquireWakeLock(Context context)
	{
		if(wakeLock != null)
		{
			if(!wakeLock.isHeld())
			{
				wakeLock.acquire();
			}
			return;
		}
		
		//Setup a WakeLock
		PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, StartSync.class.getName());
		wakeLock.setReferenceCounted(true);
		
		//acquire the lock
		if(!wakeLock.isHeld())
		{
			wakeLock.acquire();
		}
	}
	
	
	/*
	 * Método adicionado na versão 2.4-M3.1 do OpenMobster.
	 */

	//É necessário um método sincronizado para liberar o lock também se não pode ocorrer NullPointerException no acquireWakeLock.
	static synchronized void releaseWakeLock() {
		if(wakeLock != null)
		{
			if(wakeLock.isHeld())
			{
				wakeLock.release();
			}
			wakeLock = null;
		}
	}
}
