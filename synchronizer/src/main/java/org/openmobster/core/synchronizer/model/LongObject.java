/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.model;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * @author openmobster@gmail.com
 */
public class LongObject implements Serializable
{
	private int chunkIndex = 0;
	private List dataChunks = null;
	
	/**
	 * 
	 * @param messageSize
	 * @param data
	 */
	public LongObject(int messageSize, String data)
	{
		this.dataChunks = new ArrayList();
		this.chunkIndex = 0;
		
		if(messageSize > 0)
		{			
			int index = 0;
			while(index < data.length())
			{
				StringBuffer buffer = new StringBuffer();
				for(int i=0;i<messageSize;i++)
				{
					if(index < data.length())
					{
						buffer.append(data.charAt(index++));
					}
					else
					{
						break;
					}
				}
				this.dataChunks.add(buffer.toString());
			}
						
		}
		else
		{
			this.dataChunks.add(data);
		}
	}
	
	/**
	 * 
	 * @param command
	 * @return
	 */
	public void processNextChunk(AbstractOperation command)
	{		
		Item item = (Item) command.getItems().get(0);

		StringBuffer buffer = new StringBuffer();

		if (this.chunkIndex < this.dataChunks.size())
		{
			buffer.append((String)this.dataChunks.get(this.chunkIndex++));						
			
			if(this.chunkIndex == this.dataChunks.size())
			{
				//This means all chunks have been processed
				item.setMoreData(false);
				command.setChunkedRecord(null);						
			}
		}		

		item.setData(buffer.toString());
	}
	
	/**
	 * 
	 * @return
	 */
	public List getDataChunks()
	{
		return this.dataChunks;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCurrentChunk()
	{
		String chunk = null;
		
		if (this.chunkIndex < this.dataChunks.size())
		{
			chunk = (String)this.dataChunks.get(this.chunkIndex++);									
		}
		
		return chunk;
	}
}
