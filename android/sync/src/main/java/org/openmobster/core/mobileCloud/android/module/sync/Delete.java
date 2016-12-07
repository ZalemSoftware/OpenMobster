/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.module.sync;


/**
 * 
 * @author openmobster@gmail.com
 *
 */
public final class Delete extends AbstractOperation
{	
	private boolean archive;
	private boolean softDelete;
		
	/**
	 * 
	 * @return
	 */
	public boolean isArchive()
	{
		return archive;
	}

	/**
	 * 
	 * @param archive
	 */
	public void setArchive(boolean archive)
	{
		this.archive = archive;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSoftDelete()
	{
		return softDelete;
	}

	/**
	 * 
	 * @param softDelete
	 */
	public void setSoftDelete(boolean softDelete)
	{
		this.softDelete = softDelete;
	}	
}
