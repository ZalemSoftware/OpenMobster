/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.util;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author openmobster@gmail.com
 *
 */
public final class XMLUtil 
{	
	public static String cleanupXML(String xml)
	{
		String cleanedup = "";
		
		if(xml != null)
		{
			cleanedup = StringUtil.replaceAll(xml, "&", "&amp;");
			cleanedup = StringUtil.replaceAll(cleanedup,"<", "&lt;");
			cleanedup = StringUtil.replaceAll(cleanedup,">", "&gt;");			
			cleanedup = StringUtil.replaceAll(cleanedup,"\"", "&quot;");
			cleanedup = StringUtil.replaceAll(cleanedup,"'", "&apos;");
		}
		
		return cleanedup;
	}
		
	public static String restoreXML(String xml)
	{
		String restored = "";
		
		if(xml != null)
		{
			restored = StringUtil.replaceAll(xml,"&apos;", "'");
			restored = StringUtil.replaceAll(restored,"&quot;", "\"");
			restored = StringUtil.replaceAll(restored,"&gt;", ">");			
			restored = StringUtil.replaceAll(restored,"&lt;", "<");
			restored = StringUtil.replaceAll(restored,"&amp;", "&");
		}
		
		return restored;
	}
					
	public static String removeCData(String xml)
	{
		String cleanXML = StringUtil.replaceAll(xml,"<![CDATA[", "");
		cleanXML = StringUtil.replaceAll(cleanXML,"]]>", "");
		
		return cleanXML;
	}
		
	public static String addCData(String xml)
	{
		String cdataXml = "<![CDATA[";
		cdataXml += xml;
		cdataXml += "]]>";
		return cdataXml;
	}	
	
	public static Map<String, String> parseMap(String xml) throws Exception
	{
		InputStream is = null;
		try
		{
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			is = new ByteArrayInputStream(xml.getBytes());
			SAXHandler handler = new SAXHandler();
			parser.parse(is, handler);
			
			return handler.map;
		}
		finally
		{
			if(is != null)
			{
				is.close();
			}
		}
	}
	//-----------------SAX Parser-------------------------------------------------------------------------------------------------------------
	private static class SAXHandler extends DefaultHandler
	{
		private StringBuffer fullPath;
		private StringBuffer dataBuffer;
		
		private Map<String,String> map;
		private String name;
		private String value;						
		//---DefaultHandler impl---------------------------------------------------------------------------		
		public void startDocument() throws SAXException 
		{			
			this.map = new HashMap<String,String>();		
			this.fullPath = new StringBuffer();
			this.dataBuffer = new StringBuffer();
		}
				
		public void endDocument() throws SAXException 
		{			
		}				
		
		public void startElement(String uri, String localName, String qName, Attributes attributes) 
		throws SAXException 
		{		
			this.fullPath.append("/"+localName.trim());
			this.dataBuffer = new StringBuffer();
									
			if(this.fullPath.toString().equals("/map/entry"))
			{
				this.name = null;
				this.value = null;
			}						
		}
						
		public void characters(char[] ch, int start, int length) throws SAXException 
		{		
			String data = new String(ch, start, length);	
			
			if(data != null && data.trim().length()>0)
			{
				this.dataBuffer.append(data);
			}						
		}
		
		public void endElement(String uri, String localName, String qName) throws SAXException 
		{
			//Populate with data
			//Process Session object related data
			if(this.fullPath.toString().equals("/map/entry/string"))
			{
				if(this.name == null)
				{
					this.name = this.dataBuffer.toString();
				}
				else 
				{
					this.value = this.dataBuffer.toString();
				}
			}
			else if(this.fullPath.toString().equals("/map/entry"))
			{
				this.map.put(this.name, this.value);
			}
			
			
			//Reset
			String cour = this.fullPath.toString();			
			int lastIndex = cour.lastIndexOf('/');
			this.fullPath = new StringBuffer(cour.substring(0, lastIndex));
		}
	}
}
