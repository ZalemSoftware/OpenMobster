/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.services.event;

import org.apache.log4j.Logger;

/**
 * @author openmobster@gmail.com
 */
public class MockNetworkEventListener implements NetworkEventListener
{
	private static Logger log = Logger.getLogger(MockNetworkEventListener.class);
	
	public void serverPush(NetworkEvent event) 
	{
		log.info("NetworkEvent---------------------------------------");		
		for(String updatedChannel: event.getUpdatedChannels())
		{
			log.info("Updated Channel: "+updatedChannel);
		}
		log.info("----------------------------------------------------");
	}	
}
