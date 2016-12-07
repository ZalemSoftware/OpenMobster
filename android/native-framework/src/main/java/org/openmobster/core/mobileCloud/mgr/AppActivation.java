/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.mgr;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

/**
 *
 * @author openmobster@gmail.com
 */
public final class AppActivation
{
	private Activity activity;
	private AppActivationCallback callback;
	
	private AppActivation(Activity activity,AppActivationCallback callback)
	{
		this.activity = activity;
		this.callback = callback;
	}
	
	public static AppActivation getInstance(Activity activity,AppActivationCallback callback)
	{
		return new AppActivation(activity,callback);
	}
	
	public static AppActivation getInstance(Activity activity)
	{
		return new AppActivation(activity,null);
	}
	
	public void start()
	{
		Context context = Registry.getActiveInstance().
		getContext();
		Configuration conf = Configuration.getInstance(context);
		
		EditText serverField = new EditText(this.activity);
		serverField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
		
		GenericAttributeManager wizardState = new GenericAttributeManager();
						
		//Setup the Activation Dialog
		wizardState.setAttribute("conf", conf);
		
		
		wizardState.setAttribute("currentLabel", "Server");
		wizardState.setAttribute("currentTextField", serverField);
		new ActivationWizard(wizardState).start();
	}
	
	private class ActivationWizard implements DialogInterface.OnClickListener
	{
		private AlertDialog wizard = null;
		private GenericAttributeManager wizardState;
		
		ActivationWizard(GenericAttributeManager wizardState)
		{
			Context context = Registry.getActiveInstance().
			getContext();
			Activity currentActivity = AppActivation.this.activity;
			
			this.wizardState = wizardState;
			Configuration conf = Configuration.getInstance(context);
			
			this.wizard = new AlertDialog.Builder(currentActivity).
	    	setCancelable(false).
	    	create();
			this.wizard.setButton(DialogInterface.BUTTON_POSITIVE, "OK", this);			
			this.wizard.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", this);
			
			String label = (String)this.wizardState.getAttribute("currentLabel");
			this.wizard.setTitle(label);
			
			EditText textField = (EditText)this.wizardState.getAttribute("currentTextField");
			this.wizard.setView(textField);
			
			if(label.equalsIgnoreCase("server"))
			{
				String value = conf.getServerIp();
				if(value != null && value.trim().length()>0)
				{
					textField.setText(value);
				}
			}
			else if(label.equalsIgnoreCase("port"))
			{
				/*String value = conf.decidePort();
				if(value != null && value.trim().length()>0)
				{
					textField.setText(value);
				}
				else
				{
					textField.setText("1502"); //default port
				}*/
				textField.setText("1502"); //default cleartext port
			}
			else if(label.equalsIgnoreCase("email"))
			{
				String value = conf.getEmail();
				if(value != null && value.trim().length()>0)
				{
					textField.setText(value);
				}
			}
		}
		
		private void start()
		{
			this.wizard.show();
		}
		
