/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.api.ui.framework.push;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Set;

import org.openmobster.android.api.sync.MobileBeanMetaData;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;
import org.openmobster.core.mobileCloud.android.configuration.AppSystemConfig;
import org.openmobster.core.mobileCloud.api.ui.framework.AppConfig;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;

/**
 *
 * @author openmobster@gmail.com
 */
public class SyncPushBroadcastReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{	
		try
		{
			//Some bootstrapping
			if(!AppConfig.getInstance().isActive())
			{
				AppConfig.getInstance().init();
			}
			if(!AppSystemConfig.getInstance().isActive())
			{
				AppSystemConfig.getInstance().start();
			}
			
			Bundle shared = intent.getBundleExtra("bundle");
			String pushMetaDataSize = (String)shared.get("pushMetaDataSize");
			String channel = null;
			if(pushMetaDataSize != null && pushMetaDataSize.trim().length()>0)
			{
				int size = Integer.parseInt(pushMetaDataSize);								
				
				List<MobileBeanMetaData> beanMetaData = new 
				ArrayList<MobileBeanMetaData>();
				for(int i=0;i<size;i++)
				{
					String service = (String)shared.
					get("pushMetaData["+i+"].service");
					if(!this.isMyChannel(service))
					{
						continue;
					}
					
					channel = service;
					String id = (String)shared.get("pushMetaData["+i+"].id");
					String isDeleted = (String)shared.
					get("pushMetaData["+i+"].isDeleted");
					
					MobileBeanMetaData beanInfo = new MobileBeanMetaData(service, id);
					beanInfo.setDeleted(Boolean.parseBoolean(isDeleted));
					beanMetaData.add(beanInfo);
					
					//System.out.println("Service="+beanInfo.getService());
					//System.out.println("Id="+beanInfo.getId());
					//System.out.println("Deleted="+beanInfo.isDeleted());
				}
				
				if(beanMetaData.isEmpty())
				{
					return;
				}
				
				this.notify(context,channel,beanMetaData);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "onReceive", new Object[]{						
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					} 
			));
		}
	}
	
	private void notify(Context context,String channel,List<MobileBeanMetaData> beanMetaData) throws Exception
	{
		//This should be part of configuring the Push Service, in openmobster-app.xml 
		String launchActivity = AppSystemConfig.getInstance().getPushLaunchActivityClassName();
		String pushIcon =  AppSystemConfig.getInstance().getPushIconName();
		
		Class activityClass = null;
		if(launchActivity != null && launchActivity.trim().length() > 0)
		{
			try
			{
				activityClass = Class.forName(launchActivity);
			}
			catch(Exception e)
			{
				activityClass = null;
			}
		}
		
		if(pushIcon == null || pushIcon.trim().length() == 0)
		{
			pushIcon = "appicon";
		}
	
		
		PackageManager pm = context.getPackageManager();
		CharSequence appName = pm.getApplicationLabel(context.getApplicationInfo());
		
		//Get the NotificationManager service
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(ns);
		
		//Setup the Notification instance
		int icon = this.findDrawableId(context, pushIcon);
		long when = System.currentTimeMillis();
		
		//Setup the intent for this notification
		String syncPushMessage = AppSystemConfig.getInstance().getSyncPushMessage(channel);
		CharSequence contentText = null;
		if(syncPushMessage == null || syncPushMessage.trim().length()==0)
		{
			contentText = ""+ beanMetaData.size()+" Update(s)";
		}
		else
		{
			contentText = MessageFormat.format(syncPushMessage, beanMetaData.size());
		}
		
		Intent notificationIntent = null;
		if(activityClass != null)
		{
			notificationIntent = new Intent(context, activityClass);
			notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			notificationIntent.setAction(Intent.ACTION_VIEW);
		}
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		Notification.Builder builder = new Notification.Builder(context);
		builder.setSmallIcon(icon);
		builder.setTicker(appName);
		builder.setWhen(when);
		builder.setContentTitle(appName);
		builder.setContentText(contentText);
		builder.setContentIntent(contentIntent);
		Notification notification = builder.getNotification();		
		
		//Notification Flags
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.defaults |= Notification.DEFAULT_SOUND;
		
		notificationManager.notify(GeneralTools.generateUniqueId().hashCode(), 
		notification);
	}
	
	private boolean isMyChannel(String channel)
	{
		AppSystemConfig appConfig = AppSystemConfig.getInstance();
		Set<String> myChannels = appConfig.getChannels();
		
		if(myChannels != null && !myChannels.isEmpty())
		{
			return myChannels.contains(channel);
		}
		
		return false;
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
