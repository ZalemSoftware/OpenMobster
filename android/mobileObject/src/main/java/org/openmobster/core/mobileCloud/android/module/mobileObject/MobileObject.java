/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.mobileObject;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.openmobster.core.mobileCloud.android.storage.Record;
import org.openmobster.core.mobileCloud.android.util.GeneralTools;
import org.openmobster.core.mobileCloud.android.util.StringUtil;

/**
 * @author openmobster@gmail.com
 */
public final class MobileObject 
{	
	//MobileObject Meta Data
	private String storageId;
	private String recordId;
	private String serverRecordId;
	private boolean isProxy;
	private boolean isCreatedOnDevice;
	private boolean isLocked;		
	private String dirtyStatus;
	
	//MobileObject actual Data
	private List<Field> fields;
	private List<ArrayMetaData> arrayMetaData;
	
	
	public MobileObject()
	{
		this.fields = new ArrayList<Field>();
		this.arrayMetaData = new ArrayList<ArrayMetaData>();
	}
		
	public MobileObject(List<Field> fields)
	{
		this.fields = fields;		
	}
	
	public MobileObject(Record record)
	{		
		this.storageId = record.getValue("storageId");
		this.recordId = record.getRecordId();
		this.dirtyStatus = record.getDirtyStatus();
		if(record.getValue("serverRecordId") != null)
		{
			this.serverRecordId = record.getValue("serverRecordId");
		}
		
		this.isCreatedOnDevice = false;
		if(record.getValue("isCreatedOnDevice").equals(Boolean.TRUE.toString()))
		{
			this.isCreatedOnDevice = true;
		}
		
		this.isLocked = false;
		if(record.getValue("isLocked").equals(Boolean.TRUE.toString()))
		{
			this.isLocked = true;
		}
		
		this.isProxy = false;
		if(record.getValue("isProxy").equals(Boolean.TRUE.toString()))
		{
			this.isProxy = true;
		}
		
		this.fields = new ArrayList<Field>();
		if(record.getValue("count") != null)
		{			
			int count = Integer.parseInt(record.getValue("count"));
			for(int index=0; index < count; index++)
			{
				Field field = new Field();
				
				field.setUri(record.getValue("field["+index+"].uri"));
				field.setName(record.getValue("field["+index+"].name"));
				field.setValue(record.getValue("field["+index+"].value"));
				
				this.fields.add(field);
			}
		}
		
		this.arrayMetaData = new ArrayList<ArrayMetaData>();
		if(record.getValue("arrayMetaDataCount") != null)
		{
			int count = Integer.parseInt(record.getValue("arrayMetaDataCount"));
			for(int index=0; index < count; index++)
			{
				ArrayMetaData local = new ArrayMetaData();
				
				local.setArrayUri(record.getValue("arrayMetaData["+index+"].arrayUri"));
				local.setArrayLength(record.getValue("arrayMetaData["+index+"].arrayLength"));
				local.setArrayClass(record.getValue("arrayMetaData["+index+"].arrayClass"));
				
				this.arrayMetaData.add(local);
			}
		}
	}
	
