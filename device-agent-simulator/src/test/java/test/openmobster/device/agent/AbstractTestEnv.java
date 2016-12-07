/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.synchronizer.server.engine.ChangeLogEntry;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;
import org.openmobster.device.agent.test.framework.MobileBeanRunner;
import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.frameworks.mobileObject.BeanList;
import org.openmobster.device.agent.frameworks.mobileObject.BeanListEntry;

import org.apache.log4j.Logger;

import test.openmobster.device.agent.api.TicketConnector;
import test.openmobster.device.agent.api.Ticket;
import test.openmobster.device.agent.api.Note;
import test.openmobster.device.agent.api.Part;
import test.openmobster.device.agent.api.MockDataGenerator;

/**
 * @author openmobster@gmail.com
 *
 */
public abstract class AbstractTestEnv extends TestCase
{	
	public static Logger log = Logger.getLogger(AbstractTestEnv.class);
	
	protected MobileBeanRunner runner;
	protected TicketConnector ticketConnector;
	protected ServerSyncEngine serverSyncEngine;
	protected MockDataGenerator dataGenerator;
	
	protected void setUp() throws Exception 
	{
		ServiceManager.bootstrap();
		
		this.runner = (MobileBeanRunner)ServiceManager.locate("testMobileBeanSpec://mobileBeanRunner");
		this.runner.setApp("testApp");
		this.ticketConnector = (TicketConnector)ServiceManager.locate("testMobileBeanSpec://TicketConnector");
		this.serverSyncEngine = (ServerSyncEngine)ServiceManager.
		locate("synchronizer://ServerSyncEngine");
		this.dataGenerator = (MockDataGenerator)ServiceManager.locate("testMobileBeanSpec://MockDataGenerator");
		
		this.dataGenerator.generatePersistentData();
		this.runner.activateDevice();
				
		this.printDeviceData();
		this.printServerData();
	}
	
	protected void tearDown() throws Exception 
	{		
		this.printDeviceData();
		this.printServerData();
		
		ServiceManager.shutdown();
	}
	//--------------------------------------------------------------------------------------------------------------------------------------		
	protected void bootService() throws Exception
	{
		this.runner.bootService();
		
		//Assert state
		this.assertBootUpState();
	}
	
	protected void assertBootUpState() throws Exception
	{
		//Assert state
		List<MobileObject> beans = this.runner.getDeviceDatabase().readByStorage(this.runner.getService());
		assertTrue("On Device Ticket service should not be empty!!!", (beans != null && !beans.isEmpty()));
		
		int loadedCount = 0;						
		for(MobileObject currBean: beans)
		{
			if(!currBean.isProxy())
			{
				loadedCount++;
				this.assertBean(currBean, (Ticket)this.ticketConnector.read(currBean.getServerRecordId()));
			}						
		}
		
		assertTrue("On Device Ticket service should only have 2 loaded tickets", loadedCount == 2);
	}
	
	protected void longBootup() throws Exception
	{
		this.runner.longBootup();
		
		//Assert state
		List<MobileObject> beans = this.runner.getDeviceDatabase().readByStorage(this.runner.getService());
		assertTrue("On Device Ticket service should not be empty!!!", (beans != null && !beans.isEmpty()));		
		for(MobileObject currBean: beans)
		{			
			this.assertBean(currBean, (Ticket)this.ticketConnector.read(currBean.getServerRecordId()));
		}
	}
	
	protected String getNewTicketId()
	{
		return this.ticketConnector.getNewTickets().get(0).getTicketId();
	}
	
	protected void printDeviceData() throws Exception
	{
		log.info("Dumping Device Data-----------------------------------------");
		
		List<MobileObject> beans = this.runner.getDeviceDatabase().readByStorage(this.runner.getService());
		
		for(MobileObject currBean: beans)
		{			
			this.printDeviceBean(currBean);
		}
	}
	
