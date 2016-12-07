/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.util;

import java.io.InputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;

/**
 * @author openmobster@gmail.com
 *
 */
public final class Localizer 
{
	private Map<String,String> resources;
	
	private Localizer()
	{		
		this.resources = new HashMap<String,String>();
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------
	public static Localizer getInstance(String resourceName)
	{
		try
		{
			String resource = "/"+resourceName+".properties";
								
			Localizer localizer = new Localizer();
			localizer.parse(resource);
		
			return localizer;
		}
		catch(Exception e)
		{
			SystemException syse = new SystemException(Localizer.class.getName(),"getInstance", new Object[]{
				"ResourceName: "+resourceName,
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
			ErrorHandler.getInstance().handle(syse);
			throw syse;
		}
	}
	
	public static Localizer getInstance(String resourceName, Locale locale)
	{
		try
		{
			String resource = null;
			
			if(locale.getLanguage()==null || locale.getLanguage().trim().length()==0)
			{
				throw new IllegalStateException("Language must be specified for the locale");
			}
			
			if(locale.getCountry() != null && locale.getCountry().trim().length()>0)
			{
				resource = "/"+resourceName+"_"+locale.getLanguage()+"_"+locale.getCountry()+".properties";
			}
			else
			{
				resource = "/"+resourceName+"_"+locale.getLanguage()+".properties";
			}
		
						
			
			Localizer localizer = new Localizer();
			localizer.parse(resource);
		
			return localizer;
		}
		catch(Exception e)
		{
			SystemException syse = new SystemException(Localizer.class.getName(),"getInstance", new Object[]{
				"ResourceName: "+resourceName,
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
			ErrorHandler.getInstance().handle(syse);
			throw syse;
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------
	public String getString(String key)
	{
		return (String)this.resources.get(key);
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------
	private void parse(String resource) throws IOException
	{
		InputStream is = Localizer.class.getResourceAsStream(resource);
		if(is != null)
		{
			String contents = new String(IOUtil.read(is));
			String[] tokens = StringUtil.tokenize(contents, "\n");
			if(tokens != null)
			{
				int length = tokens.length;
				for(int i=0; i<length; i++)
				{
					String token = tokens[i].trim();
					
					if(token.indexOf('=')!=-1)
					{
						String[] values = StringUtil.tokenize(token, "=");
						if(values != null && values.length >=2)
						{
							this.resources.put(values[0].trim(), values[1].trim());
						}
					}
				}
			}
		}
	}
}
