/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.frameworks.mobileObject;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import java.util.StringTokenizer;


/**
 * @author openmobster@gmail.com
 */
public final class MobileObject 
{	
	private long id = 0;
	
	//MobileObject Meta Data
	private String storageId;
	private boolean isCreatedOnDevice = false;
	private String recordId;
	private String serverRecordId;		
	private boolean isProxy = false;
	
	//MobileObject actual Data
	private List<Field> fields;
	private List<ArrayMetaData> arrayMetaData;
	
	/**
	 * Hibernate optimistic locking
	 */
	private int version;
	
	
	public MobileObject()
	{
		this.fields = new ArrayList<Field>();
		this.arrayMetaData = new ArrayList<ArrayMetaData>();
	}
	
	
	MobileObject(List<Field> fields)
	{
		this.fields = fields;		
	}
	
	
	public List<Field> getFields() 
	{
		return fields;
	}


	public void setFields(List<Field> fields) 
	{
		this.fields = fields;
	}


	public long getId() 
	{
		return id;
	}


	public void setId(long id) 
	{
		this.id = id;
	}
	
	
	public String getRecordId() 
	{
		return recordId;
	}

	public void setRecordId(String recordId) 
	{
		this.recordId = recordId;
	}
		
	public String getStorageId() 
	{
		return storageId;
	}

	public void setStorageId(String storageId) 
	{
		this.storageId = storageId;
	}
		
	public boolean isCreatedOnDevice() 
	{
		return isCreatedOnDevice;
	}

	public void setCreatedOnDevice(boolean isCreatedOnDevice) 
	{
		this.isCreatedOnDevice = isCreatedOnDevice;
	}
	
	public int getVersion()
	{
		return this.version;
	}
	
	public void setVersion(int version)
	{
		this.version = version;
	}
		
	public String getServerRecordId() 
	{
		return serverRecordId;
	}

	public void setServerRecordId(String serverRecordId) 
	{
		this.serverRecordId = serverRecordId;
	}
		
	public boolean isProxy() 
	{
		return isProxy;
	}

	public void setProxy(boolean isProxy) 
	{
		this.isProxy = isProxy;
	}
		
	public List<ArrayMetaData> getArrayMetaData() 
	{
		return arrayMetaData;
	}

	public void setArrayMetaData(List<ArrayMetaData> arrayMetaData) 
	{
		this.arrayMetaData = arrayMetaData;
	}
	//---------------------------------------------------------------------------------------------------	
	public String getValue(String fieldUri)
	{
		String value = null;
		
		if(!fieldUri.startsWith("/"))
		{
			fieldUri = "/"+fieldUri;
		}
		
		Field field = this.findField(fieldUri.replace(".", "/"));
		if(field != null)
		{
			value = field.getValue();
		}
		
		return value;
	}
		
	public void setValue(String fieldUri, String value)
	{	
		if(this.fields.isEmpty() || this.isCreatedOnDevice)
		{
			this.isCreatedOnDevice = true;
			
			String uri = "/"+fieldUri;
			uri = uri.replace(".", "/");			
			
			boolean createField = true;
			for(int i=0,size=this.fields.size(); i<size; i++)
			{
				Field courField = (Field)this.fields.get(i);
				if(courField.getUri().equals(uri))
				{
					courField.setValue(value);
					createField = false;
				}
			}
			if(createField)
			{
				Field field = new Field(uri, fieldUri, value);
				this.fields.add(field);
			}			
			return;
		}
		
		if(!fieldUri.startsWith("/"))
		{
			fieldUri = "/"+fieldUri;
		}
		
		Field field = this.findField(fieldUri.replace(".", "/"));
		if(field != null)
		{
			field.setValue(value);
		}			
	}
	//----Indexed Properties API----------------------------------------------------------------------------------------------
	public int getArrayLength(String arrayUri)
	{
		int length = 0;
		arrayUri = "/"+arrayUri.replace(".", "/");		
		
		ArrayMetaData local = this.findArrayMetaData(arrayUri);		
		if(local != null)
		{			
			length = Integer.parseInt(local.getArrayLength());			
		}
				
		return length;
	}
	
