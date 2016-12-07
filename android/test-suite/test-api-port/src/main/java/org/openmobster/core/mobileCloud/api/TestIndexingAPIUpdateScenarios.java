/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api;

import org.openmobster.android.api.sync.BeanList;
import org.openmobster.android.api.sync.BeanListEntry;
import org.openmobster.android.api.sync.MobileBean;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestIndexingAPIUpdateScenarios extends AbstractAPITest 
{
	public void runTest()
	{		
		try
		{
			this.testCreateClearUpdateLifeCycle();			
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}
	//----------------------------------------------------------------------------------------------------------------------
	private void testCreateClearUpdateLifeCycle() throws Exception
	{
		//Create a bean into existence
		String beanId = this.createBean();
		
		//Start working with this bean
		MobileBean bean = MobileBean.readById(this.service, beanId);
		
		//Clear out the email list state
		bean.clearList("emails");
		bean.clearList("fruits");
		//Make state from transient to persistent
		bean.save();
		
		//*Add* new emails,fruits to the same bean
		this.updateBean(beanId);
	}
	//----------------------------------------------------------------------------------------------------------------------
	private String createBean() throws Exception
	{
		MobileBean newBean = MobileBean.newInstance(this.service);		
		assertTrue(newBean.isInitialized(), this.getInfo()+"://NewBean_should_be_initialized");
		assertTrue(newBean.isCreateOnDevice(), this.getInfo()+"://NewBean_should_be_created_ondevice");
		assertTrue(newBean.getId()==null, this.getInfo()+"://NewBean_id_should_be_null");
		assertTrue(newBean.getServerId()==null, this.getInfo()+"://NewBean_serverId_should_be_null");
		
		//Prepare the Email List
		BeanList emails = new BeanList("emails");
		for(int i=0; i<5; i++)
		{
			BeanListEntry bean = new BeanListEntry();
			bean.setProperty("from", i+"://from");
			bean.setProperty("to", i+"://to");
			bean.setProperty("subject", i+"://subject");
			bean.setProperty("message", i+"://message");
			
			emails.addEntry(bean);			
		}
		newBean.saveList(emails);
		
		//Prepare the fruit list
		BeanList fruits = new BeanList("fruits");
		for(int i=0; i<5; i++)
		{
			BeanListEntry bean = new BeanListEntry();
			bean.setValue(i+"://fruit");
			
			fruits.addEntry(bean);
		}
		newBean.saveList(fruits);
		
		//commit
		newBean.save();
		
		//Assert Email List State
		System.out.println("-------------------------------------------------------------------------");
		BeanList list = newBean.readList("emails");
		assertTrue(list!=null && list.size()>0, this.getInfo()+"/Emails/MustNotBeNull");
		for(int index=0; index<list.size(); index++)
		{
			BeanListEntry email = list.getEntryAt(index);
			System.out.println(email.getProperty("from"));
			System.out.println(email.getProperty("to"));
			System.out.println(email.getProperty("subject"));
			System.out.println(email.getProperty("message"));
			this.assertEquals(email.getProperty("from"), index+"://from", this.getClass()+"://createBean/from");
			this.assertEquals(email.getProperty("to"), index+"://to", this.getClass()+"://createBean/to");
			this.assertEquals(email.getProperty("subject"), index+"://subject", this.getClass()+"://createBean/subject");
			this.assertEquals(email.getProperty("message"), index+"://message", this.getClass()+"://createBean/message");
		}
		
		//Assert Fruit List State
		System.out.println("-------------------------------------------------------------------------");
		BeanList fruitList = newBean.readList("fruits");
		assertTrue(fruitList!=null && fruitList.size()>0, this.getInfo()+"/Fruits/MustNotBeNull");
		for(int index=0; index<fruitList.size(); index++)
		{
			BeanListEntry fruit = fruitList.getEntryAt(index);
			System.out.println(fruit.getValue());			
			this.assertEquals(fruit.getValue(), index+"://fruit", this.getClass()+"://createBean/fruit");
		}
		
		return newBean.getId();
	}
	
	private void updateBean(String beanId) throws Exception
	{
		MobileBean bean = MobileBean.readById(this.service, beanId);
		
		for(int i=0; i<5; i++)
		{
			BeanListEntry emailBean = new BeanListEntry();
			emailBean.setProperty("from", i+"://from/updated");
			emailBean.setProperty("to", i+"://to/updated");
			emailBean.setProperty("subject", i+"://subject/updated");
			emailBean.setProperty("message", i+"://message/updated");
			
			bean.addBean("emails", emailBean);			
		}
		
		for(int i=0; i<5; i++)
		{
			BeanListEntry fruitBean = new BeanListEntry();
			fruitBean.setValue(i+"://fruit/updated");						
			bean.addBean("fruits", fruitBean);			
		}
		
		//commit
		bean.save();
		
		//Assert Email List State
		System.out.println("-------------------------------------------------------------------------");
		BeanList list = bean.readList("emails");
		assertTrue(list!=null && list.size()>0, this.getInfo()+"/Emails/MustNotBeNull");
		for(int index=0; index<list.size(); index++)
		{
			BeanListEntry email = list.getEntryAt(index);
			System.out.println(email.getProperty("from"));
			System.out.println(email.getProperty("to"));
			System.out.println(email.getProperty("subject"));
			System.out.println(email.getProperty("message"));
			this.assertEquals(email.getProperty("from"), index+"://from/updated", this.getClass()+"://createBean/from");
			this.assertEquals(email.getProperty("to"), index+"://to/updated", this.getClass()+"://createBean/to");
			this.assertEquals(email.getProperty("subject"), index+"://subject/updated", this.getClass()+"://createBean/subject");
			this.assertEquals(email.getProperty("message"), index+"://message/updated", this.getClass()+"://createBean/message");
		}
		
		//Assert Fruit List State
		System.out.println("-------------------------------------------------------------------------");
		BeanList fruitList = bean.readList("fruits");
		assertTrue(fruitList!=null && fruitList.size()>0, this.getInfo()+"/Fruits/MustNotBeNull");
		for(int index=0; index<fruitList.size(); index++)
		{
			BeanListEntry fruit = fruitList.getEntryAt(index);
			System.out.println(fruit.getValue());			
			this.assertEquals(fruit.getValue(), index+"://fruit/updated", this.getClass()+"://createBean/fruit");
		}
	}
}
