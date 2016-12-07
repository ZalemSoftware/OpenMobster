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
import org.openmobster.core.mobileCloud.android.module.connection.NotificationListener;
import org.openmobster.core.mobileCloud.android_native.framework.NetworkStartupSequence;

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
public final class StartNetwork extends Service
{
	private static volatile WakeLock wakeLock;
	
	private boolean busy = false;
	
	public StartNetwork()
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
				NetworkStartupSequence.getInstance().execute();
				
				//Check to see if the Push Socket is active...this is to stay alive till a socket
				//is established...we will try to stay alive for a maximum of one minute
				//after that is the socket is not established, we can leave
				int counter = 10;
				while(counter > 0)
				{
					boolean isActive = NotificationListener.getInstance().isActive();
					if(isActive)
					{
						return;
					}
					
					Thread.sleep(6000);
					counter--;
				}
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
				if(wakeLock != null)
				{
					if(wakeLock.isHeld())
					{
						wakeLock.release();
					}
					wakeLock = null;
				}
				StartNetwork.this.busy = false;
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
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, StartNetwork.class.getName());
		wakeLock.setReferenceCounted(true);
		
		//acquire the lock
		if(!wakeLock.isHeld())
		{
			wakeLock.acquire();
		}
	}
}
