/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.android.test.port.common;

import java.util.List;
import java.util.ArrayList;

import org.openmobster.core.mobileCloud.android.util.GeneralTools;

import org.openmobster.core.mobileCloud.android.testsuite.Test;


/**
 * @author openmobster@gmail.com
 */
public class TestGeneralTools extends Test 
{
	@Override
	public void runTest()
	{
		try
		{
			this.testUUID();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	private void testUUID() throws Exception
	{
		List<String> uuids = new ArrayList<String>();
		for(int i=0; i<20; i++)
		{
			String uuid = GeneralTools.generateUniqueId();
			
			System.out.println("UUID: "+uuid);
			
			this.assertFalse(uuids.contains(uuid), this.getInfo()+"/UUIDUniqueNessFailure");
			uuids.add(uuid);
		}
	}
}
