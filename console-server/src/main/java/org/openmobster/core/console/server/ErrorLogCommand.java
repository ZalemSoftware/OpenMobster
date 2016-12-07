/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.server;

import org.apache.log4j.Logger;


import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;
import org.openmobster.core.security.Provisioner;
import org.openmobster.core.security.device.Device;

/**
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/console/errorlog")
public class ErrorLogCommand implements MobileServiceBean
{
	private static Logger logger = Logger.getLogger(ErrorLogCommand.class);
	
	private Provisioner provisioner;
	
	public ErrorLogCommand()
	{
		
	}
	
	public Provisioner getProvisioner()
	{
		return provisioner;
	}


	public void setProvisioner(Provisioner provisioner)
	{
		this.provisioner = provisioner;
	}
	
	public Response invoke(Request request)
	{
		try
		{
			this.processUserInfo(request);
			
			String errorLog = request.getAttribute("error.log");
			String userId = request.getAttribute("user.id");
			String deviceId = request.getAttribute("device.id");
			String platform = request.getAttribute("device.platform");
			
			//Spit out the log into the running server's system.out console
			logger.info("**********************Error Log*******************************");
			logger.info("User: "+userId);
			logger.info("Device: "+deviceId);
			logger.info("Platform: "+platform);
			logger.info(errorLog);
			logger.info("**************************************************************");
			
			//TODO: store this in a database for access by a monitoring tool. To be implemented in 2.2 with the GWT/SmartGWT based Console.
			
			return null;
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
	}
	
	private void processUserInfo(Request request)
	{
		//This is useless for now
		/*String userId = request.getAttribute("user.id");
		
		if(userId != null && userId.trim().length()>0)
		{
			Device device = this.provisioner.getDeviceController().readByIdentity(userId);
			request.setAttribute("device.id", device.getIdentifier());
		}*/
	}
}