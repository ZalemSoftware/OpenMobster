/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.server;

import java.util.List;
import java.util.ArrayList;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;
import org.openmobster.core.security.IDMException;
import org.openmobster.core.security.Provisioner;
import org.openmobster.core.security.identity.Identity;

/**
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/console/showUsersCommand")
public class ShowUsersCommand implements MobileServiceBean
{
	private Provisioner provisioner;
	
	public ShowUsersCommand()
	{
		
	}
	
	public Provisioner getProvisioner()
	{
		return provisioner;
	}


	public void setProvisioner(Provisioner provisioner)
	{
		this.provisioner = provisioner;
	}
	
	public Response invoke(Request request)
	{
		try
		{
			Response response = new Response();
			
			List<Identity> identities = this.provisioner.getIdentityController().readAll();
			if(identities != null && !identities.isEmpty())
			{
				List<String> users = new ArrayList<String>();
				for(Identity identity:identities)
				{
					users.add(identity.getPrincipal());
				}
				response.setListAttribute("users", users);
			}
			
			return response;
		}
		catch(IDMException idmError)
		{
			Response response = new Response();
			response.setAttribute("idm-error", idmError.getMessage());
			response.setAttribute("idm-error-type", ""+idmError.getType());			
			return response;
		}
	}
}
