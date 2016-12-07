/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.connection;

import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.content.Context;

import org.openmobster.core.mobileCloud.android.configuration.Configuration;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.service.Service;

/**
 * @author openmobster@gmail.com
 *
 */
public final class NetworkConnector extends Service 
{	
	public NetworkConnector()
	{
		
	}
	
	public void start()
	{		
	}
	
	public void stop()
	{		
	}
	
	public static NetworkConnector getInstance()
	{
		return (NetworkConnector)Registry.getActiveInstance().lookup(NetworkConnector.class);
	}
	//--------------------------------------------------------------------------------------------------------------------------------------------
	public NetSession openSession(boolean secure) throws NetworkException
	{
		Context context = Registry.getActiveInstance().getContext();
		Configuration conf = Configuration.getInstance(context);
		
		String serverIp = conf.getServerIp();
		if(serverIp == null || serverIp.trim().length() == 0)
		{
			throw new IllegalStateException("device_inactive");
		}						
		
		try
		{
			NetSession session = null;
			
			Socket socket = this.openSocket(conf,secure);
			session = new NetSession(socket);
			
			return session;
		}
		catch(Exception ioe)
		{
			ioe.printStackTrace(System.out);
			
			throw new NetworkException(this.getClass().getName(), "openSession", new Object[]{
				"Secure="+secure,
				"Message="+ioe.getMessage()
			});
		}
	}	
	
	private Socket openSocket(Configuration conf,boolean secure) throws Exception
	{
		Socket socket = null;
		
		String serverIp = conf.getServerIp();
		if(secure && conf.isSSLActivated())
		{
			int port = Integer.parseInt(conf.getSecureServerPort());
			
			//SSL Socket
			SSLContext sslContext = getSSLContext();
			
			
			socket = sslContext.getSocketFactory().createSocket(serverIp, 
			port);
			((SSLSocket)socket).setEnabledCipherSuites(((SSLSocket)socket).
			getSupportedCipherSuites());
		}
		else
		{
			int port = Integer.parseInt(conf.getPlainServerPort());
			socket = new Socket(serverIp,port);
		}
		
		return socket;
	}
	
	private SSLContext getSSLContext() throws Exception
	{
		SSLContext sslContext = null;
		
		TrustManager[] trustManager = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                            String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                            String authType) throws CertificateException {
            }
		} };

		
		//Create an SSLContext
		sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, trustManager, null);
		
		return sslContext;
	}
}