		public void onClick(DialogInterface dialog, int status)
		{
			Activity currentActivity = AppActivation.this.activity;
			
			if(status == DialogInterface.BUTTON_NEGATIVE)
			{
				Configuration conf = (Configuration)this.wizardState.getAttribute("conf");
				
				//cancel the wizard
				if(!conf.isActive())
				{
					ViewHelper.getOkModalWithCloseApp(currentActivity, "System Error", 
							"For security reasons, your App must be activated with the Cloud")
					.show();
				}
				dialog.cancel();
			}
			else
			{
				Configuration conf = (Configuration)this.wizardState.getAttribute("conf");
				String currentLabel = (String)this.wizardState.getAttribute("currentLabel");
				EditText currentTextField = (EditText)this.wizardState.getAttribute("currentTextField");
				boolean isActive = conf.isActive();
				
				//Validation
				if(currentLabel.equalsIgnoreCase("server"))
				{
					String server = currentTextField.getText().toString();
					if(server == null || server.trim().length()==0)
					{
						ViewHelper.getOkModal(currentActivity, "Validation Error", "Server is required. Please try again.").show();
						if(!isActive)
						{
							ViewHelper.getOkModalWithCloseApp(currentActivity, "System Error", 
									"For security reasons, your App must be activated with the Cloud")
							.show();
						}
						return;
					}
				}
				
				if(currentLabel.equalsIgnoreCase("port"))
				{
					String port = currentTextField.getText().toString();
					if(port == null || port.trim().length()==0)
					{
						ViewHelper.getOkModal(currentActivity, "Validation Error", "Port is required. Please try again.").show();
						if(!isActive)
						{
							ViewHelper.getOkModalWithCloseApp(currentActivity, "System Error", 
									"For security reasons, your App must be activated with the Cloud")
							.show();
						}
						return;
					}
				}
				
				if(currentLabel.equalsIgnoreCase("email"))
				{
					String email = currentTextField.getText().toString();
					if(email == null || email.trim().length()==0)
					{
						ViewHelper.getOkModal(currentActivity, "Validation Error", "Email is required. Please try again.").show();
						if(!isActive)
						{
							ViewHelper.getOkModalWithCloseApp(currentActivity, "System Error", 
									"For security reasons, your App must be activated with the Cloud")
							.show();
						}
						return;
					}
				}
				
				if(currentLabel.equalsIgnoreCase("password"))
				{
					String password = currentTextField.getText().toString();
					if(password == null || password.trim().length()==0)
					{
						ViewHelper.getOkModal(currentActivity, "Validation Error", "Password is required. Please try again.").show();
						if(!isActive)
						{
							ViewHelper.getOkModalWithCloseApp(currentActivity, "System Error", 
									"For security reasons, your App must be activated with the Cloud")
							.show();
						}
						return;
					}
				}
				
				//go to the next screen
				dialog.cancel();
				if(currentLabel.equalsIgnoreCase("server"))
				{					
					EditText portField = new EditText(currentActivity);
					portField.setInputType(InputType.TYPE_CLASS_NUMBER);	
					
					this.wizardState.setAttribute("currentLabel", "Port");
					this.wizardState.setAttribute("currentTextField", portField);
					this.wizardState.setAttribute("server", currentTextField.getText().toString());
					
					new ActivationWizard(this.wizardState).start();
				}
				else if(currentLabel.equalsIgnoreCase("port"))
				{
					EditText emailField = new EditText(currentActivity);
					emailField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);	
					
					this.wizardState.setAttribute("currentLabel", "Email");
					this.wizardState.setAttribute("currentTextField", emailField);
					this.wizardState.setAttribute("port", currentTextField.getText().toString());
					
					new ActivationWizard(this.wizardState).start();
				}
				else if(currentLabel.equalsIgnoreCase("email"))
				{
					EditText passwordField = new EditText(currentActivity);
					passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					
					this.wizardState.setAttribute("currentLabel", "Password");
					this.wizardState.setAttribute("currentTextField", passwordField);
					this.wizardState.setAttribute("email", currentTextField.getText().toString());
					
					new ActivationWizard(this.wizardState).start();
				}
				else if(currentLabel.equalsIgnoreCase("password"))
				{
					String server = (String)wizardState.getAttribute("server");
					String port = (String)wizardState.getAttribute("port");
					String email = (String)wizardState.getAttribute("email");
					String password = currentTextField.getText().toString();
					
					CommandContext commandContext = new CommandContext();
					
					commandContext.setAttribute("task", new AppActivationTask(AppActivation.this.activity,
					AppActivation.this.callback));
					commandContext.setAttribute("server", server);
					commandContext.setAttribute("email", email);
					commandContext.setAttribute("password", password);
					commandContext.setAttribute("port", port);
					
					TaskExecutor taskExecutor = new TaskExecutor("App Activation","App Activation in Progress",
							null,currentActivity);
					taskExecutor.execute(commandContext);
				}
			}
		}
	}
}