	Record getRecord()
	{
		Record record = new Record();
		
		if(this.recordId != null && this.recordId.trim().length() > 0)
		{
			record.setRecordId(this.recordId);
		}
		
		if(this.dirtyStatus != null && this.dirtyStatus.trim().length() > 0)
		{
			record.setDirtyStatus(this.dirtyStatus);
		}
		
		if(this.serverRecordId != null && this.serverRecordId.trim().length() > 0)
		{
			record.setValue("serverRecordId", this.serverRecordId);
		}
		
		record.setValue("storageId", storageId);
		
		
		if(this.isCreatedOnDevice)
		{
			record.setValue("isCreatedOnDevice", Boolean.TRUE.toString());
		}
		else
		{
			record.setValue("isCreatedOnDevice", Boolean.FALSE.toString());
		}
		if(this.isLocked)
		{
			record.setValue("isLocked", Boolean.TRUE.toString());
		}
		else
		{
			record.setValue("isLocked", Boolean.FALSE.toString());
		}
		
		if(this.isProxy)
		{
			record.setValue("isProxy", Boolean.TRUE.toString());
		}
		else
		{
			record.setValue("isProxy", Boolean.FALSE.toString());
		}
		
		if(this.fields != null && this.fields.size() > 0)
		{
			record.setValue("count", String.valueOf(this.fields.size()));
			
			int index = 0;
			for(Field field: this.fields)
			{	
				
				record.setValue("field["+index+"].uri", field.getUri());
				
				record.setValue("field["+index+"].name", field.getName());
				
				record.setValue("field["+index+"].value", field.getValue());							
				
				index++;
			}
		}
		
		if(this.arrayMetaData != null && this.arrayMetaData.size() > 0)
		{
			record.setValue("arrayMetaDataCount", String.valueOf(this.arrayMetaData.size()));
			
			int index = 0;
			for(ArrayMetaData local: this.arrayMetaData)
			{		
				record.setValue("arrayMetaData["+index+"].arrayUri", local.getArrayUri());
				
				record.setValue("arrayMetaData["+index+"].arrayLength", local.getArrayLength());
				
				
				if(local.getArrayClass() != null)
				{
					record.setValue("arrayMetaData["+index+"].arrayClass", local.getArrayClass());
				}
				else
				{
					record.setValue("arrayMetaData["+index+"].arrayClass", "");
				}									
				
				index++;
			}
		}
		
		return record;
	}
	
	public List<Field> getFields() 
	{
		return fields;
	}


