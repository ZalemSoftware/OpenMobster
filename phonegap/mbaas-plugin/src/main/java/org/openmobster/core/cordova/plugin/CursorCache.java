/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cordova.plugin;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.android.api.sync.MobileBeanCursor;

/**
 *
 * @author openmobster@gmail.com
 */
final class CursorCache
{
	private static CursorCache singleton = null;
	
	private Map<String,MobileBeanCursor> cursors;
	
	private CursorCache()
	{
		this.cursors = new HashMap<String,MobileBeanCursor>();
	}
	
	static CursorCache getInstance()
	{
		if(singleton == null)
		{
			synchronized(CursorCache.class)
			{
				if(singleton == null)
				{
					singleton = new CursorCache();
				}
			}
		}
		return singleton;
	}
	//-----------------------------------------------------------------------------------------------------------------------------------------
	MobileBeanCursor find(String cursorId)
	{
		return this.cursors.get(cursorId);
	}
	
	void cache(MobileBeanCursor cursor)
	{
		this.cursors.put(cursor.getId(), cursor);
		
		cursor.moveToFirst();
		//cleanup if needed
		if(cursor.isAfterLast())
		{
			cursor.close();
			this.cursors.remove(cursor.getId());
		}
	}
	
	MobileBean currentBean(String cursorId)
	{
		MobileBeanCursor cursor = this.find(cursorId);
		if(cursor == null)
		{
			throw new IllegalStateException("Cursor Not Found");
		}
		
		MobileBean mobileBean = cursor.getCurrentBean();
		
		//Manage the cursor movement
		cursor.moveToNext();
		//cleanup if needed
		if(cursor.isAfterLast())
		{
			cursor.close();
			this.cursors.remove(cursorId);
		}
		
		return mobileBean;
	}
	
	List<MobileBean> allBeans(String cursorId)
	{
		MobileBeanCursor cursor = this.find(cursorId);
		if(cursor == null)
		{
			throw new IllegalStateException("Cursor Not Found");
		}
		
		List<MobileBean> allBeans = cursor.all();
		
		//cleanup
		cursor.close();
		this.cursors.remove(cursorId);
		
		return allBeans;
	}
}
