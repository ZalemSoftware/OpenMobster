/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.api.camera;

import java.util.Set;
import java.util.HashSet;

/**
 * 
 * @author openmobster@gmail.com
 */
public final class CloudPhoto
{
	private String oid;
	
	private byte[] photo; //required
	private String fullName; //required
	private String mimeType; //optional
	private Set<String> tags; //optional
	
	public CloudPhoto()
	{
		this.tags = new HashSet<String>();
	}
	
	public String getOid()
	{
		return oid;
	}
	public void setOid(String oid)
	{
		this.oid = oid;
	}
	public byte[] getPhoto()
	{
		return photo;
	}
	public void setPhoto(byte[] photo)
	{
		this.photo = photo;
	}
	public String getFullName()
	{
		return fullName;
	}
	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}
	public String getMimeType()
	{
		return mimeType;
	}
	public void setMimeType(String mimeType)
	{
		this.mimeType = mimeType;
	}
	
	public void addTag(String tag)
	{
		this.tags.add(tag);
	}
	
	public void removeTag(String tag)
	{
		this.tags.remove(tag);
	}
	
	public Set<String> getTags()
	{
		return this.tags;
	}
}
