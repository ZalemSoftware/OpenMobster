/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import java.net.URL;
import java.text.MessageFormat;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObjectDatabase;
import org.openmobster.device.agent.sync.SyncAdapter;
import org.openmobster.device.agent.sync.SyncAdapterRequest;
import org.openmobster.device.agent.sync.SyncAdapterResponse;
import org.openmobster.device.agent.sync.SyncXMLTags;
import org.openmobster.device.agent.sync.engine.SyncEngine;

import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceAttribute;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.identity.Identity;
import org.openmobster.core.security.identity.IdentityController;
import org.openmobster.core.security.Provisioner;

import org.openmobster.core.synchronizer.server.SyncServer;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;
import org.openmobster.core.synchronizer.server.engine.ChangeLogEntry;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.Utilities;

import org.openmobster.device.agent.configuration.Configuration;
import test.openmobster.device.agent.sync.server.ServerRecord;
import test.openmobster.device.agent.sync.server.ServerRecordController;

/**
 * @author openmobster@gmail.com
 */
public abstract class AbstractSync extends TestCase 
{
	private static Logger log = Logger.getLogger(AbstractSync.class);
	
	protected String service = "testServerBean";
	protected String app = "testApp";
	
	//device stack
	protected String deviceId = "IMEI:4930051";
	protected SyncEngine  deviceSyncEngine;
	protected MobileObjectDatabase deviceDatabase = null;
	
	//server stack
	protected String serverId = "http://www.openmobster.org/sync-server";
	protected ServerSyncEngine serverSyncEngine;
	protected ServerRecordController serverController = null;
	
	//security stack
	protected IdentityController identityController;
	protected DeviceController deviceController;
	protected Provisioner provisioner;
		
	//some data
	protected byte[] attachment = "blahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblah".getBytes();
	protected String message = "<tag apos=''apos'' quote=\"quote\" ampersand=''&''>{0}/Message</tag>";
	protected String subject = "This is the subject<html><body>{0}</body></html>";
	protected String newServerRecordId = "unique-3";
	protected String newDeviceRecordId = "unique-4";
	
	protected void setUp() throws Exception 
	{
		ServiceManager.bootstrap();
		
		this.deviceSyncEngine = (SyncEngine)ServiceManager.
		locate("simulator://SyncEngine");
		
		this.deviceDatabase = (MobileObjectDatabase)
		ServiceManager.locate("mobileObject://MobileObjectDatabase");
		
		this.serverSyncEngine = (ServerSyncEngine)ServiceManager.
		locate("synchronizer://ServerSyncEngine");
				
		this.serverController = ServerRecordController.getInstance();
		
		this.identityController = (IdentityController)
		ServiceManager.locate("security://IdentityController");
		
		this.deviceController = (DeviceController)
		ServiceManager.locate("security://DeviceController");
		
		this.provisioner = (Provisioner)ServiceManager.locate("security://Provisioner");
				
		//SetUp State of the system		
		this.setUpSecurity();
		this.setUpServerData();
		this.setUpDeviceData();
	}

	
	protected void tearDown() throws Exception 
	{
		ServiceManager.shutdown();
	}
	
	protected void setUpSecurity() throws Exception
	{
		//SetUp Device side security
		Configuration.getInstance().setAuthenticationHash("blahblah");
		Configuration.getInstance().setAuthenticationNonce(null);
		
		if(!this.deviceId.equals("IMEI:4930052"))
		{
			//Create the Identity
			if(this.identityController.read("blah@gmail.com") == null)
			{
				this.identityController.create(new Identity("blah@gmail.com",""));
			}
			
			//Create the Device associated with this Identity
			if(this.deviceController.read(this.deviceId) == null)
			{
				Device device = new Device(this.deviceId, identityController.read("blah@gmail.com"));
				device.addAttribute(new DeviceAttribute("nonce", "blahblah"));
				this.deviceController.create(device);
			}
		}
		else
		{
			//Create the Identity
			if(this.identityController.read("blah2@gmail.com") == null)
			{
				this.identityController.create(new Identity("blah2@gmail.com",""));
			}
			
			//Create the Device associated with this Identity
			if(this.deviceController.read(this.deviceId) == null)
			{
				Device device = new Device(this.deviceId, identityController.read("blah2@gmail.com"));
				device.addAttribute(new DeviceAttribute("nonce", "blahblah"));
				this.deviceController.create(device);
			}						
		}
	}
	