	public Hashtable getArrayElement(String arrayUri, int elementIndex)
	{
		Hashtable arrayElement = new Hashtable();
		arrayUri = "/"+arrayUri.replace(".", "/");
		
		Vector arrayElementFields = this.findArrayElementFields(arrayUri, elementIndex);
		if(arrayElementFields != null && arrayElementFields.size() > 0)
		{
			for(int i=0; i<arrayElementFields.size(); i++)
			{
				Field local = (Field)arrayElementFields.elementAt(i);
				String localUri = local.getUri();
				String localName = local.getName();
				
				//Strip out the arrayUri and just use the property value
				String localArrayUri = this.calculateArrayUri(localUri);
				String propertyUri = localUri.substring(localArrayUri.length());
				int index = propertyUri.indexOf('/');
				if(index != -1)
				{
					propertyUri = propertyUri.substring(index);
				}
				else
				{
					index = localName.indexOf('[');
					propertyUri = "/"+localName.substring(0, index); 
				}
				
				arrayElement.put(propertyUri, local.getValue());
			}
		}
		
		return arrayElement;
	}
	
	//TODO: please clean this logic up.........goshhhhhhhhh this is ugly dude!!!!!!!!!!!!
	public void addToArray(String indexedPropertyName, Hashtable properties)
	{
		if(!indexedPropertyName.startsWith("/"))
		{
			indexedPropertyName = "/"+indexedPropertyName;
		}
		indexedPropertyName = indexedPropertyName.replace(".", "/");
		
		if(properties != null)
		{	
			//Calculate the index of the array
			int indexOfLastElement = this.findIndexValueInsertionPoint(indexedPropertyName);			
			int index = 0;
			if(indexOfLastElement != -1)
			{
				Field local = (Field)this.fields.get(indexOfLastElement);
				String localUri = local.getUri();
				index = this.calculateArrayIndex(localUri);
				index++;
			}
			
			//Insert this field at the bottom of the existing array
			Enumeration names = properties.keys();
			while(names.hasMoreElements())
			{
				String property = (String)names.nextElement();
				String value = (String)properties.get(property);
				if(property == null)
				{
					property = "";
				}
				if(property.startsWith("/"))
				{
					property = property.substring(1);
				}
																																													
				if(index != 0)
				{
					indexOfLastElement = this.findIndexValueInsertionPoint(indexedPropertyName);
					String localIndexedPropertyName = null;
					Field local = (Field)this.fields.get(indexOfLastElement);
					String localUri = local.getUri();
					localIndexedPropertyName = this.calculateArrayUri(localUri);
					int insertionIndex = indexOfLastElement+1;
					
					Field newField = new Field();
					newField.setName(property);
					newField.setValue(value);
								
					String uri = null;
					if(property != null && property.trim().length()>0)
					{						
						uri = localIndexedPropertyName + "[" + (index) +"]/" + property;
					}
					else
					{
						uri = localIndexedPropertyName + "[" + (index) +"]";
						String name = uri.substring(uri.lastIndexOf('/')+1);
						newField.setName(name);
					}
					newField.setUri(uri);
					
					if(this.fields.size() > insertionIndex)
					{
						((List)this.fields).set(insertionIndex, newField);
					}
					else
					{
						((List)this.fields).add(newField);
					}										
				}
				else
				{
					//A brand new array is being created within the Mobile Object
					Field field = new Field();
					
					String uri = null;
					if(property != null && property.trim().length()>0)
					{
						uri = indexedPropertyName+"[0]/"+ property;
					}
					else
					{
						uri = indexedPropertyName + "[0]";
					}
					
					String name = uri.substring(uri.lastIndexOf('/')+1);
					field.setUri(uri);
					field.setName(name);
					field.setValue(value);
					
					this.fields.add(field);
					
					ArrayMetaData metaData = new ArrayMetaData();
					metaData.setArrayUri(indexedPropertyName);
					metaData.setArrayLength(String.valueOf(0));
					this.arrayMetaData.add(metaData);
				}
			}
			//Increment the array meta data length
			ArrayMetaData arrayMetaData = this.findArrayMetaData(indexedPropertyName);
			int arrayLength = Integer.parseInt(arrayMetaData.getArrayLength());
			arrayMetaData.setArrayLength(String.valueOf(arrayLength+1));
		}			
	}
			
