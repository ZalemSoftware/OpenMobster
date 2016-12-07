/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.module.connection;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.service.Registry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 *
 * @author openmobster@gmail.com
 */
public final class ActivatePushSocket extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		try
		{
			/*
			 * Alteração feita na versão 2.4-M3.1
			 * Só inicia o serviço se estiver o dispositivo estiver ativado na nuvem.
			 */
			if (!isDeviceActivated()) {
				ActivatePushSocketScheduler.getInstance().clear();
				return;
			}
			
			
			System.out.println("********************************************");
			System.out.println("Trying to establish a Push Socket");
			System.out.println("********************************************");
			
			if(NotificationListener.getInstance().isActive())
			{
				System.out.println("********************************************");
				System.out.println("Push Socket is Active!!!!");
				System.out.println("********************************************");
				ActivatePushSocketScheduler.getInstance().clear();
				return;
			}
			
			//check if network is out. if that has happened, clear the alarm and leave,when the network comes back on
			//the event will be processed and a push socket will be re-established
			if(!this.isOnline(context))
			{
				ActivatePushSocketScheduler.getInstance().clear();
				return;
			}
			
			
			
			//Grab a WakeLock
			BackgroundActivatePushSocket.acquireWakeLock(context);
			
			//Now invoke the service and leave
			Intent serviceIntent = new Intent(context,BackgroundActivatePushSocket.class);
			context.startService(serviceIntent);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	private boolean isOnline(Context context)
	{
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) 
	    {
	        return true;
	    }
		return false;
	}
	
	private static boolean isDeviceActivated() {
		Configuration conf = Configuration.getInstance(Registry.getActiveInstance().getContext());
		return conf.isDeviceActivated();
	}
}
