/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;

/**
 * @author openmobster@gmail.com
 *
 */
public final class TestMobileServiceInvocation extends AbstractAPITest 
{
	public void runTest()
	{		
		try
		{
			Request request = new Request("mockMobileService");
			request.setAttribute("test1", "mockValueTest1");
			request.setAttribute("test2", "mockValueTest2");
			
			Response response = MobileService.invoke(request);
			
			System.out.println("---------------------------------------------");
			String test1 = response.getAttribute("test1");
			String test2 = response.getAttribute("test2");
			System.out.println("test1="+test1);
			System.out.println("test2="+test2);
			
			this.assertEquals(test1, "response://mockValueTest1", this.getInfo()+"/test1MustMatch");
			this.assertEquals(test2, "response://mockValueTest2", this.getInfo()+"/test2MustMatch");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}
}
