/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

/**
 * 
 */
package org.openmobster.core.mobileCloud.android.module.bus;

import android.content.ContentResolver;
import android.net.Uri;
import android.database.Cursor;
import android.content.Context;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

import org.openmobster.core.mobileCloud.android.service.Registry;

/**
 * @author openmobster
 *
 */
public class TestMockContentProvider extends Test
{	
	public void runTest()
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			
			ContentResolver resolver = context.getContentResolver();
			
			this.testQueryAll(resolver);
			this.testQueryById(resolver);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
	
	private void testQueryAll(ContentResolver resolver) throws Exception
	{
		Uri uri = Uri.parse(
		"content://org.openmobster.core.mobileCloud.android.remote.bus.mock");
		
		Cursor cursor = resolver.query(uri, null, null, null, null);
		if(cursor == null || cursor.getCount()==0)
		{
			assertFalse(true, "MockContentProvider Query failed!!!");
		}
		else
		{
			int mock1Index = cursor.getColumnIndex("mock1");
			int mock2Index = cursor.getColumnIndex("mock2");
			
			cursor.moveToFirst();
			do
			{
				String mock1 = cursor.getString(mock1Index);
				String mock2 = cursor.getString(mock2Index);
				
				System.out.println("------------------------------");
				System.out.println("Mock1: "+mock1);
				System.out.println("Mock2: "+mock2);
				System.out.println("------------------------------");
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
		}
	}
	
	private void testQueryById(ContentResolver resolver) throws Exception
	{
		Uri uri = Uri.parse(
		"content://org.openmobster.core.mobileCloud.android.remote.bus.mock/org.myapp.blah");
		
		Cursor cursor = resolver.query(uri, null, null, null, null);
		if(cursor == null || cursor.getCount()==0)
		{
			assertFalse(true, "MockContentProvider Query failed!!!");
		}
		else
		{
			int mock1Index = cursor.getColumnIndex("mock1");
			int mock2Index = cursor.getColumnIndex("mock2");
			
			cursor.moveToFirst();
			do
			{
				String mock1 = cursor.getString(mock1Index);
				String mock2 = cursor.getString(mock2Index);
				
				System.out.println("------------------------------");
				System.out.println("Mock1: "+mock1);
				System.out.println("Mock2: "+mock2);
				System.out.println("------------------------------");
				
				cursor.moveToNext();
			}while(!cursor.isAfterLast());
		}
	}
}
