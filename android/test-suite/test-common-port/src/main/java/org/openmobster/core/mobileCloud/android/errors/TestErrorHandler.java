/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.errors;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.testsuite.Test;

/**
 * @author openmobster@gmail.com
 */
public class TestErrorHandler extends Test 
{
	
	@Override
	public void setUp()
	{
		try
		{
			Context context = (Context)this.getTestSuite().getContext().
			getAttribute("android:context");
			
			Database.getInstance(context).dropTable(Database.system_errors);
			Database.getInstance(context).createTable(Database.system_errors);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void runTest()
	{
		try
		{
			Context context = (Context)this.getTestSuite().getContext().
			getAttribute("android:context");
			
			this.test(context);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}

	private void test(Context context) throws Exception
	{	
		//Handle the Exception
		for(int i=0; i<10; i++)
		{
			RuntimeException exception = new RuntimeException("I AM: "+i);
			ErrorHandler.getInstance().handle(exception);
		}
		
		//Generate a Report
		String report = ErrorHandler.getInstance().generateReport();
		System.out.println(report);
		assertTrue(report !=null && report.trim().length()>0,this.getInfo()+"/test/assertingReport");
		
		//ClearAll
		ErrorHandler.getInstance().clearAll();
		
		//Generate a Report
		report = ErrorHandler.getInstance().generateReport();
		assertTrue(report==null || report.trim().length()==0,this.getInfo()+"/test/ReportMustBeEmpty");
	}
}
