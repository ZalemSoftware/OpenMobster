/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.test.framework;

import java.util.Vector;
import java.util.List;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.security.Provisioner;
import org.openmobster.device.api.service.Request;
import org.openmobster.device.api.service.Response;
import org.openmobster.device.api.service.MobileService;

import org.openmobster.device.agent.frameworks.mobileObject.MobileObjectDatabase;
import org.openmobster.device.agent.sync.SyncAdapter;
import org.openmobster.device.agent.sync.SyncAdapterRequest;
import org.openmobster.device.agent.sync.SyncAdapterResponse;
import org.openmobster.device.agent.sync.SyncXMLTags;
import org.openmobster.device.agent.sync.engine.SyncEngine;
import org.openmobster.device.agent.sync.SyncService;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;

/**
 * @author openmobster@gmail.com
 */
public class MobileBeanRunner 
{
	private static Logger log = Logger.getLogger(MobileBeanRunner.class);
	
	private String deviceId;
	private String serverId;
	private String service;
	private String user;
	private String credential;
	private String serverIp;
	private String app;
	
	private SyncEngine  deviceSyncEngine;
	private SyncService syncService;
	private MobileObjectDatabase deviceDatabase;
	private Provisioner provisioner;
	private Configuration configuration;
	
	private CometDaemon cometDaemon;
	
	public MobileBeanRunner()
	{
		
	}

	public String getDeviceId() 
	{
		return deviceId;
	}

	public void setDeviceId(String deviceId) 
	{
		this.deviceId = deviceId;
	}

	public String getServerId() 
	{
		return serverId;
	}

	public void setServerId(String serverId) 
	{
		this.serverId = serverId;
	}

	public String getService() 
	{
		return service;
	}

	public void setService(String service) 
	{
		this.service = service;
	}

	public MobileObjectDatabase getDeviceDatabase() 
	{
		return deviceDatabase;
	}

	public void setDeviceDatabase(MobileObjectDatabase deviceDatabase) 
	{
		this.deviceDatabase = deviceDatabase;
	}

	public SyncEngine getDeviceSyncEngine() 
	{
		return deviceSyncEngine;
	}

	public void setDeviceSyncEngine(SyncEngine deviceSyncEngine) 
	{
		this.deviceSyncEngine = deviceSyncEngine;
	}
		
	public SyncService getSyncService() 
	{
		return syncService;
	}

	public void setSyncService(SyncService syncService) 
	{
		this.syncService = syncService;
	}
	
	public Provisioner getProvisioner() 
	{
		return provisioner;
	}

	public void setProvisioner(Provisioner provisioner) 
	{
		this.provisioner = provisioner;
	}
		
	public String getUser() 
	{
		return user;
	}

	public void setUser(String user) 
	{
		this.user = user;
	}
		
	public String getCredential() 
	{
		return credential;
	}

	public void setCredential(String credential) 
	{
		this.credential = credential;
	}
	
	

	public CometDaemon getCometDaemon() 
	{
		return cometDaemon;
	}

	public void setCometDaemon(CometDaemon cometDaemon) 
	{
		this.cometDaemon = cometDaemon;
	}
	
	public Configuration getConfiguration() 
	{
		return configuration;
	}

	public void setConfiguration(Configuration configuration) 
	{
		this.configuration = configuration;
	}
	
	public String getServerIp()
	{
		return serverIp;
	}

	public void setServerIp(String serverIp)
	{
		this.serverIp = serverIp;
	}
	
	public String getApp()
	{
		return app;
	}

	public void setApp(String app)
	{
		this.app = app;
	}

	public void start()
	{
		org.openmobster.device.agent.configuration.Configuration.getInstance().cleanup();			
	}
	
