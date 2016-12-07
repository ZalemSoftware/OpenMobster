/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common.errors;

import org.apache.log4j.Logger;

/**
 * @author openmobster@gmail.com
 *
 */
public final class ErrorHandler 
{
	private static Logger log = Logger.getLogger(ErrorHandler.class);
	
	private static ErrorHandler singleton;
	
	private ErrorHandler()
	{
		
	}
	
	/**
	 * Returns the singleton instance of the system wide ErrorHandler
	 * 
	 * @return
	 */
	public static ErrorHandler getInstance()
	{
		if(singleton == null)
		{
			synchronized(ErrorHandler.class)
			{
				if(singleton == null)
				{
					singleton = new ErrorHandler();
				}
			}
		}
		return singleton;
	}
	
	/**
	 * Properly processes the Exception that occurred in the system
	 * 
	 * @param e
	 */
	public void handle(Throwable e)
	{
		log.error(this, e);
	}
}
