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

import org.openmobster.core.security.identity.IdentityController;
import org.openmobster.core.security.identity.Identity;
import org.openmobster.core.security.identity.IdentityAttribute;

/**
 * @author openmobster@gmail.com
 */
public class TestIdentityController extends TestCase
{
	private static Logger log = Logger.getLogger(TestIdentityController.class);
	
	private IdentityController controller = null;
	
	/**
	 * 
	 */
	public void setUp()
	{
		ServiceManager.bootstrap();
		this.controller = (IdentityController)ServiceManager.locate("security://test-suite/IdentityController");
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
		Identity stored = this.controller.read("admin");
		assertNull(stored);
		
		Identity identity = new Identity();
		identity.setPrincipal("admin");
		identity.setCredential("adminPassword");
		
		//Add Identity Attributes
		Set<IdentityAttribute> attributes = new HashSet<IdentityAttribute>();
		attributes.add(new IdentityAttribute("email", "blah@gmail.com"));
		identity.setAttributes(attributes);
		
		//Add this identity to the database
		this.controller.create(identity);
		
		//Assert the state of the database
		stored = this.controller.read("admin");
		assertNotNull(stored);
		
		assertEquals("Principal does not match", "admin", stored.getPrincipal());
		assertEquals("Credential does not match", "adminPassword", stored.getCredential());
		assertTrue("Improper ID assigned", stored.getId()>0);
		
		assertNotNull("Identity Attributes Not Found", stored.getAttributes());
		IdentityAttribute storedAttribute = stored.getAttributes().iterator().next();
		assertEquals("Email Attribute Not Found", "email", storedAttribute.getName());
		assertEquals("Email Value does not match", "blah@gmail.com", storedAttribute.getValue());
		assertTrue("Improper ID assigned", storedAttribute.getId()>0);
	}
	
