/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.sync;

import java.util.Vector;

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
	private Vector messages;	//a list a messages that were exchanged thats part of this particular package
	
	/**
	 * 
	 *
	 */
	public SyncPackage()
	{
		this.messages = new Vector();		
	}

	/**
	 * 
	 * @return
	 */
	public Vector getMessages()
	{
		return messages;
	}

	/**
	 * 
	 * @param messages
	 */
	public void setMessages(Vector messages)
	{
		if(messages != null)
		{
			this.messages = messages;
		}
		else
		{
			this.messages = new Vector();
		}
	}

	
	/**
	 * 
	 * @param message
	 */
	public void addMessage(SyncMessage message)
	{
		this.messages.addElement(message);
	}
	
		
	/**
	 * 
	 * @param messageId
	 * @return
	 */
	public SyncMessage findMessage(String messageId)
	{
		SyncMessage message = null;
		for(int i=0,size=this.messages.size();i<size;i++)
		{
			SyncMessage cour = (SyncMessage)this.messages.elementAt(i);
			if(cour.getMessageId().equals(messageId))
			{
				message = cour;
				break;
			}
		}
		return message;
	}	
}
