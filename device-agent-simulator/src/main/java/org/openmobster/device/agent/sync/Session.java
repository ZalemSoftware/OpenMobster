/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.sync;

import java.util.Vector;
import java.util.Hashtable;

import org.openmobster.device.agent.sync.engine.ChangeLogEntry;
import org.openmobster.core.common.XMLUtilities;

/**
 * 
 * @author openmobster@gmail.com
 * 
 * Memory Marker - Stateful Component (RAM Usage)
 *
 */
public final class Session 
{
	private String sessionId; //required
	private String target; //required
	private String source; //required
	private String app;//required
	
	private SyncPackage clientInitPackage;
	private SyncPackage serverInitPackage;
	
	private SyncPackage clientSyncPackage;
	private SyncPackage serverSyncPackage;
	
	private SyncPackage clientClosePackage;
	private SyncPackage serverClosePackage;
	
	private Anchor anchor;
	
	/**
	 * some session related meta data
	 */
	private SyncMessage currentMessage;
	private int phaseCode;
	private String syncType;
	private int maxClientSize;
	
	/**
	 * Long Object support
	 */
	private Vector chunkedCommands;
	private Vector chunks;
	private AbstractOperation chunkSource;
	private String chunkBackup;
	private boolean retransmission;
	
	/**
	 * Operation Command related meta data
	 */
	private Vector allOperationCommands;
	private int operationCommandIndex;
	private boolean operationCommandStateInitiated;
	
	/**
	 * Map Support
	 */
	private Hashtable recordMap;
	private boolean mapExchangeInProgress;
	
	/**
	 * Arbitrary session related data
	 */
	private Hashtable attributes;
	
	private boolean hasSyncExecutedOnce;
	
	/**
	 * 
	 *
	 */
	public Session()
	{
		this.clientInitPackage = new SyncPackage();
		this.serverInitPackage = new SyncPackage();
		
		this.clientSyncPackage = new SyncPackage();
		this.serverSyncPackage = new SyncPackage();
		
		this.clientClosePackage = new SyncPackage();
		this.serverClosePackage = new SyncPackage();
		
		this.attributes = new Hashtable();
		
		this.phaseCode = SyncAdapter.PHASE_INIT;
	}
	
	/**
	 * 
	 */
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("Session-------");
		buffer.append("Id="+this.sessionId+","+"Source="+this.source+","
		+"Target="+this.target+","+"PhaseCode="+this.phaseCode+",SyncType="+this.syncType);
		
		return buffer.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSessionId()
	{
		return sessionId;
	}

