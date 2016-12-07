/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server.engine;

import org.openmobster.core.synchronizer.model.AbstractOperation;
import org.openmobster.core.synchronizer.model.Item;

/**
 * 
 * @author openmobster@gmail.com
 */
public interface SyncRecord
{
	
	/**
	 * 
	 * @param operation
	 * @return
	 */
	public AbstractOperation getCommandInfo(int messageSize,String operation);
	
	/**
	 * 
	 * @param item
	 */
	public void setItem(Item item);	
}
