/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.cordova.plugin;

import java.util.Iterator;
import java.util.List;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import org.openmobster.android.api.sync.BeanList;
import org.openmobster.android.api.sync.BeanListEntry;
import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.android.api.sync.MobileBeanCursor;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;

/**
 *
 * @author openmobster@gmail.com
 */
public class SyncPlugin extends CordovaPlugin
{
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
	{
		try
		{
		    if ("echo".equals(action)) 
		    {
		        String reply = this.echo(args.getString(0));
		        callbackContext.success(reply);
		        
		        return true;
		    }
		    else if("json".equals(action))
		    {
		    	JSONObject json = this.json(args.getJSONObject(0));
		    	callbackContext.success(json);
		    	
		    	return true;
		    }
		    else if("newBean".equals(action))
		    {
		    	String oid = this.newBean(args);
		    	callbackContext.success(oid);
		    	
		    	return true;
		    }
		    else if("readall".equals(action))
		    {
		    	JSONArray all = this.readall(args);
		    	callbackContext.success(all);
		    	
		    	return true;
		    }
		    else if("readBean".equals(action))
		    {
		    	JSONObject bean = this.readBean(args);
		    	callbackContext.success(bean);
		    	
		    	return true;
		    }
		    else if("updateBean".equals(action))
		    {
		    	String beanId = this.updateBean(args);
		    	callbackContext.success(beanId);
		    	
		    	return true;
		    }
		    else if("deleteBean".equals(action))
		    {
		    	String beanId = this.deleteBean(args);
		    	callbackContext.success(beanId);
		    	
		    	return true;
		    }
		    else if("arrayInsert".equals(action))
		    {
		    	JSONObject metadata = this.arrayInsert(args);
		    	callbackContext.success(metadata);
		    	
		    	return true;
		    }
		    else if("arrayLength".equals(action))
		    {
		    	String arrayLength = this.arrayLength(args);
		    	callbackContext.success(arrayLength);
		    	
		    	return true;
		    }
		    else if("clearArray".equals(action))
		    {
		    	String arrayLength = this.clearArray(args);
		    	callbackContext.success(arrayLength);
		    	
		    	return true;
		    }
		    else if("arrayValue".equals(action))
		    {
		    	String arrayValue = this.arrayValue(args);
		    	callbackContext.success(arrayValue);
		    	
		    	return true;
		    }
		    else if("arrayUpdate".equals(action))
		    {
		    	String id = this.arrayUpdate(args);
		    	callbackContext.success(id);
		    	
		    	return true;
		    }
		    else if("sortByProperty".equals(action))
		    {
		    	JSONObject cursorInfo = this.sortByProperty(args);
		    	callbackContext.success(cursorInfo);
		    	
		    	return true;
		    }
		    else if("queryByProperty".equals(action))
		    {
		    	JSONObject cursorInfo = this.queryByProperty(args);
		    	callbackContext.success(cursorInfo);
		    	
		    	return true;
		    }
		    else if("searchByMatchAll".equals(action))
		    {
		    	JSONObject cursorInfo = this.searchByMatchAll(args);
		    	callbackContext.success(cursorInfo);
		    	
		    	return true;
		    }
		    else if("searchByMatchAtleastOne".equals(action))
		    {
		    	JSONObject cursorInfo = this.searchByMatchAtleastOne(args);
		    	callbackContext.success(cursorInfo);
		    	
		    	return true;
		    }
		    else if("cursorCurrBean".equals(action))
		    {
		    	JSONObject bean = this.cursorCurrBean(args);
		    	callbackContext.success(bean);
		    	
		    	return true;
		    }
		    else if("cursorAllBeans".equals(action))
		    {
		    	JSONArray all = this.cursorAllBeans(args);
		    	callbackContext.success(all);
		    	
		    	return true;
		    }
		    
		    
		    return false;  // Returning false results in a "MethodNotFound" error.
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			callbackContext.error(e.toString());
			return true;
		}
	}
	//-------------------Test Operations-----------------------------------------------------------------------------------------------------------
	private String echo(String message)
	{
		return "Echo Back: "+message;
	}
	
	private JSONObject json(JSONObject input) throws JSONException
	{
		JSONObject json = new JSONObject();
		
		json.put("param1", input.getString("param1"));
		json.put("param2", input.getString("param2"));
		
		return json;
	}
	//------------------CRUD Operations---------------------------------------------------------------------------------------------------------------
	private String newBean(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		JSONObject state = input.getJSONObject(1);
		
		MobileBean newBean = MobileBean.newInstance(channel);
		
		//Parse the JSON object
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
		
		//Persist the new bean in the local database and queue it for sync
		newBean.save();
		
		return newBean.getId();
	}
	
