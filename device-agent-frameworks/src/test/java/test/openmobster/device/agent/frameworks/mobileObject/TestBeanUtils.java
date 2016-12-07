/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.frameworks.mobileObject;

import java.beans.PropertyDescriptor;
import java.util.StringTokenizer;

import junit.framework.TestCase;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.log4j.Logger;

import org.openmobster.device.agent.frameworks.mobileObject.DeviceSerializer;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;

/**
 * @author openmobster@gmail.com
 */
public class TestBeanUtils extends TestCase
{
	private static Logger log = Logger.getLogger(TestBeanUtils.class);
	
	@Override
	protected void setUp() throws Exception 
	{	
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception 
	{	
		super.tearDown();
	}
	
	public void testPropertySetting() throws Exception
	{
		Object pojo = Thread.currentThread().getContextClassLoader().
		loadClass("test.openmobster.device.agent.frameworks.mobileObject.MockPOJO").newInstance();
		
		//Set Simple Property
		String simpleProperty = "value";
		PropertyUtils.setProperty(pojo, simpleProperty, "parent");
		
		//Set Nested Property
		String nestedProperty = "child.value";
		StringTokenizer st = new StringTokenizer(nestedProperty, ".");
		Object courObj = pojo;
		while(st.hasMoreTokens())
		{
			String token = st.nextToken();								
			if(!st.hasMoreTokens())
			{
				PropertyDescriptor metaData = PropertyUtils.getPropertyDescriptor(courObj, token);
				PropertyUtils.setNestedProperty(pojo, nestedProperty, ConvertUtils.convert("child", metaData.getPropertyType()));
			}
			else
			{
				PropertyDescriptor metaData = PropertyUtils.getPropertyDescriptor(courObj, token);				
				if(PropertyUtils.getProperty(courObj, token) == null)
				{
					Object nestedObj = metaData.getPropertyType().newInstance();
					PropertyUtils.setProperty(courObj, token, nestedObj);
					courObj = nestedObj;
				}
				else
				{
					courObj = PropertyUtils.getProperty(courObj, token);
				}
			}
		}
		
		//Set Nested Property non-string
		nestedProperty = "child.id";
		st = new StringTokenizer(nestedProperty, ".");
		courObj = pojo;
		while(st.hasMoreTokens())
		{
			String token = st.nextToken();								
			if(!st.hasMoreTokens())
			{
				PropertyDescriptor metaData = PropertyUtils.getPropertyDescriptor(courObj, token);
				PropertyUtils.setNestedProperty(pojo, nestedProperty, ConvertUtils.convert("123", metaData.getPropertyType()));
			}
			else
			{
				PropertyDescriptor metaData = PropertyUtils.getPropertyDescriptor(courObj, token);				
				if(PropertyUtils.getProperty(courObj, token) == null)
				{
					Object nestedObj = metaData.getPropertyType().newInstance();
					PropertyUtils.setProperty(courObj, token, nestedObj);
					courObj = nestedObj;
				}
				else
				{
					courObj = PropertyUtils.getProperty(courObj, token);
				}
			}
		}
		
		
		//Set Indexed Property		
		//String indexedProperty = "childArray[0]";
		//st = new StringTokenizer(indexedProperty, ".");
		//courObj = pojo;
		//while(st.hasMoreTokens())
		//{
		//	String token = st.nextToken();								
		//	if(!st.hasMoreTokens())
		//	{
		//		PropertyDescriptor metaData = PropertyUtils.getPropertyDescriptor(courObj, token);
		//		PropertyUtils.setIndexedProperty(pojo, indexedProperty, ConvertUtils.convert("child://0", metaData.getPropertyType()));				
		//	}
		//	else
		//	{
				/*if(token.indexOf('[') != -1)
				{
					token = token.substring(0, token.indexOf('['));
				}*/
				
		//		PropertyDescriptor metaData = PropertyUtils.getPropertyDescriptor(courObj, token);				
		//		if(PropertyUtils.getProperty(courObj, token) == null)
		//		{
		//			Object nestedObj = metaData.getPropertyType().newInstance();
		//			PropertyUtils.setProperty(courObj, token, nestedObj);
		//			courObj = nestedObj;
		//		}
		//		else
		//		{
		//			courObj = PropertyUtils.getProperty(courObj, token);
		//		}
		//	}
		//}
		
		
		//Assert
		String[] childArray = ((MockPOJO)pojo).getChildArray();
		assertEquals("Value does not match", ((MockPOJO)pojo).getValue(), "parent");		
		assertEquals("Value does not match", ((MockPOJO)pojo).getChild().getValue(), "child");
		assertEquals("Value does not match", ((MockPOJO)pojo).getChild().getId(), 123);
		//assertEquals("Value does not match", childArray[0], "child://0");
	}
	
	public void testDeviceSideMobileObjectCreation() throws Exception
	{
		MobileObject mo = new MobileObject();
		
		mo.setValue("value", "parent");
		mo.setValue("child.value", "child");
		mo.setValue("children[0].value", "child://0");
		
		log.info("-----------------------------------------");
		log.info("Value="+mo.getValue("value"));
		log.info("Child.Value="+mo.getValue("child.value"));
		log.info("Children[0].Value="+mo.getValue("children[0].value"));
		log.info("XML="+DeviceSerializer.getInstance().serialize(mo));
		log.info("-----------------------------------------");
	}
}
