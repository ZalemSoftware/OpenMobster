/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework.resources;

/**
 * @author openmobster@gmail.com
 *
 */
public interface AppResources 
{
	/**
	 * Localizes the given string from the resource bundle if such a String exists in the resource bundle. If no key exists in the bundle then or a bundle is not installed the default value is returned.
	 *  
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String localize(String key, String defaultValue);
	
	/**
	 * Get the image stream associated the specified imageName
	 * 
	 * @param imageName
	 * @return
	 */
	public Object getImage(String imageName);
		
	
	/**
	 * Gets the specified animation object
	 * 
	 * @param id
	 * @return
	 */
	public Object getAnimation(String id);						
}
