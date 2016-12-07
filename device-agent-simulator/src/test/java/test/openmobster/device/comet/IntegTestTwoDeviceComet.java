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
 * Two different devices are activated
 * They both listen for two channels
 * 
 * Expected:
 * One device should receive notification from both channels
 * Other device should receive notification from only one channel
 * 
 * 
 * @author openmobster@gmail.com
 */
public class IntegTestTwoDeviceComet extends AbstractCometTest
{
	public void test() throws Exception
	{					
		this.device_12345.activateDevice();
		this.device_67890.activateDevice();
		this.registerDeviceType("IMEI:12345", "android");
		this.registerDeviceType("IMEI:67890", "iphone");
		
		//Start comet daemon
		this.device_12345.startCometDaemon();		
		this.device_67890.startCometDaemon();
		
		synchronized(this)
		{
			wait();
		}
	}	
}
