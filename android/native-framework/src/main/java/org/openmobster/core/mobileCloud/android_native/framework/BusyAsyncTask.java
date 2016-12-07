/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog;
import android.os.AsyncTask;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.Command;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandService;

/**
 * @author openmobster@gmail.com
 *
 */
final class BusyAsyncTask extends AsyncTask<CommandContext,Integer,CommandContext>
{
	private ProgressDialog progressDialog;
	
	BusyAsyncTask()
	{
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		this.progressDialog = new ProgressDialog(currentActivity);
		this.progressDialog.setTitle("");
		this.progressDialog.setMessage("Processing....");
		this.progressDialog.setCancelable(false);
	}
	
	@Override
	protected CommandContext doInBackground(CommandContext... input)
	{
		CommandContext commandContext = input[0];
		try
		{			
			CommandService service = Services.getInstance().getCommandService();			
			Command command = service.findUICommand(commandContext.getTarget());
			
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
			try
			{
				command.doAction(commandContext);
			}
			catch(AppException ape)
			{
				service.reportAppException(commandContext, ape);
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
				"Exception:"+e.toString(),
				"Target Command:"+commandContext.getTarget()
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
		
		CommandService service = Services.getInstance().getCommandService();			
		Command command = service.findUICommand(result.getTarget());
		
		//Check for any errors
		Exception systemError = (Exception)result.getAttribute("system_error");		
		if(systemError != null)
		{
			ShowError.showGenericError(result);
			return;
		}
		
		//Check for an AppException
		if(result.hasErrors())
		{
			command.doViewError(result);
			return;
		}
		
		//Everything is great!!
		command.doViewAfter(result);
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
			//show command expiry dialog
			Activity currentActivity = Services.getInstance().getCurrentActivity();
			
			AlertDialog timeoutDialog = ViewHelper.getOkModal(currentActivity, 
			"Command Timed Out", 
			"RemoteCommand is taking longer to execute than expected!!");
			
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
					BusyAsyncTask.this.publishProgress(-1);		
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
