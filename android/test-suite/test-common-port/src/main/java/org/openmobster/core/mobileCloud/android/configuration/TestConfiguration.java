/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.configuration;

import java.util.List;
import java.util.Set;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.storage.Record;
import org.openmobster.core.mobileCloud.android.testsuite.Test;

/**
 * @author openmobster@gmail.com
 */
public class TestConfiguration extends Test 
{
	
	@Override
	public void setUp()
	{
		try
		{
			Context context = (Context)this.getTestSuite().getContext().
			getAttribute("android:context");
			
			Database.getInstance(context).dropTable(Database.provisioning_table);
			Database.getInstance(context).createTable(Database.provisioning_table);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void runTest()
	{
		try
		{
			Context context = (Context)this.getTestSuite().getContext().
			getAttribute("android:context");
			
			this.testConfiguration(context);
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}

	private void testConfiguration(Context context) throws Exception
	{	
		Configuration configuration = Configuration.getInstance(context);
		
		System.out.println("Upon Load-----------------------------------------------------");
		System.out.println("DeviceId: "+configuration.getDeviceId());
		System.out.println("Port: "+configuration.decidePort());
		System.out.println("Auth: "+configuration.getAuthenticationHash());
		
		configuration.setDeviceId("IMEI:1234");
		configuration.activateSSL();
		configuration.setSecureServerPort("1500");
		configuration.setPlainServerPort("1502");
		configuration.setAuthenticationHash("hash");
		configuration.setAuthenticationNonce("nonce");
		configuration.addMyChannel("twitter");
		configuration.addMyChannel("facebook");
		configuration.addMyChannel("email");
		
		System.out.println("Before save-----------------------------------------------------");
		System.out.println("DeviceId: "+configuration.getDeviceId());
		System.out.println("Port: "+configuration.decidePort());
		System.out.println("Auth: "+configuration.getAuthenticationHash());
		List<String> mychannels = configuration.getMyChannels();
		for(String channel: mychannels)
		{
			System.out.println("MyChannel: "+channel);
		}
		System.out.println("-----------------------------------------------------");
		
		configuration.save(context);
		
		//Stop the service and cleanup
		configuration.stop();
		
		//Reload the service and should load state from the database
		configuration = Configuration.getInstance(context);
		
		System.out.println("After save-----------------------------------------------------");
		System.out.println("DeviceId: "+configuration.getDeviceId());
		System.out.println("Port: "+configuration.decidePort());
		System.out.println("Auth: "+configuration.getAuthenticationHash());
		mychannels = configuration.getMyChannels();
		for(String channel: mychannels)
		{
			System.out.println("MyChannel: "+channel);
		}
		System.out.println("-----------------------------------------------------");
		
		//Asserts
		assertEquals(configuration.getDeviceId(),"IMEI:1234",this.getInfo()+"/deviceidFailed");
		assertEquals(configuration.decidePort(),"1500",this.getInfo()+"/portFailed");
		assertEquals(configuration.getAuthenticationHash(),"nonce",this.getInfo()+"/authHashFailed");
		assertTrue(mychannels.contains("facebook") && mychannels.contains("twitter") && mychannels.contains("email"),
		this.getInfo()+"/mychannelsFailed");
	}
}
