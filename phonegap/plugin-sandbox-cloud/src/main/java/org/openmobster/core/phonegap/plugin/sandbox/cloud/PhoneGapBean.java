/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.phonegap.plugin.sandbox.cloud;

import java.util.List;
import java.util.ArrayList;

import java.io.Serializable;

import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanId;
import org.openmobster.core.common.Utilities;

/**
 *
 * @author openmobster@gmail.com
 */
public class PhoneGapBean implements MobileBean,Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6284688877268345102L;


	@MobileBeanId
	private String oid;
	
	
	private String title;
	
	private String[] customers;
	
	private List<Message> messages;
		
	public PhoneGapBean()
	{
		
	}


	public String getOid() {
		return oid;
	}


	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String[] getCustomers() {
		return customers;
	}


	public void setCustomers(String[] customers) {
		this.customers = customers;
	}


	public List<Message> getMessages() {
		return messages;
	}


	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}
	
	//-------------------------------------------------------------------------------------------
	static List<PhoneGapBean> bootup()
	{
		List<PhoneGapBean> bootup = new ArrayList<PhoneGapBean>();
		
		for(int i=0; i<5; i++)
		{
			PhoneGapBean bean = new PhoneGapBean();
			
			bean.oid = Utilities.generateUID();
			
			bean.title = "title://"+i;
			
			bean.customers = new String[]{"string[0]["+i+"]","string[1]["+i+"]","string[2]["+i+"]","string[3]["+i+"]"};
			
			bean.messages = new ArrayList<Message>();
			for(int j=0; j<5;j++)
			{
				Message message = new Message();
				message.setFrom("from"+j+"@gmail.com");
				message.setTo("to"+j+"@gmail.com");
				message.setMessage("message://"+j);
				bean.messages.add(message);
			}
			
			bootup.add(bean);
		}
		
		return bootup;
	}
}
