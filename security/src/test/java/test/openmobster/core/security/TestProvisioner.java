/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.security;

import org.apache.log4j.Logger;
import junit.framework.TestCase;

import java.util.Set;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.security.Provisioner;
import org.openmobster.core.security.IDMException;
import org.openmobster.core.security.identity.IdentityController;
import org.openmobster.core.security.identity.GroupController;
import org.openmobster.core.security.identity.Identity;
import org.openmobster.core.security.identity.Group;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceController;

/**
 * @author openmobster@gmail.com
 *
 */
public class TestProvisioner extends TestCase 
{
	private static Logger log = Logger.getLogger(TestProvisioner.class);
	
	private Provisioner provisioner;
	private IdentityController identityController;
	private GroupController groupController;
	private DeviceController deviceController;
	
	public void setUp()
	{
		ServiceManager.bootstrap();		
		
		this.provisioner = (Provisioner)ServiceManager.locate("security://Provisioner");
		this.identityController = (IdentityController)ServiceManager.locate("security://IdentityController");
		this.groupController = (GroupController)ServiceManager.locate("security://GroupController");
		this.deviceController = (DeviceController)ServiceManager.locate("security://DeviceController");				
	}
	
	public void tearDown()
	{
		ServiceManager.shutdown();	
	}
	//------------------------------------------------------------------------------------------------------------------------------------------------
	public void testDeviceActivation()
	{
		//Register the Identity
		this.provisioner.registerIdentity("blah@gmail.com", "blahblah");
		
		Identity identity = this.identityController.read("blah@gmail.com");
		assertNotNull("Identity Must be Created Successfully!!", identity);
		
		log.info("------------------------------------------------------");
		log.info("Principal="+identity.getPrincipal());
		log.info("Credential="+identity.getCredential());
		log.info("Email="+identity.readAttribute("email").getValue());
		log.info("Unhashed Credential="+identity.getInactiveCredential());
		log.info("------------------------------------------------------");
		
		//Activate the Device
		this.activateDevice("blah@gmail.com", "blahblah", "IMEI:567890");
	}	
	
	public void testReActivation()
	{
		this.testDeviceActivation();
		
		//Reactivate the device with the original credential
		this.activateDevice("blah@gmail.com", "blahblah", "IMEI:567890");
	}
	
	public void testReActivationWithDifferentIMEI()
	{
		this.testDeviceActivation();
		
		Device activeDevice = this.deviceController.read("IMEI:567890");
		assertNotNull("Device Must be registered and active",activeDevice);
		
		//Reactivate with same username and credential but a different device id
		this.provisioner.registerDevice("blah@gmail.com", "blahblah", "IMEI:4930051");
		
		Set<Device> activeDevices = this.deviceController.readByIdentity("blah@gmail.com");
		assertTrue(activeDevices != null && !activeDevices.isEmpty());
		for(Device device:activeDevices)
		{
			String id = device.getIdentifier();
			assertTrue(id.equals("IMEI:567890") || id.equals("IMEI:4930051"));	
		}
	}
	//------------------------------------------------------------------------------------------------------------------------------
	private void activateDevice(String email, String credential, String deviceId)
	{
		this.provisioner.registerDevice(email, credential, deviceId);
		Device device = this.deviceController.read(deviceId);
		assertNotNull("Device Must be Created Successfully!!", device);
		assertNotNull("Inactive Credential must be removed!!", device.getIdentity().getInactiveCredential());
		assertEquals("InactiveCredential must match", device.getIdentity().getInactiveCredential(), credential);
	}
}
