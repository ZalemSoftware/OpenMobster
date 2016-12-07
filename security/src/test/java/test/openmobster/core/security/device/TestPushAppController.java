/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.openmobster.core.security.device;

import java.util.Set;
import java.util.List;
import java.util.HashSet;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.security.device.PushAppController;
import org.openmobster.core.security.device.PushApp;

/**
 * 
 * 
 * @author openmobster@gmail.com
 */
public class TestPushAppController extends TestCase
{
	private static Logger log = Logger.getLogger(TestPushAppController.class);
	
	private PushAppController pushAppController;
	
	protected void setUp() throws Exception
	{
		ServiceManager.bootstrap();
		this.pushAppController = (PushAppController)ServiceManager.locate("security://PushAppController");
	}

	protected void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	
	public void testCreate() throws Exception
	{
		PushApp pushApp1 = new PushApp();
		
		pushApp1.setAppId("mock-app-id");
		String oid = this.pushAppController.create(pushApp1);
		
		log.info("**********************************************");
		log.info("OID: "+oid);
		assertNotNull(oid);
		log.info("**********************************************");
		
		PushApp app2 = new PushApp();
		app2.setAppId("mock-app-id-2");
		app2.setCertificate("cert".getBytes());
		app2.setCertificatePassword("cert-password");
		for(int i=0; i<5; i++)
		{
			app2.addChannel("push_channel_"+i);
		}
		oid = this.pushAppController.create(app2);
		
		log.info("**********************************************");
		log.info("OID: "+oid);
		assertNotNull(oid);
		log.info("**********************************************");
	}
	
	public void testRead() throws Exception
	{
		PushApp app = new PushApp();
		app.setAppId("mock-app-id");
		app.setCertificate("cert".getBytes());
		app.setCertificatePassword("cert-password");
		for(int i=0; i<5; i++)
		{
			app.addChannel("push_channel_"+i);
		}
		String oid = this.pushAppController.create(app);
		
		log.info("**********************************************");
		log.info("OID: "+oid);
		assertNotNull(oid);
		
		
		app = this.pushAppController.readPushApp("mock-app-id");
		
		//certificate
		byte[] certificate = app.getCertificate();
		assertNotNull(certificate);
		String certificateStr = new String(certificate);
		assertEquals(certificateStr,"cert");
		log.info("Certificate: "+certificateStr);
		
		//Certificate Password
		String certPassword = app.getCertificatePassword();
		assertEquals(certPassword,"cert-password");
		log.info("Cert-Password: "+certPassword);
		
		//Channels
		Set<String> channels = app.getChannels();
		assertNotNull(channels);
		for(String channel:channels)
		{
			log.info("Channel: "+channel);
		}
		
		log.info("**********************************************");
	}
	
	public void testReadAll() throws Exception
	{
		PushApp pushApp1 = new PushApp();
		
		pushApp1.setAppId("mock-app-id");
		String oid = this.pushAppController.create(pushApp1);
		
		log.info("**********************************************");
		log.info("OID: "+oid);
		assertNotNull(oid);
		log.info("**********************************************");
		
		PushApp app2 = new PushApp();
		app2.setAppId("mock-app-id-2");
		app2.setCertificate("cert".getBytes());
		app2.setCertificatePassword("cert-password");
		for(int i=0; i<5; i++)
		{
			app2.addChannel("push_channel_"+i);
		}
		oid = this.pushAppController.create(app2);
		
		log.info("**********************************************");
		log.info("OID: "+oid);
		assertNotNull(oid);
		log.info("**********************************************");
		
		//Read All
		List<PushApp> apps = this.pushAppController.readAll();
		assertNotNull(apps);
		assertEquals(apps.size(),2);
		PushApp app1 = apps.get(0);
		assertTrue(app1.getAppId().equals("mock-app-id") || app1.getAppId().equals("mock-app-id-2"));
		app2 = apps.get(1);
		assertTrue(app2.getAppId().equals("mock-app-id") || app2.getAppId().equals("mock-app-id-2"));
	}
	
	public void testUpdate() throws Exception
	{
		PushApp pushApp = new PushApp();
		
		pushApp.setAppId("mock-app-id");
		String oid = this.pushAppController.create(pushApp);
		log.info("**********************************************");
		log.info("OID: "+oid);
		assertNotNull(oid);
		log.info("**********************************************");
		
		//Add a certificate
		pushApp.setCertificate("certificate".getBytes());
		
		//Add a certificate password
		pushApp.setCertificatePassword("password");
		
		//Add some channels
		pushApp.addChannel("channel1");
		pushApp.addChannel("channel2");
		
		this.pushAppController.update(pushApp);
		
		pushApp = this.pushAppController.readPushApp("mock-app-id");
		
		String certificate = new String(pushApp.getCertificate());
		assertEquals(certificate,"certificate");
		
		String certificatePassword = pushApp.getCertificatePassword();
		assertEquals(certificatePassword,"password");
		
		Set<String> channels = pushApp.getChannels();
		assertEquals(channels.size(),2);
		
		//Update channels
		Set<String> newChannels = new HashSet<String>();
		newChannels.add("newchannel");
		pushApp.setChannels(newChannels);
		this.pushAppController.update(pushApp);
		
		pushApp = this.pushAppController.readPushApp("mock-app-id");
		channels = pushApp.getChannels();
		assertEquals(pushApp.getChannels().size(),1);
	}
	
	public void testDelete() throws Exception
	{
		PushApp pushApp = new PushApp();
		
		pushApp.setAppId("mock-app-id");
		String oid = this.pushAppController.create(pushApp);
		log.info("**********************************************");
		log.info("OID: "+oid);
		assertNotNull(oid);
		log.info("**********************************************");
		
		//delete it
		this.pushAppController.delete(pushApp);
		
		pushApp = this.pushAppController.readPushApp("mock-app-id");
		this.assertNull(pushApp);
	}
}
