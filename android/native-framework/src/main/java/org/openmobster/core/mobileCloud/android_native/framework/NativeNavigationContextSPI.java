/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework;

import android.app.Activity;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;
import org.openmobster.core.mobileCloud.spi.ui.framework.NavigationContextSPI;

/**
 * @author openmobster@gmail.com
 *
 */
final class NativeNavigationContextSPI implements NavigationContextSPI 
{
	NativeNavigationContextSPI()
	{		
	}
	//-------------------------------------------------------------------------------------------------------------------------------------
	public void back(Screen screen) 
	{
		this.display(screen);
	}

	public void home(Screen screen) 
	{
		this.display(screen);
	}

	public void navigate(Screen screen) 
	{
		this.display(screen);
	}
	
	public void refresh()
	{
		final Activity currentActivity = Services.getInstance().getCurrentActivity();
		
		currentActivity.getWindow().getDecorView().invalidate();
	}
	//--------------------------------------------------------------------------------------------------------------------------------------
	private void display(Screen screen)
	{
		Integer screenId = (Integer)screen.getContentPane();
		
		if(screenId != null)
		{
			final Activity currentActivity = Services.getInstance().getCurrentActivity();	
			currentActivity.setContentView(screenId);						
		}
		screen.postRender();
	}
}
