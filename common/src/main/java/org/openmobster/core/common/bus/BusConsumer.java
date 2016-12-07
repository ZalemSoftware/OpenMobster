/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.common.bus;

import java.util.Map;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import org.hornetq.core.client.ClientConsumer;
import org.hornetq.core.client.ClientMessage;
import org.hornetq.core.client.ClientSessionFactory;
import org.hornetq.core.exception.HornetQException;
import org.hornetq.utils.SimpleString;
import org.hornetq.core.client.ClientSession;

import org.openmobster.core.common.XMLUtilities;
import org.openmobster.core.common.errors.ErrorHandler;
import org.openmobster.core.common.errors.SystemException;
import org.openmobster.core.common.transaction.TransactionHelper;

/**
 *
 * @author openmobster@gmail.com
 */
public final class BusConsumer implements Runnable
{
	private static Logger log = Logger.getLogger(BusConsumer.class);
	
	private static BusConsumer singleton;
	
	boolean exit = false;
	
	private BusConsumer()
	{
		
	}
	
	public static BusConsumer getInstance()
	{
		if(BusConsumer.singleton == null)
		{
			synchronized(BusConsumer.class)
			{
				if(BusConsumer.singleton == null)
				{
					BusConsumer.singleton = new BusConsumer();
				}
			}
		}
		return BusConsumer.singleton;
	}
	
	public void run()
	{
		do
		{
			try
			{
				Map<String,Bus> activeBuses = Bus.getActiveBuses();
				if(activeBuses == null)
				{
					continue;
				}
				
				Collection<Bus> buses = activeBuses.values();
				for(Bus bus:buses)
				{
					try
					{
						this.consume(bus);
					}
					catch(Throwable t)
					{
						//just eat this one....will try again..for this particular Bus
					}
				}
			}
			catch(Throwable t)
			{
				//something went wrong....but no need to abort the thread
			}
		}while(!exit);
	}
	
	public void stop()
	{
		this.exit = true;
	}
	
	private void consume(Bus bus)
	{
		ClientSessionFactory sessionFactory = Bus.getSessionFactory();
		ClientSession session = null;
		ClientConsumer messageConsumer = null;
		String uri = bus.getUri();
		try
		{				
			session = sessionFactory.createSession();
			session.start();
			messageConsumer = session.createConsumer(uri);
			
			ClientMessage message = messageConsumer.receive(1000);
	        if(message != null)
	        {
	        	boolean isStartedHere = TransactionHelper.startTx();
	        	try
	        	{
		        	SimpleString msg = (SimpleString)message.getProperty("message");
		        	
		        	BusMessage busMessage = (BusMessage)XMLUtilities.unmarshal(msg.toString());
		        	busMessage.setAttribute("hornetq-message", message);
		        	
		        	this.sendBusListenerEvent(bus,busMessage);
		        	
		        	if(isStartedHere)
		        	{
		        		TransactionHelper.commitTx();
		        	}
	        	}
	        	catch(Exception e)
	        	{
	        		e.printStackTrace();
	        		if(isStartedHere)
	        		{
	        			TransactionHelper.rollbackTx();
	        		}
	        	}
	        }
		}
		catch(HornetQException hqe)
		{
			ErrorHandler.getInstance().handle(hqe);
			throw new SystemException(hqe.getMessage(),hqe);
		}
		finally
		{
			if(messageConsumer != null && !messageConsumer.isClosed())
			{
				try
				{
					messageConsumer.close();
				}
				catch(HornetQException hqe)
				{
					ErrorHandler.getInstance().handle(hqe);
					throw new SystemException(hqe.getMessage(),hqe);
				}
			}
			
			if(session != null && !session.isClosed())
			{
				try
				{
					session.stop();
					session.close();
				}
				catch(HornetQException hqe)
				{
					ErrorHandler.getInstance().handle(hqe);
					throw new SystemException(hqe.getMessage(),hqe);
				}
			}
		}
	}
	
	private void sendBusListenerEvent(Bus bus,BusMessage busMessage)
	{
		List<BusListener> busListeners = bus.getBusListeners();
		if(busListeners == null)
		{
			return;
		}
		for(BusListener busListener: busListeners)
		{
			try
			{
				busListener.messageIncoming(busMessage);
			}
			catch(Exception e)
			{
				//so that if an error occurs on one listener, others don't suffer
				//listeners must be isolated of each other
				try{ErrorHandler.getInstance().handle(e);}catch(Exception ex){}
			}
		}
	}
}
