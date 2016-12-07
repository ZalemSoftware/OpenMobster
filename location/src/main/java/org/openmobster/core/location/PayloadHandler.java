/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;
import org.openmobster.cloud.api.location.LocationContext;
import org.openmobster.cloud.api.location.Request;
import org.openmobster.cloud.api.location.Response;
import org.openmobster.cloud.api.location.Address;
import org.openmobster.cloud.api.location.Place;
import org.openmobster.core.common.XMLUtilities;
import org.openmobster.core.common.errors.SystemException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author openmobster@gmail.com
 */
public final class PayloadHandler
{
	public PayloadHandler()
	{
		
	}
	
	public LocationContext deserializeRequest(String payload)
	{
		try
		{
			Document document = XMLUtilities.parse(payload);
			
			//Request
			Element locationRequest = (Element)document.getElementsByTagName("location-request").item(0);
			
			//Service
			Element serviceElement = (Element)locationRequest.getElementsByTagName("service").item(0);
			String service = serviceElement.getTextContent();
			
			//Request Payload
			Element requestPayloadElement = (Element)locationRequest.getElementsByTagName("request-payload").item(0);
			String requestPayload = requestPayloadElement.getTextContent();
			
			//Location Payload
			Element locationPayloadElement = (Element)locationRequest.getElementsByTagName("location-payload").item(0);
			String locationPayload = locationPayloadElement.getTextContent();
			
			//Assemble the Request object
			Request request = new Request(service);
			JSONParser parser = new JSONParser();
			JSONObject parsedRequest = (JSONObject)parser.parse(requestPayload);
			
			//get the keys
			Set<String> names = parsedRequest.keySet();
			for(String name:names)
			{
				Object value = parsedRequest.get(name);
				
				if(value instanceof JSONArray)
				{
					JSONArray array = (JSONArray)value;
					List<String> list = new ArrayList<String>();
					
					//list attribute
					int length = array.size();
					for(int i=0; i<length; i++)
					{
						String local = (String)array.get(i);
						list.add(local);
					}
					
					request.setListAttribute(name, list);
				}
				else if(value instanceof JSONObject)
				{
					//map attribute
					JSONObject map = (JSONObject)value;
					Map<String,String> mapAttr = new HashMap<String,String>();
					
					Set<String> keys = map.keySet();
					for(String key:keys)
					{
						String local = (String)map.get(key);
						mapAttr.put(key, local);
					}
					
					request.setMapAttribute(name, mapAttr);
				}
				else if(value instanceof String)
				{
					//string attribute
					request.setAttribute(name, (String)value);
				}
			}
			
			//Assemble the LocationContext
			LocationContext locationContext = new LocationContext();
			parsedRequest = (JSONObject)parser.parse(locationPayload);
			
			//Latitude Longitude data
			String latitude = (String)parsedRequest.get("latitude");
			String longitude = (String)parsedRequest.get("longitude");
			if(latitude != null)
			{
				locationContext.setLatitude(latitude);
			}
			if(longitude != null)
			{
				locationContext.setLongitude(longitude);
			}
			
			//Place Types
			JSONArray placeTypes = (JSONArray)parsedRequest.get("placeTypes");
			if(placeTypes != null)
			{
				List<String> list = new ArrayList<String>();
				int length = placeTypes.size();
				for(int i=0; i<length; i++)
				{
					String placeType = (String)placeTypes.get(i);
					list.add(placeType);
				}
				locationContext.setPlaceTypes(list);
			}
			
			//Place Reference
			String placeReference = (String)parsedRequest.get("placeReference");
			if(placeReference != null)
			{
				locationContext.setPlaceReference(placeReference);
			}
			
			locationContext.setAttribute("request", request);
			
			//Address
			JSONObject address = (JSONObject)parsedRequest.get("address");
			if(address != null)
			{
				Address incomingAddress = this.deserializeAddress(address);
				locationContext.setAddress(incomingAddress);
			}
			
			//Rest of the Context
			Set<String> keys = parsedRequest.keySet();
			if(keys != null && !keys.isEmpty())
			{
				for(String key:keys)
				{
					Object value = parsedRequest.get(key);
					if(value instanceof String)
					{
						locationContext.setAttribute(key, (String)value);
					}
				}
			}
			
			return locationContext;
		}
		catch(Exception e)
		{
			throw new SystemException(e.getMessage());
		}
	}
	
