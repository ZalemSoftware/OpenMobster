/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.cloud.api.sync;


/**
 * This is used by server side Mobile Beans that have data like big attachments, big music files etc
 * 
 * that are designed to not sent all the data on the first sync
 * 
 * This data from the Mobile Bean is loaded on-demand at a later time when it needs to be used
 * 
 * @author openmobster@gmail.com
 *
 */
public interface MobileBeanStreamable extends MobileBean 
{
	/**
	 * Returns a partially populated Mobile Bean instance. The partially populated Mobile Bean is typically used during the initial synchronization
	 * 
	 * @return a partially populated Mobile Bean instance
	 */
	public MobileBean getPartial();
	
	/**
	 * Returns a fully populated Mobile Bean instance. The fully populated Mobile Bean is used at a later time when the rest of the information related 
	 * to the bean is needed
	 * 
	 * @return a fully populated Mobile Bean instance
	 */
	public MobileBean getFull();
}
