/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api;

import java.util.Set;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.android.api.sync.BeanListEntry;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObjectDatabase;
import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObject;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestBeanCreateWithFields extends AbstractAPITest 
{
	public void runTest()
	{		
		try
		{
			MobileBean newBean = MobileBean.newInstance(this.service);
			
			assertTrue(newBean.isInitialized(), this.getInfo()+"://NewBean_should_be_initialized");
			assertTrue(newBean.isCreateOnDevice(), this.getInfo()+"://NewBean_should_be_created_ondevice");
			assertTrue(newBean.getId()==null, this.getInfo()+"://NewBean_id_should_be_null");
			assertTrue(newBean.getServerId()==null, this.getInfo()+"://NewBean_serverId_should_be_null");
			
			newBean.setValue("from", "newBean/From");
			
			//Shows how to setup and array of Strings named fruits
			for(int i=0; i<3; i++)
			{
				BeanListEntry fruit = new BeanListEntry();
				fruit.setValue(i+"://fruit");
				newBean.addBean("fruits", fruit);
			}
			
			//Shows how to setup an array of the Email object named emails
			for(int i=0; i<3; i++)
			{
				BeanListEntry email = new BeanListEntry();
				email.setProperty("from", "from("+i+")@gmail.com");
				email.setProperty("to", "to("+i+")@gmail.com");
				email.setProperty("subject", "subject"+i);
				email.setProperty("message", "message"+i);
				newBean.addBean("emails", email);
			}
			
			newBean.save();
			
			
			String id = newBean.getId();
			
			assertTrue(newBean.isCreateOnDevice(), this.getInfo()+"://NewBean_should_be_created_ondevice");
			assertTrue(newBean.getId()!=null, this.getInfo()+"://NewBean_id_should_not_be_null");
			assertTrue(newBean.getServerId()==null, this.getInfo()+"://NewBean_serverId_should_be_null");
			assertEquals(newBean.getValue("from"), "newBean/From", this.getInfo()+"://From_value_should_match");
			
			MobileBean instance2 = MobileBean.readById(this.service, id);
			assertTrue(instance2.isCreateOnDevice(), this.getInfo()+"://NewBean_should_be_created_ondevice");
			assertTrue(instance2.getId()!=null, this.getInfo()+"://NewBean_id_should_not_be_null");
			assertTrue(instance2.getServerId()==null, this.getInfo()+"://NewBean_serverId_should_be_null");
			assertEquals(instance2.getValue("from"), "newBean/From", this.getInfo()+"://From_value_should_match");			
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
}
