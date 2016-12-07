/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.server;

import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;
import org.openmobster.core.security.IDMException;
import org.openmobster.core.security.Provisioner;

/**
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="/console/resetpasswdCommand")
public class ResetPasswordCommand implements MobileServiceBean
{
	private Provisioner provisioner;
	
	public ResetPasswordCommand()
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
			String username = request.getAttribute("username");
			String oldpass = request.getAttribute("oldpass");
			String newpass = request.getAttribute("newpass");
			
			this.provisioner.resetPassword(username, oldpass, newpass);
			
			return null;
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
