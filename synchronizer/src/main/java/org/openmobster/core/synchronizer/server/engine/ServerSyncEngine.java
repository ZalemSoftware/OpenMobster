/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server.engine;

import java.util.List;
import java.util.Map;


import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.core.synchronizer.model.SyncCommand;
import org.openmobster.core.synchronizer.model.Add;

/**
 * @author openmobster@gmail.com
 */
public interface ServerSyncEngine 
{
	/**
	 * 
	 */
	public static final String OPERATION_ADD = "Add";

	public static final String OPERATION_UPDATE = "Replace";

	public static final String OPERATION_DELETE = "Delete";
	
	public static final String OPERATION_MAP = "Map";	
	
	public List getSlowSyncCommands(int messageSize, String pluginId);
	
	public List processSlowSyncCommand(org.openmobster.core.synchronizer.server.Session session,
			String pluginId, SyncCommand syncCommand);
	
	public List getAddCommands(int messageSize,String pluginId, String syncType);
	
	public List getReplaceCommands(int messageSize,String pluginId, String syncType);
	
	public List getDeleteCommands(String pluginId, String syncType);
	
	public List processSyncCommand(org.openmobster.core.synchronizer.server.Session session,
	String pluginId, SyncCommand syncCommand);
	
	public Add getStream(org.openmobster.core.synchronizer.server.Session session,
			String pluginId, SyncCommand syncCommand);
	
	public List<Add> processBootSync(org.openmobster.core.synchronizer.server.Session session,
			String pluginId);
	
	public void addChangeLogEntries(String target, String app, List entries);
	
	public List getChangeLog(String target, String nodeId, String app, String operation);
	
	public void clearChangeLogEntry(String target, String app, ChangeLogEntry logEntry);
	
	public void clearChangeLog(String target, String service, String app);
	
	public boolean changeLogEntryExists(ChangeLogEntry entry);
		
	public String marshal(MobileBean record);	
	
	public Anchor getAnchor(String target,String app);
	
	public void updateAnchor(Anchor anchor);
	
	public void deleteAnchor(String target,String app);
	
	public void saveRecordMap(String source, String target, Map recordMap);	
	
	public void clearRecordMap();
	
	public void clearConflictEngine();
}
