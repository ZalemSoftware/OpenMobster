/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.cmdline.shell.commands;

import org.apache.geronimo.gshell.command.annotation.CommandComponent;

/**
 * 
 * @author openmobster@gmail.com
 */
@CommandComponent(id = "openmobster:logout", description = "Logout")
public class LogoutCommand extends ConsoleCommand
{
	protected Object doExecute() throws Exception
	{
		ConsoleSession.getInstance().logout();
		
		return SUCCESS;
	}
}
