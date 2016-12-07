/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.dataService.http;

import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import javax.net.ssl.X509TrustManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.SSLContext;
import java.security.SecureRandom;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.scheme.Scheme;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.openmobster.core.common.ServiceManager;

/**
 *
 * @author openmobster@gmail.com
 */
public class TestHttpServer extends TestCase 
{
	/**
	 * @param name
	 */
	public TestHttpServer(String name) 
	{
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception 
	{
		super.setUp();
		
		ServiceManager.bootstrap();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception 
	{
		super.tearDown();
		
		ServiceManager.shutdown();
	}
	
	
	public void testHttpGET() throws Exception
	{
		/*DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://localhost:1504/om/testservice?id=xyz&blah=blahbblah2");

        HttpResponse response1 = httpclient.execute(httpGet);

        // The underlying HTTP connection is still held by the response object 
        // to allow the response content to be streamed directly from the network socket. 
        // In order to ensure correct deallocation of system resources 
        // the user MUST either fully consume the response content  or abort request 
        // execution by calling HttpGet#releaseConnection().
        try 
        {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            
            // do something useful with the response body
            // and ensure it is fully consumed
            String result = null;
    		if(entity1 != null)
    		{
    			result = EntityUtils.toString(entity1);
    		}
            
            System.out.println("***************************************************");
            System.out.println(result);
            System.out.println("***************************************************");
        } 
        finally 
        {
        }*/
	}
	
	/*public void testHttpPOST() throws Exception
	{
		// First create a trust manager that won't care.
        X509TrustManager trustManager = new X509TrustManager() 
        {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() 
            {
                // Don't do anything.
                return null;
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException 
           {
                // TODO Auto-generated method stub
           }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException 
            {
                // TODO Auto-generated method stub

            }
        };
        
        // Now put the trust manager into an SSLContext.
        // Supported: SSL, SSLv2, SSLv3, TLS, TLSv1, TLSv1.1
        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[] { trustManager },
                new SecureRandom());

        // Use the above SSLContext to create your socket factory
        // Accept any hostname, so the self-signed certificates don't fail

        SSLSocketFactory sf = new SSLSocketFactory(sslContext,
                SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        // Register our new socket factory with the typical SSL port and the
        // correct protocol name.
        Scheme httpsScheme = new Scheme("https", 1504, sf);

        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getConnectionManager().getSchemeRegistry()
                .register(httpsScheme);
		
		
        HttpPost httpPost = new HttpPost("https://localhost:1504/om/testservice?id=xyz&blah=blahbblah2");
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for(int i=0; i<1024; i++)
        {
        	for(int j=0; j<1024; j++)
        	{
        		bos.write("a".getBytes());
        	}
        }
        
        
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("IDToken1", "username"));
        nvps.add(new BasicNameValuePair("IDToken2", new String(bos.toByteArray())));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps,"UTF-8");
        entity.setChunked(true);
        httpPost.setEntity(entity);

        HttpResponse response1 = httpclient.execute(httpPost);

        // The underlying HTTP connection is still held by the response object 
        // to allow the response content to be streamed directly from the network socket. 
        // In order to ensure correct deallocation of system resources 
        // the user MUST either fully consume the response content  or abort request 
        // execution by calling HttpGet#releaseConnection().
        try 
        {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            
            // do something useful with the response body
            // and ensure it is fully consumed
           //read the response
    		String result = null;
    		if(entity1 != null)
    		{
    			result = EntityUtils.toString(entity1);
    		}
            
            System.out.println("***************************************************");
            System.out.println(result);
            System.out.println("***************************************************");
        } 
        finally 
        {
        }
	}*/
	
	/*public void testBasicGET() throws Exception
	{
		BasicHttpClient httpClient = new BasicHttpClient("http://localhost:1504");
		
		ParameterMap params = httpClient.newParams();
		params.add("id", "x&y&z");
		params.add("blah","blahblah2,12345");
		
		HttpResponse httpResponse = httpClient.get("/om/testservice", params);
		
		System.out.println(httpResponse.getBodyAsString());
	}*/
}
