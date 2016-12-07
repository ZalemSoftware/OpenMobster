/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.cmdline.shell;

import org.apache.log4j.Logger;

import jline.Terminal;
import org.apache.geronimo.gshell.command.IO;
import org.apache.geronimo.gshell.shell.Environment;
import org.apache.geronimo.gshell.branding.Branding;
import org.apache.geronimo.gshell.registry.CommandRegistry;
import org.apache.geronimo.gshell.layout.LayoutManager;
import org.apache.geronimo.gshell.DefaultEnvironment;
import org.apache.geronimo.gshell.DefaultVariables;
import org.apache.geronimo.gshell.DefaultShell;
import org.apache.geronimo.gshell.DefaultCommandExecutor;
import org.apache.geronimo.gshell.DefaultShellInfo;
import org.apache.geronimo.gshell.layout.DefaultLayoutManager;
import org.apache.geronimo.gshell.layout.loader.XMLLayoutLoader;

import org.openmobster.core.console.cmdline.ServiceManager;


/**
 * @author openmobster@gmail.com
 */
public class ConsoleShell 
{
	private static Logger log = Logger.getLogger(ConsoleShell.class);
	
	private Branding consoleBranding;
	private Environment environment;
	private CommandRegistry commandRegistry;
	private LayoutManager layoutManager;
	private DefaultShell shell;
	
	public ConsoleShell()
	{
		this.environment = new DefaultEnvironment(new IO(), new DefaultVariables());
	}
	
	public static ConsoleShell getInstance()
	{
		return (ConsoleShell)ServiceManager.locate("console://ConsoleShell");
	}
	
	public void start()
	{
		try
		{	
			//Shell Information
			DefaultShellInfo shellInfo = new DefaultShellInfo(this.consoleBranding);
			shellInfo.initialize();
			
			//Command Executor
			XMLLayoutLoader loader = new XMLLayoutLoader(shellInfo);
			loader.initialize();
			this.layoutManager = new DefaultLayoutManager(loader, 
			this.environment //environment
			);
			((DefaultLayoutManager)this.layoutManager).initialize();
		
			
			//Setup the CommandLineBuilder
			ConsoleCommandLineBuilder commandLineBuilder = new ConsoleCommandLineBuilder();
			DefaultCommandExecutor commandExe = new DefaultCommandExecutor(
					this.layoutManager, //layout manager
					commandRegistry, //commandRegistry
					commandLineBuilder, //commandLineBuilder
					this.environment //environment
			);
			commandLineBuilder.setExecutor(commandExe);
			commandLineBuilder.setEnvironment(this.environment);
			
			
			this.shell = new DefaultShell(
			shellInfo, 
			this.consoleBranding, 
			commandExe, 
			Terminal.getTerminal(), 
			this.environment, //environment
			this.environment.getIO());
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new RuntimeException(e);
		}
	}
	
	public void stop()
	{
		
	}
	//--------------------------------------------------------------------------------------------------------------
	public Branding getConsoleBranding()
	{
		return consoleBranding;
	}

	public void setConsoleBranding(Branding consoleBranding)
	{
		this.consoleBranding = consoleBranding;
	}

	public Environment getEnvironment()
	{
		return environment;
	}

	public CommandRegistry getCommandRegistry()
	{
		return commandRegistry;
	}

	public void setCommandRegistry(CommandRegistry commandRegistry)
	{
		this.commandRegistry = commandRegistry;
	}

	public LayoutManager getLayoutManager()
	{
		return layoutManager;
	}
	
	public DefaultShell getShell()
	{
		return this.shell;
	}
}
