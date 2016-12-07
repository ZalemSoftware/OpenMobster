/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.Array;

import java.beans.PropertyDescriptor;
import java.util.StringTokenizer;
import java.util.Collection;
import java.util.ArrayList;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import junit.framework.TestCase;

/**
 * @author openmobster@gmail.com
 */
public class TestBeanSyntax extends TestCase
{
	private static Logger log = Logger.getLogger(TestBeanSyntax.class);
	
	public void testIndexedProperties() throws Exception
	{
		MockPOJO pojo = new MockPOJO();
				
		this.setNestedProperty(pojo, "children[0].parent.value", "embeddedParent");
		this.setNestedProperty(pojo, "children[0].parent.strings[0]", "embeddedStrings");
		this.setNestedProperty(pojo, "childArray[0]", "stringArray://0");
		this.setNestedProperty(pojo, "child.parent.value", "child://parent");
		
		//Assert the state
		this.assertTrue("children must not be null!!!", pojo.getChildren()!=null && 
		!pojo.getChildren().isEmpty());
		MockChild child = pojo.getChildren().get(0);
		assertEquals("Value must match!!", "embeddedParent", child.getParent().getValue());
		assertEquals("Value must match!!", "child://parent", pojo.getChild().getParent().getValue());
	}
	//----------------------------------------------------------------------------------------------
	private void setNestedProperty(Object mobileBean, String nestedProperty, String value)
	throws Exception
	{		
		StringTokenizer st = new StringTokenizer(nestedProperty, ".");
		Object courObj = mobileBean;
				
		while(st.hasMoreTokens())
		{
			String token = st.nextToken();			
			
			PropertyDescriptor metaData = PropertyUtils.getPropertyDescriptor(courObj, token);			
			if(token.indexOf('[')!=-1 && token.indexOf(']')!=-1)
			{
				String indexedPropertyName = token.substring(0, token.indexOf('['));
				metaData = PropertyUtils.getPropertyDescriptor(courObj,indexedPropertyName);
			}
						
			if(!st.hasMoreTokens())
			{	
				if(Collection.class.isAssignableFrom(metaData.getPropertyType()) ||
						   metaData.getPropertyType().isArray())
				{
					//An IndexedProperty						
					courObj = this.initializeIndexedProperty(courObj, token, metaData);															
				}
				
				//Actually set the value of the property
				if(!metaData.getPropertyType().isArray())
				{
					PropertyUtils.setNestedProperty(mobileBean, nestedProperty, 
					ConvertUtils.convert(value, metaData.getPropertyType()));
				}
				else
				{
					PropertyUtils.setNestedProperty(mobileBean, nestedProperty, 
					ConvertUtils.convert(value, metaData.getPropertyType().getComponentType()));
				}
			}
			else
			{							
				if(Collection.class.isAssignableFrom(metaData.getPropertyType()) ||
				   metaData.getPropertyType().isArray())
				{
					//An IndexedProperty						
					courObj = this.initializeIndexedProperty(courObj, token, metaData);
				}				
				else
				{
					//A Simple Property
					courObj = this.initializeSimpleProperty(courObj, token, metaData);										
				}								
			}
		}
	}
	
	private Object initializeSimpleProperty(Object parentObject, String property, PropertyDescriptor propertyMetaData)
	throws Exception
	{
		Object propertyValue = null;
		
		//A Regular Property
		propertyValue = PropertyUtils.getProperty(parentObject, property); 
		if(propertyValue == null)
		{
			Object newlyInitialized = propertyMetaData.getPropertyType().newInstance();
			PropertyUtils.setProperty(parentObject, property, newlyInitialized);
			propertyValue = newlyInitialized;
		}		
		
		return propertyValue;
	}
	
	private Object initializeIndexedProperty(Object parentObject, String property, 
	PropertyDescriptor propertyMetaData) throws Exception
	{
		Object element = null;
		
		//Find the Class of the elementType
		Class elementType = null;
		
		if(!propertyMetaData.getPropertyType().isArray())
		{
			ParameterizedType returnType = (ParameterizedType)propertyMetaData.
			getReadMethod().getGenericReturnType();
			Type[] actualTypes = returnType.getActualTypeArguments();		
			for(Type actualType: actualTypes)
			{
				elementType = (Class)actualType;
			}
		}
		else
		{
			elementType = propertyMetaData.getPropertyType().getComponentType();
		}
				
		//An IndexedProperty
		Object indexedProperty = PropertyUtils.getProperty(parentObject, propertyMetaData.getName());
		
		//Initialize the IndexedProperty (An Array or Collection)
		if(indexedProperty == null)
		{						
			if(propertyMetaData.getPropertyType().isArray())
			{
				//TODO: Remove hardcoded array size							
				PropertyUtils.setProperty(parentObject, propertyMetaData.getName(),
				Array.newInstance(elementType, 1));
			}
			else
			{
				//Handle Collection Construction
				PropertyUtils.setProperty(parentObject, propertyMetaData.getName(), new ArrayList());
			}
			indexedProperty = PropertyUtils.getProperty(parentObject, propertyMetaData.getName());
		}
		
		//Check to see if the index specified by the field requires creation of new
		//element
		try
		{
			element = PropertyUtils.getIndexedProperty(parentObject, property);
		}
		catch(IndexOutOfBoundsException iae)
		{
			Object newlyInitialized = elementType.newInstance();
						
			if(!propertyMetaData.getPropertyType().isArray())
			{
				((Collection)indexedProperty).add(newlyInitialized);
			}
			else
			{
				//TODO: Remove hardcoded array index
				Array.set(indexedProperty, 0, newlyInitialized);
			}
									
			element = newlyInitialized;
		}
		
				
		return element;
	}
}
