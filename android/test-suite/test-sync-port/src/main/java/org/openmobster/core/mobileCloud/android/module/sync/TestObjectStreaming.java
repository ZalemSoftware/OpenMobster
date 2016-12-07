/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;

import org.openmobster.core.mobileCloud.android.module.mobileObject.MobileObject;


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class TestObjectStreaming extends AbstractSyncTest 
{		
	/**
	 * 
	 */
	public void runTest()
	{
		try
		{
			this.setUp("add");
			SyncService.getInstance().performTwoWaySync(service, service, false);					
			this.assertRecordPresence("unique-1", "/TestTwoWaySync/add");
			this.assertRecordPresence("unique-2", "/TestTwoWaySync/add");
			this.assertRecordPresence("unique-3", "/TestTwoWaySync/add");
			this.assertRecordPresence("unique-4", "/TestTwoWaySync/add");
			
			SyncService.getInstance().performStreamSync(service, "unique-1", false);
			MobileObject unique1 = this.getRecord("unique-1");
			this.assertNotNull(unique1.getValue("attachment"), "/TestObjectStreaming/AttachmentMustBeDownloaded");
			
			this.tearDown();
						
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.toString());
		}
	}	
}