	private JSONArray readall(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		JSONArray properties = input.getJSONArray(1);
		
		JSONArray all = new JSONArray();
		
		MobileBean[] beans = MobileBean.readAll(channel);
		if(beans == null || beans.length == 0)
		{
			return all;
		}
		
        int length = beans.length;
        for(int i=0; i<length; i++)
        {
            MobileBean local = beans[i];
            JSONObject jsonBean = new JSONObject();
        	jsonBean.put("id", local.getId());
        	
        	if(properties != null)
        	{
	            for(int j=0,size=properties.length();j<size; j++)
	            {
	            	String property = properties.getString(j);
	            	if(property.indexOf('[') != -1)
					{
						continue;
					}
	            	
	            	String value = local.getValue(property);
	            	if(value != null)
	            	{
	            		jsonBean.put(property, value);
	            	}
	            }
        	}
            all.put(jsonBean);
        }
        
        return all;
	}
	
	private JSONObject readBean(JSONArray input) throws Exception
	{
		JSONObject bean = new JSONObject();
		
		String channel = input.getString(0);
		String id = input.getString(1);
		JSONArray properties = input.getJSONArray(2);
		
		MobileBean mobileBean = MobileBean.readById(channel, id);
		if(mobileBean == null)
		{
			return bean;
		}
		
		bean.put("id", mobileBean.getId());
		if(properties != null)
		{
			for(int i=0,size=properties.length();i<size; i++)
	        {
	        	String property = properties.getString(i);
	        	if(property.indexOf('[') != -1)
				{
					continue;
				}
	        	
	        	String value = mobileBean.getValue(property);
	        	if(value != null)
	        	{
	        		bean.put(property, value);
	        	}
	        }
		}
		
		return bean;
	}
	
	private String updateBean(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String id = input.getString(1);
		JSONObject state = input.getJSONObject(2);
		
		MobileBean bean = MobileBean.readById(channel, id);
		if(bean == null)
		{
			//do nothing
			throw new RuntimeException("Bean Not Found");
		}
		
		//Parse the JSON object
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
		
		//Persist the new bean in the local database and queue it for sync
		bean.save();
		
		return id;
	}
	
	private String deleteBean(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String oid = input.getString(1);
		
		MobileBean bean = MobileBean.readById(channel, oid);
		if(bean == null)
		{
			//nothing to do
			throw new RuntimeException("Bean Not Found");
		}
		
    	String deletedBeanId = bean.getId();
    	bean.delete();
    	
    	return deletedBeanId;
	}
	//-------------------------------Array related Operations----------------------------------------------------------------------------------------------------
	private JSONObject arrayInsert(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String oid = input.getString(1);
		String fieldUri = input.getString(2);
		JSONArray array = input.getJSONArray(3); 
		
		MobileBean bean = MobileBean.readById(channel, oid);
		if(bean == null)
		{
			throw new RuntimeException("Bean Not Found");
		}
		
		int arrayLength = array.length();
		for(int i=0; i<arrayLength; i++)
		{
			Object object = array.get(i);
			BeanListEntry arrayBean = this.arrayBean(object);
			bean.addBean(fieldUri, arrayBean);
		}
		
		//Persist the new bean in the local database and queue it for sync
		bean.save();
		
		JSONObject metadata = new JSONObject();
		metadata.put("id", bean.getId());
		metadata.put("arrayLength", this.arrayLength(input));
		
		return metadata;
	}
	
