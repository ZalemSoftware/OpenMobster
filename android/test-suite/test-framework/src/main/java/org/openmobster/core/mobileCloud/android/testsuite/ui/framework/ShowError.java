/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android.testsuite.ui.framework;

import android.app.Activity;

import org.openmobster.core.mobileCloud.android.service.Registry;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.api.ui.framework.resources.AppResources;
import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.SystemLocaleKeys;

import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;


/**
 * @author openmobster@gmail.com
 *
 */
final class ShowError
{
	public static void showGenericError(final CommandContext commandContext)
	{
		final Activity currentActivity = Services.getInstance().getCurrentActivity();
				
		AppResources appResources = Services.getInstance().getResources();
		
		String errorTitle = appResources.localize(
		SystemLocaleKeys.system_error, 
		"system_error");
		
		String errorMsg = appResources.localize(
		SystemLocaleKeys.unknown_system_error, 
		"unknown_system_error");
		
		//Show the dialog
    	ViewHelper.getOkModal(currentActivity, errorTitle, errorMsg).show();
	}
}
