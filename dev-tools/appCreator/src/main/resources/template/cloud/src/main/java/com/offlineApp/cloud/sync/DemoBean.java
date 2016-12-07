/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.cloud.sync;

import java.util.List;

import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanId;

/**
 * @author openmobster@gmail.com
 */
public class DemoBean implements MobileBean 
{
	private static final long serialVersionUID = -13825574505549274L;

	@MobileBeanId
	private String beanId;
	
	private String demoString; //used to demonstrate mobilizing a simple property of type 'String'
	
	private String[] demoArray; //used to demonstrate mobilizing of an indexed property that is an 'array'
	
	private List<String> demoList; //used to demonstrate mobilizing an indexed property that is a 'list'
	
	public DemoBean()
	{
		
	}

	public String getBeanId()
	{
		return beanId;
	}

	public void setBeanId(String beanId)
	{
		this.beanId = beanId;
	}

	public String getDemoString()
	{
		return demoString;
	}

	public void setDemoString(String demoString)
	{
		this.demoString = demoString;
	}

	public String[] getDemoArray()
	{
		return demoArray;
	}

	public void setDemoArray(String[] demoArray)
	{
		this.demoArray = demoArray;
	}

	public List<String> getDemoList()
	{
		return demoList;
	}

	public void setDemoList(List<String> demoList)
	{
		this.demoList = demoList;
	}
}
