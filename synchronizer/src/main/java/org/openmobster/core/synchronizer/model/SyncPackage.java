/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * 
 * @author openmobster@gmail.com
 */
public class SyncPackage implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1092031444477558971L;

	/**
	 * 
	 */
	private List messages = null;	//a list a messages that were exchanged thats part of this particular package
	
	/**
	 * 
	 *
	 */
	public SyncPackage()
	{
		this.messages = new ArrayList();		
	}

	/**
	 * 
	 * @return
	 */
	public List getMessages()
	{
		return messages;
	}

	/**
	 * 
	 * @param messages
	 */
	public void setMessages(List messages)
	{
		if(messages != null)
		{
			this.messages = messages;
		}
		else
		{
			this.messages = new ArrayList();
		}
	}

	
	/**
	 * 
	 * @param message
	 */
	public void addMessage(SyncMessage message)
	{
		this.messages.add(message);
	}
	
		
	/**
	 * 
	 * @param messageId
	 * @return
	 */
	public SyncMessage findMessage(String messageId)
	{
		SyncMessage message = null;
		for(int i=0;i<this.messages.size();i++)
		{
			SyncMessage cour = (SyncMessage)this.messages.get(i);
			if(cour.getMessageId().equals(messageId))
			{
				message = cour;
				break;
			}
		}
		return message;
	}	
}
