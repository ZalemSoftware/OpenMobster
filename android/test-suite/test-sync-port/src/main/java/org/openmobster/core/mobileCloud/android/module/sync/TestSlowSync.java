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
public final class TestSlowSync extends AbstractSyncTest 
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
			SyncService.getInstance().performSlowSync(service, service, false);			
			//These records from the server should show up on the device
			this.assertRecordPresence("unique-1", "/TestSlowSync/add");
			this.assertRecordPresence("unique-2", "/TestSlowSync/add");
			this.assertRecordPresence("unique-3", "/TestSlowSync/add");
			this.assertRecordPresence("unique-4", "/TestSlowSync/add");
			this.tearDown();
			
			//Replace test case
			this.setUp("replace");						
			SyncService.getInstance().performSlowSync(service, service, false);
			MobileObject replacedRecord = this.getRecord("unique-2");			
			//These records from the server should show up on the device
			this.assertRecordPresence("unique-1", "/TestSlowSync/replace");
			this.assertRecordPresence("unique-2", "/TestSlowSync/replace");
			this.assertEquals(replacedRecord.getValue("message"), "<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Updated/Client</tag>", "/TestSlowSync/replace/updated");
			this.tearDown();
			
			//Delete test case
			this.setUp("delete");
			SyncService.getInstance().performSlowSync(service, service, false);							
			//These records deleted from the device should *show up* on the device
			//Should not be deleted in a SlowSync Setup
			this.assertRecordPresence("unique-1", "/TestSlowSync/delete");
			this.assertRecordPresence("unique-2", "/TestSlowSync/delete");
			this.tearDown();
			
			//Conflict test case
			SyncService.getInstance().performSlowSync(service, service, false);
			this.setUp("conflict");						
			SyncService.getInstance().performSlowSync(service, service, false);
			MobileObject conflictedRecord = this.getRecord("unique-1");			
			//These records from the server should show up on the device
			this.assertRecordPresence("unique-1", "/TestSlowSync/conflict");
			this.assertRecordPresence("unique-2", "/TestSlowSync/conflict");
			this.assertEquals(conflictedRecord.getValue("message"), "<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Updated/Server</tag>", "/TestSlowSync/conflict/unique-1");
			this.tearDown();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
}
