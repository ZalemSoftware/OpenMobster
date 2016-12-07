/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.testsuite.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.transaction.TransactionHelper;

import org.openmobster.core.security.Provisioner;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceAttribute;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.identity.Identity;
import org.openmobster.core.security.identity.IdentityController;

import org.openmobster.core.synchronizer.server.SyncServer;
import org.openmobster.core.synchronizer.server.engine.ChangeLogEntry;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;

import org.openmobster.core.dataService.processor.Input;
import org.openmobster.core.dataService.processor.Processor;
import org.openmobster.core.dataService.processor.ProcessorException;
import org.openmobster.core.dataService.processor.SyncProcessor;

import org.openmobster.core.services.CometService;
import org.openmobster.core.services.subscription.Subscription;
import org.openmobster.core.services.subscription.SubscriptionManager;

/**
 * @author openmobster@gmail.com
 */
public class MockServer implements Processor
{
	private static Logger log = Logger.getLogger(MockServer.class);
	
	private String id;
		
	private ServerRecordController serverController = null;		
	private ServerSyncEngine serverSyncEngine = null;		
	private SyncServer originalAdapter = null;
	private SyncServer adapterWithErrors = null;
			
	private String deviceId = "IMEI:4930051";	
	private String service = "testServerBean";
	private String serviceWithMapping = "testServerBean.testMapping";
	private String app = "testApp";
	
	private byte[] attachment = "blahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblahblah".getBytes();
	
	private DeviceController deviceController;
	private IdentityController identityController;
	private Provisioner provisioner;
	
	private SubscriptionManager subscriptionManager;
	
	
	public ServerSyncEngine getServerSyncEngine()
	{
		return serverSyncEngine;
	}

	
	public void setServerSyncEngine(ServerSyncEngine serverSyncEngine)
	{
		this.serverSyncEngine = serverSyncEngine;
	}
	
	
	public SyncServer getOriginalAdapter() 
	{
		return originalAdapter;
	}

	
	public void setOriginalAdapter(SyncServer syncAdapter) 
	{
		this.originalAdapter = syncAdapter;
	}
	
			
	public SyncServer getAdapterWithErrors() 
	{
		return adapterWithErrors;
	}


	public void setAdapterWithErrors(SyncServer adapterWithErrors) 
	{
		this.adapterWithErrors = adapterWithErrors;
	}
	
	
	public DeviceController getDeviceController() 
	{
		return deviceController;
	}


	public void setDeviceController(DeviceController deviceController) 
	{
		this.deviceController = deviceController;
	}


	public IdentityController getIdentityController() 
	{
		return identityController;
	}


	public void setIdentityController(IdentityController identityController) 
	{
		this.identityController = identityController;
	}
	
	public Provisioner getProvisioner() 
	{
		return provisioner;
	}

	public void setProvisioner(Provisioner provisioner) 
	{
		this.provisioner = provisioner;
	}


	public void start()
	{
		boolean isStartedHere = false;
		try
		{
			isStartedHere = TransactionHelper.startTx();
			
			//Create the Identity
			if(this.identityController.read("blah@gmail.com") == null)
			{
				//this.identityController.create(new Identity("blah@gmail.com",""));
				this.provisioner.registerIdentity("blah@gmail.com", "blahblah");
			}
			
			if(this.deviceController.read(this.deviceId) == null)
			{
				//Create the Device associated with this Identity
				//Device device = new Device(this.deviceId, identityController.read("blah@gmail.com"));
				//device.addAttribute(new DeviceAttribute("nonce", "blahblah"));
				//this.deviceController.create(device);
				this.provisioner.registerDevice("blah@gmail.com", "blahblah", this.deviceId);
			}
						
			Device device = this.deviceController.read(this.deviceId);
			
			log.info("------------------------------------------------------");
			log.info("MockServer successfully loaded for the server side testsuite.....");
			log.info("Device="+device.getIdentifier());
			log.info("Nonce="+device.readAttribute("nonce").getValue());
			log.info("------------------------------------------------------");
			
			//Harness for testing Agent Provisioning related test cases
			this.provisioner.registerIdentity("blah2@gmail.com", "blahblah2");
								
			
			if(isStartedHere)
			{
				TransactionHelper.commitTx();
			}
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(isStartedHere)
			{
				TransactionHelper.rollbackTx();
			}
			
			throw new RuntimeException(e);
		}
	}
	
	
	public void stop()
	{		
	}
	
	
	private void setUpServerData()
	{
		//SetUp the Backend data for the "testRecordConnector" test service of the test suite
		this.serverController = ServerRecordController.getInstance();		
		
		this.serverController.deleteAll();
		
		for(int i=1; i<3; i++)
		{
			ServerRecord serverData = new ServerRecord();
			serverData.setObjectId("unique-"+i);
			serverData.setFrom(i+"@from.com");
			serverData.setTo(i+"@to.com");
			serverData.setSubject(i+"/Subject");
			serverData.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+i+"/Message"+"</tag>");	
			serverData.setAttachment(this.attachment);
			this.serverController.create(serverData);
		}
		
