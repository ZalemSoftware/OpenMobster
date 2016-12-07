/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.mgr;

import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.command.AppException;

/**
 *
 * @author openmobster@gmail.com
 */
public interface Task
{
	public void execute(CommandContext commandContext) throws AppException;
	
	public void postExecute(CommandContext commandContext) throws AppException;
	
	public void postExecuteAppException(CommandContext commandContext) throws AppException;
}
