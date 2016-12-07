/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.mobileContainer;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.mobileContainer.MobileContainer;
import org.openmobster.core.mobileContainer.Invocation;
import org.openmobster.core.mobileContainer.InvocationResponse;
import org.openmobster.core.mobileObject.xml.MobileObjectSerializer;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.frameworks.mobileObject.DeviceSerializer;
import org.openmobster.device.agent.frameworks.mobileObject.BeanList;
import org.openmobster.device.agent.frameworks.mobileObject.BeanListEntry;

/**
 * @author openmobster@gmail.com
 */
public class TestMobileIdentity extends TestCase
{	
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(TestMobileIdentity.class);
	
	private MobileContainer mobileContainer;
	private IdentityConnector identityConnector;
	private MobileObjectSerializer serializer;	
	
	
	public void setUp() throws Exception
	{		
		ServiceManager.bootstrap();
		
		this.mobileContainer = (MobileContainer)ServiceManager.
		locate("mobileContainer://MobileContainer");
		
		this.identityConnector = (IdentityConnector)ServiceManager.locate("test://mobileContainer/IdentityConnector");
		
		this.serializer = (MobileObjectSerializer)ServiceManager.locate("mobileObject://MobileObjectSerializer");
				
		//Load Identity data
		for(int i=0; i<5; i++)
		{
			Identity newIdentity = this.getNewMockIdentity();		
			this.identityConnector.create(newIdentity);
		}
	}
	
	public void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	//-----------------------------------------------------------------------------------------------------	
	public void testReadAll() throws Exception
	{		
		List<MobileObject> all = this.getAllIdentities();
		
		assertTrue(all != null && !all.isEmpty());
		
		for(MobileObject object: all)
		{						
			String id = object.getValue("id");
			String principal = object.getValue("principal");
			String credential = object.getValue("credential");
			
			log.info("Id="+id);
			log.info("Principal="+principal);
			log.info("Credential="+credential);
						
			assertEquals("Principal Mismatch", principal, principal);
			assertEquals("Credential Mismatch", credential, credential);
			
			//Deal with set of attributes
			BeanList attributes = IndexingAPIUtil.readList(object, "attributes");
			for(int j=0; j<attributes.size(); j++)
			{
				BeanListEntry attribute = attributes.getEntryAt(j);
				String name = "name";
				String value = "value";
				log.info("attribute["+j+"] =" + attribute.getProperty(name)+","+attribute.getProperty(value));
				assertNotNull("Attribute Mismatch", attribute.getProperty(name));
				assertTrue("Attribute Mismatch", attribute.getProperty(value).endsWith("/value"));				
			}
			
			//Deal with array of attributes
			BeanList arrayTest = IndexingAPIUtil.readList(object, "arrayTest");
			for(int j=0; j<arrayTest.size(); j++)
			{
				BeanListEntry element = arrayTest.getEntryAt(j);
				String name = "name";
				String value = "value";
				log.info("arrayTest["+j+"] =" + element.getProperty(name)+","+element.getProperty(value));
				assertNotNull("Array Mismatch", element.getProperty(name));
				assertTrue("Array Mismatch", element.getProperty(value).endsWith("/value"));
			}
			
			log.info("-------------------------------------------------");
		}
	}
		
	public void testRead() throws Exception
	{									
		//Read the object just created from the backend		
		MobileObject object = this.getIdentity("1");
		
		//Assert the state of the backend storage
		String id = object.getValue("id");
		String principal = object.getValue("principal");
		String credential = object.getValue("credential");		
		
		log.info("-------------------------------------------------");
		log.info("Id="+id);
		log.info("Principal="+principal);
		log.info("Credential="+credential);
		
		assertEquals("Id Mismatch", id, "1");
		assertEquals("Principal Mismatch", principal, principal);
		assertEquals("Credential Mismatch", credential, credential);
		
		//Deal with set of attributes
		BeanList attributes = IndexingAPIUtil.readList(object, "attributes");
		for(int j=0; j<attributes.size(); j++)
		{
			BeanListEntry attribute = attributes.getEntryAt(j);
			String name = "name";
			String value = "value";
			log.info("attribute["+j+"] =" + attribute.getProperty(name)+","+attribute.getProperty(value));
			assertNotNull("Attribute Mismatch", attribute.getProperty(name));
			assertTrue("Attribute Mismatch", attribute.getProperty(value).endsWith("/value"));				
		}
		
		//Deal with array of attributes
		BeanList arrayTest = IndexingAPIUtil.readList(object, "arrayTest");
		for(int j=0; j<arrayTest.size(); j++)
		{
			BeanListEntry element = arrayTest.getEntryAt(j);
			String name = "name";
			String value = "value";
			log.info("arrayTest["+j+"] =" + element.getProperty(name)+","+element.getProperty(value));
			assertNotNull("Array Mismatch", element.getProperty(name));
			assertTrue("Array Mismatch", element.getProperty(value).endsWith("/value"));
		}		
		log.info("-------------------------------------------------");
	}
	
