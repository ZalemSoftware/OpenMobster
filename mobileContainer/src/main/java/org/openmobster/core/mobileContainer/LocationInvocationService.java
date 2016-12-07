/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileContainer;

import java.util.List;
import java.util.ArrayList;

import org.openmobster.core.services.LocationServiceMonitor;

import org.openmobster.cloud.api.location.LocationContext;
import org.openmobster.cloud.api.location.Response;
import org.openmobster.cloud.api.location.Request;
import org.openmobster.cloud.api.location.Place;
import org.openmobster.cloud.api.location.Address;
import org.openmobster.cloud.api.location.LocationServiceBean;
import org.openmobster.cloud.api.ExecutionContext;

import org.openmobster.core.location.GeoCodeProvider;
import org.openmobster.core.location.PlaceProvider;
import org.openmobster.core.location.AddressSPI;
import org.openmobster.core.location.PlaceSPI;

/**
 *
 * @author openmobster@gmail.com
 */
public class LocationInvocationService implements ContainerService
{
	private String id;
	private LocationServiceMonitor locationServiceMonitor;
	private GeoCodeProvider geocoder;
	private PlaceProvider placeProvider;
	
	public LocationInvocationService()
	{
		
	}
	
	public void start()
	{
		
	}
	
	public void stop()
	{
		
	}
	
	
	public LocationServiceMonitor getLocationServiceMonitor()
	{
		return locationServiceMonitor;
	}

	public void setLocationServiceMonitor(
			LocationServiceMonitor locationServiceMonitor)
	{
		this.locationServiceMonitor = locationServiceMonitor;
	}
	
	public GeoCodeProvider getGeocoder()
	{
		return geocoder;
	}

	public void setGeocoder(GeoCodeProvider geocoder)
	{
		this.geocoder = geocoder;
	}
	
	public PlaceProvider getPlaceProvider()
	{
		return placeProvider;
	}

	public void setPlaceProvider(PlaceProvider placeProvider)
	{
		this.placeProvider = placeProvider;
	}

	public void setId(String id)
	{
		this.id = id;
	}
	
	@Override
	public String getId()
	{
		return this.id;
	}

	@Override
	public InvocationResponse execute(Invocation invocation)
			throws InvocationException
	{
		try
		{
			InvocationResponse response = InvocationResponse.getInstance();
			
			LocationContext locationContext = ExecutionContext.getInstance().getLocationContext();
			Request request = invocation.getLocationRequest();
			
			//setup the LocationContext with data
			this.setupLocationContext(locationContext);
			
			//make the service invocation
			String service = request.getService();
			LocationServiceBean bean = this.locationServiceMonitor.lookup(service);
			if(bean == null)
			{
				throw new RuntimeException("InvocationException: "+service+" LocationServiceBean Not Found!!");
			}
			
			Response locationResponse = null;
			try
			{
				locationResponse = bean.invoke(locationContext, request);
				if(locationResponse == null)
				{
					locationResponse = new Response();
					locationResponse.setStatusCode("204");
					locationResponse.setStatusMsg("No Content");
				}
				else
				{
					locationResponse.setStatusCode("200");
					locationResponse.setStatusMsg("OK");
				}
				response.setLocationResponse(locationResponse);
			}
			catch(Throwable t)
			{
				locationResponse = new Response();
				
				locationResponse.setStatusCode("500");
				locationResponse.setStatusMsg("Internal Server Error: "+t.getMessage());
				
				response.setLocationResponse(locationResponse);
			}
		
			return response;
		}
		catch(Exception e)
		{
			InvocationException ine = new InvocationException(e.getMessage());
			throw ine;
		}
	}
	//------------------------------------------------------------------------------------------------------
	private Place parsePlace(PlaceSPI placeSPI)
	{
		Place place = new Place();
		
		place.setAddress(placeSPI.getAddress());
		place.setPhone(placeSPI.getPhone());
		place.setInternationalPhoneNumber(placeSPI.getInternationalPhoneNumber());
		place.setUrl(placeSPI.getUrl());
		place.setWebsite(placeSPI.getWebsite());
		place.setIcon(placeSPI.getIcon());
		place.setName(placeSPI.getName());
		place.setLatitude(placeSPI.getLatitude());
		place.setLongitude(placeSPI.getLongitude());
		place.setId(placeSPI.getId());
		place.setReference(placeSPI.getReference());
		place.setRating(placeSPI.getRating());
		place.setTypes(placeSPI.getTypes());
		place.setVicinity(placeSPI.getVicinity());
		place.setHtmlAttribution(placeSPI.getHtmlAttribution());
		
		return place;
	}
	
