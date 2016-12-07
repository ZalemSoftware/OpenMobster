/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.jscript.bridge;

import org.openmobster.android.api.sync.BeanList;
import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.android.api.sync.CommitException;

/**
 * A Javascript bridge that exposes the OpenMobster MobileBean service to the HTML5/Javascript layer of the App.
 * 
 * 
 * @author openmobster@gmail.com
 */
public final class MobileBeanBridge 
{
	private MobileBean activeBean;
	
    /**
     * Access the value of a field of a domain object deployed in the Cloud
     * 
     * @param channel
     * @param oid
     * @param fieldUri
     * @return
     */
    public String getValue(String channel,String oid,String fieldUri)
    {
        MobileBean bean = MobileBean.readById(channel, oid);
        return bean.getValue(fieldUri);
    }
    
    /**
     * Reads oids of all the MobileBeans locally stored on the device
     * 
     * @param channel
     * @return
     */
    public String readAll(String channel)
    {
        MobileBean[] demoBeans = MobileBean.readAll(channel);
        if(demoBeans != null && demoBeans.length > 0)
        {
            StringBuilder buffer = new StringBuilder();
            int length = demoBeans.length;
            for(int i=0; i<length; i++)
            {
                MobileBean local = demoBeans[i];
                buffer.append(local.getId());
                if(i < length-1)
                {
                    buffer.append(",");
                }
            }
            
            return buffer.toString();
        }
        return null;
    }
    
    /**
     * Gets the length of an indexed/array property of the domain object
     * 
     * @param channel
     * @param oid
     * @param arrayUri
     * @return
     */
    public int arrayLength(String channel,String oid,String arrayUri)
    {
        MobileBean bean = MobileBean.readById(channel, oid);
        
        BeanList array = bean.readList(arrayUri);
        if(array != null)
        {
            return array.size();
        }
        return 0;
    }
    
    /**
     * Delete the bean locally and from the Cloud
     * 
     * @param channel
     * @param oid
     */
    public String deleteBean(String channel, String oid)
    {
    	MobileBean bean = MobileBean.readById(channel, oid);
    	String deletedBeanId = bean.getId();
    	
    	try
    	{
    		bean.delete();
    	}
    	catch(CommitException cme)
    	{
    		throw new RuntimeException(cme);
    	}
    	
    	return deletedBeanId;
    }
    
    /**
     * Saves the specified 'field' value on the bean instance
     * 
     * @param channel
     * @param oid
     * @param fieldUri
     * @param value
     * @return
     */
    public void updateBean(String channel,String oid,String fieldUri,String value)
    {
    	if(this.activeBean == null)
		{
    		this.activeBean = MobileBean.readById(channel, oid);
		}
		this.activeBean.setValue(fieldUri, value);
    }
    
    public void addBean(String channel,String fieldUri, String value)
    {
    	if(this.activeBean == null)
    	{
    		this.activeBean = MobileBean.newInstance(channel);
    	}
    	this.activeBean.setValue(fieldUri, value);
    }
    
    public String commit()
	{
    	try
    	{
	    	if(this.activeBean == null)
	    	{
	    		return null;
	    	}
	    
	    	try
	    	{
	    		this.activeBean.save();
	    		return this.activeBean.getId();
	    	}
	    	catch(CommitException cme)
	    	{
	    		throw new RuntimeException(cme);
	    	}
    	}
    	finally
    	{
    		this.activeBean = null;
    	}
	}
}
