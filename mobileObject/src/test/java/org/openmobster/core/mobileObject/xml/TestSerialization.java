/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileObject.xml;

import java.util.List;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import org.openmobster.core.mobileObject.MockPOJO;
import org.openmobster.core.mobileObject.MockChild;

import org.openmobster.core.common.ServiceManager;

/**
 * @author openmobster@gmail.com
 */
public class TestSerialization extends TestCase
{
	private static Logger log = Logger.getLogger(TestSerialization.class);
	
	protected MobileObjectSerializer serializer;
			
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
	//-------------------------------------------------------------------------------------------------------------------
	public void testSimpleMockPOJO() throws Exception
	{		
		MockPOJO pojo = this.createPOJOWithStrings("top-level", false);
		
				
		String deviceXml = this.serializer.serialize(pojo);
		log.info("--------------------------------------");		
		log.info(deviceXml);
		log.info("--------------------------------------");		
		
		//Assert state
		pojo = (MockPOJO)this.serializer.deserialize(MockPOJO.class, deviceXml);
		this.assertEquals("MockPOJO Value must match!!!", "top-level", pojo.getValue());
	}
	
	public void testMockPOJOWithChild() throws Exception
	{		
		MockPOJO pojo = new MockPOJO("top-level");		
		
		MockChild child = new MockChild("child");
		MockPOJO embedded = new MockPOJO("embedded");
		child.setParent(embedded);
		pojo.setChild(child);
				
		String deviceXml = this.serializer.serialize(pojo);
		log.info("--------------------------------------");		
		log.info(deviceXml);
		log.info("--------------------------------------");
		
		//Assert state
		pojo = (MockPOJO)this.serializer.deserialize(MockPOJO.class, deviceXml);
		this.assertEquals("MockPOJO Value must match!!!", "top-level", pojo.getValue());
		this.assertEquals("MockChild Value must match!!!", "child", pojo.getChild().getValue());
		this.assertEquals("MockChild Parent Value must match!!!", "embedded", pojo.getChild().getParent().getValue());
	}
	
	public void testMockPOJOWithNullChildArray() throws Exception
	{		
		MockPOJO pojo = new MockPOJO("top-level");		
		
		MockChild child = new MockChild("child");
		MockPOJO embedded = new MockPOJO("embedded");
		child.setParent(embedded);
		pojo.setChild(child);
		
		List<MockChild> children = new ArrayList<MockChild>();
		for(int i=0; i<5; i++)
		{
			MockChild localChild = new MockChild("child://"+i);
			localChild.setParent(this.createPOJOWithStrings("embedded://"+i, true));
			children.add(localChild);
		}
		pojo.setChildren(children);
		
		String deviceXml = this.serializer.serialize(pojo);		
		log.info("--------------------------------------");		
		log.info(deviceXml);
		log.info("--------------------------------------");
		
		//Assert state
		pojo = (MockPOJO)this.serializer.deserialize(MockPOJO.class, deviceXml);
		this.assertEquals("MockPOJO Value must match!!!", "top-level", pojo.getValue());
		this.assertEquals("MockChild Value must match!!!", "child", pojo.getChild().getValue());
		this.assertEquals("MockChild Parent Value must match!!!", "embedded", pojo.getChild().getParent().getValue());
		
		children = pojo.getChildren();
		this.assertTrue("Children must not be empty", children != null && !children.isEmpty());
		int index = 0;
		for(MockChild assertChild: children)
		{			
			this.assertEquals("Child Value must match!!", "child://"+index, assertChild.getValue());
			index++;
		}
	}
	