		//Start a Device Subscription with EmailConnector channel
		Subscription subscription = new Subscription();
		subscription.setClientId(this.deviceId);
		this.subscriptionManager = CometService.getInstance().activateSubscription(subscription);
		this.subscriptionManager.addMyChannel("emailConnector");
	}
	
	private void cleanupServerSideState()
	{
		SyncProcessor syncProcessor = (SyncProcessor)ServiceManager.locate("sync");			
		syncProcessor.setSyncAdapter(this.originalAdapter);
		this.serverSyncEngine.clearChangeLog(this.deviceId, this.service,this.app);
		this.serverSyncEngine.deleteAnchor(this.deviceId+"/"+this.service,this.app);
		this.serverSyncEngine.deleteAnchor(this.deviceId+"/"+this.serviceWithMapping,this.app);
		this.serverSyncEngine.deleteAnchor(this.deviceId+"/emailConnector",this.app);
		this.serverSyncEngine.clearRecordMap();
		
		EmailConnector.close();
		
		Device device = deviceController.read(this.deviceId);
		device.updateAttribute(new DeviceAttribute("nonce", "blahblah"));
		deviceController.update(device);
		
		//Cleanup data for the agent provisioning tests
		Identity identity = identityController.read("blah2@gmail.com");
		identity.setInactiveCredential("blahblah2");
		identityController.update(identity);				
	}
	
	/**
	 * This is to be used only in test suite running mode..This is used for setting up
	 * proper server side of the Server Sync Adapter in the context of the test being
	 * executed between the client and the server
	 * 
	 * @param info
	 */
	public void setUp(String info)
	{				
		String operation = info.substring(info.lastIndexOf('/')+1);
		
		if(info.contains("CleanUp"))
		{
			this.cleanupServerSideState();
			return;
		}
		
		if(info.contains("Errors") || info.contains("ErrorHandling"))
		{
			this.setUpErrorHarness(info);
			return;
		}	
		
		if(info.contains("Notification"))
		{
			this.setUpNotification(info);
			return;
		}
		
		String serverNodeId = this.service;		
		if(info.contains(this.serviceWithMapping))
		{
			serverNodeId = this.serviceWithMapping;
		}
		
		
		this.setUpServerData();
				
		if(info.contains("SlowSync"))
		{
			if(operation.equals("add"))
			{
				//Making sure 'unique-1' and 'unique-2' as added are reflected in the
				//ChangeLog
				List serverChangeLog = new ArrayList();
				for(int i=1; i<3; i++)
				{
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
					serverEntry.setRecordId("unique-"+String.valueOf(i));
					serverChangeLog.add(serverEntry);
				}
				this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
			}
			else if(operation.equals("conflict"))
			{
				//Updating 'unique-1' on the server
				List serverChangeLog = new ArrayList();
				for(int i=1; i<2; i++)
				{
					String recordId = "unique-"+String.valueOf(i);
					ServerRecord record = this.serverController.readServerRecord(recordId);
					record.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+recordId+"/Updated/Server"+"</tag>");
					this.serverController.save(record);
					
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_UPDATE);
					serverEntry.setRecordId(recordId);
					serverChangeLog.add(serverEntry);
				}
				this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
			}
			else if(operation.equals("multirecord"))
			{				
				//also add unique-5, unique-6, and unique-7
				for(int i=5; i<8; i++)
				{
					String uniqueId = "unique-" + String.valueOf(i);
					ServerRecord serverData = new ServerRecord();
					serverData.setObjectId(uniqueId);
					serverData.setFrom(uniqueId+"@from.com");
					serverData.setTo(uniqueId+"@to.com");
					serverData.setSubject(uniqueId+"/Subject");
					serverData.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Message"+"</tag>");
					this.serverController.save(serverData);					
				}				
			}
		}				
		else if(info.contains("OneWayClientSync") || 
		   info.contains("OneWayServerSync") || 
		   info.contains("TwoWaySync") ||
		   info.contains("ChangeLogSupport") ||
		   info.contains("LongObjectSupport") ||
		   info.contains("AnchorSupport") ||
		   info.contains("ObjectStreaming")
		)
		{
			if(operation.equals("add"))
			{
				//Making sure 'unique-1' and 'unique-2' as added are reflected in the
				//ChangeLog
				List serverChangeLog = new ArrayList();
				for(int i=1; i<3; i++)
				{
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
					serverEntry.setRecordId("unique-"+String.valueOf(i));
					serverChangeLog.add(serverEntry);
				}
				this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
			}
			else if(operation.equals("replace") || operation.equals("conflict"))
			{
				//Updating 'unique-1' on the server
				List serverChangeLog = new ArrayList();
				for(int i=1; i<2; i++)
				{
					String recordId = "unique-"+String.valueOf(i);
					ServerRecord record = this.serverController.readServerRecord(recordId);
					record.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+recordId+"/Updated/Server"+"</tag>");
					this.serverController.save(record);
					
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_UPDATE);
					serverEntry.setRecordId(recordId);
					serverChangeLog.add(serverEntry);
				}
				this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
			}
			else if(operation.equals("delete"))
			{
				//Deleting just 'unique-1' from the server
				List serverChangeLog = new ArrayList();
				for(int i=1; i<2; i++)
				{	
					String recordId = "unique-"+String.valueOf(i);
					ServerRecord record = this.serverController.readServerRecord(recordId);
					this.serverController.delete(record);
					
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_DELETE);
					serverEntry.setRecordId(recordId);
					serverChangeLog.add(serverEntry);
				}
				this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
			}
			else if(operation.equals("multirecord"))
			{
				//Making sure 'unique-1' and 'unique-2' as added are reflected in the
				//ChangeLog
				List serverChangeLog = new ArrayList();
				for(int i=1; i<3; i++)
				{
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
					serverEntry.setRecordId("unique-"+String.valueOf(i));
					serverChangeLog.add(serverEntry);
				}				
				
				//also add unique-5, unique-6, and unique-7
				for(int i=5; i<8; i++)
				{
					String uniqueId = "unique-" + String.valueOf(i);
					ServerRecord serverData = new ServerRecord();
					serverData.setObjectId(uniqueId);
					serverData.setFrom(uniqueId+"@from.com");
					serverData.setTo(uniqueId+"@to.com");
					serverData.setSubject(uniqueId+"/Subject");
					serverData.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Message"+"</tag>");
					this.serverController.save(serverData);
					
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
					serverEntry.setRecordId("unique-"+String.valueOf(i));
					serverChangeLog.add(serverEntry);
				}
				
				//Updating 'unique-1' on the server
				for(int i=1; i<2; i++)
				{
					String recordId = "unique-"+String.valueOf(i);
					ServerRecord record = this.serverController.readServerRecord(recordId);
					record.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+recordId+"/Updated/Server"+"</tag>");
					this.serverController.save(record);
					
					ChangeLogEntry serverEntry = new ChangeLogEntry();
					serverEntry.setNodeId(serverNodeId);
					serverEntry.setOperation(ServerSyncEngine.OPERATION_UPDATE);
					serverEntry.setRecordId(recordId);
					serverChangeLog.add(serverEntry);
				}				
				this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
			}
		}				
	}	
	
	private void setUpErrorHarness(String info)
	{				
		this.serverSyncEngine.clearChangeLog(this.deviceId, this.service, this.app);				
		String serverNodeId = this.service;		
		if(info.contains(this.serviceWithMapping))
		{
			serverNodeId = this.serviceWithMapping;
		}
				
		
		if(info.endsWith("deferMapUpdateToNextSync"))
		{
			List serverChangeLog = new ArrayList();
			this.serverController = ServerRecordController.getInstance();					
			this.serverController.deleteAll();
			for(int i=5; i<7; i++)
			{
				String uniqueId = "unique-" + String.valueOf(i);
				ServerRecord serverData = new ServerRecord();
				serverData.setObjectId(uniqueId);
				serverData.setFrom(uniqueId+"@from.com");
				serverData.setTo(uniqueId+"@to.com");
				serverData.setSubject(uniqueId+"/Subject");
				serverData.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Message"+"</tag>");
				this.serverController.save(serverData);
				
				ChangeLogEntry serverEntry = new ChangeLogEntry();
				serverEntry.setNodeId(serverNodeId);
				serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
				serverEntry.setRecordId("unique-"+String.valueOf(i));
				serverChangeLog.add(serverEntry);
			}
			this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
			
			//swap serverSyncAdapter with the one that simulates errors
			SyncServer serverSyncAdapter = new SyncServerAdapterWithMapErrorSimulation(this.serverSyncEngine);
			((SyncServerAdapterWithMapErrorSimulation)serverSyncAdapter).
			activateProcessRecordMapFailure = true;
			
			SyncProcessor syncProcessor = (SyncProcessor)ServiceManager.locate("sync");			
			syncProcessor.setSyncAdapter(serverSyncAdapter);
		}
		else if(info.endsWith("deferMapUpdateToNextSyncFailure"))
		{
			List serverChangeLog = new ArrayList();
			this.serverController = ServerRecordController.getInstance();					
			this.serverController.deleteAll();
			for(int i=5; i<7; i++)
			{
				String uniqueId = "unique-" + String.valueOf(i);
				ServerRecord serverData = new ServerRecord();
				serverData.setObjectId(uniqueId);
				serverData.setFrom(uniqueId+"@from.com");
				serverData.setTo(uniqueId+"@to.com");
				serverData.setSubject(uniqueId+"/Subject");
				serverData.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Message"+"</tag>");
				this.serverController.save(serverData);
				
				ChangeLogEntry serverEntry = new ChangeLogEntry();
				serverEntry.setNodeId(serverNodeId);
				serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
				serverEntry.setRecordId("unique-"+String.valueOf(i));
				serverChangeLog.add(serverEntry);
			}
			this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
			
			//swap serverSyncAdapter with the one that simulates errors
			SyncServer serverSyncAdapter = new SyncServerAdapterWithMapErrorSimulation(this.serverSyncEngine);
			((SyncServerAdapterWithMapErrorSimulation)serverSyncAdapter).
			activateProcessRecordMapFailure = true;
			((SyncServerAdapterWithMapErrorSimulation)serverSyncAdapter).
			activateDeferMapUpdateToNextSyncFailure= true;
			
			SyncProcessor syncProcessor = (SyncProcessor)ServiceManager.locate("sync");			
			syncProcessor.setSyncAdapter(serverSyncAdapter);
		}
		else if(info.endsWith("deferMapUpdateToNextSyncClientPersistFailure"))
		{
			List serverChangeLog = new ArrayList();
			this.serverController = ServerRecordController.getInstance();					
			this.serverController.deleteAll();
			for(int i=5; i<7; i++)
			{
				String uniqueId = "unique-" + String.valueOf(i);
				ServerRecord serverData = new ServerRecord();
				serverData.setObjectId(uniqueId);
				serverData.setFrom(uniqueId+"@from.com");
				serverData.setTo(uniqueId+"@to.com");
				serverData.setSubject(uniqueId+"/Subject");
				serverData.setMessage("<tag apos='apos' quote=\"quote\" ampersand='&'>"+uniqueId+"/Message"+"</tag>");
				this.serverController.save(serverData);
				
				ChangeLogEntry serverEntry = new ChangeLogEntry();
				serverEntry.setNodeId(serverNodeId);
				serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
				serverEntry.setRecordId("unique-"+String.valueOf(i));
				serverChangeLog.add(serverEntry);
			}
			this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
			
			SyncProcessor syncProcessor = (SyncProcessor)ServiceManager.locate("sync");			
			syncProcessor.setSyncAdapter(this.originalAdapter);
		}
		else if(info.contains("ServerSideErrorHandling"))
		{			
			this.setUpServerData();
			List serverChangeLog = new ArrayList();
			for(int i=1; i<3; i++)
			{
				ChangeLogEntry serverEntry = new ChangeLogEntry();
				serverEntry.setNodeId(serverNodeId);
				serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
				serverEntry.setRecordId("unique-"+String.valueOf(i));
				serverChangeLog.add(serverEntry);
			}
			this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
			
			SyncProcessor syncProcessor = (SyncProcessor)ServiceManager.locate("sync");			
			syncProcessor.setSyncAdapter(this.adapterWithErrors);
		}		
		else
		{
			this.setUpServerData();
			List serverChangeLog = new ArrayList();
			for(int i=1; i<3; i++)
			{
				ChangeLogEntry serverEntry = new ChangeLogEntry();
				serverEntry.setNodeId(serverNodeId);
				serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
				serverEntry.setRecordId("unique-"+String.valueOf(i));
				serverChangeLog.add(serverEntry);
			}
			this.serverSyncEngine.addChangeLogEntries(this.deviceId, this.app, serverChangeLog);
		}
	}
	
	private void setUpNotification(String info)
	{				
		if(info.contains("/sendNewEmail/"))
		{
			//Add a new email as if an new email was received from the internet
			//Out of Band addition
			String newEmailUid = info.substring(info.lastIndexOf('/')+1);
			Email email = new Email(
					newEmailUid,
					"from@blah.com",
					"to@blah.com",
					"newSubject",
					"new message"
			);
			EmailConnector.add(this.deviceId, email);
		}
		else
		{
			EmailConnector.initialize(this.deviceId);
		}
	}
	//------Processor implementation---------------------------------------------------------------------------------------------------------------------------------
	public String getId()
	{
		return this.id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}

	public String process(Input input) throws ProcessorException 
	{
		String payload = input.getMessage();
		
		this.setUp(payload);
		
		return null;
	}	
}
