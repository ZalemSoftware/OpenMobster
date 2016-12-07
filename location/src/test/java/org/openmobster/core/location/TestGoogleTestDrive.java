/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.openmobster.core.common.ServiceManager;

import junit.framework.TestCase;

/**
 *
 * @author openmobster@gmail.com
 */
public class TestGoogleTestDrive extends TestCase
{
	private static Logger log = Logger.getLogger(TestGoogleTestDrive.class);
	
	private PlaceProvider placeProvider;
	
	public void setUp()
	{
		ServiceManager.bootstrap();
		placeProvider = (PlaceProvider)ServiceManager.locate("Places");
	}
	
	public void tearDown()
	{
		ServiceManager.shutdown();
	}
	
	public void testPlacesRequest() throws Exception
	{
		log.info("Starting Google Place Search........");
		
		String apiKey = "AIzaSyAntv38LTmUTBSlSLHzX-XbfNFcl4F5zrA";
		String url = "https://maps.googleapis.com/maps/api/place/search/xml?location=-33.8670522,151.1957362" +
				"&radius=500&types=food&name=harbour&sensor=false&key="+apiKey;
		
		//setup the request object
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		
		//send the request
		HttpResponse response = client.execute(request);
		
		//read the response
		String result = null;
		HttpEntity entity = response.getEntity();
		if(entity != null)
		{
			result = EntityUtils.toString(entity);
		}
		
		//process
		System.out.println(result);
	}
	
	public void testFetchNearbyPlaces() throws Exception
	{
		log.info("Starting fetchNearbyPlaces....");
		
		String latitude = "35.09315";
		String longitude = "-89.73365";
		List<String> types = new ArrayList<String>();
		types.add("food");
		types.add("hospital");
		
		List<PlaceSPI> places = this.placeProvider.fetchNearbyPlaces(latitude, longitude, types, 500,null);
		assertTrue(places !=null && !places.isEmpty());
		
		for(PlaceSPI place:places)
		{
			place = this.placeProvider.fetchPlace(place.getReference());
			
			String name = place.getName();
			log.info("Name: "+name);
			
			String address = place.getAddress();
			log.info("Address: "+address);
			
			String phone = place.getPhone();
			log.info("Phone: "+phone);
			
			String internationalNumber = place.getInternationalPhoneNumber();
			log.info("International Number: "+internationalNumber);
			
			String url = place.getUrl();
			log.info("Url: "+url);
			
			String website = place.getWebsite();
			log.info("Website: "+website);
			
			String icon = place.getIcon();
			log.info("Icon: "+icon);
			
			String lat = place.getLatitude();
			log.info("Latitude: "+lat);
			
			String lng = place.getLongitude();
			log.info("Longitude: "+lng);
			
			String id = place.getId();
			log.info("Id: "+id);
			
			String reference = place.getReference();
			log.info("Reference: "+reference);
			
			String rating = place.getRating();
			log.info("Rating: "+rating);
			
			String vicinity = place.getVicinity();
			log.info("Vicinity: "+vicinity);
			
			String htmlAttribution = place.getHtmlAttribution();
			log.info("html_attribution: "+htmlAttribution);
			
			List<String> myTypes = place.getTypes();
			if(myTypes != null && !myTypes.isEmpty())
			{
				for(String type:myTypes)
				{
					log.info("Type: "+type);
				}
			}
			
			log.info("***************************");
		}
	}
	
	public void testFetchNearbyPlacesByName() throws Exception
	{
		log.info("Starting fetchNearbyPlacesByName....");
		
		String latitude = "-33.8670522";
		String longitude = "151.1957362";
		List<String> types = new ArrayList<String>();
		types.add("food");
		types.add("hospital");
		
		List<PlaceSPI> places = this.placeProvider.fetchNearbyPlaces(latitude, longitude, types, 500,"lotus");
		assertTrue(places !=null && !places.isEmpty());
		
		for(PlaceSPI place:places)
		{
			place = this.placeProvider.fetchPlace(place.getReference());
			
			String name = place.getName();
			log.info("Name: "+name);
			
			String address = place.getAddress();
			log.info("Address: "+address);
			
			String phone = place.getPhone();
			log.info("Phone: "+phone);
			
			String internationalNumber = place.getInternationalPhoneNumber();
			log.info("International Number: "+internationalNumber);
			
			String url = place.getUrl();
			log.info("Url: "+url);
			
			String website = place.getWebsite();
			log.info("Website: "+website);
			
			String icon = place.getIcon();
			log.info("Icon: "+icon);
			
			String lat = place.getLatitude();
			log.info("Latitude: "+lat);
			
			String lng = place.getLongitude();
			log.info("Longitude: "+lng);
			
			String id = place.getId();
			log.info("Id: "+id);
			
			String reference = place.getReference();
			log.info("Reference: "+reference);
			
			String rating = place.getRating();
			log.info("Rating: "+rating);
			
			String vicinity = place.getVicinity();
			log.info("Vicinity: "+vicinity);
			
			String htmlAttribution = place.getHtmlAttribution();
			log.info("html_attribution: "+htmlAttribution);
			
			List<String> myTypes = place.getTypes();
			if(myTypes != null && !myTypes.isEmpty())
			{
				for(String type:myTypes)
				{
					log.info("Type: "+type);
				}
			}
			
			log.info("***************************");
		}
	}
}
