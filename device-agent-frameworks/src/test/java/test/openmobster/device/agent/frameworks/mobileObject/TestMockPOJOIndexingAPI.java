/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.frameworks.mobileObject;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.openmobster.device.agent.frameworks.mobileObject.DeviceSerializer;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.frameworks.mobileObject.BeanList;
import org.openmobster.device.agent.frameworks.mobileObject.BeanListEntry;

import test.openmobster.device.agent.frameworks.mobileObject.email.MockChild;

//TODO: Test all the usecases from TestMockPOJO......it extensively tests a bunch of scenarios possible in the
//object graph

/**
 * @author openmobster@gmail.com
 */
public class TestMockPOJOIndexingAPI extends AbstractSerialization 
{	
	private static Logger log = Logger.getLogger(TestMockPOJOIndexingAPI.class);
		
	public void testReadListOfStrings() throws Exception
	{
		MockPOJO pojo = this.createPOJOWithStrings();
		
		String xml = this.serverSerialize(pojo);
		MobileObject mobileObject = this.deviceDeserialize(xml);
		
		log.info("-----------------------------------------");
		log.info(xml);				
		log.info("-----------------------------------------");		
		
		//Assert the state
		BeanList list = IndexingAPIUtil.readList(mobileObject, "strings");
		assertEquals("Strings Size Match Failed!!", 5, list.size());
		for(int i=0; i<list.size(); i++)
		{
			BeanListEntry entry = list.getEntryAt(i);
			String propertyValue = entry.getValue();
			assertEquals("Entry value does not match", "string://"+i, propertyValue);
		}
		
		String deXml = DeviceSerializer.getInstance().serialize(mobileObject);
		MockPOJO dePojo = (MockPOJO)this.serverDeserialize(MockPOJO.class, deXml);
		List<String> deStrings = dePojo.getStrings();
		assertEquals("Strings Size Match Failed!!", 5, deStrings.size());
		for(int i=0; i<deStrings.size(); i++)
		{				 
			String local = deStrings.get(i);
			log.info("DeString("+i+")="+local);			
			assertEquals("Entry value does not match", "string://"+i, local);
		}
	}
	
	public void testUpdateListOfStrings() throws Exception
	{
		int arraySize = 7;
		
		MockPOJO pojo = this.createPOJOWithStrings();		
		String xml = this.serverSerialize(pojo);
		MobileObject mobileObject = this.deviceDeserialize(xml);
		
		for(int i=5; i<arraySize; i++)
		{
			BeanListEntry bean = new BeanListEntry();
			bean.setValue("string://"+i);
			IndexingAPIUtil.addBean(mobileObject, "strings", bean);
		}		
		
		String deXml = DeviceSerializer.getInstance().serialize(mobileObject);		
		log.info("-----------------------------------------");
		log.info(deXml);				
		log.info("-----------------------------------------");
		
		//Assert the state
		BeanList list = IndexingAPIUtil.readList(mobileObject, "strings");
		assertEquals("Strings Size Match Failed!!", arraySize, list.size());
		for(int i=0; i<list.size(); i++)
		{
			BeanListEntry entry = list.getEntryAt(i);
			String propertyValue = entry.getValue();
			assertEquals("Entry value does not match", "string://"+i, propertyValue);
		}
		
		MockPOJO dePojo = (MockPOJO)this.serverDeserialize(MockPOJO.class, deXml);
		List<String> deStrings = dePojo.getStrings();
		assertEquals("Strings Size Match Failed!!", arraySize, deStrings.size());
		for(int i=0; i<deStrings.size(); i++)
		{				 
			String local = deStrings.get(i);
			log.info("DeString("+i+")="+local);			
			assertEquals("Entry value does not match", "string://"+i, local);
		}
	}
	
