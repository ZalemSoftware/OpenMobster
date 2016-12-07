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
public final class TestBeanUpdate extends AbstractAPITest 
{
	public void runTest()
	{		
		try
		{
			this.startBootSync();
			this.waitForBeans();
			
			MobileBean curr = MobileBean.readById(service, "unique-1");
			
			assertEquals(curr.getService(), this.service, this.getInfo()+"://Service does not match");
			
			String id = curr.getId();
			assertTrue(id.equals("unique-1") || id.equals("unique-2"), this.getInfo()+"://Id Does not match");
			
			assertEquals(curr.getValue("from"), "from@gmail.com", this.getInfo()+"://From does not match");
			assertEquals(curr.getValue("to"), "to@gmail.com", this.getInfo()+"://To does not match");
			assertEquals(curr.getValue("subject"), "This is the subject<html><body>"+id+"</body></html>", this.getInfo()+"://Subject does not match");
			assertEquals(curr.getValue("message"), 
			"<tag apos='apos' quote=\"quote\" ampersand='&'>"+id+"/Message"+"</tag>",
			this.getInfo()+"://Message does not match");
			
			curr.setValue("from", "from/Updated");
			assertEquals(curr.getValue("from"), "from/Updated", this.getInfo()+"://From does not match");
			
			curr.setBinaryValue("attachment", "blahblah".getBytes());
			
			MobileBean stored = MobileBean.readById(this.service, id);
			assertEquals(stored.getValue("from"), "from@gmail.com", this.getInfo()+"://Should_Not_Be_Updated_Yet");
			
			System.out.println("Saving the bean.....");
			curr.save();
			
			stored = MobileBean.readById(this.service, id);
			assertEquals(stored.getValue("from"), "from/Updated", this.getInfo()+"://Should_Be_Updated_Now");
			
			byte[] attachment = stored.getBinaryValue("attachment");
			String attachmentValue = new String(attachment);
			assertEquals(attachmentValue,"blahblah",this.getInfo()+"://Attachment_Didnot_Match");
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
}
