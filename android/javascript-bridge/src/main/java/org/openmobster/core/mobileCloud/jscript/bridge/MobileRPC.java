/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.jscript.bridge;

import java.util.Iterator;

import org.json.JSONObject;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;

/**
 * A Javascript bridge that exposes the OpenMobster MobileBean service to the HTML5/Javascript layer of the App.
 * 
 * 
 * @author openmobster@gmail.com
 */
public final class MobileRPC 
{
    public String invoke(String service,String jsonPayload)
    {
    	try
    	{
    		Request request = new Request(service);
    	
    		//parse the payload
    		JSONObject json = new JSONObject(jsonPayload);
    		Iterator keys = json.keys();
    		while(keys.hasNext())
    		{
    			String name = (String)keys.next();
    			String value = json.getString(name);
    			request.setAttribute(name, value);
    		}
    		
    		Response response = MobileService.invoke(request);
    		json = new JSONObject();
    		String[] names = response.getNames();
    		for(String name:names)
    		{
    			String value = response.getAttribute(name);
    			json.put(name, value);
    		}
    		
    		String jsonStr = json.toString();
    	
    		return jsonStr;
    	}
    	catch(Exception e)
    	{
    		//e.printStackTrace();
    		try
    		{
    			JSONObject error = new JSONObject();
    			error.put("status", "500");
    			error.put("statusMsg", e.toString());
    			return error.toString();
    		}
    		catch(Exception ex)
    		{
    			//we tried
    			//e.printStackTrace();
    			return null;
    		}
    	}
    }
}
