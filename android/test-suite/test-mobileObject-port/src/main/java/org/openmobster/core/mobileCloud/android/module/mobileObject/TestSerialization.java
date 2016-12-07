/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.mobileObject;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

import java.util.Map;
import java.util.HashMap;


/**
 * @author openmobster@gmail.com
 */
public class TestSerialization extends Test 
{
	@Override
	public void runTest()
	{
		try
		{
			this.testSimpleSerialization();
			this.testComplexSerialization();
			this.testArraySetup();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	private void testSimpleSerialization() throws Exception
	{
		MobileObject pojo = this.createPOJOWithStrings("top-level");
		
		
		String deviceXml = DeviceSerializer.getInstance().serialize(pojo);
		System.out.println("--------------------------------------");		
		System.out.println(deviceXml);
		System.out.println("--------------------------------------");		
		
		//Assert state
		pojo = DeviceSerializer.getInstance().deserialize(deviceXml);
		this.assertEquals(pojo.getValue("value"), "top-level", this.getInfo()+"/testSimpleSerialization/ValueMatchFailed");
		
		for(int i=0; i<5; i++)
		{
			String value = pojo.getArrayElement("strings", i).values().iterator().next();
			System.out.println("strings: "+value);
			this.assertEquals(value, "string://"+i, this.getInfo()+"/testSimpleSerialization/ArrayMatchFailed");
		}
	}
	
	private void testComplexSerialization() throws Exception
	{
		MobileObject pojo = this.createNestedPOJO("nestedpojo");
		
		
		String deviceXml = DeviceSerializer.getInstance().serialize(pojo);
		System.out.println("--------------------------------------");		
		System.out.println(deviceXml);
		System.out.println("--------------------------------------");		
		
		//Assert state
		pojo = DeviceSerializer.getInstance().deserialize(deviceXml);
		this.assertEquals(pojo.getValue("name"), "nestedpojo", this.getInfo()+"/testComplexSerialization/ValueMatchFailed");
		
		for(int i=0; i<2; i++)
		{
			String childName = pojo.getArrayElement("child.children", i).get("/childName");
			String attachmentName = pojo.getArrayElement("child.children", i).get("/attachment/name");
			String attachmentSize = pojo.getArrayElement("child.children", i).get("/attachment/size");
			System.out.println("ChildName: "+childName);
			System.out.println("AttachmentName: "+attachmentName);
			System.out.println("AttachmentSize: "+attachmentSize);
			this.assertEquals(childName, "child://"+i, this.getInfo()+"/testComplexSerialization/ArrayMatchFailed");
			this.assertEquals(attachmentName, "attachment://"+i, this.getInfo()+"/testComplexSerialization/ArrayMatchFailed");
			this.assertEquals(attachmentSize, i+"K", this.getInfo()+"/testComplexSerialization/ArrayMatchFailed");
		}
	}
	
	private void testArraySetup() throws Exception
	{
		MobileObject mobileObject = new MobileObject();
		
		for(int i=0; i<5; i++)
		{
			mobileObject.setValue("fruits["+i+"]", "fruit://"+i);
		}
		
		String xml = DeviceSerializer.getInstance().serialize(mobileObject);
		
		System.out.println(xml);
	}
	//-----------------------------------------------------------------------------------------------------------------------
	private MobileObject createPOJOWithStrings(String name)
	{
		MobileObject mo = new MobileObject();
		
		mo.setValue("value", name);
		
		for(int i=0; i<5; i++)
		{
			Map<String,String> properties = new HashMap<String, String>();
			properties.put("", "string://"+i);
			mo.addToArray("strings", properties);
		}
		
		return mo;
	}
	
	private MobileObject createNestedPOJO(String name)
	{
		MobileObject mo = new MobileObject();
		
		mo.setValue("name", name);
		
		for(int i=0; i<2; i++)
		{
			//mo.setValue("child.children["+i+"].childName", "child://"+i);
			//mo.setValue("child.children["+i+"].attachment.name", "attachment://"+i);
			//mo.setValue("child.children["+i+"].attachment.size", i+"K");
			
			Map<String,String> properties = new HashMap<String, String>();
			properties.put("/childName", "child://"+i);
			properties.put("/attachment/name", "attachment://"+i);
			properties.put("/attachment/size", i+"K");
			mo.addToArray("child.children", properties);
		}
		
		return mo;
	}
}
