/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileObject.xml;

import java.io.Writer;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.io.xml.AbstractXmlWriter;

import org.openmobster.core.common.XMLUtilities;

/**
 * @author openmobster@gmail.com
 */
class MobileObjectWriter extends AbstractXmlWriter
{
	private static Logger log = Logger.getLogger(MobileObjectWriter.class);
	
	private static final String OBJECT = "object";
	private static final String FIELDS = "fields";
	private static final String FIELD = "field";
	private static final String URI = "uri";
	private static final String NAME = "name";
	private static final String VALUE = "value";
	private static final String META_DATA = "metadata";
	private static final String ARRAY_META_DATA = "array-metadata";
	private static final String ARRAY_LENGTH = "array-length";
	private static final String ARRAY_CLASS = "array-class";
	
	private Writer writer;
	private Field currentField;
	private Vector<Field> fields;
	private Stack<String> uriStack;
	private boolean activatePopping;
	private boolean isTagEmpty;
	private Map<String, ArrayMetaData> metaData;
	
	
	//Array related state
	private Stack<ArrayTracker> arrayTracker;
	private boolean isArrayActive;
	
	public MobileObjectWriter(Writer writer) 
	{
		this.writer = writer;
        this.fields = new Vector<Field>();
        this.uriStack= new Stack<String>();
        this.arrayTracker = new Stack<ArrayTracker>();
        this.metaData = new HashMap<String, ArrayMetaData>();
    }
		
	
	public void startNode(String name, Class clazz) 
	{				
		this.uriStack.push(name);
		
		//Handle Array/Lists.....Activate Tracking an Array
		if(clazz != null &&
		  (Collection.class.isAssignableFrom(clazz) ||
		   (clazz.isArray() && !clazz.getComponentType().isAssignableFrom(byte.class)))
		)
		{			
			this.activateArray();	
			
			//Try to figure out the array class
			
		}
		else
		{
			if(this.isArrayActive)
			{
				ArrayTracker activeArray = this.arrayTracker.peek();
				if(activeArray.arrayMetaData.arrayClass == null)
				{
					activeArray.arrayMetaData.arrayClass = clazz.getName();
				}
			}
		}
								
		this.startNode(name);
	}
	
	public void startNode(String name) 
	{		
		this.currentField = new Field();
		this.activatePopping = false;
		this.isTagEmpty = true;
		
		//Perform Active Array Deactivation if necessary
		//Check to see if array index needs incrementing
		if(this.isArrayActive && !name.startsWith("field:"))
		{
			this.incrementArrayIndex();
		}
	}
	
	public void setValue(String value) 
	{						
		this.isTagEmpty = false;
		this.activatePopping = true;
		
		String uri = this.getUri();
		this.currentField.value = value;
		if(!isArrayActive)
		{				
			this.currentField.setUri(this.calculateUri(uri));						
			this.fields.add(this.currentField);				
		}
		else
		{
			ArrayTracker activeArray = this.arrayTracker.peek();			
			
			if(uri.contains(activeArray.arrayMetaData.arrayUri))
			{
				//Set the Array Element				
				String diff = uri.substring(activeArray.arrayMetaData.arrayUri.length());
				diff = this.calculateUri(diff);
				
				//TODO: Cleanup this logic.....Calculate the Element URI with proper indexes
				StringBuilder elementUri = new StringBuilder();
				StringBuilder buffer = new StringBuilder();				
				ArrayTracker[] activeArrays = this.arrayTracker.toArray(new ArrayTracker[0]);
				for(ArrayTracker localActive: activeArrays)
				{
					String arrayUri = this.calculateUri(localActive.arrayMetaData.arrayUri);
					int arrayIndex = localActive.currentIndex;
					
					if(activeArray.arrayMetaData.arrayUri.contains(localActive.arrayMetaData.arrayUri))
					{
						String prev = buffer.toString();
						String currentToken = arrayUri.substring(prev.length());
						
						elementUri.append(currentToken+"["+arrayIndex+"]");						
						buffer.append(currentToken);
					}					
				}
				
				log.debug("---------------------------------------");
				log.debug("Raw Uri="+uri);
				log.debug("ActiveArrayUri="+activeArray.arrayMetaData.arrayUri);
				log.debug("Diff="+diff);				
				log.debug("ElementUri="+elementUri.toString());
				log.debug("Value="+value);
				log.debug("---------------------------------------");
																
				this.currentField.setUri(elementUri.toString() + diff);				
				this.fields.add(this.currentField);	
				
				//Add Array Meta Data 
				String arrayMetaDataUri = elementUri.toString();
				int lastIndex = arrayMetaDataUri.lastIndexOf('[');
				
				ArrayMetaData arrayMetaData = new ArrayMetaData();
				arrayMetaData.arrayUri = arrayMetaDataUri.substring(0, lastIndex);
				arrayMetaData.arrayLength = (activeArray.currentIndex)+1;
				arrayMetaData.arrayClass = activeArray.arrayMetaData.arrayClass;
				this.metaData.put(arrayMetaDataUri.substring(0, lastIndex), 
				arrayMetaData);
			}
			else
			{
				this.currentField.setUri(this.calculateUri(uri));				
				this.fields.add(this.currentField);
			}
		}
		
		//Cleanup
		this.currentField = null;		
	}
	
	public void endNode() 
	{
		try
		{
			//Look for null fields, especially inside arrays
			if(this.isTagEmpty)
			{
				//pop the entire field out
				do
				{
					this.uriStack.pop();
				}while(this.uriStack.peek().startsWith("field:"));
								
				return;
			}
			
			if(this.activatePopping)
			{
				this.uriStack.pop();
			}
		}
		finally
		{
			//reset the value
			this.isTagEmpty = false;
			
			//deactivate the array if one is active and needs to be reclaimed
			this.finalizeActiveArray();
		}
	}
	
