/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.security.device;

import java.util.Set;
import java.util.HashSet;

/**
 * 
 * @author openmobster@gmail.com
 */
public class PushApp
{
	private long id;
	private String appId;
	private byte[] certificate;
	private String certificateName;
	private String certificatePassword;
	private Set<String> channels;
	private Set<String> devices;
	
	public PushApp()
	{
		this.channels = new HashSet<String>();
		this.devices = new HashSet<String>();
	}
	
	public long getId()
	{
		return this.id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}

	public String getAppId()
	{
		return appId;
	}

	public void setAppId(String appId)
	{
		this.appId = appId;
	}

	public byte[] getCertificate()
	{
		return certificate;
	}

	public void setCertificate(byte[] certificate)
	{
		this.certificate = certificate;
	}

	public String getCertificatePassword()
	{
		return certificatePassword;
	}

	public void setCertificatePassword(String certificatePassword)
	{
		this.certificatePassword = certificatePassword;
	}
	
	public String getCertificateName()
	{
		return certificateName;
	}

	public void setCertificateName(String certificateName)
	{
		this.certificateName = certificateName;
	}

	public Set<String> getChannels()
	{
		return channels;
	}

	public void setChannels(Set<String> channels)
	{
		this.channels = channels;
	}
	
	public void addChannel(String channel)
	{
		this.channels.add(channel);
	}

	public Set<String> getDevices()
	{
		return devices;
	}

	public void setDevices(Set<String> devices)
	{
		this.devices = devices;
	}
	
	public void addDevice(String device)
	{
		this.devices.add(device);
	}
}
