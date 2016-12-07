/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.services.channel;

/**
 * Represents the types of Notifications supported by the device side container
 * 
 * @author openmobster@gmail.com
 */
public enum ChannelUpdateType 
{
	ADD, //New MobileBean appeared on the Channel
	REPLACE, //An existing MobileBean was updated on the Channel
	DELETE, //An existing MobileBean was deleted from the Channel	
}
