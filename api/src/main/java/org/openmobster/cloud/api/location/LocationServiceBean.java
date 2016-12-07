/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.cloud.api.location;

/**
 * Represents a service side Location Service Bean component.
 * 
 * This components receives remote coarse grain location-oriented invocations from the devices
 * 
 * It is to be used by Developers to integrate contextual Location data with backend Enterprise data
 * 
 * @author openmobster@gmail.com
 */
public interface LocationServiceBean 
{
	/**
	 * Receives service invocations from the device side
	 * 
	 * @param contextual location information associated with the device in real time
	 * @param request service request
	 * @return service response with appropriate data within the context of the application
	 */
	public Response invoke(LocationContext locationContext, Request request);
}