	private BeanListEntry arrayBean(Object object) throws Exception
	{
		BeanListEntry arrayBean = new BeanListEntry();
		if(object instanceof String)
		{
			String value = (String)object;
			arrayBean.setValue(value);
		}
		else if(object instanceof JSONObject)
		{
			JSONObject value = (JSONObject)object;
			
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
		
		return arrayBean;
	}
	
	private String arrayLength(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String oid = input.getString(1);
		String arrayUri = input.getString(2);
		
		MobileBean bean = MobileBean.readById(channel, oid);
		if(bean == null)
		{
			throw new RuntimeException("Bean Not Found");
		}
		
		BeanList array = bean.readList(arrayUri);
		if(array == null)
		{
			return "0";
		}
		
		return ""+array.size();
	}
	
	private String clearArray(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String oid = input.getString(1);
		String fieldUri = input.getString(2);
		
		MobileBean bean = MobileBean.readById(channel, oid);
		
		bean.clearList(fieldUri);
		
		bean.save();
		
		return this.arrayLength(input);
	}
	
	private String arrayValue(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String oid = input.getString(1);
		String valueUri = input.getString(2);
		
		MobileBean bean = MobileBean.readById(channel, oid);
		if(bean == null)
		{
			throw new RuntimeException("Bean Not Found");
		}
		
		String value = bean.getValue(valueUri);
		
		return value;
	}
	
	private String arrayUpdate(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String id = input.getString(1);
		String fieldUri = input.getString(2);
		String value = input.getString(3);
		
		MobileBean bean = MobileBean.readById(channel, id);
		if(bean == null)
		{
			//do nothing
			throw new RuntimeException("Bean Not Found");
		}
		
		bean.setValue(fieldUri, value);
		
		//Persist the new bean in the local database and queue it for sync
		bean.save();
		
		return id;
	}
	//--------------------------Query related operations-------------------------------------------------------------------------------------
	private JSONObject sortByProperty(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String property = input.getString(1);
		boolean ascending = input.getBoolean(2);
		
		JSONObject cursorInfo = new JSONObject();
		
		MobileBeanCursor cursor = MobileBean.sortByProperty(channel, property, ascending);
		
		cursorInfo.put("id", cursor.getId());
		cursorInfo.put("channel", cursor.getChannel());
		cursorInfo.put("count",cursor.count());
		
		//Cache the cursor for reading in later calls
		CursorCache.getInstance().cache(cursor);
		
		return cursorInfo;
	}
	
	private JSONObject queryByProperty(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		String property = input.getString(1);
		String value = input.getString(2);
		
		JSONObject cursorInfo = new JSONObject();
		
		MobileBeanCursor cursor = MobileBean.queryByProperty(channel, property, value);
		
		cursorInfo.put("id", cursor.getId());
		cursorInfo.put("channel", cursor.getChannel());
		cursorInfo.put("count",cursor.count());
		
		//Cache the cursor for reading in later calls
		CursorCache.getInstance().cache(cursor);
		
		return cursorInfo;
	}
	
	private JSONObject searchByMatchAll(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		JSONArray nameValuePairs = input.getJSONArray(1);
		GenericAttributeManager criteria = new GenericAttributeManager();
		
		//prepare the criteria
		int arrayLength = nameValuePairs.length();
		for(int i=0; i<arrayLength; i++)
		{
			JSONObject nameValuePair = nameValuePairs.getJSONObject(i);
			String name = nameValuePair.getString("name");
			String value = nameValuePair.getString("value");
			criteria.setAttribute(name, value);
		}
		
		JSONObject cursorInfo = new JSONObject();
		
		MobileBeanCursor cursor = MobileBean.searchByMatchAll(channel, criteria);
		
		cursorInfo.put("id", cursor.getId());
		cursorInfo.put("channel", cursor.getChannel());
		cursorInfo.put("count",cursor.count());
		
		//Cache the cursor for reading in later calls
		CursorCache.getInstance().cache(cursor);
		
		return cursorInfo;
	}
	
	private JSONObject searchByMatchAtleastOne(JSONArray input) throws Exception
	{
		String channel = input.getString(0);
		JSONArray nameValuePairs = input.getJSONArray(1);
		GenericAttributeManager criteria = new GenericAttributeManager();
		
		//prepare the criteria
		int arrayLength = nameValuePairs.length();
		for(int i=0; i<arrayLength; i++)
		{
			JSONObject nameValuePair = nameValuePairs.getJSONObject(i);
			String name = nameValuePair.getString("name");
			String value = nameValuePair.getString("value");
			criteria.setAttribute(name, value);
		}
		
		JSONObject cursorInfo = new JSONObject();
		
		MobileBeanCursor cursor = MobileBean.searchByMatchAtleastOne(channel, criteria);
		
		cursorInfo.put("id", cursor.getId());
		cursorInfo.put("channel", cursor.getChannel());
		cursorInfo.put("count",cursor.count());
		
		//Cache the cursor for reading in later calls
		CursorCache.getInstance().cache(cursor);
		
		return cursorInfo;
	}
	
	private JSONObject cursorCurrBean(JSONArray input) throws Exception
	{
		JSONObject bean = new JSONObject();
		
		String cursorId = input.getString(0);
		JSONArray properties = input.getJSONArray(1);
		
		MobileBean mobileBean = CursorCache.getInstance().currentBean(cursorId);
		if(mobileBean == null)
		{
			return bean;
		}
		
		bean.put("id", mobileBean.getId());
		if(properties != null)
		{
			for(int i=0,size=properties.length();i<size; i++)
	        {
	        	String property = properties.getString(i);
	        	if(property.indexOf('[') != -1)
				{
					continue;
				}
	        	
	        	String value = mobileBean.getValue(property);
	        	if(value != null)
	        	{
	        		bean.put(property, value);
	        	}
	        }
		}
		
		return bean;
	}
	
	private JSONArray cursorAllBeans(JSONArray input) throws Exception
	{
		JSONArray all = new JSONArray();
		
		String cursorId = input.getString(0);
		JSONArray properties = input.getJSONArray(1);
		
		List<MobileBean> beans = CursorCache.getInstance().allBeans(cursorId);
		if(beans == null || beans.isEmpty())
		{
			return all;
		}
		
        for(MobileBean local:beans)
        {
            JSONObject jsonBean = new JSONObject();
        	jsonBean.put("id", local.getId());
        	
        	if(properties != null)
        	{
	            for(int j=0,size=properties.length();j<size; j++)
	            {
	            	String property = properties.getString(j);
	            	if(property.indexOf('[') != -1)
					{
						continue;
					}
	            	
	            	String value = local.getValue(property);
	            	if(value != null)
	            	{
	            		jsonBean.put(property, value);
	            	}
	            }
        	}
            all.put(jsonBean);
        }
        
        return all;
	}
}
