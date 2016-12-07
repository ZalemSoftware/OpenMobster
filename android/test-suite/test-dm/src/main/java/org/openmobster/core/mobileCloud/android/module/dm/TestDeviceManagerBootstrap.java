/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.module.dm;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

import android.os.Build;

/**
 * 
 * @author openmobster@gmail.com
 */
public class TestDeviceManagerBootstrap extends Test
{
	@Override
	public void runTest()
	{
		DeviceManager dm = DeviceManager.getInstance();
		this.assertNotNull(dm, "/dm/boostrap/check");
	}
}
