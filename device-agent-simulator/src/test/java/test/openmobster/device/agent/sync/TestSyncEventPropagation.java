/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync;

import java.text.MessageFormat;
import java.util.List;
import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.test.framework.MobileBeanRunner;

/**
 * @author openmobster@gmail.com
 */
public class TestSyncEventPropagation extends AbstractSync 
{
	private static Logger log = Logger.getLogger(TestSyncEventPropagation.class);
	
	MobileBeanRunner imei0;
	MobileBeanRunner imei1;
	MobileBeanRunner imei2;
	
	public void setUp() throws Exception
	{
		super.setUp();
		
		//Setup the device 'IMEI:0'
		this.imei0 = (MobileBeanRunner)ServiceManager.locate("IMEI:0");
		this.imei0.setApp("testApp");
		this.imei0.activateDevice();
		
		//Setup the device 'IMEI:1'
		this.imei1 = (MobileBeanRunner)ServiceManager.locate("IMEI:1");
		this.imei1.setApp("testApp");
		this.imei1.activateDevice();
		
		//Setup the device 'IMEI:2'
		this.imei2 = (MobileBeanRunner)ServiceManager.locate("IMEI:2");
		this.imei2.setApp("testApp");
		this.imei2.activateDevice();
	}
	
	public void testSyncEventAdd() throws Exception
	{
		log.info("Starting testSyncEventAdd.............");
		
		this.imei0.bootService();
		this.print(this.imei0.readAll());
		
		this.imei1.swapConfiguration();
		this.imei1.bootService();
		this.print(this.imei1.readAll());
		
		this.imei2.swapConfiguration();
		this.imei2.bootService();
		this.print(this.imei2.readAll());
		
		//Add a bean
		this.imei0.swapConfiguration();
		MobileObject mo = new MobileObject();
		mo.setRecordId("unique-10");
		mo.setStorageId(this.service);
		mo.setValue("from","from@gmail.com");
		mo.setValue("to","to@gmail.com");
		mo.setValue("subject", MessageFormat.format(this.subject,new Object[]{mo.getRecordId()}));
		mo.setValue("message", MessageFormat.format(this.message,new Object[]{mo.getRecordId()}));
		this.imei0.create(mo);
		
		this.imei0.syncService();
		
		//Assert imei1's changelog
		List imei1ChangeLog = this.serverSyncEngine.getChangeLog("IMEI:1", "testServerBean", "testApp", 
		ServerSyncEngine.OPERATION_ADD);
		this.assertTrue(imei1ChangeLog != null && !imei1ChangeLog.isEmpty());
		
		//Assert imei2's changelog
		List imei2ChangeLog = this.serverSyncEngine.getChangeLog("IMEI:2", "testServerBean", "testApp", 
		ServerSyncEngine.OPERATION_ADD);
		this.assertTrue(imei2ChangeLog == null || imei2ChangeLog.isEmpty());
		
		//Assert imei0's changelog
		List imei0ChangeLog = this.serverSyncEngine.getChangeLog("IMEI:0", "testServerBean", "testApp", 
		ServerSyncEngine.OPERATION_ADD);
		this.assertTrue(imei0ChangeLog == null || imei0ChangeLog.isEmpty());
	}
	
	public void testSyncEventUpdate() throws Exception
	{
		log.info("Starting testSyncEventUpdate.............");
		
		this.imei0.bootService();
		this.print(this.imei0.readAll());
		
		this.imei1.swapConfiguration();
		this.imei1.bootService();
		this.print(this.imei1.readAll());
		
		this.imei2.swapConfiguration();
		this.imei2.bootService();
		this.print(this.imei2.readAll());
		
		//Updating a bean
		this.imei0.swapConfiguration();
		MobileObject unique1 = this.imei0.read("unique-1");
		unique1.setValue("from", "updated by device 1");
		this.imei0.update(unique1);
		
		this.imei0.syncService();
		
		//Assert imei1's changelog
		List imei1ChangeLog = this.serverSyncEngine.getChangeLog("IMEI:1", "testServerBean", "testApp", 
		ServerSyncEngine.OPERATION_UPDATE);
		this.assertTrue(imei1ChangeLog != null && !imei1ChangeLog.isEmpty());
		
		//Assert imei2's changelog
		List imei2ChangeLog = this.serverSyncEngine.getChangeLog("IMEI:2", "testServerBean", "testApp", 
		ServerSyncEngine.OPERATION_UPDATE);
		this.assertTrue(imei2ChangeLog != null && !imei2ChangeLog.isEmpty());
		
		//Assert imei0's changelog
		List imei0ChangeLog = this.serverSyncEngine.getChangeLog("IMEI:0", "testServerBean", "testApp", 
		ServerSyncEngine.OPERATION_UPDATE);
		this.assertTrue(imei0ChangeLog == null || imei0ChangeLog.isEmpty());
	}
	