	public void testMockPOJOFullyPopulated() throws Exception
	{		
		MockPOJO pojo = new MockPOJO("top-level");
		
		MockChild child = new MockChild("child");
		MockPOJO embedded = new MockPOJO("embedded");
		child.setParent(embedded);
		embedded.setChildArray(new String[]{"child://blah0", "child://blah1"});
		pojo.setChild(child);
		
		List<MockChild> children = new ArrayList<MockChild>();
		for(int i=0; i<5; i++)
		{
			MockChild localChild = new MockChild("child://"+i);
			localChild.setParent(this.createPOJOWithStrings("embedded://"+i, false));
			children.add(localChild);
		}
		pojo.setChildren(children);
		
		String deviceXml = this.serializer.serialize(pojo);		
		log.info("--------------------------------------");		
		log.info(deviceXml);
		log.info("--------------------------------------");
		
		this.serializeWithDefaultXStream(pojo);
		
		//Assert state
		pojo = (MockPOJO)this.serializer.deserialize(MockPOJO.class, deviceXml);
		this.assertEquals("MockPOJO Value must match!!!", "top-level", pojo.getValue());
		
		this.assertEquals("MockChild Value must match!!!", "child", pojo.getChild().getValue());
		this.assertEquals("MockChild Parent Value must match!!!", "embedded", pojo.getChild().getParent().getValue());		
		String[] childArray = pojo.getChild().getParent().getChildArray();
		this.assertTrue("Child Array must have 2 strings", childArray.length == 2);
		this.assertEquals("Parent Child Array must match!!", childArray[0], "child://blah0");
		this.assertEquals("Parent Child Array must match!!", childArray[1], "child://blah1");
		
				
		children = pojo.getChildren();
		this.assertTrue("Children must not be empty", children != null && !children.isEmpty());
		int index = 0;
		for(MockChild assertChild: children)
		{			
			this.assertEquals("Child Value must match!!", "child://"+index, assertChild.getValue());			
			
			//Deeper assertions
			MockPOJO parent = assertChild.getParent();
			this.assertEquals("MockChild Parent Value must match!!!", "embedded://"+index, parent.getValue());
			
			int strIndex=0;
			for(String parentString: parent.getStrings())
			{
				this.assertEquals("Parent String Value must match!!!", "string://"+strIndex, parentString);
				strIndex++;
			}
			
			String[] assertChildArray = parent.getChildArray();
			this.assertTrue("Parent Child Array must have 2 strings", assertChildArray.length == 2);
			this.assertEquals("Parent Child Array must match!!", assertChildArray[0], "blah0");
			this.assertEquals("Parent Child Array must match!!", assertChildArray[1], "blah1");
			
			index++;
		}
	}
	
	public void testDefaultPOJO() throws Exception
	{				
		defaultPOJO pojo1 = new defaultPOJO();
		defaultPOJO pojo2 = new defaultPOJO();
		pojo2.setValue("I am pojo2");
		pojo1.setValue("I am pojo1");
		pojo1.setDefaultPOJO(pojo2);
		
		String deviceXml = this.serializer.serialize(pojo1);
		log.info("--------------------------------------");		
		log.info(deviceXml);
		log.info("--------------------------------------");
		
		//Assert state
		pojo1 = (defaultPOJO)this.serializer.deserialize(defaultPOJO.class, deviceXml);
		this.assertEquals("POJO1 Value must match!!!", "I am pojo1", pojo1.getValue());
		this.assertEquals("POJO2 Value must match!!!", "I am pojo2", pojo1.getDefaultPOJO().getValue());
	}		
	//---------------------------------------------------------------------------------------------------
	protected MockPOJO createPOJOWithStrings(String name, boolean leaveChildArrayNull)
	{
		MockPOJO mockPOJO = new MockPOJO(name);
		
		List<String> strings = new ArrayList<String>();
		for(int i=0; i<5; i++)
		{
			strings.add("string://"+i);
		}
		mockPOJO.setStrings(strings);
		
		if(!leaveChildArrayNull)
		{
			mockPOJO.setChildArray(new String[]{"blah0", "blah1"});
		}
		
		return mockPOJO;
	}
	
	protected void serializeWithDefaultXStream(Object pojo)
	{
		log.info("--------------------------------------");
		XStream coreXStream = new XStream()
		{
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MobileObjectMapperWrapper(next);
            }
        };
		log.info(coreXStream.toXML(pojo));
		log.info("--------------------------------------");
	}
}
