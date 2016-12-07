/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.mobileObject;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.openmobster.core.mobileCloud.android.testsuite.Test;


/**
 * @author openmobster@gmail.com
 */
public class TestMobileObject extends Test 
{
	@Override
	public void runTest()
	{
		try
		{
			this.testArrayRemove();
			this.testArrayMultiplePropertiesBug();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	private void testArrayRemove() throws Exception
	{
		System.out.println("Starting testArrayRemove...");
		
		int size = 5;
		MobileObject mobileObject = this.createArrayPOJO("arrayPOJO", size);
		
		//Assert Array
		int arrayLength = mobileObject.getArrayLength("child.children");
		this.assertTrue(arrayLength == size, "/TestMobileObject/testArrayRemove/arrayLength/MisMatch");
		for(int i=0; i<arrayLength; i++)
		{
			Map<String,String> arrayElement = mobileObject.getArrayElement("child.children", i);
			
			String childName = arrayElement.get("/childName");
			String attachmentName = arrayElement.get("/attachment/name");
			String attachmentSize = arrayElement.get("/attachment/size");
			System.out.println("ChildName: "+childName);
			System.out.println("AttachmentName: "+attachmentName);
			System.out.println("AttachmentSize: "+attachmentSize);
			System.out.println("------------------------------------");
			
			//Assert
			this.assertEquals(childName, "child://"+i, "/TestMobileObject/testArrayRemove/firstPass/childName");
			this.assertEquals(attachmentName, "attachment://"+i, "/TestMobileObject/testArrayRemove/firstPass/attachmentName");
			this.assertEquals(attachmentSize, i+"K", "/TestMobileObject/testArrayRemove/firstPass/attachmentSize");
		}
		
		//Remove ArrayElements from the middle
		mobileObject.removeArrayElement("child.children", 1);
		arrayLength = mobileObject.getArrayLength("child.children");
		for(int i=0;i<arrayLength;i++)
		{
			Map<String,String> arrayElement = mobileObject.getArrayElement("child.children", i);
			
			String childName = arrayElement.get("/childName");
			String attachmentName = arrayElement.get("/attachment/name");
			String attachmentSize = arrayElement.get("/attachment/size");
			System.out.println("ChildName: "+childName);
			System.out.println("AttachmentName: "+attachmentName);
			System.out.println("AttachmentSize: "+attachmentSize);
			System.out.println("------------------------------------");
			
			//Assert
			int expected = i;
			if(i > 0)
			{
				expected++;
			}
			
			this.assertEquals(childName, "child://"+expected, "/TestMobileObject/testArrayRemove/removeFromMiddle/childName");
			this.assertEquals(attachmentName, "attachment://"+expected, "/TestMobileObject/testArrayRemove/removeFromMiddle/attachmentName");
			this.assertEquals(attachmentSize, expected+"K", "/TestMobileObject/testArrayRemove/removeFromMiddle/attachmentSize");
		}
		
		//Remove ArrayElements from the top
		mobileObject.removeArrayElement("child.children", 0);
		arrayLength = mobileObject.getArrayLength("child.children");
		for(int i=0;i<arrayLength;i++)
		{
			Map<String,String> arrayElement = mobileObject.getArrayElement("child.children", i);
			
			String childName = arrayElement.get("/childName");
			String attachmentName = arrayElement.get("/attachment/name");
			String attachmentSize = arrayElement.get("/attachment/size");
			System.out.println("ChildName: "+childName);
			System.out.println("AttachmentName: "+attachmentName);
			System.out.println("AttachmentSize: "+attachmentSize);
			System.out.println("------------------------------------");
			
			//Assert
			int expected = i+2;
			this.assertEquals(childName, "child://"+expected, "/TestMobileObject/testArrayRemove/removeFromTop/childName");
			this.assertEquals(attachmentName, "attachment://"+expected, "/TestMobileObject/testArrayRemove/removeFromTop/attachmentName");
			this.assertEquals(attachmentSize, expected+"K", "/TestMobileObject/testArrayRemove/removeFromTop/attachmentSize");
		}
		
		//Remove ArrayElements from the bottom
		mobileObject.removeArrayElement("child.children", arrayLength-1);
		arrayLength = mobileObject.getArrayLength("child.children");
		for(int i=0;i<arrayLength;i++)
		{
			Map<String,String> arrayElement = mobileObject.getArrayElement("child.children", i);
			
			String childName = arrayElement.get("/childName");
			String attachmentName = arrayElement.get("/attachment/name");
			String attachmentSize = arrayElement.get("/attachment/size");
			System.out.println("ChildName: "+childName);
			System.out.println("AttachmentName: "+attachmentName);
			System.out.println("AttachmentSize: "+attachmentSize);
			System.out.println("------------------------------------");
			
			//Assert
			int expected = i+2;
			this.assertEquals(childName, "child://"+expected, "/TestMobileObject/testArrayRemove/removeFromBottom/childName");
			this.assertEquals(attachmentName, "attachment://"+expected, "/TestMobileObject/testArrayRemove/removeFromBottom/attachmentName");
			this.assertEquals(attachmentSize, expected+"K", "/TestMobileObject/testArrayRemove/removeFromBottom/attachmentSize");
		}
	}
	
	private void testArrayMultiplePropertiesBug() throws Exception
	{
		System.out.println("Starting testArrayMultiplePropertiesBug...");
		
		int size = 5;
		MobileObject mobileObject = this.createArrayPOJO("arrayPOJO", size);
		
		this.print(mobileObject);
		
		//Assert
		this.assertTrue(mobileObject.getArrayMetaData().size()==1, "/TestMobileObject/testArrayMultiplePropertiesBug/arraySizeFailure");
	}
	//----------------------------------------------------------------------------------------------------------------------
	private MobileObject createPOJOWithStrings(String value)
	{
		MobileObject mobileObject = new MobileObject();
		
		mobileObject.setRecordId("recordId");
		mobileObject.setServerRecordId("serverRecordId");
		
		mobileObject.setValue("value", value);
		
		return mobileObject;
	}
	
	private MobileObject createArrayPOJO(String value, int size)
	{
		MobileObject mobileObject = this.createPOJOWithStrings(value);
		
		for(int i=0; i<size; i++)
		{
			Map<String,String> properties = new HashMap<String,String>();
			String childName = "child://"+i;
			String attachmentName = "attachment://"+i;
			String attachmentSize = i+"K";
			
			properties.put("/childName", childName);
			properties.put("/attachment/name", attachmentName);
			properties.put("/attachment/size", attachmentSize);
			
			mobileObject.addToArray("child.children", properties);
		}
		
		return mobileObject;
	}
	
	private void print(MobileObject mobileObject)
	{
		List<Field> fields = mobileObject.getFields();
		if(fields != null)
		{
			for(Field local:fields)
			{
				System.out.println("Uri: "+local.getUri());
				System.out.println("Name: "+local.getName());
				System.out.println("Value: "+local.getValue());
				System.out.println("-----------------------------------");
			}
		}
		else
		{
			System.out.println("Fields Not Found!!!");
		}
		
		List<ArrayMetaData> arrayMetaData = mobileObject.getArrayMetaData();
		if(arrayMetaData != null)
		{
			for(ArrayMetaData local:arrayMetaData)
			{
				System.out.println("ArrayUri: "+local.getArrayUri());
				System.out.println("ArrayLength: "+local.getArrayLength());
				System.out.println("ArrayClass: "+local.getArrayClass());
				System.out.println("-----------------------------------");
			}
		}
		else
		{
			System.out.println("Array Not Found!!!");
		}
	}
}
