/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.util.Base64;

/**
 *
 * @author openmobster@gmail.com
 */
public final class Cryptographer
{
	private static Cryptographer singleton;
	
	private byte[] secretKey;
	
	private Cryptographer()
	{
		
	}
	
	public static Cryptographer getInstance()
	{
		if(Cryptographer.singleton == null)
		{
			synchronized(Cryptographer.class)
			{
				if(Cryptographer.singleton == null)
				{
					Cryptographer.singleton = new Cryptographer();
				}
			}
		}
		return Cryptographer.singleton;
	}
	
	public static void stop()
	{
		Cryptographer.singleton = null;
	}
	
	void setSecretKey(byte[] secretKey)
	{
		this.secretKey = secretKey;
	}
	//----------------------------------------------------------------------------------------
	public String encrypt(byte[] data)
	{
		if(this.secretKey == null)
		{
			throw new IllegalStateException("Secret Key required for cryptography");
		}
		try
		{
			//AES encription
		    Cipher c = Cipher.getInstance("AES");
		    SecretKeySpec k = new SecretKeySpec(this.secretKey, "AES");
		    c.init(Cipher.ENCRYPT_MODE, k);
		    byte[] encryptedBytes = c.doFinal(data);
		    return Base64.encodeBytes(encryptedBytes);
		}
		catch(Throwable t)
		{
			SystemException sys = new SystemException(Cryptographer.class.getName(), "encrypt", new String[]{
				"Throwable: "+t.toString(),
				"Message: "+t.getMessage()
			});
			throw sys;
		}
	}
	
	public String decrypt(String encoded)
	{
		if(this.secretKey == null)
		{
			throw new IllegalStateException("Secret Key required for cryptography");
		}
		try
		{
			Cipher c = Cipher.getInstance("AES");
			SecretKeySpec k = new SecretKeySpec(this.secretKey, "AES");
			c.init(Cipher.DECRYPT_MODE, k);
			
			byte[] encryptedBytes = Base64.decode(encoded);
			
			byte[] decryptedBytes = c.doFinal(encryptedBytes);
			
			return new String(decryptedBytes);
		}
		catch(Throwable t)
		{
			SystemException sys = new SystemException(Cryptographer.class.getName(), "decrypt", new String[]{
				"Throwable: "+t.toString(),
				"Message: "+t.getMessage()
			});
			throw sys;
		}
	}
}
