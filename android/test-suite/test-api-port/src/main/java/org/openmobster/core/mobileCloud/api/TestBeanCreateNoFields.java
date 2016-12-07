/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api;

import org.openmobster.android.api.sync.MobileBean;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestBeanCreateNoFields extends AbstractAPITest 
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
			
			newBean.save();
			assertTrue(newBean.isCreateOnDevice(), this.getInfo()+"://NewBean_should_be_created_ondevice");
			assertTrue(newBean.getId()!=null, this.getInfo()+"://NewBean_id_should_not_be_null");
			assertTrue(newBean.getServerId()==null, this.getInfo()+"://NewBean_serverId_should_be_null");
			
			System.out.println("New Ondevice RecordId="+newBean.getId());			
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
}
