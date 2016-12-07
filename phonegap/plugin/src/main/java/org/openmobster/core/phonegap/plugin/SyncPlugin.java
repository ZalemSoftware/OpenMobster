/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.phonegap.plugin;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.android.api.sync.BeanListEntry;
import org.openmobster.android.api.sync.BeanList;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;

/**
 *
 * @author openmobster@gmail.com
 */
public final class SyncPlugin extends Plugin
{	
	/*@Override
	public void setContext(PhonegapActivity ctx) 
	{
		super.setContext(ctx);
	}*/

	@Override
	public synchronized PluginResult execute(String action, JSONArray input, String callbackId) 
	{
		PluginResult result = null;
		try
		{
			String returnValue = "";
			if(action.equals("readall"))
			{
				returnValue = this.readall(input);
			}
			else if(action.equals("value"))
			{
				returnValue = this.value(input);
			}
			else if(action.equals("insertIntoArray"))
			{
				returnValue = this.insertIntoArray(input);
			}
			else if(action.equals("clearArray"))
			{
				returnValue = this.clearArray(input);
			}
			else if(action.equals("arrayLength"))
			{
				returnValue = this.arrayLength(input);
			}
			else if(action.equals("addNewBean"))
			{
				returnValue = this.addNewBean(input);
			}
			else if(action.equals("deleteBean"))
			{
				returnValue = this.deleteBean(input);
			}
			else if(action.equals("updateBean"))
			{
				returnValue = this.updateBean(input);
			}
			else if(action.equals("commit"))
			{
				returnValue = this.commit(input);
			}
			else if(action.equals("queryByMatchAll"))
			{
				returnValue = this.queryByMatchAll(input);
			}
			else if(action.equals("queryByMatchOne"))
			{
				returnValue = this.queryByMatchOne(input);
			}
			else if(action.equals("queryByNotMatchAll"))
			{
				returnValue = this.queryByNotMatchAll(input);
			}
			else if(action.equals("queryByNotMatchOne"))
			{
				returnValue = this.queryByNotMatchOne(input);
			}
			else if(action.equals("queryByContainsAll"))
			{
				returnValue = this.queryByContainsAll(input);
			}
			else if(action.equals("queryByContainsOne"))
			{
				returnValue = this.queryByContainsOne(input);
			}
			else if(action.equals("test"))
			{
				returnValue = this.test(input);
			}
			
			result = new PluginResult(Status.OK,returnValue);
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			result = new PluginResult(Status.ERROR,e.toString()+":"+e.getMessage());
			return result;
		}
	}
	
	
	
	@Override
	public boolean isSynch(String action) 
	{
		// TODO Auto-generated method stub
		//return super.isSynch(action);
		return true;
	}

	private String readall(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		
		MobileBean[] beans = MobileBean.readAll(channel);
		if(beans == null || beans.length == 0)
		{
			return "0";
		}
		
		JSONArray oids = new JSONArray();
        int length = beans.length;
        for(int i=0; i<length; i++)
        {
            MobileBean local = beans[i];
            oids.put(local.getId());
        }
        
        return oids.toString();
	}
	
	private String value(JSONArray input) throws Exception
    {
		String channel = input.getString(0);
		String oid = input.getString(1);
		String fieldUri = input.getString(2);
		
		MobileBean bean = SyncSession.getInstance().readBean(oid);
		if(bean == null)
		{
			bean = MobileBean.readById(channel, oid);
			SyncSession.getInstance().cacheBean(bean);
		}
		
		//used for debugging
		/*try
		{
			return bean.getValue(fieldUri);
		}
		catch(Exception e)
		{
			System.out.println("Broken on:"+fieldUri);
			throw e;
		}*/
		
		return bean.getValue(fieldUri);
    }
	
	private String insertIntoArray(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String oid = input.getString(1);
		String fieldUri = input.getString(2);
		JSONObject value = input.getJSONObject(3); 
		
		MobileBean bean = SyncSession.getInstance().readBean(oid);
		if(bean == null)
		{
			bean = MobileBean.readById(channel, oid);
			SyncSession.getInstance().cacheBean(bean);
		}
		
		//Parse the JSONObject
		BeanListEntry arrayBean = new BeanListEntry();
		if(value.length() == 1)
		{
			//just a string array
			JSONArray names = value.names();
			int length = names.length();
			for(int i=0; i<length; i++)
			{
				String name = names.getString(i);
				String element = value.getString(name);
				arrayBean.setValue(element);
			}
		}
		else
		{
			//an object array
			JSONArray names = value.names();
			int length = names.length();
			for(int i=0; i<length; i++)
			{
				String name = names.getString(i);
				String element = value.getString(name);
				arrayBean.setProperty(name, element);
			}
		}
		
		bean.addBean(fieldUri, arrayBean);
		
		return this.arrayLength(input);
	}
	
