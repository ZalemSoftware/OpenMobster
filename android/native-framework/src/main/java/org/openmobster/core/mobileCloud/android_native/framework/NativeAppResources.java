/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework;

import java.io.InputStream;
import java.util.Locale;

import android.graphics.BitmapFactory;

import org.openmobster.core.mobileCloud.api.ui.framework.AppConfig;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.util.Localizer;


/**
 * @author openmobster@gmail.com
 *
 */
final class NativeAppResources implements AppResources
{
	private Localizer systemLocalizer;
	private Localizer appLocalizer;
			
	NativeAppResources()
	{
		try
		{
			this.systemLocalizer = Localizer.getInstance("system/localize");
			
			String appResource = "moblet-app/localize";
			Locale locale = AppConfig.getInstance().getAppLocale();
			
			if(locale == null)
			{
				this.appLocalizer = Localizer.getInstance(appResource);
			}
			else
			{
				this.appLocalizer = Localizer.getInstance(appResource, locale);
			}
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(),"constructor",new Object[]{
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
		}
	}
	//--------------------------------------------------------------------------------------------------------------------------------------------
	public Object getImage(String imageName) 
	{
		InputStream is = null;
		try
		{
			if(!imageName.startsWith("/"))
			{
				imageName = "/" + imageName;
			}
			is = NativeAppResources.class.getResourceAsStream(imageName);
			
			if(is != null)
			{
				return BitmapFactory.decodeStream(is);
			}
			
			return null;
		}
		catch(Exception e)
		{
			throw new SystemException(this.getClass().getName(),"getImage",new Object[]{
				"Image: "+imageName,
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
		}
		finally
		{
			if(is != null)
			{
				try{is.close();}catch(Exception e){}
			}
		}
	}
								
	public String localize(String key, String defaultValue) 
	{				
		String value = this.appLocalizer.getString(key);
		if(value != null)
		{
			return value;
		}
		
		value = this.systemLocalizer.getString(key);
		if(value != null)
		{
			return value;
		}
		
		return defaultValue;
	}	
	
	public Object getAnimation(String id)
	{
		return null;
	}					
}
