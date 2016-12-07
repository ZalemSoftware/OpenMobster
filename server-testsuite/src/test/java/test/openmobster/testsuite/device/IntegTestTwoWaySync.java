/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.testsuite.device;

import org.apache.log4j.Logger;
import org.openmobster.device.agent.sync.SyncService;

/**
 * @author openmobster@gmail.com
 */
public class IntegTestTwoWaySync extends AbstractSync 
{
	private static Logger log = Logger.getLogger(IntegTestTwoWaySync.class);
				
	public void test() throws Exception
	{
		this.syncService.startSync(SyncService.TWO_WAY, this.deviceId, 
		this.serverId, this.service, this.service);		
	}		
}
