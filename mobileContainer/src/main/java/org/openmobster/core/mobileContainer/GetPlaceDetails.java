/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileContainer;

import org.apache.log4j.Logger;

import org.openmobster.cloud.api.location.BeanURI;
import org.openmobster.cloud.api.location.LocationContext;
import org.openmobster.cloud.api.location.LocationServiceBean;
import org.openmobster.cloud.api.location.Place;
import org.openmobster.cloud.api.location.Request;
import org.openmobster.cloud.api.location.Response;
import org.openmobster.core.location.PlaceProvider;
import org.openmobster.core.location.PlaceSPI;

/**
 *
 * @author openmobster@gmail.com
 */
@BeanURI(uri="placeDetails")
public class GetPlaceDetails implements LocationServiceBean
{
	private static Logger log = Logger.getLogger(GetPlaceDetails.class);
	
	private PlaceProvider placeProvider;
	
	
	public PlaceProvider getPlaceProvider()
	{
		return placeProvider;
	}


	public void setPlaceProvider(PlaceProvider placeProvider)
	{
		this.placeProvider = placeProvider;
	}


	@Override
	public Response invoke(LocationContext locationContext, Request request)
	{
		try
		{
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
			return null;
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new RuntimeException(e);
		}
	}
	
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
}
