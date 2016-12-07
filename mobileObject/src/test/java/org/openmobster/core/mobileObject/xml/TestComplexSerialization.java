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

import org.apache.log4j.Logger;

import org.openmobster.core.mobileObject.MockPOJO;
import org.openmobster.core.mobileObject.MockChild;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * @author openmobster@gmail.com
 */
public class TestComplexSerialization extends AbstractTestSerialization
{
	private static Logger log = Logger.getLogger(TestComplexSerialization.class);
	
	public void testListOfStrings() throws Exception
	{
		this.testListOfStrings(5);
	}
	
	public void testListOfTwoStrings() throws Exception
	{
		this.testListOfStrings(2);
	}
	
	public void testListOfOneString() throws Exception
	{
		this.testListOfStrings(1);
	}
	
	public void testListOfObjectsTwoLevelsDeep() throws Exception
	{
		this.testListOfObjectsTwoLevelsDeep(5);
	}
	
	public void testListOfTwoObjectsTwoLevelsDeep() throws Exception
	{
		this.testListOfObjectsTwoLevelsDeep(2);
	}
	
	public void testListOfOneObjectTwoLevelsDeep() throws Exception
	{
		this.testListOfObjectsTwoLevelsDeep(1);
	}
	
	public void testListOfObjectsThreeLevelsDeep() throws Exception
	{
		this.testListOfObjectsThreeLevelsDeep(5);
	}
	
	public void testListOfTwoObjectsThreeLevelsDeep() throws Exception
	{
		this.testListOfObjectsThreeLevelsDeep(2);
	}
	
	public void testListOfOneObjectThreeLevelsDeep() throws Exception
	{
		this.testListOfObjectsThreeLevelsDeep(1);
	}
	//------------------------------------------------------------------------------------------------------
	private void testListOfStrings(int index) throws Exception
	{
		MockPOJO pojo = new MockPOJO("parent");
						
		List<MockChild> children = new ArrayList<MockChild>();	
		for(int i=0; i<index; i++)
		{
			MockChild child = new MockChild("child://"+i);
			child.setParent(this.createPOJOWithStrings());
			children.add(child);
		}
		pojo.setChildren(children);
		
		String xml = this.serializer.serialize(pojo);
				
		MockPOJO deMo = (MockPOJO)this.serializer.deserialize(MockPOJO.class, xml);
				
		log.info("Parent Value="+deMo.getValue());
		assertEquals("Serialization Failure!!", deMo.getValue(), "parent");
		
		for(int i=0; i<index; i++)
		{
			MockChild child = (MockChild)deMo.getChildren().get(i);
			String key = "children["+i+"].value";
			log.info(key+"="+child.getValue());
			assertEquals("Serialization Failure!!", child.getValue(), "child://"+i);
			
			for(int j=0; j<index; j++)
			{
				String stringValue = (String)child.getParent().getStrings().get(j);
				String stringKey = "children["+i+"].parent.strings["+j+"].string";
				log.info(stringKey+"="+stringValue);
				assertEquals("Serialization Failure!!", stringValue, "string://"+j);
			}
		}				
	}
	
	private void testListOfObjectsTwoLevelsDeep(int index) throws Exception
	{
		MockPOJO root = new MockPOJO("root");			
		List<MockChild> rootChildren = new ArrayList<MockChild>();
		root.setChildren(rootChildren);
		for(int i=0; i<index; i++)
		{	
			MockPOJO iPojo = new MockPOJO("parent://"+i);
			MockChild iChild = new MockChild("child://"+i, iPojo);			
			rootChildren.add(iChild);
			
			List<MockChild> iChildren = new ArrayList<MockChild>();
			iPojo.setChildren(iChildren);
			for(int j=0; j<index; j++)
			{
				MockPOJO jPojo = new MockPOJO("parent://"+i+","+j);
				MockChild jChild = new MockChild("child://"+i+","+j, jPojo);			
				iChildren.add(jChild);
			}
		}
						
		String xml = this.serializer.serialize(root);
		log.info("-----------------------------------------------------");
		log.info(xml);
		log.info("-----------------------------------------------------");
				
		MockPOJO deMo = (MockPOJO)this.serializer.deserialize(MockPOJO.class, xml);
				
		log.info("Parent Value="+deMo.getValue());
		assertEquals("Serialization Failure!!", deMo.getValue(), "root");
		
		for(int i=0; i<index; i++)
		{
			MockChild iChild = deMo.getChildren().get(i);
			String parentKey1 = "children["+i+"].parent.value";
			String key1 = "children["+i+"].value";
			
			log.info(parentKey1+"="+iChild.getParent().getValue());
			log.info(key1+"="+iChild.getValue());
			
			assertEquals("Serialization Failure!!", iChild.getParent().getValue(), "parent://"+i);
			assertEquals("Serialization Failure!!", iChild.getValue(), "child://"+i);
			for(int j=0; j<index; j++)
			{
				MockChild jChild = iChild.getParent().getChildren().get(j);
				String parentKey2 = "children["+i+"].parent.children["+j+"].parent.value";
				String key2 = "children["+i+"].parent.children["+j+"].value";
				
				log.info(parentKey2+"="+jChild.getParent().getValue());
				log.info(key2+"="+jChild.getValue());				
				
				assertEquals("Serialization Failure!!", jChild.getParent().getValue(), "parent://"+i+","+j);
				assertEquals("Serialization Failure!!", jChild.getValue(), "child://"+i+","+j);
			}
		}				
	}
	
