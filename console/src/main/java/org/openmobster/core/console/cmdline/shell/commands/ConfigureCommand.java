/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.cmdline.shell.commands;

import java.text.MessageFormat;

import org.apache.geronimo.gshell.clp.Option;
import org.apache.geronimo.gshell.command.annotation.CommandComponent;

import org.openmobster.cloudConnector.api.service.MobileService;
import org.openmobster.cloudConnector.api.service.Request;
import org.openmobster.cloudConnector.api.service.Response;

/**
 * @author openmobster@gmail.com
 */
@CommandComponent(id = "openmobster:configure", description = "Configures the Console for use with the Cloud Server")
public class ConfigureCommand extends ConsoleCommand
{
	@Option(name="-a", aliases={"--address"}, required=true, metaVar="ADDRESS", description="Cloud Server 'address'")
	private String address;
	
	@Option(name="-po", aliases={"--port"}, required=true, metaVar="PORT", description="Cloud Server 'port'")
	private String port;
	
	@Option(name="-u", aliases={"--user"}, required=true, metaVar="USER", description="Console 'root' user")
	private String user;
	
	@Option(name="-p", aliases={"--password"}, required=true, metaVar="PASSWORD", description="Console 'root' password")
	private String password;
	
	protected Object doExecute() throws Exception
	{
		ConsoleSession session = ConsoleSession.getInstance();
		session.startConfigure(address, port);
		
		Request request = new Request("/console/consoleService");
		request.setAttribute("action", "configure");
		request.setAttribute("username", user);
		request.setAttribute("password", password);
		MobileService service = new MobileService();
		Response response = service.invoke(request);
		
		if(response != null && response.getStatusCode().equals("204"))
		{
			session.finishConfigure();
			
			String status =
			"****************Connected to OpenMobster Cloud Server*****************************\n"+
			"Cloud Server: {0}\n"+
			"Cloud Server Port: {1}\n"+
			"**********************************************************************************\n";
			
			io.out.println(MessageFormat.format(status,session.getAttribute("host"),session.getAttribute("port")));
			
			return SUCCESS;
		}
		
		io.out.println("*****************************************************************");
		io.out.println("Configuration Error: "+response.getStatusMsg()+" Please try again");
		io.out.println("*****************************************************************");
		return FAILURE;	
	}
}
