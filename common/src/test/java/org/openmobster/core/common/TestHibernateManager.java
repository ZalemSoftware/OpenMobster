/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common;

import java.io.InputStream;
import org.w3c.dom.Document;

import junit.framework.TestCase;

import org.openmobster.core.common.database.HibernateManager;
import org.openmobster.core.common.XMLUtilities;

/**
 * 
 * @author openmobster@gmail.com
 */
public class TestHibernateManager extends TestCase
{
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testStartSessionFactory() throws Exception
	{
		HibernateManager hib = new HibernateManager();
		
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("hibernate-test.cfg.xml");
		Document doc = XMLUtilities.parse(is);
		hib.startSessionFactory(doc);
	}
}
