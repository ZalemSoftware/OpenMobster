/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.android.api.d2d;

import java.util.List;
import java.util.ArrayList;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.d2d.D2DSession;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;
import org.openmobster.android.api.rpc.MobileService;

import android.content.Context;

/**
 * The Device-To-Device Push Service. This is used for sending the push message.
 *
 * @author openmobster@gmail.com
 */
public final class D2DService extends Service
{
	@Override
	public void start()
	{	
	}

	@Override
	public void stop()
	{	
	}
	
	public static D2DService getInstance()
	{
		return (D2DService)Registry.getActiveInstance().lookup(D2DService.class);
	}
	//----------------------------------------------------------------------------------------
	/**
	 * Send a message to the designated user
	 * 
	 * @param to The user that is supposed to receive this message
	 * @param message
	 * @throws D2DServiceException
	 */
	public void send(String to,String message) throws D2DServiceException
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			Configuration conf = Configuration.getInstance(context);
			
			//from
			String from = conf.getEmail();
			
			//source_deviceid
			String source_deviceid = conf.getDeviceId();
			
			//destination_deviceid
			String destination_deviceid = null;
			D2DMessage msg = D2DSession.getSession().getLatestMessage();
			if(msg != null)
			{
				String msgFrom = msg.getFrom();
				if(msgFrom.equals(to))
				{
					destination_deviceid = msg.getSenderDeviceId();
				}
			}
			
			//app_id
			String appId = context.getPackageName();
			
			//Setup the Request object
			Request request = new Request("/d2d/send");
			request.setAttribute("to", to);
			request.setAttribute("from", from);
			request.setAttribute("message", message);
			request.setAttribute("source_deviceid", source_deviceid);
			request.setAttribute("app_id", appId);
			if(destination_deviceid !=null && destination_deviceid.trim().length() !=0)
			{
				request.setAttribute("destination_deviceid", destination_deviceid);
			}
			
			MobileService.invoke(request);
		}
		catch(Exception e)
		{
			throw new D2DServiceException(D2DService.class.getName(),"send",new Object[]{
				"Exception: "+e.getMessage()
			});
		}
	}
	
	/**
	 * Get a list of activated users from the Cloud
	 * 
	 * @return
	 * @throws D2DServiceException
	 */
	public List<String> userList() throws D2DServiceException
	{
		try
		{
			//Setup the Request object
			Request request = new Request("/d2d/users");
			
			//Invoke
			Response response = MobileService.invoke(request);
			
			//get the user list
			List<String> users = response.getListAttribute("users");
			if(users == null || users.isEmpty())
			{
				return null;
			}
			
			return users;
		}
		catch(Exception e)
		{
			throw new D2DServiceException(D2DService.class.getName(),"userList",new Object[]{
				"Exception: "+e.getMessage()
			});
		}
	}
}
