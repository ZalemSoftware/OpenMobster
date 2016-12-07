/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.core.security.identity;

import java.util.Set;
import java.util.Map;

import org.apache.log4j.Logger;
import junit.framework.TestCase;

import org.openmobster.core.common.ServiceManager;
import org.openmobster.core.common.validation.ObjectValidator;
import org.openmobster.core.security.identity.Identity;

/**
 * @author openmobster@gmail.com
 *
 */
public class TestIdentityValidation extends TestCase 
{
	private static Logger log = Logger.getLogger(TestIdentityValidation.class);
	
	private ObjectValidator domainValidator;
	
	public void setUp()
	{
		ServiceManager.bootstrap();		
		
		this.domainValidator = (ObjectValidator)ServiceManager.locate("security://DomainValidator");
	}
	
	public void tearDown()
	{
		ServiceManager.shutdown();	
	}
	//------------------------------------------------------------------------------------------------------------------------------------------------
	public void testValidation()
	{
		Identity identity = new Identity();
		Map<String, String[]> errorKeys = this.domainValidator.validate(identity);
		
		this.printValidationErrors(errorKeys);
		
		identity.setCredential("somehash");
		identity.setPrincipal("blah");
		errorKeys = this.domainValidator.validate(identity);
		
		this.printValidationErrors(errorKeys);
		
		identity.setPrincipal("blah@gmail.com");
		errorKeys = this.domainValidator.validate(identity);
		
		this.printValidationErrors(errorKeys);
	}	
	//------------------------------------------------------------------------------------------------------------------------------------------------
	private void printValidationErrors(Map<String, String[]> errorKeys)
	{
		log.info("--------------------------------------------");
		if(errorKeys == null || errorKeys.isEmpty())
		{
			log.info("Object fully passed Validation......");
		}
		else
		{
			Set<String> fieldErrorKeys = errorKeys.keySet();			
			for(String fieldErrorKey: fieldErrorKeys)
			{
				String[] fieldErrors = errorKeys.get(fieldErrorKey);
				for(String error: fieldErrors)
				{
					
					log.info("Error Key: ["+fieldErrorKey+"]."+error);				
				}
			}
		}
		log.info("--------------------------------------------");
	}
}
