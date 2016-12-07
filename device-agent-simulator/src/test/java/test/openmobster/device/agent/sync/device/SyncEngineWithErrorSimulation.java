/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync.device;

import org.openmobster.device.agent.frameworks.mobileObject.MobileObject;
import org.openmobster.device.agent.sync.Session;
import org.openmobster.device.agent.sync.engine.SyncEngine;

/**
 * @author openmobster@gmail.com
 */
public class SyncEngineWithErrorSimulation extends SyncEngine
{

	@Override
	protected void deleteRecord(Session session, MobileObject mobileObject) 
	{
		throw new RuntimeException("Error Simulation!!");
	}

	@Override
	protected void saveRecord(Session session, MobileObject mobileObject) 
	{
		throw new RuntimeException("Error Simulation!!");
	}	
}
