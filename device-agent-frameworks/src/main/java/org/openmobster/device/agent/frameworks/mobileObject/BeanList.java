/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.frameworks.mobileObject;

import java.util.Vector;
import java.util.Enumeration;

/**
 * @author openmobster@gmail.com
 *
 */
public final class BeanList 
{
	private Vector entries;
	private String listProperty;
	
	public BeanList(String listProperty)
	{
		this.listProperty = listProperty;
		this.entries = new Vector();
	}	
	//Public API---------------------------------------------------------------------------------------------------------------------------
	public String getListProperty()
	{
		return this.listProperty;
	}
	
	public int size()
	{
		return this.entries.size();
	}
	
	public Enumeration entries()
	{
		return this.entries.elements();
	}
	
	public BeanListEntry getEntryAt(int index)	
	{
		return (BeanListEntry)this.entries.elementAt(index);
	}
	
	public void addEntry(BeanListEntry entry)
	{
		entry.setListProperty(this.listProperty);
		this.entries.addElement(entry);
	}
}
