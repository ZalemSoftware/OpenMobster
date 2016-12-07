/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.errors;

import java.util.Date;
import java.util.Set;

import android.content.Context;
import android.content.pm.PackageManager;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.storage.Record;

/**
 * @author openmobster@gmail.com
 *
 */
public final class ErrorHandler
{
	private static ErrorHandler singleton;
	
	private ErrorHandler()
	{		
	}
			
	public static ErrorHandler getInstance()
	{
		if(ErrorHandler.singleton == null)
		{
			synchronized(ErrorHandler.class)
			{
				if(ErrorHandler.singleton == null)
				{
					ErrorHandler.singleton = new ErrorHandler();
				}
			}
		}
		return ErrorHandler.singleton;
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------	
	public void handle(Exception e)
	{
		try
		{
			//Collect some meta data
			Registry registry = Registry.getActiveInstance();
			Context context = registry.getContext();
			boolean isCloud = registry.isContainer();
			Date date = new Date();
			
			//TODO: Collect some device related information
			PackageManager pm = context.getPackageManager();
			CharSequence applicationName = pm.getApplicationLabel(context.getApplicationInfo());
			
			StringBuffer buffer = new StringBuffer();
			buffer.append(applicationName+" ");
			buffer.append(date.toString()+" ");
			if(isCloud)
			{
				buffer.append("MobileCloud ");
			}
			else
			{
				buffer.append("Moblet \n");
			}
			
			buffer.append(e.toString()+"\n");
			buffer.append(e.getMessage());

			this.save(context,buffer.toString());
		}
		catch(Exception ex)
		{
			//Crap Error Handler itself bombed....this can turn into an self-fulfilling prophecy
			//Has to be ignored
		}
	}
	
	public String generateReport() 
	{
		try
		{
			StringBuffer buffer = new StringBuffer();
			
			Context context = Registry.getActiveInstance().getContext();
			
			Set<Record> all = this.query(context);
			if(all != null && !all.isEmpty())
			{
				for(Record record:all)
				{
					String message = record.getValue("message");
					buffer.append(message);
					buffer.append("\n----------------------------------------------\n");
				}
			}
			
			return buffer.toString();
		}
		catch(Exception ex)
		{
			throw new SystemException(this.getClass().getName(), "generateReport", new Object[]{
				"Exception="+ex.toString(),
				"Message="+ex.getMessage()
			});
		}
	}
	
	public void clearAll()
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			this.delete(context);
		}
		catch(Exception ex)
		{
			throw new SystemException(this.getClass().getName(), "clearAll", new Object[]{
				"Exception="+ex.toString(),
				"Message="+ex.getMessage()
			});
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------
	private void save(Context context,String message) throws Exception
	{
		Database database = Database.getInstance(context);
		
		//insert
		Record errorRecord = new Record();
		errorRecord.setValue("message", message);
		database.insert(Database.system_errors, 
		errorRecord);
	}	
	
	private void delete(Context context) throws Exception
	{
		Database database = Database.getInstance(context);
		
		//deleteAll
		database.deleteAll(Database.system_errors);
	}
	
	private Set<Record> query(Context context) throws Exception
	{
		Database database = Database.getInstance(context);
		
		Set<Record> all = database.selectAll(Database.system_errors);
		
		return all;
	}
}
