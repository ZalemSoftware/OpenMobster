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
 */
public class TestSettingNullNewBean extends AbstractAPITest 
{
	@Override
	public void runTest()
	{
		try
		{
			this.startBootSync();
			this.waitForBeans();
			
			MobileBean bean = MobileBean.newInstance(service);
			
			bean.setValue("to", "to@gmail.com");
			bean.setValue("newField", null);
			bean.setValue("to", null);
			bean.save();
			
			String beanId = bean.getId();
			
			Thread.sleep(20000); //sleep hopefully the update sync happens in this window
			
			this.startBootSync();
			this.waitForBeans();
			
			do
			{
				bean = MobileBean.readById(service, beanId);
			}while(bean == null);
			this.assertNotNull(bean, this.getInfo()+"/MustNotBeNull");
			
			this.assertNull(bean.getValue("to"), this.getInfo()+"/ToValueMustBeNull");
			this.assertNull(bean.getValue("newField"), this.getInfo()+"/NewFieldValueMustBeNull");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
}
