/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.api;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.openmobster.core.mobileCloud.android.testsuite.Test;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.android.storage.Database;
import org.openmobster.core.mobileCloud.android.storage.Record;

import org.openmobster.core.mobileCloud.api.camera.CloudCamera;
import org.openmobster.core.mobileCloud.api.camera.CloudPhoto;

/**
 * 
 * @author openmobster@gmail.com
 */
public class TestCloudCamera extends Test
{
	private Database db;
	
	public void setUp()
	{
		try
		{
			super.setUp();
			db = Database.getInstance(Registry.getActiveInstance().getContext());
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public void tearDown()
	{
		try
		{
			super.tearDown();
			CloudCamera.getInstance().removeAll();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void runTest()
	{
		try
		{
			//Store with readall
			this.setUp();
			this.storeWithReadById();
			this.tearDown();
			
			//Store with readByTags
			this.setUp();
			this.storeWithReadByTags();
			this.tearDown();
			
			//Store with readByTagsNoMatch
			this.setUp();
			this.storeWithReadByTagsNoMatch();
			this.tearDown();
			
			//Store with readByAll
			this.setUp();
			this.storeWithReadByAll();
			this.tearDown();
			
			//STore with remove
			this.setUp();
			this.storeWithRemove();
			this.tearDown();
			
			//STore with sync
			this.setUp();
			this.storeWithSync();
			this.tearDown();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e);
		}
	}
	
	private void storeWithReadById() throws Exception
	{
		CloudPhoto photo = new CloudPhoto();
		photo.setFullName("blah.jpg");
		photo.setPhoto("blahblah".getBytes());
		photo.setMimeType("image/jpeg");
		
		String oid = CloudCamera.getInstance().store(photo);
		
		photo = CloudCamera.getInstance().readById(oid);
		
		String fullname = photo.getFullName();
		String content = new String(photo.getPhoto());
		String mime = photo.getMimeType();
		System.out.println("FullName: "+fullname);
		System.out.println("Content: "+content);
		System.out.println("Mime: "+mime);
		
		this.assertEquals(fullname, "blah.jpg", "/readbyid/fullname_check");
		this.assertEquals(content, "blahblah", "/readbyid/content_check");
		this.assertEquals(mime, "image/jpeg", "/readbyid/mime_check");
	}
	
	private void storeWithReadByTags() throws Exception
	{
		for(int i=0; i<3; i++)
		{
			CloudPhoto photo = new CloudPhoto();
			photo.setFullName("blah.jpg");
			photo.setPhoto("blahblah".getBytes());
			photo.setMimeType("image/jpeg");
			for(int j=0; j<5; j++)
			{
				photo.addTag("tag:"+j);
			}
			
			CloudCamera.getInstance().store(photo);
		}
		
		Set<String> tags = new HashSet<String>();
		for(int i=0; i<3; i++)
		{
			tags.add("tag:"+i);
		}
		List<CloudPhoto> photos = CloudCamera.getInstance().readByTags(tags);
		
		for(CloudPhoto local:photos)
		{
			System.out.println("*********************************");
			String fullname = local.getFullName();
			String content = new String(local.getPhoto());
			String mime = local.getMimeType();
			
			System.out.println("FullName: "+fullname);
			System.out.println("Content: "+content);
			System.out.println("Mime: "+mime);
			
			this.assertEquals(fullname, "blah.jpg", "/readbytags/fullname_check");
			this.assertEquals(content, "blahblah", "/readbytags/content_check");
			this.assertEquals(mime, "image/jpeg", "/readbytags/mime_check");
		}
	}
	
	private void storeWithReadByTagsNoMatch() throws Exception
	{
		for(int i=0; i<3; i++)
		{
			CloudPhoto photo = new CloudPhoto();
			photo.setFullName("blah.jpg");
			photo.setPhoto("blahblah".getBytes());
			photo.setMimeType("image/jpeg");
			for(int j=0; j<5; j++)
			{
				photo.addTag("tag:"+j);
			}
			
			CloudCamera.getInstance().store(photo);
		}
		
		Set<String> tags = new HashSet<String>();
		for(int i=0; i<10; i++)
		{
			tags.add("tag:"+i);
		}
		
		List<CloudPhoto> photos = CloudCamera.getInstance().readByTags(tags);
		boolean notFound = photos == null || photos.size() ==0;
		
		this.assertTrue(notFound, "/store_with_nomatch");
	}
	
	private void storeWithReadByAll() throws Exception
	{
		for(int i=0; i<3; i++)
		{
			CloudPhoto photo = new CloudPhoto();
			photo.setFullName("blah.jpg");
			photo.setPhoto("blahblah".getBytes());
			photo.setMimeType("image/jpeg");
			for(int j=0; j<5; j++)
			{
				photo.addTag("tag:"+j);
			}
			
			CloudCamera.getInstance().store(photo);
		}
		
		List<CloudPhoto> photos = CloudCamera.getInstance().readAll();
		
		for(CloudPhoto local:photos)
		{
			System.out.println("*********************************");
			String fullname = local.getFullName();
			String content = new String(local.getPhoto());
			String mime = local.getMimeType();
			
			System.out.println("FullName: "+fullname);
			System.out.println("Content: "+content);
			System.out.println("Mime: "+mime);
			
			this.assertEquals(fullname, "blah.jpg", "/readbytags/fullname_check");
			this.assertEquals(content, "blahblah", "/readbytags/content_check");
			this.assertEquals(mime, "image/jpeg", "/readbytags/mime_check");
		}
	}
	
	private void storeWithRemove() throws Exception
	{
		for(int i=0; i<3; i++)
		{
			CloudPhoto photo = new CloudPhoto();
			photo.setFullName("blah.jpg");
			photo.setPhoto("blahblah".getBytes());
			photo.setMimeType("image/jpeg");
			for(int j=0; j<5; j++)
			{
				photo.addTag("tag:"+j);
			}
			
			CloudCamera.getInstance().store(photo);
		}
		
		List<CloudPhoto> photos = CloudCamera.getInstance().readAll();
		
		for(CloudPhoto local:photos)
		{
			System.out.println("*********************************");
			String fullname = local.getFullName();
			String content = new String(local.getPhoto());
			String mime = local.getMimeType();
			
			System.out.println("FullName: "+fullname);
			System.out.println("Content: "+content);
			System.out.println("Mime: "+mime);
			
			this.assertEquals(fullname, "blah.jpg", "/readbytags/fullname_check");
			this.assertEquals(content, "blahblah", "/readbytags/content_check");
			this.assertEquals(mime, "image/jpeg", "/readbytags/mime_check");
			
			CloudCamera.getInstance().remove(local);
		}
		
		photos = CloudCamera.getInstance().readAll();
		boolean notFound = photos == null || photos.size() ==0;
		
		this.assertTrue(notFound, "/store_with_remove");
	}
	
	private void storeWithSync() throws Exception
	{
		for(int i=0; i<3; i++)
		{
			CloudPhoto photo = new CloudPhoto();
			photo.setFullName("blah.jpg");
			photo.setPhoto("blahblah".getBytes());
			photo.setMimeType("image/jpeg");
			for(int j=0; j<5; j++)
			{
				photo.addTag("tag:"+j);
			}
			
			CloudCamera.getInstance().store(photo);
		}
		
		CloudCamera.getInstance().syncWithCloud("/upload/picture");
	}
}
