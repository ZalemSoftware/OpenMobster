/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.mobileObject;

import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**
 * @author openmobster@gmail.com
 *
 */
final class Query 
{
	private LogicChain logic;
	
	private Query(LogicChain logic)
	{
		this.logic = logic;
	}
		
	static Query createInstance(LogicChain logic)
	{
		if(logic == null)
		{
			throw new IllegalArgumentException("LogicChain must be specified!!");
		}
		
		return new Query(logic);
	}
	//------------------------------------------------------------------------------------------------------------------------
	Set<MobileObject> executeQuery(Set<MobileObject> mobileBeans)
	{
		Set<MobileObject> result = new HashSet<MobileObject>();
		
		if(mobileBeans != null)
		{
			int size = mobileBeans.size();
			for(MobileObject cour:mobileBeans)
			{
				if(this.isMatched(cour))
				{
					result.add(cour);
				}
			}
		}
		
		return result;
	}
	//------------------------------------------------------------------------------------------------------------------------
	private boolean isMatched(MobileObject object)
	{
		List<LogicExpression> expressions = this.logic.getExpressions();
		
		int size = expressions.size();
		int exprMatchCount = 0;
		for(LogicExpression expression: expressions)
		{
			String lhsValue = object.getValue(expression.getLhs());
			String rhsValue = expression.getRhs();
			
			//Evaluate the expression
			if(this.eval(lhsValue, rhsValue, expression.getOp()))
			{
				exprMatchCount++;
			}
		}
						
		return this.eval(this.logic.getLogicLink(), exprMatchCount, size);
	}
	
	private boolean eval(String lhsValue, String rhsValue, int op)
	{
		switch(op)
		{
			case LogicExpression.OP_EQUALS:
				if(lhsValue != null && lhsValue.equals(rhsValue))
				{
					return true;
				}
			break;
			
			case LogicExpression.OP_NOT_EQUALS:
				if(lhsValue != null && !(lhsValue.equals(rhsValue)))
				{
					return true;
				}
			break;
			
			case LogicExpression.OP_LIKE:
				if(lhsValue != null && lhsValue.startsWith(rhsValue))
				{
					return true;
				}
			break;
			
			case LogicExpression.OP_CONTAINS:
				if(lhsValue != null && lhsValue.indexOf(rhsValue)!=-1)
				{
					return true;
				}
			break;
			
			default:				
			break;
		}
		
		return false;
	}
	
	private boolean eval(int logicLink, int exprMatchCount, int totalExprCount)
	{
		switch(logicLink)
		{
			case LogicChain.AND:
				if(exprMatchCount == totalExprCount)
				{
					return true;
				}			
			break;
			
			case LogicChain.OR:
				if(exprMatchCount > 0)
				{
					return true;
				}
			break;
			
			default:
			break;
		}
		
		return false;
	}
}
