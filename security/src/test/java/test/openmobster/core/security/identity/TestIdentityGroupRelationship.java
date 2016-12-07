/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.security.identity;

import java.util.Set;

import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.core.security.identity.IdentityController;
import org.openmobster.core.security.identity.GroupController;
import org.openmobster.core.security.identity.Identity;
import org.openmobster.core.security.identity.Group;
import org.openmobster.core.security.identity.IdentityAttribute;
import org.openmobster.core.security.identity.GroupAttribute;

/**
 * @author openmobster@gmail.com
 */
public class TestIdentityGroupRelationship extends TestCase
{
	private IdentityController identityController = null;
	private GroupController groupController = null;
	
	/**
	 * 
	 */
	public void setUp()
	{
		ServiceManager.bootstrap();
		this.identityController = (IdentityController)ServiceManager.locate("security://test-suite/IdentityController");
		this.groupController = (GroupController)ServiceManager.locate("security://test-suite/GroupController");
		
		//Create an Identity
		Identity identity = new Identity();		
		identity.setPrincipal("admin");
		identity.setCredential("adminpassword");
		identity.addAttribute(new IdentityAttribute("testId", "Test Id"));
		this.identityController.create(identity);
		
		//Create a Group with the added Identity as a member
		Group group = new Group();
		group.setName("administrator");
		group.getMembers().add(identity);
		group.addAttribute(new GroupAttribute("testGroup", "Test Group"));
		this.groupController.create(group);
	}
	
	/**
	 * 
	 */
	public void tearDown()
	{
		ServiceManager.shutdown();
		this.identityController = null;
		this.groupController = null;
	}
	
	public void testCreate() throws Exception
	{		
		//Assert Group Storage
		Group storedGroup = this.groupController.read("administrator");
		this.groupController.loadMembers(storedGroup);
		
		assertNotNull("Group was not successfully stored!!!", storedGroup);		
		assertEquals("Group Name does not match", "administrator", storedGroup.getName());
		assertEquals("Group Attribute does not match", "Test Group", 
		storedGroup.readAttribute("testGroup").getValue());
		
		//Assert Identity Storage
		Identity storedIdentity = this.identityController.read("admin");
		assertNotNull("Identity was not successfully stored!!!", storedIdentity);
		assertEquals("Identity Principal does not match", "admin", storedIdentity.getPrincipal());
		assertEquals("Identity Credential does not match", "adminpassword", storedIdentity.getCredential());
		assertEquals("Identity Attribute does not match", "Test Id", storedIdentity.readAttribute("testId").getValue());
		
		//Assert Group-to-Identity many-to-many relationship
		Set<Identity> groupToIdentity = storedGroup.getMembers();		
		assertTrue("Group-To-Identity Relationship failure", (groupToIdentity!=null && !groupToIdentity.isEmpty()));
		
		Identity member = this.groupController.findMember("admin", storedGroup);		
		assertEquals("Member Principal does not match", "admin", member.getPrincipal());
		assertEquals("Member Credential does not match", "adminpassword", member.getCredential());
		assertEquals("Member Attribute does not match", "Test Id", member.readAttribute("testId").getValue());
	}
	
	public void testGroupMemberUpdate() throws Exception
	{		
		Group group = this.groupController.read("administrator");		
		
		//Add a new Identity to this group
		Identity identity = new Identity();		
		identity.setPrincipal("testAdd");
		identity.setCredential("testaddpassword");
		identity.addAttribute(new IdentityAttribute("testAddId", "Test Add Id"));
		this.identityController.create(identity);
		this.groupController.addMember(identity, group);
		
		//Remove an existing Identity from this group
		this.groupController.removeMember("admin", group);
		
		
		//Assert membership
		group = this.groupController.read("administrator");
		
		Identity member = this.groupController.findMember("testAdd", group);	
		assertTrue("Improper ID assigned", member.getId()>0);
		assertEquals("Member Principal does not match", "testAdd", member.getPrincipal());
		assertEquals("Member Credential does not match", "testaddpassword", member.getCredential());
		assertEquals("Member Attribute does not match", "Test Add Id", member.readAttribute("testAddId").getValue());
		
		member = this.groupController.findMember("admin", group);
		assertNull("Member was not deleted successfully...", member);
		
		identity = this.identityController.read("admin");
		assertNotNull("The member identity should not be deleted", identity);
	}
	
	/**
	 * 
	 */
	public void testReadIdentityGroups() throws Exception
	{		
		Identity identity = this.identityController.read("admin");
		this.identityController.loadGroups(identity);
		
		for(Group group: identity.getGroups())
		{
			assertNotNull("Group Should Not be Null", group);			
			assertEquals("Group Name does not match", "administrator", group.getName());
			assertEquals("Group Attribute does not match", "Test Group", 
			group.readAttribute("testGroup").getValue());
		}
	}
}
