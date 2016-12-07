/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.configuration;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openmobster.core.mobileCloud.android.errors.SystemException;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;
import org.openmobster.core.mobileCloud.android.util.IOUtil;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

/**
 *
 * @author openmobster@gmail.com
 */
public final class AppSystemConfig
{
	private static AppSystemConfig singleton;
	
	private GenericAttributeManager attrMgr;
	private boolean isActive;
	
	private AppSystemConfig()
	{
		
	}
	
	public static AppSystemConfig getInstance()
	{
		if(AppSystemConfig.singleton == null)
		{
			synchronized(AppSystemConfig.class)
			{
				if(AppSystemConfig.singleton == null)
				{
					AppSystemConfig.singleton = new AppSystemConfig();
				}
			}
		}
		return AppSystemConfig.singleton;
	}
	
	public static void stop()
	{
		AppSystemConfig.singleton = null;
	}
	
	public boolean isActive()
	{
		return this.isActive;
	}
	
	public synchronized void start()
	{
		try
		{
			this.attrMgr = new GenericAttributeManager();
			
			//parse the openmobster-app.xml
			InputStream is = AppSystemConfig.class.getResourceAsStream("/openmobster-app.xml");
			
			if(is == null)
			{
				//configuration not found
				return;
			}
			
			String xml = new String(IOUtil.read(is));
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document root = builder.parse(new ByteArrayInputStream(xml.getBytes()));
			
			//Parse <encryption>true/false</encryption>
			NodeList encryptionNodes = root.getElementsByTagName("encryption");
			if(encryptionNodes != null && encryptionNodes.getLength()>0)
			{
				int length = encryptionNodes.getLength();
				for(int i=0; i<length; i++)
				{
					Element local = (Element)encryptionNodes.item(i);
					String encryption = local.getFirstChild().getNodeValue().trim();
					this.attrMgr.setAttribute("encryption", encryption);
				}
			}
			
			/**
			 * Parse the <push>
			 * 				<launch-activity-class></launch-activity-class>
			 * 				<icon-name></icon-name>
			 * 				<!-- enable background re-establishment of the Push Socket if its disconnected -->
			 *				<reconnect/>
			 * 			 </push>
			 */
			NodeList push = root.getElementsByTagName("push");
			if(push != null && push.getLength()>0)
			{
				Element pushElement = (Element)push.item(0);
				Element launchActivityClass = (Element)pushElement.getElementsByTagName("launch-activity-class").item(0);
				Element iconName = (Element)pushElement.getElementsByTagName("icon-name").item(0);
				NodeList reconnect = pushElement.getElementsByTagName("reconnect");
				
				if(launchActivityClass != null)
				{
					String pushActivityClass = launchActivityClass.getFirstChild().getNodeValue().trim();
					this.attrMgr.setAttribute("launch-activity-class",pushActivityClass);
				}
				
				if(iconName != null)
				{
					String pushIconName = iconName.getFirstChild().getNodeValue().trim();
					this.attrMgr.setAttribute("push-icon-name", pushIconName);
				}
				
				if(reconnect != null && reconnect.getLength()>0)
				{
					this.attrMgr.setAttribute("reconnect", true);
				}
			}
			
			/**
			 * Parse
			 * 
			 *<channels>
			 *		<channel name='fuseapp_channel'>
			 *			<sync-push-message>You have {0} Fuse App Messages</sync-push-message>
			 *		</channel>
			 *		<channel name='one_sync_channel'>
			 *			<sync-push-message>You have {0} One Sync Channel Messages</sync-push-message>
			 *		</channel>
			 *	    <channel name='two_sync_channel'>
			 *			<sync-push-message>You have {0} Two Sync Channel Messages</sync-push-message>
			 *		</channel>
			 *</channels> 
			 */
			List<ChannelInfo> channels = new ArrayList<ChannelInfo>();
			this.attrMgr.setAttribute("channels", channels);
			NodeList channelsNodes = root.getElementsByTagName("channels");
			if(channelsNodes != null && channelsNodes.getLength()>0)
			{
				Element channelsElem = (Element)channelsNodes.item(0);
				NodeList channelNodes = channelsElem.getElementsByTagName("channel");
				int length = channelNodes.getLength();
				for(int i=0; i<length; i++)
				{
					ChannelInfo channelInfo = new ChannelInfo();
					Element channelElem = (Element)channelNodes.item(i);
					channelInfo.channel = channelElem.getAttribute("name");
					NodeList messageNodes = channelElem.getElementsByTagName("sync-push-message");
					if(messageNodes != null && messageNodes.getLength()>0)
					{
						Element messageElem = (Element)messageNodes.item(0);
						channelInfo.syncPushMessage = messageElem.getTextContent();
					}
					channels.add(channelInfo);
				}
			}
			
			/**
			 * 
			 * Parse:
			 * 
			 *<custom>
			 *	<push>
			 *		<notification-handler>custom_class</notification-handler>
			 *	</push>
			 *</custom>
			 * 
			 * 
			 */
			NodeList customNodeList = root.getElementsByTagName("custom");
			if(customNodeList != null && customNodeList.getLength()>0)
			{
				NodeList pushNodeList = ((Element)customNodeList.item(0)).getElementsByTagName("push");
				if(pushNodeList != null && pushNodeList.getLength()>0)
				{
					Element pushElement = (Element)pushNodeList.item(0);
					Element notificationHandlerElement = (Element)pushElement.getElementsByTagName("notification-handler").item(0);
					String notificationHandlerClass = notificationHandlerElement.getTextContent();
					this.attrMgr.setAttribute("/custom/push/notification-handler", notificationHandlerClass);
				}
			}
			
			this.isActive = true;
			
			
			/**
			 * 
			 * Parse
			 * 
			 *<device-activation>
	    	 *	<server>192.168.1.108</server>
	    	 *	<port>1502</port>
			 *</device-activation>
			 * 
			 * 
			 */
			NodeList deviceActivationNodes = root.getElementsByTagName("device-activation");
			if(deviceActivationNodes != null && deviceActivationNodes.getLength()>0)
			{
				Element deviceActivation = (Element)deviceActivationNodes.item(0);
				Element serverElement = (Element)deviceActivation.getElementsByTagName("server").item(0);
				NodeList portNodes = deviceActivation.getElementsByTagName("port");
				String port = "1502"; //default port
				if(portNodes != null && portNodes.getLength()>0)
				{
					Element portElement = (Element)portNodes.item(0);
					port = portElement.getTextContent();
				}
				String server = serverElement.getTextContent();
				
				this.attrMgr.setAttribute("device-activation.server", server);
				this.attrMgr.setAttribute("device-activation.port", port);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace(System.out);
			
			SystemException syse = new SystemException(this.getClass().getName(), "init", new Object[]{
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
			
			throw syse;
		}
	}
	
	public boolean isEncryptionActivated()
	{
		if(this.attrMgr == null)
		{
			this.start();
		}
		
		String encryption = (String)this.attrMgr.getAttribute("encryption");
		
		if(encryption != null && encryption.trim().length()>0)
		{
			if(encryption.equals(""+Boolean.TRUE))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public String getPushLaunchActivityClassName()
	{
		return (String)this.attrMgr.getAttribute("launch-activity-class");
	}
	
	public String getPushIconName()
	{
		return (String)this.attrMgr.getAttribute("push-icon-name");
	}
	
	public Set<String> getChannels()
	{
		Set<String> registeredChannels = new LinkedHashSet<String>();
		
		List<ChannelInfo> channelInfo = (List<ChannelInfo>)this.attrMgr.getAttribute("channels");
		if(channelInfo != null)
		{
			for(ChannelInfo channel:channelInfo)
			{
				registeredChannels.add(channel.channel);
			}
		}
		
		return registeredChannels;
	}
	
	public String getSyncPushMessage(String channel)
	{
		List<ChannelInfo> channelInfo = (List<ChannelInfo>)this.attrMgr.getAttribute("channels");
		for(ChannelInfo local:channelInfo)
		{
			if(local.channel.equals(channel))
			{
				return local.syncPushMessage;
			}
		}
		return null;
	}
	
	private static class ChannelInfo
	{
		private String channel;
		private String syncPushMessage;
	}
	
	public String getCustomPushNotificationHandler()
	{
		String customPushNotificationHandler = (String)this.attrMgr.getAttribute("/custom/push/notification-handler");
		if(customPushNotificationHandler == null)
		{
			//use the default one
			return "org.openmobster.core.mobileCloud.api.ui.framework.push.NotifySyncPushInvocationHandler";
		}
		return customPushNotificationHandler;
	}
	
	public boolean isPushReconnectActivated()
	{
		if(this.attrMgr.getAttribute("reconnect")!=null)
		{
			return true;
		}
		return false;
	}
	
	public String getServer()
	{
		return (String)this.attrMgr.getAttribute("device-activation.server");
	}
	
	public String getPort()
	{
		return (String)this.attrMgr.getAttribute("device-activation.port");
	}
}
