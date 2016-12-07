/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.device.agent.frameworks.mobileObject;

/**
 * @author openmobster@gmail
 *
 */
final class LogicExpression 
{
	static final int OP_EQUALS = 0;
	static final int OP_NOT_EQUALS = 1;
	static final int OP_LIKE = 2;
	static final int OP_CONTAINS = 3;
	
	private String lhs;
	private String rhs;
	private int op;
	
	private LogicExpression(String lhs, String rhs, int op)
	{
		this.lhs = lhs;
		this.rhs = rhs;
		this.op = op;
	}
	
	static LogicExpression createInstance(String lhs, String rhs, int op)
	{
		if(lhs == null)
		{
			throw new IllegalArgumentException("LHS portion of the expression must be specified!!");
		}
		if(rhs == null)
		{
			throw new IllegalArgumentException("RHS portion of the expression must be specified!!");
		}
		if(op != LogicExpression.OP_EQUALS && op != LogicExpression.OP_NOT_EQUALS && 
		   op != LogicExpression.OP_LIKE && op != LogicExpression.OP_CONTAINS)
		{
			throw new UnsupportedOperationException("Specified Operation is unsupported!!");
		}
		
		return new LogicExpression(lhs, rhs, op);
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	String getLhs() 
	{
		return this.lhs;
	}

	String getRhs() 
	{
		return this.rhs;
	}

	int getOp() 
	{
		return this.op;
	}		
}
