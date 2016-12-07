/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.security;

import java.util.Map;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.validation.ObjectValidator;
import org.openmobster.core.common.Utilities;
import org.openmobster.core.common.event.EventManager;
import org.openmobster.core.common.event.Event;
import org.openmobster.core.common.transaction.TransactionHelper;
import org.openmobster.core.security.identity.Identity;
import org.openmobster.core.security.identity.Group;
import org.openmobster.core.security.identity.GroupController;
import org.openmobster.core.security.identity.IdentityAttribute;
import org.openmobster.core.security.identity.IdentityController;
import org.openmobster.core.security.device.Device;
import org.openmobster.core.security.device.DeviceAttribute;
import org.openmobster.core.security.device.DeviceController;

/**
 * Responsible for all Identity/Device provisioning related services
 * 
 * @author openmobster@gmail.com
 *
 */
public class Provisioner 
{
	private static Logger log = Logger.getLogger(Provisioner.class);
	
	private IdentityController identityController;
	private GroupController groupController;
	private DeviceController deviceController;
	private ObjectValidator domainValidator;
	private EventManager eventManager;
	
	public Provisioner()
	{
		
	}
	
	public static Provisioner getInstance()
	{
		return (Provisioner)ServiceManager.locate("security://Provisioner");
	}
		
	public IdentityController getIdentityController() 
	{
		return identityController;
	}

	public void setIdentityController(IdentityController identityController) 
	{
		this.identityController = identityController;
	}

	public DeviceController getDeviceController() 
	{
		return deviceController;
	}

	public void setDeviceController(DeviceController deviceController) 
	{
		this.deviceController = deviceController;
	}
			
	public GroupController getGroupController() 
	{
		return groupController;
	}

	public void setGroupController(GroupController groupController) 
	{
		this.groupController = groupController;
	}

	public ObjectValidator getDomainValidator() 
	{
		return domainValidator;
	}

	public void setDomainValidator(ObjectValidator domainValidator) 
	{
		this.domainValidator = domainValidator;
	}		
	
	
	public EventManager getEventManager() 
	{
		return eventManager;
	}

	public void setEventManager(EventManager eventManager) 
	{
		this.eventManager = eventManager;
	}
	
