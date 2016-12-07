/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.mobileObject;

import java.util.List;
import java.util.ArrayList;

/**
 * @author openmobster@gmail
 *
 */
public final class LogicChain 
{
	public static final int AND = 0; //&&
	public static final int OR = 1; //||
	
	private List<LogicExpression> chain;
	private int logicLink;
	
	LogicChain()
	{
		this.chain = new ArrayList<LogicExpression>();
	}
	
	static LogicChain createANDChain()
	{
		LogicChain chain = new LogicChain();
		chain.logicLink = LogicChain.AND;
		return chain;
	}
	
	static LogicChain createORChain()
	{
		LogicChain chain = new LogicChain();
		chain.logicLink = LogicChain.OR;
		return chain;
	}
	//--------------------------------------------------------------------------------------------------------------------------------	
	LogicChain add(LogicExpression expression)
	{
		if(expression == null)
		{
			throw new IllegalArgumentException("LogicExpression must be specified!!");
		}
		
		this.chain.add(expression);
		return this;
	}
	
	List<LogicExpression> getExpressions()
	{
		return this.chain;
	}
	
	int getLogicLink()
	{
		return this.logicLink;
	}
}
