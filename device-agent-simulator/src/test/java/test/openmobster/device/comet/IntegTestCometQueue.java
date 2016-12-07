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
public class IntegTestCometQueue extends AbstractCometTest
{
	public void test() throws Exception
	{	
		//Just activate the device, but no active connection
		this.device_12345.activateDevice();
		this.registerDeviceType("IMEI:12345", "android");
		
		//Wait for some messages in the comet queue, and then connect
		Thread.currentThread().sleep(10000);
		
		//Connect to the server, and messages in the queue should be delivered
		this.device_12345.startCometDaemon();
		
		Thread.currentThread().sleep(20000);
		
		this.device_12345.stop();
		this.device_12345.start();
		this.device_12345.startCometDaemon();
		
		synchronized(this)
		{
			wait();
		}
	}	
}
