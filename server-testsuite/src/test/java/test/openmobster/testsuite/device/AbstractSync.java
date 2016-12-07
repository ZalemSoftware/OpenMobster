/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.testsuite.device;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Vector;

import org.openmobster.cloudConnector.api.SecurityConfig;
import org.openmobster.core.common.IOUtilities;
import org.openmobster.device.agent.Tools;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObjectDatabase;
import org.openmobster.device.agent.sync.SyncService;
import org.openmobster.device.agent.sync.engine.SyncEngine;
import org.openmobster.device.agent.configuration.Configuration;

import junit.framework.TestCase;

/**
 * @author openmobster@gmail.com
 */
public abstract class AbstractSync extends TestCase 
{
	protected String service = "testServerBean";
	protected String serverId = "http://www.openmobster.org/sync-server";
	
	//device stack
	protected String deviceId = "IMEI:4930051";
	protected SyncEngine  deviceSyncEngine;	
	protected MobileObjectDatabase deviceDatabase;
	
	protected String message = "<tag apos=''apos'' quote=\"quote\" ampersand=''&''>{0}/Message</tag>";
	protected String subject = "This is the subject<html><body>{0}</body></html>";
	
	protected SyncService syncService;
	
	protected void setUp() throws Exception 
	{
		ServiceManager.bootstrap();
		org.openmobster.cloudConnector.api.Configuration configuration = 
		org.openmobster.cloudConnector.api.Configuration.getInstance();
		configuration.setSecurityConfig((SecurityConfig)ServiceManager.locate("/cloudConnector/securityConfig"));
		this.syncService = (SyncService)ServiceManager.locate("simulator://SyncService");
		
		this.deviceSyncEngine = (SyncEngine)ServiceManager.
		locate("simulator://SyncEngine");
		
		this.deviceDatabase = (MobileObjectDatabase)
		ServiceManager.locate("mobileObject://MobileObjectDatabase");
								
		this.setUpDeviceData();
		Configuration.getInstance().setDeviceId(this.deviceId);
		Configuration.getInstance().setAuthenticationNonce(null);
		Configuration.getInstance().setAuthenticationHash("blahblah");
		
		this.setUpServerData();				
	}
	
	protected void tearDown() throws Exception
	{
		Configuration.cleanup();
		ServiceManager.shutdown();		
	}
		
	
	protected void setUpDeviceData() throws Exception
	{
		Vector changelog = new Vector();
		
		MobileObject mo = new MobileObject();
		mo.setRecordId("unique-3");
		mo.setStorageId(this.service);
		mo.setValue("from","from@gmail.com");
		mo.setValue("to","to@gmail.com");
		mo.setValue("subject", MessageFormat.format(this.subject,new Object[]{mo.getRecordId()}));
		mo.setValue("message", MessageFormat.format(this.message,new Object[]{mo.getRecordId()}));
		this.deviceDatabase.create(mo);
		org.openmobster.device.agent.sync.engine.ChangeLogEntry entry = 
		new org.openmobster.device.agent.sync.engine.ChangeLogEntry();
		entry.setNodeId(this.service);
		entry.setOperation(SyncEngine.OPERATION_ADD);
		entry.setRecordId(mo.getRecordId());
		changelog.add(entry);

		mo = new MobileObject();
		mo.setRecordId("unique-4");
		mo.setStorageId(this.service);
		mo.setValue("from", "from@gmail.com");
		mo.setValue("to", "to@gmail.com");
		mo.setValue("subject", MessageFormat.format(this.subject,new Object[]{mo.getRecordId()}));
		mo.setValue("message", MessageFormat.format(this.message,new Object[]{mo.getRecordId()}));
		this.deviceDatabase.create(mo);
		entry = 
		new org.openmobster.device.agent.sync.engine.ChangeLogEntry();
		entry.setNodeId(this.service);
		entry.setOperation(SyncEngine.OPERATION_ADD);
		entry.setRecordId(mo.getRecordId());
		changelog.add(entry);
		
		//Update the Client ChangeLog				
		this.deviceSyncEngine.addChangeLogEntries(changelog);
	}
	
	protected void setUpServerData() throws Exception
	{
		Socket socket = null;
		OutputStream os = null;
		InputStream is = null;
		try
		{					
			socket = Tools.getPlainSocket();
			
			is = socket.getInputStream();
			os = socket.getOutputStream();	
			
			String payload = 
			"<request>" +
				"<header>" +
					"<name>processor</name>"+
					"<value>testsuite</value>"+
				"</header>"+
			"</request>";
			IOUtilities.writePayLoad(payload, os);	
			
			String data = IOUtilities.readServerResponse(is);
			if(data.indexOf("status=200")!=-1)
			{
				payload = "setUp="+this.getClass().getName()+"/CleanUp\n";
				IOUtilities.writePayLoad(payload, os);		
				data = IOUtilities.readServerResponse(is);
				
				payload = "setUp="+this.getClass().getName()+"/add\n";
				IOUtilities.writePayLoad(payload, os);		
				data = IOUtilities.readServerResponse(is);
			}
		}
		finally
		{					
			if(socket != null)
			{
				socket.close();
			}
		}
	}		
}
