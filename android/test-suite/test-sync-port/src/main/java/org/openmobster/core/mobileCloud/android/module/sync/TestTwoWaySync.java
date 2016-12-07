/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;

import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObject;


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class TestTwoWaySync extends AbstractSyncTest 
{		
	/**
	 * 
	 */
	public void runTest()
	{
		try
		{
			//Add test case
			this.setUp("add");
			SyncService.getInstance().performTwoWaySync(service, service, false);					
			this.assertRecordPresence("unique-1", "/TestTwoWaySync/add");
			this.assertRecordPresence("unique-2", "/TestTwoWaySync/add");
			this.assertRecordPresence("unique-3", "/TestTwoWaySync/add");
			this.assertRecordPresence("unique-4", "/TestTwoWaySync/add");
			this.tearDown();
			
			//Replace test case
			this.setUp("replace");
			SyncService.getInstance().performTwoWaySync(service, service, false);
			MobileObject afterUnique1 = this.getRecord("unique-1");
			MobileObject afterUnique2 = this.getRecord("unique-2");			
			this.assertRecordPresence("unique-1", "/TestTwoWaySync/replace");
			this.assertRecordPresence("unique-2", "/TestTwoWaySync/replace");
			this.assertEquals(afterUnique1.getValue("message"), "<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Updated/Server</tag>", 
			"/TestTwoWaySync/replace/updated/unique-1");
			this.assertEquals(afterUnique2.getValue("message"), "<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Updated/Client</tag>", 
			"/TestTwoWaySync/replace/updated/unique-2");
			this.tearDown();
						
			//Run the Delete test case.
			this.setUp("delete");
			SyncService.getInstance().performTwoWaySync(service, service, false);				
			this.assertRecordAbsence("unique-1", "/TestTwoWaySync/delete");
			this.assertRecordAbsence("unique-2", "/TestTwoWaySync/delete");
			this.tearDown();
			
			//Conflict test case
			this.setUp("conflict");
			SyncService.getInstance().performBootSync(service, service, false);
			this.setUp("conflict");
			SyncService.getInstance().performTwoWaySync(service, service, false);
			afterUnique1 = this.getRecord("unique-1");
			afterUnique2 = this.getRecord("unique-2");			
			this.assertRecordPresence("unique-1", "/TestTwoWaySync/conflict");
			this.assertRecordPresence("unique-2", "/TestTwoWaySync/conflict");
			this.assertEquals(afterUnique1.getValue("message"), "<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Updated/Server</tag>", 
			"/TestTwoWaySync/conflict/updated/unique-1");
			this.assertEquals(afterUnique2.getValue("message"), "<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Message</tag>", 
			"/TestTwoWaySync/conflict/updated/unique-2");
			this.tearDown();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
}
