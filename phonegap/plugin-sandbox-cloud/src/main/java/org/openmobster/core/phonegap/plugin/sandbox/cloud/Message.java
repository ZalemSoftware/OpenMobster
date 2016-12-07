/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.phonegap.plugin.sandbox.cloud;

import java.io.Serializable;

/**
 *
 * @author openmobster@gmail.com
 */
public class Message implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1000769376074391388L;
	
	private String from;
	private String to;
	private String message;
	
	public Message()
	{
		
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
