
package <appCreator.android.main.groupId>.command;

/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import org.openmobster.core.mobileCloud.android.module.bus.Bus;
import org.openmobster.core.mobileCloud.android.module.bus.SyncInvocation;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * @author openmobster@gmail.com
 *  
 */

public class ResetChannel extends AsyncTask<Void,Void,Void>{
	Context context;
	ProgressDialog dialog = null;
	Handler handler;
	Message message;
	
	public ResetChannel(Context context,Handler handler){
		this.context=context;
		this.handler = handler;	
	}
	
	@Override
	protected void onPostExecute(Void result)
	{
		dialog.dismiss();
		handler.sendMessage(message);
	}

	@Override
	protected void onPreExecute()
	{
		dialog = new ProgressDialog(context);		
		dialog.setMessage("Please wait...");
		dialog.setCancelable(false);
		dialog.show();	
	}

	@Override
	protected Void doInBackground(Void... arg0)
	{
		message = handler.obtainMessage();
		try
		{			
			SyncInvocation syncInvocation = new SyncInvocation("org.openmobster.core.mobileCloud.android.invocation.SyncInvocationHandler", 
			SyncInvocation.bootSync, "crm_ticket_channel");		
			Bus.getInstance().invokeService(syncInvocation);
			message.what=1;
		}		
		catch(Exception be){
			
		}
		
		return null;
	}	
}