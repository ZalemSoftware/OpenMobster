/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.crypto;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.crypto.KeyGenerator;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.util.Base64;
import org.openmobster.core.mobileCloud.android.util.IOUtil;

/**
 *
 * @author openmobster@gmail.com
 */
public final class CryptoManager
{
	private static CryptoManager singleton;
	
	private CryptoManager()
	{
		
	}
	
	public static CryptoManager getInstance()
	{
		if(CryptoManager.singleton == null)
		{
			synchronized(CryptoManager.class)
			{
				if(CryptoManager.singleton == null)
				{
					CryptoManager.singleton = new CryptoManager();
				}
			}
		}
		return CryptoManager.singleton;
	}
	
	public void start()
	{
		try
		{
			String secretKey = this.findSecretKeyOnLocalStorage();
			if(secretKey == null)
			{
				secretKey = this.generateSecretKey();
				this.storeSecretKeyOnLocalStorage(secretKey);
				secretKey = this.findSecretKeyOnLocalStorage();
			}
			
			//set the secret key for the Cryptographer
			byte[] secretKeyBytes = Base64.decode(secretKey);
			Cryptographer.getInstance().setSecretKey(secretKeyBytes);
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
			this.stop();
		}
	}
	
	public void stop()
	{
		Cryptographer.stop();
		CryptoManager.singleton = null;
	}
	//---------------------------------------------------------------------------------------------------------------------
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
