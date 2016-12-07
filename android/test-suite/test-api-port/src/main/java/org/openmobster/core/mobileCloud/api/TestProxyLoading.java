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
public final class TestProxyLoading extends AbstractAPITest 
{
	public void runTest()
	{		
		try
		{	
			this.startBootSync();
			this.waitForBeans();
			
			MobileBean[] beans = MobileBean.readAll(this.service);
			this.assertNotNull(beans, this.getInfo()+"/MustNotBeNull");
			
			//Download all data associated with the beans (takes care of proxy-lazy loaded beans)						
			//Wait for proxy loading to load the rest
			int attempts = 5;
			while(beans.length < 3 && attempts > 0)
			{
				System.out.println("Waiting on background proxy loading.........");
				Thread.currentThread().sleep(20000);
				beans = MobileBean.readAll(this.service);
				attempts--;
			}
			
			if(beans.length < 3)
			{
				throw new IllegalStateException("Background State Management was not able to get the State ready in time...Try again");
			}
			
			//Test Proxy based Loading
			MobileBean curr = MobileBean.readById(service, "unique-2");
			
			assertEquals(curr.getService(), this.service, this.getInfo()+"://Service does not match");
			
			String id = curr.getId();
			assertTrue(id.equals("unique-1") || id.equals("unique-2"), this.getInfo()+"://Id Does not match");
			
			assertEquals(curr.getValue("from"), "from@gmail.com", this.getInfo()+"://From does not match");
			assertEquals(curr.getValue("to"), "to@gmail.com", this.getInfo()+"://To does not match");
			assertEquals(curr.getValue("subject"), "This is the subject<html><body>"+id+"</body></html>", this.getInfo()+"://Subject does not match");
			assertEquals(curr.getValue("message"), 
			"<tag apos='apos' quote=\"quote\" ampersand='&'>"+id+"/Message"+"</tag>",
			this.getInfo()+"://Message does not match");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}
}
