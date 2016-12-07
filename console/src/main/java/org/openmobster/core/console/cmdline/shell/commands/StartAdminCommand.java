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
@CommandComponent(id = "openmobster:startadmin", description = "Starts Server Administration")
public class StartAdminCommand extends ConsoleCommand
{
	@Option(name="-u", aliases={"--username"}, required=true, metaVar="USERNAME", description="Administrator 'username'")
	private String username;
	
	@Option(name="-p", aliases={"--password"}, required=true, metaVar="PASSWORD", description="Administrator 'password'")
	private String password;
	
	protected Object doExecute() throws Exception
	{
		ConsoleSession session = ConsoleSession.getInstance();
		if(session.isConfigured())
		{
			Request request = new Request("/console/consoleService");
			request.setAttribute("action", "auth");
			request.setAttribute("username", username);
			request.setAttribute("password", password);
			MobileService service = new MobileService();
			Response response = service.invoke(request);
			
			if(response != null && response.getStatusCode().equals("204"))
			{
				io.out.println("*********************************************");
				io.out.println("Logged In As: "+this.username);
				io.out.println("*********************************************");
				
				ConsoleSession.getInstance().authSuccessNotification(this.username);
				
				return SUCCESS;
			}
			else
			{
				io.out.println("**********************************************************");
				io.out.println("Login Failed. Please check your credentials and try again.");
				io.out.println("Login Error: "+response.getStatusMsg());
				io.out.println("**********************************************************");
				return FAILURE;
			}
		}
		
		io.out.println("*****************************************************************");
		io.out.println("Console is not configured yet. Please use the 'configure' command");
		io.out.println("*****************************************************************");
		return FAILURE;
	}
}
