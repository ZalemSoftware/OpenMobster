/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.filesystem;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileNotFoundException;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;

import android.content.Context;

/**
 *
 * @author openmobster@gmail.com
 */
public final class FileSystem
{
	private static FileSystem singleton;
	
	private FileSystem()
	{
		
	}
	
	public static FileSystem getInstance()
	{
		if(FileSystem.singleton == null)
		{
			synchronized(FileSystem.class)
			{
				if(FileSystem.singleton == null)
				{
					FileSystem.singleton = new FileSystem();
				}
			}
		}
		return FileSystem.singleton;
	}
	
	public InputStream openInputStream(String file)
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			
			file = file.substring("file:///".length());
			
			return context.openFileInput(file);
		}
		catch(FileNotFoundException fne)
		{
			throw new RuntimeException(fne);
		}
	}
	
	public File openOutputStream()
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			
			String file = GeneralTools.generateUniqueId();
			file = file.replaceAll(java.io.File.pathSeparator, "");
			
			OutputStream os = context.openFileOutput(file,Context.MODE_PRIVATE);
			
			File returnValue = new File("file:///"+file,os);
			
			return returnValue;
		}
		catch(FileNotFoundException fne)
		{
			throw new RuntimeException(fne);
		}
	}
	
	public void cleanup(String file)
	{
		Context context = Registry.getActiveInstance().getContext();
		
		file = file.substring("file:///".length());
		
		context.deleteFile(file);
	}
}
