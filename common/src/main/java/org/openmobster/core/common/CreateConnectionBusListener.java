/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.common;

import java.io.FileOutputStream;
import java.io.File;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.openmobster.core.common.bus.BusListener;
import org.openmobster.core.common.bus.BusMessage;

/**
 *
 * @author openmobster@gmail.com
 */
public final class CreateConnectionBusListener implements BusListener
{
	private static Logger log = Logger.getLogger(CreateConnectionBusListener.class);
	
	private FileOutputStream createConnection;
	private long createConnectionCtr;
	
	public void start()
	{
		try
		{
			//Connection Success
			File createConnectionFile = new File("connection-success.xml");
			createConnectionFile.delete();
			this.createConnection = new FileOutputStream("connection-success.xml",true);
			this.createConnectionCtr = 0;
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	
	public void stop()
	{
		try
		{
			if(this.createConnection != null)
			{
				this.createConnection.close();
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	
	@Override
	public synchronized void messageIncoming(BusMessage busMessage)
	{
		try
		{
			synchronized(this.createConnection)
			{	
				Calendar calendar = Calendar.getInstance();
				int hour_of_day = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				int second = calendar.get(Calendar.SECOND);
				int milli = calendar.get(Calendar.MILLISECOND);
				
				
				StringBuilder buffer = new StringBuilder();
				
				buffer.append("<entry>\n");
				buffer.append("<time-millis>"+calendar.getTimeInMillis()+"</time-millis>\n");
				buffer.append("<time>"+hour_of_day+":"+minute+":"+second+":"+milli+"</time>\n");
				buffer.append("<connection-number>"+(++this.createConnectionCtr)+"</connection-number>\n");
				buffer.append("</entry>\n\n");
				
				this.createConnection.write(buffer.toString().getBytes());
				this.createConnection.flush();
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
		finally
		{
			busMessage.acknowledge();
		}
	}
}