	public void testSyncEventDelete() throws Exception
	{
		log.info("Starting testSyncEventDelete.............");
		
		this.imei0.bootService();
		this.print(this.imei0.readAll());
		
		this.imei1.swapConfiguration();
		this.imei1.bootService();
		this.print(this.imei1.readAll());
		
		this.imei2.swapConfiguration();
		this.imei2.bootService();
		this.print(this.imei2.readAll());
		
		//Updating a bean
		this.imei0.swapConfiguration();
		MobileObject unique1 = this.imei0.read("unique-1");
		this.imei0.delete(unique1);
		
		this.imei0.syncService();
		
		//Assert imei1's changelog
		List imei1ChangeLog = this.serverSyncEngine.getChangeLog("IMEI:1", "testServerBean", "testApp", 
		ServerSyncEngine.OPERATION_DELETE);
		this.assertTrue(imei1ChangeLog != null && !imei1ChangeLog.isEmpty());
		
		//Assert imei2's changelog
		List imei2ChangeLog = this.serverSyncEngine.getChangeLog("IMEI:2", "testServerBean", "testApp", 
		ServerSyncEngine.OPERATION_DELETE);
		this.assertTrue(imei2ChangeLog != null && !imei2ChangeLog.isEmpty());
		
		//Assert imei0's changelog
		List imei0ChangeLog = this.serverSyncEngine.getChangeLog("IMEI:0", "testServerBean", "testApp", 
		ServerSyncEngine.OPERATION_DELETE);
		this.assertTrue(imei0ChangeLog == null || imei0ChangeLog.isEmpty());
	}
	
	public void testSyncEventNotBooted() throws Exception
	{
		log.info("Starting testSyncEventNotBooted.............");
		
		this.imei0.bootService();
		this.print(this.imei0.readAll());
		
		//Updating a bean
		this.imei0.swapConfiguration();
		MobileObject unique1 = this.imei0.read("unique-1");
		unique1.setValue("from", "updated by device 1");
		this.imei0.update(unique1);
		
		this.imei0.syncService();
		
		//Assert imei1's changelog
		List imei1ChangeLog = this.serverSyncEngine.getChangeLog("IMEI:1", "testServerBean", "testApp", 
		ServerSyncEngine.OPERATION_UPDATE);
		this.assertTrue(imei1ChangeLog == null || imei1ChangeLog.isEmpty());
		
		//Assert imei2's changelog
		List imei2ChangeLog = this.serverSyncEngine.getChangeLog("IMEI:2", "testServerBean", "testApp", 
		ServerSyncEngine.OPERATION_UPDATE);
		this.assertTrue(imei2ChangeLog == null || imei2ChangeLog.isEmpty());
		
		//Assert imei0's changelog
		List imei0ChangeLog = this.serverSyncEngine.getChangeLog("IMEI:0", "testServerBean", "testApp", 
		ServerSyncEngine.OPERATION_UPDATE);
		this.assertTrue(imei0ChangeLog == null || imei0ChangeLog.isEmpty());
	}
	
	public void print(List<MobileObject> mobileObjects)
	{
		log.info("***********************************");
		for(MobileObject local:mobileObjects)
		{
			log.info("OID: "+local.getRecordId());
			log.info("From: "+local.getValue("from"));
		}
		log.info("***********************************");
	}
}