	private String clearArray(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String oid = input.getString(1);
		String fieldUri = input.getString(2);
		
		MobileBean bean = SyncSession.getInstance().readBean(oid);
		if(bean == null)
		{
			bean = MobileBean.readById(channel, oid);
			SyncSession.getInstance().cacheBean(bean);
		}
		
		bean.clearList(fieldUri);
		
		return "0";
	}
	
	private String arrayLength(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String oid = input.getString(1);
		String arrayUri = input.getString(2);
		
		MobileBean bean = SyncSession.getInstance().readBean(oid);
		if(bean == null)
		{
			bean = MobileBean.readById(channel, oid);
			SyncSession.getInstance().cacheBean(bean);
		}
		
		BeanList array = bean.readList(arrayUri);
		if(array == null)
		{
			return "0";
		}
		
		return ""+array.size();
	}
	
	private String test(JSONArray input) throws Exception
	{
		String tag = input.getString(0);
		
		System.out.println(tag);
		
		return tag;
	}
	
	private String commit(JSONArray input) throws Exception
	{
		SyncSession.getInstance().commit();
		return "0";
	}
	
	private String addNewBean(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String jsonAdd = input.getString(1);
		
		MobileBean newBean = MobileBean.newInstance(channel);
		
		//generate a temporary oid for this bean
		//temporary because the real one comes after sync with the Cloud
		String tempOid = GeneralTools.generateUniqueId();
		
		//cache this bean
		SyncSession.getInstance().cacheBean(tempOid, newBean);
		
		//Parse the JSON object
		JSONObject state = new JSONObject(jsonAdd);
		if(state != null)
		{
			Iterator keys = state.keys();
			if(keys != null)
			{
				while(keys.hasNext())
				{
					String name = (String)keys.next();
					String value = state.getString(name);
					
					//validate for array...arrays should be specified by array specific methods
					if(name.indexOf('[') != -1)
					{
						continue;
					}
					
					newBean.setValue(name, value);
				}
			}
		}
		
		return tempOid;
	}
	
	private String updateBean(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String oid = input.getString(1);
		String jsonUpdate = input.getString(2);
		
		MobileBean bean = SyncSession.getInstance().readBean(oid);
		if(bean == null)
		{
			bean = MobileBean.readById(channel, oid);
			SyncSession.getInstance().cacheBean(bean);
		}
		
		//Parse the JSON object
		JSONObject state = new JSONObject(jsonUpdate);
		if(state != null)
		{
			Iterator keys = state.keys();
			if(keys != null)
			{
				while(keys.hasNext())
				{
					String name = (String)keys.next();
					String value = state.getString(name);
					
					//validate for array...arrays should be specified by array specific methods
					/*if(name.indexOf('[') != -1)
					{
						continue;
					}*/
					
					bean.setValue(name, value);
				}
			}
		}
		
		return "0";
	}
	
	private String deleteBean(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String oid = input.getString(1);
		
		MobileBean bean = MobileBean.readById(channel, oid);
    	String deletedBeanId = bean.getId();
    	
    	bean.deleteWithoutSync();
    	
    	return deletedBeanId;
	}
	
	
	private String queryByMatchAll(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		JSONObject criteria = input.getJSONObject(1); 
		
		if(criteria == null)
		{
			return "0";
		}
		
		GenericAttributeManager criteriaAttributes = new GenericAttributeManager();
		JSONArray names = criteria.names();
		int length = names.length();
		for(int i=0; i<length;i++)
		{
			String name = names.getString(i);
			String value = criteria.getString(name);
			
			criteriaAttributes.setAttribute(name, value);
		}
		
		MobileBean[] beans = MobileBean.queryByEqualsAll(channel, criteriaAttributes);
		if(beans == null || beans.length == 0)
		{
			return "0";
		}
		
		JSONArray oids = new JSONArray();
        length = beans.length;
        for(int i=0; i<length; i++)
        {
            MobileBean local = beans[i];
            oids.put(local.getId());
        }
        
        return oids.toString();
	}
	
