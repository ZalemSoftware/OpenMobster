/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.dataService.http;

import java.util.concurrent.TimeUnit;

import static org.jboss.netty.channel.Channels.*;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.jboss.netty.handler.execution.ExecutionHandler;

/**
 *
 * @author openmobster@gmail.com
 */
public class TextProtocolPipelineFactory implements ChannelPipelineFactory
{
	private OrderedMemoryAwareThreadPoolExecutor eventExecutor;
	
	public TextProtocolPipelineFactory()
	{
		this.eventExecutor = new OrderedMemoryAwareThreadPoolExecutor(5, 1000000, 10000000, 100,
        TimeUnit.MILLISECONDS);
	}
	
	@Override
	public ChannelPipeline getPipeline() throws Exception 
	{
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();
		  
		// Add the text line codec combination first,
		pipeline.addLast("framer", new DelimiterBasedFrameDecoder(3000000, 
				ChannelBuffers.wrappedBuffer(new byte[] {'E','O','F'})));
		//pipeline.addLast("framer", new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, 
		//	Delimiters.lineDelimiter()));
		
		//pipeline.addLast("eofFramer", new TextProtocolFrameDecoder());
		pipeline.addLast("decoder", new StringDecoder());
		pipeline.addLast("encoder", new StringEncoder());
		
		// Insert OrderedMemoryAwareThreadPoolExecutor before your blocking handler
		pipeline.addLast("pipelineExecutor", new ExecutionHandler(this.eventExecutor));
		
		// and then business logic.
		pipeline.addLast("handler", new TextProtocolHandler());
		
		return pipeline;
	}
}
