/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import junit.framework.TestCase;
import org.apache.log4j.Logger;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;

import org.apache.http.HttpResponse;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import org.openmobster.core.common.ServiceManager;

import org.openmobster.core.location.yahoo.*;
import org.openmobster.core.location.GeoCodeProvider;

/**
 *
 * @author openmobster@gmail.com
 */
public class TestPlaceFinderTestDrive extends TestCase 
{
	private static Logger log = Logger.getLogger(TestPlaceFinderTestDrive.class);
	
	private GeoCodeProvider geoCoder;
	
	public void setUp()
	{
		ServiceManager.bootstrap();
		geoCoder = (GeoCodeProvider)ServiceManager.locate("GeoCoder");
	}
	
	public void tearDown()
	{
		ServiceManager.shutdown();
	}
	
	public void testGeoCodeAddress() throws Exception
	{
		log.info("Starting PlaceFinder Address GeoCoding........");
		
		String appId = "91XdMe7k";
		String url = "http://where.yahooapis.com/geocode?location=37.787082+-122.400929&gflags=R&appid="+appId;
		
		//setup the request object
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		
		//send the request
		HttpResponse response = client.execute(request);
		
		//read the response
		String json = null;
		HttpEntity entity = response.getEntity();
		if(entity != null)
		{
			json = EntityUtils.toString(entity);
		}
		
		//process the JSON into an object model
		System.out.println(json);
	}
	
	public void testResultSetSorting() throws Exception
	{
		log.info("Starting ResultSetSorting........");
		List<Result> results = new ArrayList<Result>();
		
		Result result1 = new Result();
		YahooAddress address1 = new YahooAddress();
		address1.setQuality(99);
		result1.setAddress(address1);
		results.add(result1);
		
		Result result4 = new Result();
		YahooAddress address4 = new YahooAddress();
		address4.setQuality(99);
		result4.setAddress(address4);
		results.add(result4);
		
		Result result2 = new Result();
		YahooAddress address2 = new YahooAddress();
		address2.setQuality(98);
		result2.setAddress(address2);
		results.add(result2);
		
		Result result3 = new Result();
		YahooAddress address3 = new YahooAddress();
		address3.setQuality(97);
		result3.setAddress(address3);
		results.add(result3);
		
		Collections.sort(results, new ResultComparator());
		
		for(Result local:results)
		{
			System.out.println(local.getAddress().getQuality());
		}
	}
	
	public void testGeoCoder() throws Exception
	{
		log.info("Starting testGeoCoder");
		
		List<AddressSPI> addresses = this.geoCoder.reverseGeoCode("37.787082", "-122.400929");
		
		assertTrue(addresses != null && !addresses.isEmpty());
		
		AddressSPI address = addresses.get(0);
		
		System.out.println("Street: "+address.getStreet());
		assertEquals("655 Mission St",address.getStreet());
		
		System.out.println("City: "+address.getCity());
		assertEquals("San Francisco",address.getCity());
		
		System.out.println("State: "+address.getState());
		assertEquals("California",address.getState());
		
		System.out.println("Country: "+address.getCountry());
		assertEquals("United States",address.getCountry());
		
		System.out.println("Zip: "+address.getZipCode());
		assertEquals("94105",address.getZipCode());
		
		System.out.println("Postal: "+address.getPostal());
		assertEquals("94105",address.getPostal());
		
		System.out.println("County: "+address.getCounty());
		assertEquals("San Francisco County",address.getCounty());
		
		System.out.println("Latitude: "+address.getLatitude());
		assertEquals("37.787102",address.getLatitude());
		
		System.out.println("Longitude: "+address.getLongitude());
		assertEquals("-122.400963",address.getLongitude());
		
		System.out.println("Radius: "+address.getRadius());
		assertEquals("400",address.getRadius());
		
		System.out.println("woeid: "+address.getWoeid());
		assertEquals("12797156",address.getWoeid());
		
		System.out.println("woetype: "+address.getWoetype());
		assertEquals("11",address.getWoetype());
	}
	
	public void testAddressGeoCoding() throws Exception
	{
		List<AddressSPI> addresses = this.geoCoder.geoCode("2046 Dogwood Gardens Drive", "Germantown", "TN", "USA", "38139");
		assertTrue(addresses != null && !addresses.isEmpty());
		
		System.out.println("Number of Addresses Found: "+addresses.size());
		
		AddressSPI address = addresses.get(0);
		
		System.out.println("Street: "+address.getStreet());
		//assertEquals("655 Mission St",address.getStreet());
		
		System.out.println("City: "+address.getCity());
		//assertEquals("San Francisco",address.getCity());
		
		System.out.println("State: "+address.getState());
		//assertEquals("California",address.getState());
		
		System.out.println("Country: "+address.getCountry());
		//assertEquals("United States",address.getCountry());
		
		System.out.println("Zip: "+address.getZipCode());
		//assertEquals("94105",address.getZipCode());
		
		System.out.println("Postal: "+address.getPostal());
		//assertEquals("94105-4126",address.getPostal());
		
		System.out.println("County: "+address.getCounty());
		//assertEquals("San Francisco County",address.getCounty());
		
		System.out.println("Latitude: "+address.getLatitude());
		//assertEquals("37.787082",address.getLatitude());
		
		System.out.println("Longitude: "+address.getLongitude());
		//assertEquals("-122.400929",address.getLongitude());
		
		System.out.println("Radius: "+address.getRadius());
		//assertEquals("500",address.getRadius());
		
		System.out.println("woeid: "+address.getWoeid());
		//assertEquals("12797156",address.getWoeid());
		
		System.out.println("woetype: "+address.getWoetype());
		//assertEquals("11",address.getWoetype());
	}
}
