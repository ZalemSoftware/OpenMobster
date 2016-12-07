/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.security.device;

import org.apache.log4j.Logger;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.core.security.device.DeviceController;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceAttribute;
import org.openmobster.core.security.identity.Identity;
import org.openmobster.core.security.identity.IdentityController;

/**
 * @author openmobster@gmail.com
 */
public class TestDeviceCache extends TestCase
{
	private static Logger log = Logger.getLogger(TestDeviceCache.class);
	
	private DeviceController deviceController = null;
	private IdentityController identityController = null;
	
	/**
	 * 
	 */
	public void setUp()
	{
		ServiceManager.bootstrap();
		this.deviceController = (DeviceController)ServiceManager.locate("security://test-suite/DeviceController");
		
		//Create the associated Identity
		this.identityController = (IdentityController)ServiceManager.locate("security://test-suite/IdentityController");
		this.identityController.create(new Identity("blah@gmail.com",""));
	}
	
	/**
	 * 
	 */
	public void tearDown()
	{
		ServiceManager.shutdown();
		this.deviceController = null;
	}
	
	
	public void testCreate() throws Exception
	{
		String imei = "IMEI:4930051";
		
		//Assert
		assertNotNull(this.deviceController);
		Device stored = this.deviceController.read(imei);
		assertNull(stored);
		
		Device device = new Device(imei, this.identityController.read("blah@gmail.com"));	
		
		//Add Device Attributes
		Set<DeviceAttribute> attributes = new HashSet<DeviceAttribute>();
		attributes.add(new DeviceAttribute("email", "blah@gmail.com"));
		device.setAttributes(attributes);
		 
		//Add this Device to the database
		this.deviceController.create(device);
		
		//Assert the state of the database
		for(int i=0; i<10; i++)
		{
			stored = this.deviceController.read(imei);
			assertNotNull(stored);
		}
		
		/*assertEquals("IMEI does not match", imei, stored.getIdentifier());		
		
		assertNotNull("Device Attributes Not Found", stored.getAttributes());
		DeviceAttribute storedAttribute = stored.getAttributes().iterator().next();
		assertEquals("Email Attribute Not Found", "email", storedAttribute.getName());
		assertEquals("Email Value does not match", "blah@gmail.com", storedAttribute.getValue());
		assertTrue("Improper ID assigned", storedAttribute.getId()>0);
		
		assertNotNull("Identity not associated", stored.getIdentity());
		assertEquals("Proper Identity Not Associated", stored.getIdentity().getPrincipal(), "blah@gmail.com");*/
	}
}
