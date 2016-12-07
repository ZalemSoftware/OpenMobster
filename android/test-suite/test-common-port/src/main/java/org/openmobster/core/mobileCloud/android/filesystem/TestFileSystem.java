/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.filesystem;

import java.io.InputStream;
import java.io.OutputStream;

import org.openmobster.core.mobileCloud.android.util.IOUtil;

import org.openmobster.core.mobileCloud.android.testsuite.Test;


/**
 * @author openmobster@gmail.com
 */
public class TestFileSystem extends Test 
{
	@Override
	public void runTest()
	{
		try
		{
			this.testWorkflow();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
	
	private void testWorkflow() throws Exception
	{
		File file = FileSystem.getInstance().openOutputStream();
		System.out.println("File Name: "+file.getName());
		OutputStream os = file.getOutputStream();
		os.write("Hello World".getBytes());
		os.close();
		
		InputStream is = FileSystem.getInstance().openInputStream(file.getName());
		String content = new String(IOUtil.read(is));
		is.close();
		System.out.println("Content: "+content);
		this.assertEquals(content, "Hello World", "/testWorkflow/ContentReadCheckFailed");
		
		FileSystem.getInstance().cleanup(file.getName());
		boolean deleteSuccess = false;
		try
		{
			is = FileSystem.getInstance().openInputStream(file.getName());
		}
		catch(Exception ex)
		{
			deleteSuccess = true;
		}
		this.assertTrue(deleteSuccess, "/testWorkflow/DeleteFileFailed");
	}
}
