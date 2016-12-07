/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.provider;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.KeyGenerator;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.content.Context;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.util.Base64;
import org.openmobster.core.mobileCloud.android.util.IOUtil;

/**
 *
 * @author openmobster@gmail.com
 */
public class CryptoSecretKeyProvider extends ContentProvider
{
	@Override
	public boolean onCreate()
	{
		return true;
	}
	
	@Override
	public String getType(Uri uri)
	{
		return "vnd.android.cursor.dir/vnd.openmobster.crypto.secret.key";
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection, 
			String[] selectionArgs,
			String sortOrder)
	{
		try
		{	
			MatrixCursor cursor = new MatrixCursor(new String[]{"secret-key"});
			
			String secretKey = this.findSecretKeyOnLocalStorage();
			if(secretKey == null)
			{
				secretKey = this.generateSecretKey();
				this.storeSecretKeyOnLocalStorage(secretKey);
				secretKey = this.findSecretKeyOnLocalStorage();
			}
		
			
			if(secretKey != null && secretKey.trim().length()>0)
			{
				cursor.addRow(new String[]{secretKey});
			}
		
			return cursor;
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
			return null;
		}
	}
	
	@Override
	public Uri insert(Uri arg0, ContentValues contentValues)
	{
		//Not Applicable
		return null;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArguments)
	{
		//Not Applicable
		return 0;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArguments)
	{
		//Not Applicable
		return 0;
	}
	//-----------------------------------------------------------------------------------------------------------------
	private String generateSecretKey() throws Exception
	{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(256);
		byte[] secretKey = keyGenerator.generateKey().getEncoded();
		
		return Base64.encodeBytes(secretKey);
	}
	
	private String findSecretKeyOnLocalStorage() 
	{
		Context context = Registry.getActiveInstance().getContext();
		FileInputStream fis = null;
		try
		{
			fis = context.openFileInput("misc");
			byte[] buffer = IOUtil.read(fis);
			
			return new String(buffer);
		}
		catch(IOException ioe)
		{
			return null;
		}
		finally
		{
			if(fis != null)
			{
				try{fis.close();}catch(Exception e){}
			}
		}
	}
	
	private boolean storeSecretKeyOnLocalStorage(String secretKey)
	{
		Context context = Registry.getActiveInstance().getContext();
		FileOutputStream fos = null;
		try
		{
			fos = context.openFileOutput("misc", Context.MODE_PRIVATE);
			fos.write(secretKey.getBytes());
			fos.flush();
			
			return true;
		}
		catch(IOException ioe)
		{
			return false;
		}
		finally
		{
			if(fos != null)
			{
				try{fos.close();}catch(Exception e){}
			}
		}
	}
}
