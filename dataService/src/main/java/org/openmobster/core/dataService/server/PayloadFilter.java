/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.server;

import org.apache.log4j.Logger;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;

import org.openmobster.core.dataService.Constants;

/**
 * @author openmobster@gmail.com
 */
public class PayloadFilter extends IoFilterAdapter
{	
	private static Logger log = Logger.getLogger(PayloadFilter.class);
	
	public PayloadFilter()
	{
		
	}
		
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message)
	{
		//log.debug("RAWSocketMsg---------------------------------------------------------------------------");
		//log.debug(message);
		//log.debug("---------------------------------------------------------------------------------------");
		/*if(this.processPayload(session, (String)message))
		{
			nextFilter.messageReceived(session, message);
		}
		else
		{
			//payload is still being constructed...no need to go into further processing
			return;
		}*/
		String payloadMessage = ((String)message).trim();
		if(payloadMessage.length() == 0)
		{
			//empty payload
			return;
		}
		
		session.setAttribute(Constants.payload, payloadMessage);
		nextFilter.messageReceived(session, message);
		session.removeAttribute(Constants.payload);
	}	
	
		
	private boolean processPayload(IoSession session, String message)
	{		
		StringBuilder payLoadBuilder = (StringBuilder)session.getAttribute(Constants.payloadBuilder);
		if(payLoadBuilder == null)
		{
			payLoadBuilder = new StringBuilder();
			session.setAttribute(Constants.payloadBuilder, payLoadBuilder);
		}
		
		payLoadBuilder.append(message.trim());
		
		if(!payLoadBuilder.toString().trim().endsWith(Constants.eof))
		{
			return false;
		}
		
		//Payload is fully constructed now, for further processing down the server stack
		String payload = payLoadBuilder.toString().trim().replace(Constants.eof, "");
		session.removeAttribute(Constants.payloadBuilder);
		session.setAttribute(Constants.payload, payload);
		
		return true;
	}				
}
