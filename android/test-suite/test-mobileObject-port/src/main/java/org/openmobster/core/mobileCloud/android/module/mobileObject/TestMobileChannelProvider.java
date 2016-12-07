/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.mobileObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.openmobster.core.mobileCloud.android.testsuite.Test;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

/**
 * @author openmobster@gmail.com
 */
public class TestMobileChannelProvider extends Test 
{	
	@Override
	public void runTest()
	{
		try
		{
			//cleanup
			this.testDeleteAll();
			
			Set<String> ids = new HashSet<String>();
			for(int i=0; i<5; i++)
			{
				String id = this.testInsert(i);
				ids.add(id);
			}
						
			this.testQueryAll();
			
			//test query interface
			this.testQueryByEqualsAll();
			this.testQueryByEqualsAtleastOne();
			this.testQueryByNotEqualsAll();
			this.testQueryByNotEqualsAtleastOne();
			this.testQueryByContainsAll();
			this.testQueryByContainsAtleastOne();
			
			String id = ids.iterator().next();
			this.testQuery(id);
			this.testUpdate(id);
			this.testDelete(id);
			
			this.testDeleteAll();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
	
	private String testInsert(int index)
	{
		MobileObject mobileObject = new MobileObject();
		mobileObject.setStorageId("email");
		mobileObject.setCreatedOnDevice(false);
		mobileObject.setLocked(false);
		mobileObject.setProxy(false);
		mobileObject.setValue("to", "to://"+index);
		mobileObject.setValue("from", "from");
		mobileObject.setValue("subject", "subject://"+index);
		
		String id = MobileObjectDatabase.getInstance().
		create(mobileObject);
		assertNotNull(id,"testInsert://IdMustNotBeNull");
						
		return id;
	}
	
	private void testQueryAll()
	{
		Set<MobileObject> all = MobileObjectDatabase.getInstance().readAll("email");		
		assertTrue(all!=null && !all.isEmpty(),"testQueryAll://objects_not_found");
		assertEquals(""+all.size(),"5","testQueryAll://#_of_objects_mismatch");
		
		for(MobileObject mo:all)
		{
			System.out.println("--------------------------------------------------");
			System.out.println("RecordId: "+mo.getRecordId());
			System.out.println("Channel: "+mo.getStorageId());
			System.out.println("To: "+mo.getValue("to"));
			System.out.println("From: "+mo.getValue("from"));
			System.out.println("Subject: "+mo.getValue("subject"));
			System.out.println("--------------------------------------------------");
		}		
	}
	
	private void testQuery(String recordId)
	{
		MobileObject mobileObject = MobileObjectDatabase.getInstance().
		read("email", recordId);
		
		this.assertEquals(mobileObject.getRecordId(), 
		recordId, "testQuery://IdMisMatch");
	}
	
	private void testUpdate(String recordId)
	{
		MobileObjectDatabase database = MobileObjectDatabase.getInstance();
		MobileObject mobileObject = database.read("email", recordId);
		
		mobileObject.setProxy(true);
		database.update(mobileObject);
			
		mobileObject = database.read("email", recordId);		
		assertTrue(mobileObject.isProxy(),"testUpdate://isProxyMismatch");
	}
	
	private void testDelete(String recordId)
	{
		MobileObjectDatabase database = MobileObjectDatabase.getInstance();
		
		MobileObject mobileObject = database.read("email", recordId);
		database.delete(mobileObject);
		
		mobileObject = database.read("email", recordId);
		assertNull(mobileObject,"testDelete://RecordNotDeleted");
	}
	
	private void testDeleteAll()
	{
		MobileObjectDatabase database = MobileObjectDatabase.getInstance();
		database.deleteAll("email");
		
		Set<MobileObject> all = database.readAll("email");	
		assertTrue(all == null || all.isEmpty(),"testDeleteAll://ChannelShouldBeEmpty");
	}
	
	private void testQueryByEqualsAll()
	{
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.AND));
		
		List<LogicExpression> expressions = new ArrayList<LogicExpression>();
		input.setAttribute("expressions", expressions);
		
		String lhs = "to";
		String rhs = "to://2";
		expressions.add(LogicExpression.
		createInstance(lhs, rhs, LogicExpression.OP_EQUALS));
		
		lhs = "from";
		rhs = "from";
		expressions.add(LogicExpression.
		createInstance(lhs, rhs, LogicExpression.OP_EQUALS));
		
		Set<MobileObject> result = MobileObjectDatabase.getInstance().
				query("email", input);
		
		int number_of_objects_found = result.size();
		this.assertEquals(""+number_of_objects_found, "1", this.getInfo()+"/testQueryByEquals/OneObjectShouldBeFound");
		
		for(MobileObject mo:result)
		{
			System.out.println("-------------------testQueryByEqualsAll-------------------------------");
			System.out.println("RecordId: "+mo.getRecordId());
			System.out.println("Channel: "+mo.getStorageId());
			System.out.println("To: "+mo.getValue("to"));
			System.out.println("From: "+mo.getValue("from"));
			System.out.println("Subject: "+mo.getValue("subject"));
			System.out.println("-------------------------------------------------------------------");
		}
	}
	
