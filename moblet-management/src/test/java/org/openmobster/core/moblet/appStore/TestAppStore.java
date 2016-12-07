/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.moblet.appStore;

import java.util.List;
import junit.framework.TestCase;
import org.apache.log4j.Logger;


import org.openmobster.cloud.api.rpc.Request;
import org.openmobster.cloud.api.rpc.Response;
import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.moblet.Tool;

/**
 * @author openmobster@gmail.com
 */
public class TestAppStore extends TestCase 
{
	private static Logger log = Logger.getLogger(TestAppStore.class);
	
	private AppStore appStore;
	
	public void setUp() throws Exception
	{
		ServiceManager.bootstrap();
		appStore = (AppStore)ServiceManager.locate("moblet-management://appStore");
	}
	
	public void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
			
	public void testGetRegisteredApps() throws Exception
	{
		Request request = new Request("moblet-management://appStore");
		request.setAttribute("action", "getRegisteredApps");	
		
		Response response = this.appStore.invoke(request);
		
		//App Uris
		List<String> uris = response.getListAttribute("uris");
		List<String> names = response.getListAttribute("names");
		List<String> descs = response.getListAttribute("descs");
		List<String> downloadUrls = response.getListAttribute("downloadUrls");
		assertTrue("Must have registered app uris", (uris!=null && !uris.isEmpty()));
		assertTrue("Must have registered app names", (names!=null && !names.isEmpty()));
		assertTrue("Must have registered app description", (descs!=null && !descs.isEmpty()));
		assertTrue("Must have registered app downloadUrl", (downloadUrls!=null && !downloadUrls.isEmpty()));
		
		log.info("-------------------------------------------");
		for(String uri: uris)
		{
			log.info("App Uri="+uri);
			assertTrue("Uri must not be empty!!", (uri!=null && uri.trim().length()>0));
		}
		
		for(String name: names)
		{
			log.info("App Name="+name);
			assertTrue("Name Value must match", name.equals("TestApp") || 
			name.equals("TestApp2") || name.equals("TestApp3"));
		}
		
		for(String desc: descs)
		{
			log.info("App Description="+desc);
			assertTrue("Description Value must match", desc.equals("TestApp used for testing Moblet Provisioning") || 
			desc.equals("TestApp2 used for testing Moblet Provisioning") ||
			desc.equals("TestApp3 used for testing Moblet Provisioning")
			);
		}
		
		for(String downloadUrl: downloadUrls)
		{
			log.info("App Download Url="+downloadUrl);
			assertTrue("DowloadUrl Value must match", downloadUrl.equals("/testapp/rimos-4.3.0/testapp.jad") || 
			downloadUrl.equals("/testapp2/rimos-4.3.0/testapp2.jad") ||
			downloadUrl.equals("/android-2.0/test.apk")
			);
			this.assertDownloadArtifacts(downloadUrl);
		}
		log.info("-------------------------------------------");
	}	
	//-------------------------------------------------------------------------------------------------
	private void assertDownloadArtifacts(String downloadUrl) throws Exception
	{	
		if(!downloadUrl.endsWith("apk"))
		{
			Tool.assertBinary(this, this.appStore.getAppBinary(downloadUrl));
			
			byte[] configuration = this.appStore.getAppConfig(downloadUrl);
			log.info(new String(configuration));
			assertNotNull("Jad file must exist", configuration);
		}
		else
		{
			this.assertNotNull("Should not be null",this.appStore.getAppBinary(downloadUrl));
		}
	}		
}
