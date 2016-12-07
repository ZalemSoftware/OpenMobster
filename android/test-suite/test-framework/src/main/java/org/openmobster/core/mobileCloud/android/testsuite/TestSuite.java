/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.testsuite;

import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import org.openmobster.core.mobileCloud.android.util.IOUtil;
import org.openmobster.core.mobileCloud.android.util.StringUtil;

import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.testsuite.ui.framework.BusyAsyncTask;

import android.content.Context;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class TestSuite 
{
	/**
	 * 
	 */
	private Vector tests;
	private Vector errors;
	private TestContext context;
	private String status;
	
	private String cloudServer;
	private String email;
	private String password;
	
	/**
	 * 
	 *
	 */
	public TestSuite()
	{
		this.tests = new Vector();
		this.errors = new Vector();
		
		try
		{
			//Load up cloud activation related configuration
			Properties properties = new Properties();
			properties.load(TestSuite.class.getResourceAsStream("/moblet-app/activation.properties"));
			
			
			this.cloudServer = properties.getProperty("cloud_server_ip");
			this.email = properties.getProperty("email");
			this.password = properties.getProperty("password");
			
			Context context = Registry.getActiveInstance().getContext();
			final Configuration conf = Configuration.getInstance(context);
			String registeredIp = conf.getServerIp();
			if(registeredIp != null && registeredIp.trim().length()>0)
			{
				this.cloudServer = registeredIp;
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param addTests
	 */
	public TestSuite(Vector addTests)
	{
		this();
		if(addTests != null)
		{
			for(int i=0,size=addTests.size(); i<size; i++)
			{
				this.addTest((Test)addTests.elementAt(i));
			}
		}
	}
	
	/**
	 * 
	 * @param test
	 */
	public void addTest(Test test)
	{
		this.tests.addElement(test);
	}
	
	public final void reportError(String error)
	{
		this.errors.addElement(error);
	}
	
	public final TestContext getContext()
	{
		return this.context;
	}
	
	public final void setContext(TestContext context)
	{
		this.context = context;
	}
	
	
	
	public String getCloudServer()
	{
		return cloudServer;
	}

	public void setCloudServer(String cloudServer)
	{
		this.cloudServer = cloudServer;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * 
	 *
	 */
	public final void execute(CommandContext commandContext)
	{
		int numberOfTests = this.tests.size();
		for(int i=0,size=numberOfTests; i<size; i++)
		{
			Test test = (Test)this.tests.elementAt(i);
			String startMessage = "Executing ["+test.getInfo()+"]";
			System.out.println(startMessage);
			
			StringBuilder buffer = new StringBuilder(startMessage+"\n\n");
			buffer.append("("+(i+1)+" out of "+numberOfTests+")");
			
			this.publishMessage(buffer.toString(), commandContext);
			try
			{
				test.setTestSuite(this);
				test.setUp();
				test.runTest();
				test.tearDown();
				test.setTestSuite(null);
			}
			catch(Exception e)
			{
				//eat this and keep going
				this.reportError("Exception:"+test.getInfo()+":"+e.toString()+":"+e.getMessage());
			}			
		}
		
		//Report the errors
		StringBuilder buffer = new StringBuilder();
		if(this.errors.size()>0)
		{
			System.out.println("Errors---------------------------------");
			for(int i=0,errSize=this.errors.size(); i<errSize; i++)
			{
				String errorStr = this.errors.elementAt(i).toString();
				buffer.append(errorStr+"\n\n\n");
			}
		}
		else
		{
			buffer.append("TestSuite successfully executed all tests...");
		}
		this.status = buffer.toString();
		
		System.out.println(status);
		commandContext.setAttribute("status", this.status);
	}
	
	public Vector getErrors()
	{
		return this.errors;
	}
	
	public final void load()
	{
		try
		{
			InputStream is = TestSuite.class.getResourceAsStream("/moblet-app/tests.cfg");
			if(is != null)
			{
				String contents = new String(IOUtil.read(is));
				if(contents != null && contents.trim().length()>0)
				{
					String[] tokens = StringUtil.tokenize(contents,"\n");
					if(tokens != null)
					{
						int length = tokens.length;
						for(int i=0; i<length; i++)
						{
							String token = tokens[i].trim();
							if(token.startsWith("#") || token.trim().length() == 0)
							{
								continue;
							}
							
							try
							{
								this.addTest((Test)Class.forName(token).newInstance());
							}
							catch(Exception e)
							{
								
							}
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public String getStatus()
	{
		return this.status;
	}
	
	private void publishMessage(String message, CommandContext commandContext)
	{
		commandContext.setAttribute("publish-message", message);
		BusyAsyncTask activeTask = (BusyAsyncTask)commandContext.getAttribute("active-task");
		activeTask.publishMessage();
	}
}
