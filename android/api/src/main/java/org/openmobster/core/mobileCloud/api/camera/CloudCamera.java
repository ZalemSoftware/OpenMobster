/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.api.camera;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import org.openmobster.android.api.rpc.MobileService;
import org.openmobster.android.api.rpc.Request;
import org.openmobster.android.api.rpc.Response;
import org.openmobster.core.mobileCloud.android.util.Base64;
import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.storage.Record;
import org.openmobster.core.mobileCloud.android.storage.DBException;
import org.openmobster.core.mobileCloud.android.util.GenericAttributeManager;
import org.openmobster.core.mobileCloud.android.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.android.errors.SystemException;

import android.content.Context;

/**
 * This service connects the local device camera with the Cloud
 * 
 * @author openmobster@gmail.com
 */
public final class CloudCamera
{
	private static CloudCamera singleton;
	private static final String camera_table = "tb_cloud_camera";
	
	private CloudCamera()
	{
		try
		{
			Database db = Database.getInstance(Registry.getActiveInstance().getContext());
			
			if(!db.doesTableExist(camera_table))
			{
				db.createTable(camera_table);
			}
		}
		catch(DBException dbe)
		{
			SystemException syse = new SystemException("CloudCamera", "newInstance", new String[]{
					"Exception: "+dbe.toString()
			});
			ErrorHandler.getInstance().handle(syse);
			throw syse;
		}
	}
	
	public static CloudCamera getInstance()
	{
		if(singleton == null)
		{
			synchronized(CloudCamera.class)
			{
				if(singleton == null)
				{
					singleton = new CloudCamera();
				}
			}
		}
		return singleton;
	}
	
	public CloudPhoto readById(String oid) throws CCException
	{
		try
		{
			if(oid == null || oid.trim().length() ==0)
			{
				return null;
			}
			Context context = Registry.getActiveInstance().getContext();
			Database db = Database.getInstance(context);
			
			Record record = db.select(camera_table, oid);
			if(record != null)
			{
				CloudPhoto photo = this.parse(record);
				return photo;
			}
			
			return null;
		}
		catch(Exception e)
		{
			throw new CCException(e);
		}
	}
	
	public List<CloudPhoto> readAll() throws CCException
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			Database db = Database.getInstance(context);
			
			List<CloudPhoto> all = new ArrayList<CloudPhoto>();
			
			Set<Record> allRecords = db.selectAll(camera_table);
			if(allRecords != null)
			{
				for(Record record:allRecords)
				{
					CloudPhoto photo = this.parse(record);
					all.add(photo);
				}
			}
			
