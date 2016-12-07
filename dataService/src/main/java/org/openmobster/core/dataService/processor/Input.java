/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.processor;

import java.io.Serializable;
import org.apache.mina.core.session.IoSession;

/**
 * 
 * @author openmobster@gmail.com
 */
public class Input implements Serializable
{
	/**
	 * 
	 */
	private IoSession session = null;
	
	/**
	 * 
	 */
	private String message = null;
	
	/**
	 * 
	 * @param session
	 */
	public Input(IoSession session, String message)
	{
		this.session = session;
		this.message = message;
	}
	
	/**
	 * 
	 * @return
	 */
	public IoSession getSession()
	{
		return this.session;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMessage()
	{
		return this.message;
	}
}
