/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.spi.ui.framework;

import org.openmobster.core.mobileCloud.api.ui.framework.navigation.Screen;

/**
 * @author openmobster@gmail.com
 *
 */
public interface NavigationContextSPI 
{
	/**
	 * Navigate to the screen specified
	 * 
	 * @param screen
	 */
	public void navigate(Screen screen);
	
	/**
	 * Navigate back to the previous screen
	 * 
	 * @param screen
	 */
	public void back(Screen screen);
	
	/**
	 * Navigate to the home screen of the App
	 * 
	 * @param screen
	 */
	public void home(Screen screen);
	
	/**
	 * Refreshes the currently displayed screen
	 * 
	 */
	public void refresh();
}