/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.cmdline.shell.commands;

import org.apache.geronimo.gshell.clp.CommandLineProcessor;
import org.apache.geronimo.gshell.clp.Option;
import org.apache.geronimo.gshell.clp.Printer;
import org.apache.geronimo.gshell.command.annotation.CommandComponent;
import org.apache.geronimo.gshell.common.Arguments;
import org.apache.geronimo.gshell.command.IO;
import org.apache.geronimo.gshell.command.Variables;
import org.apache.geronimo.gshell.command.Command;
import org.apache.geronimo.gshell.command.CommandContext;

/**
 * 
 * @author openmobster@gmail.com
 */
public abstract class ConsoleCommand implements Command
{
	protected CommandContext context;

	protected IO io;

	protected Variables variables;
	
	private boolean isProtected; //specifies if command is available in anonymous 'mode'

	@Option(name = "-h", aliases = { "--help" }, description = "Display this help message", requireOverride = true)
	private boolean displayHelp;

	@Deprecated
	public String getId()
	{
		CommandComponent cmd = getClass().getAnnotation(CommandComponent.class);
		if (cmd == null)
		{
			throw new IllegalStateException("Command id not found");
		}
		return cmd.id();
	}

	@Deprecated
	public String getDescription()
	{
		CommandComponent cmd = getClass().getAnnotation(CommandComponent.class);
		if (cmd == null)
		{
			throw new IllegalStateException("Command description not found");
		}
		return cmd.description();
	}

	public void init(final CommandContext context)
	{
		assert context != null;

		this.context = context;
		this.io = context.getIO();
		this.variables = context.getVariables();

		// Re-setup logging using our id
		String id = getId();
	}

	public Object execute(final CommandContext context, final Object... args)
			throws Exception
	{
		assert context != null;
		assert args != null;
		
		if(!this.isAccessible())
		{
			throw new Exception("Access Denied!!");
		}

		init(context);

		CommandLineProcessor clp = new CommandLineProcessor(this);
		clp.process(Arguments.toStringArray(args));

		// Handle --help/-h automatically for the command
		if (displayHelp)
		{
			//
			// TODO: Make a special PrinterHandler to abstract this muck from
			// having to process it by hand
			//
			displayHelp(context, clp);
			
			//reset the help status
			this.displayHelp = false;

			return SUCCESS;
		}

		return doExecute();
	}

	protected abstract Object doExecute() throws Exception;

	protected void displayHelp(final CommandContext context,
			final CommandLineProcessor clp)
	{
		assert context != null;
		assert clp != null;

		// Use the alias if we have one, else use the command name
		String name = context.getInfo().getAlias();
		if (name == null)
		{
			name = context.getInfo().getName();
		}

		io.out.println(name);
		io.out.println(" -- ");
		io.out.println();

		Printer printer = new Printer(clp);
		printer.printUsage(io.out);
		io.out.println();
	}

	public boolean isProtected()
	{
		return isProtected;
	}

	public void setProtected(boolean isProtected)
	{
		this.isProtected = isProtected;
	}
	
	public boolean isAccessible()
	{
		//If its not protected....no foul
		if(!this.isProtected)
		{
			return true;
		}
		
		return !ConsoleSession.getInstance().isAnonymousMode();
	}
}