	public void testCreate() throws Exception
	{		
		MobileObject newMobileObject = new MobileObject();
		newMobileObject.setValue("principal", "new://principal");
		newMobileObject.setValue("credential", "new://credential");
		
		//list of attributes
		for(int i=0; i<3; i++)
		{
			BeanListEntry attribute = new BeanListEntry();						
			String name = "name";
			String value = "value";
			attribute.setProperty(name, name);
			attribute.setProperty(value, value);
			IndexingAPIUtil.addBean(newMobileObject, "attributes", attribute);
		}
		
		//array of attributes
		for(int i=0; i<3; i++)
		{
			BeanListEntry attribute = new BeanListEntry();						
			String name = "name";
			String value = "value";
			attribute.setProperty(name, name);
			attribute.setProperty(value, value);
			IndexingAPIUtil.addBean(newMobileObject, "arrayTest", attribute);
		}
		
		String id = this.saveIdentity(newMobileObject); 									
		long newId = Long.parseLong(id);
		assertEquals("New Identity Creation Failed!!", newId, 6);
		
		
		MobileObject object = this.getIdentity(id);
		id = object.getValue("id");
		String principal = object.getValue("principal");
		String credential = object.getValue("credential");		
		
		log.info("-------------------------------------------------");
		log.info("Id="+id);
		log.info("Principal="+principal);
		log.info("Credential="+credential);
		
		assertEquals("Id Mismatch", id, "6");
		assertEquals("Principal Mismatch", principal, principal);
		assertEquals("Credential Mismatch", credential, credential);
		
		//Deal with set of attributes
		BeanList attributes = IndexingAPIUtil.readList(object, "attributes");
		for(int j=0; j<attributes.size(); j++)
		{
			BeanListEntry attribute = attributes.getEntryAt(j);
			String name = "name";
			String value = "value";
			log.info("attribute["+j+"] =" + attribute.getProperty(name)+","+attribute.getProperty(value));
			assertNotNull("Attribute Mismatch", attribute.getProperty(name));
			assertTrue("Attribute Mismatch", attribute.getProperty(value).endsWith("value"));				
		}
		
		//Deal with array of attributes
		BeanList arrayTest = IndexingAPIUtil.readList(object, "arrayTest");
		for(int j=0; j<arrayTest.size(); j++)
		{
			BeanListEntry element = arrayTest.getEntryAt(j);
			String name = "name";
			String value = "value";
			log.info("arrayTest["+j+"] =" + element.getProperty(name)+","+element.getProperty(value));
			assertNotNull("Array Mismatch", element.getProperty(name));
			assertTrue("Array Mismatch", element.getProperty(value).endsWith("value"));
		}		
		log.info("-------------------------------------------------");
	}
	
	public void testUpdate() throws Exception
	{
		String inputId = "1";
		MobileObject object = this.getIdentity(inputId);
		object.setValue("principal", "updated://principal");
		this.saveIdentity(object);		
		object = this.getIdentity(inputId);		
						
		//Assert the state of the backend storage		
		String id = object.getValue("id");
		String principal = object.getValue("principal");
		String credential = object.getValue("credential");		
		
		log.info("-------------------------------------------------");
		log.info("Id="+id);
		log.info("Principal="+principal);
		log.info("Credential="+credential);
		
		assertEquals("Id Mismatch", id, "1");
		assertEquals("Principal Mismatch", principal, "updated://principal");
		assertEquals("Credential Mismatch", credential, credential);
		
		//Deal with set of attributes
		BeanList attributes = IndexingAPIUtil.readList(object, "attributes");
		for(int j=0; j<attributes.size(); j++)
		{
			BeanListEntry attribute = attributes.getEntryAt(j);
			String name = "name";
			String value = "value";
			log.info("attribute["+j+"] =" + attribute.getProperty(name)+","+attribute.getProperty(value));
			assertNotNull("Attribute Mismatch", attribute.getProperty(name));
			assertTrue("Attribute Mismatch", attribute.getProperty(value).endsWith("/value"));				
		}
		
		//Deal with array of attributes
		BeanList arrayTest = IndexingAPIUtil.readList(object, "arrayTest");
		for(int j=0; j<arrayTest.size(); j++)
		{
			BeanListEntry element = arrayTest.getEntryAt(j);
			String name = "name";
			String value = "value";
			log.info("arrayTest["+j+"] =" + element.getProperty(name)+","+element.getProperty(value));
			assertNotNull("Array Mismatch", element.getProperty(name));
			assertTrue("Array Mismatch", element.getProperty(value).endsWith("/value"));
		}		
		log.info("-------------------------------------------------");	
	}
	
