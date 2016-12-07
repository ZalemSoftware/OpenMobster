/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileObject.xml;

import java.util.List;

/**
 *
 * @author openmobster@gmail.com
 */
public class Blobs
{
	private String name;
	
	//does not work with serialization
	private List<byte[]> blobs;
	
	//does not work with serialization
	private List<String[]> strings;
	
	//works fine with serialization
	private List<Attachment> attachments;
	
	public Blobs()
	{
		
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<byte[]> getBlobs()
	{
		return blobs;
	}

	public void setBlobs(List<byte[]> blobs)
	{
		this.blobs = blobs;
	}

	public List<String[]> getStrings()
	{
		return strings;
	}

	public void setStrings(List<String[]> strings)
	{
		this.strings = strings;
	}

	public List<Attachment> getAttachments()
	{
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments)
	{
		this.attachments = attachments;
	}
}
