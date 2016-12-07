/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync;

import java.util.List;
import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.test.framework.MobileBeanRunner;

/**
 * @author openmobster@gmail.com
 */
public class TestMultiDeviceConflictResolution extends AbstractSync 
{
	private static Logger log = Logger.getLogger(TestMultiDeviceConflictResolution.class);
	MobileBeanRunner device1;
	MobileBeanRunner device2;
	
	public void setUp() throws Exception
	{
		super.setUp();
		
		//Setup the device '12345'
		this.device1 = (MobileBeanRunner)ServiceManager.locate("12345");
		this.device1.setApp("testApp");
		this.device1.activateDevice();
		
		//Setup the device '6789'
		this.device2 = (MobileBeanRunner)ServiceManager.locate("6789");
		this.device2.setApp("testApp");
		this.device2.activateDevice();
	}
	
	public void testMultiDeviceWriteUsecase() throws Exception
	{
		log.info("Starting MultiDeviceWriteUsecase.............");
		
		this.device1.swapConfiguration();
		this.device1.bootService();
		this.print(this.device1.readAll());
		
		this.device2.swapConfiguration();
		this.device2.bootService();
		this.print(this.device2.readAll());
		
		//Update from device1
		this.device1.swapConfiguration();
		MobileObject unique1 = this.device1.read("unique-1");
		unique1.setValue("from", "updated by device 1");
		this.device1.update(unique1);
		this.device1.syncService();
		
		//Assert
		MobileObject device1Object = this.device1.read("unique-1");
		String device1From = device1Object.getValue("from");
		this.assertEquals("updated by device 1", device1From);
		
		//Update from device2
		this.device2.swapConfiguration();
		unique1 = this.device2.read("unique-1");
		unique1.setValue("from", "updated by device 2");
		this.device2.update(unique1);
		this.device2.syncService();
		
		//Assert
		MobileObject device2Object = this.device2.read("unique-1");
		String device2From = device2Object.getValue("from");
		this.assertEquals("updated by device 1", device2From);
		
		//Boot both devices
		this.device1.swapConfiguration();
		this.device1.bootService();
		this.print(this.device1.readAll());
		
		this.device2.swapConfiguration();
		this.device2.bootService();
		this.print(this.device2.readAll());
		
		//Assert
		device1Object = this.device1.read("unique-1");
		device2Object = this.device2.read("unique-1");
		
		device1From = device1Object.getValue("from");
		device2From = device2Object.getValue("from");
		
		this.assertEquals("updated by device 1", device1From);
		this.assertEquals("updated by device 1", device2From);
	}
	
	public void print(List<MobileObject> mobileObjects)
	{
		log.info("***********************************");
		for(MobileObject local:mobileObjects)
		{
			log.info("OID: "+local.getRecordId());
			log.info("From: "+local.getValue("from"));
		}
		log.info("***********************************");
	}
}
