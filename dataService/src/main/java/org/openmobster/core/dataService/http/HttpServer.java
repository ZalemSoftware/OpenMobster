/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.dataService.http;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.log4j.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.Channel;

/**
 *
 * @author openmobster@gmail.com
 */
public final class HttpServer 
{
	private static Logger log = Logger.getLogger(HttpServer.class);
	
	private Channel channel;
	
	private String keyStoreLocation = null;
	private String keyStorePassword = null;
	
	public String getKeyStoreLocation()
	{
		return keyStoreLocation;
	}

	public void setKeyStoreLocation(String keyStoreLocation)
	{
		this.keyStoreLocation = keyStoreLocation;
	}

	public String getKeyStorePassword()
	{
		return keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword)
	{
		this.keyStorePassword = keyStorePassword;
	}

	public void start()
	{
		try
		{
			//Configure the server
			NioServerSocketChannelFactory nioFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());
			ServerBootstrap bootstrap = new ServerBootstrap(nioFactory);
			
			// Configure the pipeline factory.
			bootstrap.setPipelineFactory(new HttpServerPipelineFactory(this.getSSLContext()));
			
			//some more options
			bootstrap.setOption("child.tcpNoDelay", true);
	        bootstrap.setOption("child.keepAlive", true);
			
			// Bind and start to accept incoming connections.
			this.channel = bootstrap.bind(new InetSocketAddress(1504));
			
			System.out.println("--------------------------------------------");
	        System.out.println("Netty Http Server successfully loaded on port ("+1504+").....");
	        System.out.println("--------------------------------------------");
		}
		catch(Exception e)
		{
			log.error(this, e);
			this.stop();
			throw new RuntimeException(e);
		}
	}
	
	public void stop()
	{
		this.channel.close().awaitUninterruptibly();
	}
	
	private SSLContext getSSLContext() throws Exception
	{
		SSLContext sslContext = null;
		
		//Setup the KeyStore
		String keyStorePass = this.keyStorePassword;
		if(keyStorePass == null)
		{
			keyStorePass = "";
		}
		
		KeyStore keyStore = KeyStore.getInstance("JKS");
		FileInputStream fis = new FileInputStream(keyStoreLocation);
		keyStore.load(fis, keyStorePass.toCharArray());
				
		//Setup the KeyManagers for private key management
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
		KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, keyStorePass.toCharArray());
		KeyManager[] keyManager = keyManagerFactory.getKeyManagers();
		
		//Setup TrustManagers for handling trusts with other peers
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
		TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(keyStore);
		TrustManager[] trustManager = trustManagerFactory.getTrustManagers();
		
		//Create an SSLContext
		sslContext = SSLContext.getInstance("SSL");
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		sslContext.init(keyManager, trustManager, secureRandom);
		
		return sslContext;
	}
}
