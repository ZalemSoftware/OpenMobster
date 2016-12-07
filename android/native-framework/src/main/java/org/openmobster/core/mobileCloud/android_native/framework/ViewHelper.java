/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.android_native.framework;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * @author openmobster@gmail.com
 * 
 */
public class ViewHelper
{
	public static View findViewById(Activity activity, String viewId)
	{
		try
		{
			String idClass = activity.getPackageName() + ".R$id";
			Class clazz = Class.forName(idClass);
			Field field = clazz.getField(viewId);

			return activity.findViewById(field.getInt(clazz));
		} catch (Exception e)
		{
			return null;
		}
	}

	public static int findViewId(Activity activity, String variable)
	{
		try
		{
			String idClass = activity.getPackageName() + ".R$id";
			Class clazz = Class.forName(idClass);
			Field field = clazz.getField(variable);

			return field.getInt(clazz);
		} catch (Exception e)
		{
			return -1;
		}
	}

	public static int findLayoutId(Activity activity, String variable)
	{
		try
		{
			String idClass = activity.getPackageName() + ".R$layout";
			Class clazz = Class.forName(idClass);
			Field field = clazz.getField(variable);

			return field.getInt(clazz);
		} catch (Exception e)
		{
			return -1;
		}
	}

	public static int findDrawableId(Activity activity, String variable)
	{
		try
		{
			String idClass = activity.getPackageName() + ".R$drawable";
			Class clazz = Class.forName(idClass);
			Field field = clazz.getField(variable);

			return field.getInt(clazz);
		} catch (Exception e)
		{
			return -1;
		}
	}

	public static AlertDialog getOkModal(final Context currentActivity,
			String title, String message)
	{
		AlertDialog okModal = null;

		okModal = new AlertDialog.Builder(currentActivity).setTitle(title)
				.setMessage(message).setCancelable(false).create();
		okModal.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int status)
					{
						dialog.dismiss();
					}
				});

		return okModal;
	}

	public static AlertDialog getOkModalWithCloseApp(
			final Activity currentActivity, String title, String message)
	{
		AlertDialog okModal = null;

		okModal = new AlertDialog.Builder(currentActivity).setTitle(title)
				.setMessage(message).setCancelable(false).create();
		okModal.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int status)
					{
						dialog.dismiss();
						currentActivity.finish();
					}
				});

		return okModal;
	}

	public static AlertDialog getOkAttachedModalWithCloseApp(final int dialogId,
			final Activity currentActivity, String title, String message)
	{
		AlertDialog okModal = null;

		okModal = new AlertDialog.Builder(currentActivity).setTitle(title)
				.setMessage(message).setCancelable(false).create();
		okModal.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int status)
					{
						currentActivity.dismissDialog(dialogId);
						currentActivity.removeDialog(dialogId);
						currentActivity.finish();
					}
				});

		return okModal;
	}
}