	protected void resetServerNonce()
	{		
		Device device = deviceController.read(this.deviceId);
		device.updateAttribute(new DeviceAttribute("nonce", "blahblah"));
		this.deviceController.update(device);
		
		//SetUp Device side security
		Configuration.getInstance().setAuthenticationHash("blahblah");
		Configuration.getInstance().setAuthenticationNonce(null);
	}
	
	protected void bootAnotherDevice(String deviceId) throws Exception
	{
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
        for (Enumeration e = cl.getResources("META-INF/openmobster-config.xml"); e.hasMoreElements(); )
        {
          URL url = (URL) e.nextElement();
          log.info(url);
          if(url.toString().contains("device-agent-simulator/target/classes/META-INF/openmobster-config.xml") ||
        	 url.toString().contains("device-agent-frameworks/target/classes/META-INF/openmobster-config.xml") ||
        	 url.toString().contains("device-agent-frameworks-2.0-snapshot.jar!/META-INF/openmobster-config.xml"))
          {
        	  log.info("Deployed Artifact URL="+url);
        	  ServiceManager.redeploy(url);
          }
        }
        
        this.deviceSyncEngine = (SyncEngine)ServiceManager.
		locate("simulator://SyncEngine");
        
        this.deviceDatabase = (MobileObjectDatabase)
		ServiceManager.locate("mobileObject://MobileObjectDatabase");
        
        this.deviceId = deviceId;
        this.setUpSecurity();
	}
	//--------------------------------------------------------------------------------------------------------
	protected void setUpServerData() throws Exception
	{
		ServerRecord serverData = new ServerRecord();
		serverData.setObjectId("unique-1");
		serverData.setFrom("from@gmail.com");
		serverData.setTo("to@gmail.com");
		serverData.setSubject(MessageFormat.format(this.subject,new Object[]{serverData.getObjectId()}));
		serverData.setMessage(MessageFormat.format(this.message,new Object[]{serverData.getObjectId()}));
		serverData.setAttachment(this.attachment);
		this.serverController.create(serverData);

		serverData = new ServerRecord();
		serverData.setObjectId("unique-2");
		serverData.setFrom("from@gmail.com");
		serverData.setTo("to@gmail.com");
		serverData.setSubject(MessageFormat.format(this.subject,new Object[]{serverData.getObjectId()}));
		serverData.setMessage(MessageFormat.format(this.message,new Object[]{serverData.getObjectId()}));
		serverData.setAttachment(this.attachment);
		this.serverController.create(serverData);
	}
	
	protected ServerRecord getServerRecord(String uid) throws Exception
	{
		return this.serverController.readServerRecord(uid);
	}
	
	protected ServerRecord createNewServerRecord()
	{			
		//add a new record to the server
		ServerRecord serverRecord = new ServerRecord();
		serverRecord.setObjectId(this.newServerRecordId);
		serverRecord.setFrom("from@gmail.com");
		serverRecord.setTo("to@gmail.com");
		serverRecord.setSubject(MessageFormat.format(this.subject,new Object[]{serverRecord.getObjectId()}));
		serverRecord.setMessage(MessageFormat.format(this.message,new Object[]{serverRecord.getObjectId()}));
		serverRecord.setAttachment(this.attachment);
		this.serverController.create(serverRecord);

		// Update the Server ChangeLog
		List serverChangeLog = new ArrayList();
		ChangeLogEntry serverEntry = new ChangeLogEntry();
		serverEntry.setNodeId(this.service);
		serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
		serverEntry.setRecordId(serverRecord.getObjectId());
		serverChangeLog.add(serverEntry);
		this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
		
		return serverRecord;
	}
	
