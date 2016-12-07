/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.android.api.sync.CommitException;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestBeanUpdateOptimisticLocking extends AbstractAPITest 
{
	public void runTest()
	{		
		try
		{
			this.startBootSync();
			this.waitForBeans();
			
			MobileBean instance1 = MobileBean.readById(this.service, "unique-1");
			MobileBean instance2 = MobileBean.readById(this.service, "unique-1");
			
			String newValueInstance1 = "/instance1/from/Updated";
			instance1.setValue("from", newValueInstance1);
			instance1.save();
			
			boolean exceptionOccured= false;
			String newValueInstance2 = "/instance2/from/Updated";
			instance2.setValue("from", newValueInstance2);
			try
			{
				instance2.save(); //This should throw an exception
			}
			catch(CommitException e)
			{				
				exceptionOccured = true;
			}
			instance2.refresh();
			
			System.out.println("****************************************************");
			System.out.println("Instance2: "+instance2.getValue("from"));
			System.out.println("****************************************************");
			
			assertEquals(instance2.getValue("from"), "/instance1/from/Updated", this.getInfo()+"://Instance2_not_integral/"+instance2.getValue("from"));
			assertTrue(exceptionOccured, this.getInfo()+"://LockingException_Should_Occur");			
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
}