	protected void printServerData() throws Exception
	{
		log.info("Dumping Server Data-----------------------------------------");
		
		List<Ticket> tickets = this.ticketConnector.getTicketds().readAll();
		
		for(Ticket ticket: tickets)
		{			
			log.info("Local Bean Id="+ticket.getTicketId());			
			log.info("Ticket Name="+ticket.getName());
			
			log.info("Customer DB Id="+ticket.getCustomerInfo().getId());
			log.info("Customer Id="+ticket.getCustomerInfo().getCustomerId());
			log.info("Customer Name="+ticket.getCustomerInfo().getName());
			log.info("Customer Comments="+ticket.getCustomerInfo().getComments());
			
			if(ticket.getTechnician() != null)
			{
				log.info("Technician Id="+ticket.getTechnician().getId());
				log.info("Employee Id="+ticket.getTechnician().getEmployeeId());
				log.info("Technician Name="+ticket.getTechnician().getName());
				log.info("Technician Status="+ticket.getTechnician().getStatus());
			}
			
			
			if(ticket.getEquipment() != null)
			{
				log.info("Equipment ID="+ticket.getEquipment().getId());
				log.info("Equipment Name="+ticket.getEquipment().getName());
			}
			
			
			List<Note> notes = ticket.getNotes();
			if(notes != null)
			{
				for(Note note: notes)
				{
					
					log.info("Note ID="+note.getId());
					log.info("Note="+note.getNote());
				}
			}
			
			List<Part> parts = ticket.getParts();
			if(parts != null)
			{
				for(Part part: parts)
				{
					log.info("Part ID="+part.getId());
					log.info("Part="+part.getName());
				}
			}
			
			log.info("-----------------------------------------");
		}
	}
	
	protected void printDeviceBean(MobileObject currBean)
	{
		log.info("Local Bean Id="+currBean.getRecordId());
		log.info("Remote Bean Id="+currBean.getServerRecordId());
		log.info("Ticket Id="+currBean.getValue("ticketId"));
		log.info("Ticket Name="+currBean.getValue("name"));
		
		log.info("Customer DB Id="+currBean.getValue("customerInfo.id"));
		log.info("Customer Id="+currBean.getValue("customerInfo.customerId"));
		log.info("Customer Name="+currBean.getValue("customerInfo.name"));
		log.info("Customer Comments="+currBean.getValue("customerInfo.comments"));
		
		log.info("Technician Id="+currBean.getValue("technician.id"));
		log.info("Employee Id="+currBean.getValue("technician.employeeId"));
		log.info("Technician Name="+currBean.getValue("technician.name"));
		log.info("Technician Status="+currBean.getValue("technician.status"));
		
		
		log.info("Equipment ID="+currBean.getValue("equipment.id"));
		log.info("Equipment Name="+currBean.getValue("equipment.name"));
		
		//Use better indexing API
		BeanList notes = IndexingAPIUtil.readList(currBean, "notes");
		for(int j=0; j<notes.size(); j++)
		{
			BeanListEntry note = notes.getEntryAt(j);
			log.info("Note ID="+note.getProperty("id"));
			log.info("Note="+note.getProperty("note"));
		}
		
		//Use better indexing API
		BeanList parts = IndexingAPIUtil.readList(currBean, "parts");
		for(int j=0; j<parts.size(); j++)
		{
			BeanListEntry part = parts.getEntryAt(j);
			log.info("Part ID="+part.getProperty("id"));
			log.info("Part="+part.getProperty("name"));
		}
		
		log.info("-----------------------------------------");
	}
	
	protected void assertBean(MobileObject deviceBean, Ticket serverBean)
	{
		//Asserting Object Ids
		if(!deviceBean.isCreatedOnDevice())
		{
			assertEquals("Object ids must match!!", deviceBean.getServerRecordId(), serverBean.getTicketId());
		}
		
		assertEquals("Ticket Names must match!!", deviceBean.getValue("name"), serverBean.getName());
		
		//Asserting Nested Properties
		if(serverBean.getCustomerInfo() != null)
		{
			assertEquals("Customer Id must match!!", deviceBean.getValue("customerInfo.customerId"), serverBean.getCustomerInfo().getCustomerId());
			assertEquals("Customer Name must match!!", deviceBean.getValue("customerInfo.name"), serverBean.getCustomerInfo().getName());
			assertEquals("Customer Comments must match!!", deviceBean.getValue("customerInfo.comments"), serverBean.getCustomerInfo().getComments());
		}
		
		if(serverBean.getEquipment() != null)
		{			
			assertEquals("Equipment Name must match!!", deviceBean.getValue("equipment.name"), serverBean.getEquipment().getName());
		}
		
		if(serverBean.getTechnician() != null)
		{
			assertEquals("Technician EmployeeId must match!!", deviceBean.getValue("technician.employeeId"), serverBean.getTechnician().getEmployeeId());
			assertEquals("Technician Name must match!!", deviceBean.getValue("technician.name"), serverBean.getTechnician().getName());
			assertEquals("Technician Status must match!!", deviceBean.getValue("technician.status"), serverBean.getTechnician().getStatus());
		}
		
		//Asserting Indexed Properties from Server to Device
		List<Note> notes = serverBean.getNotes();
		if(notes != null)
		{
			for(Note note: notes)
			{
				this.assertNote(deviceBean, note);								
			}
		}
		
		List<Part> parts = serverBean.getParts();
		if(parts != null)
		{
			for(Part part: parts)
			{
				this.assertPart(deviceBean, part);								
			}
		}
		
		//Asserting Indexed Properties from Device to Server		
		BeanList deviceNotes = IndexingAPIUtil.readList(deviceBean, "notes");
		for(int i=0; i<deviceNotes.size(); i++)
		{
			if(deviceBean.getValue("notes["+i+"].note") != null)
			{
				this.assertNote(deviceBean, i, notes);
			}
		}
		
		BeanList deviceParts = IndexingAPIUtil.readList(deviceBean, "parts");
		for(int i=0; i<deviceParts.size(); i++)
		{
			if(deviceBean.getValue("parts["+i+"].name") != null)
			{
				this.assertPart(deviceBean, i, parts);
			}
		}
	}
	
