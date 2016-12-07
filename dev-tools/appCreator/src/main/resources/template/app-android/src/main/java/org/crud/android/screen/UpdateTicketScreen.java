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
import <appCreator.android.main.groupId>.command.UpdateTicket;

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
 * Controls the 'Ticket Modification' screen.
 * 
 * The UI presents a simple form with pre-populated data, and an 'OK' to save the changes, and 'Cancel' button to cancel the
 * operation
 * 
 * @author openmobster@gmail.com
 */

public class UpdateTicketScreen extends Activity{
	
	MobileBean ticket=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_ticket);
		
		Intent intent= getIntent();
		int index=intent.getIntExtra("SelectedIndex",-1);
		
		if(index==-1){
			return;
		}
		ticket=HomeScreen.activeBeans[index];
		
		EditText title = (EditText)findViewById(R.id.title);
		title.setText(ticket.getValue("title"));
		
		EditText comments = (EditText)findViewById(R.id.comments);
		comments.setText(ticket.getValue("comment"));
		
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
        			if(ticket != null)
        			{
        				selectedCustomer = ticket.getValue("customer");
        				selectedSpecialist = ticket.getValue("specialist");
        			}
        			
        			//Load the specialist customers
        			Spinner customer = (Spinner)findViewById(R.id.customer);
        			ArrayAdapter<CharSequence> customerAdapter = new ArrayAdapter<CharSequence>(UpdateTicketScreen.this,android.R.layout.simple_spinner_item);
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
        			ArrayAdapter<CharSequence> specialistAdapter = new ArrayAdapter<CharSequence>(UpdateTicketScreen.this,android.R.layout.simple_spinner_item);
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
		new AsyncLoadSpinners(UpdateTicketScreen.this,handler).execute();
        
		Button save = (Button)findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener(){
			public void onClick(View button)
			{
				UpdateTicketScreen.this.save(ticket);
			}
		});
		
		Button cancel = (Button)findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener(){
			public void onClick(View button)
			{
				Toast.makeText(UpdateTicketScreen.this,"Ticket Update was cancelled!!",Toast.LENGTH_LONG).show();
				Intent intent=new Intent(UpdateTicketScreen.this,HomeScreen.class);
				startActivity(intent);
				finish();
			}
		});
		
	}
	private void save(MobileBean ticket)
	{
		
		//Update the state of this ticket with the newly modified data from the user
		EditText title = (EditText)findViewById(R.id.title);
		ticket.setValue("title", title.getText().toString());
		
		EditText comments = (EditText)findViewById(R.id.comments);
		ticket.setValue("comment", comments.getText().toString());
		
		Spinner customer = (Spinner)findViewById(R.id.customer);
		ticket.setValue("customer", ((TextView)customer.getSelectedView()).getText().toString());
		
		Spinner specialist = (Spinner)findViewById(R.id.specialist);
		ticket.setValue("specialist", ((TextView)specialist.getSelectedView()).getText().toString());
		
		Handler handler = new Handler() {
			@Override
        	public void handleMessage(Message msg) {
				int what= msg.what;
        		if(what==1){
        			Toast.makeText(UpdateTicketScreen.this,"Record successfully updated",1).show();
        			Intent intent=new Intent(UpdateTicketScreen.this,HomeScreen.class);
        			startActivity(intent);
        			finish();
        		}        					        				
			}
        };	
		new UpdateTicket(this, handler,ticket).execute();		
	}
}