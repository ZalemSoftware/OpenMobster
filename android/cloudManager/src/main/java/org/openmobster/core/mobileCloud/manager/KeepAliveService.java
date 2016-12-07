/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.manager;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 *
 * @author openmobster@gmail.com
 */
public class KeepAliveService extends Service
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		this.stopForeground(true);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		String activityClassName = intent.getStringExtra("activityClassName");
		
		this.startForegroundRegistration(activityClassName);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	private void startForegroundRegistration(String activityClassName)
	{
		try
		{
			Class activityClass = Thread.currentThread().getContextClassLoader().loadClass(activityClassName);
			
			Context context = Registry.getActiveInstance().getContext();
			PackageManager pm = context.getPackageManager();
			CharSequence appName = pm.getApplicationLabel(context.getApplicationInfo());
			
			//Setup the Notification instance
			int icon = this.findDrawableId(this, "icon");
			long when = System.currentTimeMillis();
			
			Intent notificationIntent = new Intent(this, activityClass);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
			Notification.Builder builder = new Notification.Builder(context);
			builder.setSmallIcon(icon);
			builder.setTicker(appName);
			builder.setWhen(when);
			builder.setContentTitle(appName);
			builder.setContentText("OpenMobster Cloud Manager");
			builder.setContentIntent(contentIntent);
			Notification notification = builder.getNotification();		
			
			notification.flags|=Notification.FLAG_NO_CLEAR;
			
			int id = GeneralTools.generateUniqueId().hashCode();
			
			this.startForeground(id, notification);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
	
	private int findDrawableId(Context context, String variable)
	{
		try
		{
			Resources resources = context.getResources();
			int resourceId = resources.getIdentifier(variable, "drawable", context.getPackageName());
			
			return resourceId;
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			return 0;
		}
	}
}