	private void assertNote(MobileObject deviceBean, Note note)
	{
		BeanList deviceNotes = IndexingAPIUtil.readList(deviceBean, "notes");
		for(int j=0; j<deviceNotes.size(); j++)
		{
			if(deviceBean.getValue("notes["+j+"].note").equals(note.getNote()))
			{
				return;
			}
		}		
		assertTrue("Notes(Indexed Property) are not properly synchronized!!! Missing Note=["+note.getId()+"],["+note.getNote()+"]", 
		false);
	}
	
	private void assertNote(MobileObject deviceBean, int index, List<Note> notes)
	{		
		if(notes != null)
		{
			for(Note note: notes)
			{
				if(deviceBean.getValue("notes["+index+"].note").equals(note.getNote()))
				{
					return;
				}
			}
		}		
		assertTrue("Notes(Indexed Property) are not properly synchronized!!! Missing Note=["+deviceBean.getValue("notes["+index+"].id")+"],["+deviceBean.getValue("notes["+index+"].note")+"]", 
		false);
	}
	
	private void assertPart(MobileObject deviceBean, Part part)
	{
		BeanList deviceParts = IndexingAPIUtil.readList(deviceBean, "parts");
		for(int j=0; j<deviceParts.size(); j++)
		{
			if(deviceBean.getValue("parts["+j+"].name").equals(part.getName()))
			{
				return;
			}
		}		
		assertTrue("Parts(Indexed Property) are not properly synchronized!!! Missing Part=["+part.getId()+"],["+part.getName()+"]", 
		false);
	}
	
	private void assertPart(MobileObject deviceBean, int index, List<Part> parts)
	{		
		if(parts != null)
		{
			for(Part part: parts)
			{
				if(deviceBean.getValue("parts["+index+"].name").equals(part.getName()))
				{
					return;
				}
			}
		}		
		assertTrue("Parts(Indexed Property) are not properly synchronized!!! Missing Part=["+deviceBean.getValue("parts["+index+"].id")+"],["+deviceBean.getValue("parts["+index+"].name")+"]", 
		false);
	}
	//-----------------------------------------------------------------------------------------------------------------------
	protected String createNewServerObject() throws Exception
	{
		Ticket ticket = this.dataGenerator.generateTransientData("ticket://new");
		String ticketId = this.ticketConnector.getTicketds().create(ticket);
		ticket = this.ticketConnector.getTicketds().readByTicketId(ticketId);
		
		List serverChangeLog = new ArrayList();
		ChangeLogEntry serverEntry = new ChangeLogEntry();
		serverEntry.setNodeId(this.runner.getService());
		serverEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
		serverEntry.setRecordId(ticket.getTicketId());
		serverChangeLog.add(serverEntry);
		this.serverSyncEngine.addChangeLogEntries(this.runner.getDeviceId(), this.runner.getApp(), serverChangeLog);
		
		return ticket.getTicketId();
	}
	
