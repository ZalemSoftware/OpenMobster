/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server.workflow;

/**
 * @author openmobster@gmail.com
 */
public interface WorkflowConstants 
{
	public String proceed = "proceed";
	public String goback = "goback";
	public String initialize = "initialize";
	public String synchronize = "synchronize";
	public String normalSync = "synchronize:normalSync";
	public String streamSync = "synchronize:streamSync";
	public String bootSync = "synchronize:bootSync";
	public String chunkAccepted = "synchronize:chunkAccepted";
	public String closeChunk = "synchronize:closeChunk";
	public String nextMessage = "synchronize:nextMessage";
	public String mapExchange = "synchronize:mapExchange";
	public String performMapExchange = "synchronize:performMapExchange";
	public String close = "close";
	public String end = "end";
}
