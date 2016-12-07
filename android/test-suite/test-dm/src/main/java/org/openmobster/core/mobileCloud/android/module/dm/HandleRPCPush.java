/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.dm;

import org.openmobster.core.mobileCloud.api.ui.framework.push.PushCommand;
import org.openmobster.core.mobileCloud.api.ui.framework.push.PushCommandContext;



/**
 * 
 * 
 * @author openmobster@gmail.com
 */
public final class HandleRPCPush implements PushCommand
{
	
	public void handlePush(PushCommandContext commandContext)
	{
		System.out.println("Handle RPC PushCommand succesfull (DM)..........");
		System.out.println("ABC: "+commandContext.getAttribute("abc"));
		System.out.println("XYZ: "+commandContext.getAttribute("xyz"));
	}	
}
