/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.mgr;

import java.util.Vector;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.app.Activity;

/**
 * @author openmobster@gmail.com
 *
 */
final class LoadAppStoreTask implements Task
{	
	LoadAppStoreTask()
	{
	}
	
	public void execute(CommandContext commandContext) throws AppException
	{
		try
		{
			Request request = new Request("moblet-management://appStore");
			request.setAttribute("action", "getRegisteredApps");
			request.setAttribute("platform", "android");
			
			Response response = MobileService.invoke(request);	
			
			Vector uris = response.getListAttribute("uris");
			Vector names = response.getListAttribute("names");
			Vector descs = response.getListAttribute("descs");
			Vector downloadUrls = response.getListAttribute("downloadUrls");
			
			commandContext.setAttribute("uris", uris);
			commandContext.setAttribute("names", names);
			commandContext.setAttribute("descs", descs);
			commandContext.setAttribute("downloadUrls", downloadUrls);
		}		
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "execute", new Object[]{
			"Exception :"+ e.getMessage()				
			}));
			
			AppException appException = new AppException();
			appException.setMessageKey("load_app_store_failure");
			throw appException;
		}
	}
	
	public void postExecute(CommandContext commandContext) throws AppException
	{
		try
		{
			Vector uris = (Vector)commandContext.getAttribute("uris");
			Vector names = (Vector)commandContext.getAttribute("names");
			Vector descs = (Vector)commandContext.getAttribute("descs");
			Vector downloadUrls = (Vector)commandContext.getAttribute("downloadUrls");
			
			String[] items = null;
			if(names != null && !names.isEmpty())
			{
				items = new String[names.size()];
				int i = 0;
				for(Object item:names)
				{
					items[i++] = (String)item; 
				}
			}
			else
			{
				items = new String[]{"The Enterprise App Store is empty"};
			}
			
			Activity currentActivity = (Activity)commandContext.getAttribute("currentActivity");
			AlertDialog appDialog = new AlertDialog.Builder(currentActivity).
			setItems(items, 
			new ClickListener(currentActivity,downloadUrls)).
	    	setCancelable(true).
	    	create();
			
			appDialog.setTitle("Enterprise App Store");
							
			appDialog.show();
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "postExecute", new Object[]{
			"Exception :"+ e.getMessage()				
			}));
			
			AppException appException = new AppException();
			appException.setMessageKey("load_app_store_failure");
			throw appException;
		}
	}
	
	private class ClickListener implements DialogInterface.OnClickListener
	{
		private Activity currentActivity;
		private Vector downloadUrls;
		
		private ClickListener(Activity currentActivity,Vector downloadUrls)
		{
			this.currentActivity = currentActivity;
			this.downloadUrls = downloadUrls;
		}
		
		public void onClick(DialogInterface dialog, int status)
		{
			if(downloadUrls != null &&! downloadUrls.isEmpty())
			{
				String downloadUrl = (String)this.downloadUrls.get(status);
				//Download/Install the App via the official browser download mechanism
				
				Context context = Registry.getActiveInstance().getContext();
				Configuration conf = Configuration.getInstance(context);
				String httpPort = conf.getHttpPort();
				
				String appUrl = "http://"+conf.getServerIp()+":"+httpPort+"/o/android"+downloadUrl;
				this.currentActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl)));
			}
			else
			{
				dialog.cancel();
			}
		}
	}
	
	@Override
	public void postExecuteAppException(CommandContext commandContext) throws AppException
	{
		
	}
}
