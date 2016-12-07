/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

import android.graphics.Bitmap;

import org.openmobster.core.mobileCloud.api.ui.framework.SystemLocaleKeys;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;



/**
 * @author openmobster@gmail.com
 */
public class TestNativeAppResources extends Test 
{
	@Override
	public void runTest()
	{
		try
		{
			this.testSystemResources();			
			this.testAppResources();
			
			this.testSystemImages();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}

	private void testSystemResources() throws Exception
	{	
		AppResources appRes = Services.getInstance().getResources();
		
		String ok = appRes.localize(SystemLocaleKeys.ok, "defaultOk");
		String cancel = appRes.localize(SystemLocaleKeys.cancel, "defaultCancel");
		
		System.out.println("System Localized ------------------------------------");
		System.out.println("ok: "+ok);
		System.out.println("cancel: "+cancel);
		System.out.println("-----------------------------------------------------");
		
		this.assertEquals(ok, "OK", this.getInfo()+"/testSystemResources/OKCheckFailed");
		this.assertEquals(cancel, "Cancel", this.getInfo()+"/testSystemResources/CANCELCheckFailed");
	}
	
	private void testAppResources() throws Exception
	{	
		AppResources appRes = Services.getInstance().getResources();
		
		String test1 = appRes.localize("test1", "default");
		
		System.out.println("App Localized ------------------------------------");
		System.out.println("test1: "+test1);
		System.out.println("-----------------------------------------------------");
		
		this.assertEquals(test1, "Android i18n test1(en_GB)", this.getInfo()+"/testAppResources/test1CheckFailed");
	}
	
	private void testSystemImages() throws Exception
	{
		AppResources appRes = Services.getInstance().getResources();
		
		Bitmap loading = (Bitmap)appRes.getImage("/system/images/loading.gif");
		Bitmap push = (Bitmap)appRes.getImage("/system/images/push.png");
		
		this.assertNotNull(loading, this.getInfo()+"/testSystemImages/LoadingFailed");
		this.assertNotNull(push, this.getInfo()+"/testSystemImages/PushFailed");
		
		System.out.println("System Image Localized ------------------------------------");
		System.out.println("loading width: "+loading.getWidth());
		System.out.println("loading height: "+loading.getHeight());
		System.out.println("push width: "+push.getWidth());
		System.out.println("push height: "+push.getHeight());
		System.out.println("-----------------------------------------------------");
	}
}
