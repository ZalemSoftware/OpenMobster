/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.server;

import org.apache.log4j.Logger;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import org.openmobster.core.dataService.Constants;
import org.openmobster.core.dataService.processor.ProcessorException;

import org.openmobster.core.common.errors.ErrorHandler;


/**
 * @author openmobster@gmail.com
 */
public class ServerHandler extends IoHandlerAdapter
{			
	private static Logger log = Logger.getLogger(ServerHandler.class);
			
	private CommandController commandController;
	private ProcessorController processorController;
	
	
	public ServerHandler()
	{
	}
	
	
	public void start()
	{		
	}
	
	
	public void stop()
	{		
	}
				
	public CommandController getCommandController() 
	{
		return commandController;
	}


	public void setCommandController(CommandController commandController) 
	{
		this.commandController = commandController;
	}
		
	public ProcessorController getProcessorController() 
	{
		return processorController;
	}

	public void setProcessorController(ProcessorController processorController) 
	{
		this.processorController = processorController;
	}
	//---------------------------------------------------------------------------------------------------------	
	public void sessionCreated(IoSession session) throws Exception 
	{
		log.debug("------------------------------------");
		log.debug("Session successfully created...");
		log.debug("------------------------------------");		
	}
					
    public void messageSent(IoSession session, Object message) throws Exception
    {
    	//log.debug("---------------------------------");
		//log.debug("Message(Sent)="+message);
		//log.debug("---------------------------------");
    }
        
	public void exceptionCaught(IoSession session, Throwable t) throws Exception 
	{
		log.error(this, t);
		session.close(true);
	}
	//------------------------------------------------------------------------------------------------------------------------
	public void messageReceived(IoSession session, Object message) 
	{
		//log.debug("---------------------------------");
		//log.debug("Message(Received)="+message);
		//log.debug("---------------------------------");																				
		try
		{							
			//Now route this message to the proper Processor
			String payload = (String)session.getAttribute(Constants.payload);
			if(payload == null)
			{				
				return;
			}
			
			ConnectionRequest request = (ConnectionRequest)session.getAttribute(Constants.request);
			
			if(request != null && request.getCommand() != null)
			{
				this.commandController.execute(session, payload, request);								
			}			
			else
			{
				this.processorController.execute(session,payload, request);								
			}															
		}
		catch(ProcessorException pe)
		{
			//log.error(this, pe);
			session.write(Constants.status+"="+500+Constants.endOfStream);									
			ErrorHandler.getInstance().handle(pe);
			session.setAttribute("tx-rollback", Boolean.TRUE);
		}
		catch(Exception e)
		{
			//log.error(this, e);
			session.write(Constants.status+"="+500+Constants.endOfStream);
			ErrorHandler.getInstance().handle(e);				
			session.setAttribute("tx-rollback", Boolean.TRUE);
		}
	}							
}
