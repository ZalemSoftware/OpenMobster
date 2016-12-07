/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api;

import java.util.List;

import org.openmobster.android.api.sync.*;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestCursorQueries extends AbstractAPITest 
{
	public void runTest()
	{		
		try
		{
			this.testSortByProperty();
			
			this.testQueryByProperty();
			
			this.testSearchByMatchAll();
			
			this.testSearchByMatchAtleastOne();
			
			this.testCursorAll();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	private void testSortByProperty() throws Exception
	{	
		MobileBeanCursor cursor = MobileBean.sortByProperty("queryChannel", "from", true);
		cursor.moveToFirst();
		do
		{
			MobileBean bean = cursor.getCurrentBean();
			
			System.out.println("Bean Id: "+bean.getId());
			System.out.println("From: "+bean.getValue("from"));
			System.out.println("****************************");
			
			cursor.moveToNext();
		}while(!cursor.isAfterLast());
		cursor.close();
		
		cursor = MobileBean.sortByProperty("queryChannel", "from", false);
		cursor.moveToFirst();
		do
		{
			MobileBean bean = cursor.getCurrentBean();
			
			System.out.println("Bean Id: "+bean.getId());
			System.out.println("From: "+bean.getValue("from"));
			System.out.println("****************************");
			
			cursor.moveToNext();
		}while(!cursor.isAfterLast());
		cursor.close();
	}
	
	private void testQueryByProperty() throws Exception
	{	
		MobileBeanCursor cursor = MobileBean.queryByProperty("queryChannel", "message.to", "4/message/to");
		
		int counter = 0;
		cursor.moveToFirst();
		do
		{
			MobileBean bean = cursor.getCurrentBean();
			
			System.out.println("Bean Id: "+bean.getId());
			System.out.println("From: "+bean.getValue("from"));
			System.out.println("To: "+bean.getValue("to"));
			System.out.println("Message/To: "+bean.getValue("message.to"));
			System.out.println("Message/From: "+bean.getValue("message.from"));
			System.out.println("****************************");
			
			counter++;
			cursor.moveToNext();
		}while(!cursor.isAfterLast());
		cursor.close();
		
		this.assertTrue(counter==1, this.getInfo()+"/testQueryByProperty/OnlyOneBeanShouldBeFound");
	}
	
	private void testSearchByMatchAll() throws Exception
	{
		GenericAttributeManager criteria = new GenericAttributeManager();
		criteria.setAttribute("to", "0/to");
		criteria.setAttribute("message.from", "0/message/from");
		
		MobileBeanCursor cursor = MobileBean.searchByMatchAll("queryChannel", criteria);
		int counter = 0;
		cursor.moveToFirst();
		do
		{
			MobileBean bean = cursor.getCurrentBean();
			
			System.out.println("Bean Id: "+bean.getId());
			System.out.println("From: "+bean.getValue("from"));
			System.out.println("To: "+bean.getValue("to"));
			System.out.println("Message/To: "+bean.getValue("message.to"));
			System.out.println("Message/From: "+bean.getValue("message.from"));
			System.out.println("****************************");
			
			counter++;
			cursor.moveToNext();
		}while(!cursor.isAfterLast());
		cursor.close();
		
		this.assertTrue(counter==1, this.getInfo()+"/testSearchByMatchAll/OnlyOneBeanShouldBeFound");
		
		criteria = new GenericAttributeManager();
		criteria.setAttribute("to", "0/to");
		criteria.setAttribute("message.from", "4/message/from");
		
		cursor = MobileBean.searchByMatchAll("queryChannel", criteria);
		this.assertTrue(cursor.count()==0, this.getInfo()+"/testSearchByMatchAll/NothingShouldBeFound");
		cursor.close();
	}
	
	private void testSearchByMatchAtleastOne() throws Exception
	{
		GenericAttributeManager criteria = new GenericAttributeManager();
		
		criteria = new GenericAttributeManager();
		criteria.setAttribute("to", "0/to");
		criteria.setAttribute("message.from", "4/message/from");
		
		MobileBeanCursor cursor = MobileBean.searchByMatchAtleastOne("queryChannel", criteria);
		this.assertTrue(cursor.count()==2, this.getInfo()+"/testSearchByMatchAtleastOne/TwoBeansShouldBeFound");
		cursor.close();
	}
	
	private void testCursorAll() throws Exception
	{
		GenericAttributeManager criteria = new GenericAttributeManager();
		
		criteria = new GenericAttributeManager();
		criteria.setAttribute("to", "0/to");
		criteria.setAttribute("message.from", "4/message/from");
		
		MobileBeanCursor cursor = MobileBean.searchByMatchAtleastOne("queryChannel", criteria);
		this.assertTrue(cursor.count()==2, this.getInfo()+"/testSearchByMatchAtleastOne/TwoBeansShouldBeFound");
		
		cursor.moveToFirst();
		do
		{
			MobileBean bean = cursor.getCurrentBean();
			
			System.out.println("Bean Id: "+bean.getId());
			System.out.println("From: "+bean.getValue("from"));
			System.out.println("To: "+bean.getValue("to"));
			System.out.println("Message/To: "+bean.getValue("message.to"));
			System.out.println("Message/From: "+bean.getValue("message.from"));
			System.out.println("****************************");
			
			cursor.moveToNext();
		}while(!cursor.isAfterLast());
		
		List<MobileBean> all = cursor.all();
		this.assertTrue(all.size()==2, this.getInfo()+"/testSearchByMatchAtleastOne/all/TwoBeansShouldBeFound");
		
		cursor.close();
	}
}
