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
public class TestEmptyStringsFromCloud extends AbstractAPITest 
{
	@Override
	public void runTest()
	{
		try
		{
			this.startBootSync();
			this.waitForBeans();
			
			MobileBean bean = MobileBean.readById(service, "unique-8675309");
			this.assertNotNull(bean, this.getInfo()+"/MustNotBeNull");
			this.assertEquals(bean.getValue("to"),"", this.getInfo()+"/ToValueMustBeEmpty");
			
			bean.setValue("from", "from@gmail.com");
			bean.save();
			
			Thread.sleep(20000); //sleep hopefully the update sync happens in this window
			
			this.startBootSync();
			this.waitForBeans();
			
			bean = MobileBean.readById(service, "unique-8675309");
			this.assertNotNull(bean, this.getInfo()+"/MustNotBeNull");
			
			this.assertEquals(bean.getValue("from"), "from@gmail.com", this.getInfo()+"/FromValueMustNotBeNull");
			this.assertEquals(bean.getValue("to"),"", this.getInfo()+"/ToValueMustBeEmpty");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
}
