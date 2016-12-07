/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.frameworks.mobileObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.mobileObject.xml.MobileObjectSerializer;

import test.openmobster.device.agent.frameworks.mobileObject.MockPOJO;
import test.openmobster.device.agent.frameworks.mobileObject.email.MockChild;

public class TestQuery extends TestCase 
{
	private static Logger log = Logger.getLogger(TestQuery.class);
	
	private MobileObjectSerializer serializer;
	
	protected void setUp() throws Exception 
	{		
		ServiceManager.bootstrap();
		
		this.serializer = (MobileObjectSerializer)ServiceManager.
		locate("mobileObject://MobileObjectSerializer");				
	}	
		
	protected void tearDown() throws Exception 
	{
		ServiceManager.shutdown();
	}
	//---------------------------------------------------------------------------------------------------------------------------------
	public void testEqualsANDQuery() throws Exception
	{		
		Vector all = this.getAll();
		
		//Construct the query		
		LogicChain andChain = LogicChain.createANDChain();
		andChain.add(LogicExpression.createInstance("value","parent://2",LogicExpression.OP_EQUALS))
		.add(LogicExpression.createInstance("child.value","child://2",LogicExpression.OP_EQUALS));
		Query query = Query.createInstance(andChain);
		
		//Execute the query
		Vector result = query.executeQuery(all);
		assertTrue("Must Not be Empty", result!=null && !result.isEmpty());		
		
		int size = result.size();
		assertEquals("Must have only 1 result", 1, size);
		for(int i=0; i<size; i++)
		{
			MobileObject mobileObject = (MobileObject)result.elementAt(i);
			
			log.info("-------------------------------------------");
			log.info("Parent="+mobileObject.getValue("value"));
			log.info("-------------------------------------------");
			assertEquals("Value must match",mobileObject.getValue("value"), "parent://2");
		}
	}
	
	public void testEqualsORQuery() throws Exception
	{		
		Vector all = this.getAll();
		
		//Construct the query		
		LogicChain andChain = LogicChain.createORChain();
		andChain.add(LogicExpression.createInstance("value","parent://2",LogicExpression.OP_EQUALS))
		.add(LogicExpression.createInstance("strings[2]","string://2",LogicExpression.OP_EQUALS));
		Query query = Query.createInstance(andChain);
		
		//Execute the query
		Vector result = query.executeQuery(all);
		assertTrue("Must Not be Empty", result!=null && !result.isEmpty());
		
		int size = result.size();
		assertEquals("Must have all records", all.size(), size);
		for(int i=0; i<size; i++)
		{
			MobileObject mobileObject = (MobileObject)result.elementAt(i);
			
			log.info("-------------------------------------------");
			log.info("Parent="+mobileObject.getValue("value"));
			log.info("-------------------------------------------");
		}
	}
	//----------------------------------------------------------------------------------------------------------------------------------
	public void testNotEqualsQuery() throws Exception
	{		
		Vector all = this.getAll();
		
		//Construct the query		
		LogicChain chain = LogicChain.createANDChain();
		chain.add(LogicExpression.createInstance("value","parent://2",LogicExpression.OP_NOT_EQUALS));		
		Query query = Query.createInstance(chain);
		
		//Execute the query
		Vector result = query.executeQuery(all);
		assertTrue("Must Not be Empty", result!=null && !result.isEmpty());		
		
		int size = result.size();
		assertEquals("Must Exclude parent://2", all.size()-1, size);
		for(int i=0; i<size; i++)
		{
			MobileObject mobileObject = (MobileObject)result.elementAt(i);
			
			log.info("-------------------------------------------");
			log.info("Parent="+mobileObject.getValue("value"));
			log.info("-------------------------------------------");
			assertTrue("Must Exclude parent://2",!mobileObject.getValue("value").equals("parent://2"));
		}
	}
	//---------------------------------------------------------------------------------------------------------------------------------
	public void testLikeQuery() throws Exception
	{		
		Vector all = this.getAll();
		
		//Construct the query		
		LogicChain chain = LogicChain.createANDChain();
		chain.add(LogicExpression.createInstance("value","parent",LogicExpression.OP_LIKE));		
		Query query = Query.createInstance(chain);
		
		//Execute the query
		Vector result = query.executeQuery(all);
		assertTrue("Must Not be Empty", result!=null && !result.isEmpty());
		
		int size = result.size();
		assertEquals("Must have all records", all.size(), size);
		for(int i=0; i<size; i++)
		{
			MobileObject mobileObject = (MobileObject)result.elementAt(i);
			
			log.info("-------------------------------------------");
			log.info("Parent="+mobileObject.getValue("value"));
			log.info("-------------------------------------------");
		}
	}
	//----------------------------------------------------------------------------------------------------------------------------------
	public void testContainsQuery() throws Exception
	{		
		Vector all = this.getAll();
		
		//Construct the query		
		LogicChain chain = LogicChain.createANDChain();
		chain.add(LogicExpression.createInstance("value","://2",LogicExpression.OP_CONTAINS));		
		Query query = Query.createInstance(chain);
		
		//Execute the query
		Vector result = query.executeQuery(all);
		assertTrue("Must Not be Empty", result!=null && !result.isEmpty());		
		
		int size = result.size();
		assertEquals("Must have only 1 result", 1, size);
		for(int i=0; i<size; i++)
		{
			MobileObject mobileObject = (MobileObject)result.elementAt(i);
			
			log.info("-------------------------------------------");
			log.info("Parent="+mobileObject.getValue("value"));
			log.info("-------------------------------------------");
			assertEquals("Value must match",mobileObject.getValue("value"), "parent://2");
		}
	}
	//----------------------------------------------------------------------------------------------------------------------------------
	private Vector getAll()
	{
		Vector all = new Vector();
		
		for(int i=0; i<5; i++)
		{
			MockPOJO mockPOJO = new MockPOJO("parent://"+i);
			MockChild child = new MockChild("child://"+i);
			mockPOJO.setChild(child);
			
			List<String> strings = new ArrayList<String>();
			for(int j=0; j<5; j++)
			{
				strings.add("string://"+j);
			}
			mockPOJO.setStrings(strings);
			
			String xml = this.serializer.serialize(mockPOJO);		
			MobileObject mobileObject = DeviceSerializer.getInstance().deserialize(xml);
			
			all.addElement(mobileObject);
		}
		
		return all;
	}
}
