/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.cmdline.shell.commands;

import java.util.Vector;

import org.apache.geronimo.gshell.clp.Argument;
import org.apache.geronimo.gshell.clp.CommandLineProcessor;
import org.apache.geronimo.gshell.command.CommandContext;
import org.apache.geronimo.gshell.command.annotation.CommandComponent;
import org.openmobster.cloudConnector.api.service.MobileService;
import org.openmobster.cloudConnector.api.service.Request;
import org.openmobster.cloudConnector.api.service.Response;

/**
 * @author openmobster@gmail.com
 */
@CommandComponent(id = "openmobster:show", description = "Shows a list of supported entities")
public class ShowCommand extends ConsoleCommand
{
	@Argument(index=0, metaVar="ENTITY", description="The name of the entity that should be listed", required=true)
	private String entity;
	
	protected Object doExecute() throws Exception
	{
		if(entity.equalsIgnoreCase("users"))
		{
			return this.showUsers();
		}
		else
		{
			io.out.println("Usage Error: Entity ("+this.entity+") is not supported");
			return FAILURE;
		}
	}
	
	protected void displayHelp(final CommandContext context,
			final CommandLineProcessor clp)
	{
		super.displayHelp(context, clp);
		io.out.println("Supported Entities: 'users'");
	}
	
	private Object showUsers()
	{
		Request request = new Request("/console/showUsersCommand");
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		if(response != null && response.getStatusCode().equals("200"))
		{
			io.out.println("******************USERS***************************");
			Vector users = response.getListAttribute("users");
			if(users != null && !users.isEmpty())
			{
				for(Object user: users)
				{
					io.out.println(user);
					io.out.println("-----------------------------------------------");
				}
			}
			else
			{
				io.out.println("There are no registered users");
			}
			io.out.println("**************************************************");
			
			return SUCCESS;
		}
		
		io.out.println("*******************************************************");
		io.out.println("Error: "+response.getStatusMsg());
		io.out.println("*******************************************************");
		return FAILURE;
	}
}
