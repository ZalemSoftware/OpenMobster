/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.agent.provisioning;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.UnavailableException;

import org.apache.log4j.Logger;

import org.openmobster.core.common.transaction.TransactionHelper;
import org.openmobster.core.common.IOUtilities;
import org.openmobster.core.moblet.appStore.AppStore;

/**
 * @author openmobster@gmail.com
 */
public class AndroidAgentInstaller extends HttpServlet
{		
	private static final long serialVersionUID = -6983143036322360870L;
	private static Logger log = Logger.getLogger(AndroidAgentInstaller.class);
	
	private byte[] cloudModule;
	//---------------------------------------------------------------------------------------------------------
	public void init(ServletConfig servletConfig) throws ServletException 
	{
		try
		{						
			InputStream is = Thread.currentThread().getContextClassLoader().
			getResourceAsStream("android/20/CloudManager.apk");
			
			this.cloudModule = IOUtilities.readBytes(is);
			
			log.info("OpenMobster Android AppStore Successfully Initialized........................");
		}
		catch(Exception exception)
		{
			log.error(this, exception);
			throw new UnavailableException(exception.getMessage());
		}
	}
	
	protected void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) 
	throws ServletException, IOException 
	{
		boolean startedHere = TransactionHelper.startTx();
		try
		{
			//Spit out the headers sent by the browser
			Enumeration headerNames = httpRequest.getHeaderNames();
			log.debug("------------------------------------------");
			while(headerNames.hasMoreElements())
			{
				String name = (String)headerNames.nextElement();
				String value = httpRequest.getHeader(name);
				
				log.debug(name+"="+value);
			}
			log.debug("------------------------------------------");
			
			
			String requestURI = httpRequest.getRequestURI();
			String[] split = requestURI.split("/");			
			String moduleName = split[split.length-1];
			
			log.debug("Getting......."+moduleName);
			
			byte[] module = null;
			if(!moduleName.equalsIgnoreCase("cloudmanager"))
			{
				module = this.findMobletModule(httpRequest, moduleName);
			}
			else
			{
				module = this.cloudModule;
			}
		
			//Send the module back after either loading it from cache or finding it and loading it into cache
			if(module != null)
			{
				String contentType = "application/vnd.android.package-archive";
				
				httpResponse.setContentType(contentType);
				httpResponse.getOutputStream().write(module);
				httpResponse.getOutputStream().flush();
			}
			else
			{
				httpResponse.sendError(HttpServletResponse.SC_NO_CONTENT);
			}
			
			if(startedHere)
			{
				TransactionHelper.commitTx();
			}
		}
		catch(Exception e)
		{
			log.error(this, e);
			
			if(startedHere)
			{
				TransactionHelper.rollbackTx();
			}
			
			httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
	//---------------------------------------------------------------------------------------------------------
	private byte[] findMobletModule(HttpServletRequest request, String moduleName) throws Exception
	{
		String requestURI = request.getRequestURI();
		String downloadUrl = requestURI.replaceFirst(request.getContextPath(), "");
		downloadUrl = downloadUrl.replaceFirst("/android", "");
						
		return this.getMobletModule(downloadUrl, moduleName);
	}
	
	private byte[] getMobletModule(String downloadUrl, String moduleName) throws Exception
	{
		InputStream is = AppStore.getInstance().getAppBinary(downloadUrl);
		
		if(is != null)
		{
			//nothing found
			return IOUtilities.readBytes(is);
		}
		
		return null;
	}
}
