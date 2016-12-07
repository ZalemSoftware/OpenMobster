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

import org.openmobster.device.agent.frameworks.mobileObject.DeviceSerializer;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;

/**
 * @author openmobster@gmail.com
 */
public class TestArraySerialization extends AbstractSerialization 
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(TestArraySerialization.class);	
	
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
		
		this.assertLevelOne(5, xml, mo);
		
		String deserializedXml = DeviceSerializer.getInstance().serialize(mo);		
		Root deMo = (Root)this.serverDeserialize(Root.class, deserializedXml);
		
		this.assertLevelOneDeserialization(5, deserializedXml, deMo);
		
		//Update the mobile object, Deserialize and assert the updated state
		this.assertLevelOneDeserializationWithUpdate(5, mo);
	}
	
	public void testLevelOneTwoElements()
	{
		List node1 = new ArrayList();
		for(int i=0; i<2; i++)
		{
			node1.add(new Node1("node1://"+i));
		}
		Root root = new Root("root");
		root.setNode1(node1);
		
		String xml = this.serverSerialize(root);
		MobileObject mo = this.deviceDeserialize(xml);
		
		this.assertLevelOne(2, xml, mo);
		
		String deserializedXml = DeviceSerializer.getInstance().serialize(mo);
		Root deMo = (Root)this.serverDeserialize(Root.class, deserializedXml);
		
		this.assertLevelOneDeserialization(2, deserializedXml, deMo);
		
		this.assertLevelOneDeserializationWithUpdate(2, mo);
	}
	
	public void testLevelOneSingleElement()
	{
		List node1 = new ArrayList();
		for(int i=0; i<1; i++)
		{
			node1.add(new Node1("node1://"+i));
		}
		Root root = new Root("root");
		root.setNode1(node1);
		
		String xml = this.serverSerialize(root);
		MobileObject mo = this.deviceDeserialize(xml);
		
		this.assertLevelOne(1, xml, mo);
		
		String deserializedXml = DeviceSerializer.getInstance().serialize(mo);
		Root deMo = (Root)this.serverDeserialize(Root.class,deserializedXml);
		
		this.assertLevelOneDeserialization(1, deserializedXml, deMo);
		
		this.assertLevelOneDeserializationWithUpdate(1, mo);
	}
		
	
	public void testLevelTwo()
	{		
		List node1 = new ArrayList();
		for(int i=0; i<5; i++)
		{
			Node1 outerNode = new Node1("node1://"+i, new ArrayList());
			node1.add(outerNode);
			for(int j=0; j<5; j++)
			{
				Node2 node2 = new Node2("node2://"+i+","+j);
				outerNode.getNode2().add(node2);
			}
		}
		
		Root root = new Root("root");
		root.setNode1(node1);
		
		String xml = this.serverSerialize(root);
		MobileObject mo = this.deviceDeserialize(xml);
		
		this.assertLevelTwo(5, 5, xml, mo);
		
		String deserializedXml = DeviceSerializer.getInstance().serialize(mo);		
		
		Root deMo = (Root)this.serverDeserialize(Root.class,deserializedXml);
		this.assertLevelTwoDeserialization(5, 5, deserializedXml, deMo);
		this.assertLevelTwoDeserializationWithUpdate(5,5, mo);
	}
	
	public void testLevelTwoElements()
	{		
		List node1 = new ArrayList();
		for(int i=0; i<2; i++)
		{
			Node1 outerNode = new Node1("node1://"+i, new ArrayList());
			node1.add(outerNode);
			for(int j=0; j<2; j++)
			{
				Node2 node2 = new Node2("node2://"+i+","+j);
				outerNode.getNode2().add(node2);
			}
		}
		
		Root root = new Root("root");
		root.setNode1(node1);
		
		String xml = this.serverSerialize(root);
		MobileObject mo = this.deviceDeserialize(xml);
		
		this.assertLevelTwo(2, 2, xml, mo);
		
		String deserializedXml = DeviceSerializer.getInstance().serialize(mo);
		Root deMo = (Root)this.serverDeserialize(Root.class,deserializedXml);
		this.assertLevelTwoDeserialization(2, 2, deserializedXml, deMo);
		this.assertLevelTwoDeserializationWithUpdate(2,2, mo);
	}
	
	public void testLevelTwoSingleElement()
	{		
		List node1 = new ArrayList();
		for(int i=0; i<1; i++)
		{
			Node1 outerNode = new Node1("node1://"+i, new ArrayList());
			node1.add(outerNode);
			for(int j=0; j<1; j++)
			{
				Node2 node2 = new Node2("node2://"+i+","+j);
				outerNode.getNode2().add(node2);
			}
		}
		
		Root root = new Root("root");
		root.setNode1(node1);
		
		String xml = this.serverSerialize(root);
		MobileObject mo = this.deviceDeserialize(xml);
		
		this.assertLevelTwo(1, 1, xml, mo);
		
		String deserializedXml = DeviceSerializer.getInstance().serialize(mo);
		Root deMo = (Root)this.serverDeserialize(Root.class,deserializedXml);
		this.assertLevelTwoDeserialization(1, 1, deserializedXml, deMo);
		this.assertLevelTwoDeserializationWithUpdate(1,1, mo);
	}
	
	
	public void testLevelThree()
	{		
		List node1 = new ArrayList();
		for(int i=0; i<5; i++)
		{
			Node1 outerNode1 = new Node1("node1://"+i, new ArrayList());
			node1.add(outerNode1);
			
			for(int j=0; j<5; j++)
			{
				Node2 node2 = new Node2("node2://"+i+","+j, new ArrayList());
				outerNode1.getNode2().add(node2);
				
				for(int k=0; k<5; k++)
				{
					Node3 node3 = new Node3("node3://"+i+","+j+","+k);
					node2.getNode3().add(node3);
				}
			}
		}
		
		Root root = new Root("root");
		root.setNode1(node1);
		
		String xml = this.serverSerialize(root);
		MobileObject mo = this.deviceDeserialize(xml);
		
		assertLevelThree(5, 5, 5, xml, mo);
		
		String deserializedXml = DeviceSerializer.getInstance().serialize(mo);
		Root deMo = (Root)this.serverDeserialize(Root.class,deserializedXml);
		this.assertLevelThreeDeserialization(5,5,5,deserializedXml, deMo);
		this.assertLevelThreeDeserializationWithUpdate(5,5,5,mo);
	}
	
	public void testLevelThreeTwoElements()
	{		
		List node1 = new ArrayList();
		for(int i=0; i<2; i++)
		{
			Node1 outerNode1 = new Node1("node1://"+i, new ArrayList());
			node1.add(outerNode1);
			
			for(int j=0; j<2; j++)
			{
				Node2 node2 = new Node2("node2://"+i+","+j, new ArrayList());
				outerNode1.getNode2().add(node2);
				
				for(int k=0; k<2; k++)
				{
					Node3 node3 = new Node3("node3://"+i+","+j+","+k);
					node2.getNode3().add(node3);
				}
			}
		}
		
		Root root = new Root("root");
		root.setNode1(node1);
		
		String xml = this.serverSerialize(root);
		MobileObject mo = this.deviceDeserialize(xml);
		
		assertLevelThree(2, 2, 2, xml, mo);
		
		String deserializedXml = DeviceSerializer.getInstance().serialize(mo);
		Root deMo = (Root)this.serverDeserialize(Root.class,deserializedXml);
		this.assertLevelThreeDeserialization(2,2,2,deserializedXml, deMo);
		this.assertLevelThreeDeserializationWithUpdate(2,2,2,mo);
	}
	
	public void testLevelThreeSingleElement()
	{		
		List node1 = new ArrayList();
		for(int i=0; i<1; i++)
		{
			Node1 outerNode1 = new Node1("node1://"+i, new ArrayList());
			node1.add(outerNode1);
			
			for(int j=0; j<1; j++)
			{
				Node2 node2 = new Node2("node2://"+i+","+j, new ArrayList());
				outerNode1.getNode2().add(node2);
				
				for(int k=0; k<1; k++)
				{
					Node3 node3 = new Node3("node3://"+i+","+j+","+k);
					node2.getNode3().add(node3);
				}
			}
		}
		
		Root root = new Root("root");
		root.setNode1(node1);
		
		String xml = this.serverSerialize(root);
		MobileObject mo = this.deviceDeserialize(xml);
		
		assertLevelThree(1, 1, 1, xml, mo);
		
		String deserializedXml = DeviceSerializer.getInstance().serialize(mo);
		Root deMo = (Root)this.serverDeserialize(Root.class,deserializedXml);
		this.assertLevelThreeDeserialization(1,1,1,deserializedXml, deMo);
		this.assertLevelThreeDeserializationWithUpdate(1,1,1,mo);
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
		
	private void assertLevelTwo(int node1Size, int node2Size,String xml, MobileObject mo)
	{
		log.info("----------------------------------------------------");
		log.info(xml);
		log.info("----------------------------------------------------");
		
		log.info("value="+mo.getValue("value"));
		assertEquals("Read Failure", mo.getValue("value"), "root");
		
		for(int i=0; i<node1Size; i++)
		{
			String node1Key = "node1["+i+"].value";
			log.info(mo.getValue(node1Key));
			assertEquals("Read Failure", mo.getValue(node1Key), "node1://"+i);
			
			for(int j=0; j<node2Size; j++)
			{
				String node2Key = "node1["+i+"].node2["+j+"].value"; 
				log.info(mo.getValue(node2Key));
				assertEquals("Read Failure", mo.getValue(node2Key), "node2://"+i+","+j);
			}
		}
	}
	
	private void assertLevelTwoDeserialization(int node1Size, 
	int node2Size, String xml, Root root)
	{
		log.info("----------------------------------------------------");
		log.info(xml);
		log.info("----------------------------------------------------");
		
		log.info("value="+root.getValue());
		assertEquals("Deserialization Failure", root.getValue(), "root");
		
		for(int i=0; i<node1Size; i++)
		{
			Node1 node1 = (Node1)root.getNode1().get(i);
			String node1Key = "node1["+i+"].value";
			log.info(node1Key+"="+node1.getValue());
			assertEquals("Deserialization Failure", node1.getValue(), "node1://"+i);
			
			for(int j=0; j<node2Size; j++)
			{
				Node2 node2 = (Node2)node1.getNode2().get(j);
				String node2Key = "node1["+i+"].node2["+j+"].value"; 
				log.info(node2Key+"="+node2.getValue());
				assertEquals("Deserialization Failure", node2.getValue(), "node2://"+i+","+j);
			}
		}
	}
	
	private void assertLevelTwoDeserializationWithUpdate(int node1Size, 
	int node2Size, MobileObject mo)
	{
		for(int i=0; i<node1Size; i++)
		{
			String node1Key = "node1["+i+"].value";
			mo.setValue(node1Key, "node1://updated/"+i);
						
			for(int j=0; j<node2Size; j++)
			{
				String node2Key = "node1["+i+"].node2["+j+"].value"; 
				mo.setValue(node2Key, "node2://updated/"+i+","+j);				
			}
		}
		
		String deserializedXml = DeviceSerializer.getInstance().serialize(mo);
		Root root = (Root)this.serverDeserialize(Root.class, deserializedXml);
		log.info("----------------------------------------------------");
		log.info(deserializedXml);
		log.info("----------------------------------------------------");
		
		log.info("value="+root.getValue());
		assertEquals("Deserialization Failure", root.getValue(), "root");
		
		for(int i=0; i<node1Size; i++)
		{
			Node1 node1 = (Node1)root.getNode1().get(i);
			String node1Key = "node1["+i+"].value";
			log.info(node1Key+"="+node1.getValue());
			assertEquals("Deserialization Failure", node1.getValue(), "node1://updated/"+i);
			
			for(int j=0; j<node2Size; j++)
			{
				Node2 node2 = (Node2)node1.getNode2().get(j);
				String node2Key = "node1["+i+"].node2["+j+"].value"; 
				log.info(node2Key+"="+node2.getValue());
				assertEquals("Deserialization Failure", node2.getValue(), "node2://updated/"+i+","+j);
			}
		}
	}
	
	private void assertLevelThree(int node1Size, int node2Size, int node3Size,String xml, MobileObject mo)
	{
		log.info("----------------------------------------------------");
		log.info(xml);
		log.info("----------------------------------------------------");
		
		log.info("value="+mo.getValue("value"));
		assertEquals("Read Failure", mo.getValue("value"), "root");
		
		for(int i=0; i<node1Size; i++)
		{
			String node1Key = "node1["+i+"].value";
			log.info(mo.getValue(node1Key));
			assertEquals("Read Failure", mo.getValue(node1Key), "node1://"+i);
			
			for(int j=0; j<node2Size; j++)
			{
				String node2Key = "node1["+i+"].node2["+j+"].value"; 
				log.info(mo.getValue(node2Key));
				assertEquals("Read Failure", mo.getValue(node2Key), "node2://"+i+","+j);
				
				for(int k=0; k<node3Size; k++)
				{					
					String node3Key = "node1["+i+"].node2["+j+"].node3["+k+"].value";
					log.info(mo.getValue(node3Key));
					assertEquals("Read Failure", mo.getValue(node3Key), "node3://"+i+","+j+","+k);
				}
			}
		}
	}
	
	private void assertLevelThreeDeserialization(int node1Size, int node2Size, int node3Size,
	String xml, Root root)
	{		
		log.info("----------------------------------------------------");
		log.info(xml);
		log.info("----------------------------------------------------");
		
		log.info("value="+root.getValue());
		assertEquals("Deserialization Failure", root.getValue(), "root");
		
		for(int i=0; i<node1Size; i++)
		{
			Node1 node1 = (Node1)root.getNode1().get(i);
			String node1Key = "node1["+i+"].value";
			log.info(node1Key+"="+node1.getValue());
			assertEquals("Deserialization Failure", node1.getValue(), "node1://"+i);
			
			for(int j=0; j<node2Size; j++)
			{
				Node2 node2 = (Node2)node1.getNode2().get(j);
				String node2Key = "node1["+i+"].node2["+j+"].value"; 
				log.info(node2Key+"="+node2.getValue());
				assertEquals("Deserialization Failure", node2.getValue(), "node2://"+i+","+j);		
			
				for(int k=0; k<node3Size; k++)
				{
					Node3 node3 = (Node3)node2.getNode3().get(k);
					String node3Key = "node1["+i+"].node2["+j+"].node3["+k+"].value";
					log.info(node3Key+"="+node3.getValue());
					assertEquals("Deserialization Failure", node3.getValue(), "node3://"+i+","+j+","+k);
				}
			}
		}
	}
	
	private void assertLevelThreeDeserializationWithUpdate(int node1Size, int node2Size, int node3Size,
	MobileObject mo)
	{
		for(int i=0; i<node1Size; i++)
		{
			String node1Key = "node1["+i+"].value";
			mo.setValue(node1Key, "node1://update/"+i);			
			
			for(int j=0; j<node2Size; j++)
			{
				String node2Key = "node1["+i+"].node2["+j+"].value"; 
				mo.setValue(node2Key, "node2://update/"+i+","+j);				
				
				for(int k=0; k<node3Size; k++)
				{					
					String node3Key = "node1["+i+"].node2["+j+"].node3["+k+"].value";
					mo.setValue(node3Key, "node3://update/"+i+","+j+","+k);					
				}
			}
		}
		
		String deserializedXml = DeviceSerializer.getInstance().serialize(mo);
		Root root = (Root)this.serverDeserialize(Root.class, deserializedXml);
		log.info("----------------------------------------------------");
		log.info(deserializedXml);
		log.info("----------------------------------------------------");
		
		log.info("value="+root.getValue());
		assertEquals("Deserialization Failure", root.getValue(), "root");
		
		for(int i=0; i<node1Size; i++)
		{
			Node1 node1 = (Node1)root.getNode1().get(i);
			String node1Key = "node1["+i+"].value";
			log.info(node1Key+"="+node1.getValue());
			assertEquals("Deserialization Failure", node1.getValue(), "node1://update/"+i);
			
			for(int j=0; j<node2Size; j++)
			{
				Node2 node2 = (Node2)node1.getNode2().get(j);
				String node2Key = "node1["+i+"].node2["+j+"].value"; 
				log.info(node2Key+"="+node2.getValue());
				assertEquals("Deserialization Failure", node2.getValue(), "node2://update/"+i+","+j);		
			
				for(int k=0; k<node3Size; k++)
				{
					Node3 node3 = (Node3)node2.getNode3().get(k);
					String node3Key = "node1["+i+"].node2["+j+"].node3["+k+"].value";
					log.info(node3Key+"="+node3.getValue());
					assertEquals("Deserialization Failure", node3.getValue(), "node3://update/"+i+","+j+","+k);
				}
			}
		}
	}
}
