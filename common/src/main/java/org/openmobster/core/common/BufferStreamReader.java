/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * @author openmobster@gmail.com
 */
public final class BufferStreamReader
{
	private ByteArrayOutputStream buffer;
	
	public BufferStreamReader(byte[] data)
	{
		try
		{
			this.buffer = new ByteArrayOutputStream();
			this.buffer.write(data);
		}
		catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}
	
	public BufferStreamReader()
	{
		this.buffer = new ByteArrayOutputStream();
	}
	
	public void fillBuffer(byte[] data)
	{
		try
		{
			this.buffer.write(data);
		}
		catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}
	
	public void close()
	{
		try
		{
			if(this.buffer != null)
			{
				this.buffer.close();
				this.buffer = null;
			}
		}
		catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}
	
	public String readLine()
	{
		ByteArrayOutputStream line = null;
		ByteArrayOutputStream newBuffer = null;
		try
		{
			byte[] contents = this.buffer.toByteArray();
			int length = contents.length;
		
			line = new ByteArrayOutputStream();
			int pointer = 0;
			boolean lineFound = false;
			for(int i=0; i<length; i++)
			{
				line.write(contents[i]);
				
				if(contents[i] == '\n')
				{
					pointer = i+1;
					lineFound = true;
					break;
				}
			}
			
			if(lineFound)
			{
				//re-position the buffer
				newBuffer = new ByteArrayOutputStream();
				for(int i=pointer; i<length; i++)
				{
					newBuffer.write(contents[i]);
				}
				this.close();
				this.buffer = newBuffer;
				
				//construct the line to be returned
				String lineStr = new String(line.toByteArray());
				
				return lineStr;
			}
		
			return null;
		}
		finally
		{
			if(line != null)
			{
				try{line.close();}catch(IOException ioex){}
			}
		}
	}
}