	private void testQueryByEqualsAtleastOne()
	{
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.OR));
		
		List<LogicExpression> expressions = new ArrayList<LogicExpression>();
		input.setAttribute("expressions", expressions);
		
		String lhs = "to";
		String rhs = "to://2";
		expressions.add(LogicExpression.
		createInstance(lhs, rhs, LogicExpression.OP_EQUALS));
		
		lhs = "from";
		rhs = "from";
		expressions.add(LogicExpression.
		createInstance(lhs, rhs, LogicExpression.OP_EQUALS));
		
		Set<MobileObject> result = MobileObjectDatabase.getInstance().
				query("email", input);
		
		int number_of_objects_found = result.size();
		this.assertEquals(""+number_of_objects_found, "5", this.getInfo()+"/testQueryByEqualsAtleastOne/AllObjectsShouldBeFound");
		
		for(MobileObject mo:result)
		{
			System.out.println("-------------------testQueryByEqualsAtleastOne-------------------------------");
			System.out.println("RecordId: "+mo.getRecordId());
			System.out.println("Channel: "+mo.getStorageId());
			System.out.println("To: "+mo.getValue("to"));
			System.out.println("From: "+mo.getValue("from"));
			System.out.println("Subject: "+mo.getValue("subject"));
			System.out.println("-----------------------------------------------------------------------------");
		}
	}
	
	private void testQueryByNotEqualsAll()
	{
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.AND));
		
		List<LogicExpression> expressions = new ArrayList<LogicExpression>();
		input.setAttribute("expressions", expressions);
		
		String lhs = "to";
		String rhs = "to://2";
		expressions.add(LogicExpression.
		createInstance(lhs, rhs, LogicExpression.OP_NOT_EQUALS));
		
		lhs = "subject";
		rhs = "subject";
		expressions.add(LogicExpression.
		createInstance(lhs, rhs, LogicExpression.OP_NOT_EQUALS));
		
		Set<MobileObject> result = MobileObjectDatabase.getInstance().
				query("email", input);
		
		int number_of_objects_found = result.size();
		this.assertEquals(""+number_of_objects_found, "4", this.getInfo()+"/testQueryByNotEqualsAll/OneObjectShouldBeLeftBehind");
		
		for(MobileObject mo:result)
		{
			System.out.println("-------------------testQueryByNotEqualsAll-------------------------------");
			System.out.println("RecordId: "+mo.getRecordId());
			System.out.println("Channel: "+mo.getStorageId());
			System.out.println("To: "+mo.getValue("to"));
			System.out.println("From: "+mo.getValue("from"));
			System.out.println("Subject: "+mo.getValue("subject"));
			System.out.println("-------------------------------------------------------------------");
		}
	}
	
	private void testQueryByNotEqualsAtleastOne()
	{
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.OR));
		
		List<LogicExpression> expressions = new ArrayList<LogicExpression>();
		input.setAttribute("expressions", expressions);
		
		String lhs = "to";
		String rhs = "to://2";
		expressions.add(LogicExpression.
		createInstance(lhs, rhs, LogicExpression.OP_NOT_EQUALS));
		
		lhs = "subject";
		rhs = "subject";
		expressions.add(LogicExpression.
		createInstance(lhs, rhs, LogicExpression.OP_NOT_EQUALS));
		
		Set<MobileObject> result = MobileObjectDatabase.getInstance().
				query("email", input);
		
		int number_of_objects_found = result.size();
		this.assertEquals(""+number_of_objects_found, "5", this.getInfo()+"/testQueryByNotEqualsAtleastOne/AllObjectsShouldBeFound");
		
		for(MobileObject mo:result)
		{
			System.out.println("-------------------testQueryByNotEqualsAtleastOne-------------------------------");
			System.out.println("RecordId: "+mo.getRecordId());
			System.out.println("Channel: "+mo.getStorageId());
			System.out.println("To: "+mo.getValue("to"));
			System.out.println("From: "+mo.getValue("from"));
			System.out.println("Subject: "+mo.getValue("subject"));
			System.out.println("-------------------------------------------------------------------");
		}
	}
	
	private void testQueryByContainsAll()
	{
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.AND));
		
		List<LogicExpression> expressions = new ArrayList<LogicExpression>();
		input.setAttribute("expressions", expressions);
		
		String lhs = "to";
		String rhs = "://";
		expressions.add(LogicExpression.
		createInstance(lhs, rhs, LogicExpression.OP_CONTAINS));
		
		lhs = "subject";
		rhs = "mustnotmatchany";
		expressions.add(LogicExpression.
		createInstance(lhs, rhs, LogicExpression.OP_CONTAINS));
		
		Set<MobileObject> result = MobileObjectDatabase.getInstance().
				query("email", input);
		
		int number_of_objects_found = result.size();
		this.assertEquals(""+number_of_objects_found, "0", this.getInfo()+"/testQueryByContainsAll/NoneShouldBeFound");
		
		for(MobileObject mo:result)
		{
			System.out.println("-------------------testQueryByContainsAll-------------------------------");
			System.out.println("RecordId: "+mo.getRecordId());
			System.out.println("Channel: "+mo.getStorageId());
			System.out.println("To: "+mo.getValue("to"));
			System.out.println("From: "+mo.getValue("from"));
			System.out.println("Subject: "+mo.getValue("subject"));
			System.out.println("-------------------------------------------------------------------");
		}
	}
	
	private void testQueryByContainsAtleastOne()
	{
		GenericAttributeManager input = new GenericAttributeManager();
		
		input.setAttribute("logicLink", new Integer(LogicChain.OR));
		
		List<LogicExpression> expressions = new ArrayList<LogicExpression>();
		input.setAttribute("expressions", expressions);
		
		String lhs = "to";
		String rhs = "://";
		expressions.add(LogicExpression.
		createInstance(lhs, rhs, LogicExpression.OP_CONTAINS));
		
		lhs = "subject";
		rhs = "mustnotmatchany";
		expressions.add(LogicExpression.
		createInstance(lhs, rhs, LogicExpression.OP_CONTAINS));
		
		Set<MobileObject> result = MobileObjectDatabase.getInstance().
				query("email", input);
		
		int number_of_objects_found = result.size();
		this.assertEquals(""+number_of_objects_found, "5", this.getInfo()+"/testQueryByContainsAtleastOne/AllShouldBeFound");
		
		for(MobileObject mo:result)
		{
			System.out.println("-------------------testQueryByContainsAtleastOne-------------------------------");
			System.out.println("RecordId: "+mo.getRecordId());
			System.out.println("Channel: "+mo.getStorageId());
			System.out.println("To: "+mo.getValue("to"));
			System.out.println("From: "+mo.getValue("from"));
			System.out.println("Subject: "+mo.getValue("subject"));
			System.out.println("-------------------------------------------------------------------");
		}
	}
}
