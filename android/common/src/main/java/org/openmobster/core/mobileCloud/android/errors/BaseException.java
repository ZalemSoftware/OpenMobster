/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.errors;

/**
 * @author openmobster@gmail.com
 *
 */
public abstract class BaseException extends Exception
{
	/**
	 * 
	 */
	protected String className; //non-null
	
	/**
	 * 
	 */
	protected String method; //non-null
	
	/**
	 * 
	 */
	protected Object[] parameters; //nullable
	
	
	/**
	 * 
	 * @param className
	 * @param method
	 * @param parameters
	 */
	public BaseException(String className, String method, Object[] parameters)
	{
		super();
		
		this.className = className;
		this.method = method;
		this.parameters = parameters;
	}
	
	/**
	 * 
	 */
	public String getMessage()
	{		
		StringBuffer buffer = new StringBuffer();
		buffer.append("Class="+this.className+"\n");
		buffer.append("Method="+this.method+"\n");
		
		if(this.parameters != null && this.parameters.length>0)
		{
			for(int i=0; i<parameters.length; i++)
			{
				buffer.append("Param("+i+")="+parameters[i].toString()+"\n");
			}
		}
		
		return buffer.toString();
	}
	
	/**
	 * 
	 */
	public String toString()
	{
		return this.getMessage();
	}

	/**
	 * 
	 * @return
	 */
	public String getClassName() 
	{
		return className;
	}

	/**
	 * 
	 * @return
	 */
	public String getMethod() 
	{
		return method;
	}

	/**
	 * 
	 * @return
	 */
	public Object[] getParameters() 
	{
		return parameters;
	}	
}
