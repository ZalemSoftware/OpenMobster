/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.device.agent.frameworks.mobileObject.MobileObjectDatabase;
import org.openmobster.device.agent.sync.SyncAdapter;
import org.openmobster.device.agent.sync.SyncAdapterRequest;
import org.openmobster.device.agent.sync.SyncAdapterResponse;
import org.openmobster.device.agent.sync.SyncXMLTags;
import org.openmobster.device.agent.sync.engine.SyncEngine;

import org.openmobster.core.synchronizer.server.SyncServer;
import org.openmobster.core.synchronizer.server.engine.MapEngine;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;

import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.identity.IdentityController;

import test.openmobster.device.agent.sync.device.SyncAdapterWithMapErrorSimulation;
import test.openmobster.device.agent.sync.server.ServerRecordControllerMapping;
import test.openmobster.device.agent.sync.server.SyncServerAdapterWithMapErrorSimulation;

/**
 * @author openmobster@gmail.com
 */
public abstract class AbstractSyncMapping extends AbstractSync 
{
	private static Logger log = Logger.getLogger(AbstractSyncMapping.class);
	
	protected MapEngine mapEngine = null;
	
	protected void setUp() throws Exception 
	{
		this.service = "testServerBean.testMapping";
		
		ServiceManager.bootstrap();
		
		this.deviceSyncEngine = (SyncEngine)ServiceManager.
		locate("simulator://SyncEngine");
		
		this.serverSyncEngine = (ServerSyncEngine)ServiceManager.
		locate("synchronizer://ServerSyncEngine");
				
		this.serverController = ServerRecordControllerMapping.getInstance();
		
		this.deviceDatabase = (MobileObjectDatabase)
		ServiceManager.locate("mobileObject://MobileObjectDatabase");
		
		this.identityController = (IdentityController)
		ServiceManager.locate("security://IdentityController");
		
		this.deviceController = (DeviceController)
		ServiceManager.locate("security://DeviceController");
		
		mapEngine = (MapEngine)ServiceManager.locate("synchronizer://MapEngine");
		
		//SetUp State of the system
		this.setUpSecurity();
		this.setUpServerData();	
		
		this.performSlowSync();
	}
	
	protected void tearDown() throws Exception 
	{	
		super.tearDown();
	}
	
	public void assertMapping()
	{
		assertEquals(this.mapEngine.mapFromLocalToServer("1-luid"), "1");
		assertEquals(this.mapEngine.mapFromLocalToServer("2-luid"), "2");
				
		assertEquals(this.mapEngine.mapFromServerToLocal("1"), "1-luid");
		assertEquals(this.mapEngine.mapFromServerToLocal("2"), "2-luid");
	}
	
	public void assertMappingWithNewObjects()
	{
		assertEquals(this.mapEngine.mapFromLocalToServer("1-luid"), "1");
		assertEquals(this.mapEngine.mapFromLocalToServer("2-luid"), "2");
		assertEquals(this.mapEngine.mapFromLocalToServer("3-luid"), "3");
		assertEquals(this.mapEngine.mapFromLocalToServer(this.newDeviceRecordId+"-luid"), "4");
		
		assertEquals(this.mapEngine.mapFromServerToLocal("1"), "1-luid");
		assertEquals(this.mapEngine.mapFromServerToLocal("2"), "2-luid");
		assertEquals(this.mapEngine.mapFromServerToLocal("3"), "3-luid");
		assertEquals(this.mapEngine.mapFromServerToLocal("4"), this.newDeviceRecordId+"-luid");
	}
	//---------------------------------------------------------------------------------------------------------
	protected void executeErrorSync(boolean processRecordMapFailure, boolean deferMapUpdateToNextSyncFailure,
	boolean clientAdapterWithErrors) throws Exception
	{
		SyncServer serverSyncAdapter = null;
		SyncAdapter deviceSyncAdapter = null;
		
		serverSyncAdapter = new SyncServerAdapterWithMapErrorSimulation(this.serverSyncEngine);		
		if(deferMapUpdateToNextSyncFailure)
		{
			((SyncServerAdapterWithMapErrorSimulation)serverSyncAdapter).
			activateDeferMapUpdateToNextSyncFailure = true;
		}
		if(processRecordMapFailure)
		{
			((SyncServerAdapterWithMapErrorSimulation)serverSyncAdapter).
			activateProcessRecordMapFailure = true;
		}
		
		if(clientAdapterWithErrors)
		{
			deviceSyncAdapter = new SyncAdapterWithMapErrorSimulation();
			deviceSyncAdapter.setSyncEngine(this.deviceSyncEngine);
		}
		else
		{
			deviceSyncAdapter = new SyncAdapter();
			deviceSyncAdapter.setSyncEngine(this.deviceSyncEngine);
		}
		
		SyncAdapterRequest initRequest = new SyncAdapterRequest();
		initRequest.setAttribute(SyncServer.SOURCE, this.deviceId);
		initRequest.setAttribute(SyncServer.TARGET, this.serverId);
		initRequest.setAttribute(SyncServer.MAX_CLIENT_SIZE, String.valueOf("0"));
		initRequest.setAttribute(SyncServer.CLIENT_INITIATED, "true");
		initRequest.setAttribute(SyncServer.DATA_SOURCE, service);
		initRequest.setAttribute(SyncServer.DATA_TARGET, service);
		initRequest.setAttribute(SyncServer.SYNC_TYPE, SyncAdapter.TWO_WAY);
		initRequest.setAttribute(SyncXMLTags.App, this.app);
		
		this.executeSyncProtocol(initRequest, serverSyncAdapter, deviceSyncAdapter);
	}
		
	
	private void executeSyncProtocol(SyncAdapterRequest initRequest, SyncServer serverSyncAdapter,
	SyncAdapter deviceSyncAdapter) throws Exception
	{
		log.info("---------------------------------------------------------------");
		log.info("Starting the Map Error Sync...................................");
				
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
		
		log.info("---------------------------------------------------------------");
	}
}