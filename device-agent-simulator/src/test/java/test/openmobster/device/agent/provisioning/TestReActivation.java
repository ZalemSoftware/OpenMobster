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
import org.openmobster.device.agent.configuration.Configuration;

/**
 * @author openmobster@gmail
 *
 */
public class TestReActivation extends AbstractTestEnv
{
	public void test() throws Exception
	{
		Configuration.getInstance().cleanup();
		
		this.runner.activateDevice();
	}
}
