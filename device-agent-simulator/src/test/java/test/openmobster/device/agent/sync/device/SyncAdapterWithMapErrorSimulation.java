/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync.device;

import java.util.Vector;

import org.apache.log4j.Logger;

import org.openmobster.device.agent.sync.SyncException;
import org.openmobster.device.agent.sync.Session;
import org.openmobster.device.agent.sync.Status;
import org.openmobster.device.agent.sync.SyncAdapter;
import org.openmobster.device.agent.sync.SyncMessage;
import org.openmobster.device.agent.sync.SyncXMLTags;

/**
 * @author openmobster@gmail.com
 */
public class SyncAdapterWithMapErrorSimulation extends SyncAdapter
{
	private static Logger log = Logger.getLogger(SyncAdapterWithMapErrorSimulation.class);
	
	protected void end(Session session)
	{
		SyncMessage currentMessage = session.getCurrentMessage();
		
		Vector statuses = currentMessage.getStatus();
		for(int i=0, size=statuses.size(); i<size; i++)
		{
			Status status = (Status)statuses.elementAt(i);
			
			if(status.getCmd().equals(SyncXMLTags.Map) && status.getData().equals(SyncAdapter.SUCCESS))
			{
				//cleanup the recordmap
				session.setRecordMap(null);
			}
		}
		
		if(!session.getRecordMap().isEmpty())
		{
			/**
			 * Cache the RecordMap on the client
			 */
			//Something happened with Map Update on the server
			//Need to persist this Map and should be sent back in the next
			//synchronization with the server
			try
			{
				//UnitTest simulation code for TestMapSupport:testDeferMapUpdateToNextSyncClientPersistFailure
				throw new SyncException(new RuntimeException("Mapping Error Simulation!!"));
				
				//this.syncEngine.saveRecordMap(session.getDataSource(false), session.getDataTarget(false),
				//session.getRecordMap());
			}
			catch(Exception e)
			{
				//Handle this properly as part of the Map Support
			
				//When trying to persist the client map, to pick it up
				//during next sync.				
				//An error occured in doing that	
				
				//This extremely bad...if the Map is not communicated to the server
				//and it also cannot be cached on the device for next sync
				//Only option is to not allow anymore syncs and all data should
				//be deleted from the client and a SlowSync needs to be performed
				try{
					this.resetSyncState(session);
				}catch(Exception ex){}
			}			
		}
	}
}