	public void start()
	{
		boolean isStarted = TransactionHelper.startTx();
		try
		{
			if(this.groupController.read("standard") == null)
			{
				Group group = new Group();
				group.setName("standard");
				this.groupController.create(group);
				
				log.info("***************************************");
				log.info("Standard Group successfully created.....");
				log.info("****************************************");
			}
			else
			{
				log.info("***************************************");
				log.info("Standard Group already exists..........");
				log.info("****************************************");
			}
			
			if(isStarted)
			{
				TransactionHelper.commitTx();
			}
		}
		catch(Throwable t)
		{
			if(isStarted)
			{
				TransactionHelper.rollbackTx();
			}
			log.error(this, t);
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------------
	public boolean exists(String email)
	{
		return this.identityController.exists(email);
	}
	
	/**
	 * Provisions the Identity with the system
	 * 
	 * @param email unique identifier for the user
	 * @param credential used for authenticating this user
	 * @throws IDMException
	 */
	public void registerIdentity(String email, String credential) throws IDMException
	{
		try
		{
			if(credential == null || credential.trim().length() == 0)
			{
				throw new IDMException("invalid_credential", IDMException.VALIDATION_ERROR);
			}
			
			//Check to make sure this identity is not already registered
			if(this.identityController.exists(email))
			{
				throw new IDMException(IDMException.IDENITITY_ALREADY_EXISTS);
			}
			
			//Remove this code once things are stable. This is now moved into
			//the start method
			/*if(this.groupController.read("standard") == null)
			{
				synchronized(Provisioner.class)
				{
					if(this.groupController.read("standard") == null)
					{
						Group group = new Group();
						group.setName("standard");
						this.groupController.create(group);
					}
				}
			}*/
			
			//Generate a one way credential hash
			String authenticationHash = Utilities.generateOneWayHash(credential, email+credential);
			
			//Register the Identity with the system
			Identity identity = new Identity();
			identity.setPrincipal(email);
			identity.setCredential(authenticationHash);
			identity.addAttribute(new IdentityAttribute("email", email));
			identity.setInactiveCredential(credential);
			identity.activate();
			Group standardGroup = this.groupController.read("standard");
			identity.getGroups().add(standardGroup);
			
			//Validate the Identity object
			Map<String, String[]> validationErrors = this.domainValidator.validate(identity);
			if(validationErrors != null && !validationErrors.isEmpty())
			{
				//A Validation Error occured...Do not create the Identity object into the system
				IDMException idm = new IDMException(IDMException.VALIDATION_ERROR);
				idm.setValidationErrors(validationErrors);
				throw idm;
			}			
			
			//Go ahead
			this.identityController.create(identity);
		}
		catch(Exception e)
		{
			if(e instanceof IDMException)
			{
				throw (IDMException)e;
			}
			else
			{
				throw new IDMException(e, IDMException.SYSTEM_ERROR);
			}
		}
	}
	
	/**
	 * Provisions the Device with the system
	 * 
	 * @param email email of the registered Identity
	 * @param deviceIdentifier a unique hardware related identifier for the device
	 * @throws IDMException
	 */
	public void registerDevice(String email, String activationCredential, String deviceIdentifier) throws IDMException
	{
		try
		{

			Identity registeredIdentity = this.identityController.read(email);
			
			if(registeredIdentity == null)
			{
				throw new IDMException(IDMException.IDENITITY_NOT_FOUND);
			}
			
			if(!this.deviceController.exists(deviceIdentifier))
			{
				if(activationCredential == null || !activationCredential.equals(registeredIdentity.getInactiveCredential()))
				{
					//log.info("---------------------------------------------------------------");
					//log.info("SubmittedCredential: "+activationCredential);
					//log.info("StoredCredential: "+registeredIdentity.getInactiveCredential());
					//log.info("---------------------------------------------------------------");
					
					throw new IDMException(IDMException.ACTIVATION_CREDENTIAL_MISMATCH);
				}
				
				//Commented to allow multiple devices per user
				/*Device activeDevice = this.deviceController.readByIdentity(registeredIdentity.getPrincipal());
				if(activeDevice != null)
				{
					//Current user already has a device registered. An override is manually required
					//to switch the device. This is a security issue
					//throw new IDMException(IDMException.IDENTITY_ALREADY_HAS_A_DIFFERENT_DEVICE);
					
					//Delete this device...since a new device is being activated...a user can only have one active device
					//at a time...otherwise there can be security issues...This is a better way to handle than an exception
					//previos one requires an admin to go in a swap device registration...which can become a nightmare
					//considering how often users switch devices...especially in a B2C/non-corporate setup
					//maybe this behavior can be customized in a later release
					this.deviceController.delete(activeDevice);
				}*/
				
				//Prepare the Device to be registered
				Device device = new Device(deviceIdentifier, registeredIdentity);
				device.addAttribute(new DeviceAttribute("nonce", registeredIdentity.getCredential()));
				
				//Validate the Device
				Map<String, String[]> validationErrors = this.domainValidator.validate(device);
				if(validationErrors != null && !validationErrors.isEmpty())
				{
					//A Validation Error occured...Do not create the Device object into the system
					IDMException idm = new IDMException(IDMException.VALIDATION_ERROR);
					idm.setValidationErrors(validationErrors);
					throw idm;
				}
										
				//Go ahead			
				this.deviceController.create(device);
				
				//Send an announcement that new device has appeared in the system
				Event event = new Event();
				event.setAttribute("new-device", device);
				this.eventManager.fire(event);
			}
			else
			{
				/*
				 * Alteração feita na na versão 2.4-M3.1.
				 * Permite que um dispositivo possa trocar de "dono" (Identity).
				 */
				Device device = this.deviceController.read(deviceIdentifier);
				device.setIdentity(registeredIdentity);
				device.updateAttribute(new DeviceAttribute("nonce", registeredIdentity.getCredential()));
				this.deviceController.update(device);
				
				//Utiliza um mecanismo já implementado no ChannelDaemon para avisá-lo que o dispositvo teve seu dono alterado (e com isto seu email).  
				Event event = new Event();
				event.setAttribute("invalidate-device-cache", Boolean.TRUE);
				this.eventManager.fire(event);
				
				
//				//Reset the nonce, if credentials/device association checks out
//				Identity deviceIdentity = device.getIdentity();
//				if(!deviceIdentity.getPrincipal().equals(email) || 
//				   !deviceIdentity.getInactiveCredential().equals(activationCredential))
//				{
//					//Makes sure device is associated with the proper user account
//					throw new IDMException(IDMException.ACTIVATION_CREDENTIAL_MISMATCH);
//				}

			}
		}
		catch(Exception e)
		{
			if(e instanceof IDMException)
			{
				throw (IDMException)e;
			}
			else
			{
				throw new IDMException(e, IDMException.SYSTEM_ERROR);
			}
		}
	}
	
	public void activate(String principal) throws IDMException
	{
		if(principal == null || principal.trim().length()==0)
		{
			throw new IllegalStateException("Principal should not be null!!");
		}
		
		Identity identity = this.identityController.read(principal);
		if(identity == null)
		{
			throw new IDMException(IDMException.IDENITITY_NOT_FOUND);
		}
		
		identity.activate();
		this.identityController.update(identity);
	}
	
	public void deactivate(String principal) throws IDMException
	{
		if(principal == null || principal.trim().length()==0)
		{
			throw new IllegalStateException("Principal should not be null!!");
		}
		
		Identity identity = this.identityController.read(principal);
		if(identity == null)
		{
			throw new IDMException(IDMException.IDENITITY_NOT_FOUND);
		}
		
		identity.deactivate();
		this.identityController.update(identity);
	}
	
	public void resetPassword(String principal, String oldpassword, String newpassword) throws IDMException
	{
		if(principal == null || principal.trim().length()==0)
		{
			throw new IllegalStateException("Principal should not be null!!");
		}
		if(oldpassword == null || oldpassword.trim().length()==0)
		{
			throw new IllegalStateException("Old Password should not be null!!");
		}
		if(newpassword == null || newpassword.trim().length()==0)
		{
			throw new IllegalStateException("New Password should not be null!!");
		}
		
		Identity identity = this.identityController.read(principal);
		if(identity == null)
		{
			throw new IDMException(IDMException.IDENITITY_NOT_FOUND);
		}
		
		if(!oldpassword.equals(identity.getInactiveCredential()))
		{
			throw new IDMException(IDMException.ACTIVATION_CREDENTIAL_MISMATCH);
		}
		
		//Everything checks out...go ahead and modify the password
		identity.setInactiveCredential(newpassword);
		this.identityController.update(identity);
	}
	
	public void resetPassword(String deviceId,String newpassword) throws IDMException
	{
		if(deviceId == null || deviceId.trim().length()==0)
		{
			throw new IllegalStateException("Device Id should not be null!!");
		}
		if(newpassword == null || newpassword.trim().length()==0)
		{
			throw new IllegalStateException("New Password should not be null!!");
		}
		
		DeviceController deviceController = DeviceController.getInstance();
		Device device = deviceController.read(deviceId);
		
		Identity identity = device.getIdentity();
		if(identity == null)
		{
			throw new IDMException(IDMException.IDENITITY_NOT_FOUND);
		}
		
		//change the credential hash
		//Generate a one way credential hash
		String authenticationHash = Utilities.generateOneWayHash(newpassword, identity.getPrincipal()+newpassword);
		
		//Everything checks out...go ahead and modify the password
		identity.setInactiveCredential(newpassword);
		identity.setCredential(authenticationHash);
		this.identityController.update(identity);
		
		device.updateAttribute(new DeviceAttribute("nonce", authenticationHash));
		this.deviceController.update(device);
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------------
}
