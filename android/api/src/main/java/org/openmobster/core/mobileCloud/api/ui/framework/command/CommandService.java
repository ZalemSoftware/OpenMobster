/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework.command;

import java.util.Hashtable;

import org.openmobster.core.mobileCloud.api.ui.framework.AppConfig;

/**
 * @author openmobster@gmail.com
 *
 */
public abstract class CommandService 
{	
	public CommandService()
	{		
	}
	
	public final UIInitiatedCommand findUICommand(String commandId)
	{
		Hashtable commandConfig = AppConfig.getInstance().getAppCommands();		
		
		Command command = (Command)commandConfig.get(commandId);
		if(command instanceof UIInitiatedCommand)
		{
			return (UIInitiatedCommand)command;
		}
		
		return null;
	}
	
	public final SystemInitiatedCommand findSystemCommand(String commandId)
	{
		Hashtable commandConfig = AppConfig.getInstance().getAppCommands();		
		
		Command command = (Command)commandConfig.get(commandId);
		if(command instanceof SystemInitiatedCommand)
		{
			return (SystemInitiatedCommand)command;
		}
		
		return null;
	}
	
	public final void reportAppException(CommandContext commandContext, AppException appException)
	{
		commandContext.setAppException(appException);
	}
	
	/**
	 * Executes a Command based on the CommandContext
	 * 
	 * @param commandContext
	 */
	public abstract void execute(CommandContext commandContext);		
}
