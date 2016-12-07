/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * @author openmobster@gmail.com
 *
 */
public final class IOUtil 
{
	public static byte[] read(InputStream is) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try
		{
			byte[] buffer = new byte[100];
			while(true)
			{
				int number_of_bytes = is.read(buffer);
				if(number_of_bytes == -1)
				{
					break;
				}
				bos.write(buffer, 0, number_of_bytes);
			}
			return bos.toByteArray();
		}
		finally
		{
			bos.close();
			is.close();
		}
	}
}
