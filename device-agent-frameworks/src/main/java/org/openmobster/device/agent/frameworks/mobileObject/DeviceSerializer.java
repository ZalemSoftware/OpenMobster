/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.frameworks.mobileObject;

import java.util.List;
import org.openmobster.core.common.XMLUtilities;

/**
 * @author openmobster@gmail.com
 */
public final class DeviceSerializer 
{
	private static DeviceSerializer singleton;
	
	private DeviceSerializer()
	{
		
	}
	
	public static DeviceSerializer getInstance()
	{
		if(singleton == null)
		{
			singleton = new DeviceSerializer();
		}
		return singleton;
	}
	
	public String serialize(MobileObject mobileObject)
	{		
		StringBuffer xmlBuffer = new StringBuffer();
		
		if(!mobileObject.isCreatedOnDevice())
		{
			xmlBuffer.append("<mobileObject>\n");
		}
		else
		{
			xmlBuffer.append("<mobileObject createdOnDevice='true'>\n");
		}
		xmlBuffer.append("<recordId>"+XMLUtilities.cleanupXML(mobileObject.getRecordId())+"</recordId>\n");
		xmlBuffer.append("<serverRecordId>"+XMLUtilities.cleanupXML(mobileObject.getServerRecordId())+"</serverRecordId>\n");
		xmlBuffer.append("<object>\n");
		
		//Serialize the Fields
		List<Field> fields = mobileObject.getFields();
		if(fields != null && !fields.isEmpty())
		{
			xmlBuffer.append("<fields>\n");
			for(Field field: fields)
			{
				xmlBuffer.append("<field>\n");
				xmlBuffer.append("<uri>"+field.getUri()+"</uri>\n");
				xmlBuffer.append("<name>"+field.getName()+"</name>\n");
				xmlBuffer.append("<value>"+XMLUtilities.cleanupXML(field.getValue())+"</value>\n");
				xmlBuffer.append("</field>\n");
			}
			xmlBuffer.append("</fields>\n");
		}
		
		//Serialize the Array Meta Data
		List<ArrayMetaData> arrayMetaData = mobileObject.getArrayMetaData();
		if(arrayMetaData != null && !arrayMetaData.isEmpty())
		{
			xmlBuffer.append("<metadata>\n");
			
			for(ArrayMetaData metaData: arrayMetaData)
			{
				xmlBuffer.append("<array-metadata>\n");
				xmlBuffer.append("<uri>"+metaData.getArrayUri()+"</uri>\n");
				xmlBuffer.append("<array-length>"+metaData.getArrayLength()+"</array-length>\n");
				xmlBuffer.append("<array-class>"+metaData.getArrayClass()+"</array-class>\n");
				xmlBuffer.append("</array-metadata>\n");
			}
			
			xmlBuffer.append("</metadata>\n");
		}
		
		xmlBuffer.append("</object>\n");
		xmlBuffer.append("</mobileObject>\n");
				
		return xmlBuffer.toString();
	}
	
	public MobileObject deserialize(String xml)
	{
		MobileObjectReader reader = new MobileObjectReader();		
		return reader.parse(xml);
	}	
}
