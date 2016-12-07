/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.cloudConnector.api;

import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * @author openmobster@gmail.com
 */
public class Tools 
{
	public static Socket getSocket() throws Exception
	{
		Socket socket = null;
		Configuration configuration = Configuration.getInstance();
		
		//Create a socket
		String serverIp = configuration.getServerIp();
		
		boolean isSecure = configuration.isSSLActivated();
		
		if(isSecure)
		{
			int port = Integer.parseInt(configuration.getSecureServerPort());
			
			//SSL Socket
			SSLContext sslContext = getSSLContext();
			
			
			socket = sslContext.getSocketFactory().createSocket(serverIp, port);
			((SSLSocket)socket).setEnabledCipherSuites(((SSLSocket)socket).getSupportedCipherSuites());
		}
		else
		{
			int port = Integer.parseInt(configuration.getServerPort());
			
			//Plain Socket
			socket = new Socket(serverIp,port);
		}
		
		return socket;
	}
	
	private static SSLContext getSSLContext() throws Exception
	{
		SSLContext sslContext = null;
		
		//Setup the KeyStore
		SecurityConfig securityConfig = Configuration.getInstance().getSecurityConfig();
		String keyStoreLocation = securityConfig.getKeyStoreLocation();
		String keyStorePass = securityConfig.getKeyStorePassword();
		
		KeyStore keyStore = KeyStore.getInstance("JKS");
		FileInputStream fis = new FileInputStream(keyStoreLocation);
		keyStore.load(fis, keyStorePass.toCharArray());
		
		//Setup TrustManagers for handling trusts with other peers
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
		TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(keyStore);
		TrustManager[] trustManager = trustManagerFactory.getTrustManagers();
		
		//Create an SSLContext
		sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, trustManager, null);
		
		return sslContext;
	}
}
