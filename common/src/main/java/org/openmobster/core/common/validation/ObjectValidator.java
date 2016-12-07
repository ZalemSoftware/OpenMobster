/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common.validation;

import java.io.InputStream;

import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;

import org.apache.log4j.Logger;

import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;

/**
 * @author openmobster@gmail.com
 */
public class ObjectValidator 
{	
	private static Logger log = Logger.getLogger(ObjectValidator.class);
		
	private String name = null;
		
	private String rulesFile = null;
		
	private ValidatorResources validatorResources = null;
		
	public ObjectValidator()
	{
		
	}
	
	public String getRulesFile() 
	{
		return rulesFile;
	}
	
	public void setRulesFile(String rulesFile) 
	{
		this.rulesFile = rulesFile;
	}
			
	public String getName() 
	{
		return name;
	}
	
	public void setName(String name) 
	{
		this.name = name;
	}
	
	public void start()
	{
		InputStream is = null;
		try
		{
			is = Thread.currentThread().getContextClassLoader().
			getResourceAsStream(this.rulesFile);
			
			this.validatorResources = new ValidatorResources(is);
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new ValidationException(e);
		}
		finally
		{
			if(is != null)
			{
				try
				{
					is.close();
				}
				catch(Exception e)
				{
					log.error(this, e);
					throw new ValidationException(e);
				}
			}
		}
	}
		
	public void stop()
	{
		
	}
	
	/**
	 * Validates the value of the specified Field on the target Object
	 * 
	 * @param object
	 * @param fieldName
	 * @return a Set of Validation Error Keys
	 * @throws ValidationException
	 */
	public Set<String> validate(Object object, String fieldName) 
	throws ValidationException
	{
		try
		{
			Set<String> errorKeys = new HashSet<String>();
			String objectId = object.getClass().getName();
			
			//Setup the Validator
			Validator validator = new Validator(this.validatorResources, objectId);
			validator.setParameter(Validator.BEAN_PARAM, object);
			validator.setFieldName(fieldName);
			
			ValidatorResults results = validator.validate();
			
			Form form = this.validatorResources.getForm(Locale.getDefault(), objectId);
			Iterator propertyNames = results.getPropertyNames().iterator();
			while(propertyNames.hasNext())
			{
				String property = (String)propertyNames.next();
				ValidatorResult result = results.getValidatorResult(property);
				Map actionMap = result.getActionMap();
				Iterator keys = actionMap.keySet().iterator();
				while (keys.hasNext()) 
				{
	                String actionName = (String) keys.next();
	                if (!result.isValid(actionName)) 
	                {
	                	Field field = form.getField(property);
	                	Arg[] args = field.getArgs(actionName);
	                	if(args != null)
	                	{
	                		for(int i=0; i<args.length; i++)
	                		{
	                			errorKeys.add(args[i].getKey());
	                		}
	                	}
	                }
	            }
			}
			
			return errorKeys;
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new ValidationException(e);
		}
	}
	
	/**
	 * Fully validates all the Fields of the specified Object
	 * 
	 * @param object
	 * @return a Map of Fields to their corresponding Validation Error Keys
	 * 
	 * @throws ValidationException
	 */
	public Map<String, String[]> validate(Object object) 
	throws ValidationException
	{
		try
		{
			Map<String, String[]> errorKeys = new HashMap<String, String[]>();
			String objectId = object.getClass().getName();
			
			//Setup the Validator
			Validator validator = new Validator(this.validatorResources, objectId);
			validator.setParameter(Validator.BEAN_PARAM, object);
			
			ValidatorResults results = validator.validate();
			
			Form form = this.validatorResources.getForm(Locale.getDefault(), objectId);
			Iterator propertyNames = results.getPropertyNames().iterator();
			while(propertyNames.hasNext())
			{
				Set<String> cour = new HashSet<String>();
				String property = (String)propertyNames.next();
				ValidatorResult result = results.getValidatorResult(property);
				Map actionMap = result.getActionMap();
				Iterator keys = actionMap.keySet().iterator();
				boolean errorFound = false;
				while (keys.hasNext()) 
				{
	                String actionName = (String) keys.next();
	                if (!result.isValid(actionName)) 
	                {
	                	Field field = form.getField(property);
	                	Arg[] args = field.getArgs(actionName);
	                	if(args != null)
	                	{
	                		for(int i=0; i<args.length; i++)
	                		{
	                			cour.add(args[i].getKey());
	                			errorFound = true;
	                		}
	                	}
	                }
	            }
				if(errorFound)
				{
					errorKeys.put(property, cour.toArray(new String[cour.size()]));
				}
			}
			
			return errorKeys;
		}
		catch(Exception e)
		{
			log.error(this, e);
			throw new ValidationException(e);
		}
	}
}
