/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common.bus;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;

import org.hornetq.core.client.ClientMessage;
import org.hornetq.core.client.ClientProducer;
import org.hornetq.core.client.ClientSession;
import org.hornetq.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.config.TransportConfiguration;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.hornetq.core.exception.HornetQException; 
import org.hornetq.utils.SimpleString;

import org.openmobster.core.common.errors.ErrorHandler;
import org.openmobster.core.common.errors.SystemException;
import org.openmobster.core.common.XMLUtilities;

/**
 * @author openmobster@gmail.com
 */
public final class Bus 
{
	private static Logger log = Logger.getLogger(Bus.class);
	
	private static Map<String, Bus> activeBuses;
	private static ClientSessionFactory sessionFactory;
	static
	{
		activeBuses = new HashMap<String, Bus>();
		
		Thread t = new Thread(BusConsumer.getInstance());
		t.start();
	}
	
	private String uri;
	private List<BusListener> busListeners;
	
	private Bus(String uri)
	{
		if(uri == null || uri.trim().length()==0)
		{
			throw new IllegalArgumentException("Bus Uri must be specified!!");
		}
		
		this.uri = uri;
		this.busListeners = new ArrayList<BusListener>();
	}				
	//-----------------------------------------------------------------------------------------------------------
	private void start()
	{
		ClientSession coreSession = null;
		try
		{
			if(Bus.activeBuses.containsKey(this.uri))
			{
				//Bus infrastructure for this already exists
				//no need to create redundant one
				return;
			}
			
			
			synchronized(Bus.class)
			{
				if(sessionFactory == null)
				{
					sessionFactory = new ClientSessionFactoryImpl (new TransportConfiguration(
					InVMConnectorFactory.class.getName()));
					
					sessionFactory.setMinLargeMessageSize(1000000000); //a gig
				}
			}
			
			//Start a hornetq associated with this Bus instance
			// Create a core queue
	        coreSession = sessionFactory.createSession(false, false, false);        
	        
	        //Create a Queue 
	        if(!coreSession.queueQuery(new SimpleString(this.uri)).isExists())
	        {
	        	coreSession.createQueue(this.uri, this.uri, true); //a durable queue
	        }
	                        
	        coreSession.close();
	        
	        Bus.activeBuses.put(this.uri, this);
		}
		catch(HornetQException hqe)
		{
			log.error(this, hqe);
			ErrorHandler.getInstance().handle(hqe);
			throw new SystemException(hqe.getMessage(),hqe);
		}
		finally
		{
			if(coreSession != null && !coreSession.isClosed())
			{
				try{coreSession.close();}
				catch(HornetQException hqe)
				{
					ErrorHandler.getInstance().handle(hqe);
					throw new SystemException(hqe.getMessage(),hqe);
				}
			}
		}
	}	
	
	private void stop()
	{
		try
		{
			Bus.activeBuses.remove(uri);
		}
		catch(Exception e)
		{
			ErrorHandler.getInstance().handle(e);
		}
	}
	
	String getUri()
	{
		return this.uri;
	}
	
	List<BusListener> getBusListeners()
	{
		return this.busListeners;
	}
	//-----------------------------------------------------------------------------------------------------------
	private void addBusListener(BusListener listener)
	{
		if(listener == null)
		{
			throw new IllegalArgumentException("BusListener must be specified!!");
		}
		
		this.busListeners.add(listener);
	}
	
	private void removeBusListener(BusListener listener)
	{				
		this.busListeners.remove(listener);
	}
	