	public void setFields(List<Field> fields) 
	{
		this.fields = fields;
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
		
	public boolean isLocked() 
	{
		return isLocked;
	}

	public void setLocked(boolean isLocked) 
	{
		this.isLocked = isLocked;
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
		
		Field field = this.findField(StringUtil.replaceAll(fieldUri,".", "/"));
		if(field != null)
		{
			value = field.getValue();
		}
		
		return value;
	}
		
	public void setValue(String fieldUri, String value)
	{
		String fieldName = fieldUri;
		if(fieldUri.indexOf('.') != -1)
		{
			int lastIndex = fieldUri.lastIndexOf('.');
			fieldName = fieldUri.substring(lastIndex+1);
		}
		
		
		if(this.fields.isEmpty() || this.isCreatedOnDevice)
		{
			this.isCreatedOnDevice = true;
			
			/*
			 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
			 * Feito com que a "/" só seja colocada se necessário (conforme outras rotinas similares do OpenMobster).
			 */
			String uri = fieldUri;
			if(!uri.startsWith("/")) {
				uri = "/" + uri;
			}
			uri = StringUtil.replaceAll(uri,".", "/");			
			
			boolean createField = true;
			Field nullField = null;
			for(Field courField: this.fields)
			{
				if(courField.getUri().equals(uri))
				{
					if(value != null)
					{
						courField.setValue(value);
					}
					else
					{
						nullField = courField;
						break;
					}
					createField = false;
				}
			}
			if(nullField != null)
			{
				this.fields.remove(nullField);
			}
			
			if(createField)
			{
				if(value != null)
				{
					Field field = new Field(uri, fieldName, value);
					this.fields.add(field);
				}
			}			
			return;
		}
		
		
		String uri = fieldUri;
		if(!uri.startsWith("/"))
		{
			uri = "/"+fieldUri;
		}
		uri = StringUtil.replaceAll(uri,".", "/");
		
		Field field = this.findField(uri);
		if(field != null)
		{
			if(value != null)
			{
				field.setValue(value);
			}
			else
			{
				this.fields.remove(field);
			}
		}	
		else
		{	
			//create this field
			if(value != null)
			{
				Field newField = new Field(uri, fieldName, value);
				this.fields.add(newField);
			}
		}
	}
	
	public int getArrayLength(String arrayUri)
	{
		int length = 0;
		
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Feito com que a "/" só seja colocada se necessário (conforme outras rotinas similares do OpenMobster).
		 */
		if(!arrayUri.startsWith("/")) {
			arrayUri = "/" + arrayUri;
		}
		arrayUri = StringUtil.replaceAll(arrayUri,".", "/");		
		
		ArrayMetaData local = this.findArrayMetaData(arrayUri);		
		if(local != null)
		{			
			length = Integer.parseInt(local.getArrayLength());			
		}
				
		return length;
	}
	
	public Map<String, String> getArrayElement(String arrayUri, int elementIndex)
	{
		Map<String, String> arrayElement = new HashMap<String, String>();
		
		/*
		 * Alteração feita na versão 2.4-M3.1 do OpenMobster.
		 * Feito com que a "/" só seja colocada se necessário (conforme outras rotinas similares do OpenMobster).
		 */
		if(!arrayUri.startsWith("/")) {
			arrayUri = "/" + arrayUri;
		}
		arrayUri = StringUtil.replaceAll(arrayUri,".", "/");
		
		List<Field> arrayElementFields = this.findArrayElementFields(arrayUri, elementIndex);
		if(arrayElementFields != null && arrayElementFields.size() > 0)
		{
			for(Field local: arrayElementFields)
			{
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
	public void addToArray(String indexedPropertyName, Map<String, String> properties)
	{
		if(!indexedPropertyName.startsWith("/"))
		{
			indexedPropertyName = "/"+indexedPropertyName;
		}
		indexedPropertyName = StringUtil.replaceAll(indexedPropertyName,".", "/");
		
		if(properties != null)
		{	
			//Calculate the index of the array
			int indexOfLastElement = this.findIndexValueInsertionPoint(indexedPropertyName);			
			int index = 0;
			if(indexOfLastElement != -1)
			{
				Field local = this.fields.get(indexOfLastElement);
				String localUri = local.getUri();
				index = this.calculateArrayIndex(localUri);
				index++;
			}
			
			//Insert this field at the bottom of the existing array
			Set<String> names = properties.keySet();
			boolean arrayCreated = false;
			for(String property: names)
			{
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
						//FIXME:make this more efficient
						//((List<Field>)this.fields).set(insertionIndex, newField);
						int length = this.fields.size();
						List<Field> temp = new ArrayList<Field>();
						for(int i=0; i<length; i++)
						{
							Field tempField = this.fields.get(i);
							if(i == insertionIndex)
							{
								temp.add(newField);
							}
							temp.add(tempField);
						}
						this.fields = temp;
					}
					else
					{
						((List<Field>)this.fields).add(newField);
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
					
					if(!arrayCreated)
					{
						ArrayMetaData metaData = new ArrayMetaData();
						metaData.setArrayUri(indexedPropertyName);
						metaData.setArrayLength(String.valueOf(0));
						this.arrayMetaData.add(metaData);
						arrayCreated = true;
					}
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
		indexedFieldUri = StringUtil.replaceAll(indexedFieldUri,".", "/");
		
		List<Field> fieldsToDelete = new ArrayList<Field>();
				
		//Get a list of indexed properties whose uri must now be modified
		int openIndex = indexedFieldUri.lastIndexOf('[');
		String indexedPropertyName = indexedFieldUri.substring(0, openIndex);
		for(Field local: this.fields)
		{	
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
						uri = localIndexedPropertyName+"["+(elementIndex-1)+"]"+diff;
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
					fieldsToDelete.add(local);
				}				
			}
		}
		
		if(fieldsToDelete != null)
		{
			for(Field fieldToDelete: fieldsToDelete)
			{
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
		indexedPropertyName = StringUtil.replaceAll(indexedPropertyName,".", "/");
		
		List<Field> fieldsToDelete = this.findArrayFields(indexedPropertyName);
		if(fieldsToDelete != null)
		{
			for(Field fieldToDelete: fieldsToDelete)
			{
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
	//-----------------------------------------------------------------------------------------------	
	private Field findField(String inputUri)
	{
		Field field = null;
				
		if(this.fields != null)
		{
			for(Field local: this.fields)
			{				
				boolean doesUriMatch = this.doesUriMatch(inputUri, local.getUri());
				if(doesUriMatch)
				{
					field = local;
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
			int i = 0;
			for(Field local: this.fields)
			{
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
				i++;
			}						
		}
		
		return matchedIndex;
	}
	
	private List<Field> findArrayElementFields(String arrayUri, int elementIndex)
	{
		List<Field> arrayElementFields = new ArrayList<Field>();
		
		if(this.fields != null)
		{
			for(Field local: this.fields)
			{
				String localUri = local.getUri();
				String localIndexedPropertyName = this.calculateArrayUri(localUri);
				if(localIndexedPropertyName != null)
				{
					int localElementIndex = this.calculateArrayIndex(localUri);
					if(localElementIndex == elementIndex && doesUriMatch(arrayUri, localIndexedPropertyName))
					{						
						arrayElementFields.add(local);
					}
				}
			}
		}
		
		return arrayElementFields;
	}
	
	private List<Field> findArrayFields(String arrayUri)
	{
		List<Field> arrayFields = new ArrayList<Field>();
		
		if(this.fields != null)
		{
			for(Field local: this.fields)
			{
				String localUri = local.getUri();
				String localArrayUri = this.calculateArrayUri(localUri);
				if(localArrayUri != null)
				{					
					if(doesUriMatch(arrayUri, localArrayUri))
					{						
						arrayFields.add(local);
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
			for(ArrayMetaData metaData: this.arrayMetaData)
			{								
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
		String[] st = StringUtil.tokenize(fieldUri,"/");
		if(st != null)
		{
			for(int i=0; i<st.length; i++)
			{
				String token = st[i];			
				if(token.indexOf(".") == -1)
				{
					buffer.append(token+"/");
				}
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
	    		inputUri = StringUtil.replaceAll(inputUri,"[0]", "");
		    	fieldUri = StringUtil.replaceAll(fieldUri,"[0]", "");		    			    		    	
		    	
		    	if(fieldUri.equals(inputUri))
		    	{
		    		match = true;
		    	}
	    	}
	    }
			    
		return match;
	}

	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof MobileObject))
		{
			return false;
		}
		
		MobileObject incoming = (MobileObject)o;
		
		String myRecordId = this.recordId;
		String incomingRecordId = incoming.recordId;
		
		if(myRecordId == null || incomingRecordId == null)
		{
			return false;
		}
		
		if(myRecordId.equals(incomingRecordId))
		{
			return true;
		}
		
		return false;
	}

	@Override
	public int hashCode()
	{
		if(this.recordId == null)
		{
			return GeneralTools.generateUniqueId().hashCode();
		}
		return this.recordId.hashCode();
	}	
	
	
	/*
	 * Métodos adicionados na versão 2.4-M3.1
	 */
	
	/**
	 * Verifica se o campo existe, ignorando campos array.
	 * 
	 * @param name o nome do campo.
	 * @return <code>true</code> se o campo existe e <code>false</code> caso contrário.
	 */
	public boolean hasField(String name) {
		if (fields != null) {
			for (Field field : fields) {
				if (field.getName().equals(name)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Verifica se o campo existe, considerando campos array.
	 * 
	 * @param uri uri do campo.
	 * @return <code>true</code> se o campo existe e <code>false</code> caso contrário.
	 */
	public boolean hasFieldOrArray(String uri) {
		if (fields != null) {
			//Como vai verificar a uri, tem que começar com "/".
			if(!uri.startsWith("/")) {
				uri = "/" + uri;
			}
			
			for (Field field : fields) {
				String fieldUri = field.getUri();
				//Faz a verificação desta forma ao invés de usar uma expressão regular por questões de performance, já que esta rotina é chamada constantemente.
				if (fieldUri.startsWith(uri)) {
					//Se for do mesmo tamanho, significa que é igual (n há necessidade do equals()).
					if (fieldUri.length() == uri.length()) {
						return true;
					}
					
					//Se a uri do campo for maior, tem que verificar se trata-se de um array ou um objeto interno ao invés de um campo similar.
					//Por exemplo, o campo "/nome" não pode ser confundido com "/nome2". Já "/obj" é válido para "/obj/nome" e "/objs" é válido para "/objs[0]/nome".
					char c = fieldUri.charAt(uri.length());
					if (c == '/' || c == '[') {
						return true;
					}
				}
			}
		}
		
		return false;
	}
}