	public void testCreateListOfStrings() throws Exception
	{
		int arraySize = 2;
				
		MobileObject mobileObject = new MobileObject();
		
		BeanList list = new BeanList("strings");
		for(int i=0; i<arraySize; i++)
		{
			BeanListEntry entry = new BeanListEntry();
			entry.setValue("string://"+i);
			list.addEntry(entry);
		}
		IndexingAPIUtil.saveList(mobileObject, list);
		
		String deXml = DeviceSerializer.getInstance().serialize(mobileObject);		
		log.info("-----------------------------------------");
		log.info(deXml);				
		log.info("-----------------------------------------");
		
		//Assert the state
		list = IndexingAPIUtil.readList(mobileObject, "strings");
		assertEquals("Strings Size Match Failed!!", arraySize, list.size());
		for(int i=0; i<list.size(); i++)
		{
			BeanListEntry entry = list.getEntryAt(i);
			String propertyValue = entry.getValue();
			assertEquals("Entry value does not match", "string://"+i, propertyValue);
		}
		
		MockPOJO dePojo = (MockPOJO)this.serverDeserialize(MockPOJO.class, deXml);
		List<String> deStrings = dePojo.getStrings();
		assertEquals("Strings Size Match Failed!!", arraySize, deStrings.size());
		for(int i=0; i<deStrings.size(); i++)
		{				 
			String local = deStrings.get(i);
			log.info("DeString("+i+")="+local);			
			assertEquals("Entry value does not match", "string://"+i, local);
		}
	}
	
	public void testListOfStringsLifeCycle() throws Exception
	{
		int arraySize = 2;
				
		MobileObject mobileObject = new MobileObject();
		
		BeanList list = new BeanList("strings");
		for(int i=0; i<arraySize; i++)
		{
			BeanListEntry entry = new BeanListEntry();
			entry.setValue("string://"+i);
			list.addEntry(entry);
		}
		IndexingAPIUtil.saveList(mobileObject, list);
		
		String deXml = DeviceSerializer.getInstance().serialize(mobileObject);		
		log.info("-----------------------------------------");
		log.info(deXml);				
		log.info("-----------------------------------------");
		
		//Assert the state
		list = IndexingAPIUtil.readList(mobileObject, "strings");
		assertEquals("Strings Size Match Failed!!", arraySize, list.size());
		for(int i=0; i<list.size(); i++)
		{
			BeanListEntry entry = list.getEntryAt(i);
			String propertyValue = entry.getValue();
			assertEquals("Entry value does not match", "string://"+i, propertyValue);
		}
		
		MockPOJO dePojo = (MockPOJO)this.serverDeserialize(MockPOJO.class, deXml);
		List<String> deStrings = dePojo.getStrings();
		assertEquals("Strings Size Match Failed!!", arraySize, deStrings.size());
		for(int i=0; i<deStrings.size(); i++)
		{				 
			String local = deStrings.get(i);
			log.info("DeString("+i+")="+local);			
			assertEquals("Entry value does not match", "string://"+i, local);
		}
		
				
		IndexingAPIUtil.clearList(mobileObject, list.getListProperty());				
		
		for(int i=0; i<5; i++)
		{
			BeanListEntry bean = new BeanListEntry();
			bean.setValue("string://updated/"+i);
			IndexingAPIUtil.addBean(mobileObject, list.getListProperty(), bean);
		}
		
		deXml = DeviceSerializer.getInstance().serialize(mobileObject);		
		log.info("-----------------------------------------");
		log.info(deXml);				
		log.info("-----------------------------------------");
		
		//Assert the state
		list = IndexingAPIUtil.readList(mobileObject, "strings");
		assertEquals("Strings Size Match Failed!!", 5, list.size());
		for(int i=0; i<list.size(); i++)
		{
			BeanListEntry entry = list.getEntryAt(i);
			String propertyValue = entry.getValue();
			assertEquals("Entry value does not match", "string://updated/"+i, propertyValue);
		}
		
		dePojo = (MockPOJO)this.serverDeserialize(MockPOJO.class, deXml);
		deStrings = dePojo.getStrings();
		assertEquals("Strings Size Match Failed!!", 5, deStrings.size());
		for(int i=0; i<deStrings.size(); i++)
		{				 
			String local = deStrings.get(i);
			log.info("DeString("+i+")="+local);			
			assertEquals("Entry value does not match", "string://updated/"+i, local);
		}
	}
		
