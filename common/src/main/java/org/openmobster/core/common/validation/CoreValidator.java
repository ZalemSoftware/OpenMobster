/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common.validation;

import org.apache.commons.validator.util.ValidatorUtils;
import org.apache.commons.validator.*;
                                                          
/**
 * 
 * @author openmobster@gmail.com
 */
public class CoreValidator 
{                                                          
   /**
    * Checks if the field is required.
    *
    * @return boolean If the field isn't <code>null</code> and
    * has a length greater than zero, <code>true</code> is returned.  
    * Otherwise <code>false</code>.
    */
   public static boolean validateRequired(Object bean, Field field) 
   {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      return !GenericValidator.isBlankOrNull(value);
   }
   
   /**
    * Checks if the field is an e-mail address.
    *
    * @param 	value 		The value validation is being performed on.
    * @return	boolean		If the field is an e-mail address
    *                           <code>true</code> is returned.  
    *                           Otherwise <code>false</code>.
    */
   public static boolean validateEmail(Object bean, Field field) 
   {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      return GenericValidator.isEmail(value);
   }
   
   /**
    * Checks if the input value is a valid e-mail address
    * 
    * @param email
    * @return
    */
   public static boolean validateEmail(String email) 
   {
      return GenericValidator.isEmail(email);
   }

  
  /**
   * 
   * @param bean
   * @param field
   * @return
   */
  public static boolean minimumLength(Object bean, Field field) 
  {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      String varValue = field.getVarValue("minimumLength");
      return GenericValidator.minLength(value, Integer.parseInt(varValue));
  }    
}                                                         