	public String serializeResponse(LocationContext locationContext)
	{
		Response response = (Response)locationContext.getAttribute("response");
		
		//Prepare the response JSONObject
		JSONObject responseJSON = new JSONObject();
		String[] names = response.getNames();
		if(names != null && names.length>0)
		{
			for(String name:names)
			{
				Object value = response.get(name);
				responseJSON.put(name, value);
			}
		}
		
		//Prepare the location context JSONObject
		JSONObject locationJSON = new JSONObject();
		
		//Places
		List<Place> places = locationContext.getNearbyPlaces();
		if(places != null && !places.isEmpty())
		{
			JSONArray placesJSON = new JSONArray();
			for(Place place:places)
			{
				JSONObject placeJSON = this.serializePlace(place);
				placesJSON.add(placeJSON);
			}
			locationJSON.put("places", placesJSON);
		}
		
		//Place Details
		Place placeDetails = locationContext.getPlaceDetails();
		if(placeDetails != null)
		{
			JSONObject placeDetailsJSON = this.serializePlace(placeDetails);
			locationJSON.put("placeDetails", placeDetailsJSON);
		}
		
		//PlaceTypes
		List<String> placeTypes = locationContext.getPlaceTypes();
		if(placeTypes != null && !placeTypes.isEmpty())
		{
			JSONArray placeTypesJSON = new JSONArray();
			for(String placeType:placeTypes)
			{
				placeTypesJSON.add(placeType);
			}
			locationJSON.put("placeTypes", placeTypesJSON);
		}
		
		//Address
		Address address = locationContext.getAddress();
		if(address != null)
		{
			JSONObject addressJSON = this.serializeAddress(address);
			locationJSON.put("address", addressJSON);
		}
		
		//Add the rest of the context
		names = locationContext.getNames();
		if(names != null && names.length>0)
		{
			for(String name:names)
			{
				Object value = locationContext.getAttribute(name);
				if(value instanceof String)
				{
					locationJSON.put(name, (String)value);
				}
			}
		}
		
		String responsePayload = responseJSON.toJSONString();
		String locationPayload = locationJSON.toJSONString();
		
		StringBuilder buffer = new StringBuilder();
		buffer.append("<location-response>\n");
		buffer.append("<response-payload>"+XMLUtilities.addCData(responsePayload)+"</response-payload>\n");
		buffer.append("<location-payload>"+XMLUtilities.addCData(locationPayload)+"</location-payload>\n");
		buffer.append("</location-response>\n");
		
		String xml = buffer.toString();
		
		return xml;
	}
	//---------------------------------------------------------------------------------------------
	private JSONObject serializePlace(Place place)
	{
		JSONObject json = new JSONObject();
		
		if(place.getAddress() != null && place.getAddress().trim().length()>0)
		{
			json.put("address", place.getAddress());
		}
		if(place.getPhone() != null && place.getPhone().trim().length()>0)
		{
			json.put("phone", place.getPhone());
		}
		if(place.getInternationalPhoneNumber() != null && place.getInternationalPhoneNumber().trim().length()>0)
		{
			json.put("international_phone_number", place.getInternationalPhoneNumber());
		}
		if(place.getUrl() != null && place.getUrl().trim().length()>0)
		{
			json.put("url", place.getUrl());
		}
		if(place.getWebsite() != null && place.getWebsite().trim().length()>0)
		{
			json.put("website", place.getWebsite());
		}
		if(place.getIcon() != null && place.getIcon().trim().length()>0)
		{
			json.put("icon", place.getIcon());
		}
		if(place.getName() != null && place.getName().trim().length()>0)
		{
			json.put("name", place.getName());
		}
		if(place.getLatitude() != null && place.getLatitude().trim().length()>0)
		{
			json.put("latitude", place.getLatitude());
		}
		if(place.getLongitude() != null && place.getLongitude().trim().length()>0)
		{
			json.put("longitude", place.getLongitude());
		}
		if(place.getId() != null && place.getId().trim().length()>0)
		{
			json.put("id", place.getId());
		}
		if(place.getReference() != null && place.getReference().trim().length()>0)
		{
			json.put("reference", place.getReference());
		}
		if(place.getRating() != null && place.getRating().trim().length()>0)
		{
			json.put("rating", place.getRating());
		}
		if(place.getVicinity() != null && place.getVicinity().trim().length()>0)
		{
			json.put("vicinity", place.getVicinity());
		}
		if(place.getHtmlAttribution() != null && place.getHtmlAttribution().trim().length()>0)
		{
			json.put("html_attribution", place.getHtmlAttribution());
		}
		if(place.getTypes() != null && !place.getTypes().isEmpty())
		{
			json.put("types", place.getTypes());
		}
		
		return json;
	}
	