	/**
	 * 
	 * @param sessionId
	 */
	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}
	
	public String getApp()
	{
		return app;
	}

	public void setApp(String app)
	{
		this.app = app;
	}

	/**
	 * 
	 * @return
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * 
	 * @param source
	 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/**
	 * 
	 * @return
	 */
	public String getTarget()
	{
		return target;
	}

	/**
	 * 
	 * @param target
	 */
	public void setTarget(String target)
	{
		this.target = target;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMaxClientSize()
	{
		return maxClientSize;
	}

	/**
	 * 
	 * @param maxClientMessageSize
	 */
	public void setMaxClientSize(int maxClientSize)
	{
		this.maxClientSize = maxClientSize;
	}

	/**
	 * 
	 * @return
	 */
	public SyncPackage getClientInitPackage()
	{
		return clientInitPackage;
	}

	/**
	 * 
	 * @param clientInitPackage
	 */
	public void setClientInitPackage(SyncPackage clientInitPackage)
	{
		if(clientInitPackage != null)
		{
			this.clientInitPackage = clientInitPackage;
		}
		else
		{
			this.clientInitPackage = new SyncPackage();
		}
	}

	/**
	 * 
	 * @return
	 */
	public SyncPackage getClientSyncPackage()
	{
		return clientSyncPackage;
	}

	/**
	 * 
	 * @param clientSyncPackage
	 */
	public void setClientSyncPackage(SyncPackage clientSyncPackage)
	{
		if(clientSyncPackage != null)
		{
			this.clientSyncPackage = clientSyncPackage;
		}
		else
		{
			this.clientSyncPackage = new SyncPackage();
		}
	}

	/**
	 * 
	 * @return
	 */
	public SyncPackage getServerInitPackage()
	{
		return serverInitPackage;
	}

	/**
	 * 
	 * @param serverInitPackage
	 */
	public void setServerInitPackage(SyncPackage serverInitPackage)
	{
		if(serverInitPackage != null)
		{
			this.serverInitPackage = serverInitPackage;
		}
		else
		{
			this.serverInitPackage = new SyncPackage();
		}
	}

	/**
	 * 
	 * @return
	 */
	public SyncPackage getServerSyncPackage()
	{
		return serverSyncPackage;
	}

	/**
	 * 
	 * @param serverSyncPackage
	 */
	public void setServerSyncPackage(SyncPackage serverSyncPackage)
	{
		if(serverSyncPackage != null)
		{
			this.serverSyncPackage = serverSyncPackage;
		}
		else
		{
			this.serverSyncPackage = new SyncPackage();
		}
	}
	
	public SyncPackage getClientClosePackage()
	{
		if(this.clientClosePackage == null)
		{
			this.clientClosePackage = new SyncPackage();
		}
		return this.clientClosePackage;
	}

	public void setClientClosePackage(SyncPackage clientClosePackage)
	{
		this.clientClosePackage = clientClosePackage;
	}

	public SyncPackage getServerClosePackage()
	{
		if(this.serverClosePackage == null)
		{
			this.serverClosePackage = new SyncPackage();
		}
		return this.serverClosePackage;
	}

	public void setServerClosePackage(SyncPackage serverClosePackage)
	{
		this.serverClosePackage = serverClosePackage;
	}

	/**
	 * 
	 * @return
	 */
	public SyncMessage getCurrentMessage()
	{
		return currentMessage;
	}

	/**
	 * 
	 * @param currentMessage
	 */
	public void setCurrentMessage(SyncMessage currentMessage)
	{
		this.currentMessage = currentMessage;
	}

	/**
	 * 
	 * @return
	 */
	public int getPhaseCode()
	{
		return phaseCode;
	}

	/**
	 * 
	 * @param phaseCode
	 */
	public void setPhaseCode(int phaseCode)
	{
		this.phaseCode = phaseCode;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSyncType()
	{
		return syncType;
	}

	/**
	 * 
	 * @param syncType
	 */
	public void setSyncType(String syncType)
	{
		this.syncType = syncType;
	}
	
	/**
	 * 
	 * @return
	 */
	public Anchor getAnchor() 
	{
		return anchor;
	}

	/**
	 * 
	 * @param anchor
	 */
	public void setAnchor(Anchor anchor) 
	{
		this.anchor = anchor;
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	private String getDataSource(SyncMessage message)
	{
		String dataSource = null;
		
		Vector alerts = message.getAlerts();
		for(int i=0, size=alerts.size();i<size;i++)
		{
			Alert alert = (Alert)alerts.elementAt(i);
			Vector items = alert.getItems();
			for(int j=0, jSize=items.size();j<jSize;j++)
			{
				Item item = (Item)items.elementAt(j);
				if(item.getSource() != null && item.getSource().trim().length()>0)
				{
					dataSource = item.getSource();
					break;
				}
			}
		}
		
		return dataSource;
	}
	
	/**
	 * 
	 * @param message
	 * @return
	 */
	private String getDataTarget(SyncMessage message)
	{
		String dataTarget = null;
		
		Vector alerts = message.getAlerts();
		for(int i=0, size=alerts.size();i<size;i++)
		{
			Alert alert = (Alert)alerts.elementAt(i);
			Vector items = alert.getItems();
			for(int j=0, jSize=items.size();j<jSize;j++)
			{
				Item item = (Item)items.elementAt(j);
				if(item.getTarget() != null && item.getTarget().trim().length()>0)
				{
					dataTarget = item.getTarget();
					break;
				}
			}
		}
		
		return dataTarget;
	}
	
	/**
	 * 
	 * @param syncAdapter
	 * @return
	 */
	public String getDataSource(boolean searchServerMessages)
	{
		String dataSource = null;
		
		if(searchServerMessages)
		{
			Vector allServerMessages = new Vector();
			allServerMessages.addAll(this.serverInitPackage.getMessages());
			allServerMessages.addAll(this.serverSyncPackage.getMessages());			
			
			for(int i=0, size=allServerMessages.size();i<size;i++)
			{
				SyncMessage cour = (SyncMessage)allServerMessages.elementAt(i);
				dataSource = this.getDataSource(cour);
				if(dataSource != null && dataSource.trim().length()>0)
				{
					return dataSource;
				}
			}			
		}
		else
		{
			Vector allClientMessages = new Vector();
			allClientMessages.addAll(this.clientInitPackage.getMessages());
			allClientMessages.addAll(this.clientSyncPackage.getMessages());
			
			for(int i=0, size=allClientMessages.size();i<size;i++)
			{
				SyncMessage cour = (SyncMessage)allClientMessages.elementAt(i);
				dataSource = this.getDataSource(cour);
				if(dataSource != null && dataSource.trim().length()>0)
				{
					return dataSource;
				}
			}
		}
		
		return dataSource;
	}
	
	/**
	 * 
	 * @param syncAdapter
	 * @return
	 */
	public String getDataTarget(boolean searchServerMessages)
	{
		String dataTarget = null;
		
		if(searchServerMessages)
		{
			Vector allServerMessages = new Vector();
			allServerMessages.addAll(this.serverInitPackage.getMessages());
			allServerMessages.addAll(this.serverSyncPackage.getMessages());
			
			for(int i=0, size=allServerMessages.size();i<size;i++)
			{
				SyncMessage cour = (SyncMessage)allServerMessages.elementAt(i);
				dataTarget = this.getDataTarget(cour);
				if(dataTarget != null && dataTarget.trim().length()>0)
				{
					return dataTarget;
				}
			}			
		}
		else
		{
			Vector allClientMessages = new Vector();
			allClientMessages.addAll(this.clientInitPackage.getMessages());
			allClientMessages.addAll(this.clientSyncPackage.getMessages());
			
			for(int i=0, size=allClientMessages.size();i<size;i++)
			{
				SyncMessage cour = (SyncMessage)allClientMessages.elementAt(i);
				dataTarget = this.getDataTarget(cour);
				if(dataTarget != null && dataTarget.trim().length()>0)
				{
					return dataTarget;
				}
			}
		}
		
		return dataTarget;
	}
	//----Long Object support------------------------------------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public Vector getChunkedCommands()
	{
		if(this.chunkedCommands == null)
		{
			this.chunkedCommands = new Vector();
		}
		return chunkedCommands;
	}

	/**
	 * 
	 * @param chunkedCommands
	 */
	public void setChunkedCommands(Vector chunkedCommands)
	{
		this.chunkedCommands = chunkedCommands;
	}
	
	/**
	 * 
	 * @return
	 */
	public AbstractOperation getChunkSource()
	{
		return chunkSource;
	}

	/**
	 * 
	 * @param chunkSource
	 */
	public void setChunkSource(AbstractOperation chunkSource)
	{
		this.chunkSource = chunkSource;
	}

	/**
	 * 
	 * @return
	 */
	private Vector getChunks()
	{
		if(this.chunks == null)
		{
			this.chunks = new Vector();
		}
		return chunks;
	}
		
	/**
	 * 
	 * @param chunk
	 */
	public void addChunk(AbstractOperation chunk)
	{
		this.getChunks().addElement(chunk);
	}
	
	/**
	 * 
	 * @param chunk
	 */
	public void saveChunkState(AbstractOperation chunk)
	{
		this.addChunk(chunk);
		this.chunkSource = chunk;
	}
	
	/**
	 * 
	 *
	 */
	public void clearChunkState()
	{
		this.getChunks().removeAllElements();
		this.chunkSource = null;
		this.retransmission = false;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isChunkOpen()
	{
		return (!this.getChunks().isEmpty() || this.chunkSource != null || this.retransmission);
	}
	
	/**
	 * 
	 * @return
	 */
	public String reassembleChunks()
	{
		String reassembled = null;
		
		if(this.chunkBackup != null)
		{
			reassembled = this.chunkBackup;
			return reassembled;
		}
		
		StringBuffer buffer = new StringBuffer();		
		Vector chunks = this.getChunks();
		for(int i=0, size=chunks.size();i<size;i++)
		{
			AbstractOperation chunk = (AbstractOperation)chunks.elementAt(i);
			Item item = (Item)chunk.getItems().elementAt(0);
			String data = item.getData().trim();
			buffer.append(data);
		}
		
		reassembled = buffer.toString();
						
		return reassembled;
	}
	
	/**
	 * 
	 * @return
	 */
	public long getTotalSizeOfChunks()
	{
		long totalSize = 0;
		
		AbstractOperation firstChunk = (AbstractOperation)this.getChunks().elementAt(0);
		String meta = firstChunk.getMeta();
		int startIndex = "<Size>".length();
		int endIndex = meta.indexOf("</Size>");
		
		String size = meta.substring(startIndex,endIndex);
		
		totalSize = Long.parseLong(size);
		
		return totalSize;
	}
	
	public long getTotalSizeOfReceivedChunks()
	{
		long totalSize = 0;
				
		String reassembledChunks = this.reassembleChunks().trim();
		totalSize = reassembledChunks.length();
				
		return totalSize;
	}
	
	/**
	 * 
	 *
	 */
	public void performChunkBackup()
	{
		if(!this.getChunks().isEmpty())
		{
			this.chunkBackup = this.reassembleChunks();
		}
	}
	
	/**
	 * 
	 *
	 */
	public void clearChunkBackup()
	{
		this.chunkBackup = null;
	}
	
	/**
	 * 
	 * @return
	 */
	public AbstractOperation getStartingChunk()
	{
		AbstractOperation startingChunk = null;
		
		startingChunk = (AbstractOperation)this.getChunks().elementAt(0);
		
		return startingChunk;
	}
	
	/**
	 * 
	 *
	 */
	public void activateRetransmission()
	{
		this.retransmission = true;
	}
	
	//-----ChangeLog Support------------------------------------------------------------------------------------
	/**
	 * 
	 * @param status
	 * @return
	 */
	public ChangeLogEntry findClientLogEntry(Status status)
	{
		ChangeLogEntry changeLogEntry = null;
		
		String cmd = status.getCmd();
		String messageRef = status.getMsgRef();
		String cmdRef = status.getCmdRef();
		
		Vector messages = null;
		messages = this.getClientSyncPackage().getMessages();
		
		for(int msgCounter=0,size=messages.size();msgCounter<size;msgCounter++)
		{
			SyncMessage message = (SyncMessage)messages.elementAt(msgCounter);
			
			if(!message.getMessageId().equals(messageRef))
			{
				continue;
			}
						
			Vector syncCommands = message.getSyncCommands();
			for(int cmdCounter=0, cmdSize=syncCommands.size();cmdCounter<cmdSize;cmdCounter++)
			{
				SyncCommand command = (SyncCommand)syncCommands.elementAt(cmdCounter);
				String nodeId = command.getSource();				
				
				Vector operationCommands = null;
				if(cmd.equals(SyncXMLTags.Add))
				{
					operationCommands = command.getAddCommands();
				}
				else if(cmd.equals(SyncXMLTags.Replace))
				{
					operationCommands = command.getReplaceCommands();
				}
				else if(cmd.equals(SyncXMLTags.Delete))
				{
					operationCommands = command.getDeleteCommands();
				}				
				
				for(int opCounter=0,opSize=operationCommands.size();opCounter<opSize;opCounter++)
				{
					AbstractOperation op = (AbstractOperation)operationCommands.elementAt(opCounter);
					
					Vector items = op.getItems();
					if(op.getCmdId().equals(cmdRef))
					{
						Item item = (Item)items.elementAt(0);
						
						//We got a match here
						changeLogEntry = new ChangeLogEntry();
						changeLogEntry.setNodeId(nodeId);	
						changeLogEntry.setItem(item);
						return changeLogEntry;
					}
				}
			}
		}
			
		
		return changeLogEntry;
	}		
	//---Operation Command Management---------------------------------------------------------------------------
	/**
	 * 
	 * @param status
	 * @return
	 */
	public AbstractOperation findClientOperationCommand(Status status)
	{
		return this.findOperationCommand(false, status);
	}
	
	/**
	 * 
	 * @param status
	 * @return
	 */
	public AbstractOperation findServerOperationCommand(Status status)
	{
		return this.findOperationCommand(true, status);
	}
	
	/**
	 * 
	 * @param isServer
	 * @param status
	 * @return
	 */
	private AbstractOperation findOperationCommand(boolean isServer, Status status)
	{
		AbstractOperation operationCommand = null;
		
		String cmd = status.getCmd();
		String messageRef = status.getMsgRef();
		String cmdRef = status.getCmdRef();
		
		Vector messages = null;
		if(isServer)
		{
			messages = this.getServerSyncPackage().getMessages();
		}
		else
		{
			messages = this.getClientSyncPackage().getMessages();
		}
		
		for(int msgCounter=0,msgSize=messages.size();msgCounter<msgSize;msgCounter++)
		{
			SyncMessage message = (SyncMessage)messages.elementAt(msgCounter);
			
			if(!message.getMessageId().equals(messageRef))
			{
				continue;
			}
						
			Vector commands = message.getSyncCommands();
			for(int cmdCounter=0,cmdSize=commands.size();cmdCounter<cmdSize;cmdCounter++)
			{
				SyncCommand command = (SyncCommand)commands.elementAt(cmdCounter);
				String nodeId = command.getSource();				
				
				Vector operationCommands = null;
				if(cmd.equals(SyncXMLTags.Add))
				{
					operationCommands = command.getAddCommands();
				}
				else if(cmd.equals(SyncXMLTags.Replace))
				{
					operationCommands = command.getReplaceCommands();
				}
				else if(cmd.equals(SyncXMLTags.Delete))
				{
					operationCommands = command.getDeleteCommands();
				}				
				
				for(int opCounter=0,opSize=operationCommands.size();opCounter<opSize;opCounter++)
				{
					AbstractOperation op = (AbstractOperation)operationCommands.elementAt(opCounter);
					
					if(op.getCmdId().equals(cmdRef))
					{
						operationCommand = op;
						return operationCommand;
					}
				}
			}
		}
		
		return operationCommand;
	}
	
	/**
	 * 
	 * @return
	 */
	public Vector getAllOperationCommands()
	{
		if(this.allOperationCommands == null)
		{
			this.allOperationCommands = new Vector();
		}
		return allOperationCommands;
	}

	/**
	 * 
	 * @param allOperationCommands
	 */
	public void setAllOperationCommands(Vector allOperationCommands)
	{
		this.allOperationCommands = allOperationCommands;
	}

	/**
	 * 
	 * @return
	 */
	public int getOperationCommandIndex()
	{
		return operationCommandIndex;
	}

	/**
	 * 
	 * @param operationCommandIndex
	 */
	public void setOperationCommandIndex(int operationCommandIndex)
	{
		this.operationCommandIndex = operationCommandIndex;
	}
	
	/**
	 * 
	 *
	 */
	public void clearOperationCommandState()
	{
		this.getAllOperationCommands().removeAllElements();
		this.operationCommandIndex = 0;
		this.operationCommandStateInitiated = false;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isOperationCommandStateActive()
	{
		boolean isOperationCommandStateSet = false;
		
		if(!this.getAllOperationCommands().isEmpty())
		{
			isOperationCommandStateSet = true;
		}
		
		return isOperationCommandStateSet;
	}
	
	/**
	 * 
	 *
	 */
	public void initiateOperationCommandState()
	{
		this.operationCommandStateInitiated = true;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isOperationCommandStateInitiated()
	{
		return this.operationCommandStateInitiated;
	}
	//-------Map Support------------------------------------------------------------------------------------------
	/**
	 * 
	 */
	public Hashtable getRecordMap()
	{
		if(this.recordMap == null)
		{
			this.recordMap = new Hashtable();
		}
		return recordMap;
	}

	/**
	 * 
	 * @param recordMap
	 */
	public void setRecordMap(Hashtable recordMap)
	{
		this.recordMap = recordMap;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isMapExchangeInProgress()
	{
		return mapExchangeInProgress;
	}

	/**
	 * 
	 * @param mapExchangeInProgress
	 */
	public void setMapExchangeInProgress(boolean mapExchangeInProgress)
	{
		this.mapExchangeInProgress = mapExchangeInProgress;
	}
	
	/**
	 * 
	 * @param attribute
	 * @return
	 */
	public Object getAttribute(String attribute)
	{
		return this.attributes.get(attribute);
	}
	
	/**
	 * 
	 * @param attribute
	 * @param value
	 */
	public void setAttribute(String attribute, Object value)
	{
		this.attributes.put(attribute, value);
	}
	//-----------------------------------------------------------------------------------------------------------------
	public boolean hasSyncExecutedOnce()
	{
		return hasSyncExecutedOnce;
	}

	public void setHasSyncExecutedOnce(boolean hasSyncExecutedOnce)
	{
		this.hasSyncExecutedOnce = hasSyncExecutedOnce;
	}
}