			return all;
		}
		catch(Exception e)
		{
			throw new CCException(e);
		}
	}
	
	public List<CloudPhoto> readByTags(Set<String> tags) throws CCException
	{
		try
		{
			if(tags == null || tags.size() == 0)
			{
				return null;
			}
			
			Context context = Registry.getActiveInstance().getContext();
			Database db = Database.getInstance(context);
			
			List<CloudPhoto> all = new ArrayList<CloudPhoto>();
			
			Set<Record> allRecords = db.selectAll(camera_table);
			if(allRecords != null)
			{
				int tagCount = tags.size();
				for(Record record:allRecords)
				{
					Set<String> storedTags = record.getNames();
					
					int matchCount = 0;
					for(String tag:storedTags)
					{
						if(tag.equals("fullname") || tag.equals("photo") || tag.equals("mime"))
						{
							continue;
						}
						
						if(tags.contains(tag))
						{
							matchCount ++;
						}
					}
					
					if(matchCount == tagCount)
					{
						CloudPhoto photo = this.parse(record);
						all.add(photo);
					}
				}
			}
			
			return all;
		}
		catch(Exception e)
		{
			throw new CCException(e);
		}
	}
	
	public String store(CloudPhoto photo) throws CCException
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			Database db = Database.getInstance(context);
	
			String fullName = photo.getFullName();
			byte[] bin = photo.getPhoto();
			if(fullName == null || fullName.trim().length()==0)
			{
				throw new IllegalStateException("Full Name is required");
			}
			if(bin == null)
			{
				throw new IllegalStateException("Binary data for the photo is required");
			}
			
			Record record = new Record();
			record.setValue("fullname",fullName);
			record.setValue("photo", Base64.encodeBytes(bin));
			if(photo.getMimeType() != null)
			{
				record.setValue("mime", photo.getMimeType());
			}
			
			//Set the tags
			if(photo.getTags() != null)
			{
				Set<String> tagNames = photo.getTags();
				for(String name:tagNames)
				{
					record.setValue(name, name);
				}
			}
			
			String id = db.insert(camera_table, record);
			
			return id;
		}
		catch(Exception e)
		{
			throw new CCException(e);
		}
	}
	
	public void remove(CloudPhoto photo) throws CCException
	{
		try
		{
			if(photo == null || photo.getOid()==null || photo.getOid().trim().length()==0)
			{
				return;
			}
			
			Context context = Registry.getActiveInstance().getContext();
			Database db = Database.getInstance(context);
			
			Record record = db.select(camera_table, photo.getOid());
			if(record != null)
			{
				db.delete(camera_table, record);
			}
		}
		catch(Exception e)
		{
			throw new CCException(e);
		}
	}
	
	public void removeAll() throws CCException
	{
		try
		{
			Context context = Registry.getActiveInstance().getContext();
			Database db = Database.getInstance(context);
			
			db.deleteAll(camera_table);
		}
		catch(Exception e)
		{
			throw new CCException(e);
		}
	}
	
	public void syncWithCloud(String cloudService) throws CCException
	{
		try
		{
			List<CloudPhoto> all = this.readAll();
			
			if(all != null)
			{
				for(CloudPhoto local:all)
				{
					Response response = this.sync(cloudService, local);
					
					if(!response.getStatusCode().equals("200"))
					{
						//abort the operation
						break;
					}
				}
			}
		}
		catch(Exception e)
		{
			throw new CCException(e);
		}
	}
	
	public void syncWithCloud(String cloudService, List<CloudPhoto> photos) throws CCException
	{
		try
		{
			if(photos != null)
			{
				for(CloudPhoto local:photos)
				{
					Response response = this.sync(cloudService, local);
					
					if(!response.getStatusCode().equals("200"))
					{
						//abort the operation
						break;
					}
				}
			}
		}
		catch(Exception e)
		{
			throw new CCException(e);
		}
	}
	//-----------------------------------------------------------------------------------------------------
	private CloudPhoto parse(Record record) throws IOException
	{
		CloudPhoto photo = new CloudPhoto();
		photo.setFullName(record.getValue("fullname"));
		
		String encoded = record.getValue("photo");
		byte[] decoded = Base64.decode(encoded.getBytes());
		photo.setPhoto(decoded);
		
		String mime = record.getValue("mime");
		if(mime != null)
		{
			photo.setMimeType(mime);
		}
		
		Set<String> names = record.getNames();
		for(String name:names)
		{
			if(!name.equals("fullname") && !name.equals("photo") && !name.equals("mime"))
			{
				photo.addTag(name);
			}
		}
		
		photo.setOid(record.getRecordId());
		
		return photo;
	}
	
	private Response sync(String cloudService,CloudPhoto photo) throws Exception
	{
		//Generate an upload request
		Request request = new Request("/cloud/camera");
		request.setAttribute("command", cloudService);
		request.setAttribute("photo", Base64.encodeBytes(photo.getPhoto()));
		request.setAttribute("fullname", photo.getFullName());
		request.setAttribute("mime", photo.getMimeType());
		
		Set<String> tags = photo.getTags();
		if(tags != null)
		{
			for(String tag:tags)
			{
				request.setAttribute(tag, tag);
			}
		}
		
		//TODO: pack other environmental goodies like 'Location', etc for better
		//Cloud-to-Camera integration
		
		Response response = MobileService.invoke(request);
		
		return response;
	}
}
