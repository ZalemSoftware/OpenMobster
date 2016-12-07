/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.android.api.sync;

import java.util.Vector;
import java.util.Enumeration;

/**
 * BeanList is the representation an indexed property such as an array, list, etc
 * 
 * 
 * @author openmobster@gmail.com
 *
 */
public final class BeanList 
{
	private Vector<BeanListEntry> entries;
	private String listProperty;
	
	public BeanList(String listProperty)
	{
		this.listProperty = listProperty;
		this.entries = new Vector<BeanListEntry>();
	}	
	//Public API---------------------------------------------------------------------------------------------------------------------------
	/**
	 * The List expression associated with this bean property
	 * 
	 * @return the expression of the list property
	 */
	public String getListProperty()
	{
		return this.listProperty;
	}
	
	/**
	 * Provides the size of the List represented by this property
	 * 
	 * @return the size of this list property
	 */
	public int size()
	{
		return this.entries.size();
	}
	
	/**
	 * Provides an enumeration of entries in this list
	 * 
	 * @return entries of this list (BeanListEntry instances)
	 */
	public Enumeration<BeanListEntry> entries()
	{
		return this.entries.elements();
	}
	
	/**
	 * Provides the entry located at the specified index
	 * 
	 * @param index index of the entry
	 * @return the entry
	 */
	public BeanListEntry getEntryAt(int index)	
	{
		return (BeanListEntry)this.entries.get(index);
	}
	
	/**
	 * Add an entry to the list
	 * 
	 * @param entry the entry to be added
	 */
	public void addEntry(BeanListEntry entry)
	{
		entry.setListProperty(this.listProperty);
		this.entries.add(entry);
	}
}