	private void sendMessageOnQueue(BusMessage busMessage)
	{
		ClientSession session = null;
		ClientProducer producer = null;
		try
		{
			session = sessionFactory.createSession();
			producer = session.createProducer(uri);
			
			session.start();		        
	        	        
	        String busMessageXml = XMLUtilities.marshal(busMessage);
	        
	        ClientMessage message = session.createClientMessage(true); //makes it a durable message
	        message.putStringProperty("message", busMessageXml);
	        
	        producer.send(message);
	        
	        producer.close();
	        session.stop();
	        session.close();
		}
		catch(HornetQException hqe)
		{
			ErrorHandler.getInstance().handle(hqe);
			throw new SystemException(hqe.getMessage(),hqe);
		}
		finally
		{
			if(producer != null && !producer.isClosed())
			{
				try{producer.close();}
				catch(HornetQException hqe)
				{
					ErrorHandler.getInstance().handle(hqe);
					throw new SystemException(hqe.getMessage(),hqe);
				}
			}
			
			if(session != null && !session.isClosed())
			{
				try{session.stop();session.close();}
				catch(HornetQException hqe)
				{
					ErrorHandler.getInstance().handle(hqe);
					throw new SystemException(hqe.getMessage(),hqe);
				}
			}
		}
	}
	//-----------------------------------------------------------------------------------------------------------
	public static void startBus(String uri)
	{
		Bus bus = new Bus(uri);
		bus.start();
	}
	
	public static void stopBus(String uri)
	{
		Bus bus = Bus.activeBuses.get(uri);
		bus.stop();
	}
	
	public static void restartBus(String uri)
	{
		//does nothing....just here so that the public contract is not broken
		//deprecated
	}
	
	public static void addBusListener(String uri, BusListener listener)
	{
		Bus bus = Bus.activeBuses.get(uri);
		
		if(bus == null)
		{
			throw new IllegalStateException(uri+" is not active!!");
		}
		
		if(listener == null)
		{
			throw new IllegalArgumentException("BusListener must be specified!!");
		}
		
		bus.addBusListener(listener);
	}
	
	public static void removeBusListener(String uri, BusListener listener)
	{
		Bus bus = Bus.activeBuses.get(uri);
		
		if(bus != null)
		{
			bus.removeBusListener(listener);
		}
	}
	
	public static void sendMessage(BusMessage message)
	{
		if(message == null)
		{
			return;
		}
		
		if(message.getSenderUri()==null || message.getSenderUri().trim().length()==0)
		{
			throw new IllegalStateException("Sender URI is mandatory!!");
		}
		
		if(message.getBusUri()==null || message.getBusUri().trim().length()==0)
		{
			throw new IllegalStateException("Bus URI is mandatory!!");
		}
		
		Bus bus = Bus.activeBuses.get(message.getBusUri());
				
		//Send the message on a queue
		bus.sendMessageOnQueue(message);
	}
	
	static Map<String,Bus> getActiveBuses()
	{
		return Collections.unmodifiableMap(Bus.activeBuses);
	}
	
	static ClientSessionFactory getSessionFactory()
	{
		return Bus.sessionFactory;
	}
	
	public static void dumpUnprocessedQueueCount()
	{
		ClientSession coreSession = null;
	    int count = 0;
	    try 
	    {
	        coreSession = sessionFactory.createSession(false, false, false);
	        
	        if(!Bus.activeBuses.isEmpty())
	        {
	        	Set<String> uris = Bus.activeBuses.keySet();
	        	for(String uri:uris)
	        	{
	        		count += coreSession.queueQuery(new SimpleString(uri)).getMessageCount();
	        	}
	        }
	        
	        StringBuilder buffer = new StringBuilder(); 
	        buffer.append("*****************************************\n");
	        buffer.append("# of unprocessed messages: "+count+"\n");
	        buffer.append("*****************************************\n");
	        
	        String dump = buffer.toString();
	        File dumpFile = new File("queue-dump.xml");
			dumpFile.delete();
			FileOutputStream dumpStream = new FileOutputStream("queue-dump.xml",true);
			dumpStream.write(dump.getBytes());
			dumpStream.flush();
			
			dumpStream.close();
	    } 
	    catch (Throwable t) 
	    {
	        t.printStackTrace();
	        throw new RuntimeException(t);
	    } 
	    finally 
	    {
	        if (coreSession!= null )
	        {
	            try 
	            {
	                coreSession.close();
	            } 
	            catch (HornetQException e) 
	            {
	                e.printStackTrace();
	            }
	        }
	    }
	}
}
