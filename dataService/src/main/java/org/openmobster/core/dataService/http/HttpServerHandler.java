/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.openmobster.core.dataService.http;

import static org.jboss.netty.handler.codec.http.HttpHeaders.*;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.*;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpChunkTrailer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;

public class HttpServerHandler extends SimpleChannelUpstreamHandler {

    /** Buffer that stores the response content */
    private final StringBuilder buf = new StringBuilder();
    private final StringBuilder chunkBuffer = new StringBuilder();

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception 
    {
    	Object o = e.getMessage();
    
    	
    	if(o instanceof HttpRequest)
    	{
	    	HttpRequest request = (HttpRequest) e.getMessage();
	
	        buf.setLength(0);
	        buf.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
	        buf.append("===================================\r\n");
	
	        buf.append("VERSION: " + request.getProtocolVersion() + "\r\n");
	        buf.append("HOSTNAME: " + getHost(request, "unknown") + "\r\n");
	        buf.append("REQUEST_URI: " + request.getUri() + "\r\n");
	        buf.append("METHOD: " + request.getMethod().getName() + "\r\n\r\n");
	
	        for (Map.Entry<String, String> h: request.getHeaders()) {
	            buf.append("HEADER: " + h.getKey() + " = " + h.getValue() + "\r\n");
	        }
	        buf.append("\r\n");
	
	        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
	        Map<String, List<String>> params = queryStringDecoder.getParameters();
	        if (!params.isEmpty()) {
	            for (Entry<String, List<String>> p: params.entrySet()) {
	                String key = p.getKey();
	                List<String> vals = p.getValue();
	                for (String val : vals) {
	                    buf.append("PARAM: " + key + " = " + val + "\r\n");
	                }
	            }
	            buf.append("\r\n");
	        }
	
	        if(!request.isChunked())
	        {
		        ChannelBuffer content = request.getContent();
		        if (content.readable()) 
		        {
		        	String contentStr = content.toString(CharsetUtil.UTF_8);
		            QueryStringDecoder postDecoder = new QueryStringDecoder(contentStr, false);
		            Map<String, List<String>> postParams = postDecoder.getParameters();
		            if (!postParams.isEmpty()) {
		                for (Entry<String, List<String>> p: postParams.entrySet()) {
		                    String key = p.getKey();
		                    List<String> vals = p.getValue();
		                    for (String val : vals) {
		                        buf.append("POST PARAM: " + key + " = " + val + "\r\n");
		                    }
		                }
		                buf.append("\r\n");
		            }
		        }
		        
		        //Write the response back
		        writeResponse(e);
	        }
    	}
    	else if(o instanceof HttpChunk)
    	{
    		HttpChunk chunk = (HttpChunk) e.getMessage();
            if (chunk.isLast()) 
            {
            	String contentStr = chunkBuffer.toString();
	            QueryStringDecoder postDecoder = new QueryStringDecoder(contentStr, false);
	            Map<String, List<String>> postParams = postDecoder.getParameters();
	            if (!postParams.isEmpty()) {
	                for (Entry<String, List<String>> p: postParams.entrySet()) {
	                    String key = p.getKey();
	                    List<String> vals = p.getValue();
	                    for (String val : vals) {
	                        buf.append("POST PARAM: " + key + " = " + val + "\r\n");
	                    }
	                }
	                buf.append("\r\n");
	            }
		        
                writeResponse(e);
            } 
            else 
            {
                chunkBuffer.append(chunk.getContent().toString(CharsetUtil.UTF_8));
            }
    	}
    }

    private void writeResponse(MessageEvent e) 
    {
        // Build the response object.
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        
        response.setContent(ChannelBuffers.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));
        response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");

        // Write the response.
        ChannelFuture future = e.getChannel().write(response);

        // Close the non-keep-alive connection after the write operation is done.
        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception 
    {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }
}
