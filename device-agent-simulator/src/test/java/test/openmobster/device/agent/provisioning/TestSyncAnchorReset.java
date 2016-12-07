/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

/**
 * 
 */
package test.openmobster.device.agent.provisioning;

import test.openmobster.device.agent.AbstractTestEnv;

/**
 * @author openmobster@gmail
 *
 */
public class TestSyncAnchorReset extends AbstractTestEnv
{
	public void test() throws Exception
	{
		//boot the service
		this.bootService();
		
		//do some device side activity
		this.createNewDeviceObject();
		
		//reset the synchronization anchor
		this.runner.resetSyncAnchor();
		
		//do a two sync...but absence of an anchor should result in a boot sync instead
		this.runner.syncService();
		this.assertBootUpState();
		
		//This to make sure the authentication nonce is in sync after the 
		//forced boot sync from an anchor failure
		this.bootService();
	}
}
