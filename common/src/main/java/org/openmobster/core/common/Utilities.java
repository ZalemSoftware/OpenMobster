/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common;

import java.rmi.server.UID;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;

import org.openmobster.core.common.errors.SystemException;


/**
 * 
 * @author openmobster@gmail.com
 */
public class Utilities
{
	/**
	 * Randomly generates a unique identifier
	 * 
	 * @return the unique identifier
	 */
	public static String generateUID()
	{
		String uid = null;
		
		UID cour = new UID();
		uid = String.valueOf(cour.toString());
		
		return uid;
	}
		
	/**
	 * Performs Base64 encoding on the content of the byte array
	 * 
	 * @param data
	 * @return String representation of the encoded byte array
	 */
	public static String encodeBinaryData(byte[] data)
	{
		String encodedData = null;
		
		encodedData = new String(Base64.encodeBase64(data));
		
		return encodedData;
	}
		
	/**
	 * Decodes a Base64 encoded String object
	 * 
	 * @param encodedData
	 * @return Decoded data in the form of a byte array
	 */
	public static byte[] decodeBinaryData(String encodedData)
	{
		byte[] binaryData = null;
		
		binaryData = Base64.decodeBase64(encodedData.getBytes());
		
		return binaryData;
	}
	
	public static String generateOneWayHash(String input, String knownSalt)
	{
		try
		{
			String onewayHash = null;
			
			MessageDigest digest = MessageDigest.getInstance("SHA-512");		
			String randomSalt = Utilities.generateUID();
			byte[] hashBytes = digest.digest((knownSalt+randomSalt).getBytes());
			onewayHash = Utilities.encodeBinaryData(hashBytes);
			
			return onewayHash;
		}
		catch(Exception e)
		{
			throw new SystemException(e.getMessage(), e);
		}
	}
}
