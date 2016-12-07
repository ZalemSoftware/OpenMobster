/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.storage;

import android.content.Context;
import org.openmobster.core.mobileCloud.android.testsuite.Test;

/**
 * @author openmobster@gmail.com
 */
public class TestConcurrency extends Test 
{
	@Override
	public void runTest()
	{
		try
		{
			Context context = (Context)this.getTestSuite().getContext().
			getAttribute("android:context");
			
			//TODO: add concurrency tests
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
}
