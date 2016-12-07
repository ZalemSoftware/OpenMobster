/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dataService.server;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import java.io.FileInputStream;

import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.log4j.Logger;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.ssl.SslFilter;

import org.apache.mina.transport.socket.nio.NioSocketAcceptor;


import org.openmobster.core.cluster.ClusterEvent;
import org.openmobster.core.cluster.ClusterService;
import org.openmobster.core.cluster.ClusterListener;


/**
 * 
 * @author openmobster@gmail.com
 */
public class Server implements ClusterListener
{	
	private static Logger log = Logger.getLogger(Server.class);
		
	private IoAcceptor acceptor = null;
		
	private ServerHandler handler = null;
		
	protected int port = 1500; //default port if not overriden from configuration
		
	private String keyStoreLocation = null;
		
	private String keyStorePassword = null;
	
	private TransactionFilter transactionFilter;
	private AuthenticationFilter authenticationFilter;
	private PayloadFilter payloadFilter;
	private RequestConstructionFilter requestFilter;
	
	private ClusterService clusterService;
		
	public Server()
	{
		
	}
		
	public void start() throws RuntimeException
	{
		this.clusterService.register(this);  
	}
		
	public void stop() throws RuntimeException
	{
		this.acceptor.unbind();
		
		this.acceptor = null;
		this.handler = null;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
	
	public ServerHandler getHandler()
	{
		return handler;
	}
	
	public void setHandler(ServerHandler handler)
	{
		this.handler = handler;
	}
		
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
	
	
	public AuthenticationFilter getAuthenticationFilter() 
	{
		return authenticationFilter;
	}

	public void setAuthenticationFilter(AuthenticationFilter authenticationFilter) 
	{
		this.authenticationFilter = authenticationFilter;
	}
	
	
	public TransactionFilter getTransactionFilter() 
	{
		return transactionFilter;
	}

	public void setTransactionFilter(TransactionFilter transactionFilter) 
	{
		this.transactionFilter = transactionFilter;
	}
		
	public PayloadFilter getPayloadFilter() 
	{
		return payloadFilter;
	}

	public void setPayloadFilter(PayloadFilter payloadFilter) 
	{
		this.payloadFilter = payloadFilter;
	}
	
	public RequestConstructionFilter getRequestFilter()
	{
		return requestFilter;
	}

	public void setRequestFilter(RequestConstructionFilter requestFilter)
	{
		this.requestFilter = requestFilter;
	}
	
	public ClusterService getClusterService()
	{
		return clusterService;
	}

	public void setClusterService(ClusterService clusterService)
	{
		this.clusterService = clusterService;
	}

	//---------------------------------------------------------------------------------------------------
	public boolean isSecure()
	{
		return (this.keyStoreLocation != null && this.keyStoreLocation.trim().length()>0);
	}
	protected void startListening(boolean activateSSL)
	{
		try
		{
			this.acceptor = new NioSocketAcceptor();
	        ((NioSocketAcceptor)this.acceptor).setReuseAddress(true);
	       
	        if(activateSSL)
	        {
	        	//Makes the tcp connection protected with SSL
	        	SslFilter sslFilter = new SslFilter(this.getSSLContext());	        
	        	this.acceptor.getFilterChain().addLast("ssl", sslFilter);
	        }
	        
	        //
	        //TextLineCodecFactory textLine = new TextLineCodecFactory(Charset.forName("UTF-8"));	
	        TextLineCodecFactory textLine = new TextLineCodecFactory(Charset.forName("UTF-8"),
	    	        LineDelimiter.UNIX.getValue(),
	    	        "EOF");
	        textLine.setDecoderMaxLineLength(Integer.MAX_VALUE);
	        textLine.setEncoderMaxLineLength(Integer.MAX_VALUE);
	        ProtocolCodecFilter codecFilter = new ProtocolCodecFilter(textLine);
	        this.acceptor.getFilterChain().addLast( "codec", codecFilter);
	        
	        //
	        this.acceptor.getFilterChain().addLast("threadPool", new ExecutorFilter());
	        
	      //Add Custom filters here
	        if(this.payloadFilter != null)
	        {
	        	this.acceptor.getFilterChain().addLast("payloadFilter", this.payloadFilter);
	        }
	        
	        if(this.requestFilter != null)
	        {
	        	this.acceptor.getFilterChain().addLast("requestFilter", this.requestFilter);
	        }
	        
	        if(this.transactionFilter != null)
	        {
	        	this.acceptor.getFilterChain().addLast("transactionFilter", this.transactionFilter);	   
	        }
	        
	        if(this.authenticationFilter != null)
	        {
	        	this.acceptor.getFilterChain().addLast("authenticationFilter", this.authenticationFilter);
	        }
	        
	        //session specific configuration
	        this.acceptor.getSessionConfig().setBothIdleTime(10);
	        
	        this.acceptor.setHandler(this.handler);
	        this.acceptor.bind(new InetSocketAddress(this.port));
	        
	        log.info("--------------------------------------------");
	        log.info("Mobile Data Server successfully loaded on port ("+this.port+").....");
	        log.info("--------------------------------------------");
		}
		catch(Exception e)
		{
			log.error(this, e);
			this.stop();
			throw new RuntimeException(e);
		}
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

	@Override
	public void startService(ClusterEvent event) throws Exception
	{
		if(this instanceof PlainServer)
		{
			this.startListening(false);
		}
		else
		{
			this.startListening(this.isSecure()); 
		}
	}
}