	public void testExists() throws Exception
	{
		//Assert
		assertNotNull(this.controller);
		boolean exists = this.controller.exists("admin");
		assertFalse("Identity Must Not Exist!!", exists);
		
		Identity identity = new Identity();
		identity.setPrincipal("admin");
		identity.setCredential("adminPassword");
		
		//Add Identity Attributes
		Set<IdentityAttribute> attributes = new HashSet<IdentityAttribute>();
		attributes.add(new IdentityAttribute("email", "blah@gmail.com"));
		identity.setAttributes(attributes);
		
		//Add this identity to the database
		this.controller.create(identity);
		
		//Assert the state of the database
		exists = this.controller.exists("admin");
		assertTrue("Identity Must Exist Now!!", exists);
	}
	
	
	public void testUpdateIdentityOnly() throws Exception
	{
		//Assert
		assertNotNull(this.controller);
		Identity stored = this.controller.read("admin");
		assertNull(stored);
		
		Identity identity = new Identity();
		identity.setPrincipal("admin");
		identity.setCredential("adminPassword");
		
		//Add Identity Attributes
		Set<IdentityAttribute> attributes = new HashSet<IdentityAttribute>();
		attributes.add(new IdentityAttribute("email", "blah@gmail.com"));
		identity.setAttributes(attributes);
		
		//Add this identity to the database
		this.controller.create(identity);
		
		//Assert the state of the database
		stored = this.controller.read("admin");
		assertNotNull(stored);
		
		assertEquals("Principal does not match", "admin", stored.getPrincipal());
		assertEquals("Credential does not match", "adminPassword", stored.getCredential());
		assertTrue("Improper ID assigned", stored.getId()>0);
		
		assertNotNull("Identity Attributes Not Found", stored.getAttributes());
		IdentityAttribute storedAttribute = stored.getAttributes().iterator().next();
		assertEquals("Email Attribute Not Found", "email", storedAttribute.getName());
		assertEquals("Email Value does not match", "blah@gmail.com", storedAttribute.getValue());
		assertTrue("Improper ID assigned", storedAttribute.getId()>0);
		
		//Now Update the Identity data
		stored.setPrincipal("admin/updated");
		stored.setCredential("adminPassword/updated");
		this.controller.update(stored);
		
		//Assert the state of the database
		Identity mustNotExist = this.controller.read("admin");
		assertNull("Old Identity must not exist", mustNotExist);
		stored = this.controller.read("admin/updated");
		assertNotNull(stored);
		
		assertEquals("Principal does not match", "admin/updated", stored.getPrincipal());
		assertEquals("Credential does not match", "adminPassword/updated", stored.getCredential());
		assertTrue("Improper ID assigned", stored.getId()>0);
		
		assertNotNull("Identity Attributes Not Found", stored.getAttributes());
		storedAttribute = stored.getAttributes().iterator().next();
		assertEquals("Email Attribute Not Found", "email", storedAttribute.getName());
		assertEquals("Email Value does not match", "blah@gmail.com", storedAttribute.getValue());		
	}
	
	
	public void testUpdateIdentityAttributes() throws Exception
	{
		//Assert
		assertNotNull(this.controller);
		Identity stored = this.controller.read("admin");
		assertNull(stored);
		
		Identity identity = new Identity();
		identity.setPrincipal("admin");
		identity.setCredential("adminPassword");
		
		//Add Identity Attributes
		Set<IdentityAttribute> attributes = new HashSet<IdentityAttribute>();
		attributes.add(new IdentityAttribute("email", "blah@gmail.com"));
		attributes.add(new IdentityAttribute("first_name", "firstblah"));
		attributes.add(new IdentityAttribute("last_name", "lastblah"));
		attributes.add(new IdentityAttribute("miscellaneous", "miscellaneous"));
		identity.setAttributes(attributes);
		
		//Add this identity to the database
		this.controller.create(identity);
		
		//Assert the state of the database
		stored = this.controller.read("admin");
		assertNotNull(stored);
		
		assertEquals("Principal does not match", "admin", stored.getPrincipal());
		assertEquals("Credential does not match", "adminPassword", stored.getCredential());
		assertTrue("Improper ID assigned", stored.getId()>0);
		
				
		//Now Update the Identity Attributes
		stored.addAttribute(new IdentityAttribute("company", "OpenMobster, Inc"));
		stored.removeAttribute(new IdentityAttribute("miscellaneous", "miscellaneous"));
		stored.updateAttribute(new IdentityAttribute("email", "blah/updated@gmail.com"));
		this.controller.update(stored);
		
		//Assert the attributes
		stored = this.controller.read("admin");
		attributes = stored.getAttributes();
		log.info("-------------------------------------------------");
		for(IdentityAttribute attribute: attributes)
		{
			log.info("Id="+attribute.getId()+", Name="+attribute.getName()+", Value="+attribute.getValue());
		}
		assertEquals("Company Attribute Failed", "OpenMobster, Inc", 
		stored.readAttribute("company").getValue());
		assertEquals("Email Attribute Failed", "blah/updated@gmail.com", 
				stored.readAttribute("email").getValue());
		assertNull("Miscellaneous Delete Attribute Failed", stored.readAttribute("miscellaneous"));
		
		//Assert the attribute table itself
		Session session = null;
		Transaction tx = null;
		boolean orphanDeleted = true;
		try
		{
			session = this.controller.getHibernateManager().getSessionFactory().getCurrentSession();
			tx = session.beginTransaction();
			
			String query = "from IdentityAttribute";
			List allAttributes = session.createQuery(query).list();
			log.info("-------------------------------------------------");
			for(int i=0; i<allAttributes.size(); i++)
			{
				IdentityAttribute cour = (IdentityAttribute)allAttributes.get(i);
				log.info("Id="+cour.getId()+", Name="+cour.getName()+", Value="+cour.getValue());
				if(cour.getName().equals("miscellaneous"))
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
}
