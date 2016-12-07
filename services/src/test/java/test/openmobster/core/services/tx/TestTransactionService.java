/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.services.tx;

import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.database.HibernateManager;

/**
 * @author openmobster@gmail.com
 */
public class TestTransactionService extends TestCase 
{
	private HibernateManager hibernateManager;
	
	public void setUp()
	{
		ServiceManager.bootstrap();
		
		this.hibernateManager = (HibernateManager)ServiceManager.locate("test://HibernateManager");
	}
	
	public void testTransactionManager() throws Exception
	{
		assertNotNull("Hibernate Manager must not be null", this.hibernateManager);
	}
}
