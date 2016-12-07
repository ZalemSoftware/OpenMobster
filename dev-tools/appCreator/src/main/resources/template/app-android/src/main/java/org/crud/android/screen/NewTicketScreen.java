/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package <appCreator.android.main.groupId>.screen;
import <appCreator.android.main.groupId>.R;
import <appCreator.android.main.groupId>.command.AsyncLoadSpinners;
import <appCreator.android.main.groupId>.command.CreateTicket;
import java.util.Map;
import java.util.Vector;
import org.openmobster.android.api.sync.MobileBean;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Controls the 'Create New Ticket' screen. 
 * 
 * The UI presents a simple Form for inputing ticket details, and then you can 'OK' or 'Cancel' the changes
 * 
 * 
 * @author openmobster@gmail.com
 */

public class NewTicketScreen extends Activity{	
	@Override
	protected void onCreate(Bundle savedInstanceState){		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_ticket);
		
		Handler handler = new Handler() {
			@Override
        	public void handleMessage(Message msg) {
				int what= msg.what;
        		if(what==1){
        			Map map=(Map) msg.obj;
        			Vector customers= (Vector) map.get("customers");
        			Vector specialists=(Vector) map.get("specialists");
        			
        			String selectedCustomer = "";
        			String selectedSpecialist = "";
        			//Load the specialist customers
        			Spinner customer = (Spinner)findViewById(R.id.customer);
        			ArrayAdapter<CharSequence> customerAdapter = new ArrayAdapter<CharSequence>(NewTicketScreen.this,android.R.layout.simple_spinner_item);
        			int selectedCustomerPosition = -1;
        			customerAdapter.add("--Select--");
        			int size = customers.size();
        			for(int i=0; i<size; i++)
        			{
        				String local = (String)customers.get(i);
        				customerAdapter.add(local);
        				if(local.equals(selectedCustomer))
        				{
        					selectedCustomerPosition = i;
        				}
        			}
        			customerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        			customer.setAdapter(customerAdapter);
        			customer.setSelection(selectedCustomerPosition+1, true);
        			
        			
        			//Load the specialist spinner        			
        			Spinner specialist = (Spinner)findViewById(R.id.specialist);
        			ArrayAdapter<CharSequence> specialistAdapter = new ArrayAdapter<CharSequence>(NewTicketScreen.this,android.R.layout.simple_spinner_item);
        			int selectedSpecialistPosition = -1;
        			specialistAdapter.add("--Select--");
        			size = specialists.size();
        			for(int i=0; i<size; i++)
        			{
        				String local = (String)specialists.get(i);
        				specialistAdapter.add(local);
        				if(local.equals(selectedSpecialist))
        				{
        					selectedSpecialistPosition = i;
        				}
        			}
        			specialistAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        			specialist.setAdapter(specialistAdapter);
        			specialist.setSelection(selectedSpecialistPosition+1, true);    			
        			
        		}        					        				
			}
        };	
		new AsyncLoadSpinners(NewTicketScreen.this,handler).execute();
		
		
		//Add Event Handlers
		Button save = (Button)findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener(){
			public void onClick(View button)
			{
				NewTicketScreen.this.save();
				
			}
		});
		
		Button cancel = (Button)findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener(){
			public void onClick(View button)
			{
				Toast.makeText(NewTicketScreen.this,"Ticket Creation was cancelled!!",Toast.LENGTH_LONG).show();
				Intent intent=new Intent(NewTicketScreen.this,HomeScreen.class);
				startActivity(intent);
				finish();
			}
		});
		
	}
	private void save()
	{
		
		
		//Creates a new ticket instance on the device. Once 'saved', it will be seamlessly synchronized with the Cloud
		MobileBean ticket = MobileBean.newInstance("crm_ticket_channel");
		
		EditText title = (EditText)findViewById(R.id.title);
		ticket.setValue("title", title.getText().toString());
		
		EditText comments = (EditText)findViewById(R.id.comments);
		ticket.setValue("comment", comments.getText().toString());
		
		Spinner customer = (Spinner)findViewById(R.id.customer);
		ticket.setValue("customer", ((TextView)customer.getSelectedView()).getText().toString());
		
		Spinner specialist = (Spinner)findViewById(R.id.specialist);
		ticket.setValue("specialist", ((TextView)specialist.getSelectedView()).getText().toString());
		
		//execute the create ticket usecase. It creates a new ticket in the on-device db and
		//its synchronized automagically with the Cloud
		
		Handler handler = new Handler() {
			@Override
        	public void handleMessage(Message msg) {
				int what= msg.what;
        		if(what==1){
        			Toast.makeText(NewTicketScreen.this,"Record successfully saved",1).show();
        			Intent intent=new Intent(NewTicketScreen.this,HomeScreen.class);
        			startActivity(intent);    
        			finish();
        		}
			}
		};
		new CreateTicket(NewTicketScreen.this, handler,ticket).execute();		
	}	
}