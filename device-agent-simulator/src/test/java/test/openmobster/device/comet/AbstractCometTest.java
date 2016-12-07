/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.comet;

import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceAttribute;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.device.agent.test.framework.MobileBeanRunner;

import org.apache.log4j.Logger;

/**
 * @author openmobster@gmail.com
 *
 */
public abstract class AbstractCometTest extends TestCase
{	
	public static Logger log = Logger.getLogger(AbstractCometTest.class);
	
	protected MobileBeanRunner device_12345;
	protected MobileBeanRunner device_67890;
	
	protected void setUp() throws Exception 
	{
		ServiceManager.bootstrap();
		
		this.device_12345 = (MobileBeanRunner)ServiceManager.locate("IMEI:12345");
		this.device_67890 = (MobileBeanRunner)ServiceManager.locate("IMEI:67890");
	}
	
	protected void tearDown() throws Exception 
	{						
		ServiceManager.shutdown();
	}
	
	protected void registerDeviceType(String deviceId,String deviceType)
	{
		DeviceController deviceController = DeviceController.getInstance();
		Device toBeUpdated = deviceController.read(deviceId);
		DeviceAttribute osAttribute = new DeviceAttribute("os",deviceType);
		DeviceAttribute versionAttribute = new DeviceAttribute("version","2.0");
		toBeUpdated.updateAttribute(osAttribute);
		toBeUpdated.updateAttribute(versionAttribute);
		
		deviceController.update(toBeUpdated);
	}
	
	protected void registerDeviceType(String deviceId,String deviceType, String deviceToken)
	{
		DeviceController deviceController = DeviceController.getInstance();
		Device toBeUpdated = deviceController.read(deviceId);
		
		DeviceAttribute osAttribute = new DeviceAttribute("os",deviceType);
		DeviceAttribute versionAttribute = new DeviceAttribute("version","2.0");
		DeviceAttribute deviceTokenAttr = new DeviceAttribute("device-token",deviceToken);
		toBeUpdated.updateAttribute(osAttribute);
		toBeUpdated.updateAttribute(versionAttribute);
		toBeUpdated.updateAttribute(deviceTokenAttr);
		
		deviceController.update(toBeUpdated);
	}
	
	protected Device findDevice(String deviceId)
	{
		DeviceController deviceController = DeviceController.getInstance();
		Device device = deviceController.read(deviceId);
		return device;
	}
}
