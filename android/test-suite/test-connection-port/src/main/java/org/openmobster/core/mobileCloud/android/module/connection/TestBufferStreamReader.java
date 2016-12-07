/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

/**
 * 
 */
package org.openmobster.core.mobileCloud.android.module.connection;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

/**
 * @author openmobster
 *
 */
public class TestBufferStreamReader extends Test
{
	public void runTest()
	{
		try
		{
			this.testHelloWorld();
			this.testComplex1();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
	
	private void testHelloWorld() throws Exception
	{
		String dataPacket = "Hello World\r\n";
		
		BufferStreamReader reader = new BufferStreamReader(dataPacket.getBytes());
		
		String secondPacket = "Bye World\r\n";
		reader.fillBuffer(secondPacket.getBytes());
		
		System.out.println(dataPacket.length()+","+secondPacket.length());
		
		String line = reader.readLine();
		System.out.println(line+" length="+line.length());
		this.assertEquals(line, "Hello World\r\n", "/testHelloWorld/HelloWorld");
		
		line = reader.readLine();
		System.out.println(line+" length="+line.length());
		this.assertEquals(line, "Bye World\r\n", "/testHelloWorld/ByeWorld");
		
		reader.close();
	}
	
	private void testComplex1()
	{
		String dataPacket = "Hello World\n";
		String secondPacket = "Bye World ";
		String thirdPacket = "Hi World\r\n";
		
		BufferStreamReader reader = new BufferStreamReader(dataPacket.getBytes());
		
		reader.fillBuffer(secondPacket.getBytes());
		
		String line = reader.readLine();
		System.out.println(line);
		this.assertEquals(line, "Hello World\n", "/testComplex1/HelloWorld");
		
		
		line = reader.readLine();
		if(line == null)
		{
			System.out.println("Line: null");
		}
		this.assertNull(line, "/testComplex1/NullCheck");
		
		reader.fillBuffer(thirdPacket.getBytes());
		line = reader.readLine();
		System.out.println(line);
		this.assertEquals(line, "Bye World Hi World\r\n", "/testComplex1/RestOfThePackets");
		
		reader.close();
	}
}
