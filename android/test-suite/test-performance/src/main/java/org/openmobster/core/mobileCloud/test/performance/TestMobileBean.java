/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.test.performance;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.android.api.sync.BeanList;
import org.openmobster.android.api.sync.BeanListEntry;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.RemoteCommand;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

import android.app.Activity;
import android.content.Context;

/**
 * The MVC AsyncCommand. The 'AsyncCommand' is used to perform actions asynchronously (Ajax from the web world). 
 * 
 * This particular AsyncCommand, invokes the 'GetDetails' service in the Cloud and gets a fully populated Email instance for display.
 * 
 * @author openmobster@gmail.com
 *
 */
public final class TestMobileBean implements RemoteCommand
{
	//Executes on the UI thread. All UI related operations are safe here. It is invoked to perform some pre-action UI related tasks.
	public void doViewBefore(CommandContext commandContext)
	{	
		//Nothing to do
	}

	//This does not execute on the UI thread. When this method is invoked, the UI thread is freed up, so that its not frozen while the 
	//information is being loaded from the Cloud
	public void doAction(CommandContext commandContext) 
	{
		try
		{
			MobileObjectDatabase db = MobileObjectDatabase.getInstance();
			//db.deleteAll("perfbeans");
			
			//create perfbeans
			if(!MobileBean.isBooted("perfbeans"))
			{
				System.out.println("Adding beans.........");
				
				long startTime = System.currentTimeMillis();
				for(int i=0; i<1000; i++)
				{
					MobileBean perfBean = MobileBean.newInstance("perfbeans");
					
					//set some state
					perfBean.setValue("name", "name://"+i);
					perfBean.setValue("title", "title://"+i);
					
					//add an array
					BeanList fruits = new BeanList("fruits");
					for(int j=0; j<5; j++)
					{
						BeanListEntry fruit = new BeanListEntry();
						fruit.setValue("fruit://"+j);
						fruits.addEntry(fruit);
					}
					perfBean.saveList(fruits);
					
					//save the bean
					perfBean.perfSave();
					
					System.out.println("Added Bean # "+i);
				}
				long endTime = System.currentTimeMillis();
				System.out.println("WriteTime: "+(endTime-startTime));
			}
			//Just to load the cache
			MobileBean.readAll("perfbeans");
			
			//read all the beans
			long readStartTime = System.currentTimeMillis();
			
			GenericAttributeManager criteria = new GenericAttributeManager();
			criteria.setAttribute("name", "name://19");
			criteria.setAttribute("title", "title://19");
			
			//MobileBean[] all = MobileBean.readAll("perfbeans");
			//MobileBean[] all = MobileBean.queryByEqualsAll("perfbeans", criteria);
			MobileBean[] all = MobileBean.queryByNotEqualsAll("perfbeans", criteria);
			//MobileBean[] all = MobileBean.queryByContainsAll("perfbeans", criteria);
			//MobileBean[] all = MobileBean.queryByEqualsAtleastOne("perfbeans", criteria);
			//MobileBean[] all = MobileBean.queryByNotEqualsAtleastOne("perfbeans", criteria);
			//MobileBean[] all = MobileBean.queryByContainsAtleastOne("perfbeans", criteria);
			
			long readEndTime = System.currentTimeMillis();
			System.out.println("ReadTime: "+(readEndTime-readStartTime));
			System.out.println("Number of Beans read: "+all.length);
			
			/*for(MobileBean local:all)
			{
				String name = local.getValue("name");
				String title = local.getValue("title");
				
				System.out.println("Name: "+name);
				System.out.println("Title: "+title);
				
				//Output the fruits
				for(int i=0; i<5; i++)
				{
					System.out.println("Fruit://"+i+":"+local.getValue("fruits["+i+"]"));
				}
				
				System.out.println("*******************************");
			}*/
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			
			//throw an AppException. If this happens, the doViewError will be invoked to alert the user with an error message
			AppException appe = new AppException();
			appe.setMessage(e.getMessage());
			
			//Record this error in the Cloud Error Log
			ErrorHandler.getInstance().handle(appe);
			
			throw appe;
		}
	}	
	
	//Executes on the UI thread. All UI operations are safe. It is invoked after the doAction is executed without any errors.
	//From an Ajax standpoint, consider this invocation as the UI callback
	public void doViewAfter(CommandContext commandContext)
	{

	}
	
	//Executes on the UI thread. All UI operations are safe. This method is invokes if there is an error during the doAction execution.
	//From an Ajax standpoint, consider this invocation as a UI callback
	public void doViewError(CommandContext commandContext)
	{
		//Shows an Error Alert
		Activity currentActivity = Services.getInstance().getCurrentActivity();
		ViewHelper.getOkModal(currentActivity, "App Error", 
		commandContext.getAppException().getMessage()).
		show();
	}
}
