/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package test.openmobster.device.agent.sync;

import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;
import test.openmobster.device.agent.sync.server.ServerRecord;
import org.openmobster.core.mobileObject.xml.MobileObjectSerializer;

/**
 *
 * @author openmobster@gmail.com
 */
public class TestArraySerialization extends TestCase
{
	private MobileObjectSerializer serializer;
	
	public void setUp()
	{
		ServiceManager.bootstrap();
		
		this.serializer = (MobileObjectSerializer)ServiceManager.locate("mobileObject://MobileObjectSerializer");
	}
	
	public void tearDown()
	{
		ServiceManager.shutdown();
	}
	
	public void testSerialization()
	{
		String deviceXml = "<mobileObject createdOnDevice='true'>" +
				"<recordId>7aa721f6-f568-4086-a40b-b6cedb28910b</recordId>" +
				"<serverRecordId></serverRecordId>" +
				"<object>" +
				"<fields>" +
				"<field><uri>/from</uri><name>from</name><value>newBean/From</value></field>" +
				"<field><uri>/fruits[0]</uri><name>fruits[0]</name><value>0://fruit</value></field>" +
				"<field><uri>/fruits[1]</uri><name>fruits[1]</name><value>1://fruit</value></field>" +
				"<field><uri>/fruits[2]</uri><name>fruits[2]</name><value>2://fruit</value></field>" +
				"</fields>" +
				"<metadata>" +
				"<array-metadata>" +
				"<uri>/fruits</uri>" +
				"<array-length>3</array-length>" +
				"<array-class></array-class>" +
				"</array-metadata>" +
				"</metadata>" +
				"</object>" +
				"</mobileObject>";
		
		Object deserializedObject = this.serializer.deserialize(ServerRecord.class, deviceXml);
		
		String objectXml = this.serializer.serialize(deserializedObject);
		
		System.out.println(objectXml);
	}
}
