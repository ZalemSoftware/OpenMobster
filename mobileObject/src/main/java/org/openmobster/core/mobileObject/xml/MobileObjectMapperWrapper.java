/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileObject.xml;

import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 * @author openmobster@gmail.com
 */
class MobileObjectMapperWrapper extends MapperWrapper
{		
	MobileObjectMapperWrapper(Mapper wrapped) 
	{
        super(wrapped);
    }
	
	public String serializedMember(Class type, String memberName) 
	{
		String fieldType = "field";								
		return super.serializedMember(type, fieldType+":"+memberName);
	}
}