	private void testListOfObjectsThreeLevelsDeep(int index) throws Exception
	{
		MockPOJO root = new MockPOJO("root");			
		List<MockChild> rootChildren = new ArrayList<MockChild>();
		root.setChildren(rootChildren);				
		for(int i=0; i<index; i++)
		{	
			MockPOJO iPojo = new MockPOJO("parent://"+i);
			MockChild iChild = new MockChild("child://"+i, iPojo);			
			rootChildren.add(iChild);
			
			List<MockChild> iChildren = new ArrayList<MockChild>();
			iPojo.setChildren(iChildren);
			for(int j=0; j<index; j++)
			{
				MockPOJO jPojo = new MockPOJO("parent://"+i+","+j);
				MockChild jChild = new MockChild("child://"+i+","+j, jPojo);			
				iChildren.add(jChild);
				
				List<MockChild> jChildren = new ArrayList<MockChild>();
				jPojo.setChildren(jChildren);
				for(int k=0; k<index; k++)
				{
					MockPOJO kPojo = new MockPOJO("parent://"+i+","+j+","+k);
					MockChild kChild = new MockChild("child://"+i+","+j+","+k, kPojo);			
					jChildren.add(kChild);
				}
			}
		}
		
		String xml = this.serializer.serialize(root);
				
		MockPOJO deMo = (MockPOJO)this.serializer.deserialize(MockPOJO.class, xml);
				
		log.info("Parent Value="+deMo.getValue());
		assertEquals("Serialization Failure!!", deMo.getValue(), "root");
		
		for(int i=0; i<index; i++)
		{
			MockChild iChild = deMo.getChildren().get(i);
			String parentKey1 = "children["+i+"].parent.value";
			String key1 = "children["+i+"].value";
			
			log.info(parentKey1+"="+iChild.getParent().getValue());
			log.info(key1+"="+iChild.getValue());
			
			assertEquals("Serialization Failure!!", iChild.getParent().getValue(), "parent://"+i);
			assertEquals("Serialization Failure!!", iChild.getValue(), "child://"+i);
			for(int j=0; j<index; j++)
			{
				MockChild jChild = iChild.getParent().getChildren().get(j);
				String parentKey2 = "children["+i+"].parent.children["+j+"].parent.value";
				String key2 = "children["+i+"].parent.children["+j+"].value";
				
				log.info(parentKey2+"="+jChild.getParent().getValue());
				log.info(key2+"="+jChild.getValue());				
				
				assertEquals("Serialization Failure!!", jChild.getParent().getValue(), "parent://"+i+","+j);
				assertEquals("Serialization Failure!!", jChild.getValue(), "child://"+i+","+j);
				
				for(int k=0; k<index; k++)
				{
					MockChild kChild = jChild.getParent().getChildren().get(k);
					String parentKey3 = "children["+i+"].parent.children["+j+"].parent.children["+k+"].parent.value";
					String key3 = "children["+i+"].parent.children["+j+"].parent.children["+k+"].value";
					
					log.info(parentKey3+"="+kChild.getParent().getValue());
					log.info(key3+"="+kChild.getValue());				
					
					assertEquals("Serialization Failure!!", kChild.getParent().getValue(), "parent://"+i+","+j+","+k);
					assertEquals("Serialization Failure!!", kChild.getValue(), "child://"+i+","+j+","+k);
				}
			}
		}				
	}
	
	private MockPOJO createPOJOWithStrings()
	{
		MockPOJO mockPOJO = new MockPOJO("parent");
		
		List<String> strings = new ArrayList<String>();
		for(int i=0; i<5; i++)
		{
			strings.add("string://"+i);
		}
		mockPOJO.setStrings(strings);
		
		return mockPOJO;
	}		
}
