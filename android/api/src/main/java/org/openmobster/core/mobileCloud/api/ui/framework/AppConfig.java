/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;
import org.openmobster.core.mobileCloud.android.util.IOUtil;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.api.ui.framework.push.PushCommand;

/**
 * @author openmobster@gmail.com
 *
 */
public final class AppConfig 
{
	private static AppConfig singleton;
	
	private GenericAttributeManager attrMgr;
	
	private boolean isActive;
	
	private AppConfig()
	{		
	}
	
	public static AppConfig getInstance()
	{
		if(AppConfig.singleton == null)
		{
			synchronized(AppConfig.class)
			{
				if(AppConfig.singleton == null)
				{
					AppConfig.singleton = new AppConfig();
				}
			}
		}
		return AppConfig.singleton;
	}
	
	public static void stopSingleton()
	{
		AppConfig.singleton = null;
	}
	
	public boolean isActive()
	{
		return this.isActive;
	}
	
	public synchronized void init()
	{
		try
		{
			if(this.attrMgr == null)
			{
				this.attrMgr = new GenericAttributeManager();
			
				//parse the /moblet-app/moblet-app.xml
				InputStream is = AppConfig.class.getResourceAsStream("/moblet-app/moblet-app.xml");
				if(is == null)
				{
					return;
				}
				
				String xml = new String(IOUtil.read(is));
				
				
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document root = builder.parse(new ByteArrayInputStream(xml.getBytes()));
				
				if(xml.indexOf("<bootstrap>") != -1)
				{
					this.parseNew(root);
					return;
				}
				
				//parse app commands
				NodeList commands = root.getElementsByTagName("command");
				Hashtable registeredCommands = new Hashtable();
				if(commands != null)
				{					
					this.attrMgr.setAttribute("commands", registeredCommands);
					int size = commands.getLength();
					for(int i=0; i<size; i++)
					{
						Element command = (Element)commands.item(i);
						String commandId = command.getAttribute("id");
						String commandClassName = command.getFirstChild().getNodeValue().trim();
						registeredCommands.put(commandId, Class.forName(commandClassName).newInstance());						
					}					
				}
												
				//parse app locale, optional
				NodeList localeNode = root.getElementsByTagName("locale");
				if(localeNode != null && localeNode.getLength()>0)
				{					
					Element localeElem = (Element)localeNode.item(0);					
					
					Element languageElem = (Element)localeElem.getElementsByTagName("language-code").item(0);					
					String language = languageElem.getFirstChild().getNodeValue().trim();
					
					String country = null;
					NodeList countryNode = localeElem.getElementsByTagName("country-code");
					if(countryNode!=null && countryNode.getLength()>0)
					{
						Element countryElem = (Element)countryNode.item(0);
						country = countryElem.getFirstChild().getNodeValue().trim();
					}
					
					Locale locale = null;
					if(country != null)
					{
						locale = new Locale(language, country);
					}
					else
					{
						locale = new Locale(language); 
					}
					this.attrMgr.setAttribute("locale", locale);
				}
				
				//parse screens
				NodeList screens = root.getElementsByTagName("screen");
				Hashtable screenConfig = new Hashtable();
				if(screens !=null && screens.getLength()>0)
				{										
					this.attrMgr.setAttribute("screenConfig", screenConfig);
					int size = screens.getLength();
					for(int i=0; i<size; i++)
					{
						Element screen = (Element)screens.item(i);
						String screenClass = screen.getFirstChild().getNodeValue().trim();
						String screenId = screen.getAttribute("id");
						screenConfig.put(screenId, screenClass);
					}					
				}	
				
				//Validate the configuration
				//Must have a startup command
				/*if(!registeredCommands.containsKey("startup"))
				{
					throw new IllegalStateException("Startup Command is missing!!");
				}*/
				
				//Must have a home command
				if(this.isMVCBeingUsed() && !screenConfig.containsKey("home"))
				{
					throw new IllegalStateException("Home screen is missing!!");
				}
				
				//Parse Push-Commands if they are registered
				NodeList pNodes = root.getElementsByTagName("push-commands");
				if(pNodes != null && pNodes.getLength()>0)
				{
					Element pushCommands = (Element)pNodes.item(0);
					NodeList pCommands = pushCommands.getElementsByTagName("command");
					Map<String, PushCommand> configMap = new HashMap<String, PushCommand>();
					if(pCommands != null && pCommands.getLength()>0)
					{
						int size = pCommands.getLength();
						for(int i=0; i<size; i++)
						{
							Element local = (Element)pCommands.item(i);
							
							String id = local.getAttribute("id");
							String commandClass = local.getFirstChild().getNodeValue().trim();
							
							PushCommand pushCommand = (PushCommand)Class.forName(commandClass).newInstance();
							
							configMap.put(id, pushCommand);
						}
					}
					
					this.attrMgr.setAttribute("push-commands", configMap);
				}
			}
			
			this.isActive = true;
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			
			SystemException syse = new SystemException(this.getClass().getName(), "init", new Object[]{
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
			ErrorHandler.getInstance().handle(syse);
			
			this.attrMgr.setAttribute("frameworkBootstrapFailure", syse);
		}
	}
	
	private void parseNew(Document root) throws Exception
	{
		//parse app commands
		NodeList commands = root.getElementsByTagName("command");
		Hashtable registeredCommands = new Hashtable();
		if(commands != null)
		{					
			this.attrMgr.setAttribute("commands", registeredCommands);
			int size = commands.getLength();
			for(int i=0; i<size; i++)
			{
				Element command = (Element)commands.item(i);
				String commandId = command.getAttribute("id");
				String commandClassName = command.getFirstChild().getNodeValue().trim();
				registeredCommands.put(commandId, Class.forName(commandClassName).newInstance());						
			}					
		}
										
		//parse app locale, optional
		NodeList localeNode = root.getElementsByTagName("locale");
		if(localeNode != null && localeNode.getLength()>0)
		{					
			Element localeElem = (Element)localeNode.item(0);					
			
			Element languageElem = (Element)localeElem.getElementsByTagName("language-code").item(0);					
			String language = languageElem.getFirstChild().getNodeValue().trim();
			
			String country = null;
			NodeList countryNode = localeElem.getElementsByTagName("country-code");
			if(countryNode!=null && countryNode.getLength()>0)
			{
				Element countryElem = (Element)countryNode.item(0);
				country = countryElem.getFirstChild().getNodeValue().trim();
			}
			
			Locale locale = null;
			if(country != null)
			{
				locale = new Locale(language, country);
			}
			else
			{
				locale = new Locale(language); 
			}
			this.attrMgr.setAttribute("locale", locale);
		}
		
		//parse screens
		NodeList screens = root.getElementsByTagName("screen");
		Hashtable screenConfig = new Hashtable();
		if(screens !=null && screens.getLength()>0)
		{										
			this.attrMgr.setAttribute("screenConfig", screenConfig);
			int size = screens.getLength();
			for(int i=0; i<size; i++)
			{
				Element screen = (Element)screens.item(i);
				String screenClass = screen.getFirstChild().getNodeValue().trim();
				String screenId = screen.getAttribute("id");
				screenConfig.put(screenId, screenClass);
			}					
		}	
		
		//Process bootstrap element
		Element bootstrap = (Element)root.getElementsByTagName("bootstrap").item(0);
		NodeList commandNodes = bootstrap.getElementsByTagName("command");
		if(commandNodes != null && commandNodes.getLength()>0)
		{
			Element startupCommand = (Element)commandNodes.item(0);
			registeredCommands.put("startup", Class.forName(startupCommand.getFirstChild().
			getNodeValue().trim()).newInstance());
		}
		NodeList screenNodes = bootstrap.getElementsByTagName("screen");
		if(screenNodes != null && screenNodes.getLength()>0)
		{
			Element homeScreen = (Element)screenNodes.item(0);
			screenConfig.put("home", homeScreen.getFirstChild().getNodeValue().trim());
		}
		
		//Process push element
		NodeList pushNodes = root.getElementsByTagName("push");
		if(pushNodes != null && pushNodes.getLength()>0)
		{
			Element pushNode = (Element)pushNodes.item(0);
			Element commandNode = (Element)pushNode.getElementsByTagName("command").item(0);
			String pushCommand = commandNode.getFirstChild().getNodeValue().trim();
			registeredCommands.put("push", Class.forName(pushCommand).newInstance());
		}
		
		//Validate the configuration
		
		//Must have a home command
		if(this.isMVCBeingUsed() && !screenConfig.containsKey("home"))
		{
			throw new IllegalStateException("Home screen is missing!!");
		}
		
		//Parse Push-Commands if they are registered
		NodeList pNodes = root.getElementsByTagName("push-commands");
		if(pNodes != null && pNodes.getLength()>0)
		{
			Element pushCommands = (Element)pNodes.item(0);
			NodeList pCommands = pushCommands.getElementsByTagName("command");
			Map<String, PushCommand> configMap = new HashMap<String, PushCommand>();
			if(pCommands != null && pCommands.getLength()>0)
			{
				int size = pCommands.getLength();
				for(int i=0; i<size; i++)
				{
					Element local = (Element)pCommands.item(i);
					
					String id = local.getAttribute("id");
					String commandClass = local.getFirstChild().getNodeValue().trim();
					
					PushCommand pushCommand = (PushCommand)Class.forName(commandClass).newInstance();
					
					configMap.put(id, pushCommand);
				}
			}
			this.attrMgr.setAttribute("push-commands", configMap);
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------						
	public Locale getAppLocale()
	{
		return (Locale)this.attrMgr.getAttribute("locale");
	}
	
	public Hashtable getScreenConfig()
	{
		return (Hashtable)this.attrMgr.getAttribute("screenConfig");
	}
	
	public Hashtable getAppCommands()
	{
		return (Hashtable)this.attrMgr.getAttribute("commands");
	}
	
	public boolean isFrameworkActive()
	{
		return this.attrMgr.getAttribute("frameworkBootstrapFailure") == null;
	}	
	
	private boolean isMVCBeingUsed()
	{
		if(this.attrMgr.getAttribute("screenConfig") != null)
		{
			return true;
		}
		return false;
	}
	
	public Map<String, PushCommand> getPushCommands()
	{
		return (Map<String, PushCommand>)this.attrMgr.getAttribute("push-commands");
	}
}
