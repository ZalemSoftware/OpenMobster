/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.cmdline.shell;

import org.apache.geronimo.gshell.branding.VersionLoader;

/**
 * @author openmobster@gmail.com
 */
public class ConsoleVersionLoader implements VersionLoader 
{
	private String version;
	
	public ConsoleVersionLoader()
	{
		
	}
	
	public String getVersion() 
	{
		return this.version;
	}
	
	public void setVersion(String version)
	{
		this.version = version;
	}
}
