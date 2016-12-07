/**
 * Copyright (c) {2003,2013} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.module.connection;

import android.content.Context;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import org.openmobster.core.mobileCloud.android.service.Registry;

/**
 *
 * @author openmobster@gmail.com
 */
final class DeadSocketScheduler
{
	private static DeadSocketScheduler singleton = null;
	
	private PendingIntent alarmIntent;
	
	private DeadSocketScheduler()
	{
		Context context = Registry.getActiveInstance().getContext();
		Intent intent = new Intent(context, DeadSocketDetector.class);
		
		/*
		 * Alteração feita na versão 2.4-M3.1.
		 * Utiliza o mecanismo de "setPackage" do Intent para limitar o recebimento da mensagem apenas para esta aplicação.
		 * Desta forma, o esquema original do OpenMobster de definir o pacote da aplicação como Action do Intent não é mais necessário.
		 */
		intent.setPackage(context.getPackageName());
//		intent.setAction(context.getPackageName());
		
		this.alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	static DeadSocketScheduler getInstance()
	{
		if(singleton == null)
		{
			synchronized(DeadSocketScheduler.class)
			{
				if(singleton == null)
				{
					singleton = new DeadSocketScheduler();
				}
			}
		}
		return singleton;
	}
	
	synchronized void schedule()
	{
		this.clear();
		
		//now schedule a repeating alarm
		Context context = Registry.getActiveInstance().getContext();
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 120000, this.alarmIntent);
		
		Log.d("org.openmobster.android", "************************************");
		Log.d("org.openmobster.android", "Starting the DeadSocket Detector....");
		Log.d("org.openmobster.android", "************************************");
	}
	
	synchronized void clear()
	{
		Context context = Registry.getActiveInstance().getContext();
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(this.alarmIntent);
		
		Log.d("org.openmobster.android", "************************************");
		Log.d("org.openmobster.android", "Stopping the DeadSocket Detector....");
		Log.d("org.openmobster.android", "************************************");
	}
}
