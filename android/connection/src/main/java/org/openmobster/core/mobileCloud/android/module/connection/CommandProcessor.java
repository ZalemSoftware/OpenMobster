/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.connection;

import java.util.Map;
import java.util.HashMap;
import java.net.URLDecoder;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.util.StringUtil;
import org.openmobster.core.mobileCloud.android.util.XMLUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class CommandProcessor extends Service 
{	
	
	/*
	 * Adicionado na versão 2.4-M3.1.
	 */
	public static final String PUSH_MESSAGE = "org.openmobster.push.PUSH_MESSAGE";
	
	public CommandProcessor()
	{
		
	}
	
	public void start() 
	{	
	}
	
	public void stop() 
	{			
	}
	
	
	public static CommandProcessor getInstance()
	{
		return (CommandProcessor)Registry.getActiveInstance().
		lookup(CommandProcessor.class);
	}
	
	
	public void process(String command)
	{	
		//Process the Command on a separate thread, not on the Notification Listening Thread		
		String[] tokens = StringUtil.tokenize(command, Constants.separator);
		Map<String,String> input = new HashMap<String,String>();
		
		if(tokens != null)
		{
			for(String token:tokens)
			{
				try
				{
					token = URLDecoder.decode(token, "UTF-8");
				}
				catch(Exception e)
				{
					e.printStackTrace(System.out);
					return;
				}
				int index = token.indexOf('=');
				String name = token.substring(0, index);
				String value = token.substring(index+1);
				input.put(name.trim(), value.trim());
			}
			
			if(input.get(Constants.command)!=null)
			{
				String inputCommand = input.get(Constants.command);
				if(inputCommand.equals(Constants.sync))
				{
					this.sync(input);
				}
				else if(inputCommand.equals(Constants.push))
				{
					this.push(input);
				}
				else if(inputCommand.equals(Constants.deviceManagement))
				{
					this.deviceManagement(input);
				}
			}
		}
	}
		
	private void sync(Map<String,String> input)
	{
		try
		{
			String service = input.get(Constants.service);
			String silent = input.get(Constants.silent);
			
			if(service == null)
			{
				SystemException se = new SystemException(this.getClass().getName(),"run", 
				new Object[]{
					"Error=Service to synchronize with is missing!!!"
				});
				ErrorHandler.getInstance().handle(se);
				return;
			}
							
			//Prepare the bundle
			Bundle bundle = new Bundle();
			bundle.putString("channel", service);
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
		catch(Exception e)
		{
			SystemException se = new SystemException(this.getClass().getName(),"run", 
					new Object[]{
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					});
			ErrorHandler.getInstance().handle(se);
		}	
	}
	
	private void push(Map<String,String> input)
	{
		try
		{
			String message = input.get("message");
			String extras = input.get("extras");
			Map<String, String> extrasData = XMLUtil.parseMap(extras);
			
			String title = extrasData.get("title");
			String details = extrasData.get("detail");
			String appId = extrasData.get("app-id");
			if(appId == null || appId.trim().length() == 0)
			{
				//App Id must be specified. Thats the only way to know which App handles the notification
				return;
			}
			
			if(title == null || title.trim().length()==0)
			{
				title = message;
			}
			
			if(details == null || details.trim().length()==0)
			{
				details = "";
			}
			
			Context context = Registry.getActiveInstance().getContext();
			
			/*
			 * Alteração feita na versão 2.4-M3.1.
			 * Utiliza o mecanismo de "setPackage" do Intent para limitar o recebimento da mensagem apenas para esta aplicação.
			 * Desta forma, o esquema original do OpenMobster de definir o pacote da aplicação como Action do Intent não é mais necessário.
			 */
			Intent pushIntent = new Intent(PUSH_MESSAGE);
			pushIntent.setPackage(appId);
//			Intent pushIntent = new Intent(appId);
			
			pushIntent.putExtra("message", message);
			pushIntent.putExtra("title", title);
			pushIntent.putExtra("detail", details);
			pushIntent.putExtra("app-id", appId);
			
			//create an alarm pending intent
			PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, pushIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), alarmIntent);
		}
		catch(Exception e)
		{
			SystemException se = new SystemException(this.getClass().getName(),"run", 
					new Object[]{
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					});
			ErrorHandler.getInstance().handle(se);
		}
	}
	
	private void deviceManagement(Map<String,String> input)
	{
		try
		{
			String action = input.get(Constants.action);
			
			//Prepare the intent
			Intent intent = new Intent("org.openmobster.device.management");
			intent.putExtra("action", action);
			
			//Send the broadcast
			Context context = Registry.getActiveInstance().getContext();
			
			PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), alarmIntent);
		}
		catch(Exception e)
		{
			SystemException se = new SystemException(this.getClass().getName(),"run", 
					new Object[]{
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					});
			ErrorHandler.getInstance().handle(se);
		}
	}
}
