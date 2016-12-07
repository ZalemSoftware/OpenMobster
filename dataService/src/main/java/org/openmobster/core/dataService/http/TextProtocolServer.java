/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.dataService.http;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.channel.Channel;

/**
 *
 * @author openmobster@gmail.com
 */
public final class TextProtocolServer 
{
	private Channel channel;
	
	public void start()
	{
		//Configure the server
		NioServerSocketChannelFactory nioFactory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
			Executors.newCachedThreadPool());
		ServerBootstrap bootstrap = new ServerBootstrap(nioFactory);
		
		// Configure the pipeline factory.
		bootstrap.setPipelineFactory(new TextProtocolPipelineFactory());
		
		//some more options
		bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
		
		// Bind and start to accept incoming connections.
		this.channel = bootstrap.bind(new InetSocketAddress(1504));
		
		System.out.println("--------------------------------------------");
        System.out.println("Netty Http Server successfully loaded on port ("+1504+").....");
        System.out.println("--------------------------------------------");
	}
	
	public void stop()
	{
		this.channel.close().awaitUninterruptibly();
	}
}
