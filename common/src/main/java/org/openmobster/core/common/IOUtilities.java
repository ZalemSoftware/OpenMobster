/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedInputStream;
import java.io.OutputStream;

/**
 * 
 * @author openmobster@gmail.com
 */
public class IOUtilities 
{	
	public static byte[] readBytes(InputStream is) throws IOException
	{		
		byte[] data = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        
        try
        {
	        byte[] buffer = new byte[512];        
	        int read = -1;
	        while((read=bis.read(buffer,0,buffer.length)) != -1)
	        {
	            bos.write(buffer,0,read);
	        }
	        data = bos.toByteArray();
        }
        finally
        {
        	if(is != null)
        	{
        		is.close();
        	}
        	if(bos != null)
        	{
        		bos.close();
        	}
        	if(bis != null)
        	{
        		bis.close();
        	}
        }
        
        return data;
	}
		
	public static String readServerResponse(InputStream is) throws IOException
	{	
		byte[] received = null;
		StringBuilder incomingData = new StringBuilder();
		boolean exit = false;
		BufferStreamReader reader = new BufferStreamReader();
		boolean content_length_processed = false;
		try
		{
			while(true)
			{
				received = readFromStream(is);
				
				if(received == null)
				{
					//no data read this iteration, better luck in the next iteration
					continue;
				}
				
				//drop the data into the reader
				reader.fillBuffer(received);
				
				//Now read a line
				String line = null;
				while((line=reader.readLine()) != null)
				{
					if(line.startsWith("content-length="))
					{
						if(content_length_processed)
						{
							continue;
						}
						
						content_length_processed = true;
						
						//do stuff here
						int contentLength = Integer.parseInt(line.substring("content-length=".length()).trim());
			
						
						continue;
					}
					
					if(line.endsWith("OPENMOBSTER_EOF_\r\n"))
					{
						//thats it....end of the line
						int index = line.indexOf("OPENMOBSTER_EOF_\r\n");
						String newLine = line.substring(0, index);
						
						incomingData.append(newLine);
						
						exit = true;
						break;
					}
					
					incomingData.append(line);
				}
				
				if(exit)
				{
					break;
				}
			}
			
			String returnValue = incomingData.toString().trim();
			
			return returnValue;
		}
		finally
		{
			reader.close();
		}
	}
	
	private static byte[] readFromStream(InputStream is) throws IOException
	{
		byte[] dataPacket = new byte[1024];
		
		int numBytesRead = is.read(dataPacket);
		if(numBytesRead == -1)
		{
			throw new IOException("InputStream is closed!!");
		}
		
		if(numBytesRead > 0)
		{
			byte[] packet = new byte[numBytesRead];
			System.arraycopy(dataPacket, 0, packet, 0, numBytesRead);
			
			return packet;
		}
		
		return null;
	}
	
	public static void writePayLoad(String payLoad, OutputStream os) throws IOException
	{
		try
		{
			int startIndex = 0;
			int endIndex = 0;
			boolean eofSent = false;
			while((endIndex=payLoad.indexOf("\n", startIndex))!=-1)
			{				
				String packet = payLoad.substring(startIndex, endIndex);
				os.write((packet+"\n").getBytes());
				
				startIndex = endIndex +1;
				
				//Check if startIndex has exceeded beyond the last index of the string
				if(startIndex >= payLoad.length()-1)
				{
					os.write("EOF\n".getBytes());
					os.flush();
					eofSent = true;
					break;
				}
			}
			if(!eofSent)
			{
				String packet = payLoad.substring(startIndex);
				os.write((packet+"EOF\n").getBytes());
				os.flush();
			}
			
			//log that request is successfull
			PerfLogInterceptor.getInstance().logRequestSent();
			byte[] payloadInBytes = payLoad.getBytes();
			PerfLogInterceptor.getInstance().recordBytesTransferred(payloadInBytes.length);
		}
		catch(Throwable t)
		{
			//log that request is unsuccessful
			PerfLogInterceptor.getInstance().logRequestFailed();
			
			if(t instanceof IOException)
			{
				throw (IOException)t;
			}
			else
			{
				throw new IOException(t);
			}
		}
	}
}
