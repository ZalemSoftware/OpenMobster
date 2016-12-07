/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.offlineApp.cloud.sync;

import java.util.List;

import org.apache.log4j.Logger;
import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;


import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.test.framework.MobileBeanRunner;

/**
 * @author openmobster@gmail.com
 */
public class TestDemoChannel extends TestCase
{
	private static Logger log = Logger.getLogger(TestDemoChannel.class);
	
	protected MobileBeanRunner runner;
	
	public void setUp() throws Exception
	{
		ServiceManager.bootstrap();
		this.runner = (MobileBeanRunner)ServiceManager.locate("mobileBeanRunner");
		this.runner.setApp("testApp");
		this.runner.activateDevice();
		this.runner.bootService();
	}
	
	public void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	
	public void testBootUp() throws Exception
	{
		//Assert the state on the device side
		List<MobileObject> beans = this.runner.getDeviceDatabase().readByStorage(this.runner.getService());
		assertTrue("On Device DemoChannel service should not be empty!!!", (beans != null && !beans.isEmpty()));
		
		for(MobileObject mobileObject: beans)
		{
			log.info("BeanId: "+mobileObject.getRecordId());
			log.info("IsFullyLoaded: "+!mobileObject.isProxy());
			if(!mobileObject.isProxy())
			{
				log.info("Demo String: "+mobileObject.getValue("demoString"));
				
				int demoArrayLength = mobileObject.getArrayLength("demoArray");
				for(int i=0; i<demoArrayLength; i++)
				{
					log.info("DemoArray["+i+"]: "+mobileObject.getValue("demoArray["+i+"]"));
				}
				
				int demoListLength = mobileObject.getArrayLength("demoList");
				for(int i=0; i<demoListLength; i++)
				{
					log.info("DemoList["+i+"]: "+mobileObject.getValue("demoList["+i+"]"));
				}
			}
			log.info("-----------------------------------------------------------------");
		}
	}
}