	public void testRemoveElementFromMiddle() throws Exception
	{
		MockPOJO pojo = this.createPOJOWithStrings();
		
		String xml = this.serverSerialize(pojo);
		MobileObject mobileObject = this.deviceDeserialize(xml);
		
		log.info("-----------------------------------------");
		log.info(xml);				
		log.info("-----------------------------------------");
		
		IndexingAPIUtil.removeBean(mobileObject, "strings", 2);
		
		String deXml = DeviceSerializer.getInstance().serialize(mobileObject);
		
		log.info("-----------------------------------------");
		log.info(deXml);				
		log.info("-----------------------------------------");
		
		BeanList list = IndexingAPIUtil.readList(mobileObject, "strings");
		assertEquals("Strings Size Match Failed!!", 4, list.size());
		for(int i=0; i<list.size(); i++)
		{
			BeanListEntry bean = list.getEntryAt(i);	
			String propertyValue = bean.getValue(); 
			log.info("Value="+propertyValue);
			
			if(i<2)
			{
				assertEquals("Value does not match", "string://"+i, propertyValue);
			}
			else
			{
				assertEquals("Value does not match", "string://"+(i+1), propertyValue);
			}
		}
		
		MockPOJO dePojo = (MockPOJO)this.serverDeserialize(MockPOJO.class, deXml);
		List<String> deStrings = dePojo.getStrings();
		assertEquals("Strings Size Match Failed!!", 4, deStrings.size());
		for(int i=0; i<deStrings.size(); i++)
		{				 
			String local = deStrings.get(i);
			log.info("DeString("+i+")="+local);
			
			if(i<2)
			{
				assertEquals("Value does not match", "string://"+i, local);
			}
			else
			{
				assertEquals("Value does not match", "string://"+(i+1), local);
			}
		}				
	}
	
	public void testRemoveElementFromTop() throws Exception
	{
		MockPOJO pojo = this.createPOJOWithStrings();
		
		String xml = this.serverSerialize(pojo);
		MobileObject mobileObject = this.deviceDeserialize(xml);
		
		log.info("-----------------------------------------");
		log.info(xml);				
		log.info("-----------------------------------------");
		
		IndexingAPIUtil.removeBean(mobileObject, "strings", 0);
		
		String deXml = DeviceSerializer.getInstance().serialize(mobileObject);
		
		log.info("-----------------------------------------");
		log.info(deXml);				
		log.info("-----------------------------------------");
		
		BeanList list = IndexingAPIUtil.readList(mobileObject, "strings");
		assertEquals("Strings Size Match Failed!!", 4, list.size());
		for(int i=0; i<list.size(); i++)
		{	
			BeanListEntry bean = list.getEntryAt(i);	
			String propertyValue = bean.getValue(); 
			log.info("Value="+propertyValue);
			
			assertEquals("Value does not match", "string://"+(i+1), propertyValue);
		}
		
		MockPOJO dePojo = (MockPOJO)this.serverDeserialize(MockPOJO.class, deXml);
		List<String> deStrings = dePojo.getStrings();
		assertEquals("Strings Size Match Failed!!", 4, deStrings.size());
		for(int i=0; i<deStrings.size(); i++)
		{				 
			String local = deStrings.get(i);
			log.info("DeString("+i+")="+local);
			
			assertEquals("Value does not match", "string://"+(i+1), local);
		}				
	}
	
