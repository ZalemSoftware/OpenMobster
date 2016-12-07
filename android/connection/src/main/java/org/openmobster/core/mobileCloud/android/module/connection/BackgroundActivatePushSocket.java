/**
 * Copyright (c) {2003,2013} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.module.connection;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.app.Service;

/**
 *
 * @author openmobster@gmail.com
 */
public final class BackgroundActivatePushSocket extends Service
{
	private static volatile WakeLock wakeLock;
	
	private boolean busy = false;
	
	public BackgroundActivatePushSocket()
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
	
	synchronized void execute()
	{
		if(!busy)
		{
			busy = true;
			Thread t = new Thread(new Task());
			t.start();
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		super.onStartCommand(intent, flags, startId);
		
		if(!busy)
		{
			busy = true;
			Thread t = new Thread(new Task());
			t.start();
		}
		
		return Service.START_STICKY;
	}
	
	private class Task implements Runnable
	{
		public void run()
		{
			try
			{
				Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.CometRecycleHandler");
				Bus.getInstance().invokeService(invocation);
				
				//Check to see if the Push Socket is active
				int counter = 10;
				while(counter > 0)
				{
					boolean isActive = NotificationListener.getInstance().isActive();
					if(isActive)
					{
						//clear the alarm
						ActivatePushSocketScheduler.getInstance().clear();
						return;
					}
					
					Thread.sleep(6000);
					counter--;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace(System.out);
			}
			finally
			{
				if(wakeLock != null)
				{
					if(wakeLock.isHeld())
					{
						wakeLock.release();
					}
					wakeLock = null;
				}
				busy = false;
			}
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, BackgroundActivatePushSocket.class.getName());
		wakeLock.setReferenceCounted(true);
		
		//acquire the lock
		if(!wakeLock.isHeld())
		{
			wakeLock.acquire();
		}
	}
}