	protected ServerRecord createNewServerRecord(String newRecordId)
	{			
		//add a new record to the server
		ServerRecord serverRecord = new ServerRecord();
		serverRecord.setObjectId(newRecordId);
		serverRecord.setFrom("from@gmail.com");
		serverRecord.setTo("to@gmail.com");
		serverRecord.setSubject(MessageFormat.format(this.subject,new Object[]{serverRecord.getObjectId()}));
		serverRecord.setMessage(MessageFormat.format(this.message,new Object[]{serverRecord.getObjectId()}));
		serverRecord.setAttachment(this.attachment);
		this.serverController.create(serverRecord);

		// Update the Server ChangeLog
		List serverChangeLog = new ArrayList();
		ChangeLogEntry serverEntry = new ChangeLogEntry();
		serverEntry.setNodeId(this.service);
		serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
		serverEntry.setRecordId(serverRecord.getObjectId());
		serverChangeLog.add(serverEntry);
		this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
		
		return serverRecord;
	}
	
	protected ServerRecord updateServerRecord(String serverRecordId) throws Exception
	{
		//update a record on the server
		ServerRecord serverRecord = this.getServerRecord(serverRecordId);
		String recordId = serverRecord.getObjectId();
		serverRecord.setMessage("Testing record update...(Server)");
		this.serverController.save(serverRecord);
		
		//Update the client ChangeLog
		List serverChangeLog = new ArrayList();
		ChangeLogEntry serverEntry = new ChangeLogEntry();
		serverEntry.setNodeId(this.service);
		serverEntry.setOperation(ServerSyncEngine.OPERATION_UPDATE);
		serverEntry.setRecordId(recordId);
		serverChangeLog.add(serverEntry);
		this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
		
		return serverRecord;
	}
	
	protected String deleteServerRecord(String serverRecordId) throws Exception
	{
		ServerRecord serverRecord = (ServerRecord) this.getServerRecord(serverRecordId);
		String recordId = serverRecord.getObjectId();
		this.serverController.delete(serverRecord);
		
		List serverChangeLog = new ArrayList();
		ChangeLogEntry serverEntry = new ChangeLogEntry();
		serverEntry.setNodeId(this.service);
		serverEntry.setOperation(ServerSyncEngine.OPERATION_DELETE);
		serverEntry.setRecordId(recordId);
		serverChangeLog.add(serverEntry);
		this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
		
		return recordId;
	}
	//--------------------------------------------------------------------------------------------------------
	protected void setUpDeviceData() throws Exception
	{
		MobileObject mo = new MobileObject();
		mo.setRecordId("unique-1");
		mo.setStorageId(this.service);
		mo.setValue("from","from@gmail.com");
		mo.setValue("to","to@gmail.com");
		mo.setValue("subject", MessageFormat.format(this.subject,new Object[]{mo.getRecordId()}));
		mo.setValue("message", MessageFormat.format(this.message,new Object[]{mo.getRecordId()}));
		this.deviceDatabase.create(mo);

		mo = new MobileObject();
		mo.setRecordId("unique-2");
		mo.setStorageId(this.service);
		mo.setValue("from", "from@gmail.com");
		mo.setValue("to", "to@gmail.com");
		mo.setValue("subject", MessageFormat.format(this.subject,new Object[]{mo.getRecordId()}));
		mo.setValue("message", MessageFormat.format(this.message,new Object[]{mo.getRecordId()}));
		this.deviceDatabase.create(mo);
	}
	
	protected List readDeviceData() throws Exception
	{
		List allData = this.deviceDatabase.readByStorage(this.service);
		return allData;
	}
	
