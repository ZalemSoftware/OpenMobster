/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.cmdline.shell.commands;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import org.apache.geronimo.gshell.command.Command;

import org.openmobster.core.console.cmdline.shell.ConsoleShell;

/**
 * @author openmobster@gmail.com
 */
public class CommandRepository
{
	private static Logger log = Logger.getLogger(CommandRepository.class);
	
	private ConsoleShell consoleShell;
	
	public CommandRepository()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}

	public ConsoleShell getConsoleShell()
	{
		return consoleShell;
	}

	public void setConsoleShell(ConsoleShell consoleShell)
	{
		this.consoleShell = consoleShell;
	}
	//------------------------------------------------------------------------------------------------------------
	public void notifyCommand(Command consoleCommand)
	{
		try
		{
			this.consoleShell.getCommandRegistry().register(consoleCommand);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
