/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package <appCreator.android.main.groupId>.screen;

import <appCreator.android.main.groupId>.R;
import <appCreator.android.main.groupId>.command.DeleteTicket;
import <appCreator.android.main.groupId>.command.DemoPush;
import <appCreator.android.main.groupId>.command.PlainPush;
import <appCreator.android.main.groupId>.command.ResetChannel;
import <appCreator.android.main.groupId>.system.ActivationRequest;
import java.util.ArrayList;
import java.util.HashMap;
import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android_native.framework.CloudService;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.Button;

public class HomeScreen extends Activity{
	public static MobileBean[] activeBeans;
	ListView listView=null;
	
	private static boolean syncInProgress=false;
	private static boolean syncComplete = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		listView=(ListView) findViewById(R.id.list);							
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		//Bootstrap the OpenMobster Service in the main activity of your App
		CloudService.getInstance().start(this);
	}
	
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		//Check to make sure the App is activated with the OpenMobster Backend
		boolean isDeviceActivated = CloudService.getInstance().isDeviceActivated();
		if(!isDeviceActivated)
		{						
			this.startDeviceActivation();
			return;
		}
		
		this.showTicket();
	}

	private void startDeviceActivation()
	{
		final AlertDialog activationDialog = new AlertDialog.Builder(HomeScreen.this).create();
		activationDialog.setTitle("App Activation");
		
		View view=LayoutInflater.from(this).inflate(R.layout.appactivation,null);
		activationDialog.setView(view);
		final EditText serverip_t=(EditText)view.findViewById(R.id.serverip);
		final EditText portno_t=(EditText)view.findViewById(R.id.portno);
		final EditText emailid_t=(EditText)view.findViewById(R.id.emailid);
		final EditText password_t=(EditText)view.findViewById(R.id.password);
		activationDialog.setCancelable(false);
		
		activationDialog.setButton(AlertDialog.BUTTON_POSITIVE,"Submit", (DialogInterface.OnClickListener)null);		
	
		activationDialog.setButton2("Cancel",new OnClickListener() {			
			@Override
			public void onClick(DialogInterface arg0, int arg1){
				finish();
			}
		});
		
		activationDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			
			@Override
			public void onShow(DialogInterface d)
			{
				Button submit = activationDialog.getButton(AlertDialog.BUTTON_POSITIVE);
				submit.setOnClickListener(new View.OnClickListener(){
					@Override
					public void onClick(View view)
					{
						String serverip=serverip_t.getText().toString();
						String portnoStr=portno_t.getText().toString();
						String emailid=emailid_t.getText().toString();
						String password=password_t.getText().toString();
						if(serverip == null || serverip.trim().length()==0 ||
						   portnoStr == null || portnoStr.trim().length()==0 ||
						   emailid == null || emailid.trim().length()==0 ||
						   password == null || password.trim().length()==0
						)
						{
							ViewHelper.getOkModal(HomeScreen.this, "App Activation Failure", "All the fields are required for a successful activation").show();
							return;
						}
						
						Handler handler=new Handler(){
							@Override
							public void handleMessage(Message msg)
							{
								int what=msg.what;
								if(what==1)
								{
									activationDialog.dismiss();
									showTicket();
								}
							}				
						};
						
						int portno = Integer.parseInt(portnoStr);
						ActivationRequest activationRequest=new ActivationRequest(serverip,portno,emailid,password);
						new ToActivateDevice(HomeScreen.this,handler,activationRequest).execute();
					}
				});
			}
		});
		
		activationDialog.show();
	}
	
	private class ToActivateDevice extends AsyncTask<Void,Void,String>
	{

		Context context;
		ProgressDialog dialog = null;
		Handler handler;
		Message message;
		ActivationRequest activationRequest;		
		
		public ToActivateDevice(Context context,Handler handler,ActivationRequest activationRequest){
			this.context=context;
			this.handler = handler;	
			this.activationRequest=activationRequest;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			dialog.dismiss();
			
			if(result != null)
			{
				ViewHelper.getOkModal(HomeScreen.this, "App Activation Failure", result).show();
			}
			else
			{
				handler.sendMessage(message);
			}
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
		protected String doInBackground(Void... arg0){			 
			try
			{
				//Start device activation
				CloudService.getInstance().activateDevice(activationRequest.getServerIP(),
				activationRequest.getPortNo(),activationRequest.getEmailId(),activationRequest.getPassword());
				
				//Start the local OpenMobster service after a successful activation
				CloudService.getInstance().start(HomeScreen.this);
				
				message=handler.obtainMessage();
				message.what=1;
				
				return null;
			}
			catch(Exception se)
			{
				
				return se.getMessage();
			}
		}		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add("New Ticket");	
		menu.add("Refresh Screen");	
		menu.add("Test Push Notification");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		String title=item.getTitle().toString();
		if(title.equalsIgnoreCase("New Ticket")){
			Intent intent=new Intent(HomeScreen.this,NewTicketScreen.class);
			startActivity(intent);
			finish();
		}
		else if(title.equalsIgnoreCase("Refresh Screen")){
			this.showTicket();
		}						
		else if(title.equalsIgnoreCase("Test Push Notification")){
			Handler handler=new Handler(){
				@Override
				public void handleMessage(Message msg)
				{
					int what=msg.what;
					if(what==1){
						Toast.makeText(HomeScreen.this,"Push Notification successfully triggered",1).show();
						showTicket();
					}
				}				
			};
			new PlainPush(HomeScreen.this,handler).execute();			
		}				
		return super.onMenuItemSelected(featureId, item);
	}
	
	public void showTicket()
	{
		//Read all the CRM Ticket instances synced locally with the device
		if(MobileBean.isBooted("crm_ticket_channel"))
		{			
			activeBeans = MobileBean.readAll("crm_ticket_channel");
			
			ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
			if(activeBeans != null && activeBeans.length>0)
			{
				for(MobileBean local:activeBeans)
				{
					HashMap<String, String> map = new HashMap<String, String>();				
					String customer = local.getValue("customer");
					String title = local.getValue("title");
					
					if(customer.length() > 25)
					{
						customer = customer.substring(0, 22)+"...";
					}
					
					if(title.length() > 25)
					{
						title = title.substring(0, 22)+"...";
					}
					
					map.put("customer", customer);
					map.put("title", title);
					mylist.add(map);
				}
			}
			
			SimpleAdapter ticketAdapter = new SimpleAdapter(HomeScreen.this, mylist,R.layout.ticket_row,new String[] {"customer", "title"}, new int[] {R.id.customer,R.id.title});
		    listView.setAdapter(ticketAdapter);		 
		    listView.setOnItemClickListener(new MyItemClickListener(activeBeans));
		}
		else
		{
			//Tickets not found...put up a Sync in progress message and wait for data to be downloaded 
			//from the Backend
			if(!HomeScreen.syncInProgress && !HomeScreen.syncComplete)
			{
				HomeScreen.syncInProgress = true;
				SyncInProgressAsyncTask task = new SyncInProgressAsyncTask();
				task.execute();
			}
		}
	}
	
	private class SyncInProgressAsyncTask extends AsyncTask<Void,Void,String>
	{
		private ProgressDialog dialog = null;
		
		private SyncInProgressAsyncTask()
		{
			
		}
		
		@Override
		protected void onPreExecute()
		{
			dialog = new ProgressDialog(HomeScreen.this);		
			dialog.setMessage("Sync in Progress....");
			dialog.setCancelable(false);
			dialog.show();
		}

		@Override
		protected String doInBackground(Void... arg0)
		{
			try
			{
				//Check if the CRM Ticket channel has data to be read
				boolean isBooted = MobileBean.isBooted("crm_ticket_channel");
				int counter = 20;
				while(!isBooted)
				{
					Thread.sleep(2000);
					
					if(counter > 0)
					{
						isBooted = MobileBean.isBooted("crm_ticket_channel");
						counter--;
					}
					else
					{
						break;
					}
				}
				
				return ""+isBooted;
			}
			catch(Exception e)
			{
				return "failure";
			}
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			this.dialog.dismiss();
			
			if(result.equals(Boolean.TRUE.toString()) || result.equals(Boolean.FALSE.toString()))
			{
				HomeScreen.syncInProgress = false;
				HomeScreen.syncComplete = true;
				showTicket();
			}
			else
			{
				final AlertDialog dialog = ViewHelper.getOkModalWithCloseApp(HomeScreen.this, "Sync Failure", "Data Sync Failed. Please restart the App");
				dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int status)
							{
								dialog.dismiss();
								HomeScreen.syncInProgress = false;
								HomeScreen.syncComplete = false;
								HomeScreen.this.finish();
							}
				});
				dialog.show();
			}
		}
	}
	
	private class MyItemClickListener implements OnItemClickListener
	{
		private MobileBean[] activeBeans;
		MyItemClickListener(MobileBean[] activeBeans){
			this.activeBeans=activeBeans;
		}
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1,final int selectedIndex,long arg3)
		{
			final MobileBean selectedBean = activeBeans[selectedIndex];
			AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
			builder.setMessage(selectedBean.getValue("comment"));
			builder.setCancelable(false);
			builder.setTitle("Specialist: "+selectedBean.getValue("specialist"));
			builder.setPositiveButton("Update", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					Intent intent=new Intent(HomeScreen.this,UpdateTicketScreen.class);
					intent.putExtra("SelectedIndex",selectedIndex);
					startActivity(intent);
					finish();
					dialog.dismiss();
				}
			});			
			builder.setNegativeButton("Delete", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					Handler handler = new Handler() {
						@Override
			        	public void handleMessage(Message msg) {
							int what= msg.what;
			        		if(what==1){
			        			Toast.makeText(HomeScreen.this,"Record successfully deleted",1).show();
			        		}
			        		showTicket();			        				
						}
			        };			        		
			        new DeleteTicket(HomeScreen.this, handler, selectedBean).execute();
			        dialog.dismiss();
				}
			});
			builder.setNeutralButton("Close", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					dialog.dismiss();
				}
			});			
			AlertDialog alert = builder.create();
			alert.show();			
		}		
	}
}