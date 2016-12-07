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
public class TestBus extends TestCase 
{	
	private static Logger log = Logger.getLogger(TestBus.class);
	
	private ClientSessionFactory sessionFactory;
	private String queueName;
	
	public void setUp() throws Exception
	{
		ServiceManager.bootstrap();
		
		//As we are not using a JNDI environment we instantiate the objects directly         
        this.sessionFactory = new ClientSessionFactoryImpl (new TransportConfiguration(
        InVMConnectorFactory.class.getName()));
        this.sessionFactory.setMinLargeMessageSize(1000000000); //a gig
        
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
	
		
	public void testSimpleCoreScenario() throws Exception
	{
		final String propName = "myprop";
		final String msg;
		
		StringBuilder buffer = new StringBuilder();
		for(int i=0;i<1024; i++)
		{
			for(int j=0; j<1000; j++)
			{
				buffer.append("a");
			}
		}
		msg = buffer.toString();
		
		//Publish a message				
        ClientSession session = null;
        try
        {
           //Create the session, and producer
           session = this.sessionFactory.createSession();
                                  
           ClientProducer producer = session.createProducer(queueName);
  
           //Create and send a message
           ClientMessage message = session.createClientMessage(false);                      
           
           message.putStringProperty(propName, msg);
                                 
           producer.send(message);           
        }
        finally
        {
           if (session != null)
           {
              session.close();
           }           
        }
        
        //Read a Message
        session = null;
        try
        {
           //Create the consumer session
           session = this.sessionFactory.createSession();
           session.start();
                                                       
           //Create the message consumer
           ClientConsumer messageConsumer = session.createConsumer(queueName);           
  
           //Receive the message. 
           ClientMessage messageReceived = messageConsumer.receive(1000);
           
           String receivedMsg = messageReceived.getProperty(propName).toString();
           log.info("-------------------------------------------------------------");
           log.info("Received TextMessage:" + receivedMsg);
           log.info("-------------------------------------------------------------");
           assertEquals("Message Did not match", receivedMsg, msg);
        }
        finally
        {
           if (session != null)
           {
              session.close();
           }           
        }
	}		
	//------------------------------------------------------------------------------------------------------------
	public void testPublishSubscribe() throws Exception
	{
		Thread subscriberThread = new Thread(new Subscriber("Thread A"));
		subscriberThread.start();
		
		
		Thread publishThread = new Thread(new Publisher());
		publishThread.start();
		
		
		subscriberThread.join();		
	}
	
	public void testFinishPublishFirst() throws Exception
	{
		Thread publishThread = new Thread(new Publisher());
		publishThread.start();
		publishThread.join();
		
		Thread subscriberThread = new Thread(new Subscriber("Thread A"));
		subscriberThread.start();
		subscriberThread.join();		
	}
	//------------------------------------------------------------------------------------------------------------
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
					try
					{
				        session = TestBus.this.sessionFactory.createSession();
				        session.start();		        
				        ClientProducer producer = session.createProducer(queueName);
				        
						this.sendMessage(session, producer, "Hello from iteration : " + iterations);
						
						session.close();
					}
					finally
					{
						if(session != null)
						{
							session.close();
						}
					}
					
					Thread.currentThread().sleep(500);					
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
			ClientSession session = null;
			try
			{
				//Start a session
		        session = TestBus.this.sessionFactory.createSession();
		        session.start();
		        
		        ClientConsumer messageConsumer = session.createConsumer(queueName);
		        
				int iterations = 4;
				do
				{										
					this.receiveMessage(messageConsumer);
					
					Thread.currentThread().sleep(2000);
				}while(iterations-- > 0);
			}
			catch(Exception e)
			{
				log.error(this, e);
			}
			finally
	        {
	           //Be sure to close our resources!
	           if (session != null)
	           {
	              try{session.close();}catch(Exception e){}
	           }           
	        }
		}
		
		private void receiveMessage(ClientConsumer messageConsumer) throws Exception
		{			                                  	           	  	           			  
			//Receive the message. 
			ClientMessage messageReceived = messageConsumer.receive();
           
			final String propName = "myprop";
			log.info("Client("+this.subscriberId+"):" + messageReceived.getProperty(propName));			
		}
	}
	//------------------------------------------------------------------------------------------------------------
	/*public void testSimpleJMSQueueScenario() throws Exception
	{
		//Establist a Connection Factory
		TransportConfiguration transportConfig = new TransportConfiguration(
		        InVMConnectorFactory.class.getName());
		HornetQConnectionFactory cf = new HornetQConnectionFactory(transportConfig);
		
		//Create a Queue programmatically
		ClientSession coreSession = cf.getCoreFactory().createSession(false,false,false);
		coreSession.createQueue("jms.queue./simple/queue","jms.queue./simple/queue", true);
		coreSession.close();
		
		//Start a Publish Session
		Queue queue = new HornetQQueue("/simple/queue");
		Connection connection = null;
		try
		{
			connection = cf.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(queue);
			
			TextMessage message = session.createTextMessage("Hello sent at " + new Date());
			producer.send(message);						
		}
		finally
		{
			if(connection != null)
			{
				connection.close();
			}
		}
		
		//Start a Read Session
		connection = null;
		try
		{
			connection = cf.createConnection();
			connection.start();
			
			//Start a Listening Session on this connection
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);						
			MessageConsumer messageConsumer = session.createConsumer(queue);            
            
            TextMessage messageReceived = (TextMessage)messageConsumer.receive();
            log.info("-------------------------------------------------------------------");
            log.info("Received TextMessage:" + messageReceived.getText());
            log.info("-------------------------------------------------------------------");
		}
		finally
		{
			if(connection != null)
			{
				connection.close();
			}
		}
	}*/
}