	public void testRemoveElementFromBottom() throws Exception
	{
		MockPOJO pojo = this.createPOJOWithStrings();
		
		String xml = this.serverSerialize(pojo);
		MobileObject mobileObject = this.deviceDeserialize(xml);
		
		log.info("-----------------------------------------");
		log.info(xml);				
		log.info("-----------------------------------------");
		
		IndexingAPIUtil.removeBean(mobileObject, "strings", 4);
		
		String deXml = DeviceSerializer.getInstance().serialize(mobileObject);
		
		log.info("-----------------------------------------");
		log.info(deXml);				
		log.info("-----------------------------------------");
		
		BeanList list = IndexingAPIUtil.readList(mobileObject, "strings");
		assertEquals("Strings Size Match Failed!!", 4, list.size());
		for(int i=0; i<list.size(); i++)
		{	
			BeanListEntry bean = list.getEntryAt(i);	
			String propertyValue = bean.getValue(); 
			log.info("Value="+propertyValue);
			
			assertEquals("Value does not match", "string://"+i, propertyValue);
		}
		
		MockPOJO dePojo = (MockPOJO)this.serverDeserialize(MockPOJO.class, deXml);
		List<String> deStrings = dePojo.getStrings();
		assertEquals("Strings Size Match Failed!!", 4, deStrings.size());
		for(int i=0; i<deStrings.size(); i++)
		{				 
			String local = deStrings.get(i);
			log.info("DeString("+i+")="+local);
			
			assertEquals("Value does not match", "string://"+i, local);
		}				
	}
	
	public void testFullPopulatedMobileObject() throws Exception
	{
		MobileObject mobileObject = new MobileObject();
		
		mobileObject.setValue("value", "top-level");
		BeanList list = new BeanList("strings");
		for(int i=0; i<5; i++)
		{
			BeanListEntry entry = new BeanListEntry();
			entry.setValue("string://"+i);
			list.addEntry(entry);
		}
		IndexingAPIUtil.saveList(mobileObject, list);
		
		String deviceXml = DeviceSerializer.getInstance().serialize(mobileObject);
		log.info("----DeviceXml------------------------------------------------");
		log.info(deviceXml);
		
		MockPOJO mockPOJO = (MockPOJO)this.serverDeserialize(MockPOJO.class, deviceXml);
		
		assertEquals(mockPOJO.getValue(), "top-level");
		List<String> strings = mockPOJO.getStrings();
		int i=0;
		for(String string: strings)
		{
			assertEquals("string://"+i++, string);
			log.info("------------------------------------------------");
			log.info(string);
		}		
	}
	
	public void testReadWithListOfStrings() throws Exception
	{
		this.testReadWithListOfStrings(5);
	}
	
	public void testReadWithListOf2Strings() throws Exception
	{
		this.testReadWithListOfStrings(2);
	}
	
	public void testReadWithListOf1String() throws Exception
	{
		this.testReadWithListOfStrings(1);
	}
	
	public void testCreateWithListOfStrings() throws Exception
	{
		this.testCreateWithListOfStrings(5);
	}
	
	public void testCreateWithListOf2Strings() throws Exception
	{
		this.testCreateWithListOfStrings(2);
	}
	
	public void testCreateWithListOf1String() throws Exception
	{
		this.testCreateWithListOfStrings(1);
	}
	//----------------------------------------------------------------------------------------------------------------------------------
	private void testReadWithListOfStrings(int index)
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
		
		String xml = this.serverSerialize(pojo);
		MobileObject mobileObject = this.deviceDeserialize(xml);
		
		log.info("-----------------------------------------");
		log.info(xml);				
		log.info("-----------------------------------------");
		log.info("Parent Value="+mobileObject.getValue("value"));
		assertEquals("Read Failure!!", mobileObject.getValue("value"), "parent");
		
