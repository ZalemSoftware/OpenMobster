/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileObject.xml;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;

/**
 * @author openmobster@gmail.com
 */
public class TestResultSetSerialization extends TestCase
{
	private static Logger log = Logger.getLogger(TestResultSetSerialization.class);
	
	private MobileObjectSerializer serializer;
			
	protected void setUp() throws Exception 
	{		
		ServiceManager.bootstrap();
		
		this.serializer = (MobileObjectSerializer)ServiceManager.
		locate("mobileObject://MobileObjectSerializer");
	}	
		
	protected void tearDown() throws Exception 
	{
		ServiceManager.shutdown();
	}
	//-------------------------------------------------------------------------------------------------------------------
	public void testSimpleResultSet() throws Exception
	{		
		MockBean bean = new MockBean();
		bean.setName("This is mock bean!!");
		
		ResultSet resultSet = new ResultSet();
		Bean[] beans = new Bean[]{bean};
		resultSet.setBeans(beans);
		
				
		String deviceXml = this.serializer.serialize(resultSet);
		log.info("--------------------------------------");		
		log.info(deviceXml);
		log.info("--------------------------------------");		
		
		//Assert state
		resultSet = (ResultSet)this.serializer.deserialize(ResultSet.class, deviceXml);
		beans = resultSet.getBeans();
		assertEquals(1, beans.length);
		assertEquals("This is mock bean!!", ((MockBean)beans[0]).getName());
	}	
}
