/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.cloud.api.location;

import java.io.Serializable;
import java.util.List;

import org.openmobster.core.common.InVMAttributeManager;

/**
 * Represents the context and its data associated with an active Location Bean request
 * 
 * @author openmobster@gmail.com
 */
public final class LocationContext implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6163601371562344355L;
	private InVMAttributeManager attributeManager;
	
	/**
	 * 
	 */
	public LocationContext()
	{
		this.attributeManager = new InVMAttributeManager();
	}
	
	
	/**
	 * Sets arbitrary attributes representing the contextual data associated with this context
	 * 
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, Object value)
	{
		this.attributeManager.setAttribute(name, value);
	}
	
	/**
	 * Gets an arbitrary attribute value from the context
	 * 
	 * @param name
	 * @return
	 */
	public Object getAttribute(String name)
	{
		return this.attributeManager.getAttribute(name);
	}
	
	/**
	 * Gets all the names that identify values of attributes in the context
	 * 
	 * @return
	 */
	public String[] getNames()
	{
		return this.attributeManager.getNames();
	}
	
	/**
	 * Gets all the values of attributes in the context
	 * 
	 * @return
	 */
	public Object[] getValues()
	{
		return this.attributeManager.getValues();
	}
	
	/**
	 * Removes an attribute
	 * 
	 * @param name
	 */
	public void removeAttribute(String name)
	{
		this.attributeManager.removeAttribute(name);
	}
	
	/**
	 * Returns the Latitude of the address associated with this context
	 * 
	 * @return latitude
	 */
	public String getLatitude()
	{
		return (String)this.attributeManager.getAttribute("latitude");
	}
	
	/**
	 * Sets the Latitude of the address associated with this context
	 * 
	 * @param latitude
	 */
	public void setLatitude(String latitude)
	{
		this.attributeManager.setAttribute("latitude", latitude);
	}
	
	/**
	 * Gets the Longitude of the address associated with this context
	 * 
	 * @return longitude
	 */
	public String getLongitude()
	{
		return (String)this.attributeManager.getAttribute("longitude");
	}
	
	/**
	 * Sets the Longitude of the address associated with this context
	 * 
	 * @param longitude
	 */
	public void setLongitude(String longitude)
	{
		this.attributeManager.setAttribute("longitude", longitude);
	}
	
	/**
	 * Gets the Address associated with this context
	 * 
	 * @return address
	 */
	public Address getAddress()
	{
		return (Address)this.attributeManager.getAttribute("address");
	}
	
	/**
	 * Sets the Address associated with this context
	 * 
	 * @param address
	 */
	public void setAddress(Address address)
	{
		this.attributeManager.setAttribute("address", address);
	}
	
	/**
	 * Gets a list of places near the address associated with the context
	 * 
	 * @return places
	 */
	public List<Place> getNearbyPlaces()
	{
		return (List<Place>)this.attributeManager.getAttribute("places");
	}
	
	/**
	 * Sets the list of places near the address associated with the context
	 * 
	 * @param places
	 */
	public void setNearbyPlaces(List<Place> places)
	{
		this.attributeManager.setAttribute("places", places);
	}
	
	/**
	 * Get the details associated with a place
	 * 
	 * @return
	 */
	public Place getPlaceDetails()
	{
		return (Place)this.attributeManager.getAttribute("placeDetails");
	}
	
	/**
	 * Set the details associated with a place
	 * 
	 * @param placeDetails
	 */
	public void setPlaceDetails(Place placeDetails)
	{
		this.attributeManager.setAttribute("placeDetails", placeDetails);
	}
	
	/**
	 * Set the type of places to search for
	 * 
	 * @return
	 */
	public List<String> getPlaceTypes()
	{
		return (List<String>)this.attributeManager.getAttribute("placeTypes");
	}
	
	/**
	 * Get the type of places to be searched for
	 * 
	 * @param placeTypes
	 */
	public void setPlaceTypes(List<String> placeTypes)
	{
		this.attributeManager.setAttribute("placeTypes", placeTypes);
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPlaceReference()
	{
		return (String)this.attributeManager.getAttribute("placeReference");
	}
	
	/**
	 * 
	 * @param placeReference
	 */
	public void setPlaceReference(String placeReference)
	{
		this.attributeManager.setAttribute("placeReference", placeReference);
	}
	
	/**
	 * Set the radius of how far to look for nearby places
	 * 
	 * @param radius
	 */
	public void setRadius(int radius)
	{
		this.attributeManager.setAttribute("radius", ""+radius);
	}
	
	/**
	 * Get the radius on how far to look for nearby places
	 * 
	 * @return
	 */
	public int getRadius()
	{
		String radiusValue = (String)this.attributeManager.getAttribute("radius");
		if(radiusValue == null || radiusValue.trim().length()==0)
		{
			return 0;
		}
		return Integer.parseInt(radiusValue);
	}
	
	/**
	 * Set the name of the place to search for
	 * 
	 * @param searchName
	 */
	public void setSearchName(String searchName)
	{
		this.attributeManager.setAttribute("searchName", searchName);
	}
	
	/**
	 * Get the name of the place to search for
	 * 
	 * @return
	 */
	public String getSearchName()
	{
		return (String)this.attributeManager.getAttribute("searchName");
	}
}
