/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.d2d;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.push.notification.Constants;
import org.openmobster.core.push.notification.Notification;
import org.openmobster.core.push.notification.Notifier;
import org.openmobster.core.security.identity.Identity;
import org.openmobster.core.security.identity.IdentityController;

/**
 *
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/d2d/users")
public final class D2DUsers implements MobileServiceBean
{
	public static Logger log = Logger.getLogger(D2DUsers.class);
	
	public void start()
	{
		log.info("********************************************************");
		log.info("Device-To-Device Users successfully started....");
		log.info("********************************************************");
	}
	
	public void stop()
	{
		
	}
	
	@Override
	public Response invoke(Request request)
	{
		IdentityController idm = IdentityController.getInstance();
		List<Identity> users = idm.readAll();
		if(users == null || users.isEmpty())
		{
			return null;
		}
		
		List<String> usernames = new ArrayList<String>();
		for(Identity local:users)
		{
			usernames.add(local.getPrincipal());
		}
		
		Response response = new Response();
		response.setListAttribute("users", usernames);
		
		return response;
	}
}
