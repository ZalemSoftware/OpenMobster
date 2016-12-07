/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.storage;

import java.util.Set;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.io.OutputStreamWriter;
import java.io.IOException;

import android.util.JsonWriter;

import org.openmobster.core.mobileCloud.android.filesystem.FileSystem;
import org.openmobster.core.mobileCloud.android.filesystem.File;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;

/**
 * @author openmobster@gmail.com
 *
 */
public final class Record 
{
	private Map<String, String> state;
	
	public Record()
	{
		
	}	
	public Record(String recordId)
	{		
		this.setRecordId(recordId);
	}
	
	public Record(Map<String, String> state)
	{
		this.state = state;
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------
	public String getRecordId()
	{
		return this.getState().get("recordId");
	}
	
	public void setRecordId(String recordId)
	{
		if(recordId == null || recordId.trim().length() == 0)
		{
			throw new IllegalArgumentException("Record Id cannot be empty");
		}
		this.setValue("recordId", recordId);
	}
	
	public void setDirtyStatus(String dirtyStatus)
	{
		this.setValue("dirty", dirtyStatus);
	}
	
	public String getDirtyStatus()
	{
		return this.getValue("dirty");
	}
	
	public void setValue(String name, String value)
	{
		if(name == null)
		{
			throw new IllegalArgumentException("Name cannot be Null");
		}
		
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Suporte a valores nulos para o contorno feito em OpenMobsterBugUtils.onBeforeInsertRecord.
		 */
//		if(value == null)
//		{
//			throw new IllegalArgumentException(name+": Value cannot be Null");
//		}
		
		this.getState().put(name, value);
	}
	
	public String getValue(String name)
	{
		String value = (String)this.getState().get(name);
		
		/*if(value != null && value.trim().length() == 0)
		{
			value = null;
		}*/
		
		return value;
	}
	
	public void removeValue(String name)
	{
		if(name == null)
		{
			throw new IllegalArgumentException("Name cannot be Null");
		}
		this.getState().put(name, "");
	}
	
	public Set<String> getNames()
	{
		return this.getState().keySet();
	}
	
	public Collection<String> getValues()
	{
		return this.getState().values();
	}
		
	public Map<String, String> getState()
	{
		if(this.state == null)
		{
			this.state = new HashMap<String, String>();
		}
		return this.state;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Record))
		{
			return false;
		}
		
		Record incoming = (Record)o;
		String recordId = this.getRecordId();
		String incomingRecordId = incoming.getRecordId();
		
		if(recordId == null || incomingRecordId == null)
		{
			return false;
		}
		
		if(recordId.equals(incomingRecordId))
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
		String recordId = this.getRecordId();
		if(recordId == null)
		{
			return GeneralTools.generateUniqueId().hashCode();
		}
		return recordId.hashCode();
	}
	
	public String toJson()
	{
		FileSystem fileSystem = FileSystem.getInstance();
		File file = fileSystem.openOutputStream();
		JsonWriter writer = new JsonWriter(new OutputStreamWriter(file.getOutputStream()));
		try
		{
			writer.beginObject();
			
			Set<String> names = this.getNames();
			for(String name:names)
			{
				String value = this.getValue(name);
				
				/*
				 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
				 * Suporte a valores nulos para o contorno feito em OpenMobsterBugUtils.onBeforeInsertRecord.
				 */
				if (value == null) {
					continue;
				}
				
				writer.name(name).value(value);
			}
			writer.endObject();
			writer.flush();
			
			return file.getName();
		}
		catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
		finally
		{
			try
			{
				writer.close();
				file.getOutputStream().close();
			}catch(IOException ioe){};
		}
	}
	
	public boolean isStoreable()
	{
		long size = 0;
		
		Set<String> names = this.getNames();
		for(String name:names)
		{
			String value = this.getValue(name);
			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 * Suporte a valores nulos para o contorno feito em OpenMobsterBugUtils.onBeforeInsertRecord.
			 */
			if (value == null) {
				continue;
			}
			
			size += name.length();
			size += value.length();
		}
		
		//Object is too big for local storage
		if(size > 2000000)
		{
			return false;
		}
		
		return true;
	}
}
