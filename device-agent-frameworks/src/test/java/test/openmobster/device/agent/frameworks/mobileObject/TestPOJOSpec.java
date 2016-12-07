/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.frameworks.mobileObject;

import java.util.Set;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.openmobster.device.agent.frameworks.mobileObject.DeviceSerializer;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.frameworks.mobileObject.BeanListEntry;

/**
 * @author openmobster@gmail.com
 */
public class TestPOJOSpec extends AbstractSerialization 
{	
	private static Logger log = Logger.getLogger(TestPOJOSpec.class);
			
	public void testSetBasedIndexedPropertyCheck() throws Exception
	{
		//Go From Server to Device
		MockPOJO pojo = this.createPOJOWithSetOfStrings();		
		String xml = this.serverSerialize(pojo);		
		log.info("-----------------------------------------");
		log.info(xml);				
		log.info("-----------------------------------------");
		MobileObject mobileObject = this.deviceDeserialize(xml);
		
		//Get Modified on the Device
		for(int i=0; i<5; i++)
		{
			BeanListEntry entry = new BeanListEntry();
			entry.setValue("string://"+i);
			IndexingAPIUtil.addBean(mobileObject, "setOfStrings", entry);
		}		
		
		//Go From Device to Server
		String deXml = DeviceSerializer.getInstance().serialize(mobileObject);		
		log.info("-----------------------------------------");
		log.info(deXml);				
		log.info("-----------------------------------------");
		try
		{
			pojo = (MockPOJO)this.serverDeserialize(MockPOJO.class, deXml);
		}
		catch(Exception e)
		{
			return;
		}
		
		//I shouldn't get here
		this.assertTrue("Set Based Indexed Property is not allowed!!!", false);
	}
	
	public void testNonParametrizedListCheck() throws Exception
	{						
		//Get Created On Device
		MobileObject mobileObject = new MobileObject();
		for(int i=0; i<5; i++)
		{
			BeanListEntry entry = new BeanListEntry();
			entry.setValue("string://"+i);
			IndexingAPIUtil.addBean(mobileObject, "nonParametrizedList", entry);
		}		
		
		//Go From Device to Server
		String deXml = DeviceSerializer.getInstance().serialize(mobileObject);		
		log.info("-----------------------------------------");
		log.info(deXml);				
		log.info("-----------------------------------------");
		try
		{
			MockPOJO pojo = (MockPOJO)this.serverDeserialize(MockPOJO.class, deXml);
		}
		catch(Exception e)
		{
			return;
		}
		
		//I shouldn't get here
		this.assertTrue("Non-ParametrizedList is not allowed!!!", false);
	}
	
	public void testAbstractParametrizedListCheck() throws Exception
	{		
		MockPOJO pojo = null;
		MobileObject mobileObject = new MobileObject();
		
		//Get Modified on the Device
		for(int i=0; i<5; i++)
		{
			BeanListEntry entry = new BeanListEntry();
			entry.setProperty("name","concretePOJO://fromdevice/"+i);
			IndexingAPIUtil.addBean(mobileObject, "abstractList", entry);
		}		
		
		//Go From Device to Server
		String deXml = DeviceSerializer.getInstance().serialize(mobileObject);		
		log.info("-----------------------------------------");
		log.info(deXml);				
		log.info("-----------------------------------------");
		try
		{
			pojo = (MockPOJO)this.serverDeserialize(MockPOJO.class, deXml);
		}
		catch(Exception e)
		{
			return;
		}
		
		for(BasePOJO local: pojo.getAbstractList())
		{
			log.info(local.getName());
		}
		
		//I shouldn't get here
		this.assertTrue("Set Based Indexed Property is not allowed!!!", false);
	}
	
	public void testConcreteParametrizedListCheck() throws Exception
	{		
		MockPOJO pojo = null;
		MobileObject mobileObject = new MobileObject();
		
		//Get Modified on the Device
		for(int i=0; i<5; i++)
		{
			BeanListEntry entry = new BeanListEntry();
			entry.setProperty("name","concretePOJO://fromdevice/"+i);
			IndexingAPIUtil.addBean(mobileObject, "concreteList", entry);
		}		
		
		//Go From Device to Server
		String deXml = DeviceSerializer.getInstance().serialize(mobileObject);		
		log.info("-----------------------------------------");
		log.info(deXml);				
		log.info("-----------------------------------------");
		pojo = (MockPOJO)this.serverDeserialize(MockPOJO.class, deXml);
		
		for(ConcretePOJO local: pojo.getConcreteList())
		{
			log.info(local.getName());
			assertTrue(local.getName().startsWith("concretePOJO://fromdevice/"));
		}				
	}
	
	public void testArrayWithNullElementCheck() throws Exception
	{
		//Go From Server to Device
		MockPOJO pojo = this.createPOJOWithNullArrayElements();
		try
		{
			String xml = this.serverSerialize(pojo);
		}
		catch(Exception e)
		{
			return;
		}
		
		//I shouldn't get here
		this.assertTrue("Array with Null Elements are not allowed!!!", false);				
	}
	//--------------------------------------------------------------------------------------------------------------------
	private MockPOJO createPOJOWithSetOfStrings()
	{
		MockPOJO mockPOJO = new MockPOJO("parent");
		
		Set<String> strings = new HashSet<String>();
		for(int i=0; i<5; i++)
		{
			strings.add("string://"+i);
		}
		mockPOJO.setSetOfStrings(strings);
		
		return mockPOJO;
	}
	
	private MockPOJO createPOJOWithNullArrayElements()
	{
		MockPOJO mockPOJO = new MockPOJO("parent");
		
		String[] arrayWithNullElements = new String[]{"blah","blah2",null,"blah3"};		
		mockPOJO.setArrayWithNullElements(arrayWithNullElements);
		
		return mockPOJO;
	}
}
