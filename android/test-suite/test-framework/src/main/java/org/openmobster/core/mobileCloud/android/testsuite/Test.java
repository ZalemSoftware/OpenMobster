/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.testsuite;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public abstract class Test 
{
	private TestSuite suite;
	
	/**
	 * 
	 *
	 */
	public void setUp()
	{
		
	}
	
	/**
	 * 
	 *
	 */
	public void tearDown()
	{
		
	}
	
	/**
	 * 
	 *
	 */
	public Test()
	{
		
	}
	
	/**
	 * 
	 * @param suite
	 */
	public final void setTestSuite(TestSuite suite)
	{
		this.suite = suite;
	}
	
	public final TestSuite getTestSuite()
	{
		return this.suite;
	}
	
	/**
	 * 
	 * @param message
	 * @param expected
	 * @param context
	 */
	public final void assertEquals(String message, String expected, String context)
	{
		String errorMessage = "AssertionError:Actual Value="+message+", Expected Value="+expected+":"+context;
		if((message == null || expected == null) && message != expected)
		{
			this.suite.reportError(errorMessage);
		}
		else if(!message.equals(expected))
		{
			this.suite.reportError(errorMessage);
		}
	}
	
	/**
	 * 
	 * @param expected
	 * @param context
	 */
	public final void assertNotNull(Object actualValue, String context)
	{
		String errorMessage = "AssertionError: Value is Null:"+context;
		if(actualValue == null)
		{
			this.suite.reportError(errorMessage);
		}
	}
	
	/**
	 * 
	 * @param expected
	 * @param context
	 */
	public final void assertNull(Object actualValue, String context)
	{
		String errorMessage = "AssertionError: Value is Not Null:"+context;
		if(actualValue != null)
		{
			this.suite.reportError(errorMessage);
		}
	}
	
	/**
	 * 
	 * @param expected
	 * @param context
	 */
	public final void assertTrue(boolean actualValue, String context)
	{
		String errorMessage = "AssertionError: Value is False:"+context;
		if(!actualValue)
		{
			this.suite.reportError(errorMessage);
		}
	}
	
	/**
	 * 
	 * @param expected
	 * @param context
	 */
	public final void assertFalse(boolean actualValue, String context)
	{
		String errorMessage = "AssertionError: Value is True:"+context;
		if(actualValue)
		{
			this.suite.reportError(errorMessage);
		}
	}
	
	/**
	 * 
	 *
	 */
	public abstract void runTest();
	
	/**
	 * 
	 * @return
	 */
	public String getInfo()
	{
		return this.getClass().getName();
	}
}