	public void testDelete() throws Exception
	{									
		//Read the object just created from the backend
		String inputId = "1";
		inputId = this.deleteIdentity(inputId);		
		MobileObject object = this.getIdentity(inputId);
		
		assertNull("The deleted object should not be found", object);
		
		List<MobileObject> all = this.getAllIdentities();
		assertEquals(4, all.size());
		
		//this.testReadAll();				
	}
	//-------------------------------------------------------------------------------------------------	
	private Identity getNewMockIdentity()
	{
		Identity identity = new Identity();
				
		identity.setPrincipal("principal");
		identity.setCredential("credential");
		identity.addAttribute(new IdentityAttribute("attr://1", "attr://1/value"));
		identity.addAttribute(new IdentityAttribute("attr://2", "attr://2/value"));
		identity.addAttribute(new IdentityAttribute("attr://3", "attr://3/value"));
		
		IdentityAttribute[] arrayTest = new IdentityAttribute[]{
				new IdentityAttribute("array://1", "array://1/value"),
				new IdentityAttribute("array://2", "array://2/value"),
				new IdentityAttribute("array://3", "array://3/value")
		};
		identity.setArrayTest(arrayTest);
		
		return identity;
	}
	
	private List<MobileObject> getAllIdentities() throws Exception
	{
		List<MobileObject> all = new ArrayList<MobileObject>();
		
		Invocation invocation = Invocation.getInstance();
		invocation.setServiceUrl("/bean/all");
		invocation.setConnectorId("identity");
		
		//Perform the invocation
		InvocationResponse response = mobileContainer.invoke(invocation);
		List<MobileBean> serverBeans = response.getAllBeans();
		for(MobileBean serverBean: serverBeans)
		{
			String deviceXml = this.serializer.serialize(serverBean);
			all.add(DeviceSerializer.getInstance().deserialize(deviceXml));
		}
		
		return all;
	}
	
	private MobileObject getIdentity(String id) throws Exception
	{
		MobileObject mobileObject = null;
		
		Invocation invocation = Invocation.getInstance();
		invocation.setServiceUrl("/bean/id");
		invocation.setBeanId(id);
		invocation.setConnectorId("identity");
		
		//Perform the invocation
		InvocationResponse response = mobileContainer.invoke(invocation);
		MobileBean serverBean = response.getBean();
		if(serverBean != null)
		{
			String deviceXml = this.serializer.serialize(serverBean);
			mobileObject = DeviceSerializer.getInstance().deserialize(deviceXml);
		}
				
		return mobileObject;
	}
	
	private String saveIdentity(MobileObject mobileObject) throws Exception
	{
		String beanId = null;
		
		Invocation invocation = Invocation.getInstance();
		invocation.setServiceUrl("/bean/save");
		invocation.setConnectorId("identity");
		invocation.setSerializedBean(DeviceSerializer.getInstance().serialize(mobileObject));
		
		//Perform the invocation
		InvocationResponse response = mobileContainer.invoke(invocation);
		beanId = response.getBeanId();
		
		return beanId;
	}
	
	private String deleteIdentity(String beanId) throws Exception
	{		
		Invocation invocation = Invocation.getInstance();
		invocation.setServiceUrl("/bean/delete");
		invocation.setBeanId(beanId);
		invocation.setConnectorId("identity");
		
		//Perform the invocation
		InvocationResponse response = mobileContainer.invoke(invocation);
		beanId = response.getBeanId();
		
		return beanId;
	}
}
