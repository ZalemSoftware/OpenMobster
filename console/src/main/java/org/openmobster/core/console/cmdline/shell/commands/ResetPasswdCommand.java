/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.cmdline.shell.commands;

import org.apache.geronimo.gshell.clp.Option;
import org.apache.geronimo.gshell.command.annotation.CommandComponent;
import org.openmobster.cloudConnector.api.service.MobileService;
import org.openmobster.cloudConnector.api.service.Request;
import org.openmobster.cloudConnector.api.service.Response;

/**
 * @author openmobster@gmail.com
 */
@CommandComponent(id = "openmobster:resetpasswd", description = "Resets a User's password")
public class ResetPasswdCommand extends ConsoleCommand
{
	@Option(name="-u", aliases={"--username"}, required=true, metaVar="USERNAME", description="Unique 'username'")
	private String username;
	
	@Option(name="-o", aliases={"--oldpasswd"}, required=true, metaVar="OLDPASSWORD", description="Old 'password' of the user")
	private String oldPass;
	
	@Option(name="-n", aliases={"--newpasswd"}, required=true, metaVar="NEWPASSWORD", description="New 'password' of the user")
	private String newPass;
	
	protected Object doExecute() throws Exception
	{
		Request request = new Request("/console/resetpasswdCommand");
		request.setAttribute("username", username);
		request.setAttribute("oldpass", oldPass);
		request.setAttribute("newpass", newPass);
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		if(response != null && response.getStatusCode().equals("204"))
		{
			io.out.println("******************************************************");
			io.out.println("Password was successfully reset");
			io.out.println("******************************************************");
			
			return SUCCESS;
		}
		
		io.out.println("*******************************************************");
		io.out.println("Activation Error: "+response.getAttribute("idm-error"));
		io.out.println("*******************************************************");
		return FAILURE;
	}
}
