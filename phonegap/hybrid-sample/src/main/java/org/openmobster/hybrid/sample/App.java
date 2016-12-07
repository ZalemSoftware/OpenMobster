/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.hybrid.sample;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.CloudService;
import org.openmobster.core.mobileCloud.mgr.AppActivation;

import android.os.Bundle;

import org.apache.cordova.*;

/**
 *
 * @author openmobster@gmail.com
 */
public class App extends DroidGap 
{
	@Override
	public void onCreate(Bundle bundle) 
	{
		super.onCreate(bundle);
		
		// Set by <content src="index.html" /> in config.xml
        super.loadUrl(Config.getStartUrl());
        //super.loadUrl("file:///android_asset/www/index.html")
	}
	
	@Override
	protected void onStart()
	{
		try
		{
			super.onStart();
			CloudService.getInstance().start(this);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onStart", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
		}
	}
	
	@Override
	protected void onResume()
	{
		try
		{
			super.onResume();
			
			//check if App activation is needed
			Configuration conf = Configuration.getInstance(Registry.getActiveInstance().getContext());
			if(!conf.isActive())
			{
				AppActivation appActivation = AppActivation.getInstance(this);
				appActivation.start();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), 
					"onResume", new Object[]{
						"Message:"+e.getMessage(),
						"Exception:"+e.toString()
					}));
		}
	}
}
