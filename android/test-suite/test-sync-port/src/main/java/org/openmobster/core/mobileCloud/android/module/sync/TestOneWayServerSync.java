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
public final class TestOneWayServerSync extends AbstractSyncTest 
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
			SyncService.getInstance().performOneWayServerSync(service, service, false);
			this.assertRecordPresence("unique-1", "/TestOneWayServerSync/add");
			this.assertRecordPresence("unique-2", "/TestOneWayServerSync/add");
			this.assertRecordPresence("unique-3", "/TestOneWayServerSync/add");
			this.assertRecordPresence("unique-4", "/TestOneWayServerSync/add");
			this.tearDown();		
			
			//Replace test case
			this.setUp("replace");
			SyncService.getInstance().performOneWayServerSync(service, service, false);
			MobileObject afterUnique1 = this.getRecord("unique-1");
			MobileObject afterUnique2 = this.getRecord("unique-2");
			this.assertRecordPresence("unique-1", "/TestOneWayServerSync/replace");
			this.assertRecordPresence("unique-2", "/TestOneWayServerSync/replace");
			this.assertEquals(afterUnique1.getValue("message"), "<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Updated/Server</tag>", 
			"/TestOneWayServerSync/replace/updated/unique-1");
			this.assertEquals(afterUnique2.getValue("message"), "<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Updated/Client</tag>", 
			"/TestOneWayServerSync/replace/updated/unique-2");
			this.tearDown();
			
			//Delete test case
			this.setUp("delete");
			SyncService.getInstance().performOneWayServerSync(service, service, false);
			this.assertRecordAbsence("unique-1", "/TestOneWayServerSync/delete");
			this.assertRecordAbsence("unique-2", "/TestOneWayServerSync/delete");
			this.tearDown();
			
			//Conflict test case
			this.setUp("conflict");
			SyncService.getInstance().performOneWayServerSync(service, service, false);
			afterUnique1 = this.getRecord("unique-1");
			afterUnique2 = this.getRecord("unique-2");
			this.assertRecordPresence("unique-1", "/TestOneWayServerSync/conflict");
			this.assertRecordPresence("unique-2", "/TestOneWayServerSync/conflict");
			this.assertEquals(afterUnique1.getValue("message"), "<tag apos='apos' quote=\"quote\" ampersand='&'>unique-1/Updated/Server</tag>", 
			"/TestOneWayServerSync/conflict/updated/unique-1");
			this.assertEquals(afterUnique2.getValue("message"), "<tag apos='apos' quote=\"quote\" ampersand='&'>unique-2/Message</tag>", 
			"/TestOneWayServerSync/conflict/updated/unique-2");
			this.tearDown();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
}