	protected MobileObject getDeviceRecord(String uid) throws Exception
	{
		MobileObject record = null;		
		
		List records = this.readDeviceData();
		for(int i=0;i<records.size();i++)
		{
			MobileObject cour = (MobileObject)records.get(i);
			if(cour.getRecordId().equals(uid))
			{
				record = cour;
				break;
			}
		}
		
		return record;
	}	
	protected MobileObject createNewDeviceRecord() throws Exception
	{
		//add a new record to the device and make it out-of-sync with the
		// server
		MobileObject mo = new MobileObject();
		mo.setRecordId(this.newDeviceRecordId);
		mo.setStorageId(this.service);
		mo.setValue("from", "from@gmail.com");
		mo.setValue("to", "to@gmail.com");
		mo.setValue("subject", MessageFormat.format(this.subject,new Object[]{mo.getRecordId()}));
		mo.setValue("message", MessageFormat.format(this.message,new Object[]{mo.getRecordId()}));
		this.deviceDatabase.create(mo);
		
		//Update the Client ChangeLog
		Vector changelog = new Vector();
		org.openmobster.device.agent.sync.engine.ChangeLogEntry entry = 
		new org.openmobster.device.agent.sync.engine.ChangeLogEntry();
		entry.setNodeId(this.service);
		entry.setOperation(SyncEngine.OPERATION_ADD);
		entry.setRecordId(mo.getRecordId());
		changelog.add(entry);
		this.deviceSyncEngine.addChangeLogEntries(changelog);
		
		return mo;
	}
	
	protected void createNewDeviceRecords() throws Exception
	{
		//add a new record to the device and make it out-of-sync with the
		// server
		for(int i=0; i<5; i++)
		{
			MobileObject mo = new MobileObject();
			mo.setRecordId(Utilities.generateUID());
			mo.setStorageId(this.service);
			mo.setValue("from", "from@gmail.com");
			mo.setValue("to", "to@gmail.com");
			mo.setValue("subject", MessageFormat.format(this.subject,new Object[]{mo.getRecordId()}));
			mo.setValue("message", MessageFormat.format(this.message,new Object[]{mo.getRecordId()}));
			this.deviceDatabase.create(mo);
			
			//Update the Client ChangeLog
			Vector changelog = new Vector();
			org.openmobster.device.agent.sync.engine.ChangeLogEntry entry = 
			new org.openmobster.device.agent.sync.engine.ChangeLogEntry();
			entry.setNodeId(this.service);
			entry.setOperation(SyncEngine.OPERATION_ADD);
			entry.setRecordId(mo.getRecordId());
			changelog.add(entry);
			this.deviceSyncEngine.addChangeLogEntries(changelog);
		}
	}
	
	protected void createNewServerRecords()
	{	
		for(int i=0; i<10; i++)
		{
			//add a new record to the server
			ServerRecord serverRecord = new ServerRecord();
			serverRecord.setObjectId(Utilities.generateUID());
			serverRecord.setFrom("from@gmail.com");
			serverRecord.setTo("to@gmail.com");
			serverRecord.setSubject(MessageFormat.format(this.subject,new Object[]{serverRecord.getObjectId()}));
			serverRecord.setMessage(MessageFormat.format(this.message,new Object[]{serverRecord.getObjectId()}));
			serverRecord.setAttachment(this.attachment);
			this.serverController.create(serverRecord);
	
			// Update the Server ChangeLog
			List serverChangeLog = new ArrayList();
			ChangeLogEntry serverEntry = new ChangeLogEntry();
			serverEntry.setNodeId(this.service);
			serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
			serverEntry.setRecordId(serverRecord.getObjectId());
			serverChangeLog.add(serverEntry);
			this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
		}
	}
	