	protected void updateServerObject(String ticketId) throws Exception
	{
		Ticket ticket = (Ticket)this.ticketConnector.read(ticketId);
		
		ticket.setName("name://updated");
		
		
		List serverChangeLog = new ArrayList();
		ChangeLogEntry serverEntry = new ChangeLogEntry();
		serverEntry.setNodeId(this.runner.getService());
		serverEntry.setOperation(ServerSyncEngine.OPERATION_UPDATE);
		serverEntry.setRecordId(ticketId);
		serverChangeLog.add(serverEntry);
		this.serverSyncEngine.addChangeLogEntries(this.runner.getDeviceId(), this.runner.getApp(), serverChangeLog);
	}
	
	protected void deleteServerObject(String ticketId) throws Exception
	{
		Ticket ticket = (Ticket)this.ticketConnector.read(ticketId);
		
		
		this.ticketConnector.delete(ticket);
		
		
		List serverChangeLog = new ArrayList();
		ChangeLogEntry serverEntry = new ChangeLogEntry();
		serverEntry.setNodeId(this.runner.getService());
		serverEntry.setOperation(ServerSyncEngine.OPERATION_DELETE);
		serverEntry.setRecordId(ticketId);
		serverChangeLog.add(serverEntry);
		this.serverSyncEngine.addChangeLogEntries(this.runner.getDeviceId(), this.runner.getApp(), serverChangeLog);
	}
	
	protected String createNewDeviceObject() throws Exception
	{
		List<MobileObject> beans = this.runner.getDeviceDatabase().readByStorage(this.runner.getService());
		
		//Create the new ticket on the device
		MobileObject bean = null;
		for(MobileObject cour: beans)
		{
			if(!cour.isProxy() && !cour.isCreatedOnDevice())
			{
				bean = cour;
				break;
			}
		}
		
		MobileObject newTicket = new MobileObject();
		newTicket.setStorageId(bean.getStorageId());
		
		newTicket.setValue("name", "This new refrigerator is broken");
		
		//Referenced Nested Properties
		newTicket.setValue("customerInfo.id", bean.getValue("customerInfo.id"));
		newTicket.setValue("customerInfo.customerId", bean.getValue("customerInfo.customerId"));
		newTicket.setValue("customerInfo.name", bean.getValue("customerInfo.name"));
		newTicket.setValue("customerInfo.comments", bean.getValue("customerInfo.comments"));
		
				
		//Notes
		for(int i=0; i<2; i++)
		{
			BeanListEntry note = new BeanListEntry();
			note.setProperty("note", "note://"+i+"/added");			
			IndexingAPIUtil.addBean(newTicket, "notes", note);
		}
		
		//Parts
		for(int i=0; i<2; i++)
		{
			BeanListEntry part = new BeanListEntry();
			part.setProperty("name", "part://"+i+"/added");
			IndexingAPIUtil.addBean(newTicket, "parts", part);
		}		
				
		this.runner.create(newTicket);
		
		return newTicket.getRecordId();
	}
	
	protected void updateDeviceObject(String objectId) throws Exception
	{
		List<MobileObject> beans = this.runner.getDeviceDatabase().readByStorage(this.runner.getService());
		MobileObject storedBean = null;
		for(MobileObject cour: beans)
		{
			if(cour.getValue("technician.name") != null && !cour.isProxy())
			{
				storedBean = cour;
				break;
			}
		}
		
		
		MobileObject bean = this.runner.getDeviceDatabase().read(this.runner.getService(), objectId);	
		
		bean.setValue("name", "name://updated");
		
		//Referenced Technician	
		bean.setValue("technician.id", storedBean.getValue("technician.id"));
		bean.setValue("technician.employeeId", storedBean.getValue("technician.employeeId"));
		bean.setValue("technician.name", storedBean.getValue("technician.name"));
		bean.setValue("technician.status", storedBean.getValue("technician.status"));
		
		//Notes
		for(int i=0; i<2; i++)
		{
			BeanListEntry note = new BeanListEntry();
			note.setProperty("note", "note://"+i+"/updated");			
			IndexingAPIUtil.addBean(bean, "notes", note);
		}
		
		//Parts
		for(int i=0; i<2; i++)
		{
			BeanListEntry part = new BeanListEntry();
			part.setProperty("name", "part://"+i+"/updated");
			IndexingAPIUtil.addBean(bean, "parts", part);
		}
				
		this.runner.update(bean);
	}
	
	protected void deleteDeviceObject(String objectId) throws Exception
	{
		this.runner.delete(this.runner.getDeviceDatabase().read(this.runner.getService(), objectId));
	}
}
