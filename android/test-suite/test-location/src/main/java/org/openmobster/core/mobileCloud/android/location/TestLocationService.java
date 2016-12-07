/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmobster.core.mobileCloud.android.testsuite.Test;
import org.openmobster.android.api.location.LocationContext;
import org.openmobster.android.api.location.LocationService;
import org.openmobster.android.api.location.Request;
import org.openmobster.android.api.location.Place;
import org.openmobster.android.api.location.Address;

/**
 *
 * @author openmobster@gmail.com
 */
public class TestLocationService extends Test
{
	@Override
	public void runTest()
	{
		try
		{
			System.out.println("Running testByCoordinates.......");
			this.testByCoordinates();
			
			System.out.println("Running testAddress.......");
			this.testByAddress();
			
			System.out.println("Running testValidation.......");
			this.testValidation();
			
			System.out.println("Running testGetPlaceDetails.......");
			this.testGetPlaceDetails();
			
			System.out.println("Running testBySearchName.......");
			this.testBySearchName();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void testByCoordinates() throws Exception
	{
		LocationService service = new LocationService();
		
		Request request = new Request("friends");
		
		LocationContext locationContext = new LocationContext();
		locationContext.setAttribute("request", request);
		
		//Add a list
		List<String> list = new ArrayList<String>();
		list.add("value://0");
		list.add("value://1");
		list.add("value://2");
		request.setListAttribute("mylist", list);
		
		//Add a Map
		Map<String,String> map = new HashMap<String,String>();
		map.put("name1", "value1");
		map.put("name2", "value2");
		map.put("name3", "value3");
		request.setMapAttribute("myMap", map);
		
		for(int i=0; i<5; i++)
		{
			request.setAttribute("param"+i, "value"+i);
		}
		
		//LocationContext
		locationContext.setLatitude("-33.8670522");
		locationContext.setLongitude("151.1957362");
		locationContext.setPlaceReference("123456789");
		List<String> placeTypes = new ArrayList<String>();
		placeTypes.add("food");
		placeTypes.add("grocery");
		placeTypes.add("restaurant");
		locationContext.setPlaceTypes(placeTypes);
		for(int i=0; i<5; i++)
		{
			locationContext.setAttribute("name"+i, "value"+i);
		}
		locationContext.setRadius(1000);
		
		LocationContext responseContext = service.invoke(request, locationContext);
		
		//Place Details
		Place placeDetails = responseContext.getPlaceDetails();
		System.out.println("Place Details: "+placeDetails.getName());
		
		//Get nearby places
		List<Place> nearbyPlaces = responseContext.getNearbyPlaces();
		assertTrue(nearbyPlaces != null && !nearbyPlaces.isEmpty(),"nearbyPlaces/null/check");
		for(Place place:nearbyPlaces)
		{
			System.out.println("Name: "+place.getName());
		}
		
		//Place Reference
		System.out.println("Place Reference: "+responseContext.getPlaceReference());
		assertEquals(responseContext.getPlaceReference(),"123456789","placeReference/value/check");
		
		//Place Types
		List<String> types = responseContext.getPlaceTypes();
		assertTrue(types != null && !types.isEmpty(),"placeTypes/null/check");
		for(String type:types)
		{
			System.out.println("Place Type: "+type);
		}
		
		//Address
		Address address = responseContext.getAddress();
		System.out.println("Address Street"+address.getStreet());
		System.out.println("Address City"+address.getCity());
		System.out.println("Address Zip"+address.getZipCode());
		assertEquals(address.getStreet(),"37 Pirrama Rd","address/street/check");
		assertEquals(address.getCity(),"Sydney","address/city/check");
		assertEquals(address.getZipCode(),"2009","address/zip/check");
		
		System.out.println("Latitude: "+responseContext.getLatitude());
		System.out.println("Longitude: "+responseContext.getLongitude());
		assertEquals(address.getLatitude(),"-33.867052","latitude/check");
		assertEquals(address.getLongitude(),"151.195736","longitude/check");
	}
	
	private void testByAddress() throws Exception
	{
		LocationService service = new LocationService();
		
		Request request = new Request("friends");
		
		LocationContext locationContext = new LocationContext();
		locationContext.setAttribute("request", request);
		
		//Add a list
		List<String> list = new ArrayList<String>();
		list.add("value://0");
		list.add("value://1");
		list.add("value://2");
		request.setListAttribute("mylist", list);
		
		//Add a Map
		Map<String,String> map = new HashMap<String,String>();
		map.put("name1", "value1");
		map.put("name2", "value2");
		map.put("name3", "value3");
		request.setMapAttribute("myMap", map);
		
		for(int i=0; i<5; i++)
		{
			request.setAttribute("param"+i, "value"+i);
		}
		
		//LocationContext
		Address address = new Address();
		address.setStreet("2046 Dogwood Gardens Dr");
		address.setCity("Germantown");
		locationContext.setAddress(address);
		
		locationContext.setPlaceReference("123456789");
		
		List<String> placeTypes = new ArrayList<String>();
		placeTypes.add("food");
		placeTypes.add("grocery");
		placeTypes.add("restaurant");
		locationContext.setPlaceTypes(placeTypes);
		for(int i=0; i<5; i++)
		{
			locationContext.setAttribute("name"+i, "value"+i);
		}
		locationContext.setRadius(1000);
		
		LocationContext responseContext = service.invoke(request, locationContext);
		
		//Place Details
		Place placeDetails = responseContext.getPlaceDetails();
		System.out.println("Place Details: "+placeDetails.getName());
		
		//Get nearby places
		List<Place> nearbyPlaces = responseContext.getNearbyPlaces();
		assertTrue(nearbyPlaces != null && !nearbyPlaces.isEmpty(),"nearbyPlaces/null/check");
		for(Place place:nearbyPlaces)
		{
			System.out.println("Name: "+place.getName());
		}
		
		//Place Reference
		System.out.println("Place Reference: "+responseContext.getPlaceReference());
		assertEquals(responseContext.getPlaceReference(),"123456789","placeReference/value/check");
		
		//Place Types
		List<String> types = responseContext.getPlaceTypes();
		assertTrue(types != null && !types.isEmpty(),"placeTypes/null/check");
		for(String type:types)
		{
			System.out.println("Place Type: "+type);
		}
		
		//Address
		address = responseContext.getAddress();
		System.out.println("Address Street"+address.getStreet());
		System.out.println("Address City"+address.getCity());
		System.out.println("Address Zip"+address.getZipCode());
		assertEquals(address.getStreet(),"2046 Dogwood Gardens Dr","address/street/check");
		assertEquals(address.getCity(),"Germantown","address/city/check");
		assertEquals(address.getZipCode(),"38139","address/zip/check");
		
		System.out.println("Latitude: "+responseContext.getLatitude());
		System.out.println("Longitude: "+responseContext.getLongitude());
		assertEquals(address.getLatitude(),"35.093039","latitude/check");
		assertEquals(address.getLongitude(),"-89.733933","longitude/check");
	}
	
	private void testBySearchName() throws Exception
	{
		LocationService service = new LocationService();
		
		Request request = new Request("friends");
		
		LocationContext locationContext = new LocationContext();
		locationContext.setAttribute("request", request);
		
		//Add a list
		List<String> list = new ArrayList<String>();
		list.add("value://0");
		list.add("value://1");
		list.add("value://2");
		request.setListAttribute("mylist", list);
		
		//Add a Map
		Map<String,String> map = new HashMap<String,String>();
		map.put("name1", "value1");
		map.put("name2", "value2");
		map.put("name3", "value3");
		request.setMapAttribute("myMap", map);
		
		for(int i=0; i<5; i++)
		{
			request.setAttribute("param"+i, "value"+i);
		}
		
		//LocationContext
		Address address = new Address();
		address.setStreet("2046 Dogwood Gardens Dr");
		address.setCity("Germantown");
		locationContext.setAddress(address);
		
		locationContext.setPlaceReference("123456789");
		
		List<String> placeTypes = new ArrayList<String>();
		placeTypes.add("food");
		placeTypes.add("grocery");
		placeTypes.add("restaurant");
		locationContext.setPlaceTypes(placeTypes);
		for(int i=0; i<5; i++)
		{
			locationContext.setAttribute("name"+i, "value"+i);
		}
		locationContext.setRadius(1000);
		locationContext.setSearchName("mulan");
		
		LocationContext responseContext = service.invoke(request, locationContext);
		
		//Place Details
		Place placeDetails = responseContext.getPlaceDetails();
		System.out.println("Place Details: "+placeDetails.getName());
		
		//Get nearby places
		List<Place> nearbyPlaces = responseContext.getNearbyPlaces();
		assertTrue(nearbyPlaces != null && !nearbyPlaces.isEmpty(),"nearbyPlaces/null/check");
		for(Place place:nearbyPlaces)
		{
			System.out.println("Name: "+place.getName());
		}
		
		//Place Reference
		System.out.println("Place Reference: "+responseContext.getPlaceReference());
		assertEquals(responseContext.getPlaceReference(),"123456789","placeReference/value/check");
		
		//Place Types
		List<String> types = responseContext.getPlaceTypes();
		assertTrue(types != null && !types.isEmpty(),"placeTypes/null/check");
		for(String type:types)
		{
			System.out.println("Place Type: "+type);
		}
		
		//Address
		address = responseContext.getAddress();
		System.out.println("Address Street"+address.getStreet());
		System.out.println("Address City"+address.getCity());
		System.out.println("Address Zip"+address.getZipCode());
		assertEquals(address.getStreet(),"2046 Dogwood Gardens Dr","address/street/check");
		assertEquals(address.getCity(),"Germantown","address/city/check");
		assertEquals(address.getZipCode(),"38139","address/zip/check");
		
		System.out.println("Latitude: "+responseContext.getLatitude());
		System.out.println("Longitude: "+responseContext.getLongitude());
		assertEquals(address.getLatitude(),"35.093039","latitude/check");
		assertEquals(address.getLongitude(),"-89.733933","longitude/check");
	}
	
	private void testValidation() throws Exception
	{
		LocationContext context = new LocationContext();
		
		LocationService service = new LocationService();
		
		boolean validationFailure = false;
		try
		{
			service.invoke(null, context);
		}
		catch(IllegalStateException ise)
		{
			validationFailure = true;
		}
		
		assertTrue(validationFailure,"/validation/exception");
	}
	
	private void testGetPlaceDetails() throws Exception
	{
		Request request = new Request("friends");
		
		LocationContext locationContext = new LocationContext();
		locationContext.setAttribute("request", request);
		
		//LocationContext
		Address address = new Address();
		address.setStreet("2046 Dogwood Gardens Dr");
		address.setCity("Germantown");
		locationContext.setAddress(address);
		
		locationContext.setRadius(1000);
		
		LocationContext responseContext = LocationService.invoke(request, locationContext);
		
		//Get nearby places
		List<Place> nearbyPlaces = responseContext.getNearbyPlaces();
		assertTrue(nearbyPlaces != null && !nearbyPlaces.isEmpty(),"nearbyPlaces/null/check");
		
		Place detailedPlace = nearbyPlaces.get(0);
		String placeReference = detailedPlace.getReference();
		System.out.println("Reference: "+placeReference);
		
		System.out.println("********************************************");
		System.out.println("Address:"+detailedPlace.getAddress());
		System.out.println("Phone:"+detailedPlace.getPhone());
		System.out.println("InternationalPhoneNumber:"+detailedPlace.getInternationalPhoneNumber());
		System.out.println("Url:"+detailedPlace.getUrl());
		System.out.println("Website:"+detailedPlace.getWebsite());
		System.out.println("Icon:"+detailedPlace.getIcon());
		System.out.println("Name:"+detailedPlace.getName());
		System.out.println("Latitude:"+detailedPlace.getLatitude());
		System.out.println("Longitude:"+detailedPlace.getLongitude());
		System.out.println("Id:"+detailedPlace.getId());
		System.out.println("Rating:"+detailedPlace.getRating());
		System.out.println("Reference:"+detailedPlace.getReference());
		System.out.println("vicintty:"+detailedPlace.getVicinity());
		System.out.println("HTMLAttribution:"+detailedPlace.getHtmlAttribution());
		
		assertTrue(detailedPlace.getAddress() ==null,"");
		
		detailedPlace = LocationService.getPlaceDetails(placeReference);
		
		System.out.println("********************************************");
		System.out.println("Address:"+detailedPlace.getAddress());
		System.out.println("Phone:"+detailedPlace.getPhone());
		System.out.println("InternationalPhoneNumber:"+detailedPlace.getInternationalPhoneNumber());
		System.out.println("Url:"+detailedPlace.getUrl());
		System.out.println("Website:"+detailedPlace.getWebsite());
		System.out.println("Icon:"+detailedPlace.getIcon());
		System.out.println("Name:"+detailedPlace.getName());
		System.out.println("Latitude:"+detailedPlace.getLatitude());
		System.out.println("Longitude:"+detailedPlace.getLongitude());
		System.out.println("Id:"+detailedPlace.getId());
		System.out.println("Rating:"+detailedPlace.getRating());
		System.out.println("Reference:"+detailedPlace.getReference());
		System.out.println("vicintty:"+detailedPlace.getVicinity());
		System.out.println("HTMLAttribution:"+detailedPlace.getHtmlAttribution());
		
		assertTrue(detailedPlace.getAddress() !=null,"");
	}
}
