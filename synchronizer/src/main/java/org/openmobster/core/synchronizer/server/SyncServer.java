/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server;

import org.openmobster.core.synchronizer.SyncException;
import org.openmobster.core.synchronizer.model.SyncAdapterRequest;
import org.openmobster.core.synchronizer.model.SyncAdapterResponse;

/**
 * @author openmobster@gmail.com
 */
public interface SyncServer 
{
	/**
	 * Keys for attributes carried on the incoming request objects
	 */
	public static final String PAYLOAD = "payload"; //represents the xml payload
	public static final String SOURCE = "source";
	public static final String TARGET = "target";
	public static final String MAX_CLIENT_SIZE = "maxClientSize";
	public static final String CLIENT_INITIATED = "clientInitiated";
	public static final String DATA_SOURCE = "dataSource";
	public static final String DATA_TARGET = "dataTarget";
	public static final String SYNC_TYPE = "syncType";
	
	/**
	 * Synchronization related codes
	 */
	public static final String TWO_WAY = "200";
	public static final String SLOW_SYNC = "201";
	public static final String ONE_WAY_CLIENT = "202";
	public static final String ONE_WAY_SERVER = "204";
	public static final String SUCCESS = "200";
	public static final String AUTH_SUCCESS = "202";
	public static final String COMMAND_FAILURE = "500";
	public static final String ANCHOR_FAILURE = "508";
	public static final String CHUNK_ACCEPTED = "213";
	public static final String CHUNK_SUCCESS = "201";
	public static final String NEXT_MESSAGE = "222";	
	public static final String SIZE_MISMATCH = "424";
	public static final String GENERIC_SYNC_FAILURE = "500";
	public static final String AUTHENTICATION_SUCCESS = "212";
	public static final String AUTHENTICATION_FAILURE = "401";
	
	/**
	 * Sync Engine extension
	 */
	public static final String STREAM = "250";
	public static final String STREAM_RECORD_ID = "streamRecordId";
	public static final String BOOT_SYNC = "260";
	public static final String OPTIMISTIC_LOCK_ERROR = "501";
	
	
	/**
	 * Status codes carried by the response object
	 */
	public static final int RESPONSE_CLOSE = 1;
	
	/**
	 * Synchronization Phase Codes
	 */
	public static final int PHASE_INIT = 1;
	public static final int PHASE_SYNC = 2;
	public static final int PHASE_CLOSE = 3;
	public static final int PHASE_END = 4;
	
	/**
	 * 
	 * @param request
	 * @return
	 * @throws SyncException
	 */
	public SyncAdapterResponse service(SyncAdapterRequest request) throws SyncException;
	
}
