/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.frameworks.mobileObject;

import java.util.Hashtable;

import org.openmobster.device.agent.frameworks.mobileObject.BeanList;
import org.openmobster.device.agent.frameworks.mobileObject.BeanListEntry;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;

/**
 * @author openmobster@gmail.com
 *
 */
public class IndexingAPIUtil 
{
	public static BeanList readList(MobileObject mobileObject,String listProperty)
	{
		BeanList list = new BeanList(listProperty);
		int arrayLength = mobileObject.getArrayLength(listProperty);		
		for(int i=0; i<arrayLength; i++)
		{
			Hashtable entryState = mobileObject.getArrayElement(list.getListProperty(), i);
			BeanListEntry entry = new BeanListEntry(i, entryState);
			list.addEntry(entry);
		}
		return list;
	}
	
	public static void saveList(MobileObject mobileObject, BeanList list)
	{
		mobileObject.clearArray(list.getListProperty());
		for(int i=0; i<list.size(); i++)
		{
			BeanListEntry entry = list.getEntryAt(i);
			mobileObject.addToArray(list.getListProperty(), entry.getProperties());
		}
		
	}
	
	public static void clearList(MobileObject mobileObject, String listProperty)
	{
		mobileObject.clearArray(listProperty);
	}
	
	public static void addBean(MobileObject mobileObject, String listProperty, BeanListEntry bean)
	{
		mobileObject.addToArray(listProperty, bean.getProperties());
	}
	
	public static void removeBean(MobileObject mobileObject, String listProperty, int elementAt)
	{
		mobileObject.removeArrayElement(listProperty, elementAt);
	}
}