	private Address parseAddress(AddressSPI addressSPI)
	{
		Address address = new Address();
		
		address.setStreet(addressSPI.getStreet());
		address.setCity(addressSPI.getCity());
		address.setState(addressSPI.getState());
		address.setZipCode(addressSPI.getZipCode());
		address.setCountry(addressSPI.getCountry());
		address.setCounty(addressSPI.getCounty());
		address.setPostal(addressSPI.getPostal());
		
		address.setLatitude(addressSPI.getLatitude());
		address.setLongitude(addressSPI.getLongitude());
		address.setRadius(addressSPI.getRadius());
		address.setWoeid(address.getWoeid());
		address.setWoetype(addressSPI.getWoetype());
		
		return address;
	}
	
	private Address decideAddress(List<AddressSPI> addresses)
	{
		Address address = null;
		
		if(addresses == null || addresses.isEmpty())
		{
			return null;
		}
		
		AddressSPI selectedAddress = null;
		for(AddressSPI local:addresses)
		{
			String street = local.getStreet();
			if(street != null && street.trim().length()>0)
			{
				selectedAddress = local;
				break;
			}
		}
		
		if(selectedAddress == null)
		{
			selectedAddress = addresses.get(0);
		}
		
		address = this.parseAddress(selectedAddress);
		
		return address;
	}
	
	private void setupLocationContext(LocationContext locationContext) throws Exception
	{
		String latitude = locationContext.getLatitude();
		String longitude = locationContext.getLongitude();
		
		if(latitude == null || latitude.trim().length()==0 || longitude ==null || longitude.trim().length()==0)
		{
			Address address = locationContext.getAddress();
			if(address == null)
			{
				return;
			}
			String street = address.getStreet();
			String city = address.getCity();
			if(street == null || street.trim().length()==0 || city == null || city.trim().length() ==0)
			{
				return;
			}
			
			List<AddressSPI> addresses = this.geocoder.geoCode(address.getStreet(), 
					address.getCity(), 
					address.getState(), 
					address.getCountry(), 
					address.getZipCode());
			Address lookedup = this.decideAddress(addresses);
			if(lookedup == null)
			{
				return;
			}
			
			latitude = lookedup.getLatitude();
			longitude = lookedup.getLongitude();
			locationContext.setAddress(lookedup);
			locationContext.setLatitude(latitude);
			locationContext.setLongitude(longitude);
		}
		else
		{
			//Find the address of this location
			List<AddressSPI> addresses = this.geocoder.reverseGeoCode(latitude, longitude);
			Address address = this.decideAddress(addresses);
			if(address != null)
			{
				locationContext.setAddress(address);
			}
		}
		
		//Find nearby places to this location
		List<String> placeTypes = locationContext.getPlaceTypes();
		int radius = locationContext.getRadius();
		if(radius == 0)
		{
			radius = 500; //default to a decent 500 m radius if not specified
		}
		String searchName = locationContext.getSearchName();
		List<PlaceSPI> nearbyPlaces = this.placeProvider.fetchNearbyPlaces(latitude, longitude, placeTypes,radius,searchName);
		if(nearbyPlaces != null && !nearbyPlaces.isEmpty())
		{
			List<Place> places = new ArrayList<Place>();
			for(PlaceSPI local:nearbyPlaces)
			{
				places.add(this.parsePlace(local));
			}
			locationContext.setNearbyPlaces(places);
		}
		
		//Find any requested place details
		String placeReference = locationContext.getPlaceReference();
		if(placeReference != null && placeReference.trim().length()>0)
		{
			PlaceSPI place = this.placeProvider.fetchPlace(placeReference);
			if(place != null)
			{
				Place details = this.parsePlace(place);
				locationContext.setPlaceDetails(details);
			}
		}
	}
}
