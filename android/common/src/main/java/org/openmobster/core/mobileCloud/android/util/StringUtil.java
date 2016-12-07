/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.util;

import java.util.Vector;

/**
 * @author openmobster@gmail.com
 *
 */
public final class StringUtil 
{
	public static String[] tokenize(String input, String token)
	{
		String[] tokens = null;
		
		Vector cour = new Vector();
		if(input.indexOf(token) != -1)
		{
			int startIndex = 0;
			int endIndex = input.indexOf(token, startIndex);						
			do
			{
				String courToken = input.substring(startIndex, endIndex);
				if(courToken.trim().length()>0)
				{
					cour.addElement(courToken);
				}
				
				//Recalculate the indices
				startIndex = endIndex + token.length();
				if(startIndex >= input.length()-1)
				{
					break;
				}
				
				//Calculate the endIndex to get the next token
				endIndex = input.indexOf(token, startIndex);
				if(endIndex == -1)
				{
					endIndex = input.length();
				}
				
			}while(true);
		}
		else
		{
			cour.addElement(input);
		}
		
		tokens = new String[cour.size()];
		for(int i=0,size=cour.size(); i<size; i++)
		{
			tokens[i] = (String)cour.elementAt(i);
		}
		
		return tokens;
	}
	
	public static String replaceAll(String input, String searchStr, String replaceWithStr)
	{
		StringBuffer buffer = new StringBuffer();
		int startIndex = 0;
		int oldIndex = 0;
		
		if(input.indexOf(searchStr) == -1)
		{
			return input;
		}
		
		while((startIndex=input.indexOf(searchStr, oldIndex)) != -1)
		{
			buffer.append(input.substring(oldIndex, startIndex));
			buffer.append(replaceWithStr);
			startIndex += searchStr.length();
			oldIndex = startIndex;
			
			if(oldIndex >= input.length())
			{
				break;
			}
		}
		
		if(oldIndex < input.length())
		{
			buffer.append(input.substring(oldIndex));
		}		
				
		return buffer.toString();
	}
}
