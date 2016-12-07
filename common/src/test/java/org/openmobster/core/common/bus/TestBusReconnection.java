/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common.bus;

import org.apache.log4j.Logger;
import junit.framework.TestCase;

import org.hornetq.core.client.ClientConsumer;
import org.hornetq.core.client.ClientMessage;
import org.hornetq.core.client.ClientProducer;
import org.hornetq.core.client.ClientSession;
import org.hornetq.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.config.TransportConfiguration;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;


//will just use the core api directly
/*import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import org.hornetq.jms.HornetQQueue;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.hornetq.jms.server.JMSServerManager;
import org.hornetq.jms.server.impl.JMSServerManagerImpl;*/

import org.openmobster.core.common.ServiceManager;

/**
 * @author openmobster@gmail.com
 */
public class TestBusReconnection extends TestCase 
{	
	private static Logger log = Logger.getLogger(TestBusReconnection.class);
	
	private ClientSessionFactory sessionFactory;
	private String queueName;
	
	public void setUp() throws Exception
	{
		ServiceManager.bootstrap();
		
		//As we are not using a JNDI environment we instantiate the objects directly         
        this.sessionFactory = new ClientSessionFactoryImpl (new TransportConfiguration(
        InVMConnectorFactory.class.getName()));
        
        // Create a core queue
        ClientSession coreSession = this.sessionFactory.createSession(false, false, false);        
        
        //Create a Queue
        this.queueName = "/test/bus1";        
        coreSession.createQueue(this.queueName, this.queueName, true);
                        
        coreSession.close();
	}
		
	public void tearDown() throws Exception
	{
		ServiceManager.shutdown();
	}
	
	public void testPublishSubscribe() throws Exception
	{
		Thread publishThread = new Thread(new Publisher());
		publishThread.start();
		publishThread.join();
		
		Thread subscriberThread = new Thread(new Subscriber("Thread A"));
		subscriberThread.start();
		subscriberThread.join();		
	}
	
	private class Publisher implements Runnable
	{
		public void run()
		{
			try			
			{				
				int iterations = 4;
				do
				{										
					//Start a session
					ClientSession session = null;
					session = TestBusReconnection.this.sessionFactory.createSession();
					ClientProducer producer = session.createProducer(queueName);
					
			        session.start();		        
			       			        
					this.sendMessage(session, producer, "Hello from iteration : " + iterations);
					
					session.stop();
					session.close();				
				}while(iterations-- > 0);
			}
			catch(Exception e)
			{
				log.error(this, e);
			}				
		}
		
		private void sendMessage(ClientSession session, ClientProducer producer, String msg) throws Exception
		{
			//Create and send a message
	        ClientMessage message = session.createClientMessage(false);
	           
	        final String propName = "myprop";
	           
	        message.putStringProperty(propName, msg);
	        
	        log.info("Sending Message: "+msg);
	           
	        producer.send(message);
		}
	}
	
	private class Subscriber implements Runnable
	{
		private String subscriberId;
		
		private Subscriber(String subscriberId)
		{
			this.subscriberId = subscriberId;
		}
		
		public void run()
		{
			try
			{
				int iterations = 1;
				do
				{						
					this.reconnect();
					Thread.currentThread().sleep(2000);
				}while(iterations-- > 0);
			}
			catch(Exception e)
			{
				log.error(this, e);
				throw new RuntimeException(e);
			}
		}
		
		private void reconnect() throws Exception
		{
			log.info("Reconnecting..............................");
			ClientSession session = null;
			try
			{
				//Start a session
		        session = TestBusReconnection.this.sessionFactory.createSession();
		        ClientConsumer messageConsumer = session.createConsumer(queueName);
		        
		        session.start();
		        int iterations = 4;
				do
				{						
					this.receiveMessage(messageConsumer);
				}while(iterations-- > 0);
				
				session.stop();
				session.close();
			}
			catch(Exception e)
			{
				log.error(this, e);
			}
		}
		
		private void receiveMessage(ClientConsumer messageConsumer) throws Exception
		{			                                  	           	  	           			  
			//Receive the message. 
			ClientMessage messageReceived = messageConsumer.receive(250);
			
			if(messageReceived != null)
			{
				messageReceived.acknowledge();
				final String propName = "myprop";
				log.info("Client("+this.subscriberId+"):" + messageReceived.getProperty(propName));	
			}
			else
			{
				log.info("No Message Found on queue");
			}
		}
	}	
}
