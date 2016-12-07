/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.common.validation;

import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.validation.ObjectValidator;

/**
 * @author openmobster@gmail.com
 */
public class TestObjectValidator extends TestCase
{
	/**
	 * 
	 */
	private static Logger log = Logger.getLogger(TestObjectValidator.class);
	
	private ObjectValidator validator = null;
	
	/**
	 * 
	 */
	public void setUp()
	{
		ServiceManager.bootstrap();
		this.validator = (ObjectValidator)ServiceManager.locate("test://ObjectValidator");
	}
	
	/**
	 * 
	 */
	public void tearDown()
	{
		this.validator = null;
		ServiceManager.shutdown();
	}
	
	/**
	 * 
	 *
	 */
	public void testBeanValidation()
	{
		TestBean bean = new TestBean();
		
		Set<String> errorKeys = this.validator.validate(bean, "principal");
		this.printValidationErrors(errorKeys);
		assertTrue("Email Required Validation Failed..", errorKeys.contains("emailRequired"));
		
		bean.setPrincipal("blah");
		errorKeys = this.validator.validate(bean, "principal");
		this.printValidationErrors(errorKeys);	
		assertTrue("Email Value Validation Failed..", errorKeys.contains("emailInvalid"));
		
		bean.setPrincipal("blah@blah.com");
		errorKeys = this.validator.validate(bean, "principal");
		this.printValidationErrors(errorKeys);
		assertTrue("Email Length Validation Failed..", errorKeys.contains("emailInvalidLength"));
	}
	
	/**
	 * 
	 * @param errorKeys
	 */
	private void printValidationErrors(Set<String> errorKeys)
	{
		log.info("--------------------------------------------");
		if(errorKeys.isEmpty())
		{
			log.info("Object fully passed Validation......");
		}
		
		for(String errorKey: errorKeys)
		{
			log.info("Error Key: "+errorKey);
		}		
		log.info("--------------------------------------------");
	}
}
