/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.android.api.d2d;

import org.openmobster.core.mobileCloud.android.module.connection.Constants;
import org.openmobster.core.mobileCloud.d2d.D2DSession;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Receives a broadcast from the on device CloudManager when a new message is received from the Cloud.
 * 
 * If the D2DActivity is in the foreground, it invokes the activity via the callback mechanism.
 * 
 * If not, it sends a notification to the user using the Android Notification system
 *
 * @author openmobster@gmail.com
 */
public final class D2DReceiver extends BroadcastReceiver
{
	
	/*
	 * Adicionado na versão 2.4-M3.1.
	 */
	public static final String D2D_MESSAGE = "org.openmobster.push.D2D_MESSAGE";
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Bundle input = intent.getBundleExtra(Constants.d2dMessage);
		if(input == null)
		{
			//do nothing
			return;
		}
		
		String from = input.getString(Constants.from);
		String to = input.getString(Constants.to);
		String msg = input.getString(Constants.message);
		String source_deviceid = input.getString(Constants.source_deviceid);
		String destination_deviceid = input.getString(Constants.destination_deviceid);
		String timestamp = input.getString(Constants.timestamp);
		String app_id = input.getString(Constants.app_id);
		
		D2DMessage message = new D2DMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setMessage(msg);
		message.setSenderDeviceId(source_deviceid);
		message.setTimestamp(timestamp);
		
		
		D2DSession session = D2DSession.getSession();
		if(session.isActive())
		{
			//App is in the foreground
			session.callback(message);
		}
		else
		{
			//App is in the background, send as a push notification
			String appId = context.getPackageName();
			
			/*
			 * Alteração feita na versão 2.4-M3.1.
			 * Utiliza o mecanismo de "setPackage" do Intent para limitar o recebimento da mensagem apenas para esta aplicação.
			 * Desta forma, o esquema original do OpenMobster de definir o pacote da aplicação como Action do Intent não é mais necessário.
			 */
			Intent pushIntent = new Intent(D2D_MESSAGE);
			pushIntent.setPackage(appId);
//			Intent pushIntent = new Intent(appId);
			
			pushIntent.putExtra("message", message.getMessage());
			pushIntent.putExtra("title", "Device-To-Device Message");
			pushIntent.putExtra("detail", message.toString());
			pushIntent.putExtra("app-id", appId);
			
			context.sendBroadcast(pushIntent);
		}
	}
}
