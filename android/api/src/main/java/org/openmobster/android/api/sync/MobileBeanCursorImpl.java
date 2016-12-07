/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.android.api.sync;

import java.util.List;
import java.util.ArrayList;

import android.database.Cursor;

import org.openmobster.core.mobileCloud.android.util.GeneralTools;

/**
 *
 * @author openmobster@gmail.com
 */
final class MobileBeanCursorImpl implements MobileBeanCursor
{
	private Cursor cursor;
	private String channel;
	private String id;
	
	MobileBeanCursorImpl(String channel,Cursor cursor)
	{
		this.cursor = cursor;
		this.channel = channel;
		this.id = GeneralTools.generateUniqueId();
	}
	
	@Override
	public String getChannel()
	{
		return this.channel;
	}

	
	@Override
	public String getId()
	{
		return this.id;
	}

	@Override
	public boolean isAfterLast()
	{
		return cursor.isAfterLast();
	}

	@Override
	public boolean isBeforeFirst()
	{
		return cursor.isBeforeFirst();
	}

	@Override
	public boolean isClosed()
	{
		return cursor.isClosed();
	}

	@Override
	public boolean isFirst()
	{
		return cursor.isFirst();
	}

	@Override
	public boolean isLast()
	{
		return cursor.isLast();
	}

	@Override
	public boolean move(int offset)
	{
		return cursor.move(offset);
	}

	@Override
	public boolean moveToFirst()
	{
		return cursor.moveToFirst();
	}

	@Override
	public boolean moveToLast()
	{
		return cursor.moveToLast();
	}

	@Override
	public boolean moveToNext()
	{
		return cursor.moveToNext();
	}

	@Override
	public boolean moveToPosition(int position)
	{
		return cursor.moveToPosition(position);
	}

	@Override
	public boolean moveToPrevious()
	{
		return cursor.moveToPrevious();
	}

	@Override
	public MobileBean getCurrentBean()
	{
		int columnIndex = cursor.getColumnIndex("recordid");
		String recordid = cursor.getString(columnIndex);
		
		MobileBean mobileBean = MobileBean.readById(this.channel, recordid);
		
		return mobileBean;
	}
	
	public List<MobileBean> all()
	{
		List<MobileBean> all = new ArrayList<MobileBean>();
		if(this.cursor.getCount() == 0)
		{
			return all;
		}
		
		this.cursor.moveToFirst();
		do
		{
			MobileBean local = this.getCurrentBean();
			all.add(local);
			
			this.cursor.moveToNext();
		}while(!this.cursor.isAfterLast());
		
		return all;
	}

	@Override
	public void close()
	{
		this.cursor.close();
	}
	
	@Override
	public int count()
	{
		return this.cursor.getCount();
	}

	@Override
	public Cursor getSystemCursor()
	{
		return this.cursor;
	}
}
