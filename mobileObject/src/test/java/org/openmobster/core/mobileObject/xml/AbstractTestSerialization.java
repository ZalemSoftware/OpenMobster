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
public abstract class AbstractTestSerialization extends TestCase 
{
	private static Logger log = Logger.getLogger(AbstractTestSerialization.class);
	
	protected MobileObjectSerializer serializer;
	
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
}
