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
	
	private List<String> targetRefs; //zero to many target refs
	private List<String> sourceRefs; //zero to many source refs
	private List<Item> items; //zero to many items
	
	private Credential credential; //credential info in case of a successful authentication with the server
	
	public Status()
	{
		this.targetRefs = new ArrayList<String>();
		this.sourceRefs = new ArrayList<String>();
		this.items = new ArrayList<Item>();
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

	public List<Item> getItems()
	{
		return items;
	}

	public void setItems(List<Item> items)
	{
		if(items != null)
		{
			this.items = items;
		}
		else
		{
			this.items = new ArrayList<Item>();
		}
	}

	public List<String> getSourceRefs()
	{
		return sourceRefs;
	}

	public void setSourceRefs(List<String> sourceRefs)
	{
		if(sourceRefs != null)
		{
			this.sourceRefs = sourceRefs;
		}
		else
		{
			this.sourceRefs = new ArrayList<String>();
		}
	}

	public List<String> getTargetRefs()
	{
		return targetRefs;
	}

	public void setTargetRefs(List<String> targetRefs)
	{
		if(targetRefs != null)
		{
			this.targetRefs = targetRefs;
		}
		else
		{
			this.targetRefs = new ArrayList<String>();
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