		BeanList childrenList = IndexingAPIUtil.readList(mobileObject, "children");
		assertEquals("Children Values Size Match Failed!!", index, childrenList.size());
		for(int i=0; i<childrenList.size(); i++)
		{	
			BeanListEntry child = childrenList.getEntryAt(i);
			
			String property = "children["+i+"].value";
			String value = child.getProperty("value");
			log.info(property+"="+value);
			assertEquals("("+property+") value does not match", "child://"+i, value);
			
			//Assert the parent state
			String parentStringListProperty = "children["+i+"].parent.strings"; 
			BeanList parentStringList = IndexingAPIUtil.readList(mobileObject, parentStringListProperty);			
			for(int j=0; j<parentStringList.size(); j++)
			{	
				BeanListEntry parentString = parentStringList.getEntryAt(j);
				String strProperty = "strings["+j+"]"; 
				String strPropertyValue = parentString.getValue();
				log.info(strProperty+"="+strPropertyValue);
				assertEquals("("+strProperty+") value does not match", "string://"+j, strPropertyValue);
			}
		}		
	}
	
	private void testCreateWithListOfStrings(int index)
	{
		MockPOJO pojo = new MockPOJO("parent");
		
		List<MockChild> children = new ArrayList<MockChild>();	
		for(int i=0; i<index; i++)
		{
			MockChild child = new MockChild("child://"+i);
			child.setParent(this.createPOJOWithStrings(index));
			children.add(child);
		}
		pojo.setChildren(children);
		
		String xml = this.serverSerialize(pojo);
		MobileObject mobileObject = this.deviceDeserialize(xml);
		
		log.info("-----------------------------------------");
		log.info(xml);				
		log.info("-----------------------------------------");
		log.info("Parent Value="+mobileObject.getValue("value"));
		assertEquals("Read Failure!!", mobileObject.getValue("value"), "parent");
		
		BeanList childrenList = IndexingAPIUtil.readList(mobileObject, "children");
		assertEquals("Children Values Size Match Failed!!", index, childrenList.size());
		for(int i=0; i<childrenList.size(); i++)
		{
			BeanListEntry child = childrenList.getEntryAt(i);
			String property = "children["+i+"].value";
			String childValue = child.getProperty("value");
			log.info(property+"="+childValue);
			assertEquals("("+property+") value does not match", "child://"+i, childValue);
			
			//Assert the parent state
			String indexedProperty = "children["+i+"].parent.strings";
			BeanListEntry newString = new BeanListEntry();
			newString.setValue("new://created");
			IndexingAPIUtil.addBean(mobileObject, indexedProperty, newString);			 
			BeanList parentStringList = IndexingAPIUtil.readList(mobileObject, indexedProperty);			
			assertEquals("Parent String Values Size Match Failed!!", index+1, parentStringList.size());
			for(int j=0; j<parentStringList.size(); j++)
			{	
				BeanListEntry parentString = parentStringList.getEntryAt(j); 
				String strProperty = "strings["+j+"]"; 
				String strValue = parentString.getValue();
				log.info(strProperty+"="+strValue);
				
				if(j<(parentStringList.size()-1))
				{
					assertEquals("("+strProperty+") value does not match", "string://"+j, strValue);
				}
				else
				{
					assertEquals("("+strProperty+") value does not match", "new://created", strValue);
				}
			}
		}	
		
		String deXml = DeviceSerializer.getInstance().serialize(mobileObject);
		log.info("------------------------------------------------");
		log.info(deXml);
		log.info("------------------------------------------------");
		
		MockPOJO dePojo = (MockPOJO)this.serverDeserialize(MockPOJO.class, deXml);
		List<MockChild> assertChildren = dePojo.getChildren();
		for(int i=0; i<assertChildren.size(); i++)
		{
			MockChild child = assertChildren.get(i);
			log.info("Child Value="+child.getValue());
			assertEquals("Value does not match", "child://"+i, child.getValue());
			
			List<String> strings = child.getParent().getStrings();
			for(int j=0; j<strings.size(); j++)
			{
				log.info("String Value="+strings.get(j));
			}
		}
	}
	//-----------------------------------------------------------------------------------------------------
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
	
	private MockPOJO createPOJOWithStrings(int size)
	{
		MockPOJO mockPOJO = new MockPOJO("parent");
		
		List<String> strings = new ArrayList<String>();
		for(int i=0; i<size; i++)
		{
			strings.add("string://"+i);
		}
		mockPOJO.setStrings(strings);
		
		return mockPOJO;
	}
}
