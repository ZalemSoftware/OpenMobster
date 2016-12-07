
package <appCreator.android.main.groupId>.command;

/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * @author openmobster@gmail.com
 */

public class AsyncLoadSpinners extends AsyncTask<Void,Void,Void>{
	Context context;
	ProgressDialog dialog = null;
	Handler handler;
	Message message;
	
	public AsyncLoadSpinners(Context context,Handler handler){
		this.context=context;
		this.handler = handler;	
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
	protected void onPostExecute(Void result)
	{
		dialog.dismiss();
		handler.sendMessage(message);
	}

	@Override
	protected Void doInBackground(Void... arg0)
	{
		Request request = new Request("/async/load/spinners");
		try{
			Response response = new MobileService().invoke(request);			
			Vector customers = response.getListAttribute("customers");
			Vector specialists = response.getListAttribute("specialists");			
			message = handler.obtainMessage();
			Map <String,Vector>map=new HashMap<String,Vector>();
			try{				
				message.what = 1;
				map.put("customers",customers);
				map.put("specialists",specialists);
				message.obj=map;				
			}catch(Exception ex){				
			}
		}catch(Exception ex){			
		}
		return null;
	}	
}