/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.location.yahoo;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author openmobster@gmail.com
 */
public final class ResultSet
{
	private boolean hasError;
	private List<Result> results;
	
	public ResultSet()
	{
		this.results = new ArrayList<Result>();
	}

	public boolean isHasError()
	{
		return hasError;
	}

	public void setHasError(boolean hasError)
	{
		this.hasError = hasError;
	}

	public List<Result> getResults()
	{
		return results;
	}

	public void addResult(Result result)
	{
		this.results.add(result);
	}
	
	public void sort()
	{
		Collections.sort(this.results, new ResultComparator());
	}
}
