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
public class RequestConstructionFilter extends IoFilterAdapter
{	
	private static Logger log = Logger.getLogger(RequestConstructionFilter.class);
	
	public RequestConstructionFilter()
	{
		
	}
		
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message)
	{
		String payload = (String)session.getAttribute(Constants.payload);
		
		if(payload != null)
		{
			if(payload.startsWith("<request>") && payload.endsWith("</request>"))
			{
				//Construct a ConnectionRequest
				ConnectionRequest request = ConnectionRequest.getInstance(payload);
				session.setAttribute(Constants.request, request);
			}
			else
			{
				//cleanup request if possible
				ConnectionRequest request = (ConnectionRequest)session.getAttribute(Constants.request);
				if(request != null && request.isAnonymous())
				{
					session.setAttribute("force-auth", "true");
				}
				session.removeAttribute(Constants.request);
			}
			nextFilter.messageReceived(session, message);
		}
		else
		{
			return;
		}
	}		
}
