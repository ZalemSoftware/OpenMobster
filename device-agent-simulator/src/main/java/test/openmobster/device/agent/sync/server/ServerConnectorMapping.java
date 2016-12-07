/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.device.agent.sync.server;

import org.openmobster.cloud.api.sync.ChannelInfo;
import org.openmobster.cloud.api.sync.MobileBean;

/**
 * @author openmobster@gmail.com
 */
@ChannelInfo(uri="testServerBean.testMapping", 
		       mobileBeanClass="test.openmobster.device.agent.sync.server.ServerRecord")
public class ServerConnectorMapping extends ServerConnector
{
	protected ServerRecordController getServerController()
	{
		return ServerRecordControllerMapping.getInstance();
	}
	
	public String create(MobileBean object)
	{
		String recordId = null;
		ServerRecord serverRecord = (ServerRecord)object;
		
		/**
         * Used to show server side creation of an object id different from the device
         */
		ServerRecord newRecord = new ServerRecord();
		newRecord.setFrom(serverRecord.getFrom());
		newRecord.setTo(serverRecord.getTo());
		newRecord.setMessage(serverRecord.getMessage());
		newRecord.setSubject(serverRecord.getSubject());
		newRecord.setAttachment(serverRecord.getAttachment());
				
						
		recordId = this.getServerController().create(newRecord);
		
		return recordId;
	}
}
