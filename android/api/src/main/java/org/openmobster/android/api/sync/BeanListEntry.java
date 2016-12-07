/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.android.api.sync;

import java.io.IOException;
import java.util.Hashtable;

import org.openmobster.core.mobileCloud.android.util.StringUtil;
import org.openmobster.core.mobileCloud.android.util.Base64;

/**
 * BeanEntry represents members of a BeanList
 * 
 * @author openmobster@gmail.com
 *
 */
public class BeanListEntry 
{
	private Hashtable<String,String> properties;
	private String listProperty;
	
	/*
	 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
	 * Comentando campo inútil.
	 */
//	private int index;
	
	
	public BeanListEntry()
	{
		this(0, new Hashtable<String,String>());
	}
	
	public BeanListEntry(int index, Hashtable<String,String> properties)
	{
		this.properties = properties;
		
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Comentando campo inútil.
		 */
//		this.index = index;
	}		
	String getListProperty()
	{
		return this.listProperty;
	}
	void setListProperty(String listProperty)
	{
		this.listProperty = listProperty;
	}
	//Public API-----------------------------------------------------------------------------------------------------------------------
	/**
	 * Reads a property value on this object using a property expression
	 * 
	 * @return the value of the specified property
	 */
	public String getProperty(String propertyExpression)
	{
		String propertyUri = this.calculatePropertyUri(propertyExpression);		
		return this.properties.get(propertyUri);
	}
	
	/**
	 * Reads a property value on this object using a property expression
	 * 
	 * @return the value of the specified property
	 */
	public byte[] getBinaryProperty(String propertyExpression) throws IOException
	{
		String propertyUri = this.calculatePropertyUri(propertyExpression);		
		String value = this.properties.get(propertyUri);
		
		if(value != null && value.trim().length()>0)
		{
			byte[] binary = Base64.decode(value);
			return binary;
		}
		
		return null;
	}
	
	/**
	 * Set the property value on this object using a property expression
	 * 
	 * @param propertyExpression expression to signify the property to be set
	 * @param value value to be set
	 */
	public void setProperty(String propertyExpression, String value)
	{
		String propertyUri = this.calculatePropertyUri(propertyExpression);
		this.properties.put(propertyUri, value);
	}
	
	/**
	 * Set the property value on this object using a property expression
	 * 
	 * @param propertyExpression expression to signify the property to be set
	 * @param value value to be set
	 */
	public void setBinaryProperty(String propertyExpression, byte[] value)
	{
		String propertyUri = this.calculatePropertyUri(propertyExpression);
		String propertyValue = Base64.encodeBytes(value);
		this.properties.put(propertyUri, propertyValue);
	}
	
	/**
	 * All the properties of this object
	 * 
	 * @return all the properties of this object
	 */
	public Hashtable<String,String> getProperties()
	{		
		return this.properties;
	}
	
	/**
	 * If this object carries a single property, read it using getValue
	 * 
	 * @return the value of this object
	 */
	public String getValue()
	{
		if(this.properties.size() == 1)
		{
			String key = this.properties.keys().nextElement();
			if(key.trim().length()==0 || 
			   this.calculatePropertyUri(this.listProperty).endsWith(key.trim()))
			{
				return this.properties.elements().nextElement();
			}
		}
		return null;
	}
	
	/**
	 * Sets the single property of this object
	 * 
	 * @param value value to set
	 */
	public void setValue(String value)
	{
		this.properties.put("", value);
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	private String calculatePropertyUri(String propertyExpression) {
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Otimização desta rotina (StringBuffer inútil) e feito com que a "/" só seja colocada se necessário (conforme outras rotinas similares do OpenMobster).
		 */
		if(!propertyExpression.startsWith("/")) {
			propertyExpression = "/" + propertyExpression;
		}
		return StringUtil.replaceAll(propertyExpression, ".", "/");
		
//		StringBuffer buffer = new StringBuffer();		
//		buffer.append("/"+StringUtil.replaceAll(propertyExpression, ".", "/"));
//		return buffer.toString();
	}
}
