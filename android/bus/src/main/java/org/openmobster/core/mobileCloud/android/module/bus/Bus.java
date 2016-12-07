/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.bus;

import java.util.Map;
import java.util.HashMap;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;

/**
 * @author openmobster@gmail.com
 *
 */
public final class Bus extends Service
{
	private String busId;
	private Map<String, InvocationHandler> invocationHandlers;
	
	
	public Bus()
	{				
	}
		
	public void start()
	{
		try
		{
			this.invocationHandlers = new HashMap<String, InvocationHandler>();
			
			Context context = Registry.getActiveInstance().getContext();
			this.busId = context.getPackageName();
		}
		catch(Exception e)
		{						
			throw new SystemException(this.getClass().getName(), "start", new Object[]{e.getMessage()});
		}
	}
	
	public void stop()
	{
		try
		{
			this.invocationHandlers = null;
			this.busId = null;
		}
		catch(Exception e)
		{						
			SystemException syse = new SystemException(this.getClass().getName(), "stop", new Object[]{e.getMessage()});
			ErrorHandler.getInstance().handle(syse);
		}
	}
				
	public static Bus getInstance()
	{
		return (Bus)Registry.getActiveInstance().lookup(Bus.class);
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	public String getBusId()
	{
		return this.busId;
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------
	/**
	 * Invokes a Service registered with the ServiceBus on the device
	 * 
	 * @param input
	 * @return
	 */
	public InvocationResponse invokeService(Invocation invocation) throws BusException
	{
		try
		{
			InvocationResponse response = null;
			
			InvocationHandler handler = this.findHandler(invocation.getTarget());
			if(handler != null)
			{
				response = handler.handleInvocation(invocation);
			}
			
			return response;
		}
		catch(Exception e)
		{
			BusException be = new BusException(this.getClass().getName(), "invokeService", new Object[]{
				"Invocation="+invocation.toString(),
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
			ErrorHandler.getInstance().handle(be);
			throw be;
		}
	}
	
	/**
	 * Sends an invocation broadcast for multiple invocations on the device. The Invocation responses of the
	 * Handlers are ignored
	 * 
	 * @param input
	 * @return
	 */
	public void broadcast(Invocation invocation) throws BusException
	{
		try
		{
			this.invokeService(invocation);
		}
		catch(Exception e)
		{
			BusException be = new BusException(this.getClass().getName(), "broadcast", new Object[]{
				"Invocation="+invocation.toString(),
				"Exception="+e.toString(),
				"Message="+e.getMessage()
			});
			ErrorHandler.getInstance().handle(be);
			throw be;
		}
	}
	
	public void register(InvocationHandler handler) throws BusException
	{
		try
		{
			String target = handler.getClass().getName();
			this.invocationHandlers.put(target, handler);
		}
		catch(Exception e)
		{						
			BusException be = new BusException(this.getClass().getName(),"register",
			new Object[]{
				"InvocationHandler: "+handler.getClass().getName(),
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
			ErrorHandler.getInstance().handle(be);
			throw be;
		}
	}
	//---------------------------------------------------------------------------------------------
	public InvocationHandler findHandler(String target)
	{					
		return this.invocationHandlers.get(target);
	}
}
