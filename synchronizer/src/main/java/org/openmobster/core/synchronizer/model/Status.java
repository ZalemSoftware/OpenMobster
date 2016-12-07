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
public class Status implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6199984610574914744L;
	
	private String cmdId = null; //required
	private String data = null;	//required
	private String msgRef = null; //required
	private String cmdRef = null; //required
	private String cmd = null; //required
	
	private List targetRefs = null; //zero to many target refs
	private List sourceRefs = null; //zero to many source refs
	private List items = null; //zero to many items
	
	private Credential credential; //not required one credential element carrying security related status
	
	public Status()
	{
		this.targetRefs = new ArrayList();
		this.sourceRefs = new ArrayList();
		this.items = new ArrayList();
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

	public List getItems()
	{
		return items;
	}

	public void setItems(List items)
	{
		if(items != null)
		{
			this.items = items;
		}
		else
		{
			this.items = new ArrayList();
		}
	}

	public List getSourceRefs()
	{
		return sourceRefs;
	}

	public void setSourceRefs(List sourceRefs)
	{
		if(sourceRefs != null)
		{
			this.sourceRefs = sourceRefs;
		}
		else
		{
			this.sourceRefs = new ArrayList();
		}
	}

	public List getTargetRefs()
	{
		return targetRefs;
	}

	public void setTargetRefs(List targetRefs)
	{
		if(targetRefs != null)
		{
			this.targetRefs = targetRefs;
		}
		else
		{
			this.targetRefs = new ArrayList();
		}
	}
	
	/**
	 * 
	 * @param item
	 */
	public void addItem(Item item)
	{
		this.items.add(item);
	}
	
	/**
	 * 
	 * @param sourceRef
	 */
	public void addSourceRef(String sourceRef)
	{
		this.sourceRefs.add(sourceRef);
	}
	
	/**
	 * 
	 * @param targetRef
	 */
	public void addTargetRef(String targetRef)
	{
		this.targetRefs.add(targetRef);
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
