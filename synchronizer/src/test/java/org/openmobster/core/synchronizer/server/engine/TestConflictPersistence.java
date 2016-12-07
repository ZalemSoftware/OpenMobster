/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.synchronizer.server.engine;

import org.openmobster.core.common.ServiceManager;
import junit.framework.TestCase;

/**
 * 
 * @author openmobster@gmail.com
 */
public class TestConflictPersistence extends TestCase
{
	private ConflictEngine conflictEngine;
	/**
	 * 
	 */
	protected void setUp() throws Exception
	{
		ServiceManager.bootstrap();
		this.conflictEngine = (ConflictEngine)ServiceManager.locate("ConflictEngine");
	}

	/**
	 * 
	 */
	protected void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	
	public void testSaveLock() throws Exception
	{
		ConflictEntry entry = new ConflictEntry();
		
		//Create Lock
		entry.setDeviceId("deviceId");
		entry.setOid("oid");
		entry.setState("blahblah".getBytes());
		entry.setApp("testApp");
		entry.setChannel("testChannel");
		this.conflictEngine.saveLock(entry);
		
		//ReadLock
		ConflictEntry stored = this.conflictEngine.readLock("deviceId", "oid", "testApp", "testChannel");
		assertEquals(stored.getStateAsString(), "blahblah");
		assertTrue(stored.getId()>0);
		stored.setState("blahblah2".getBytes());
		this.conflictEngine.saveLock(stored);
		
		stored = this.conflictEngine.readLock("deviceId", "oid", "testApp", "testChannel");
		assertEquals(stored.getStateAsString(), "blahblah2");
	}
}