	public void addAttribute(String name, String value) 
	{		
	}
	
	public void flush() 
	{	
		try
		{
			StringBuilder buffer = new StringBuilder();
			
			buffer.append("<"+OBJECT+">\n");
			
			buffer.append("<"+FIELDS+">\n");
			for(Field field: this.fields)
			{
				log.debug("--------------------------------------------------");
				log.debug("URI="+field.uri);
				log.debug("Name="+field.name);
				log.debug("Value="+field.value);
				
				buffer.append("<"+FIELD+">\n");
				
				if(field.uri != null && field.uri.trim().length()>0)
				{										
					buffer.append("<"+URI+">"+field.uri+"</"+URI+">\n");
					buffer.append("<"+NAME+">"+field.name+"</"+NAME+">\n");
				}
				
				buffer.append("<"+VALUE+">"+XMLUtilities.cleanupXML(field.value)+"</"+VALUE+">\n");
				buffer.append("</"+FIELD+">\n");
			}
			buffer.append("</"+FIELDS+">\n");
			
			if(this.metaData != null && this.metaData.size()>0)
			{
				buffer.append("<"+META_DATA+">\n");
					
				Set<String> arrayUris = this.metaData.keySet();
				for(String uri: arrayUris)
				{
					ArrayMetaData arrayMetaData = this.metaData.get(uri);
					buffer.append("<"+ARRAY_META_DATA+">\n");
					buffer.append("<"+URI+">"+arrayMetaData.arrayUri+"</"+URI+">\n");
					buffer.append("<"+ARRAY_LENGTH+">"+arrayMetaData.arrayLength+"</"+ARRAY_LENGTH+">\n");
					buffer.append("<"+ARRAY_CLASS+">"+arrayMetaData.arrayClass+"</"+ARRAY_CLASS+">\n");
					buffer.append("</"+ARRAY_META_DATA+">\n");
				}
				buffer.append("</"+META_DATA+">\n");
			}
			
			buffer.append("</"+OBJECT+">\n");
			
			this.writer.write(buffer.toString());
			this.writer.flush();
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	public void close() 
	{
		try
		{
			this.writer.close();
		}
		catch(Throwable t)
		{
			throw new RuntimeException(t);
		}
	}
	//-----------------------------------------------------------------------------------------------------
	private String calculateUri(String uri)
	{		
		StringBuilder buffer = new StringBuilder();
		StringTokenizer tokenizer = new StringTokenizer(uri, "/");
		while(tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();
			
			if(token.startsWith("field:"))
			{
				String local = token.substring("field:".length());
				buffer.append("/"+local);
			}
		}
		return buffer.toString();
	}
	
	private String getUri()
	{
		StringBuilder buffer = new StringBuilder();
		
		String[] uriPieces = this.uriStack.toArray(new String[0]);
		for(String piece: uriPieces)
		{
			buffer.append("/"+piece);
		}
		
		return buffer.toString();
	}
	//-----------------------------------------------------------------------------------------------------
	private void deactivateCurrentArray()
	{
		//Active Array is Being Closed
		ArrayTracker closingArray = this.arrayTracker.pop();
		
		log.debug("Deactivating Array---------------------------------------");
		log.debug("ArrayUri="+closingArray.arrayMetaData.arrayUri);
		log.debug("---------------------------------------");
		
				
		if(this.arrayTracker.isEmpty())
		{
			this.isArrayActive = false;
		}
	}
	
	private void activateArray()
	{
		//Start tracking the array
		ArrayTracker tracker = new ArrayTracker();
		tracker.arrayMetaData = new ArrayMetaData();
		tracker.arrayMetaData.arrayUri = this.getUri();
		tracker.currentIndex = -1;			
		isArrayActive = true;
		
		log.debug("Pushing an Array-------------------------");
		log.debug("ArrayUri="+tracker.arrayMetaData.arrayUri);
		log.debug("-------------------------");
		
		this.arrayTracker.push(tracker);
	}
	
	private void incrementArrayIndex()
	{
		String uri = this.getUri();
		ArrayTracker[] activeArrays = this.arrayTracker.toArray(new ArrayTracker[0]);
		for(int i=activeArrays.length-1; i >=0; i--)
		{
			ArrayTracker activeArray = activeArrays[i];
			if(uri.contains(activeArray.arrayMetaData.arrayUri))
			{
				//the element belongs to this active array...increment its index
				activeArray.currentIndex++;				
				break;
			}
		}
	}		
	
	private void finalizeActiveArray()
	{
		if(this.isArrayActive)
		{
			String uri = this.getUri();
			
			ArrayTracker tracker = this.arrayTracker.peek();
											
			//Deactivate the currently active array
			if(!uri.contains(tracker.arrayMetaData.arrayUri))
			{
				log.debug("Before Deactivation----------------------------------");
				log.debug("URI="+uri);
				log.debug("ActiveArrayURI="+tracker.arrayMetaData.arrayUri);
				this.deactivateCurrentArray();
			}
		}
	}
	//---------------------------------------------------------------------------------------------------
	private static class Field
	{
		private String uri;
		private String value;
		private String name;		
		
		private void setUri(String uri)
		{
			this.uri = uri;
			this.name = "";
			if(this.uri != null && this.uri.trim().length()>0)
			{
				int lastIndex = this.uri.lastIndexOf('/');
				if(lastIndex != -1)
				{
					this.name = this.uri.substring(lastIndex+1);
				}
				else
				{
					this.name = this.uri;
				}
			}
		}
	}
	
	private static class ArrayTracker
	{
		private int currentIndex;
		private ArrayMetaData arrayMetaData;
	}	
}
