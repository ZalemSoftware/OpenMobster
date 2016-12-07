/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.filesystem;

import java.io.OutputStream;

/**
 *
 * @author openmobster@gmail.com
 */
public final class File
{
	private String name;
	private OutputStream os;
	
	public File(String name, OutputStream os)
	{
		this.name = name;
		this.os = os;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public OutputStream getOutputStream()
	{
		return this.os;
	}
}
