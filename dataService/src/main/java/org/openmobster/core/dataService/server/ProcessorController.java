/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.server;

import org.apache.mina.core.session.IoSession;
import org.openmobster.core.dataService.processor.Input;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.dataService.Constants;
import org.openmobster.core.dataService.processor.Processor;

/**
 * @author openmobster@gmail.com
 */
public class ProcessorController 
{		
	public ProcessorController()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}				
	//---------------------------------------------------------------------------------------------------------------------
	public void execute(IoSession session, String payload, ConnectionRequest request) throws Exception
	{
		if(request != null )
		{
			String processor = request.getProcessor();
			this.instantiateProcessor(session, processor);
		}
		else
		{
			this.processMessage(session, payload);
		}
	}	
	
	private void instantiateProcessor(IoSession session, String processorId) 
	{
		//Find/Instantiate the Processor for this data exchange
		Processor processor = (Processor)session.getAttribute(Constants.processor);
		if(processor == null)
		{
			//Find a processor for this data exchange
			processor = (Processor)ServiceManager.locate(processorId);
			session.setAttribute(Constants.processor, processor);
		}
		else
		{
			if(!processor.getId().equals(processorId))
			{
				//Swap for a new processor for this data exchange
				processor = (Processor)ServiceManager.locate(processorId);
				session.setAttribute(Constants.processor, processor);
			}
		}
		
		//Make sure proper processor was instantiated for this data exchange
		processor = (Processor)session.getAttribute(Constants.processor);
		if(processor != null && processor.getId().equals(processorId))
		{
			session.write(Constants.status+"="+200+Constants.endOfStream);
		}
		else
		{
			session.write(Constants.status+"="+404+Constants.endOfStream);
		}
	}
	
	private void processMessage(IoSession session, String payload) throws Exception
	{
		Processor processor = (Processor)session.getAttribute(Constants.processor);
		if(processor != null)
		{												
			Input input = new Input(session, payload);
			String result = processor.process(input);
							
			if(result != null && result.trim().length()!=0)
			{
				result = result.trim();
				int contentLength = result.length();
				session.write("content-length="+contentLength+"\n");
				session.write(result+Constants.endOfStream);
			}
			else
			{
				session.write(Constants.status+"="+200+Constants.endOfStream);
			}
		}
		else
		{
			session.write(Constants.status+"="+400+Constants.endOfStream);
		}
	}
}
