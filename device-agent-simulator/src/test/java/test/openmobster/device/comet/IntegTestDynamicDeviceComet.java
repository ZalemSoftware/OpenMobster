/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.comet;

/**
 * Usecase being tested:
 * 
 * One device is activated and starts listening to its channels
 * 
 * Another device is activated a bit later
 * 
 * Expected:
 * First Device should receive 2 notifications 
 * 
 * Second Device should receive notification from 1 channel only
 * 
 * 
 * @author openmobster@gmail.com
 */
public class IntegTestDynamicDeviceComet extends AbstractCometTest
{
	public void test() throws Exception
	{					
		//Add another device
		this.device_12345.activateDevice();
		this.registerDeviceType("IMEI:12345", "android");
		this.device_12345.startCometDaemon();
		
		Thread.currentThread().sleep(30000);
		
		//Add another device
		this.device_67890.activateDevice();
		this.registerDeviceType("IMEI:67890", "iphone");
		this.device_67890.startCometDaemon();
								
		synchronized(this)
		{
			wait();
		}
	}	
}
