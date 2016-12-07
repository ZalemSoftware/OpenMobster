/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * 
 * @author openmobster@gmail.com
 */
public class XMLUtilities
{
	private static XStream xstream = new XStream(new StaxDriver());
	private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	
	/**
	 * Parses an input stream of xml data into a DOM tree
	 * 
	 * @param is
	 * @return the DOM tree
	 */
	public static Document parse(InputStream is)
	{
		try
		{
			Document document = null;
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(is);
			
			return document;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if(is != null)
			{
				try{is.close();}catch(Exception e){throw new RuntimeException(e);}
			}
		}
	}
	
	/**
	 * Parses a String of xml data into a DOM tree
	 * 
	 * @param xml
	 * @return the DOM tree
	 */
	public static Document parse(String xml)
	{
		InputStream is = null;
		try
		{
			Document document = null;
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			document = builder.parse(is);
			
			return document;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if(is != null)
			{
				try{is.close();}catch(Exception e){throw new RuntimeException(e);}
			}
		}
	}
	
	/**
	 * Properly escapes all illegal characters inside the XML String
	 * 
	 * @param xml
	 * @return Properly escaped XML representation
	 */
	public static String cleanupXML(String xml)
	{
		String cleanedup = "";
		
		if(xml != null)
		{
			cleanedup = xml.replaceAll("&", "&amp;");
			cleanedup = cleanedup.replaceAll("<", "&lt;");
			cleanedup = cleanedup.replaceAll(">", "&gt;");			
			cleanedup = cleanedup.replaceAll("\"", "&quot;");
			cleanedup = cleanedup.replaceAll("'", "&apos;");
		}
		
		return cleanedup;
	}
	
	/**
	 * Properly unescapes illegal characters from the XML representation
	 * 
	 * @param xml
	 * @return the unescaped XML String
	 */
	public static String restoreXML(String xml)
	{
		String restore = "";
		
		if(xml != null)
		{
			restore = xml.replaceAll("&apos;", "'");
			restore = restore.replaceAll("&quot;", "\"");
			restore = restore.replaceAll("&gt;", ">");			
			restore = restore.replaceAll("&lt;", "<");
			restore = restore.replaceAll("&amp;", "&");
		}
		
		return restore;
	}
	
	
	/**
	 * Determines if the specified Parent contains the specified Element
	 * 
	 * @param parent
	 * @param elementName
	 * @return true: if the Parent contains the Element, false: otherwise
	 */
	public static boolean contains(Element parent,String elementName)
	{
		boolean contains = false;
		
		NodeList nodes = parent.getElementsByTagName(elementName);
		if(nodes != null && nodes.getLength() > 0)
		{
			contains = true;
		}
		
		return contains;
	}
	
	/**
	 * Determines if the specified Document (DOM Tree) contains the specified Element
	 * 
	 * @param document
	 * @param elementName
	 * @return true: if the Document(DOM Tree) contains the Element, false: otherwise
	 */
	public static boolean contains(Document document,String elementName)
	{
		boolean contains = false;
		
		NodeList nodes = document.getElementsByTagName(elementName);
		if(nodes != null && nodes.getLength() > 0)
		{
			contains = true;
		}
		
		return contains;
	}
	
	/**
	 * Removes the CDATA protection of an XML String
	 * 
	 * @param xml
	 * @return the XML String without the CDATA protection
	 */
	public static String removeCData(String xml)
	{
		String cleanXML = xml.replace("<![CDATA[", "");
		cleanXML = cleanXML.replace("]]>", "");
		
		return cleanXML;
	}
	
	/**
	 * Adds the CDATA protection to an XML String
	 * 
	 * @param xml
	 * @return the XML String protected by CDATA
	 */
	public static String addCData(String xml)
	{
		String cdataXml = "<![CDATA[";
		cdataXml += xml;
		cdataXml += "]]>";
		return cdataXml;
	}
	
	
	/**	 
	 *  
	 * Serializes the specified Object into an XML representation
	 * 
	 * @param object
	 * @return XML Representation of the Object
	 */
	public static String marshal(Object object)
	{
		String xml = xstream.toXML(object);
		
		if(xml.contains("<?xml version=\"1.0\" ?>"))
		{
			xml = xml.replace("<?xml version=\"1.0\" ?>", "");
		}
		
		if(xml.contains("<?xml version='1.0' encoding='UTF-8'?>"))
		{
			xml = xml.replace("<?xml version='1.0' encoding='UTF-8'?>","");
		}
		
		return xml.trim();
	}
	
	/**
	 * De-Serializes the XML representation of an Object 
	 * 
	 * @param xml
	 * @return the De-Serialized Object
	 */
	public static Object unmarshal(String xml)
	{
		return xstream.fromXML(xml);
	}
}
