/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework.events;

import java.io.Serializable;

import android.view.View;
import android.widget.ListView;

/**
 * @author openmobster@gmail.com
 *
 */
public final class ListItemClickEvent implements Serializable
{
	private ListView listView;
	private View view;
	private int position;
	private long id;
	
	public ListItemClickEvent(ListView listView,View view,int position,long id)
	{
		this.listView = listView;
		this.view = view;
		this.position = position;
		this.id = id;
	}

	public ListView getListView()
	{
		return listView;
	}

	public View getView()
	{
		return view;
	}

	public int getPosition()
	{
		return position;
	}

	public long getId()
	{
		return id;
	}
}
