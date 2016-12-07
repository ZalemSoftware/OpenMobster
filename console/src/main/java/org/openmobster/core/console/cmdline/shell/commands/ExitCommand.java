/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.cmdline.shell.commands;

import org.apache.geronimo.gshell.ExitNotification;
import org.apache.geronimo.gshell.clp.Argument;
import org.apache.geronimo.gshell.command.annotation.CommandComponent;

/**
 * 
 * @author openmobster@gmail.com
 */
@CommandComponent(id = "openmobster:exit", description = "Exit the shell")
public class ExitCommand extends ConsoleCommand
{
	@Argument(description = "System exit code")
	private int exitCode = 0;

	protected Object doExecute() throws Exception
	{

		//
		// DO NOT Call System.exit() !!!
		//

		throw new ExitNotification(exitCode);
	}
}
