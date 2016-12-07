/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.d2d;

import org.openmobster.android.api.d2d.D2DActivity;
import org.openmobster.android.api.d2d.D2DMessage;

/**
 *
 * @author openmobster@gmail.com
 */
public final class D2DSession
{
	private static D2DSession singleton;
	
	private D2DActivity activity;
	private D2DMessage latest;
	
	private D2DSession()
	{
		
	}
	
	public static D2DSession getSession()
	{
		if(D2DSession.singleton == null)
		{
			synchronized(D2DSession.class)
			{
				if(D2DSession.singleton == null)
				{
					D2DSession.singleton = new D2DSession();
				}
			}
		}
		return D2DSession.singleton;
	}
	
	public void start(D2DActivity activity)
	{
		this.activity = activity;
	}
	
	public void stop()
	{
		this.activity = null;
		this.latest = null;
	}
	
	public void callback(D2DMessage message)
	{
		if(this.isActive())
		{
			this.latest = message;
			this.activity.callback(this.latest);
		}
	}
	
	public boolean isActive()
	{
		return this.activity != null;
	}
	
	public D2DMessage getLatestMessage()
	{
		return this.latest;
	}
}
