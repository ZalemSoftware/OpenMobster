/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.mobileObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * @author openmobster@gmail.com
 */
final class MobileObjectReader extends DefaultHandler
{
	private List<Field> fields;
	private List<ArrayMetaData> arrayMetaData;
	
	private StringBuffer fullPath;
	private StringBuffer dataBuffer;
	private Field currentField;
	private ArrayMetaData currentMetaData;
	private String recordId;
	private String serverRecordId;
	private boolean isProxy;
	private String currentElement;
	
	public MobileObjectReader()
	{
		
	}
	
	
	public MobileObject parse(String xml)
	{
		ByteArrayInputStream bis = null;
		try
		{			
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			bis = new ByteArrayInputStream(xml.getBytes());
			
			saxParser.parse(bis, this);	
			
			MobileObject mobileObject = new MobileObject();
			mobileObject.setRecordId(this.recordId);
			mobileObject.setServerRecordId(this.serverRecordId);
			mobileObject.setProxy(isProxy);
			mobileObject.setFields(this.fields);
			mobileObject.setArrayMetaData(this.arrayMetaData);
			
			return mobileObject;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}
		finally
		{
			if(bis != null)
			{
				try{bis.close();}catch(IOException ioe){}
			}
		}
	}
	//----SAX Parsing interface---------------------------------------------------------------------------------------------------
	public void startDocument() throws SAXException
	{
		this.fields = new ArrayList<Field>();
		this.arrayMetaData = new ArrayList<ArrayMetaData>();
		this.fullPath = new StringBuffer();
		this.dataBuffer = new StringBuffer();
		this.currentField = new Field();
		this.currentMetaData = new ArrayMetaData();
		this.currentElement = null;
		this.recordId = null;
		this.serverRecordId = null;
		this.isProxy = false;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) 
	throws SAXException
	{
		this.fullPath.append("/"+localName.trim());
		this.currentElement = localName.trim();
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		String data = new String(ch, start, length);
		
		if(data != null)
		{
			this.dataBuffer.append(data);				
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		this.closeDataBuffer();
		
		if(this.currentElement.equals("proxy"))
		{
			this.isProxy = true;
		}
		
		String path = this.fullPath.toString();			
		int lastIndex = path.lastIndexOf('/');
		this.fullPath = new StringBuffer(path.substring(0, lastIndex));
	}
	//-----------------------------------------------------------------------------------------------------
	private void closeDataBuffer()
	{
		try
		{
			String data = dataBuffer.toString();
			dataBuffer = new StringBuffer();
			if(data != null)
			{
				String currentUri = this.fullPath.toString();
								
				//Process a Field Object
				if(currentUri.endsWith("/recordId"))
				{
					this.recordId = data.trim();
				}
				else if(currentUri.endsWith("/serverRecordId"))
				{
					this.serverRecordId = data.trim();
				}				
				else if(currentUri.endsWith("/field/uri"))
				{
					this.currentField.setUri(data.trim());
				}
				else if(currentUri.endsWith("/field/name"))
				{
					this.currentField.setName(data.trim());
				}
				else if(currentUri.endsWith("/field/value"))
				{
					this.currentField.setValue(data.trim());
					this.fields.add(this.currentField);
					
					this.currentField = new Field();
				}
				else if(currentUri.endsWith("/array-metadata/uri"))
				{
					this.currentMetaData.setArrayUri(data.trim());					
				}				
				else if(currentUri.endsWith("/array-metadata/array-length"))
				{
					this.currentMetaData.setArrayLength(data.trim());										
				}
				else if(currentUri.endsWith("/array-metadata/array-class"))
				{
					this.currentMetaData.setArrayClass(data.trim());
					
					this.arrayMetaData.add(this.currentMetaData);
					this.currentMetaData = new ArrayMetaData();
				}
			}
		}
		finally
		{			
		}
	}
}
