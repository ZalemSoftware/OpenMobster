/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;

import java.util.List;
import java.util.ArrayList;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class SyncPackage
{
	/**
	 * 
	 */
	private List<SyncMessage> messages;	//a list a messages that were exchanged thats part of this particular package
	
	/**
	 * 
	 *
	 */
	public SyncPackage()
	{
		this.messages = new ArrayList<SyncMessage>();		
	}

	/**
	 * 
	 * @return
	 */
	public List<SyncMessage> getMessages()
	{
		return messages;
	}

	/**
	 * 
	 * @param messages
	 */
	public void setMessages(List<SyncMessage> messages)
	{
		if(messages != null)
		{
			this.messages = messages;
		}
		else
		{
			this.messages = new ArrayList<SyncMessage>();
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
		for(SyncMessage cour:this.messages)
		{
			if(cour.getMessageId().equals(messageId))
			{
				message = cour;
				break;
			}
		}
		return message;
	}	
}
