/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.openmobster.core.synchronizer.model.AbstractOperation;
import org.openmobster.core.synchronizer.model.Add;
import org.openmobster.core.synchronizer.model.Alert;
import org.openmobster.core.synchronizer.model.Delete;
import org.openmobster.core.synchronizer.model.Item;
import org.openmobster.core.synchronizer.model.Replace;
import org.openmobster.core.synchronizer.model.Status;
import org.openmobster.core.synchronizer.model.SyncCommand;
import org.openmobster.core.synchronizer.model.SyncMessage;
import org.openmobster.core.synchronizer.model.SyncPackage;
import org.openmobster.core.synchronizer.model.SyncXMLTags;
import org.openmobster.core.synchronizer.server.engine.Anchor;
import org.openmobster.core.synchronizer.server.engine.ChangeLogEntry;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;

import org.jbpm.graph.exe.ProcessInstance;


/**
 * 
 * @author openmobster@gmail.com
 */
public class Session implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1076914456431788519L;
	
	/**
	 * 
	 */
	private ProcessInstance activeProcess = null;
	
	private String sessionId = null; //required
	private String target = null; //required
	private String source = null; //required
	private String app = null; //required
	
	private SyncPackage clientInitPackage = null;
	private SyncPackage serverInitPackage = null;
	
	private SyncPackage clientSyncPackage = null;
	private SyncPackage serverSyncPackage = null;
	
	private SyncPackage clientClosePackage = null;
	private SyncPackage serverClosePackage = null;
	
	private Anchor anchor = null;
	
	/**
	 * some session related meta data
	 */
	private SyncMessage currentMessage = null;
	private int phaseCode = 0;
	private String syncType = null;
	private int maxClientSize = 0;
	
	/**
	 * Data chunk related data
	 */
	private List chunkedCommands = null;
	private List chunks = null;
	private AbstractOperation chunkSource = null;
	private String chunkBackup = null;
	private boolean retransmission = false;
	
	/**
	 * Operation Command related meta data
	 */
	private List allOperationCommands = null;
	private int operationCommandIndex = 0;
	private boolean operationCommandStateInitiated = false;
	
	/**
	 * Map Support related
	 */
	private Map recordMap = null;
	private boolean mapExchangeInProgress = false;
	
	private boolean rollback = false;
	
	/**
	 * BootSync data
	 */
	private List<Add> bootupData;
	
	/**
	 * Normal Sync data
	 */
	private boolean isMultiSyncActive;
	private List syncCommands;
	private SyncCommand syncCommand;
	private int snapshotSize = 0;
	
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
		
		this.phaseCode = SyncServer.PHASE_INIT;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getApp()
	{
		return app;
	}

	/**
	 * 
	 * @param app
	 */
	public void setApp(String app)
	{
		this.app = app;
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
	
	/**
	 * 
	 * @return
	 */
	public ProcessInstance getActiveProcess() 
	{
		return activeProcess;
	}

	/**
	 * 
	 * @param activeProcess
	 */
	public void setActiveProcess(ProcessInstance activeProcess) 
	{
		this.activeProcess = activeProcess;
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
	 * @param syncAdapter
	 * @return
	 */
	public String getDataSource(boolean searchServerMessages)
	{
		String dataSource = null;
		
		if(searchServerMessages)
		{
			List allServerMessages = new ArrayList();
			allServerMessages.addAll(this.serverInitPackage.getMessages());
			allServerMessages.addAll(this.serverSyncPackage.getMessages());
			
			for(int i=0;i<allServerMessages.size();i++)
			{
				SyncMessage cour = (SyncMessage)allServerMessages.get(i);
				dataSource = this.getDataSource(cour);
				if(dataSource != null && dataSource.trim().length()>0)
				{
					return dataSource;
				}
			}			
		}
		else
		{
			List allClientMessages = new ArrayList();
			allClientMessages.addAll(this.clientInitPackage.getMessages());
			allClientMessages.addAll(this.clientSyncPackage.getMessages());
			
			for(int i=0;i<allClientMessages.size();i++)
			{
				SyncMessage cour = (SyncMessage)allClientMessages.get(i);
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
			List allServerMessages = new ArrayList();
			allServerMessages.addAll(this.serverInitPackage.getMessages());
			allServerMessages.addAll(this.serverSyncPackage.getMessages());
			
			for(int i=0;i<allServerMessages.size();i++)
			{
				SyncMessage cour = (SyncMessage)allServerMessages.get(i);
				dataTarget = this.getDataTarget(cour);
				if(dataTarget != null && dataTarget.trim().length()>0)
				{
					return dataTarget;
				}
			}			
		}
		else
		{
			List allClientMessages = new ArrayList();
			allClientMessages.addAll(this.clientInitPackage.getMessages());
			allClientMessages.addAll(this.clientSyncPackage.getMessages());
			
			for(int i=0;i<allClientMessages.size();i++)
			{
				SyncMessage cour = (SyncMessage)allClientMessages.get(i);
				dataTarget = this.getDataTarget(cour);
				if(dataTarget != null && dataTarget.trim().length()>0)
				{
					return dataTarget;
				}
			}
		}
		
		return dataTarget;
	}
	
	/**
	 * 
	 * @param message
	 * @return
	 */
	private String getDataSource(SyncMessage message)
	{
		String dataSource = null;
		
		for(int i=0;i<message.getAlerts().size();i++)
		{
			Alert alert = (Alert)message.getAlerts().get(i);
			for(int j=0;j<alert.getItems().size();j++)
			{
				Item item = (Item)alert.getItems().get(j);
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
		
		for(int i=0;i<message.getAlerts().size();i++)
		{
			Alert alert = (Alert)message.getAlerts().get(i);
			for(int j=0;j<alert.getItems().size();j++)
			{
				Item item = (Item)alert.getItems().get(j);
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
	
	//-------Log Entry related functions----------------------------------------------------------------------------	
	/**
	 * 
	 * @param isServer
	 * @param status
	 * @return
	 */
	public ChangeLogEntry findLogEntry(Status status)
	{
		ChangeLogEntry changeLogEntry = null;
		
		String cmd = status.getCmd();
		String messageRef = status.getMsgRef();
		String cmdRef = status.getCmdRef();
		
		List messages = null;
		messages = this.getServerSyncPackage().getMessages();
		
		for(int msgCounter=0;msgCounter<messages.size();msgCounter++)
		{
			SyncMessage message = (SyncMessage)messages.get(msgCounter);
			
			if(!message.getMessageId().equals(messageRef))
			{
				continue;
			}
						
			for(int cmdCounter=0;cmdCounter<message.getSyncCommands().size();cmdCounter++)
			{
				SyncCommand command = (SyncCommand)message.getSyncCommands().get(cmdCounter);
				String nodeId = command.getSource();				
				
				List operationCommands = null;
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
				
				for(int opCounter=0;opCounter<operationCommands.size();opCounter++)
				{
					AbstractOperation op = (AbstractOperation)operationCommands.get(opCounter);
					
					if(op.getCmdId().equals(cmdRef))
					{
						Item item = (Item)op.getItems().get(0);
						
						//We got a match here
						changeLogEntry = new ChangeLogEntry();
						changeLogEntry.setNodeId(nodeId);	
						changeLogEntry.setItem(item);
						if(op instanceof Add)
						{
							changeLogEntry.setOperation(ServerSyncEngine.OPERATION_ADD);
						}
						else if(op instanceof Replace)
						{
							changeLogEntry.setOperation(ServerSyncEngine.OPERATION_UPDATE);
						}
						else if(op instanceof Delete)
						{
							changeLogEntry.setOperation(ServerSyncEngine.OPERATION_DELETE);
						}
						return changeLogEntry;
					}
				}
			}
		}
			
		
		return changeLogEntry;
	}	
	//---------Command search related functions--------------------------------------------------------------------	
	/**
	 * 
	 * @param isServer
	 * @param status
	 * @return
	 */
	public AbstractOperation findOperationCommand(Status status)
	{
		AbstractOperation operationCommand = null;
		
		String cmd = status.getCmd();
		String messageRef = status.getMsgRef();
		String cmdRef = status.getCmdRef();
		
		List messages = null;
		messages = this.getServerSyncPackage().getMessages();
		
		for(int msgCounter=0;msgCounter<messages.size();msgCounter++)
		{
			SyncMessage message = (SyncMessage)messages.get(msgCounter);
			
			if(!message.getMessageId().equals(messageRef))
			{
				continue;
			}
						
			for(int cmdCounter=0;cmdCounter<message.getSyncCommands().size();cmdCounter++)
			{
				SyncCommand command = (SyncCommand)message.getSyncCommands().get(cmdCounter);
				String nodeId = command.getSource();				
				
				List operationCommands = null;
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
				
				for(int opCounter=0;opCounter<operationCommands.size();opCounter++)
				{
					AbstractOperation op = (AbstractOperation)operationCommands.get(opCounter);
					
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
	//-----------Data Chunk Management related commands-----------------------------------------------------------
	/**
	 * 
	 * @return
	 */
	public List getChunkedCommands()
	{
		if(this.chunkedCommands == null)
		{
			this.chunkedCommands = new ArrayList();
		}
		return chunkedCommands;
	}

	/**
	 * 
	 * @param chunkedCommands
	 */
	public void setChunkedCommands(List chunkedCommands)
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
	private List getChunks()
	{
		if(this.chunks == null)
		{
			this.chunks = new ArrayList();
		}
		return chunks;
	}
		
	/**
	 * 
	 * @param chunk
	 */
	public void addChunk(AbstractOperation chunk)
	{
		this.getChunks().add(chunk);
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
		this.getChunks().clear();
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
		for(int i=0;i<this.getChunks().size();i++)
		{
			AbstractOperation chunk = (AbstractOperation)this.getChunks().get(i);
			Item item = (Item)chunk.getItems().get(0);
			String data = item.getData();
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
		
		AbstractOperation firstChunk = (AbstractOperation)this.getChunks().get(0);
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
				
		String reassembledChunks = this.reassembleChunks();
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
		
		startingChunk = (AbstractOperation)this.getChunks().get(0);
		
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
	//---------OperationCommand queue management related functions-----------------------------------------------
	/**
	 * 
	 * @return
	 */
	public List getAllOperationCommands()
	{
		if(this.allOperationCommands == null)
		{
			this.allOperationCommands = new ArrayList();
		}
		return allOperationCommands;
	}

	/**
	 * 
	 * @param allOperationCommands
	 */
	public void setAllOperationCommands(List allOperationCommands)
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
		this.getAllOperationCommands().clear();
		this.operationCommandIndex = 0;
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
	//--------Map support----------------------------------------------------------------------------------------
	/**
	 * 
	 */
	public Map getRecordMap()
	{
		if(this.recordMap == null)
		{
			this.recordMap = new HashMap();
		}
		return recordMap;
	}

	/**
	 * 
	 * @param recordMap
	 */
	public void setRecordMap(Map recordMap)
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
	 * @return
	 */
	public String getDeviceId()
	{
		return this.getSource();
	}
	
	public String getChannel()
	{
		return this.getDataSource(false);
	}

	public boolean isRollback()
	{
		return rollback;
	}

	public void setRollback(boolean rollback)
	{
		this.rollback = rollback;
	}
	//------------------------------------------------------------------------------------------------------------------------
	public List<Add> getBootupData()
	{
		return bootupData;
	}

	public void setBootupData(List<Add> bootupData)
	{
		this.bootupData = bootupData;
	}
	
	public Add getBootupObject()
	{
		if(this.bootupData != null && !this.bootupData.isEmpty())
		{
			Add object = this.bootupData.get(0);
			this.bootupData.remove(object);
			
			return object;
		}
		return null;
	}
	
	public boolean isBootupDataFinished()
	{
		if(this.bootupData == null || this.bootupData.isEmpty())
		{
			this.bootupData = null;
			return true;
		}
		return false;
	}
	
	public boolean isBootupDataActive()
	{
		if(this.bootupData != null)
		{
			return true;
		}
		return false;
	}
	//------------------------------------------------------------------------------------------------------------------------------

	public boolean isMultiSyncActive()
	{
		return isMultiSyncActive;
	}

	public void setMultiSyncActive(boolean isMultiSyncActive)
	{
		this.isMultiSyncActive = isMultiSyncActive;
	}

	public List getSyncCommands()
	{
		return syncCommands;
	}

	public void setSyncCommands(List syncCommands)
	{
		this.syncCommands = syncCommands;
	}
	
	public AbstractOperation getNextOperation()
	{
		if(this.syncCommands != null && !this.syncCommands.isEmpty())
		{
			AbstractOperation object = (AbstractOperation)this.syncCommands.get(0);
			this.syncCommands.remove(object);
			
			return object;
		}
		return null;
	}
	
	public boolean isOperationSyncFinished()
	{
		if(this.syncCommands == null || this.syncCommands.isEmpty())
		{
			this.syncCommands = null;
			return true;
		}
		return false;
	}
	
	public boolean isOperationSyncActive()
	{
		if(this.syncCommands != null)
		{
			return true;
		}
		return false;
	}

	public SyncCommand getSyncCommand()
	{
		return syncCommand;
	}

	public void setSyncCommand(SyncCommand syncCommand)
	{
		this.syncCommand = syncCommand;
	}
	
	public int getSnapshotSize()
	{
		return this.snapshotSize;
	}
	
	public void setSnapshotSize(int snapshotSize)
	{
		this.snapshotSize = snapshotSize;
	}
	
	public boolean isSnapShotSizeSet()
	{
		if(this.snapshotSize > 0)
		{
			return true;
		}
		return false;
	}
}
