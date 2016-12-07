/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.moblet;

import android.content.Context;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;
import org.openmobster.android.api.rpc.ServiceInvocationException;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;

/**
 * @author openmobster@gmail
 *
 */
public final class BootupConfiguration
{
	public static synchronized void bootup(String serverIp, String port) 
	throws ServiceInvocationException
	{
		Context context = Registry.getActiveInstance().getContext();
		Configuration conf = Configuration.getInstance(context);
		
		conf.deActivateSSL();
		
		//Unique device id
		String deviceIdentifier = conf.getDeviceId();
		if(deviceIdentifier == null || deviceIdentifier.trim().length() == 0)
		{
			deviceIdentifier = GeneralTools.getDeviceIdentifier(context);
		}
		conf.setDeviceId(deviceIdentifier);
		
		
		conf.setServerIp(serverIp);
		if(port != null && port.trim().length()>0)
		{
			conf.setPlainServerPort(port);
		}
		conf.setActive(false);
		
		conf.save(context);
		
		Request request = new Request("provisioning");
		request.setAttribute("action", "metadata");
		Response response = MobileService.invoke(request);
		
		//Read the Server Response
		String serverId = response.getAttribute("serverId");
		String plainServerPort = response.getAttribute("plainServerPort");
		String secureServerPort = response.getAttribute("secureServerPort");
		String isSSlActive = response.getAttribute("isSSLActive");
		String maxPacketSize = response.getAttribute("maxPacketSize");
		String httpPort = response.getAttribute("httpPort");
		
		//Setup the configuration
		conf.setServerId(serverId);
		conf.setPlainServerPort(plainServerPort);
		if(secureServerPort != null && secureServerPort.trim().length()>0)
		{
			conf.setSecureServerPort(secureServerPort);
		}
		
		if(isSSlActive.equalsIgnoreCase("true"))
		{
			conf.activateSSL();
		}
		else
		{
			conf.deActivateSSL();
		}
		
		conf.setMaxPacketSize(Integer.parseInt(maxPacketSize));
		conf.setHttpPort(httpPort);
				
		conf.save(context);
	}
}
