/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync;

import java.util.Vector;

import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.sync.engine.SyncEngine;

/**
 * @author openmobster@gmail.com
 */
public class TestEmailSync extends AbstractSync 
{
	public void testSyncDevice() throws Exception
	{
		this.service = "emailConnector";
		this.performOneWayDeviceSync();
	}
	//------------------------------------------------------------------------------------------------------
	protected void setUpDeviceData() throws Exception
	{
		MobileObject mo = new MobileObject();
		mo.setRecordId("unique-1");
		mo.setStorageId("emailConnector");
		mo.setValue("from","from@gmail.com");
		mo.setValue("to","to@gmail.com");
		mo.setValue("subject", "subject");
		mo.setValue("message", "message");
		mo.setValue("smtpServer.name", "http://smtp.blah.com");
		this.deviceDatabase.create(mo);	
		
		//Update the Client ChangeLog
		Vector changelog = new Vector();
		org.openmobster.device.agent.sync.engine.ChangeLogEntry entry = 
		new org.openmobster.device.agent.sync.engine.ChangeLogEntry();
		entry.setNodeId("emailConnector");
		entry.setOperation(SyncEngine.OPERATION_ADD);
		entry.setRecordId(mo.getRecordId());
		changelog.add(entry);
		this.deviceSyncEngine.addChangeLogEntries(changelog);
	}
}
