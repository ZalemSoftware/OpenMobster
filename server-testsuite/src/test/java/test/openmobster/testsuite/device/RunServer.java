/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.testsuite.device;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;


import junit.framework.TestCase;

/**
 * @author openmobster@gmail.com
 */
public class RunServer extends TestCase 
{
	private static Logger log = Logger.getLogger(RunServer.class);
	
	public void test() throws Exception
	{
		log.info("RunServer starting..............");		
		this.blockTest("Press [Enter] to finish the Server");				
	}
	
	
	private void blockTest(String message) throws Exception
	{
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		do
		{
			log.info(message);
		}while(!bf.readLine().equals(""));		
	}
}
