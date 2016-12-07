/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openmobster.android.utils.OpenMobsterBugUtils;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.module.connection.NetSession;
import org.openmobster.core.mobileCloud.android.module.connection.NetworkConnector;
import org.openmobster.core.mobileCloud.android.module.connection.NetworkException;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.android.module.sync.daemon.LoadProxyDaemon;
import org.openmobster.core.mobileCloud.android.module.sync.engine.ChangeLogEntry;
import org.openmobster.core.mobileCloud.android.module.sync.engine.SyncEngine;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;

import android.content.Context;
import android.util.Log;


/**
 * @author openmobster@gmail.com
 *
 */
public final class SyncService extends Service 
{		
	private SyncEngine syncEngine;
	
	public static String OPERATION_ADD = "Add";
	public static String OPERATION_UPDATE = "Replace";
	public static String OPERATION_DELETE = "Delete";	
	public static String OPERATION_MAP = "Map";
				
	public SyncService()
	{
		
	}
	
	public void start()
	{
		try
		{
			this.syncEngine = new SyncEngine();																	
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "constructor", new Object[]{
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
		}
	}
	
	public void stop()
	{
		this.syncEngine = null;
	}
	
	/**
	 * 
	 * @return
	 */
	public static SyncService getInstance()
	{
		return (SyncService)Registry.getActiveInstance().lookup(SyncService.class);
	}
	
	public String getDeviceId()
	{
		Context context = Registry.getActiveInstance().getContext();
		return Configuration.getInstance(context).getDeviceId();
	}
	
	public String getServerId()
	{
		Context context = Registry.getActiveInstance().getContext();
		return Configuration.getInstance(context).getServerId();
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param recordId
	 */
	public synchronized void performStreamSync(String serviceId, String recordId, boolean isBackground) throws SyncException
	{
		NetSession session = null;
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			Configuration configuration = Configuration.getInstance(context);
			boolean secure = configuration.isSSLActivated();
			session = NetworkConnector.getInstance().openSession(secure);
						
			String deviceId = configuration.getDeviceId();
			String authHash = configuration.getAuthenticationHash();
			
			String sessionInitPayload = 
				"<request>" +
					"<header>" +
						"<name>device-id</name>"+
						"<value><![CDATA["+deviceId+"]]></value>"+
					"</header>"+
					"<header>" +
						"<name>nonce</name>"+
						"<value><![CDATA["+authHash+"]]></value>"+
					"</header>"+
					"<header>" +
						"<name>processor</name>"+
						"<value>sync</value>"+
					"</header>";
					
					/*
					 * Estrutura adicionada na versão 2.4-M3.1
					 * Se há um token de autenticação sendo utilizado atualmente, envia-o para o servidor. 
					 */
					if (configuration.getAuthenticationToken() != null) {
						sessionInitPayload +=
							"<header>" +
								"<name>authToken</name>" +
								"<value><![CDATA[" + configuration.getAuthenticationToken() + "]]></value>" +
							"</header>";
					}
					
					sessionInitPayload +=
				"</request>";
			
			String response = session.sendTwoWay(sessionInitPayload);
			
			if(response.indexOf("status=200")!=-1)
			{
				this.performStreamSync(session, serviceId, recordId, isBackground);
			}
		}
		catch(NetworkException e)
		{
			throw new SyncException(this.getClass().getName(), "performStreamSync://NetworkException", 
			new Object[]{serviceId, recordId});
		}
		finally
		{
			if(session != null)
			{
				session.close();
			}
		}
	}
	
	/**
	 * 
	 * @param deviceService
	 * @param serverService
	 * @throws SyncException
	 */
	public synchronized void performTwoWaySync(String deviceService, String serverService, boolean isBackground) throws SyncException
	{
		try
		{
			this.startSync(SyncAdapter.TWO_WAY, deviceService, serverService, isBackground);
		}
		catch(NetworkException e)
		{
			throw new SyncException(this.getClass().getName(), "performTwoWaySync://IOException", 
			new Object[]{SyncAdapter.TWO_WAY, deviceService, serverService});
		}
	}
	
	/**
	 * 
	 * @param deviceService
	 * @param serverService
	 * @throws SyncException
	 */
	public synchronized void performSlowSync(String deviceService, String serverService, boolean isBackground) throws SyncException
	{
		try
		{
			this.startSync(SyncAdapter.SLOW_SYNC, deviceService, serverService, isBackground);
		}
		catch(NetworkException e)
		{
			throw new SyncException(this.getClass().getName(), "performSlowSync://IOException", 
			new Object[]{SyncAdapter.SLOW_SYNC, deviceService, serverService});
		}
	}
	
	/**
	 * 
	 * @param deviceService
	 * @param serverService
	 * @throws SyncException
	 */
	public synchronized void performOneWayServerSync(String deviceService, String serverService, boolean isBackground) throws SyncException
	{
		try
		{
			this.startSync(SyncAdapter.ONE_WAY_SERVER, deviceService, serverService, isBackground);
		}
		catch(NetworkException e)
		{
			throw new SyncException(this.getClass().getName(), "performOneWayServerSync://IOException", 
			new Object[]{SyncAdapter.ONE_WAY_SERVER, deviceService, serverService});
		}
	}
	
	/**
	 * 
	 * @param deviceService
	 * @param serverService
	 * @throws SyncException
	 */
	public synchronized void performOneWayClientSync(String deviceService, String serverService, boolean isBackground) throws SyncException
	{
		try
		{
			this.startSync(SyncAdapter.ONE_WAY_CLIENT, deviceService, serverService, isBackground);
		}
		catch(NetworkException e)
		{
			throw new SyncException(this.getClass().getName(), "performOneWayClient://IOException", 
			new Object[]{SyncAdapter.ONE_WAY_CLIENT, deviceService, serverService});
		}
	}
	
	public synchronized void performBootSync(String deviceService, String serverService, boolean isBackground) throws SyncException
	{
		try
		{
			this.startSync(SyncAdapter.BOOT_SYNC, deviceService, serverService, isBackground);
			LoadProxyDaemon.getInstance().scheduleProxyTask();
		}
		catch(Exception e)
		{
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 * Contorno para o problema do OpenMobster apagar dados não sincronizados. Evita a exclusão total dos dados
			 * de canais persistentes, pois o Bootup irá apenas sobrescrever o que precisa.
			 */
			if (OpenMobsterBugUtils.getInstance().isPersistentChannel(deviceService)) {
				Log.e("OpenMobster Error", String.format("An Exception ocurred while performing a boot sync! The local data of the following channel will not be wiped %s/%s.", deviceService, serverService));
				e.printStackTrace();
			} else {
				//put the channel in a non-booted state in case of an exception
				MobileObjectDatabase.getInstance().bootup(deviceService);
			}
			
			throw new SyncException(this.getClass().getName(), "performBootSync://IOException", 
			new Object[]{SyncAdapter.BOOT_SYNC, deviceService, serverService});
		}
	}
	
	public void updateChangeLog(String service, String operation, String objectId) throws SyncException
	{
		updateChangeLog(service, operation, objectId, false);
	}
	
	public void updateChangeLog(String service, String operation, String objectId, boolean bulk) throws SyncException
	{
		
		ChangeLogEntry changelogEntry = new ChangeLogEntry();
		changelogEntry.setNodeId(service);
		changelogEntry.setOperation(operation);
		changelogEntry.setRecordId(objectId);
		
		if (bulk) {
			// Avoid the existence check
			this.syncEngine.addChangeLogEntry(changelogEntry);
		} else {
			List<ChangeLogEntry> entries = new ArrayList<ChangeLogEntry>();
			entries.add(changelogEntry);
			this.syncEngine.addChangeLogEntries(entries);
		}
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param is
	 * @param os
	 * @param data
	 * @throws SyncException
	 * @throws IOException
	 */
	private void performStreamSync(NetSession session, String serviceId, 
	String streamRecordId, boolean isBackground)
	throws SyncException, NetworkException
	{	
		Context context = Registry.getActiveInstance().getContext();
		SyncAdapter syncAdapter = new SyncAdapter();
		syncAdapter.setSyncEngine(syncEngine);
		
    	//Get the initialization payload
		SyncAdapterRequest request = new SyncAdapterRequest();
		request.setAttribute(SyncAdapter.SOURCE, Configuration.getInstance(context).
		getDeviceId());
		request.setAttribute(SyncAdapter.TARGET, Configuration.getInstance(context).
		getServerId());
		request.setAttribute(SyncAdapter.MAX_CLIENT_SIZE, ""+Configuration.
		getInstance(context).getMaxPacketSize());
		request.setAttribute(SyncAdapter.CLIENT_INITIATED, "true");
		request.setAttribute(SyncAdapter.DATA_SOURCE, serviceId);
		request.setAttribute(SyncAdapter.DATA_TARGET, serviceId);
		request.setAttribute(SyncAdapter.SYNC_TYPE, SyncAdapter.STREAM);
		request.setAttribute(SyncAdapter.STREAM_RECORD_ID, streamRecordId);
		request.setAttribute("isBackgroundSync", String.valueOf(isBackground));
		request.setAttribute(SyncXMLTags.App, context.getPackageName());
		
		//Start the synchronization session
		SyncAdapterResponse response = syncAdapter.start(request);
		
		//Setup the payload to be sent to the server
		String payLoad = (String)response.getAttribute(SyncAdapter.PAYLOAD);		
		
		//Start sync session																		
		String data = session.sendPayloadTwoWay(payLoad);
		
		
		
		//Orchestrate the synchronization session until it is successfully finished
		while(true)
		{
			request = new SyncAdapterRequest();
			request.setAttribute(SyncAdapter.PAYLOAD, data);
			response = syncAdapter.service(request);
						
			payLoad = (String)response.getAttribute(SyncAdapter.PAYLOAD);			
			
			
			if(response.getStatus() == SyncAdapter.RESPONSE_CLOSE)
			{
				break;
			}			
			
			
			//Send the payload to the server for processing						
			data = session.sendPayloadTwoWay(payLoad);
		}
	}
	
	private void performSync(NetSession session, String syncType, String deviceService, String serverService, boolean isBackground)
	throws SyncException, NetworkException
	{	
		Context context = Registry.getActiveInstance().getContext();
		SyncAdapter syncAdapter = new SyncAdapter();
		syncAdapter.setSyncEngine(syncEngine);
		
    	//Get the initialization payload
		SyncAdapterRequest request = new SyncAdapterRequest();
		request.setAttribute(SyncAdapter.SOURCE, Configuration.
		getInstance(context).getDeviceId());
		request.setAttribute(SyncAdapter.TARGET, Configuration.
		getInstance(context).getServerId());
		request.setAttribute(SyncAdapter.MAX_CLIENT_SIZE, ""+Configuration.
		getInstance(context).getMaxPacketSize());
		request.setAttribute(SyncAdapter.CLIENT_INITIATED, "true");
		request.setAttribute(SyncAdapter.DATA_SOURCE, deviceService);
		request.setAttribute(SyncAdapter.DATA_TARGET, serverService);
		request.setAttribute(SyncAdapter.SYNC_TYPE, syncType);
		request.setAttribute("isBackgroundSync", String.valueOf(isBackground));
		request.setAttribute(SyncXMLTags.App, context.getPackageName());
		
		//Start the synchronization session
		SyncAdapterResponse response = syncAdapter.start(request);
		
		//Setup the payload to be sent to the server
		String payLoad = (String)response.getAttribute(SyncAdapter.PAYLOAD);		
		
		//Start sync session
		String data = session.sendPayloadTwoWay(payLoad);
				
		//Orchestrate the synchronization session until it is successfully finished
		while(true)
		{
			request = new SyncAdapterRequest();
			request.setAttribute(SyncAdapter.PAYLOAD, data);
			response = syncAdapter.service(request);
						
			payLoad = (String)response.getAttribute(SyncAdapter.PAYLOAD);			
			
			
			if(response.getStatus() == SyncAdapter.RESPONSE_CLOSE)
			{
				break;
			}			
			
			
			//Send the payload to the server for processing
			data = session.sendPayloadTwoWay(payLoad);	
		}
	}
	
	private void startSync(String syncType, String deviceService, String serverService, boolean isBackground) 
	throws NetworkException, SyncException
	{				
		NetSession session = null;
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			Configuration configuration = Configuration.getInstance(context);
			boolean secure = configuration.isSSLActivated();
			session = NetworkConnector.getInstance().openSession(secure);
			
			String deviceId = configuration.getDeviceId();
			String authHash = configuration.getAuthenticationHash();
			String sessionInitPayload = 
				"<request>" +
					"<header>" +
						"<name>device-id</name>"+
						"<value><![CDATA["+deviceId+"]]></value>"+
					"</header>"+
					"<header>" +
						"<name>nonce</name>"+
						"<value><![CDATA["+authHash+"]]></value>"+
					"</header>"+
					"<header>" +
						"<name>processor</name>"+
						"<value>sync</value>"+
					"</header>";
					
					/*
					 * Estrutura adicionada na versão 2.4-M3.1
					 * Se há um token de autenticação sendo utilizado atualmente, envia-o para o servidor. 
					 */
					if (configuration.getAuthenticationToken() != null) {
						sessionInitPayload +=
							"<header>" +
								"<name>authToken</name>" +
								"<value><![CDATA[" + configuration.getAuthenticationToken() + "]]></value>" +
							"</header>";
					}
					
					sessionInitPayload +=
				"</request>";
			
			String response = session.sendTwoWay(sessionInitPayload);
			
			if(response.indexOf("status=200")!=-1)
			{
				this.performSync(session, syncType, deviceService, serverService, isBackground);
			}
		}		
		finally
		{
			if(session != null)
			{
				session.close();
			}
		}
	}
	
	
	/*
	 * Métodos adicionados na versão 2.4-M3.1
	 */
	
	public void clearChangeLog() throws SyncException {
		syncEngine.clearChangeLog();
	}
}
