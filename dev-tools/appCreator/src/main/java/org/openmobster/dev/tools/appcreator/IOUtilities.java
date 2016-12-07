/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.dev.tools.appcreator;

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
		String data = null;
		int received = 0;
		ByteArrayOutputStream bos = null;
		try
		{
			bos = new ByteArrayOutputStream();
			boolean carriageFound = false;
			while(true)
			{			
				received = is.read();				
				if(carriageFound)
				{
					carriageFound = false;
					if(received == '\n')
					{
						break;
					}					
				}				
				if(received == '\r')
				{
					carriageFound = true;
				}
				
				bos.write(received);
			}			
			bos.flush();
			
			StringBuffer buffer = new StringBuffer();
			byte[] cour = bos.toByteArray();
			buffer.append(new String(cour));						
			data = buffer.toString();
			
			return data;
		}
		finally
		{
			if(bos != null)
			{
				try{bos.close();}catch(IOException e){}
			}
		}
	}
	
	public static void writePayLoad(String payLoad, OutputStream os) throws IOException
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
	}
}
