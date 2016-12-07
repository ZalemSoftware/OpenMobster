/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.android.api.sync;

import java.util.List;

import android.database.Cursor;

/**
 *
 * @author openmobster@gmail.com
 */
public interface MobileBeanCursor
{
	//navigation related operations
	public boolean isAfterLast();
	public boolean isBeforeFirst();
	public boolean isClosed();
	public boolean isFirst();
	public boolean isLast();
	
	public boolean move(int offset);
	public boolean moveToFirst();
	public boolean moveToLast();
	public boolean moveToNext();
	public boolean moveToPosition(int position);
	public boolean moveToPrevious();
	
	public MobileBean getCurrentBean();
	public List<MobileBean> all();
	public int count();
	
	//cleanup operations
	public void close();
	
	public String getChannel();
	public String getId();
	
	//system level operations
	public Cursor getSystemCursor();
}