	public void stop()
	{
	}
	//----------------------------------------------------------------------------------------------------------------------
	public void activateDevice()
	{
		if(this.provisioner.getIdentityController().read(this.user) == null)
		{
			this.provisioner.registerIdentity(this.user, this.credential);
		}
		
		String server = null;
		String email = null;
		String deviceIdentifier = null;
		try
		{						
			deviceIdentifier = this.deviceId;						
			server = "127.0.0.1";
			if(this.serverIp != null && this.serverIp.trim().length()>0)
			{
				server = this.serverIp;
			}
			
			email = this.user;
			String password = this.credential;
												
			//Set the unique device identifier
			configuration.setDeviceId(deviceIdentifier);
			configuration.setServerIp(server);
			configuration.setEmail(email);
			configuration.deActivateSSL();
			
			//TODO: clean this nasty nasty hack...with this, only single device sync scenarios
			//can be tested. Multi-Device Comet scenarios are still possible
			org.openmobster.device.agent.configuration.Configuration.getInstance().setDeviceId(deviceIdentifier);
			org.openmobster.device.agent.configuration.Configuration.getInstance().setServerIp(server);
			org.openmobster.device.agent.configuration.Configuration.getInstance().setEmail(email);
			org.openmobster.device.agent.configuration.Configuration.getInstance().deActivateSSL();
			
			Request request = new Request("provisioning");
			request.setAttribute("email", email);
			request.setAttribute("password", password);			
			request.setAttribute("identifier", deviceIdentifier);
			
			Response response = MobileService.invoke(this,request);
						
			if(response.getAttribute("error") == null && response.getAttribute("idm-error") == null)
			{
				//Success Scenario
				processProvisioningSuccess(response);
			}
			else
			{
				//Error Scenario
				String errorKey = response.getAttribute("error");
				if(errorKey == null)
				{
					errorKey = response.getAttribute("idm-error");
				}
								
				throw new RuntimeException(errorKey);
			}						
		}
		catch(Exception e)
		{
			log.error("-------------------------------");
			log.error("Activation Failed with: "+server+" Exception: "+e.toString());
			log.error("-------------------------------");
			throw new RuntimeException(e.toString());
		}
	}
	
	private void processProvisioningSuccess(Response response)
	{				
		//Read the Server Response
		String serverId = response.getAttribute("serverId");
		String plainServerPort = response.getAttribute("plainServerPort");
		String secureServerPort = response.getAttribute("secureServerPort");
		String isSSlActive = response.getAttribute("isSSLActive");
		String maxPacketSize = response.getAttribute("maxPacketSize");
		String authenticationHash = response.getAttribute("authenticationHash");
		
		//Setup the configuration
		configuration.setServerId(serverId);
		configuration.setPlainServerPort(plainServerPort);
		if(secureServerPort != null && secureServerPort.trim().length()>0)
		{
			configuration.setSecureServerPort(secureServerPort);
		}
		
		/*if(isSSlActive.equalsIgnoreCase("true"))
		{
			configuration.activateSSL();
		}
		else
		{
			configuration.deActivateSSL();
		}*/
		
		configuration.setMaxPacketSize(Integer.parseInt(maxPacketSize));
		configuration.setAuthenticationHash(authenticationHash);
		
		
		//Setup the configuration
		//TODO: clean this nasty nasty hack...with this, only single device sync scenarios
		//can be tested. Multi-Device Comet scenarios are still possible
		org.openmobster.device.agent.configuration.Configuration.getInstance().setServerId(serverId);
		org.openmobster.device.agent.configuration.Configuration.getInstance().setPlainServerPort(plainServerPort);
		if(secureServerPort != null && secureServerPort.trim().length()>0)
		{
			org.openmobster.device.agent.configuration.Configuration.getInstance().setSecureServerPort(secureServerPort);
		}
		
		/*if(isSSlActive.equalsIgnoreCase("true"))
		{
			configuration.activateSSL();
		}
		else
		{
			configuration.deActivateSSL();
		}*/
		
		org.openmobster.device.agent.configuration.Configuration.getInstance().setMaxPacketSize(Integer.parseInt(maxPacketSize));
		org.openmobster.device.agent.configuration.Configuration.getInstance().setAuthenticationHash(authenticationHash);
	}
	