	public void removeArrayElement(String arrayUri, int elementAt)
	{
		String indexedFieldUri = arrayUri + "[" + elementAt + "]";
		
		//Validate the input
		if(!indexedFieldUri.endsWith("]"))
		{
			throw new IllegalArgumentException("Input: "+indexedFieldUri+" is invalid!!");
		}
		
		if(!indexedFieldUri.startsWith("/"))
		{
			indexedFieldUri = "/"+indexedFieldUri;
		}
		indexedFieldUri = indexedFieldUri.replace(".", "/");
		
		Vector fieldsToDelete = new Vector();
				
		//Get a list of indexed properties whose uri must now be modified
		int openIndex = indexedFieldUri.lastIndexOf('[');
		String indexedPropertyName = indexedFieldUri.substring(0, openIndex);
		for(int i=0; i<this.fields.size(); i++)
		{
			Field local = (Field)this.fields.get(i);
			
			String localUri = local.getUri();
			
			if(localUri.startsWith(indexedPropertyName))
			{
				int localOpenIndex = localUri.lastIndexOf('[');				
				int elementIndex = this.calculateArrayIndex(localUri);			
				String localIndexedPropertyName = localUri.substring(0, localOpenIndex);				
				if(elementIndex > elementAt)
				{
					//Rename the uri
					String diff = localUri.substring(indexedPropertyName.length()+3);
					String uri = null;
					if(diff != null && diff.trim().length()>0)
					{
						uri = localIndexedPropertyName+"["+(elementIndex-1)+"]/"+diff;
					}
					else
					{
						uri = localIndexedPropertyName+"["+(elementIndex-1)+"]";
					}
					local.setUri(uri);
					local.setName(uri.substring(uri.lastIndexOf('/')+1));
				}
				else if(elementIndex == elementAt)
				{
					fieldsToDelete.addElement(local);
				}				
			}
		}
		
		if(fieldsToDelete != null)
		{
			for(int i=0; i<fieldsToDelete.size(); i++)
			{
				Field fieldToDelete = (Field)fieldsToDelete.elementAt(i);
				this.fields.remove(fieldToDelete);								
			}
			
			//Decrement the Array MetaData
			ArrayMetaData arrayMetaData = this.findArrayMetaData(indexedPropertyName);
			int arrayLength = Integer.parseInt(arrayMetaData.getArrayLength());
			arrayMetaData.setArrayLength(String.valueOf(arrayLength-1));
		}
	}
	
