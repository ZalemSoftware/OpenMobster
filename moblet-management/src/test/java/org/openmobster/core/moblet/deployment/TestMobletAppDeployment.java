/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.moblet.deployment;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;
import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.moblet.registry.Registry;
import org.openmobster.core.moblet.Tool;

/**
 * @author openmobster@gmail.com
 */
public class TestMobletAppDeployment extends TestCase 
{
	private static Logger log = Logger.getLogger(TestMobletAppDeployment.class);
	
	private Registry registry;

	
	public void setUp() throws Exception
	{
		ServiceManager.bootstrap();
		registry = (Registry)ServiceManager.locate(Registry.uri);
	}
	
	public void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	
	public void testRegistry() throws Exception
	{
		this.readBinary("testapp");
		this.readBinary("testapp2");
		
		this.readApkBinary("/android-2.0/test.apk");
	}
	//-------------------------------------------------------------------------------------------------
	private void readBinary(String binaryLocation) throws Exception
	{
		InputStream is = registry.getAppBinary(binaryLocation);
		Tool.assertBinary(this, is);
	}
	
	private void readApkBinary(String binaryLocation) throws Exception
	{
		InputStream is = registry.getAppBinary(binaryLocation);
		this.assertNotNull(is);
	}
}
