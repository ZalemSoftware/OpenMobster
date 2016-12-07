/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.frameworks.mobileObject;


/**
 * @author openmobster@gmail.com
 */
public interface StorageListener 
{
	/**
	 * Receives notification from the synchronization engine, when a new device side object/record 
	 * is added to the device database. This is used for integration with native on-device applications, 
	 * and returns the recordId of the object/record returned by the native application
	 * 
	 * @param mobileObject
	 * @return
	 */
	public String create(MobileObject mobileObject);
}
