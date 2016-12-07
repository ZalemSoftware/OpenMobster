/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.phonegap.plugin.sandbox.cloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openmobster.core.common.Utilities;

/**
 *
 * @author openmobster@gmail.com
 */
public final class Store
{
	private Map<String,Object> store;
	
	public Store()
	{
		this.store = new HashMap<String,Object>();
	}
	
	public void start()
	{
		this.store.clear();
		
		for(int i=0; i<5; i++)
		{
			PhoneGapBean bean = new PhoneGapBean();
			
			bean.setOid(Utilities.generateUID());
			
			bean.setTitle("title://"+i);
			
			bean.setCustomers(new String[]{"string[0]["+i+"]","string[1]["+i+"]","string[2]["+i+"]","string[3]["+i+"]"});
			
			bean.setMessages(new ArrayList<Message>());
			for(int j=0; j<5;j++)
			{
				Message message = new Message();
				message.setFrom("from"+j+"@gmail.com");
				message.setTo("to"+j+"@gmail.com");
				message.setMessage("message://"+j);
				bean.getMessages().add(message);
			}
			
			this.save(bean.getOid(), bean);
		}
	}
	
	public Collection<Object> readAll()
	{
		return this.store.values();
	}
	
	public Object read(String oid)
	{
		return this.store.get(oid);
	}
	
	public String save(String oid, Object object)
	{
		this.store.put(oid, object);
		return oid;
	}
	
	public void delete(String oid)
	{
		this.store.remove(oid);
	}
}
