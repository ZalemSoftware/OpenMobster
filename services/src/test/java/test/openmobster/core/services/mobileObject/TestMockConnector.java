/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.services.mobileObject;

import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.services.MobileObjectMonitor;

/**
 * @author openmobster@gmail.com
 */
public class TestMockConnector extends TestCase
{
	/**
	 * 
	 */
	private MobileObjectMonitor monitor = null;
	
	/**
	 * 
	 */
	public void setUp()
	{
		ServiceManager.bootstrap();
		
		this.monitor = (MobileObjectMonitor)ServiceManager.locate("services://MobileObjectMonitor");
	}
	
	/**
	 * 
	 */
	public void tearDown()
	{
		ServiceManager.shutdown();
	}
	
	public void testMonitor()
	{
		/*MockConnector connector = (MockConnector)monitor.lookup("mock");
		assertNotNull("MockConnector not registered....",connector);*/
	}
}
