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
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import org.openmobster.core.moblet.MobletApp;

/**
 * @author openmobster@gmail.com
 */
public class AgentInstaller extends HttpServlet
{		
	private static final long serialVersionUID = -6983143036322360870L;
	private static Logger log = Logger.getLogger(AgentInstaller.class);
	
	private Map<String, byte[]> agentModules;
	//---------------------------------------------------------------------------------------------------------
	public void init(ServletConfig servletConfig) throws ServletException 
	{
		try
		{						
			this.agentModules = new HashMap<String, byte[]>();
			
			//Parse the JAD file
			this.parseAgentJAD("rimos/430/MobileCloud.jad");
			
			//Parse the Modules
			this.parseAgentModules("rimos/430/MobileCloud.cod");
			
			//Parse the JAD file
			this.parseAgentJAD("rimos/430/CloudManager.jad");
			
			//Parse the Modules
			this.parseAgentModules("rimos/430/CloudManager.cod");
			
			log.info("OpenMobster AppStore Successfully Initialized........................");
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
			
			byte[] module = this.agentModules.get(moduleName);
			if(module == null)
			{
				module = this.findMobletModule(httpRequest, moduleName);
			}
		
			//Send the module back after either loading it from cache or finding it and loading it into cache
			if(module != null)
			{
				String contentType = "text/vnd.sun.j2me.app-descriptor";
				
				if(moduleName.endsWith("cod"))
				{
					contentType = "application/vnd.rim.cod";
				}
				
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
		downloadUrl = downloadUrl.replaceFirst("/apps", "");
						
		if(downloadUrl.endsWith(".jad"))
		{
			return this.getMobletConfig(downloadUrl, moduleName);
		}
		else if(downloadUrl.endsWith(".cod"))
		{
			return this.getMobletModule(downloadUrl, moduleName);
		}
		
		return null;
	}
	
	private byte[] getMobletConfig(String downloadUrl, String moduleName) throws Exception
	{
		MobletApp app = AppStore.getInstance().findByDownloadUrl(downloadUrl);
		if(app != null)
		{
			String confLocation = app.getConfigLocation();
			String[] split = confLocation.split("/");
			String confName = split[split.length-1];
			
			if(confName.equals(moduleName))
			{
				return AppStore.getInstance().getAppConfig(downloadUrl);
			}
		}
		return null;
	}
	
	private byte[] getMobletModule(String downloadUrl, String moduleName) throws Exception
	{
		InputStream is = AppStore.getInstance().getAppBinary(downloadUrl);
		
		if(is == null)
		{
			//nothing found
			return null;
		}
		
		byte[] moduleBytes = this.readFromZipFile(is, moduleName);
		if(moduleBytes != null)
		{
			//match found inside a zip file...(a cod file with sibling cod files)
			return moduleBytes;
		}
		
		//maybe not a zip...read it straight up	
		MobletApp app = AppStore.getInstance().findByDownloadUrl(downloadUrl);
		if(app != null)
		{
			String binLocation = app.getBinaryLocation();
			String[] split = binLocation.split("/");
			String binName = split[split.length-1];
			
			if(binName.equals(moduleName))
			{
				is = AppStore.getInstance().getAppBinary(downloadUrl);
				return IOUtilities.readBytes(is);
			}
		}
		
		return null;
	}
	
	private byte[] readFromZipFile(InputStream is, String moduleName)
	{		
		ZipInputStream zis = null;
		ByteArrayOutputStream bos = null;
		try
		{						
			zis = new ZipInputStream(is);	
			ZipEntry entry = null;
			while((entry=zis.getNextEntry())!=null)
			{				
				log.debug("Entry ="+entry.getName());
				log.debug("Comment ="+entry.getComment());
				log.debug("Size ="+entry.getSize());
				log.debug("Method ="+entry.getMethod());
				log.debug("---------------------------------------------------------");
				
				if(!entry.getName().equals(moduleName))
				{
					continue;
				}
				
				byte[] buffer = new byte[1024];
				int status = -1;
				bos = new ByteArrayOutputStream();
				while((status=zis.read(buffer))!=-1)
				{
					bos.write(buffer, 0, status);
				}
				
				return bos.toByteArray();
			}
			return null;
		}		
		catch(Exception e)
		{
			return null;
		}
		finally
		{
			if(is != null)
			{
				try{is.close();}catch(IOException ioe){}
			}
			
			if(zis != null)
			{
				try{zis.close();}catch(IOException ioe){}
			}
			
			if(bos != null)
			{
				try{bos.close();}catch(IOException ioe){}
			}
		}
	}
	//-------------------------------------------------------------------------------------------------------------------
	private void parseAgentJAD(String jadUri) throws Exception
	{
		InputStream is = null;
		try
		{
			is = Thread.currentThread().getContextClassLoader().
			getResourceAsStream(jadUri);						
			
			byte[] jad = IOUtilities.readBytes(is);		
			
			String[] split = jadUri.split("/");
			String jadName = split[split.length-1];
			
			this.agentModules.put(jadName, jad);			
		}		
		finally
		{
			if(is != null)
			{
				try{is.close();}catch(IOException ioe){}
			}						
		}
	}
	
	private void parseAgentModules(String codUri) throws Exception
	{		
		InputStream is = Thread.currentThread().getContextClassLoader().
		getResourceAsStream(codUri);
		
		if(this.processSiblingCods(is))
		{
			//sibling cods have been processed...
			return;
		}
		
		//Not a sibling cod, but the real deal	
		is = Thread.currentThread().getContextClassLoader().
		getResourceAsStream(codUri);
		
		String[] split = codUri.split("/");
		String codName = split[split.length-1];
		
		this.agentModules.put(codName, IOUtilities.readBytes(is));		
	}
	
	private boolean processSiblingCods(InputStream is)
	{
		ZipInputStream zis = null;
		ByteArrayOutputStream bos = null;
		boolean isSiblingCods = false;
		try
		{						
			zis = new ZipInputStream(is);	
			ZipEntry entry = null;
			while((entry=zis.getNextEntry())!=null)
			{				
				isSiblingCods = true;
				
				log.debug("Entry ="+entry.getName());
				log.debug("Comment ="+entry.getComment());
				log.debug("Size ="+entry.getSize());
				log.debug("Method ="+entry.getMethod());
				log.debug("---------------------------------------------------------");
				
				byte[] buffer = new byte[1024];
				int status = -1;
				bos = new ByteArrayOutputStream();
				while((status=zis.read(buffer))!=-1)
				{
					bos.write(buffer, 0, status);
				}
				
				this.agentModules.put(entry.getName(), bos.toByteArray());
			}
			return isSiblingCods;
		}	
		catch(Exception e)
		{
			return false;
		}
		finally
		{
			if(is != null)
			{
				try{is.close();}catch(IOException ioe){}
			}
			
			if(zis != null)
			{
				try{zis.close();}catch(IOException ioe){}
			}
			
			if(bos != null)
			{
				try{bos.close();}catch(IOException ioe){}
			}
		}
	}
}
