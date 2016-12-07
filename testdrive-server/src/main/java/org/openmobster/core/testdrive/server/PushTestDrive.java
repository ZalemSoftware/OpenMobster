/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.testdrive.server;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import org.apache.mina.core.session.IoSession;

import org.openmobster.core.dataService.processor.Input;
import org.openmobster.core.dataService.processor.Processor;
import org.openmobster.core.dataService.processor.ProcessorException;
import org.openmobster.core.dataService.Constants;

/**
 * @author openmobster@gmail.com
 *
 */
public class PushTestDrive implements Processor
{
	private static Logger log = Logger.getLogger(PushTestDrive.class);
	
	private String id;
	
	private String pushThis;
	
	public String getId()
	{
		return this.id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public void start()
	{
		log.info("----------------------------------------------");
		log.info("PushTestDrive service successfully started....");
		log.info("----------------------------------------------");
	}

	public String process(Input input) throws ProcessorException
	{
		try
		{
			String payload = input.getMessage();
			
			log.info("-----------------------------------------------");
			log.info("Push TestDrive: "+payload);
			log.info("-----------------------------------------------");
			
			if(payload.startsWith("<push>"))
			{
				IoSession session = input.getSession();
				long startDelay = 5000;
				
				//Start the heart beat daemon
				Timer heartbeatTimer = new Timer(this.getClass().getName(), true); //sets it as a daemon thread
				TimerTask heartbeatTask = new HeartBeat(session);
				heartbeatTimer.schedule(heartbeatTask, startDelay, 55000);
				
				//Start the push data daemon
				Timer pushTimer = new Timer(this.getClass().getName(), true); //sets it as a daemon thread
				TimerTask pushTask = new PushData(session);			
				pushTimer.schedule(pushTask, startDelay, 20000);
			}
			else
			{
				if(payload.startsWith("trigger"))
				{
					this.pushThis = payload.replaceAll("trigger", "push");
				}
			}
			
			return "@";
		}
		catch(Exception e)
		{
			throw new ProcessorException(e);
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------------
	private static class HeartBeat extends TimerTask
	{
		private IoSession session;
		
		public HeartBeat(IoSession session)
		{
			this.session = session;
		}
		
		public void run()
		{
			try
			{
				if(!session.isConnected())
				{
					System.out.println("------------------------------------------");
					System.out.println("Leaving Heartbeat.........................");
					System.out.println("------------------------------------------");
					this.cancel();
					return;
				}
				
				System.out.println("--------------------------------------------------------------------");
				System.out.println("Pushing a heartbeat ("+System.currentTimeMillis()+")................");
				System.out.println("--------------------------------------------------------------------");
				this.session.write("@");
				this.session.write(Constants.endOfStream);
			}
			catch(Exception e)
			{
				System.out.println("--------------------------------------------------");
				System.out.println("HeartBeat: "+e.getMessage());
				System.out.println("--------------------------------------------------");
			}
		}
	}
	
	public class PushData extends TimerTask
	{
		private IoSession session;
		
		public PushData(IoSession session)
		{
			this.session = session;
		}
		
		public void run()
		{
			try
			{
				if(!session.isConnected())
				{
					System.out.println("------------------------------------------");
					System.out.println("Leaving Push Data.........................");
					System.out.println("------------------------------------------");
					this.cancel();
					return;
				}
				
												
				if(PushTestDrive.this.pushThis != null)
				{
					System.out.println("------------------------------------------------");
					System.out.println("Ready to push a data............................");
					System.out.println(PushTestDrive.this.pushThis);
					this.session.write(PushTestDrive.this.pushThis);
					this.session.write(Constants.endOfStream);
					PushTestDrive.this.pushThis = null; //clear push buffer
					System.out.println("------------------------------------------------");
				}				
			}
			catch(Exception e)
			{
				System.out.println("--------------------------------------------------");
				System.out.println("Push Data: "+e.getMessage());
				System.out.println("--------------------------------------------------");
			}
		}
	}
}
