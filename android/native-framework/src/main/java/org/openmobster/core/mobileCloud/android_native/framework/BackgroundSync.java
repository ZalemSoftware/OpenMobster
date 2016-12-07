/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android_native.framework;

import java.util.TimerTask;

import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;

/**
 *
 * @author openmobster@gmail.com
 */
public final class BackgroundSync extends TimerTask
{
	public void run()
	{
		try
		{
			AppStartupSequence.getInstance().execute();
		}
		catch(Throwable t)
		{
			SystemException syse = new SystemException(this.getClass().getName(),"run",new Object[]{
				"Exception: "+t.toString(),
				"Message: "+t.getMessage()
			});
			ErrorHandler.getInstance().handle(syse);
		}
		finally
		{
			//makes sure this task does not execute anymore
			this.cancel();
		}
	}
}
