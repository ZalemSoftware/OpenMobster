/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.dataService.http;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelLocal;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

/**
 *
 * @author openmobster@gmail.com
 */
public class TextProtocolHandler extends SimpleChannelUpstreamHandler
{
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception 
	{
		// Cast to a String first.
		// We know it is a String because we put some codec in TelnetPipelineFactory.
		Channel channel = e.getChannel();
		String packet = (String)e.getMessage();
		
		System.out.println("Payload processed:"+packet.length());
		channel.write("status=200\r\n");
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) 
	{
		System.out.println(this.getClass()+":"+e.getCause());
		e.getChannel().close();
	}	
}
