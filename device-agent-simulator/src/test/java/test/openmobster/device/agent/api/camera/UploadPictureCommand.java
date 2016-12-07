/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.openmobster.device.agent.api.camera;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.ExecutionContext;
import org.openmobster.cloud.api.camera.CameraCommandContext;
import org.openmobster.cloud.api.camera.CameraCommandResponse;
import org.openmobster.cloud.api.camera.CloudCameraCommand;
import org.openmobster.cloud.api.camera.CloudCameraURI;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.identity.Identity;


/**
 * 
 * @author openmobster@gmail.com
 */
@CloudCameraURI(uri="/upload/picture")
public class UploadPictureCommand implements CloudCameraCommand
{
	private static Logger log = Logger.getLogger(UploadPictureCommand.class);
	
	public void start()
	{
		log.info("************************************************");
		log.info("Upload Picture Command successfully started.....");
		log.info("************************************************");
	}
	
	public CameraCommandResponse invoke(CameraCommandContext context)
	{
		try
		{
			CameraCommandResponse response = new CameraCommandResponse();
			
			log.info("***********************************************");
			log.info("UploadPictureCommand successfully invoked");
			String fullName = context.getFullName();
			String mime = context.getMimeType();
			byte[] pic = context.getPhoto();
			
			Device device = ExecutionContext.getInstance().getDevice();
			
			log.info("Device: "+device.getIdentifier());
			log.info("Identity: "+device.getIdentity().getPrincipal());
			log.info("FullName: "+fullName);
			log.info("Mime: "+mime);
			log.info("Pic: "+new String(pic));
			
			for(int i=0; i<5; i++)
			{
				response.setAttribute("name:"+i, "value:"+i);
			}
			log.info("***********************************************");
			
			return response;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
