/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.services.event;

import java.util.List;

import org.apache.log4j.Logger;

import org.openmobster.core.services.channel.ChannelBeanMetaData;

/**
 * @author openmobster@gmail.com
 */
public class MockChannelEventListener implements ChannelEventListener
{
	private static Logger log = Logger.getLogger(MockChannelEventListener.class);
	
	public void channelUpdated(ChannelEvent event) 
	{
		List<ChannelBeanMetaData> updateInfo = (List<ChannelBeanMetaData>)event.getAttribute(ChannelEvent.metadata);
		
		for(ChannelBeanMetaData cour: updateInfo)
		{
			log.info("ChannelEvent---------------------------------------------");
			
			log.info("Channel: "+cour.getChannel());
			log.info("BeanId: "+cour.getBeanId());
			log.info("UpdateType: "+cour.getUpdateType());
			
			log.info("---------------------------------------------------------");
		}
	}	
}
