/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.moblet;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author openmobster@gmail.com
 */
public class Tool 
{
	private static Logger log = Logger.getLogger(Tool.class);
	
	public static void assertBinary(TestCase testCase, InputStream is) throws Exception
	{		
		ZipInputStream zis = null;
		try
		{			
			zis = new ZipInputStream(is);
			
			log.info("---------------------------------------------------------");
			ZipEntry entry = null;
			boolean entryFound = false;
			while((entry=zis.getNextEntry())!=null)
			{				
				entryFound = true;
				log.info("Entry ="+entry.getName());
				log.info("Comment ="+entry.getComment());
				log.info("Size ="+entry.getSize());
				log.info("Method ="+entry.getMethod());
				log.info("---------------------------------------------------------");
			}
			
			testCase.assertTrue("Binary was invalid!!!", entryFound);
									
			log.info("---------------------------------------------------------");
		}
		finally
		{
			if(is != null)
			{
				is.close();
			}
			
			if(zis != null)
			{
				zis.close();
			}
		}
	}		
}
