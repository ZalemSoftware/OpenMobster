/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common.deploy;

/**
 * @author openmobster@gmail.com
 */
public interface BootLoaderMBean
{		
	public void start() throws Exception;
		
	public void stop() throws Exception;
}
