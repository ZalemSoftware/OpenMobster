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
@CommandComponent(id = "openmobster:register", description = "Register a New User")
public class RegisterMobsterCommand extends ConsoleCommand
{
	@Option(name="-u", aliases={"--username"}, required=true, metaVar="USERNAME", description="Unique 'username' for a new account")
	private String username;
	
	@Option(name="-p", aliases={"--password"}, required=true, metaVar="PASSWORD", description="Secure 'password' for a new account")
	private String password;
	
	protected Object doExecute() throws Exception
	{
		Request request = new Request("/console/registerCommand");
		request.setAttribute("username", username);
		request.setAttribute("password", password);
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		if(response != null && response.getStatusCode().equals("204"))
		{
			io.out.println("*******************************************************");
			io.out.println("Username: "+this.username+" was successfully registered");
			io.out.println("*******************************************************");
			return SUCCESS;
		}
		
		io.out.println("*******************************************************");
		io.out.println("Registration Error: "+response.getAttribute("idm-error"));
		io.out.println("*******************************************************");
		return FAILURE;
	}
}
