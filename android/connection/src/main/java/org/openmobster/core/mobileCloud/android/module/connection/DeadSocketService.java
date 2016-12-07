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
import android.util.Log;

/**
 *
 * @author openmobster@gmail.com
 */
public final class DeadSocketService extends Service
{
	private static volatile WakeLock wakeLock;
	
	private boolean busy = false;
	
	public DeadSocketService()
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
				NotificationListener listener = NotificationListener.getInstance();
				try
				{
					listener.sendKeepAlivePacket();
					
					Log.d("org.openmobster.android", "Push Socket is alive....");
				}
				catch(NetworkException ioe)
				{
					//this means this is a dead socket
					Log.e("org.openmobster.android", ioe.getMessage(),ioe);
					
					Invocation invocation = new Invocation("org.openmobster.core.mobileCloud.android.invocation.CometRecycleHandler");
					Bus.getInstance().invokeService(invocation);
				}
			}
			catch(Exception e)
			{
				Log.e("org.openmobster.android", e.getMessage(),e);
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
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, DeadSocketService.class.getName());
		wakeLock.setReferenceCounted(true);
		
		//acquire the lock
		if(!wakeLock.isHeld())
		{
			wakeLock.acquire();
		}
	}
}
