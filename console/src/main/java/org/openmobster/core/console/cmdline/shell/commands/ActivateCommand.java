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
@CommandComponent(id = "openmobster:activate", description = "Activate a user account. Makes the account accessible again if it were deactivated")
public class ActivateCommand extends ConsoleCommand
{
	@Option(name="-u", aliases={"--username"}, required=true, metaVar="USERNAME", description="Unique 'username'")
	private String username;
	
	protected Object doExecute() throws Exception
	{
		Request request = new Request("/console/activateCommand");
		request.setAttribute("username", username);
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		if(response != null && response.getStatusCode().equals("204"))
		{
			io.out.println("******************************************************");
			io.out.println("Username: "+this.username+" was successfully activated");
			io.out.println("******************************************************");
			
			return SUCCESS;
		}
		
		io.out.println("*******************************************************");
		io.out.println("Activation Error: "+response.getAttribute("idm-error"));
		io.out.println("*******************************************************");
		return FAILURE;
	}
}
