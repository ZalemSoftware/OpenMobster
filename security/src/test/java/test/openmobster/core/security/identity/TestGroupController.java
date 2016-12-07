/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.security.identity;

import org.apache.log4j.Logger;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.core.security.identity.GroupController;
import org.openmobster.core.security.identity.Group;
import org.openmobster.core.security.identity.GroupAttribute;

/**
 * @author openmobster@gmail.com
 */
public class TestGroupController extends TestCase
{
	private static Logger log = Logger.getLogger(TestGroupController.class);
	
	private GroupController controller = null;
	
	/**
	 * 
	 */
	public void setUp()
	{
		ServiceManager.bootstrap();
		this.controller = (GroupController)ServiceManager.locate("security://test-suite/GroupController");
	}
	
	/**
	 * 
	 */
	public void tearDown()
	{
		ServiceManager.shutdown();
		this.controller = null;
	}
	
	
	public void testCreate() throws Exception
	{
		//Assert
		assertNotNull(this.controller);
		Group stored = this.controller.read("administrator");
		assertNull(stored);
		
		Group group = new Group();
		group.setName("administrator");
		
		//Add Attributes
		Set<GroupAttribute> attributes = new HashSet<GroupAttribute>();
		attributes.add(new GroupAttribute("description", "Super User"));
		group.setAttributes(attributes);
				
		
		//Add this group to the database
		this.controller.create(group);
		
		//Assert the state of the database
		stored = this.controller.read("administrator");
		assertNotNull(stored);
		
		assertEquals("Name does not match", "administrator", stored.getName());		
		assertTrue("Improper ID assigned", stored.getId()>0);
		
		assertNotNull("Group Attributes Not Found", stored.getAttributes());
		GroupAttribute storedAttribute = stored.getAttributes().iterator().next();
		assertEquals("Description Attribute Not Found", "description", storedAttribute.getName());
		assertEquals("Description Value does not match", "Super User", storedAttribute.getValue());
		assertTrue("Improper ID assigned", storedAttribute.getId()>0);
	}	
	
	public void testUpdateGroupOnly() throws Exception
	{
		//Assert
		assertNotNull(this.controller);
		Group stored = this.controller.read("administrator");
		assertNull(stored);
		
		Group group = new Group();
		group.setName("administrator");
		
		//Add Attributes
		Set<GroupAttribute> attributes = new HashSet<GroupAttribute>();
		attributes.add(new GroupAttribute("description", "Super User"));
		group.setAttributes(attributes);
		
		//Add this group to the database
		this.controller.create(group);
		
		//Assert the state of the database
		stored = this.controller.read("administrator");
		assertNotNull(stored);
		
		assertEquals("Name does not match", "administrator", stored.getName());		
		assertTrue("Improper ID assigned", stored.getId()>0);
		
		assertNotNull("Group Attributes Not Found", stored.getAttributes());
		GroupAttribute storedAttribute = stored.getAttributes().iterator().next();
		assertEquals("Description Attribute Not Found", "description", storedAttribute.getName());
		assertEquals("Description Value does not match", "Super User", storedAttribute.getValue());
		assertTrue("Improper ID assigned", storedAttribute.getId()>0);
		
		//Now Update the Identity data
		stored.setName("administrator/updated");		
		this.controller.update(stored);
		
		//Assert the state of the database
		Group mustNotExist = this.controller.read("administrator");
		assertNull("Old Group must not exist", mustNotExist);
		stored = this.controller.read("administrator/updated");
		assertNotNull(stored);
		
		assertEquals("Name does not match", "administrator/updated", stored.getName());		
		assertTrue("Improper ID assigned", stored.getId()>0);
		
		assertNotNull("Group Attributes Not Found", stored.getAttributes());
		storedAttribute = stored.getAttributes().iterator().next();
		assertEquals("Description Attribute Not Found", "description", storedAttribute.getName());
		assertEquals("Description Value does not match", "Super User", storedAttribute.getValue());
		assertTrue("Improper ID assigned", storedAttribute.getId()>0);		
	}
	
	public void testUpdateGroupAttributes() throws Exception
	{
		//Assert
		assertNotNull(this.controller);
		Group stored = this.controller.read("administrator");
		assertNull(stored);
		
		Group group = new Group();
		group.setName("administrator");
		
		//Add Attributes
		Set<GroupAttribute> attributes = new HashSet<GroupAttribute>();
		attributes.add(new GroupAttribute("testUpdate", "Test Update"));
		attributes.add(new GroupAttribute("testRemove", "Test Remove"));
		group.setAttributes(attributes);
		
		//Add this group to the database
		this.controller.create(group);
		
		//Assert the state of the database
		stored = this.controller.read("administrator");
		assertNotNull(stored);
		
		assertEquals("Name does not match", "administrator", stored.getName());		
		assertTrue("Improper ID assigned", stored.getId()>0);
		
					
		//Now Update the Group Attributes
		stored.addAttribute(new GroupAttribute("testAdd", "Test Add"));
		stored.removeAttribute(new GroupAttribute("testRemove", "Test Remove"));
		stored.updateAttribute(new GroupAttribute("testUpdate", "Test Update/updated"));
		this.controller.update(stored);
		
		//Assert the attributes
		stored = this.controller.read("administrator");
		attributes = stored.getAttributes();
		log.info("-------------------------------------------------");
		for(GroupAttribute attribute: attributes)
		{
			log.info("Id="+attribute.getId()+", Name="+attribute.getName()+", Value="+attribute.getValue());
		}
		assertEquals("Add Attribute Failed", "Test Add", 
		stored.readAttribute("testAdd").getValue());
		assertEquals("Update Attribute Failed", "Test Update/updated", 
				stored.readAttribute("testUpdate").getValue());
		assertNull("Delete Attribute Failed", stored.readAttribute("testRemove"));
		
		//Assert the attribute table itself
		Session session = null;
		Transaction tx = null;
		boolean orphanDeleted = true;
		try
		{
			session = this.controller.getHibernateManager().getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from GroupAttribute";
			List allAttributes = session.createQuery(query).list();
			log.info("-------------------------------------------------");
			for(int i=0; i<allAttributes.size(); i++)
			{
				GroupAttribute cour = (GroupAttribute)allAttributes.get(i);
				log.info("Id="+cour.getId()+", Name="+cour.getName()+", Value="+cour.getValue());
				if(cour.getName().equals("testRemove"))
				{
					orphanDeleted = false;
				}
			}
			
			tx.commit();
		}
		catch(Exception e)
		{
			if(tx != null)
			{
				tx.rollback();
			}
			throw new RuntimeException(e);
		}
		finally
		{			
			if(session != null && session.isOpen())
			{
				session.close();
			}
		}
		assertTrue("Orphan Deletion failed", orphanDeleted);
	}
	
	public void testDelete() throws Exception
	{		
		Group group = new Group();
		group.setName("administrator");
		
		//Add Attributes
		Set<GroupAttribute> attributes = new HashSet<GroupAttribute>();
		attributes.add(new GroupAttribute("testUpdate", "Test Update"));
		attributes.add(new GroupAttribute("testRemove", "Test Remove"));
		group.setAttributes(attributes);
		
		//Add this group to the database
		this.controller.create(group);
		
		//Assert the state of the database
		Group stored = this.controller.read("administrator");
		assertNotNull(stored);
		
		//Delete this identity from the database
		this.controller.delete(group);
		
		//Assert state of the database
		stored = this.controller.read("administrator");
		assertNull("Group Deletion Failed", stored);
	}
}
