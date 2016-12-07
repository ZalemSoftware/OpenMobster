/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.util;

import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.service.Registry;

import android.content.Context;

/**
 * @author openmobster@gmail
 *
 */
public final class ChannelUtil 
{
	public static boolean isChannelActive(String channel) throws Exception
	{
		Context context = Registry.getActiveInstance().getContext();
		if(Database.getInstance(context).doesTableExist(channel) && 
		  !Database.getInstance(context).isTableEmpty(channel))
		{
			return true;
		}
		return false;
	}
}
