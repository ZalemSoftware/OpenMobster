/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.phonegap.plugin.jquery.cloud;

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
		
		for(int i=0; i<4; i++)
		{
			JQueryBean bean = new JQueryBean();
			
			bean.setOid(Utilities.generateUID());
			
			bean.setTitle("title"+i);
			bean.setComments("comments for issue #"+i);
			
			switch(i)
			{
				case 0:
					bean.setCustomer("microsoft");
					bean.setSpecialist("steve_b");
				break;
				
				case 1:
					bean.setCustomer("google");
					bean.setSpecialist("eric_s");
				break;
					
				case 2:
					bean.setCustomer("oracle");
					bean.setSpecialist("larry_e");
				break;
					
				case 3:
					bean.setCustomer("apple");
					bean.setSpecialist("steve_j");
				break;
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
