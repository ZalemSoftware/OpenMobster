/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.fuseapp.cloud;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * This is a mock data repository. In a real world setting this would provide data that is actually stored in 
 * the relevant data store like a relational database, ERP backend, CRM backend, some other Cloud service like 
 * say a Google service via standard Google API. phewwwww.....You get the point, I hope ;)
 * 
 * @author openmobster@gmail.com
 */
public class DemoDataRepository
{
	private Map<String, DemoBean> data;
	private List<DemoBean> newBeans;
	
	public DemoDataRepository()
	{
		this.data = new HashMap<String, DemoBean>();
		this.newBeans = new ArrayList<DemoBean>();
	}

	public Map<String, DemoBean> getData()
	{
		return data;
	}

	public void setData(Map<String, DemoBean> data)
	{
		this.data = data;
	}
	
	public void start()
	{
		//load the demo repository with demo data
		for(int i=0; i<10; i++)
		{
			String beanId = ""+i;
			DemoBean bean = new DemoBean();
			bean.setBeanId(beanId);
			
			//Set the demo string
			bean.setDemoString("/demostring/"+i);
			
			//Set the demo array
			String[] demoArray = new String[5];
			for(int index=0; index<demoArray.length; index++)
			{
				demoArray[index] = "/demoarray/"+index+"/"+i;
			}
			bean.setDemoArray(demoArray);
			
			//Set the demo list
			List<String> demoList = new ArrayList<String>();
			for(int index=0; index<5; index++)
			{
				demoList.add("/demolist/"+index+"/"+i);
			}
			bean.setDemoList(demoList);
			
			this.data.put(beanId, bean);
		}
	}
	
	public void stop()
	{
		this.data = null;
	}
	
	public void addNewBean()
	{
		DemoBean newBean = this.createNewDemoBean();		
		this.newBeans.add(newBean);
	}
	
	public List<DemoBean> getNewBeans()
	{		
		return this.newBeans;
	}
	
	public void cleanNewBeans()
	{
		if(this.newBeans != null && !this.newBeans.isEmpty())
		{
			for(DemoBean newBean: this.newBeans)
			{
				this.data.put(newBean.getBeanId(), newBean);
			}
			this.newBeans.clear();
		}
	}
	
	private DemoBean createNewDemoBean()
	{
		int totalBeans = this.getData().size() + this.newBeans.size();
		
		String beanId = ""+totalBeans;
		DemoBean bean = new DemoBean();
		bean.setBeanId(beanId);
		
		//Set the demo string
		bean.setDemoString("/demostring/"+beanId);
		
		//Set the demo array
		String[] demoArray = new String[5];
		for(int index=0; index<demoArray.length; index++)
		{
			demoArray[index] = "/demoarray/"+index+"/"+beanId;
		}
		bean.setDemoArray(demoArray);
		
		//Set the demo list
		List<String> demoList = new ArrayList<String>();
		for(int index=0; index<5; index++)
		{
			demoList.add("/demolist/"+index+"/"+beanId);
		}
		bean.setDemoList(demoList);
				
		
		return bean;
	}
}
