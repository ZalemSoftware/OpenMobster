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

import org.openmobster.core.mobileCloud.android.service.Registry;

/**
 *
 * @author openmobster@gmail.com
 */
final class ActivatePushSocketScheduler
{
	private static ActivatePushSocketScheduler singleton = null;
	
	private PendingIntent alarmIntent;
	
	private ActivatePushSocketScheduler()
	{
		Context context = Registry.getActiveInstance().getContext();
		Intent intent = new Intent(context, ActivatePushSocket.class);
		this.alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	static ActivatePushSocketScheduler getInstance()
	{
		if(singleton == null)
		{
			synchronized(ActivatePushSocketScheduler.class)
			{
				if(singleton == null)
				{
					singleton = new ActivatePushSocketScheduler();
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
	}
	
	void clear()
	{
		Context context = Registry.getActiveInstance().getContext();
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(this.alarmIntent);
	}
}