	public void clearArray(String indexedPropertyName)
	{
		if(!indexedPropertyName.startsWith("/"))
		{
			indexedPropertyName = "/"+indexedPropertyName;
		}
		indexedPropertyName = indexedPropertyName.replace(".", "/");
		
		Vector fieldsToDelete = this.findArrayFields(indexedPropertyName);
		if(fieldsToDelete != null)
		{
			for(int i=0; i<fieldsToDelete.size(); i++)
			{
				Field fieldToDelete = (Field)fieldsToDelete.elementAt(i);
				this.fields.remove(fieldToDelete);								
			}
			
			//Decrement the Array MetaData
			ArrayMetaData arrayMetaData = this.findArrayMetaData(indexedPropertyName);
			if(arrayMetaData != null)
			{
				this.arrayMetaData.remove(arrayMetaData);
			}
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------	
	private Field findField(String inputUri)
	{
		Field field = null;
				
		if(this.fields != null)
		{
			for(int i=0,size=this.fields.size(); i<size; i++)
			{				
				boolean doesUriMatch = this.doesUriMatch(inputUri, ((Field)this.fields.get(i)).getUri());
				if(doesUriMatch)
				{
					field = (Field)this.fields.get(i);
					break;
				}
			}
		}
		
		return field;
	}
	
	private int findIndexValueInsertionPoint(String indexedPropertyName)
	{		
		int matchedIndex = -1; //If matchedIndex is -1, the array does not exist
		
		if(this.fields != null)
		{
			
			for(int i=0; i<this.fields.size(); i++)
			{
				Field local = (Field)this.fields.get(i);
				String localUri = local.getUri();
				int openIndex = localUri.lastIndexOf('[');							
				if(openIndex != -1)
				{
					String localIndexedPropertyName = localUri.substring(0, openIndex);
					if(doesUriMatch(indexedPropertyName, localIndexedPropertyName))
					{
						matchedIndex = i;
					}
				}												
			}						
		}
		
		return matchedIndex;
	}
	
	private Vector findArrayElementFields(String arrayUri, int elementIndex)
	{
		Vector arrayElementFields = new Vector();
		
		if(this.fields != null)
		{
			for(int i=0; i<this.fields.size(); i++)
			{
				Field local = (Field)this.fields.get(i);
				String localUri = local.getUri();
				String localIndexedPropertyName = this.calculateArrayUri(localUri);
				if(localIndexedPropertyName != null)
				{
					int localElementIndex = this.calculateArrayIndex(localUri);
					if(localElementIndex == elementIndex && doesUriMatch(arrayUri, localIndexedPropertyName))
					{						
						arrayElementFields.addElement(local);
					}
				}
			}
		}
		
		return arrayElementFields;
	}
	
	private Vector findArrayFields(String arrayUri)
	{
		Vector arrayFields = new Vector();
		
		if(this.fields != null)
		{
			for(int i=0; i<this.fields.size(); i++)
			{
				Field local = (Field)this.fields.get(i);
				String localUri = local.getUri();
				String localArrayUri = this.calculateArrayUri(localUri);
				if(localArrayUri != null)
				{					
					if(doesUriMatch(arrayUri, localArrayUri))
					{						
						arrayFields.addElement(local);
					}
				}
			}
		}
		
		return arrayFields;
	}
	
	private ArrayMetaData findArrayMetaData(String arrayUri)
	{
		if(this.arrayMetaData != null)
		{	
			for(int i=0; i<this.arrayMetaData.size(); i++)
			{				
				ArrayMetaData metaData = (ArrayMetaData)this.arrayMetaData.get(i);				
				if(metaData.getArrayUri().equals(arrayUri))
				{					
					return metaData;
				}
			}
		}		
		return null;
	}
	
	private String calculateArrayUri(String fieldUri)
	{
		String arrayUri = null;
		
		int lastIndex = -1;
		if((lastIndex=fieldUri.lastIndexOf('['))!=-1)
		{
			arrayUri = fieldUri.substring(0, lastIndex);
		}
		
		return arrayUri;
	}
	
	private int calculateArrayIndex(String fieldUri)
	{
		int arrayIndex = 0;
		
		int lastIndex = -1;
		if((lastIndex=fieldUri.lastIndexOf('['))!=-1)
		{
			int nextIndex = fieldUri.indexOf(']', lastIndex);
			arrayIndex = Integer.parseInt(fieldUri.substring(lastIndex+1, nextIndex).trim());
		}
		
		return arrayIndex;
	}
			
	private boolean doesUriMatch(String inputUri, String fieldUri)
	{
		boolean match = false;
		
		StringBuffer buffer = new StringBuffer("/");
		StringTokenizer tokenizer = new StringTokenizer(fieldUri, "/");
		while(tokenizer.hasMoreTokens())
		{
			String token = tokenizer.nextToken();			
			if(token.indexOf(".") == -1)
			{
				buffer.append(token+"/");
			}
		}
		
	    fieldUri = buffer.toString();
	    if(fieldUri.endsWith("/"))
	    {
	    	fieldUri = fieldUri.substring(0, fieldUri.length()-1);
	    }
	    
	    if(fieldUri.equals(inputUri))
	    {
	    	match = true;
	    }
	    
	    if(!match)
	    {
	    	if(inputUri.indexOf("[0]") != -1)
	    	{
	    		inputUri = inputUri.replace("[0]", "");
		    	fieldUri = fieldUri.replace("[0]", "");		    			    		    	
		    	
		    	if(fieldUri.equals(inputUri))
		    	{
		    		match = true;
		    	}
	    	}
	    }
			    
		return match;
	}						
}
