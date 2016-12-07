/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.security.identity;

import org.apache.log4j.Logger;

import java.util.Set;
import java.util.HashSet;

import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.core.security.identity.IdentityController;
import org.openmobster.core.security.identity.Identity;
import org.openmobster.core.security.identity.IdentityAttribute;
import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.Device;


/**
 * @author openmobster@gmail.com
 */
public class TestMultiDeviceUsecases extends TestCase
{
	private static Logger log = Logger.getLogger(TestMultiDeviceUsecases.class);
	
	private IdentityController identityController;
	private DeviceController deviceController;
	
	/**
	 * 
	 */
	public void setUp()
	{
		ServiceManager.bootstrap();
		this.identityController = (IdentityController)ServiceManager.locate("security://IdentityController");
		this.deviceController = (DeviceController)ServiceManager.locate("security://DeviceController");
	}
	
	/**
	 * 
	 */
	public void tearDown()
	{
		ServiceManager.shutdown();
		this.identityController = null;
		this.deviceController = null;
	}
	
	
	public void testCreate() throws Exception
	{
		//Assert
		assertNotNull(this.identityController);
		Identity stored = this.identityController.read("admin");
		assertNull(stored);
		
		Identity identity = new Identity();
		identity.setPrincipal("admin");
		identity.setCredential("adminPassword");
		
		//Add Identity Attributes
		Set<IdentityAttribute> attributes = new HashSet<IdentityAttribute>();
		attributes.add(new IdentityAttribute("email", "blah@gmail.com"));
		identity.setAttributes(attributes);
		
		//Add this identity to the database
		this.identityController.create(identity);
		
		//Assert the state of the database
		stored = this.identityController.read("admin");
		assertNotNull(stored);
		
		assertEquals("Principal does not match", "admin", stored.getPrincipal());
		assertEquals("Credential does not match", "adminPassword", stored.getCredential());
		assertTrue("Improper ID assigned", stored.getId()>0);
		
		assertNotNull("Identity Attributes Not Found", stored.getAttributes());
		IdentityAttribute storedAttribute = stored.getAttributes().iterator().next();
		assertEquals("Email Attribute Not Found", "email", storedAttribute.getName());
		assertEquals("Email Value does not match", "blah@gmail.com", storedAttribute.getValue());
		assertTrue("Improper ID assigned", storedAttribute.getId()>0);
	}
	
	public void testDeviceLifecycle() throws Exception
	{
		Identity identity = new Identity();
		identity.setPrincipal("admin");
		identity.setCredential("adminPassword");
		this.identityController.create(identity);
		
		Device device = new Device("imei:8675309",identity);
		this.deviceController.create(device);
		
		//An identity must be created
		Identity storedIdentity = this.identityController.read("admin");
		assertNotNull(storedIdentity);
		assertEquals("admin", storedIdentity.getPrincipal());
		assertEquals("adminPassword", storedIdentity.getCredential());
		
		//A device must be created
		Device storedDevice = this.deviceController.read("imei:8675309");
		assertNotNull(storedDevice);
		assertEquals("imei:8675309",storedDevice.getIdentifier());
		
		//Delete the device
		this.deviceController.delete(storedDevice);
		
		storedDevice = this.deviceController.read("imei:8675309");
		assertNull(storedDevice);
		
		storedIdentity = this.identityController.read("admin");
		assertNotNull(storedIdentity);
		assertEquals("admin", storedIdentity.getPrincipal());
		assertEquals("adminPassword", storedIdentity.getCredential());
	}
}
