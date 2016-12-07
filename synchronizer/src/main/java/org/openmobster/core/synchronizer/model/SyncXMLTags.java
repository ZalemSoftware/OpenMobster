/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.model;

/**
 * 
 * @author openmobster@gmail.com
 */
public interface SyncXMLTags
{
	public final String Sync = "Sync";
	public final String SyncML = "SyncML";
	public final String Meta = "Meta";
	public final String MaxMsgSize = "MaxMsgSize";
	public final String sycml_metinf = "syncml:metinf";
	public final String sycml_auth_sha = "syncml:auth-SHA-512";
	public final String Cred = "Cred";
	public final String Type = "Type";
	public final String NextNonce = "NextNonce";
	public final String Format = "Format";
	public final String Chal = "Chal";
	public final String CmdID = "CmdID";
	public final String Target = "Target";
	public final String Source = "Source";
	public final String LocURI = "LocURI";
	public final String SyncBody = "SyncBody";
	public final String Alert = "Alert";
	public final String Delete = "Delete";
	public final String Add = "Add";
	public final String Replace = "Replace";
	public final String Item = "Item";
	public final String SyncHdr = "SyncHdr";
	public final String VerDTD = "VerDTD";
	public final String VerProto = "VerProto";
	public final String SessionID = "SessionID";
	public final String MsgID = "MsgID";
	public final String Status = "Status";
	public final String Data = "Data";
	public final String Anchor = "Anchor";
	public final String Last = "Last";
	public final String Next = "Next";
	public final String Final = "Final";
	public final String MsgRef = "MsgRef";
	public final String CmdRef = "CmdRef";
	public final String Cmd = "Cmd";
	public final String TargetRef = "TargetRef";
	public final String SourceRef = "SourceRef";
	public final String MoreData = "MoreData";
	public final String Archive = "Archive";
	public final String SftDel = "SftDel";
	public final String NumberOfChanges = "NumberOfChanges";
	public final String Map = "Map";
	public final String MapItem = "MapItem";
	public final String App = "App";
	
	//some codes
	public final String TWO_NODE_FAST_SYNC = "200";
	public final String TWO_NODE_SLOW_SYNC = "201";
	public final String ONE_WAY_CLIENT_SYNC = "202";
	public final String ONE_WAY_SERVER_SYNC = "204";
}