	public void bootService()
	{
		try
		{			
			this.performSync(0, SyncAdapter.BOOT_SYNC);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void resetChannel()
	{
		try
		{
			this.performSync(0, SyncAdapter.ONE_WAY_CLIENT);
			this.performSync(0, SyncAdapter.BOOT_SYNC);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void swapConfiguration()
	{
		org.openmobster.device.agent.configuration.Configuration.getInstance().swapState(this.configuration);
	}
	
	public void longBootup()
	{
		try
		{
			this.performSync(0, SyncAdapter.SLOW_SYNC);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void syncService()
	{
		try
		{
			this.performSync(0, SyncAdapter.TWO_WAY);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void syncObject(String objectId)
	{
		try
		{
			this.performSync(0, SyncAdapter.STREAM, objectId);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void destroyService()
	{
		try
		{
			List<MobileObject> all = this.deviceDatabase.readByStorage(this.service);
			
			if(all != null)
			{
				for(MobileObject mobileObject: all)
				{
					this.delete(mobileObject);
				}
			}
			
			this.performSync(0, SyncAdapter.TWO_WAY);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void create(MobileObject mobileObject)
	{
		try
		{
			mobileObject.setStorageId(this.service);
			this.deviceDatabase.create(mobileObject);
			
			Vector changelog = new Vector();
			org.openmobster.device.agent.sync.engine.ChangeLogEntry entry = 
			new org.openmobster.device.agent.sync.engine.ChangeLogEntry();
			entry.setNodeId(this.service);
			entry.setOperation(SyncEngine.OPERATION_ADD);
			entry.setRecordId(mobileObject.getRecordId());
			changelog.add(entry);
			this.deviceSyncEngine.addChangeLogEntries(changelog);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void update(MobileObject mobileObject)
	{
		try
		{
			this.deviceDatabase.update(mobileObject);
			
			Vector changelog = new Vector();
			org.openmobster.device.agent.sync.engine.ChangeLogEntry entry = 
			new org.openmobster.device.agent.sync.engine.ChangeLogEntry();
			entry.setNodeId(this.service);
			entry.setOperation(SyncEngine.OPERATION_UPDATE);
			entry.setRecordId(mobileObject.getRecordId());
			changelog.add(entry);
			this.deviceSyncEngine.addChangeLogEntries(changelog);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void delete(MobileObject mobileObject)
	{
		try
		{
			this.deviceDatabase.delete(mobileObject);
			
			Vector changelog = new Vector();
			org.openmobster.device.agent.sync.engine.ChangeLogEntry entry = 
			new org.openmobster.device.agent.sync.engine.ChangeLogEntry();
			entry.setNodeId(this.service);
			entry.setOperation(SyncEngine.OPERATION_DELETE);
			entry.setRecordId(mobileObject.getRecordId());
			changelog.add(entry);
			this.deviceSyncEngine.addChangeLogEntries(changelog);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public MobileObject read(String id)
	{
		try
		{
			MobileObject mobileObject = this.deviceDatabase.read(this.service, id);			
			return mobileObject;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public List<MobileObject> readAll()
	{
		try
		{
			return this.deviceDatabase.readByStorage(this.service);			
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	//------Synchronization related------------------------------------------------------------------------------------------------
	private void performSync(int maxClientSize, String syncType) throws Exception
	{
		// Get the initialization payload
		SyncAdapterRequest initRequest = new SyncAdapterRequest();
		initRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.SOURCE, this.deviceId);
		initRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.TARGET, this.serverId);
		initRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.MAX_CLIENT_SIZE, String.valueOf(maxClientSize));
		initRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.CLIENT_INITIATED, "true");
		initRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.DATA_SOURCE, service);
		initRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.DATA_TARGET, service);
		initRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.SYNC_TYPE, syncType);
		initRequest.setAttribute(SyncXMLTags.App, this.app);
		this.executeSyncProtocol(initRequest);		
	}
	
	private void performSync(int maxClientSize, String syncType, String oid) throws Exception
	{
		// Get the initialization payload
		SyncAdapterRequest initRequest = new SyncAdapterRequest();
		initRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.SOURCE, this.deviceId);
		initRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.TARGET, this.serverId);
		initRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.MAX_CLIENT_SIZE, String.valueOf(maxClientSize));
		initRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.CLIENT_INITIATED, "true");
		initRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.DATA_SOURCE, service);
		initRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.DATA_TARGET, service);
		initRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.SYNC_TYPE, syncType);
		initRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.STREAM_RECORD_ID, oid);
		initRequest.setAttribute(SyncXMLTags.App, this.app);
		this.executeSyncProtocol(initRequest);		
	}
	
	protected void executeSyncProtocol(SyncAdapterRequest initRequest) throws Exception
	{
		//Get the Server Sync Adapter
		org.openmobster.core.synchronizer.server.SyncServer serverSyncAdapter = (org.openmobster.core.synchronizer.server.SyncServer)ServiceManager.locate("synchronizer://SyncServerAdapter");		

		// Get the Device Sync Adapter
		SyncAdapter deviceSyncAdapter = new SyncAdapter();
		deviceSyncAdapter.setSyncEngine(this.deviceSyncEngine);
		
		SyncAdapterResponse initResponse = deviceSyncAdapter.start(initRequest);

		log.info("-----------------------------");
		log.info("Client ="
				+ ((String)initResponse.getAttribute(org.openmobster.core.synchronizer.server.SyncServer.PAYLOAD)).replaceAll("\n", ""));
		log.info("-----------------------------");

		boolean close = false;
		SyncAdapterRequest clientRequest = new SyncAdapterRequest();
		org.openmobster.core.synchronizer.model.SyncAdapterRequest serverRequest = 
		new org.openmobster.core.synchronizer.model.SyncAdapterRequest();
		serverRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.PAYLOAD, initResponse
				.getAttribute(org.openmobster.core.synchronizer.server.SyncServer.PAYLOAD));
		do
		{
			org.openmobster.core.synchronizer.model.SyncAdapterResponse serverResponse = serverSyncAdapter
					.service(serverRequest);

			// Setup request to be sent to the Client Syncher
			// based on payload received from the server
			String payload = (String) serverResponse
					.getAttribute(org.openmobster.core.synchronizer.server.SyncServer.PAYLOAD);
			log.info("-----------------------------");
			log.info("Server =" + payload.replaceAll("\n", ""));
			log.info("-----------------------------");

			clientRequest = new SyncAdapterRequest();
			clientRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.PAYLOAD, payload);
			SyncAdapterResponse clientResponse = deviceSyncAdapter
					.service(clientRequest);

			if (clientResponse.getStatus() == org.openmobster.core.synchronizer.server.SyncServer.RESPONSE_CLOSE)
			{
				close = true;
			}
			else
			{
				payload = (String) clientResponse
						.getAttribute(org.openmobster.core.synchronizer.server.SyncServer.PAYLOAD);
				serverRequest.setAttribute(org.openmobster.core.synchronizer.server.SyncServer.PAYLOAD, payload);
				log.info("-----------------------------");
				log.info("Client =" + payload.replaceAll("\n", ""));
				log.info("-----------------------------");
			}

		} while (!close);
	}				
	//------Device Side Only--------------------------------------------------------------------------------------------------------------------
	public void appReset()
	{
		try
		{
			List<MobileObject> all = this.deviceDatabase.readByStorage(this.service);
			
			if(all != null)
			{
				for(MobileObject mobileObject: all)
				{
					this.deviceDatabase.delete(mobileObject);
				}
			}						
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void resetSyncAnchor()
	{
		try
		{
			this.deviceSyncEngine.getSyncDataSource().deleteAnchor();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.toString());
		}
	}
	//-----------------------------------------------------------------------------------------------------------
	public void startCometDaemon()
	{
		Thread thread = new Thread(new Runnable(){
			public void run()
			{
				try
				{
					MobileBeanRunner.this.cometDaemon.startSubscription();
					MobileBeanRunner.this.cometDaemon.waitforCometDaemon();
				}
				catch(Exception e)
				{
					log.error(this, e);
				}
			}
		});
		thread.start();
	}
}
