/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.openmobster.cloud.api.ExecutionContext;
import org.openmobster.core.synchronizer.SyncException;
import org.openmobster.core.synchronizer.model.*;
import org.openmobster.core.synchronizer.server.engine.ServerSyncEngine;
import org.openmobster.core.synchronizer.server.workflow.WorkflowConstants;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance; 
import org.jbpm.graph.exe.Token;
import org.jbpm.context.exe.ContextInstance;


/**
 * @author openmobster@gmail.com
 */
public class SyncJBPMServerAdapter implements SyncServer
{
	private static Logger logger = Logger.getLogger(SyncJBPMServerAdapter.class);
		
	/**
	 * objects helping out
	 */
	protected SyncObjectGenerator syncObjectGenerator = null;
	protected SyncXMLGenerator syncXMLGenerator = null;
	protected ServerSyncEngine syncEngine = null;
	protected SessionManager sessionManager = null;
	
	/**
	 * 
	 */
	protected ProcessDefinition definition = null;
	
	/**
	 * 
	 *
	 */
	public SyncJBPMServerAdapter()
	{
		super();
		this.syncObjectGenerator = new SyncObjectGenerator();
		this.syncXMLGenerator = new SyncXMLGenerator();	
	}
	
	//-----Microcontainer management related methods--------------------------------------------------------------
	/**
	 * 
	 *
	 */
	public void start()
	{
		InputStream is = null;
		try
		{
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream("sync.xml");
			this.definition = ProcessDefinition.parseXmlInputStream(is);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if(is != null)
			{
				try{is.close();}catch(Exception e){}
			}
		}
	}
	
	/**
	 * 
	 *
	 */
	public void stop()
	{
		
	}
	
	/**
	 * 
	 * @return
	 */
	public ServerSyncEngine getServerSyncEngine() 
	{
		return syncEngine;
	}

	/**
	 * 
	 * @param syncEngine
	 */
	public void setServerSyncEngine(ServerSyncEngine syncEngine) 
	{
		this.syncEngine = syncEngine;
	}
	
	
	public SessionManager getSessionManager() 
	{
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) 
	{
		this.sessionManager = sessionManager;
	}
	//------------------------------------------------------------------------------------------------------------
	/**
	 * 
	 * @param request
	 */
	public SyncAdapterResponse service(SyncAdapterRequest request) throws SyncException
	{		
		try
		{
			SyncAdapterResponse response = new SyncAdapterResponse();
			
			//Grab the payload
			String payload = (String)request.getAttribute(SyncServer.PAYLOAD);
			
			//Turn xml into object representation
			Session localSession = this.syncObjectGenerator.parseCurrentSyncMessage(payload);
			SyncMessage localMessage = localSession.getCurrentMessage();
			
			//Perform SessionManagement
			if(this.sessionManager.findSession(localSession.getSessionId()) == null)
			{
				//This establishes the session
				this.sessionManager.saveSession(localSession);											
			}
							
			//Process localMessage and update session data accordingly
			Session session = this.sessionManager.findSession(localSession.getSessionId());
			session.setCurrentMessage(localMessage);
			
			//Place session as a Thread Local object to be consumed by other
			//components in the call stack
			SyncContext.getInstance().setSession(session);
			ExecutionContext.getInstance().setSyncContext(SyncContext.getInstance());
			
			String responsePayload = this.process(session);
			response.setAttribute(SyncServer.PAYLOAD, responsePayload);
			
			//Check for tx-rollback
			if(session.isRollback())
			{
				response.setAttribute("tx-rollback", Boolean.TRUE);
				session.setRollback(false); //clear the tx flag
			}
									
			return response;
		}
		catch(Exception e)
		{
			logger.error(this, e);
			throw new SyncException(e);
		}
	}
	
	/**
	 * 
	 * @param session
	 * @return
	 */
	private String process(Session session)
	{
		String payload = "";
		
		//Create a Process instance if one does not exist and place it into session
		ProcessInstance activeProcess = session.getActiveProcess();
		if(activeProcess == null)
		{
			activeProcess = definition.createProcessInstance();
			session.setActiveProcess(activeProcess);
		}
		
		//Set session as a transient context variable
		ContextInstance context = activeProcess.getContextInstance();
		context.setTransientVariable(VariableConstants.session, session);
		context.setTransientVariable(VariableConstants.syncEngine, this.syncEngine);
		context.setTransientVariable(VariableConstants.syncXMLGenerator, this.syncXMLGenerator);
		context.setTransientVariable(VariableConstants.syncObjectGenerator, this.syncObjectGenerator);
		Token rootToken = activeProcess.getRootToken();
		
		//update Client message cache
		String currentNode = rootToken.getNode().getName(); 
		if(currentNode == null ||
		   currentNode.equals(WorkflowConstants.initialize)
		)
		{
			session.getClientInitPackage().addMessage(session.getCurrentMessage());
		}
		else if(currentNode.startsWith(WorkflowConstants.synchronize)				
		)
		{
			session.getClientSyncPackage().addMessage(session.getCurrentMessage());
		}
		else if(currentNode.equals(WorkflowConstants.close)				
		)
		{
			session.getClientClosePackage().addMessage(session.getCurrentMessage());
		}
		
		
		//move the process further		
		rootToken.signal();
		
		//Check if wait state is synchronize. If it is, move token to the next wait state
		if(rootToken.getNode().getName().equals(this.definition.getNode("synchronize").getName()))
		{
			rootToken.signal();
		}
		
		//process the payload
		payload = (String)context.getTransientVariable(VariableConstants.payload);
		if(payload == null)
		{
			payload = "";
		}
		
		return payload;
	}
}
