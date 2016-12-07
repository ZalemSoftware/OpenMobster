/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.mgr;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.os.AsyncTask;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.SystemLocaleKeys;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;

/**
 * @author openmobster@gmail.com
 *
 */
final class TaskExecutor extends AsyncTask<CommandContext,Integer,CommandContext>
{
	private ProgressDialog progressDialog;
	private Activity currentActivity;
	private String successMessage;
	private String title;
	
	TaskExecutor(String title,String waitMessage, String successMessage,Activity currentActivity)
	{
		this.currentActivity = currentActivity;
		this.successMessage = successMessage;
		this.title = title;
		
		this.progressDialog = new ProgressDialog(currentActivity);
		this.progressDialog.setTitle("");
		this.progressDialog.setMessage(waitMessage);
		this.progressDialog.setCancelable(false);
	}
	
	@Override
	protected CommandContext doInBackground(CommandContext... input)
	{
		CommandContext commandContext = input[0];
		try
		{			
			if(commandContext.isTimeoutActivated())
			{
				Timer timer = new Timer();
				timer.schedule(new RemoteCommandExpiry(commandContext), 
				15000);
			}
			else
			{
				Timer timer = new Timer();
				timer.schedule(new RemoteCommandExpiry(commandContext), 
				45000); //should never take longer this...this is a forced abort
			}
			
			this.publishProgress(0);
			
			//Perform action
			Task task = (Task)commandContext.getAttribute("task");
			try
			{
				task.execute(commandContext);
			}
			catch(AppException ape)
			{
				commandContext.setAppException(ape);
				return commandContext;
			}
			finally
			{
				commandContext.setAttribute("action-finished", "");
			}
			
			return commandContext;
		}
		catch(Exception e)
		{
			//report to ErrorHandling system
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "execute", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
			commandContext.setAttribute("system_error", e);
			
			return commandContext;
		}
	}

	@Override
	protected void onPostExecute(CommandContext result)
	{
		//if this command timeout, then it should not execute the view phase
		if(result.getAttribute("timer-expired") != null)
		{
			return;
		}
		
		this.progressDialog.dismiss();
		
		//Check for any errors
		Exception systemError = (Exception)result.getAttribute("system_error");		
		if(systemError != null)
		{
			AppResources appResources = Services.getInstance().getResources();
			
			String errorTitle = appResources.localize(
			SystemLocaleKeys.system_error, 
			"system_error");
			
			String errorMsg = appResources.localize(
			SystemLocaleKeys.unknown_system_error, 
			"unknown_system_error");
			
			//Show the dialog
	    	ViewHelper.getOkModal(this.currentActivity, errorTitle, errorMsg).show();
	    	
			return;
		}
		
		if(result.hasErrors())
		{
			//Show the dialog
	    	ViewHelper.getOkModal(this.currentActivity, "App Error", result.getAppException().getMessageKey()).show();
	    	this.callPostExecuteAppException(result);
	    	
			return;
		}
		
		//Everything is great!!
		if(this.successMessage != null && this.successMessage.trim().length()>0)
		{
			ViewHelper.getOkModal(this.currentActivity, this.title, this.successMessage).show();
		}
		
		this.callPostExecute(result);
	}
	
	private void callPostExecute(CommandContext result)
	{
		//call the post execute on the task
		Task task = (Task)result.getAttribute("task");
		try
		{
			task.postExecute(result);
		}
		catch(AppException ape)
		{
			//Show the dialog
	    	ViewHelper.getOkModal(this.currentActivity, "App Error", result.getAppException().getMessageKey()).show();
			return;
		}
	}
	
	private void callPostExecuteAppException(CommandContext result)
	{
		//call the post execute on the task
		Task task = (Task)result.getAttribute("task");
		try
		{
			task.postExecuteAppException(result);
		}
		catch(AppException ape)
		{
			//Show the dialog
	    	ViewHelper.getOkModal(this.currentActivity, "App Error", result.getAppException().getMessageKey()).show();
			return;
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values)
	{
		if(values[0] != -1)
		{
			this.progressDialog.show();
		}
		else
		{	
			AlertDialog timeoutDialog = ViewHelper.getOkModal(currentActivity, 
			"Command Timed Out", 
			"RemoteCommand is taking longer to execute than expected");
			
			this.progressDialog.dismiss();
			timeoutDialog.show();
		}
	}
	//-------------------------------------------------------------------------------------
	private class RemoteCommandExpiry extends TimerTask
	{
		private CommandContext commandContext;
		
		private RemoteCommandExpiry(CommandContext commandContext)
		{
			this.commandContext = commandContext;
		}
		
		public void run()
		{
			try
			{
				if(commandContext.getAttribute("action-finished") == null)
				{
					//remote command is still busy
					commandContext.setAttribute("timer-expired", "");	
					TaskExecutor.this.publishProgress(-1);		
				}
			}
			finally
			{
				//makes sure this is cleaned up
				this.cancel();
			}
		}
	}
}
