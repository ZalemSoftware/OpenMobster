/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location.yahoo;

import java.util.Comparator;

/**
 *
 * @author openmobster@gmail.com
 */
public class ResultComparator implements Comparator<Result>
{
	public int compare(Result lhs, Result rhs)
	{
		int rhsQuality = rhs.getAddress().getQuality();
		int lhsQuality = lhs.getAddress().getQuality();
		
		if(lhsQuality < rhsQuality)
		{
			return -1;
		}
		
		if(lhsQuality > rhsQuality)
		{
			return 1;
		}
		
		return 0;
	}
}