	protected MobileObject createDeviceRecord(String recordId) throws Exception
	{
		//add a new record to the device and make it out-of-sync with the
		// server
		MobileObject deviceRecord = new MobileObject();
		deviceRecord.setRecordId(recordId);
		deviceRecord.setStorageId(this.service);
		deviceRecord.setValue("from", "from@gmail.com");
		deviceRecord.setValue("to", "to@gmail.com");
		deviceRecord.setValue("subject", MessageFormat.format(this.subject,new Object[]{deviceRecord.getRecordId()}));
		deviceRecord.setValue("message", MessageFormat.format(this.message,new Object[]{deviceRecord.getRecordId()}));
		this.deviceDatabase.create(deviceRecord);
				
		Vector changelog = new Vector();
		org.openmobster.device.agent.sync.engine.ChangeLogEntry entry = 
		new org.openmobster.device.agent.sync.engine.ChangeLogEntry();
		entry.setNodeId(this.service);
		entry.setOperation(SyncEngine.OPERATION_ADD);
		entry.setRecordId(deviceRecord.getRecordId());
		changelog.add(entry);
		this.deviceSyncEngine.addChangeLogEntries(changelog);
		
		return deviceRecord;
	}
		
	protected MobileObject updateDeviceRecord(String recordId) throws Exception
	{
		//update a record on the device
		MobileObject deviceRecord = (MobileObject)this.getDeviceRecord(recordId);
		deviceRecord.setValue("message","Testing record update...(Device)");
		this.deviceDatabase.update(deviceRecord);
				
		Vector changelog = new Vector();
		org.openmobster.device.agent.sync.engine.ChangeLogEntry entry = 
		new org.openmobster.device.agent.sync.engine.ChangeLogEntry();
		entry.setNodeId(this.service);
		entry.setOperation(SyncEngine.OPERATION_UPDATE);
		entry.setRecordId(deviceRecord.getRecordId());
		changelog.add(entry);
		this.deviceSyncEngine.addChangeLogEntries(changelog);
		
		return deviceRecord;
	}
	
	protected MobileObject updateDeviceRecord(String recordId,String message) throws Exception
	{
		//update a record on the device
		MobileObject deviceRecord = (MobileObject)this.getDeviceRecord(recordId);
		deviceRecord.setValue("message",message);
		this.deviceDatabase.update(deviceRecord);
				
		Vector changelog = new Vector();
		org.openmobster.device.agent.sync.engine.ChangeLogEntry entry = 
		new org.openmobster.device.agent.sync.engine.ChangeLogEntry();
		entry.setNodeId(this.service);
		entry.setOperation(SyncEngine.OPERATION_UPDATE);
		entry.setRecordId(deviceRecord.getRecordId());
		changelog.add(entry);
		this.deviceSyncEngine.addChangeLogEntries(changelog);
		
		return deviceRecord;
	}
	
	protected String deleteDeviceRecord(String recordId) throws Exception
	{
		//delete a record on the device
		MobileObject deviceRecord = (MobileObject) this.getDeviceRecord(recordId);
		this.deviceDatabase.delete(deviceRecord);
				
		Vector changelog = new Vector();
		org.openmobster.device.agent.sync.engine.ChangeLogEntry entry = 
		new org.openmobster.device.agent.sync.engine.ChangeLogEntry();
		entry.setNodeId(this.service);
		entry.setOperation(SyncEngine.OPERATION_DELETE);
		entry.setRecordId(deviceRecord.getRecordId());
		changelog.add(entry);
		this.deviceSyncEngine.addChangeLogEntries(changelog);
		
		return recordId;
	}
	//-------------------------------------------------------------------------------------------------------	
	protected void assertServerPresence(String objectId)
	{
		ServerRecord record = this.serverController.readServerRecord(objectId);		
		this.assertNotNull(record);
	}
		
	protected void assertServerAbsence(String objectId)
	{
		ServerRecord record = this.serverController.readServerRecord(objectId);		
		this.assertNull(record);
	}
	
