/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.comet;

import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceAttribute;

/**
 * Usecase being tested:
 * 
 * A device is activated but not connected
 * The twitterChannel should have messages in queue while its offline
 * The device waits for a bit and then connects to the network
 * 
 * Expected:
 * The comet messages waiting in queue for this device must be delivered
 * 
 * 
 * @author openmobster@gmail.com
 */
public class IntegTestPushRouting extends AbstractCometTest
{
	public void test() throws Exception
	{	
		//Just activate the device, but no active connection
		this.device_12345.activateDevice();
		this.registerDeviceType("IMEI:12345","android");
		
		//Active the iphone device, but no active connection
		this.device_67890.activateDevice();
		this.registerDeviceType("IMEI:67890","iphone");
		
		synchronized(this)
		{
			wait();
		}
	}
}
