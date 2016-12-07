/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.android.cloud.app;

import java.lang.reflect.Field;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android_native.framework.CloudService;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Context;

/**
 * 
 * @author openmobster@gmail.com
 */
public class App2 extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		try
		{
			super.onCreate(savedInstanceState);
		} 
		catch (Exception e)
		{
			e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onCreate", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
			this.showDialog(0);
		}
	}
	
	@Override
	protected void onStart()
	{
		try
		{
			super.onStart();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), "onStart", new Object[]{
				"Message:"+e.getMessage(),
				"Exception:"+e.toString()
			}));
			this.showDialog(0);
		}
	}

	@Override
	protected void onResume()
	{
		try
		{
			super.onResume();
			this.displayMainScreen();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			ErrorHandler.getInstance().handle(new SystemException(this.getClass().getName(), 
					"onResume", new Object[]{
						"Message:"+e.getMessage(),
						"Exception:"+e.toString()
					}));
		}
	}
	
	public void displayMainScreen()
	{
		try
		{
			//render the main screen
			String layoutClass = this.getPackageName()+".R$layout";
			String main = "app2";
			Class clazz = Class.forName(layoutClass);
			Field field = clazz.getField(main);
			int screenId = field.getInt(clazz);
			this.setContentView(screenId);
			
			//setup the button
			Button testActivityLifeCycle = (Button)ViewHelper.findViewById(this,"test_activity_life_cycle");
			testActivityLifeCycle.setOnClickListener(
					new OnClickListener()
					{
						public void onClick(View clicked)
						{
							Activity currentActivity = Services.getInstance().getCurrentActivity();
							
							Context context = currentActivity.getApplicationContext();
							
							currentActivity.finish();
							
							System.out.println("Context: "+context);
							System.out.println("ApplicationInfo: "+context.getApplicationInfo().packageName);
							
							ViewHelper.getOkModal(App2.this, "Test", "Activity is finished!!").show();
						}
					}
			);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