	protected void assertServerMessage(String objectId,String message)
	{
		String recordMessage = null;
		ServerRecord record = this.serverController.readServerRecord(objectId);
		if(record != null)
		{
			recordMessage = record.getMessage();
		}		
		assertEquals("Message does not match", recordMessage, message);
	}
	
	protected void assertServerChangeLogPresence(String deviceId, String objectId)
	{
		List changelog = this.serverSyncEngine.getChangeLog(deviceId, this.service, 
		this.app, ServerSyncEngine.OPERATION_ADD);
		
		boolean found = false;
		if(changelog != null)
		{
			for(int i=0,size=changelog.size(); i<size; i++)
			{
				ChangeLogEntry cour = (ChangeLogEntry)changelog.get(i);
				if(cour.getRecordId().equals(objectId))
				{
					found = true;
					break;
				}
			}
		}
		assertTrue("ChangeLogEntry should be Present!!", found);
	}
	
	protected void assertServerChangeLogAbsence(String deviceId, String objectId)
	{
		List changelog = this.serverSyncEngine.getChangeLog(deviceId, this.service, 
		this.app,ServerSyncEngine.OPERATION_ADD);
		
		boolean found = false;
		if(changelog != null)
		{
			for(int i=0,size=changelog.size(); i<size; i++)
			{
				ChangeLogEntry cour = (ChangeLogEntry)changelog.get(i);
				if(cour.getRecordId().equals(objectId))
				{
					found = true;
					break;
				}
			}
		}
		assertFalse("ChangeLogEntry should *not* be Present!!", found);
	}
	
	protected void assertDevicePresence(String recordId)
	{
		MobileObject mobileObject = this.deviceDatabase.read(this.service, recordId);
		assertNotNull(mobileObject);
	}
	
	protected void assertDeviceAbsence(String recordId)
	{
		MobileObject mobileObject = this.deviceDatabase.read(this.service, recordId);
		assertNull(mobileObject);
	}	
	protected void assertDeviceMessage(String recordId,String message)
	{
		String recordMessage = null;
		MobileObject mobileObject = this.deviceDatabase.read(this.service, recordId);
		if(mobileObject != null)
		{
			recordMessage = mobileObject.getValue("message");
		}
		assertEquals(recordMessage, message);
	}
	//--------------------------------------------------------------------------------------------------------
	protected void performTwoWaySync() throws Exception
	{
		this.performSync(0, SyncAdapter.TWO_WAY);
	}
	
	protected void performLongObjectTwoWaySync(int packageSize) throws Exception
	{
		this.performSync(packageSize, SyncAdapter.TWO_WAY);
	}	
	
	protected void performSlowSync() throws Exception
	{
		this.performSync(0, SyncAdapter.SLOW_SYNC);
	}
	
	protected void performLongObjectSlowSync(int packageSize) throws Exception
	{
		this.performSync(packageSize, SyncAdapter.SLOW_SYNC);
	}
	
	protected void performOneWayServerSync() throws Exception
	{
		this.performSync(0, SyncAdapter.ONE_WAY_SERVER);
	}
	
	protected void performOneWayDeviceSync() throws Exception
	{
		this.performSync(0, SyncAdapter.ONE_WAY_CLIENT);
	}
	
	protected void performStreamSync(String oid) throws Exception
	{
		this.performSync(0, SyncAdapter.STREAM, oid);
	}
	
	protected void performBootSync() throws Exception
	{
		this.performSync(0, SyncAdapter.BOOT_SYNC);
	}
	
