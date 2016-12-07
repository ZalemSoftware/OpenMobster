/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.android.api.d2d;

import org.openmobster.core.mobileCloud.d2d.D2DSession;

import android.app.Activity;

/**
 * A Device-to-Device Activity class. It manages the Device-To-Device session of the App
 * 
 *
 * @author openmobster@gmail.com
 */
public abstract class D2DActivity extends Activity
{
	@Override
	protected void onStart()
	{
		super.onStart();
		D2DSession.getSession().start(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		D2DSession.getSession().stop();
	}
	
	/**
	 * This message is invoked when a new message is received
	 * Its the implementation's job to handle the GUI aspects of
	 * this callback
	 * 
	 * @param message
	 */
	public abstract void callback(D2DMessage message);
}
