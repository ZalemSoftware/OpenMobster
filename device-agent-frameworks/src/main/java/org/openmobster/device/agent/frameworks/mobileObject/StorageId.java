/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.frameworks.mobileObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which indicates the StorageId associated with a registered MobileObjectListener. The proper
 * MobileObjectListener receives MobileObject Storage notifications based on this information
 * 
 * @author openmobster@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StorageId 
{
	/**
	 * A unique identifier that allows the mobile system to route data object traffic to 
	 * the proper storage listener
	 * 
	 * @return a unique storage id
	 */
	String id();
}