	private JSONObject serializeAddress(Address address)
	{
		JSONObject json = new JSONObject();
		
		if(address.getStreet() != null && address.getStreet().trim().length()>0)
		{
			json.put("street", address.getStreet());
		}
		if(address.getCity() != null && address.getCity().trim().length()>0)
		{
			json.put("city", address.getCity());
		}
		if(address.getState() != null && address.getState().trim().length()>0)
		{
			json.put("state", address.getState());
		}
		if(address.getCountry() != null && address.getCountry().trim().length()>0)
		{
			json.put("country", address.getCountry());
		}
		if(address.getZipCode() != null && address.getZipCode().trim().length()>0)
		{
			json.put("zipcode", address.getZipCode());
		}
		if(address.getCounty() != null && address.getCounty().trim().length()>0)
		{
			json.put("county", address.getCounty());
		}
		if(address.getPostal() != null && address.getPostal().trim().length()>0)
		{
			json.put("postal", address.getPostal());
		}
		if(address.getLatitude() != null && address.getLatitude().trim().length()>0)
		{
			json.put("latitude", address.getLatitude());
		}
		if(address.getLongitude() != null && address.getLongitude().trim().length()>0)
		{
			json.put("longitude", address.getLongitude());
		}
		if(address.getRadius() != null && address.getRadius().trim().length()>0)
		{
			json.put("radius", address.getRadius());
		}
		if(address.getWoeid() != null && address.getWoeid().trim().length()>0)
		{
			json.put("woeid", address.getWoeid());
		}
		if(address.getWoetype() != null && address.getWoetype().trim().length()>0)
		{
			json.put("woetype", address.getWoetype());
		}
		return json;
	}
	
	private Address deserializeAddress(JSONObject object)
	{
		Address address = new Address();
		
		String street = (String)object.get("street");
		address.setStreet(street);
		
		String city = (String)object.get("city");
		address.setCity(city);
		
		String value = (String)object.get("state");
		address.setState(value);
		
		value = (String)object.get("country");
		address.setCountry(value);
		
		value = (String)object.get("zipcode");
		address.setZipCode(value);
		
		value = (String)object.get("county");
		address.setCounty(value);
		
		value = (String)object.get("postal");
		address.setPostal(value);
		
		value = (String)object.get("latitude");
		address.setLatitude(value);
		
		value = (String)object.get("longitude");
		address.setLongitude(value);
		
		value = (String)object.get("radius");
		address.setRadius(value);
		
		value = (String)object.get("woetype");
		address.setWoetype(value);
		
		value = (String)object.get("woeid");
		address.setWoeid(value);
		
		return address;
	}
}