	private String queryByMatchOne(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		JSONObject criteria = input.getJSONObject(1); 
		
		if(criteria == null)
		{
			return "0";
		}
		
		GenericAttributeManager criteriaAttributes = new GenericAttributeManager();
		JSONArray names = criteria.names();
		int length = names.length();
		for(int i=0; i<length;i++)
		{
			String name = names.getString(i);
			String value = criteria.getString(name);
			
			criteriaAttributes.setAttribute(name, value);
		}
		
		MobileBean[] beans = MobileBean.queryByEqualsAtleastOne(channel, criteriaAttributes);
		if(beans == null || beans.length == 0)
		{
			return "0";
		}
		
		JSONArray oids = new JSONArray();
        length = beans.length;
        for(int i=0; i<length; i++)
        {
            MobileBean local = beans[i];
            oids.put(local.getId());
        }
        
        return oids.toString();
	}
	
	private String queryByNotMatchAll(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		JSONObject criteria = input.getJSONObject(1); 
		
		if(criteria == null)
		{
			return "0";
		}
		
		GenericAttributeManager criteriaAttributes = new GenericAttributeManager();
		JSONArray names = criteria.names();
		int length = names.length();
		for(int i=0; i<length;i++)
		{
			String name = names.getString(i);
			String value = criteria.getString(name);
			
			criteriaAttributes.setAttribute(name, value);
		}
		
		MobileBean[] beans = MobileBean.queryByNotEqualsAll(channel, criteriaAttributes);
		if(beans == null || beans.length == 0)
		{
			return "0";
		}
		
		JSONArray oids = new JSONArray();
        length = beans.length;
        for(int i=0; i<length; i++)
        {
            MobileBean local = beans[i];
            oids.put(local.getId());
        }
        
        return oids.toString();
	}
	
	private String queryByNotMatchOne(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		JSONObject criteria = input.getJSONObject(1); 
		
		if(criteria == null)
		{
			return "0";
		}
		
		GenericAttributeManager criteriaAttributes = new GenericAttributeManager();
		JSONArray names = criteria.names();
		int length = names.length();
		for(int i=0; i<length;i++)
		{
			String name = names.getString(i);
			String value = criteria.getString(name);
			
			criteriaAttributes.setAttribute(name, value);
		}
		
		MobileBean[] beans = MobileBean.queryByNotEqualsAtleastOne(channel, criteriaAttributes);
		if(beans == null || beans.length == 0)
		{
			return "0";
		}
		
		JSONArray oids = new JSONArray();
        length = beans.length;
        for(int i=0; i<length; i++)
        {
            MobileBean local = beans[i];
            oids.put(local.getId());
        }
        
        return oids.toString();
	}
	
	private String queryByContainsAll(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		JSONObject criteria = input.getJSONObject(1); 
		
		if(criteria == null)
		{
			return "0";
		}
		
		GenericAttributeManager criteriaAttributes = new GenericAttributeManager();
		JSONArray names = criteria.names();
		int length = names.length();
		for(int i=0; i<length;i++)
		{
			String name = names.getString(i);
			String value = criteria.getString(name);
			
			criteriaAttributes.setAttribute(name, value);
		}
		
		MobileBean[] beans = MobileBean.queryByContainsAll(channel, criteriaAttributes);
		if(beans == null || beans.length == 0)
		{
			return "0";
		}
		
		JSONArray oids = new JSONArray();
        length = beans.length;
        for(int i=0; i<length; i++)
        {
            MobileBean local = beans[i];
            oids.put(local.getId());
        }
        
        return oids.toString();
	}
	
	private String queryByContainsOne(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		JSONObject criteria = input.getJSONObject(1); 
		
		if(criteria == null)
		{
			return "0";
		}
		
		GenericAttributeManager criteriaAttributes = new GenericAttributeManager();
		JSONArray names = criteria.names();
		int length = names.length();
		for(int i=0; i<length;i++)
		{
			String name = names.getString(i);
			String value = criteria.getString(name);
			
			criteriaAttributes.setAttribute(name, value);
		}
		
		MobileBean[] beans = MobileBean.queryByContainsAtleastOne(channel, criteriaAttributes);
		if(beans == null || beans.length == 0)
		{
			return "0";
		}
		
		JSONArray oids = new JSONArray();
        length = beans.length;
        for(int i=0; i<length; i++)
        {
            MobileBean local = beans[i];
            oids.put(local.getId());
        }
        
        return oids.toString();
	}
}
