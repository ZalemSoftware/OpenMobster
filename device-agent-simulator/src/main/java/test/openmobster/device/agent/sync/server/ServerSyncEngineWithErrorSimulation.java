/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync.server;

import org.openmobster.core.synchronizer.server.engine.ServerSyncEngineImpl;

/**
 * @author openmobster@gmail.com
 */
public class ServerSyncEngineWithErrorSimulation extends ServerSyncEngineImpl
{

	@Override
	protected void deleteRecord(String pluginId, String recordId) 
	{
		throw new RuntimeException("Error Simulation!!");
	}

	@Override
	protected void saveRecord(String pluginId, String xml) 
	{
		throw new RuntimeException("Error Simulation!!");
	}	
}
