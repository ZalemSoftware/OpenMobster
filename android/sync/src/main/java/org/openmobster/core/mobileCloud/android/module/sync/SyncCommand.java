/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;

import java.util.List;
import java.util.ArrayList;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class SyncCommand
{	
	private String cmdId; //required
	
	private String target; //nullable
	private String source; //nullable 
	private String meta; //nullable
	private String numberOfChanges; //nullable
	
	private List<Add> addCommands; //zero or many
	private List<Replace> replaceCommands; //zero or many
	private List<Delete> deleteCommands; //zero or many
		
	
	/**
	 * 
	 *
	 */
	public SyncCommand()
	{
		this.addCommands = new ArrayList<Add>();
		this.replaceCommands = new ArrayList<Replace>();
		this.deleteCommands = new ArrayList<Delete>();
	}


	public String getCmdId()
	{
		return cmdId;
	}


	public void setCmdId(String cmdId)
	{
		this.cmdId = cmdId;
	}


	public String getMeta()
	{
		return meta;
	}


	public void setMeta(String meta)
	{
		this.meta = meta;
	}


	public String getNumberOfChanges()
	{
		return numberOfChanges;
	}


	public void setNumberOfChanges(String numberOfChanges)
	{
		this.numberOfChanges = numberOfChanges;
	}


	public String getSource()
	{
		return source;
	}


	public void setSource(String source)
	{
		this.source = source;
	}


	public String getTarget()
	{
		return target;
	}


	public void setTarget(String target)
	{
		this.target = target;
	}


	public List<Add> getAddCommands()
	{
		if(this.addCommands == null)
		{
			this.addCommands = new ArrayList<Add>();
		}
		return addCommands;
	}


	public void setAddCommands(List<Add> addCommands)
	{
		this.addCommands = addCommands;
	}
	
	public List<Replace> getReplaceCommands()
	{
		if(this.replaceCommands == null)
		{
			this.replaceCommands = new ArrayList<Replace>();
		}
		return this.replaceCommands;
	}
	
	public void setReplaceCommands(List<Replace> replaceCommands)
	{
		this.replaceCommands = replaceCommands;
	}
	
	public List<Delete> getDeleteCommands()
	{
		if(this.deleteCommands == null)
		{
			this.deleteCommands = new ArrayList<Delete>();
		}
		return this.deleteCommands;
	}
	
	public void setDeleteCommands(List<Delete> deleteCommands)
	{
		this.deleteCommands = deleteCommands;
	}
	
	/**
	 * 
	 * @param command
	 */
	public void addOperationCommand(AbstractOperation command)
	{
		if(command instanceof Add)
		{
			this.getAddCommands().add((Add)command);
		}
		else if(command instanceof Replace)
		{
			this.getReplaceCommands().add((Replace)command);
		}
		else if(command instanceof Delete)
		{
			this.getDeleteCommands().add((Delete)command);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public List<? extends AbstractOperation> getAllCommands()
	{
		List<AbstractOperation> allCommands = new 
		ArrayList<AbstractOperation>();
	
		List<Add> addCommands = this.getAddCommands();
		if(addCommands != null)
		{
			allCommands.addAll(addCommands);
		}
		
		List<Replace> replaceCommands = this.getReplaceCommands();
		if(replaceCommands != null)
		{
			allCommands.addAll(replaceCommands);
		}
		
		List<Delete> deleteCommands = this.getDeleteCommands();
		if(deleteCommands != null)
		{
			allCommands.addAll(deleteCommands);
		}
				
		return allCommands;
	}	
	//-----Long Object Support----------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public List<? extends AbstractOperation> filterChunkedCommands()
	{
		List<AbstractOperation> chunkedCommands = new ArrayList<AbstractOperation>();
		
		List<Add> chunkedAddCommands = new ArrayList<Add>();
		List<Add> addCommands = this.getAddCommands();
		for(Add cour:addCommands)
		{
			if(cour.isChunked())
			{
				chunkedAddCommands.add(cour);
			}
		}
		addCommands.removeAll(chunkedAddCommands);
		chunkedCommands.addAll(chunkedAddCommands);
		
		List<Replace> chunkedReplaceCommands = new ArrayList<Replace>();
		List<Replace> replaceCommands = this.getReplaceCommands();
		for(Replace cour:replaceCommands)
		{
			if(cour.isChunked())
			{
				chunkedReplaceCommands.add(cour);
			}
		}
		replaceCommands.removeAll(chunkedReplaceCommands);
		chunkedCommands.addAll(chunkedReplaceCommands);
				
		return chunkedCommands;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<? extends AbstractOperation> getChunkedCommands()
	{
		List<AbstractOperation> chunkedCommands = new ArrayList<AbstractOperation>();
		
		List<Add> chunkedAddCommands = new ArrayList<Add>();
		List<Add> addCommands = this.getAddCommands();
		for(Add cour:addCommands)
		{
			if(cour.isChunked())
			{
				chunkedAddCommands.add(cour);
			}
		}		
		chunkedCommands.addAll(chunkedAddCommands);
		
		List<Replace> chunkedReplaceCommands = new ArrayList<Replace>();
		List<Replace> replaceCommands = this.getReplaceCommands();
		for(Replace cour:replaceCommands)
		{
			if(cour.isChunked())
			{
				chunkedReplaceCommands.add(cour);
			}
		}
		chunkedCommands.addAll(chunkedReplaceCommands);
		
		return chunkedCommands;
	}
		
	/**
	 * 
	 * @param command
	 */
	public void addChunkedCommand(AbstractOperation command)
	{
		if(command instanceof Add)
		{
			this.getAddCommands().add((Add)command);
		}
		else if(command instanceof Replace)
		{
			this.getReplaceCommands().add((Replace)command);
		}
	}	
}
