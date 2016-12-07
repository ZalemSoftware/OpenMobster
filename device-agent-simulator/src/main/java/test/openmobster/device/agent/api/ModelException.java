/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.api;

/**
 * @author openmobster@gmail.com
 */
public class ModelException extends Exception 
{
	public ModelException() 
	{
		super();	
	}

	public ModelException(String message, Throwable t) 
	{
		super(message, t);	
	}

	public ModelException(String message) 
	{
		super(message);	
	}

	public ModelException(Throwable t) 
	{
		super(t);	
	}	
}
