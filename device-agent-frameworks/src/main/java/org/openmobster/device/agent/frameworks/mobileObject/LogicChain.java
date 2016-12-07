/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.frameworks.mobileObject;

import java.util.Vector;

/**
 * @author openmobster@gmail
 *
 */
final class LogicChain 
{
	static final int AND = 0; //&&
	static final int OR = 1; //||
	
	private Vector chain;
	private int logicLink;
	
	LogicChain()
	{
		this.chain = new Vector();
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
		
		this.chain.addElement(expression);
		return this;
	}
	
	Vector getExpressions()
	{
		return this.chain;
	}
	
	int getLogicLink()
	{
		return this.logicLink;
	}
}
