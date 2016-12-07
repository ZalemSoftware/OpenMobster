/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class TestBootSync extends AbstractSyncTest 
{		
	/**
	 * 
	 */
	public void runTest()
	{
		try
		{
			this.setUp("add");
			SyncService.getInstance().performTwoWaySync(service, service, false);					
			this.assertRecordPresence("unique-1", "/TestTwoWaySync/add");
			this.assertRecordPresence("unique-2", "/TestTwoWaySync/add");
			this.assertRecordPresence("unique-3", "/TestTwoWaySync/add");
			this.assertRecordPresence("unique-4", "/TestTwoWaySync/add");			
			
			SyncService.getInstance().performBootSync(service, service, false);
			
			//Assert the device state
			this.assertRecordPresence("unique-1", "/TestBootSync/MustBeFound/unique-1");
			this.assertRecordPresence("unique-2", "/TestBootSync/MustBeFoundAsProxy/unique-2");
			this.assertRecordPresence("unique-3", "/TestBootSync/MustBeFoundAsProxy/unique-3");
			this.assertRecordPresence("unique-4", "/TestBootSync/MustBeFoundAsProxy/unique-4");
			this.assertFalse(this.getRecord("unique-1").isProxy(), "/TestBootSync/MustNotBeAProxy/unique-1");
			this.assertTrue(this.getRecord("unique-2").isProxy(), "/TestBootSync/MustBeAProxy/unique-2");
			this.assertTrue(this.getRecord("unique-3").isProxy(), "/TestBootSync/MustBeAProxy/unique-3");
			this.assertTrue(this.getRecord("unique-4").isProxy(), "/TestBootSync/MustBeAProxy/unique-4");
			
			this.tearDown();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
}
