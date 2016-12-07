/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.push.notification;

/**
 * Represents the types of Notifications supported by the device side container
 * 
 * @author openmobster@gmail.com
 */
public enum NotificationType 
{
	SYNC, //Synchronization related
	DM, //Device Management related	
	RPC,//RPC Invocation from Server to Device Component
	PUSH, //originated from a user
	D2D //device to device messaging
}
