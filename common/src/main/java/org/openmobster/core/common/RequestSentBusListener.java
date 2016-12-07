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
public final class RequestSentBusListener implements BusListener
{
	private static Logger log = Logger.getLogger(RequestSentBusListener.class);
	
	private FileOutputStream requestSent;
	private long requestSentCtr;
	
	public void start()
	{
		try
		{
			//Request Success
			File requestSentFile = new File("request-success.xml");
			requestSentFile.delete();
			this.requestSent = new FileOutputStream("request-success.xml",true);
			this.requestSentCtr = 0;
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
			if(this.requestSent != null)
			{
				this.requestSent.close();
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
			synchronized(this.requestSent)
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
				buffer.append("<request-number>"+(++this.requestSentCtr)+"</request-number>\n");
				buffer.append("</entry>\n\n");
				
				this.requestSent.write(buffer.toString().getBytes());
				this.requestSent.flush();
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
