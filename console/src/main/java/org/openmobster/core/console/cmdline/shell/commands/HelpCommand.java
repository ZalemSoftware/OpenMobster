/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.cmdline.shell.commands;

import org.apache.geronimo.gshell.ansi.Code;
import org.apache.geronimo.gshell.ansi.Renderer;
import org.apache.geronimo.gshell.clp.Argument;
import org.apache.geronimo.gshell.command.Command;
import org.apache.geronimo.gshell.command.annotation.CommandComponent;
import org.apache.geronimo.gshell.layout.model.AliasNode;
import org.apache.geronimo.gshell.layout.model.CommandNode;
import org.apache.geronimo.gshell.layout.model.GroupNode;
import org.apache.geronimo.gshell.layout.model.Node;
import org.apache.geronimo.gshell.registry.NotRegisteredException;
import org.codehaus.plexus.util.StringUtils;

import org.openmobster.core.console.cmdline.shell.ConsoleShell;

/**
 * 
 * @author openmobster@gmail.com
 */
@CommandComponent(id = "openmobster:help", description = "Show command help")
public class HelpCommand extends ConsoleCommand
{
	@Argument(metaVar = "COMMAND", description = "Display help for COMMAND")
	private String command;

	private Renderer renderer = new Renderer();

	public HelpCommand()
	{
	}

	protected Object doExecute() throws Exception
	{
		try
		{
			io.out.println();
	
			if (command == null)
			{
				displayAvailableCommands();
			} else
			{
				displayCommandHelp(command);
			}
	
			return SUCCESS;
		}
		finally
		{
			//cleanup
			this.command = null;
		}
	}

	private void displayAvailableCommands() throws Exception
	{
		io.out.print(ConsoleShell.getInstance().getConsoleBranding().getAbout());
		io.out.println();
		io.out.println("Available commands:");

		GroupNode group = ConsoleShell.getInstance().getLayoutManager().getLayout();

		displayGroupCommands(group);
	}

	private void displayGroupCommands(final GroupNode group) throws Exception
	{
		int maxNameLen = 20; // TODO: Figure this out dynamically

		// First display command/aliases nodes
		for (Node child : group.nodes())
		{
			if (child instanceof CommandNode)
			{
				try
				{
					CommandNode node = (CommandNode) child;
					String name = StringUtils.rightPad(node.getName(),
							maxNameLen);

					Command command = ConsoleShell.getInstance().getCommandRegistry().
					lookup(node.getId());
					if(((ConsoleCommand)command).isAccessible())
					{
						String desc = command.getDescription();
	
						io.out.print("  ");
						io.out.print(renderer.render(Renderer.encode(name,
								Code.BOLD)));
	
						if (desc != null)
						{
							io.out.print("  ");
							io.out.println(desc);
						} else
						{
							io.out.println();
						}
					}
				} catch (NotRegisteredException e)
				{
					// Ignore those exceptions (command will not be displayed)
				}
			} else if (child instanceof AliasNode)
			{
				AliasNode node = (AliasNode) child;
				String name = StringUtils.rightPad(node.getName(), maxNameLen);

				io.out.print("  ");
				io.out.print(renderer.render(Renderer.encode(name, Code.BOLD)));
				io.out.print("  ");

				io.out.print("Alias to: ");
				io.out.println(renderer.render(Renderer.encode(node
						.getCommand(), Code.BOLD)));
			}
		}

		io.out.println();

		// Then groups
		for (Node child : group.nodes())
		{
			if (child instanceof GroupNode)
			{
				GroupNode node = (GroupNode) child;

				String path = node.getPath();

				//
				// HACK: Until we get / and ../ stuff working, we have to strip
				// off the leading "/"
				//
				// https://issues.apache.org/jira/browse/GSHELL-86
				//
				if (path != null && path.startsWith("/"))
				{
					path = path.substring(1, path.length());
				}

				io.out.print("  ");
				io.out.println(renderer
						.render(Renderer.encode(path, Code.BOLD)));

				io.out.println();
				displayGroupCommands(node);
				io.out.println();
			}
		}
	}

	private void displayCommandHelp(String path) throws Exception
	{
		assert path != null;
		
		if(!path.startsWith("openmobster:"))
		{
			path = "openmobster:"+path;
		}

		Command cmd = ConsoleShell.getInstance().getCommandRegistry().lookup(path);

		if (cmd == null)
		{
			io.out.println("Command " + Renderer.encode(path, Code.BOLD)
					+ " not found.");
			io.out.println("Try " + Renderer.encode("help", Code.BOLD)
					+ " for a list of available commands.");
		} else
		{
			if(((ConsoleCommand)cmd).isAccessible())
			{
				io.out.println("Command " + Renderer.encode(path, Code.BOLD));
				io.out.println("   " + cmd.getDescription());
			}
		}

		io.out.println();
	}
}
