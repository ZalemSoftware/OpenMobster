/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.cmdline.shell.commands;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.Set;
import java.net.InetAddress;

import org.apache.log4j.Logger;

import org.apache.geronimo.gshell.ansi.Buffer;
import org.apache.geronimo.gshell.ansi.Code;
import org.apache.geronimo.gshell.ansi.RenderWriter;

import org.openmobster.core.console.cmdline.ServiceManager;
import org.openmobster.core.common.InVMAttributeManager;

import org.openmobster.cloudConnector.api.Configuration;

import org.openmobster.cloudConnector.api.service.MobileService;
import org.openmobster.cloudConnector.api.service.Request;
import org.openmobster.cloudConnector.api.service.Response;

/**
 * @author openmobster@gmail.com
 */
public class ConsoleSession
{
	private static Logger log = Logger.getLogger(ConsoleSession.class);
	
	private InVMAttributeManager mgr;
	
	public ConsoleSession()
	{
		
	}
	
	public static ConsoleSession getInstance()
	{
		return (ConsoleSession)ServiceManager.locate("console://ConsoleSession");
	}
	
	public void start()
	{
		InputStream is = null;
		try
		{
			this.mgr = new InVMAttributeManager();
			
			//SetUp Console Configuration
			String property = "java.io.tmpdir";
			String parent = System.getProperty(property);
			File file = new File(parent, "openmobster.conf");
			file.createNewFile();
			
			//Read this file and load up attributes based on this
			is = new FileInputStream(file);
			Properties conf = new Properties();
			conf.load(is);
			
			Set<Object> names = conf.keySet();
			for(Object name: names)
			{
				this.setAttribute((String)name, (String)conf.getProperty((String)name));
			}
			
			if(this.isConfigured())
			{
				StringWriter writer = new StringWriter();
				PrintWriter out = new RenderWriter(writer);
				Buffer buff = new Buffer();
				
				String status = 
				"****************Connected to OpenMobster Cloud Server*****************************\n"+
				"Cloud Server: {0}\n"+
				"Cloud Server Port: {1}\n"+
				"Configuration Located at: {2}\n"+
				"**********************************************************************************\n";
				
				
				buff.attrib(MessageFormat.format(status, this.getAttribute("host"),this.getAttribute("port"),
				file.getAbsolutePath()), Code.CYAN);
				out.println(buff);
				out.flush();
				

				System.out.println(writer.toString());
				
				this.loadCloudConfig();
			}
		}
		catch(Exception ioe)
		{
			log.error(this, ioe);
			throw new RuntimeException(ioe);
		}
		finally
		{
			if(is != null)
			{
				try{is.close();}catch(IOException ioe){};
			}
		}
	}
	
	public void stop()
	{
		
	}
	//--------------------------------------------------------------------------------------------------------------
	public Object getAttribute(String name)
	{
		return this.mgr.getAttribute(name);
	}
	
	public void setAttribute(String name, Object value)
	{
		this.mgr.setAttribute(name, value);
	}
	
	public void removeAttribute(String name)
	{
		this.mgr.removeAttribute(name);
	}
	//--------------------------------------------------------------------------------------------------------------
	public void authSuccessNotification(String username)
	{
		this.mgr.setAttribute("username", username);
		this.mgr.setAttribute("auth-mode", true);
	}
	
	public boolean isAnonymousMode()
	{
		return (this.mgr.getAttribute("auth-mode") == null);
	}
	
	public void logout()
	{
		this.mgr.removeAttribute("username");
		this.mgr.removeAttribute("auth-mode");
	}
	
	public boolean isConfigured()
	{
		if(this.getAttribute("isConfigured")!=null)
		{
			return true;
		}
		return false;
	}
	
	public void startConfigure(String host, String port) 
	{
		try
		{
			this.initCloudConfig(host,port);
		}
		catch(Exception ioe)
		{
			log.error(this, ioe);
			throw new RuntimeException(ioe);
		}
	}
	
	public void finishConfigure()
	{
		FileOutputStream fos = null;
		try
		{
			String property = "java.io.tmpdir";
			String parent = System.getProperty(property);
			File file = new File(parent, "openmobster.conf");
			file.createNewFile();
			
			fos = new FileOutputStream(file, true);
			
			//persist host
		    String isConfigured = "isConfigured="+Boolean.TRUE+"\n";
		    fos.write(isConfigured.getBytes());
		    this.setAttribute("isConfigured", Boolean.TRUE);
		}
		catch(Exception ioe)
		{
			log.error(this, ioe);
			throw new RuntimeException(ioe);
		}
		finally
		{
			if(fos != null)
			{
				try{fos.close();}catch(IOException e){}
			}
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------------
	private String getMyIPAddress() throws Exception
	{
		InetAddress[] myaddrs = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
		
		if(myaddrs == null)
		{
			return "127.0.0.1";
		}
		
		if(myaddrs.length == 1)
		{
			return myaddrs[0].getHostAddress();
		}
		
		for(InetAddress cour: myaddrs)
		{
			if(!cour.isLoopbackAddress())
			{
				return cour.getHostAddress();
			}
		}
		
		return null;
	}
	
	private void initCloudConfig(String host, String port) throws Exception
	{
		//Setup the Cloud Connector Configuration
		Configuration configuration = Configuration.getInstance();
		configuration.setDeviceId("console:"+this.getMyIPAddress());
		configuration.setAuthenticationHash(""); //empty
		configuration.setServerIp(host);
		configuration.setServerId(host);
		configuration.setPlainServerPort(port);
		configuration.bootup();
		
		//Setup local configuration
		String property = "java.io.tmpdir";
		String parent = System.getProperty(property);
		File file = new File(parent, "openmobster.conf");
		file.delete();
		file.createNewFile();
		
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(file, true);
			
			//persist host
		    String hostValue = "host="+host+"\n";
		    fos.write(hostValue.getBytes());
		    this.setAttribute("host", host);
			
			//persist port
		    String portValue = "port="+configuration.decidePort()+"\n";
		    fos.write(portValue.getBytes());
		    this.setAttribute("port", configuration.decidePort());
		    
		    //secure or not
		    String secure = null;
		    if(configuration.isSSLActivated())
		    {
		    	secure = "secure=true\n";
		    	
		    }
		    else
		    {
		    	secure = "secure=false\n";
		    }
		    fos.write(secure.getBytes());
		    this.setAttribute("secure", ""+configuration.isSSLActivated());
		}
		finally
		{
			if(fos != null)
			{
				try{fos.close();}catch(IOException e){}
			}
		}
	}
	
	private void loadCloudConfig() throws Exception
	{
		Configuration configuration = Configuration.getInstance();
		configuration.setDeviceId("console:"+this.getMyIPAddress());
		configuration.setAuthenticationHash(""); //empty
		configuration.setServerIp((String)this.getAttribute("host"));
		configuration.setServerId((String)this.getAttribute("host"));
		
		String secure = (String)this.getAttribute("secure");
		if(secure.equals(Boolean.TRUE.toString()))
		{
			configuration.setSecureServerPort((String)this.getAttribute("port"));
			configuration.activateSSL();
		}
		else
		{
			configuration.setPlainServerPort((String)this.getAttribute("port"));
			configuration.deActivateSSL();
		}
	}
}
