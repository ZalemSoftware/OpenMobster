/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileObject.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.OutputStreamWriter;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.AbstractXmlDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

/**
 * @author openmobster@gmail.com
 */
class MobileObjectDriver extends AbstractXmlDriver
{
	MobileObjectDriver()
	{
		super(new XmlFriendlyReplacer());
	}

	public HierarchicalStreamReader createReader(Reader reader) 
	{		
		throw new UnsupportedOperationException("MobileObjectDriver does not support de-serialization");
	}

	public HierarchicalStreamReader createReader(InputStream is) 
	{		
		throw new UnsupportedOperationException("MobileObjectDriver does not support de-serialization");
	}

	public HierarchicalStreamWriter createWriter(Writer out) 
	{		
		return new MobileObjectWriter(out);
	}

	public HierarchicalStreamWriter createWriter(OutputStream out) 
	{		
		return createWriter(new OutputStreamWriter(out));
	}
}
