/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework.push;

import java.util.Map;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Response;
import org.openmobster.core.mobileCloud.api.ui.framework.AppConfig;

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.Invocation;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationHandler;
import org.openmobster.core.mobileCloud.android.module.bus.InvocationResponse;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;

import android.os.Handler;
import android.os.Looper;



/**
 * @author openmobster@gmail.com
 *
 */
public class PushRPCInvocationHandler extends Service implements InvocationHandler
{
	private String uri;
	
	public PushRPCInvocationHandler()
	{
		
	}	
	
	public void start() 
	{
		try
		{
			Bus.getInstance().register(this);
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(), "start", new Object[]{e.getMessage()});
		}
	}
	
	public void stop() 
	{		
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------
	public String getUri() 
	{	
		return this.getClass().getName();
	}

	public InvocationResponse handleInvocation(Invocation invocation) 
	{
		try
		{
			//Get the paylod
			String payload = invocation.getValue("payload");
			
			//Leave for debugging
			//System.out.println("PushRPC on: "+ Registry.getActiveInstance().getContext().getPackageName());
			//System.out.println(payload);
			
			//Generate a CommandContext and make sure it has a target..
			//If target does not match an local command, no worries..nothing will be invoked
			PushCommandContext commandContext = this.parse(payload);
			String target = commandContext.getTarget();
			if(target == null || target.trim().length()==0)
			{
				return null;
			}
			
			//Find the Push Command to be executed
			PushCommand command = this.findPushCommand(target);
			if(command != null)
			{
				Thread t = new Thread(new PushRunner(command,commandContext));
				t.start();
			}
			
			/*
			//Setting up the EDT for Command execution
			CommandLooper looper = new CommandLooper();
			looper.start();
			
			while(!looper.isReady());
			
			looper.handler.post(new RunCommand(commandContext));*/
			
		}		
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "handleInvocation", new Object[]{						
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					} 
			));
		}
		return null;
	}
	
	private static class PushRunner implements Runnable
	{
		PushCommand command;
		PushCommandContext context;
		private PushRunner(PushCommand command, PushCommandContext context)
		{
			this.command = command;
			this.context = context;
		}
		public void run()
		{
			this.command.handlePush(context);
		}
	}
		
	private PushCommandContext parse(String response) throws Exception
	{		
		PushCommandContext commandContext = new PushCommandContext();
		
		Response parsed = MobileService.parse(response);
		
		String[] keys = parsed.getNames();
		for(String key:keys)
		{
			String value = parsed.getAttribute(key);
			commandContext.setAttribute(key, value);
		}
		commandContext.setTarget(parsed.getAttribute("service"));
		
		return commandContext;
	}
	
	private PushCommand findPushCommand(String target)
	{
		AppConfig appConfig = AppConfig.getInstance();
		
		Map<String,PushCommand> conf = appConfig.getPushCommands();
		if(conf != null)
		{
			PushCommand command = conf.get(target);
			return command;
		}
		
		return null;
	}
	
	/*private class CommandLooper extends Thread
	{
		private Handler handler;
		
		private CommandLooper()
		{
			
		}
		
		public void run()
		{
			Looper.prepare();
			
			this.handler = new Handler();
			
			Looper.loop();
		}
		
		public boolean isReady()
		{
			return this.handler != null;
		}
	}
	
	private class RunCommand implements Runnable
	{
		private CommandContext commandContext;
		private RunCommand(CommandContext commandContext)
		{
			this.commandContext = commandContext;
		}
		public void run()
		{
			//Making the invocation
			Services.getInstance().getCommandService().execute(commandContext);
		}
	}*/	
}
