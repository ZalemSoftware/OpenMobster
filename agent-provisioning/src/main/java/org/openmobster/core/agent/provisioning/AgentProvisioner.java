/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.agent.provisioning;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;
import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.CustomConfig;
import org.openmobster.core.common.errors.ErrorHandler;
import org.openmobster.core.common.errors.SystemException;
import org.openmobster.core.dataService.server.Server;
import org.openmobster.core.security.Provisioner;
import org.openmobster.core.security.IDMException;
import org.openmobster.core.security.identity.Identity;
import org.openmobster.core.security.identity.IdentityController;
import org.openmobster.security.plugin.AuthPlugin;
import org.openmobster.security.plugin.PluginContext;
import org.openmobster.security.plugin.PluginException;
import org.openmobster.security.plugin.PluginManager;

/**
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="provisioning")
public class AgentProvisioner implements MobileServiceBean
{
	private static Logger log = Logger.getLogger(AgentProvisioner.class);
	
	private CustomConfig customConfig;
	private Provisioner provisioner;
	private IdentityController identityController;
	
	public AgentProvisioner()
	{
		
	}
		
	public CustomConfig getCustomConfig() 
	{
		return customConfig;
	}

	public void setCustomConfig(CustomConfig customConfig) 
	{
		this.customConfig = customConfig;
	}
	
	public Provisioner getProvisioner() 
	{
		return provisioner;
	}

	public void setProvisioner(Provisioner provisioner) 
	{
		this.provisioner = provisioner;
	}
	
	public IdentityController getIdentityController() 
	{
		return identityController;
	}

	public void setIdentityController(IdentityController identityController) 
	{
		this.identityController = identityController;
	}

	public void start()
	{
		log.info("-----------------------------------------------------------");
		log.info("Agent Provisioning Service successfully started............");
		log.info("-----------------------------------------------------------");				
	}
	
	public Response invoke(Request request) 
	{
		Response response = new Response();
		try
		{	
			String serverMetaData = request.getAttribute("action");
			
			if(serverMetaData == null)
			{
				//Parse data from the request
				String email = request.getAttribute("email");
				String password = request.getAttribute("password");
				String deviceIdentifier = request.getAttribute("identifier");
				
				//Validate data from the request
				if(email == null || password == null || deviceIdentifier == null)
				{
					throw new IDMException("invalid_input", 
					IDMException.VALIDATION_ERROR);
				}	
				
				//Authenticate with the 3rd party system and make sure everything is on track
				AuthPlugin authPlugin = PluginManager.getInstance().authPlugin();
				if(authPlugin != null)
				{
					PluginContext pluginContext = new PluginContext();
					pluginContext.setPrincipal(email);
					pluginContext.setDeviceId(deviceIdentifier);
					pluginContext.setPassword(password);
					try
					{
						authPlugin.activateDevice(pluginContext);
					}
					catch(PluginException pe)
					{
						//Device Activation failed with the 3rd party system
						throw pe;
					}
				}
				
				//Make sure the identity is registered
				if(!this.provisioner.exists(email))
				{
					this.provisioner.registerIdentity(email, password);
				}
				
				//Register/Activate the device
				this.provisioner.registerDevice(email, password, deviceIdentifier);
								
				//Prepare response
				Identity identity = this.identityController.read(email);
				this.prepareResponse(identity, response);
						
				return response;
			}
			else
			{
				this.prepareResponse(response);
				return response;
			}
		}
		catch(IDMException idmError)
		{
			response.setAttribute("idm-error", idmError.getMessage());
			response.setAttribute("idm-error-type", ""+idmError.getType());			
			return response;
		}
		catch(PluginException pe)
		{
			response.setAttribute("idm-error", "plugin_activation_error");	
			return response;
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(e);
			throw new SystemException(e.getMessage(), e);
		}
	}
	//----------------------------------------------------------------------------------------------------------
	private void prepareResponse(Identity identity, Response response)
	{
		this.prepareResponse(response);
		
		//Get AuthenticationHash for the device
		response.setAttribute("authenticationHash", identity.getCredential());
	}
	
	private void prepareResponse(Response response)
	{
		Server plainServer = (Server)ServiceManager.locate("dataService://PlainServer");
		Server secureServer = (Server)ServiceManager.locate("dataService://Server");		
						
		//Get Server Id value		
		response.setAttribute("serverId", this.customConfig.getServerName());
		
		//Get Server Ip value
		response.setAttribute("serverIp", this.customConfig.getServerIp());
		
		String httpPort = this.customConfig.getHttpPort();
		if(httpPort != null && httpPort.trim().length()>0)
		{
			response.setAttribute("httpPort", httpPort);
		}
		
		//Get Plain Server Port
		response.setAttribute("plainServerPort", ""+plainServer.getPort());
		
		//Get Secure Server Port
		if(secureServer.isSecure())
		{
			response.setAttribute("secureServerPort", ""+secureServer.getPort());
			response.setAttribute("isSSLActive", "true");
		}
		else
		{
			response.setAttribute("isSSLActive", "false");
		}
						
		//Get MaxPacket Size....Just leave it at this...with broadband connections...this issue
		//should be completely resolved
		response.setAttribute("maxPacketSize", "0");
	}
}
