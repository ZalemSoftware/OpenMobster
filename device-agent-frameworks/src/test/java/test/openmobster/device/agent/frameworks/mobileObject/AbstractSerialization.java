/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.frameworks.mobileObject;

import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.mobileObject.xml.MobileObjectSerializer;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.frameworks.mobileObject.DeviceSerializer;

/**
 * @author openmobster@gmail.com
 */
public class AbstractSerialization extends TestCase 
{	
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

	
	protected String serverSerialize(Object pojo)
	{
		String serializedXml = null;
		
		serializedXml = this.serializer.serialize(pojo);
		
		return serializedXml;
	}
	
		
	protected Object serverDeserialize(Class objectClass, String xml)
	{
		Object pojo = null;
				
		pojo = this.serializer.deserialize(objectClass, xml);
		
		return pojo;
	}
	
	protected MobileObject deviceDeserialize(String xml)
	{
		MobileObject object = null;
				
		object = DeviceSerializer.getInstance().deserialize(xml);
				
		return object;
	}	
}
