/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.common;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;

import org.openmobster.core.common.bus.Bus;
import org.openmobster.core.common.bus.BusMessage;

/**
 *
 * @author openmobster@gmail.com
 */
public final class PerfLogInterceptor
{
	private CreateConnectionBusListener createConnectionBusListener;
	
	private FileOutputStream failedConnection;
	private long failedConnectionCtr;
	
	private RequestSentBusListener requestSentBusListener;
	
	private FileOutputStream requestFailed;
	private long requestFailedCtr;
	
	private FileOutputStream responseReceived;
	private long responseReceivedCtr;
	
	private FileOutputStream responseFailed;
	private long responseFailedCtr;
	
	private FileOutputStream pushFailed;
	private long pushFailedCtr;
	
	private FileOutputStream bytesFile;
	private List<Long> bytesTransferred;
	
	
	public PerfLogInterceptor()
	{
		
	}
	
	public void start()
	{
		try
		{
			String perfFramework = System.getProperty("perf-framework");
			if(perfFramework == null)
			{
				return;
			}
			
			//Connection Success
			this.createConnectionBusListener = new CreateConnectionBusListener();
			this.createConnectionBusListener.start();
			Bus.startBus("connection-success");
			Bus.addBusListener("connection-success", this.createConnectionBusListener);
			
			//Failed Connection
			File failedConnectionFile = new File("connection-failed.xml");
			failedConnectionFile.delete();
			this.failedConnection = new FileOutputStream("connection-failed.xml",true);
			this.failedConnectionCtr = 0;
			
			//Request Sent Successfully
			this.requestSentBusListener = new RequestSentBusListener();
			this.requestSentBusListener.start();
			Bus.startBus("request-success");
			Bus.addBusListener("request-success", this.requestSentBusListener);
			
			//Request Failed
			File requestFailedFile = new File("request-failed.xml");
			requestFailedFile.delete();
			this.requestFailed = new FileOutputStream("request-failed.xml",true);
			this.requestFailedCtr = 0;
			
			//Response Received
			File responseReceivedFile = new File("response-success.xml");
			responseReceivedFile.delete();
			this.responseReceived = new FileOutputStream("response-success.xml",true);
			this.responseReceivedCtr = 0;
			
			//Response Failed
			File responseFailedFile = new File("response-failed.xml");
			responseFailedFile.delete();
			this.responseFailed = new FileOutputStream("response-failed.xml",true);
			this.responseFailedCtr = 0;
			
			//Push Failed
			File pushFailedFile = new File("push-failed.xml");
			pushFailedFile.delete();
			this.pushFailed = new FileOutputStream("push-failed.xml",true);
			this.pushFailedCtr = 0;
			
			//Bytes Transferred
			File bytes = new File("bytes.xml");
			bytes.delete();
			this.bytesFile = new FileOutputStream("bytes.xml",true);
			this.bytesTransferred = new ArrayList<Long>();
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
			String perfFramework = System.getProperty("perf-framework");
			if(perfFramework == null)
			{
				return;
			}
			
			this.createConnectionBusListener.stop();
			
			if(this.failedConnection != null)
			{
				this.failedConnection.close();
			}
			
			this.requestSentBusListener.stop();
			
			if(this.requestFailed != null)
			{
				this.requestFailed.close();
			}
			
			if(this.responseReceived != null)
			{
				this.responseReceived.close();
			}
			
			if(this.responseFailed != null)
			{
				this.responseFailed.close();
			}
			
			if(this.pushFailed != null)
			{
				this.pushFailed.close();
			}
			
			this.logBytesTransferred();
			if(this.bytesFile != null)
			{
				this.bytesFile.close();
			}
			this.bytesTransferred = null;
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	
	public static PerfLogInterceptor getInstance()
	{
		return (PerfLogInterceptor)ServiceManager.locate("org.openmobster.core.common.PerfLogInterceptor");
	}
	
	public void logCreateConnection()
	{
		String perfFramework = System.getProperty("perf-framework");
		if(perfFramework == null)
		{
			return;
		}
		BusMessage busMessage = new BusMessage();
		busMessage.setBusUri("connection-success");
		busMessage.setSenderUri("connection-success");
		Bus.sendMessage(busMessage);
	}
	
	public void logConnectionFailed()
	{
		String perfFramework = System.getProperty("perf-framework");
		if(perfFramework == null)
		{
			return;
		}
		try
		{
			synchronized(this.failedConnection)
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
				buffer.append("<connection-number>"+(++this.failedConnectionCtr)+"</connection-number>\n");
				buffer.append("</entry>\n\n");
				
				this.failedConnection.write(buffer.toString().getBytes());
				this.failedConnection.flush();
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	
	public void logRequestSent()
	{
		String perfFramework = System.getProperty("perf-framework");
		if(perfFramework == null)
		{
			return;
		}
		BusMessage busMessage = new BusMessage();
		busMessage.setBusUri("request-success");
		busMessage.setSenderUri("request-success");
		Bus.sendMessage(busMessage);
	}
	
	public void logRequestFailed()
	{
		String perfFramework = System.getProperty("perf-framework");
		if(perfFramework == null)
		{
			return;
		}
		try
		{
			synchronized(this.requestFailed)
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
				buffer.append("<request-number>"+(++this.requestFailedCtr)+"</request-number>\n");
				buffer.append("</entry>\n\n");
				
				this.requestFailed.write(buffer.toString().getBytes());
				this.requestFailed.flush();
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	
	public void logResponseRead()
	{
		String perfFramework = System.getProperty("perf-framework");
		if(perfFramework == null)
		{
			return;
		}
		/*try
		{
			synchronized(this.responseReceived)
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
				buffer.append("<response-number>"+(++this.responseReceivedCtr)+"</response-number>\n");
				buffer.append("</entry>\n\n");
				
				this.responseReceived.write(buffer.toString().getBytes());
				this.responseReceived.flush();
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}*/
	}
	
	public void logResponseFailed()
	{
		String perfFramework = System.getProperty("perf-framework");
		if(perfFramework == null)
		{
			return;
		}
		try
		{
			synchronized(this.responseFailed)
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
				buffer.append("<response-number>"+(++this.responseFailedCtr)+"</response-number>\n");
				buffer.append("</entry>\n\n");
				
				this.responseFailed.write(buffer.toString().getBytes());
				this.responseFailed.flush();
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	
	public void logPushFailed()
	{
		String perfFramework = System.getProperty("perf-framework");
		if(perfFramework == null)
		{
			return;
		}
		try
		{
			synchronized(this.pushFailed)
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
				buffer.append("<push-number>"+(++this.pushFailedCtr)+"</push-number>\n");
				buffer.append("</entry>\n\n");
				
				this.pushFailed.write(buffer.toString().getBytes());
				this.pushFailed.flush();
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	
	public void logBytesTransferred()
	{
		String perfFramework = System.getProperty("perf-framework");
		if(perfFramework == null)
		{
			return;
		}
		try
		{
			synchronized(this.bytesFile)
			{	
				StringBuilder buffer = new StringBuilder();
				
				//generate the total
				long total = 0;
				for(Long local:this.bytesTransferred)
				{
					total += local;
				}
				
				buffer.append(""+total);
				
				this.bytesFile.write(buffer.toString().getBytes());
				this.bytesFile.flush();
			}
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	
	public void recordBytesTransferred(long bytes)
	{
		String perfFramework = System.getProperty("perf-framework");
		if(perfFramework == null)
		{
			return;
		}
		this.bytesTransferred.add(new Long(bytes));
	}
}
