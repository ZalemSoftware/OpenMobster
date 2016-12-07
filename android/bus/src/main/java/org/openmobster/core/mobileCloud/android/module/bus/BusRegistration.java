/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.bus;

import java.util.Set;
import java.util.HashSet;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.database.Cursor;
import android.content.ContentValues;

import org.openmobster.core.mobileCloud.android.service.Registry;

/**
 * @author openmobster@gmail.com
 *
 */
public final class BusRegistration
{
	private static final Uri busUri;
	
	static
	{
		busUri = Uri.
		parse("content://org.openmobster.core.mobileCloud.android.module.bus.provider");
	}
	
	private String busId;
	private Set<String> invocationHandlers;
	
	public BusRegistration(String busId)
	{
		if(busId == null || busId.trim().length() == 0)
		{
			throw new IllegalArgumentException("BusId is required!!");
		}
		this.busId = busId;		
	}

	public String getBusId()
	{
		return busId;
	}
			
	public Set<String> getInvocationHandlers()
	{
		if(this.invocationHandlers == null)
		{
			this.invocationHandlers = new HashSet<String>();
		}
		return invocationHandlers;
	}

	public void setInvocationHandlers(Set<String> invocationHandlers)
	{
		this.invocationHandlers = invocationHandlers;
	}
	
	public void addInvocationHandler(String invocationHandler)
	{
		this.getInvocationHandlers().add(invocationHandler);
	}
	//---Storage operations----------------------------------------------------------------------------------
	public void save() throws BusException
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			ContentResolver resolver = context.getContentResolver();
											
			resolver.insert(busUri, BusRegistration.mapToContent(this));
		}
		catch(Exception e)
		{
			throw new BusException(this.getClass().getName(),"save", 
					new Object[]{
					"Exception: "+e.toString(),
					"Message: "+e.getMessage()
				}
			);
		}
	}
	
	public static Set<BusRegistration> queryAll() throws BusException
	{	
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			ContentResolver resolver = context.getContentResolver();
							
			Cursor cursor = resolver.query(busUri, 
			null, 
			null, 
			null, 
			null);
			
			return BusRegistration.parseCursor(cursor);
		}
		catch(Exception e)
		{
			throw new BusException(BusRegistration.class.getName(),"queryAll", 
					new Object[]{
					"Exception: "+e.toString(),
					"Message: "+e.getMessage()
				}
			);
		}
	}
	
	public static BusRegistration query(String busId) throws BusException
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			ContentResolver resolver = context.getContentResolver();
							
			Cursor cursor = resolver.query(busUri, 
			null, 
			busId, 
			null, 
			null);
			
			Set<BusRegistration> registration = BusRegistration.parseCursor(cursor);
			if(registration != null && !registration.isEmpty())
			{
				return registration.iterator().next();
			}
			
			return null;
		}
		catch(Exception e)
		{
			throw new BusException(BusRegistration.class.getName(),"query", 
					new Object[]{
					"Exception: "+e.toString(),
					"Message: "+e.getMessage()
				}
			);
		}
	}
	
	public static void delete(String busId) throws BusException
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			ContentResolver resolver = context.getContentResolver();
							
			resolver.delete(busUri, busId, null);
		}
		catch(Exception e)
		{
			throw new BusException(BusRegistration.class.getName(),"delete", 
					new Object[]{
					"Exception: "+e.toString(),
					"Message: "+e.getMessage()
				}
			);
		}
	}
	
	public static Set<String> allBuses() throws BusException
	{
		Set<String> all = new HashSet<String>();
		
		Set<BusRegistration> allReg = BusRegistration.queryAll();
		if(allReg != null)
		{
			for(BusRegistration cour:allReg)
			{
				all.add(cour.getBusId());
			}
		}
		
		return all;
	}
	//-------Cursor processing-------------------------------------------------------------------------------					
	private static ContentValues mapToContent(BusRegistration reg)
	{
		ContentValues values = new ContentValues();
		
		String busId = reg.getBusId();
		
		values.put("busId", busId);
		
		if(reg.invocationHandlers!=null)
		{
			for(String handler: reg.invocationHandlers)
			{
				values.put(handler, handler);
			}
		}
		
		return values;
	}
	
	private static Set<BusRegistration> parseCursor(Cursor cursor)
	{
		Set<BusRegistration> regs = new HashSet<BusRegistration>();
		
		if(cursor!=null && cursor.getCount()>0)
		{
			int busIdIndex = cursor.getColumnIndex("busId");
			int handlerIndex = cursor.getColumnIndex("handler");
			cursor.moveToFirst();
			BusRegistration cour = null;
			do
			{
				String busId = cursor.getString(busIdIndex);
				if(!cursor.isNull(handlerIndex))
				{
					//this is a handler
					String handler = cursor.getString(handlerIndex);
					cour.addInvocationHandler(handler);
				}
				else
				{
					//this is busId
					cour = new BusRegistration(busId);
					regs.add(cour);
				}
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
		}
		
		return regs;
	}
}
