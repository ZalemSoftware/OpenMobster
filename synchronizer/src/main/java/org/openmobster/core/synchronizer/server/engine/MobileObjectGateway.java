/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.synchronizer.server.engine;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.annotation.Annotation;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.openmobster.cloud.api.sync.MobileBean;
import org.openmobster.cloud.api.sync.MobileBeanId;
import org.openmobster.core.common.XMLUtilities;
import org.openmobster.core.common.event.EventManager;
import org.openmobster.core.synchronizer.SyncException;
import org.openmobster.core.mobileContainer.Invocation;
import org.openmobster.core.mobileContainer.InvocationResponse;
import org.openmobster.core.mobileContainer.MobileContainer;
import org.openmobster.core.mobileObject.xml.MobileObjectSerializer;
import org.openmobster.core.common.event.EventListener;


/**
 * @author openmobster@gmail.com
 */
public class MobileObjectGateway 
{	
	private static Logger log = Logger.getLogger(MobileObjectGateway.class);
		
	private MapEngine mapEngine;
	private MobileObjectSerializer serializer;
	private MobileContainer mobileContainer;
	private EventManager eventManager;
		
	public MobileObjectGateway()
	{
		
	}
		
	public void start()
	{
	}
		
	public void stop()
	{
		
	}
	
	public void notify(EventListener eventListener)
	{
		this.eventManager.addListener(eventListener);
	}
			
	public MapEngine getMapEngine()
	{
		return mapEngine;
	}
	
	public void setMapEngine(MapEngine mapEngine)
	{
		this.mapEngine = mapEngine;
	}
		
	public MobileObjectSerializer getSerializer() 
	{
		return serializer;
	}

	public void setSerializer(MobileObjectSerializer serializer) 
	{
		this.serializer = serializer;
	}
		
	public MobileContainer getMobileContainer() 
	{
		return mobileContainer;
	}

	public void setMobileContainer(MobileContainer mobileContainer) 
	{
		this.mobileContainer = mobileContainer;
	}
	
	
	public EventManager getEventManager()
	{
		return eventManager;
	}

	public void setEventManager(EventManager eventManager)
	{
		this.eventManager = eventManager;
	}
	//----------------------------------------------------------------------------------------------	
	public String parseId(String xml) throws SyncException
	{
		String recordId = null;

		Document document = XMLUtilities.parse(xml);

		Element id = (Element) document.getElementsByTagName("recordId").item(0);
		recordId = XMLUtilities.restoreXML(id.getTextContent());
		
		//Perform any local id to server side id mappings if applies
		recordId = mapEngine.mapFromLocalToServer(recordId);
		
		return recordId;
	}	
	
	private String extractRecordId(MobileBean record)
	throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		String id = "";
		
		Class recordClazz = record.getClass();
		Field[] declaredFields = recordClazz.getDeclaredFields();		
		for(Field field: declaredFields)
		{		
			Annotation[] annotations = field.getAnnotations();			
			for(Annotation annotation: annotations)
			{								
				if(annotation instanceof MobileBeanId)
				{
					return BeanUtils.getProperty(record, field.getName());										
				}
			}			
		}
		
