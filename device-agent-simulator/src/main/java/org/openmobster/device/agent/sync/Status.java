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
public final class Status
{
	/**
	 * 
	 */
	private String cmdId; //required
	private String data;	//required
	private String msgRef; //required
	private String cmdRef; //required
	private String cmd; //required
	
	private Vector targetRefs; //zero to many target refs
	private Vector sourceRefs; //zero to many source refs
	private Vector items; //zero to many items
	
	private Credential credential; //credential info in case of a successful authentication with the server
	
	public Status()
	{
		this.targetRefs = new Vector();
		this.sourceRefs = new Vector();
		this.items = new Vector();
	}

	public String getCmd()
	{
		return cmd;
	}

	public void setCmd(String cmd)
	{
		this.cmd = cmd;
	}

	public String getCmdId()
	{
		return cmdId;
	}

	public void setCmdId(String cmdId)
	{
		this.cmdId = cmdId;
	}

	public String getCmdRef()
	{
		return cmdRef;
	}

	public void setCmdRef(String cmdRef)
	{
		this.cmdRef = cmdRef;
	}

	public String getData()
	{
		return data;
	}

	public void setData(String data)
	{
		this.data = data;
	}

	public String getMsgRef()
	{
		return msgRef;
	}

	public void setMsgRef(String msgRef)
	{
		this.msgRef = msgRef;
	}

	public Vector getItems()
	{
		return items;
	}

	public void setItems(Vector items)
	{
		if(items != null)
		{
			this.items = items;
		}
		else
		{
			this.items = new Vector();
		}
	}

	public Vector getSourceRefs()
	{
		return sourceRefs;
	}

	public void setSourceRefs(Vector sourceRefs)
	{
		if(sourceRefs != null)
		{
			this.sourceRefs = sourceRefs;
		}
		else
		{
			this.sourceRefs = new Vector();
		}
	}

	public Vector getTargetRefs()
	{
		return targetRefs;
	}

	public void setTargetRefs(Vector targetRefs)
	{
		if(targetRefs != null)
		{
			this.targetRefs = targetRefs;
		}
		else
		{
			this.targetRefs = new Vector();
		}
	}
	
	/**
	 * 
	 * @param item
	 */
	public void addItem(Item item)
	{
		this.items.addElement(item);
	}
	
	/**
	 * 
	 * @param sourceRef
	 */
	public void addSourceRef(String sourceRef)
	{
		this.sourceRefs.addElement(sourceRef);
	}
	
	/**
	 * 
	 * @param targetRef
	 */
	public void addTargetRef(String targetRef)
	{
		this.targetRefs.addElement(targetRef);
	}

	public Credential getCredential() 
	{
		return credential;
	}

	public void setCredential(Credential credential) 
	{
		this.credential = credential;
	}	
}
