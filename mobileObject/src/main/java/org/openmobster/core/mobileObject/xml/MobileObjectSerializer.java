/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileObject.xml;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.openmobster.core.common.XMLUtilities;
import org.openmobster.core.common.Utilities;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * @author openmobster@gmail.com
 */
public class MobileObjectSerializer 
{
	private static Logger log = Logger.getLogger(MobileObjectSerializer.class);
	
	private XStream streamer;
	private XStream stateChecker;
	
	public MobileObjectSerializer()
	{
		
	}
	
	public void start()
	{
		this.streamer = new XStream(new MobileObjectDriver())
		{
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new MobileObjectMapperWrapper(next);
            }
        }; 
        
        this.stateChecker = new XStream();
	}
	
	public void stop()
	{
		this.streamer = null;
	}
	
	public String serialize(Object object)
	{
		try
		{			
	        String coreXml = this.stateChecker.toXML(object);
	        if(coreXml.contains("<null/>"))
	        {
	        	throw new IllegalStateException("The Object being mobilized has Illegal Null Array Elements!!");
	        }
	        
	        
	        return this.streamer.toXML(object);
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new RuntimeException(e);
		}
	}
	
	public Object deserialize(Class pojoClazz, String deviceXml)
	{
		try
		{
			Object pojo = null;
			
			pojo = pojoClazz.newInstance();
														
			Document root = XMLUtilities.parse(deviceXml);
			
			//Parse the Object Meta Data
			List<ArrayMetaData> objectMetaData = new ArrayList<ArrayMetaData>();
			NodeList metaDataNodes = root.getElementsByTagName("array-metadata");
			if(metaDataNodes != null)
			{
				for(int i=0; i<metaDataNodes.getLength(); i++)
				{
					Element metaDataElement = (Element)metaDataNodes.item(i);
					Element arrayUriElement = (Element)metaDataElement.getElementsByTagName("uri").item(0);
					Element arrayLengthElement = (Element)metaDataElement.getElementsByTagName("array-length").item(0);
					Element arrayClassElement = (Element)metaDataElement.getElementsByTagName("array-class").item(0);
					
					ArrayMetaData arrayMetaData = new ArrayMetaData();
					arrayMetaData.arrayUri = arrayUriElement.getTextContent().trim();
					arrayMetaData.arrayLength = Integer.parseInt(arrayLengthElement.getTextContent().trim());
					arrayMetaData.arrayClass = arrayClassElement.getTextContent().trim();
					
					objectMetaData.add(arrayMetaData);
				}
			}
			
			//Set the fields
			Element fieldsElement = (Element)root.getElementsByTagName("fields").item(0);
			if(fieldsElement != null)
			{
				NodeList fieldNodes = fieldsElement.getElementsByTagName("field");
				if(fieldNodes != null)
				{
					for(int i=0; i<fieldNodes.getLength(); i++)
					{
						Element fieldElement = (Element)fieldNodes.item(i);
						
						String name = ((Element)fieldElement.
						getElementsByTagName("name").item(0)).getTextContent();		
						
						String value = ((Element)fieldElement.
						getElementsByTagName("value").item(0)).getTextContent();
						
						String uri = ((Element)fieldElement.
						getElementsByTagName("uri").item(0)).getTextContent();		
						
						String expression = this.parseExpression(uri);
						
						if(expression.indexOf('.') == -1 && expression.indexOf('[')==-1)
						{
							//Simple Property
							PropertyDescriptor metaData = PropertyUtils.getPropertyDescriptor(pojo, 
							expression);
							
							if(metaData == null || metaData.getPropertyType() == null)
							{
								log.error("******************************");
								log.error("MetaData Null For: "+expression);
								log.error("Field Not Found on the MobileBean");
								log.error("******************************");
								continue;
							}
							
							if(metaData.getPropertyType().isArray() && 
							metaData.getPropertyType().getComponentType().isAssignableFrom(byte.class))
							{
								BeanUtils.setProperty(pojo, expression, 
								Utilities.decodeBinaryData(value));
							}
							else
							{
								BeanUtils.setProperty(pojo, expression, value);
							}							
						}
						else
						{
							//Nested Property
							this.setNestedProperty(pojo, expression, value, objectMetaData);
						}
					}
				}
			}
			
			return pojo;
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new RuntimeException(e);
		}
	}
	//---------------------------------------------------------------------------------------------------------------------------
	private String parseExpression(String fieldUri)
	{
		String expression = "";
		StringBuilder buffer = new StringBuilder();
		
		StringTokenizer st = new StringTokenizer(fieldUri, "/");
		while(st.hasMoreTokens())
		{
			String token = st.nextToken();			
			if(!token.contains("."))
			{
				buffer.append(token+"/");
			}
		}
		
		String local = buffer.toString();
		if(local.endsWith("/"))
	    {
	    	local = local.substring(0, local.length()-1);
	    }
		
		expression = local.replace("/", ".");
		
		return expression;
	}
	
	private void setNestedProperty(Object mobileBean, String nestedProperty, String value,
	List<ArrayMetaData> objectMetaData) 
	{
		try
		{
			StringTokenizer st = new StringTokenizer(nestedProperty, ".");
			Object courObj = mobileBean;
			StringBuilder propertyPath = new StringBuilder();
					
			while(st.hasMoreTokens())
			{
				String token = st.nextToken();			
				propertyPath.append("/"+token);
				
				PropertyDescriptor metaData = PropertyUtils.getPropertyDescriptor(courObj, token);			
				if(token.indexOf('[')!=-1 && token.indexOf(']')!=-1)
				{
					String indexedPropertyName = token.substring(0, token.indexOf('['));
					metaData = PropertyUtils.getPropertyDescriptor(courObj,indexedPropertyName);
				}
				
				if(metaData == null)
				{
					log.error("******************************");
					log.error("MetaData Null For: "+token);
					log.error("Field Not Found on the MobileBean");
					log.error("******************************");
					continue;
				}
							
				if(!st.hasMoreTokens())
				{				
					if(Collection.class.isAssignableFrom(metaData.getPropertyType()) ||
					   (metaData.getPropertyType().isArray() && !metaData.getPropertyType().getComponentType().
						isAssignableFrom(byte.class)))
					{
						//An IndexedProperty
						this.initializeIndexedProperty(courObj, token, 
						metaData, objectMetaData, propertyPath.toString());
						
						if(metaData.getPropertyType().isArray())
						{
							PropertyUtils.setNestedProperty(mobileBean, nestedProperty, 
							ConvertUtils.convert(value, metaData.getPropertyType().getComponentType()));
						}
						else
						{
							PropertyUtils.setNestedProperty(mobileBean, nestedProperty, 
							ConvertUtils.convert(value, metaData.getPropertyType()));
						}
					}
					else
					{
						//A Simple Property														
						if(metaData.getPropertyType().isArray() && 
						metaData.getPropertyType().getComponentType().isAssignableFrom(byte.class))
						{
							BeanUtils.setProperty(mobileBean, nestedProperty, 
							Utilities.decodeBinaryData(value));
						}
						else
						{
							PropertyUtils.setNestedProperty(mobileBean, nestedProperty, 
							ConvertUtils.convert(value, metaData.getPropertyType()));
						}
					}										
				}
				else
				{							
					if(Collection.class.isAssignableFrom(metaData.getPropertyType()) ||
					   metaData.getPropertyType().isArray()
					)
					{
						//An IndexedProperty	
						courObj = this.initializeIndexedProperty(courObj, token, 
						metaData, objectMetaData, propertyPath.toString());
					}				
					else
					{
						//A Simple Property
						courObj = this.initializeSimpleProperty(courObj, token, metaData);										
					}								
				}
			}
		}
		catch(Exception e)
		{
			log.info("---------------------------------------------------");
			log.info("Blowing Up on---------"+nestedProperty);
			log.info("---------------------------------------------------");
			log.error(this, e);
			throw new RuntimeException(e);
		}
	}
	
	private Object initializeSimpleProperty(Object parentObject, String property, 
	PropertyDescriptor propertyMetaData)
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
	PropertyDescriptor propertyMetaData,List<ArrayMetaData> objectMetaData, 
	String propertyPath) throws Exception
	{
		Object element = null;
		
		//ArrayUri
		String arrayUri = null;
		Integer arrayIndex = 0;
		if(propertyPath.endsWith("]"))
		{
			int lastIndex = propertyPath.lastIndexOf('[');
			arrayUri = propertyPath.substring(0, lastIndex);
			arrayIndex = Integer.parseInt(propertyPath.substring(lastIndex+1, propertyPath.length()-1).trim());
		}
		ArrayMetaData arrayMetaData = null;
		for(ArrayMetaData local: objectMetaData)
		{
			if(local.arrayUri.equals(arrayUri))
			{
				arrayMetaData = local;
				break;
			}
		}
				
		//Find the Class of the elementType
		String elementTypeName = arrayMetaData.arrayClass;		
		Class elementType = null; 		
		if(elementTypeName != null && elementTypeName.trim().length()>0 && !elementTypeName.equals("null"))
		{
			elementType = Thread.currentThread().getContextClassLoader().
			loadClass(arrayMetaData.arrayClass);
		}
		else
		{
			//Figure out the element type from the Property Information
			//This happens when a brand new object is created on the device and is being synced
			//with the backend
			//The MobileObject Framework on the device does not know about any Class level information
			//of the remote bean
			//The Limitation of this is that:
			//
			//* Indexed Properties if Collections must be Parameterized with Concrete Types
			//* Indexed Properties if Arrays must be Arrays of Concrete Types
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
		}
				
				
		//An IndexedProperty
		Object indexedProperty = PropertyUtils.getProperty(parentObject, propertyMetaData.getName());
		
		//Initialize the IndexedProperty (An Array or Collection)
		if(propertyMetaData.getPropertyType().isArray())
		{
			int arraySize = arrayMetaData.arrayLength;
			if(indexedProperty == null)
			{
				//Initialize the Array with Size from Object Meta Data					
				PropertyUtils.setProperty(parentObject, propertyMetaData.getName(),
				Array.newInstance(elementType, arraySize));
			}
			else
			{
				//Make sure the Array Size matches
				int actualSize = Array.getLength(indexedProperty);
				if(actualSize != arraySize)
				{
					//Re-set the existing Array
					PropertyUtils.setProperty(parentObject, propertyMetaData.getName(),
					Array.newInstance(elementType, arraySize));
				}
			}
		}
		else
		{
			if(indexedProperty == null)
			{
				//Handle Collection Construction
				PropertyUtils.setProperty(parentObject, propertyMetaData.getName(), new ArrayList());
			}			
		}
		
					
		//Check to see if the index specified by the field requires creation of new
		//element
		indexedProperty = PropertyUtils.getProperty(parentObject, propertyMetaData.getName());
		
		if(!propertyMetaData.getPropertyType().isArray())
		{
			try
			{
				element = PropertyUtils.getIndexedProperty(parentObject, property);
			}
			catch(IndexOutOfBoundsException iae)
			{
				Object newlyInitialized = elementType.newInstance();							
				((Collection)indexedProperty).add(newlyInitialized);										
				element = newlyInitialized;
			}
		}
		else
		{
			element = PropertyUtils.getIndexedProperty(parentObject, property);
			if(element == null)
			{
				Object newlyInitialized = elementType.newInstance();
				Array.set(indexedProperty, arrayIndex, newlyInitialized);
				element = newlyInitialized;
			}
		}
		
				
		return element;
	}
}