		return id;
	}
	//------------------------------------------------------------------------------------------------
	public List<MobileBean> readAllRecords(String serviceId) throws SyncException
	{
		try
		{
			return this.getAllBeans(serviceId, false);						
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new SyncException(e);
		}
	}
	
	public List<MobileBean> bootup(String serviceId) throws SyncException
	{
		try
		{
			return this.getAllBeans(serviceId, true);												
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new SyncException(e);
		}
	}
		
	public MobileBean readRecord(String serviceId, String recordId) throws SyncException
	{
		try
		{
			return this.getBean(serviceId, recordId);			
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new SyncException(e);
		}
	}
		
	public String createRecord(String serviceId, String xml) throws SyncException
	{
		try
		{
			return this.saveBean(serviceId, xml);						
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new SyncException(e);
		}
	}
		
	public void updateRecord(String serviceId, String recordId, String xml) throws SyncException
	{
		try
		{
			this.updateBean(serviceId, recordId, xml);
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new SyncException(e);
		}
	}
		
	public void deleteRecord(String serviceId, String recordId) throws SyncException
	{
		try
		{
			this.deleteBean(serviceId, recordId);
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new SyncException(e);
		}
	}
		
	public void clearAll(String serviceId) throws SyncException
	{
		try
		{
			List<MobileBean> allRecords = this.readAllRecords(serviceId);
			if(allRecords != null && !allRecords.isEmpty())
			{
				for(int i=0; i<allRecords.size(); i++)
				{
					this.deleteRecord(serviceId, this.extractRecordId(allRecords.get(i)));
				}
			}
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new SyncException(e);
		}
	}
	//-----------------------------------------------------------------------------------------------
	public String marshal(MobileBean record) throws SyncException
	{
		try
		{
			String recordXml = null;
			StringBuffer buffer = new StringBuffer();
			
			String serverRecordId = this.extractRecordId(record);
			
			if(serverRecordId.startsWith("proxy[[") && serverRecordId.endsWith("]]"))
			{
				int startIndex = "proxy[[".length();
				int endIndex = serverRecordId.length()-2;
				serverRecordId = serverRecordId.substring(startIndex, endIndex);
				
				String deviceRecordId = mapEngine.mapFromServerToLocal(serverRecordId);
				
				buffer.append("<mobileObject>\n");
				
				buffer.append("<recordId>");
				buffer.append(XMLUtilities.cleanupXML(deviceRecordId));
				buffer.append("</recordId>\n");
				
				buffer.append("<serverRecordId>");
				buffer.append(XMLUtilities.cleanupXML(serverRecordId));
				buffer.append("</serverRecordId>\n");
				
				buffer.append("<proxy/>\n");
				
				
				buffer.append("</mobileObject>\n");
				
				recordXml = buffer.toString();
			}
			else
			{
				String deviceRecordId = mapEngine.mapFromServerToLocal(serverRecordId);
				
				String mobileObjectXml = this.serializer.serialize(record);
				
				buffer.append("<mobileObject>\n");
				
				buffer.append("<recordId>");
				buffer.append(XMLUtilities.cleanupXML(deviceRecordId));
				buffer.append("</recordId>\n");
				
				buffer.append("<serverRecordId>");
				buffer.append(XMLUtilities.cleanupXML(serverRecordId));
				buffer.append("</serverRecordId>\n");
				
				buffer.append(mobileObjectXml+"\n");
				
				
				buffer.append("</mobileObject>\n");
				
				recordXml = buffer.toString();
			}
			
			return recordXml;
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new SyncException(e);
		}
	}
	
	public String marshalId(String recordId) throws SyncException
	{
		StringBuffer buffer = new StringBuffer();
		recordId = mapEngine.mapFromServerToLocal(recordId);
		String id = XMLUtilities.cleanupXML(recordId);
		
		buffer.append("<mobileObject>\n");
		buffer.append("<recordId>"+id+"</recordId>\n");
		buffer.append("</mobileObject>\n");
		
		return buffer.toString();
	}	
	//------------------------------------------------------------------------------------------------
	public String mapIdFromLocalToServer(String localId) throws SyncException
	{
		String serverId = mapEngine.mapFromLocalToServer(localId);
		
		if(serverId == null || serverId.trim().length()==0)
		{
			//Nothing to map
			serverId = localId;
		}		
		
		return serverId;
	}				
	//----------Using the New POJO MobileContainer--------------------------------------------------------
	private MobileBean getBean(String serviceId, String beanId) throws Exception
	{
		MobileBean bean = null;
		
		Invocation invocation = Invocation.getInstance();
		invocation.setServiceUrl("/bean/id");
		invocation.setBeanId(beanId);
		invocation.setConnectorId(serviceId);
		
		//Perform the invocation
		InvocationResponse response = mobileContainer.invoke(invocation);
		bean = response.getBean();
		
		return bean;
	}
	
	private List<MobileBean> getAllBeans(String serviceId, boolean isBootup) throws Exception
	{
		List<MobileBean> allBeans = null;
		
		Invocation invocation = Invocation.getInstance();
		invocation.setServiceUrl("/bean/all");
		invocation.setConnectorId(serviceId);
		if(isBootup)
		{
			invocation.setAttribute("bootup", "true");
		}
		
		//Perform the invocation
		InvocationResponse response = mobileContainer.invoke(invocation);
		allBeans = response.getAllBeans();
		
		return allBeans;
	}
	
	private String saveBean(String serviceId, String serializedBean) throws Exception
	{
		String beanId = null;
		
		Invocation invocation = Invocation.getInstance();
		invocation.setServiceUrl("/bean/save");
		invocation.setConnectorId(serviceId);
		invocation.setSerializedBean(serializedBean);
		
		//Perform the invocation
		InvocationResponse response = mobileContainer.invoke(invocation);
		beanId = response.getBeanId();
		
		return beanId;
	}
	
	private String updateBean(String serviceId, String beanId, String serializedBean) throws Exception
	{
		String updatedBeanId = null;
		
		Invocation invocation = Invocation.getInstance();
		invocation.setServiceUrl("/bean/save");
		invocation.setConnectorId(serviceId);
		invocation.setBeanId(beanId);
		invocation.setSerializedBean(serializedBean);
		
		//Perform the invocation
		InvocationResponse response = mobileContainer.invoke(invocation);
		updatedBeanId = response.getBeanId();
		
		return updatedBeanId;
	}
	
	private String deleteBean(String serviceId, String beanId) throws Exception
	{
		String deletedBeanId = null;
		
		Invocation invocation = Invocation.getInstance();
		invocation.setServiceUrl("/bean/delete");
		invocation.setBeanId(beanId);
		invocation.setConnectorId(serviceId);
		
		//Perform the invocation
		InvocationResponse response = mobileContainer.invoke(invocation);
		deletedBeanId = response.getBeanId();
		
		return deletedBeanId;
	}
	//----------SAX Parser to parse out the record id-------------------------------------------------------	
}
