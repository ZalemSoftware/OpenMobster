/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.moblet.appStore;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;


import org.openmobster.cloud.api.rpc.MobileServiceBean;
import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.cloud.api.rpc.ServiceInfo;
import org.openmobster.core.common.errors.ErrorHandler;
import org.openmobster.core.common.errors.SystemException;
import org.openmobster.core.common.IOUtilities;
import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.moblet.MobletApp;
import org.openmobster.core.moblet.registry.Registry;

/**
 * @author openmobster@gmail.com
 */
@ServiceInfo(uri="moblet-management://appStore")
public class AppStore implements MobileServiceBean
{	
	private Registry registry;
	
	public AppStore()
	{
		
	}
		
	public Registry getRegistry() 
	{
		return registry;
	}


	public void setRegistry(Registry registry) 
	{
		this.registry = registry;
	}
	
	public static AppStore getInstance()
	{
		return (AppStore)ServiceManager.locate("moblet-management://appStore");
	}
	//--------------------------------------------------------------------------------------------------------
	public Response invoke(Request request) 
	{
		try
		{									
			String action = request.getAttribute("action");
												
			if(action.equalsIgnoreCase("getRegisteredApps"))
			{
				List<MobletApp> allApps = this.getRegisteredApps(request);
				
				//Prepare success response
				if(allApps != null && !allApps.isEmpty())
				{
					Response response = new Response();
					
					List<String> uris = new ArrayList<String>();
					List<String> names = new ArrayList<String>();
					List<String> descs = new ArrayList<String>();
					List<String> downloadUrls = new ArrayList<String>();
					for(MobletApp app: allApps)
					{						
						uris.add(app.getUri());
						names.add(app.getName());
						descs.add(app.getDescription());
						
						if(!app.getBinaryLocation().endsWith(".apk"))
						{
							String downloadUrl = "/"+app.getUri()+app.getConfigLocation();
							downloadUrls.add(downloadUrl);
						}
						else
						{
							downloadUrls.add(app.getBinaryLocation());
						}
					}
					
					response.setListAttribute("uris", uris);
					response.setListAttribute("names", names);
					response.setListAttribute("descs", descs);
					response.setListAttribute("downloadUrls", downloadUrls);
					
					return response;
				}								
			}
			
			return null;
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(e);
			throw new SystemException(e.getMessage(), e);
		}
	}
	//---------------------------------------------------------------------------------------------------------		
	public List<MobletApp> getRegisteredApps(Request request) throws Exception
	{
		List<MobletApp> apps = new ArrayList<MobletApp>();
		List<MobletApp> allApps = this.registry.getAllApps();
		String platform = request.getAttribute("platform");
		if(platform == null)
		{
			//BlackBerry
			for(MobletApp app:allApps)
			{
				if(app.getBinaryLocation().endsWith(".cod"))
				{
					apps.add(app);
				}
			}
		}
		else if(platform.equals("android"))
		{
			//Android
			for(MobletApp app:allApps)
			{
				if(app.getBinaryLocation().endsWith(".apk"))
				{
					apps.add(app);
				}
			}
		}
		return apps;
	}
	
	public byte[] getAppConfig(String downloadUrl) throws Exception
	{
		if(downloadUrl.endsWith(".apk"))
		{
			//NA
			return null;
		}
		StringTokenizer st = new StringTokenizer(downloadUrl, "/");
		String appUri = st.nextToken();
		InputStream is = this.registry.getAppConfig(appUri);
		return IOUtilities.readBytes(is);
	}
	
	public InputStream getAppBinary(String downloadUrl) throws Exception
	{
		if(!downloadUrl.endsWith(".apk"))
		{
			StringTokenizer st = new StringTokenizer(downloadUrl, "/");
			String appUri = st.nextToken();
			return this.registry.getAppBinary(appUri);
		}
		else
		{
			return this.registry.getAppBinary(downloadUrl);
		}
	}
	
	public MobletApp findByDownloadUrl(String downloadUrl) throws Exception
	{
		if(!downloadUrl.endsWith(".apk"))
		{
			StringTokenizer st = new StringTokenizer(downloadUrl, "/");
			String appUri = st.nextToken();
			return this.registry.getApp(appUri);
		}
		else
		{
			return this.registry.getApp(downloadUrl);
		}
	}
}
