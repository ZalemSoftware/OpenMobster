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
public final class TestLargeObjectBootup extends AbstractLargeObjectTest
{
	public void runTest()
	{		
		try
		{	
			this.startBootSync();
			this.waitForBeans();
			
			MobileBean[] beans = MobileBean.readAll(this.service);
			
			//Download all data associated with the beans (takes care of proxy-lazy loaded beans)						
			//Wait for proxy loading to load the rest
			int attempts = 10;
			while((beans == null || beans.length ==0) && attempts > 0)
			{
				System.out.println("Waiting on background proxy loading.........");
				Thread.sleep(20000);
				beans = MobileBean.readAll(this.service);
				attempts--;
			}
			
			if(beans == null || beans.length ==0)
			{
				throw new IllegalStateException("Background State Management was not able to get the State ready in time...Try again");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}
}
