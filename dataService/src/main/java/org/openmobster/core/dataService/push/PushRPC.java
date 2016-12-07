/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.dataService.push;

import java.util.Map;
import java.util.HashMap;

import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.core.push.notification.Notification;
import org.openmobster.core.push.notification.Notifier;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.identity.Identity;
import org.openmobster.core.security.identity.IdentityController;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.common.Utilities;
import org.openmobster.core.common.XMLUtilities;

import org.openmobster.core.common.ServiceManager;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class PushRPC
{
	private DeviceController deviceController;
	private Notifier notifier;
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	
	public DeviceController getDeviceController()
	{
		return deviceController;
	}

	public void setDeviceController(DeviceController deviceController)
	{
		this.deviceController = deviceController;
	}

	public Notifier getNotifier()
	{
		return notifier;
	}

	public void setNotifier(Notifier notifier)
	{
		this.notifier = notifier;
	}

	public void push(Identity identity, PushCommandContext context)
	{
		//PushRPC the whole concept is garbage
		/*Device device = this.deviceController.readByIdentity(identity.getPrincipal());
		String command = context.getCommand();
		
		Notification notification = Notification.createPushRPCNotification(device, command);
		
		Map<String,String> cour = new HashMap<String, String>();
		String[] keys = context.getNames();
		for(String local:keys)
		{
			String value = context.getAttribute(local);
			cour.put(local, value);
		}
		cour.put("service", command);
		String payload = XMLUtilities.marshal(cour);
		
		payload = Utilities.encodeBinaryData(payload.getBytes());
		
		notification.setMetaData("rpc-request", payload);
		
		this.notifier.process(notification);*/
	}
	
	public static void startPush(String identity, PushCommandContext context)
	{
		PushRPC service = (PushRPC)ServiceManager.locate("push_rpc");
		IdentityController idm = (IdentityController)ServiceManager.locate("security://IdentityController");
		
		Identity user = idm.read(identity);
		service.push(user, context);
	}
}
