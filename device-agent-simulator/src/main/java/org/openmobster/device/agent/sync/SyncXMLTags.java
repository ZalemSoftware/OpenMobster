/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.sync;

/**
 * 
 * @author openmobster@gmail.com
 *
 */
public interface SyncXMLTags
{
	public String Sync = "Sync";
	public String SyncML = "SyncML";
	public String Meta = "Meta";
	public String MaxMsgSize = "MaxMsgSize";
	public String sycml_metinf = "syncml:metinf";
	public String sycml_auth_sha = "syncml:auth-SHA-512";
	public String Cred = "Cred";
	public String Type = "Type";
	public String NextNonce = "NextNonce";
	public String Format = "Format";
	public String Chal = "Chal";
	public String CmdID = "CmdID";
	public String Target = "Target";
	public String Source = "Source";
	public String LocURI = "LocURI";
	public String SyncBody = "SyncBody";
	public String Alert = "Alert";
	public String Delete = "Delete";
	public String Add = "Add";
	public String Replace = "Replace";
	public String Item = "Item";
	public String SyncHdr = "SyncHdr";
	public String VerDTD = "VerDTD";
	public String VerProto = "VerProto";
	public String SessionID = "SessionID";
	public String MsgID = "MsgID";
	public String Status = "Status";
	public String Data = "Data";
	public String Anchor = "Anchor";
	public String Last = "Last";
	public String Next = "Next";
	public String Final = "Final";
	public String MsgRef = "MsgRef";
	public String CmdRef = "CmdRef";
	public String Cmd = "Cmd";
	public String TargetRef = "TargetRef";
	public String SourceRef = "SourceRef";
	public String MoreData = "MoreData";
	public String Archive = "Archive";
	public String SftDel = "SftDel";
	public String NumberOfChanges = "NumberOfChanges";
	public String Map = "Map";
	public String MapItem = "MapItem";
	public String App = "App";
	
	//some codes
	public String TWO_NODE_FAST_SYNC = "200";
	public String TWO_NODE_SLOW_SYNC = "201";
	public String ONE_WAY_CLIENT_SYNC = "202";
	public String ONE_WAY_SERVER_SYNC = "204";
}
