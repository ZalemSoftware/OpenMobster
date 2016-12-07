/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.frameworks.mobileObject;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.device.agent.frameworks.mobileObject.DeviceSerializer;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObjectDatabase;

/**
 * @author openmobster@gmail.com
 */
public class TestMobileObjectPersistence extends AbstractSerialization 
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(TestMobileObjectPersistence.class);
	
	private MobileObjectDatabase mobileObjectDatabase = null;
		
	public void setUp() throws Exception
	{
		super.setUp();
		
		this.mobileObjectDatabase = (MobileObjectDatabase)
		ServiceManager.locate("mobileObject://MobileObjectDatabase");
	}
	
	public void tearDown() throws Exception
	{
		super.tearDown();
		
		this.mobileObjectDatabase = null;
	}
	//------------------------------------------------------------------------------------------------
	public void testLevelOne()
	{
		List node1 = new ArrayList();
		for(int i=0; i<5; i++)
		{
			node1.add(new Node1("node1://"+i));
		}
		Root root = new Root("root");
		root.setNode1(node1);
		
		String xml = this.serverSerialize(root);
		MobileObject mo = this.deviceDeserialize(xml);
		
		log.info("XML From Server------------------------------------");
		log.info(xml);
		
		//Persist the mobileobject into the database, and re-read
		mo.setRecordId("mock");
		mo.setStorageId("mock");
		String newId = this.mobileObjectDatabase.create(mo);
		mo = this.mobileObjectDatabase.read(mo.getStorageId(), mo.getRecordId());
		
		this.assertLevelOne(5, xml, mo);
		
		String deserializedXml = DeviceSerializer.getInstance().serialize(mo);
		log.info("XML From Device------------------------------------");
		log.info(deserializedXml);
		
		Root deMo = (Root)this.serverDeserialize(Root.class, deserializedXml);
		
		this.assertLevelOneDeserialization(5, deserializedXml, deMo);
		
		//Update the mobile object, Deserialize and assert the updated state
		this.assertLevelOneDeserializationWithUpdate(5, mo);		
	}	
	//------------------------------------------------------------------------------------------------
	private void assertLevelOne(int size,String xml, MobileObject mo)
	{
		log.info("----------------------------------------------------");
		log.info(xml);
		log.info("----------------------------------------------------");
		
		log.info("value="+mo.getValue("value"));
		assertEquals("Read Failure", mo.getValue("value"), "root");
		for(int i=0; i<size; i++)
		{
			log.info("node1["+i+"]="+mo.getValue("node1["+i+"].value"));
			assertEquals("Read Failure", mo.getValue("node1["+i+"].value"), "node1://"+i);
		}
	}	
	
	private void assertLevelOneDeserialization(int size, String xml, Root root)
	{
		log.info("----------------------------------------------------");	
		log.info(xml);
		log.info("----------------------------------------------------");
		log.info("value="+root.getValue());
		assertEquals("Deserialization Failure", root.getValue(), "root");
		assertEquals("Deserialization Failure", root.getNode1().size(), size);
		for(int i=0; i<size; i++)
		{
			Node1 node1 = (Node1)root.getNode1().get(i);
			log.info("node1["+i+"]="+node1.getValue());
			assertEquals("Deserialization Failure", node1.getValue(), "node1://"+i);
		}
	}
	
	private void assertLevelOneDeserializationWithUpdate(int size, MobileObject mo)
	{
		mo.setValue("value", "root://updated");
		for(int i=0; i<size; i++)
		{
			mo.setValue("node1["+i+"].value", "node1://updated/"+i);
		}
		
		String deserializedXml = DeviceSerializer.getInstance().serialize(mo);		
		Root root = (Root)this.serverDeserialize(Root.class, deserializedXml);
		log.info("----------------------------------------------------");	
		log.info(deserializedXml);
		log.info("----------------------------------------------------");
		log.info("value="+root.getValue());
		
		assertEquals("Deserialization Failure", root.getValue(), "root://updated");
		assertEquals("Deserialization Failure", root.getNode1().size(), size);
		for(int i=0; i<size; i++)
		{
			Node1 node1 = (Node1)root.getNode1().get(i);
			log.info("node1["+i+"]="+node1.getValue());
			assertEquals("Deserialization Failure", node1.getValue(), "node1://updated/"+i);
		}
	}
}