	private void performSync(int maxClientSize, String syncType) throws Exception
	{
		// Get the initialization payload
		SyncAdapterRequest initRequest = new SyncAdapterRequest();
		initRequest.setAttribute(SyncServer.SOURCE, this.deviceId);
		initRequest.setAttribute(SyncServer.TARGET, this.serverId);
		initRequest.setAttribute(SyncServer.MAX_CLIENT_SIZE, String.valueOf(maxClientSize));
		initRequest.setAttribute(SyncServer.CLIENT_INITIATED, "true");
		initRequest.setAttribute(SyncServer.DATA_SOURCE, service);
		initRequest.setAttribute(SyncServer.DATA_TARGET, service);
		initRequest.setAttribute(SyncServer.SYNC_TYPE, syncType);
		initRequest.setAttribute(SyncXMLTags.App, this.app);
		
		this.executeSyncProtocol(initRequest);		
	}
	
	private void performSync(int maxClientSize, String syncType, String oid) throws Exception
	{
		// Get the initialization payload
		SyncAdapterRequest initRequest = new SyncAdapterRequest();
		initRequest.setAttribute(SyncServer.SOURCE, this.deviceId);
		initRequest.setAttribute(SyncServer.TARGET, this.serverId);
		initRequest.setAttribute(SyncServer.MAX_CLIENT_SIZE, String.valueOf(maxClientSize));
		initRequest.setAttribute(SyncServer.CLIENT_INITIATED, "true");
		initRequest.setAttribute(SyncServer.DATA_SOURCE, service);
		initRequest.setAttribute(SyncServer.DATA_TARGET, service);
		initRequest.setAttribute(SyncServer.SYNC_TYPE, syncType);
		initRequest.setAttribute(SyncServer.STREAM_RECORD_ID, oid);
		initRequest.setAttribute(SyncXMLTags.App, this.app);
		
		this.executeSyncProtocol(initRequest);		
	}
	
	private void executeSyncProtocol(SyncAdapterRequest initRequest) throws Exception
	{
		//Get the Server Sync Adapter
		SyncServer serverSyncAdapter = (SyncServer)ServiceManager.locate("synchronizer://SyncServerAdapter");		

		// Get the Device Sync Adapter
		this.resetServerNonce();
		SyncAdapter deviceSyncAdapter = new SyncAdapter();
		deviceSyncAdapter.setSyncEngine(this.deviceSyncEngine);
		
		SyncAdapterResponse initResponse = deviceSyncAdapter.start(initRequest);

		log.info("-----------------------------");
		log.info("Client ="
				+ ((String)initResponse.getAttribute(SyncServer.PAYLOAD)).replaceAll("\n", ""));
		log.info("-----------------------------");

		boolean close = false;
		SyncAdapterRequest clientRequest = new SyncAdapterRequest();
		org.openmobster.core.synchronizer.model.SyncAdapterRequest serverRequest = 
		new org.openmobster.core.synchronizer.model.SyncAdapterRequest();
		serverRequest.setAttribute(SyncServer.PAYLOAD, initResponse
				.getAttribute(SyncServer.PAYLOAD));
		do
		{
			org.openmobster.core.synchronizer.model.SyncAdapterResponse serverResponse = serverSyncAdapter
					.service(serverRequest);

			// Setup request to be sent to the Client Syncher
			// based on payload received from the server
			String payload = (String) serverResponse
					.getAttribute(SyncServer.PAYLOAD);
			log.info("-----------------------------");
			log.info("Server =" + payload.replaceAll("\n", ""));
			log.info("-----------------------------");

			clientRequest = new SyncAdapterRequest();
			clientRequest.setAttribute(SyncServer.PAYLOAD, payload);
			SyncAdapterResponse clientResponse = deviceSyncAdapter
					.service(clientRequest);

			if (clientResponse.getStatus() == SyncServer.RESPONSE_CLOSE)
			{
				close = true;
			}
			else
			{
				payload = (String) clientResponse
						.getAttribute(SyncServer.PAYLOAD);
				serverRequest.setAttribute(SyncServer.PAYLOAD, payload);
				log.info("-----------------------------");
				log.info("Client =" + payload.replaceAll("\n", ""));
				log.info("-----------------------------");
			}

		} while (!close);
	}
}