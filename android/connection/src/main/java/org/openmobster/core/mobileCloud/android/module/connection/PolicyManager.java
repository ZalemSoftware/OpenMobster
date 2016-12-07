/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openmobster.core.mobileCloud.android.module.connection;

import java.io.File;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.os.Environment;

import org.openmobster.core.mobileCloud.android.service.Registry;

/**
 *
 * @author openmobster@gmail.com
 */
public final class PolicyManager
{
	private static PolicyManager singleton;
	
	DevicePolicyManager devicePolicyManager;
    ComponentName deviceAdminReceiver;
	
	private PolicyManager()
	{
		Context context = Registry.getActiveInstance().getContext();
		this.devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        this.deviceAdminReceiver = new ComponentName(context, PolicyReceiver.class);
	}
	
	public static PolicyManager getInstance()
	{
		if(PolicyManager.singleton == null)
		{
			synchronized(PolicyManager.class)
			{
				if(PolicyManager.singleton == null)
				{
					PolicyManager.singleton = new PolicyManager();
				}
			}
		}
		return PolicyManager.singleton;
	}
	
	public void showAdminScreen(Activity parent)
	{
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminReceiver);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"");
		parent.startActivityForResult(intent, 1);
	}
	
	private boolean isAdminActive()
	{
		return this.devicePolicyManager.isAdminActive(this.deviceAdminReceiver);
	}
	
	public void lock()
	{
		if(this.isAdminActive())
		{
			this.devicePolicyManager.lockNow();
		}
	}
	
	public void wipe()
	{
		if(this.isAdminActive())
		{
			//Recursively erase the SDCard
			String state = Environment.getExternalStorageState();
			if(state.equals(Environment.MEDIA_MOUNTED))
			{
				File sdRoot = Environment.getExternalStorageDirectory();
				if(sdRoot != null)
				{
					this.deleteDir(sdRoot);
				}
			}
			
			//Wipe the phone now
			this.devicePolicyManager.wipeData(0);
		}
	}
	
	private boolean deleteDir(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			if(children != null)
			{
				for (int i = 0; i < children.length; i++)
				{
					boolean success = deleteDir(new File(dir, children[i]));
					if (!success)
					{
						return false;
					}
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}
	
	public static class PolicyReceiver extends DeviceAdminReceiver 
	{
	}
}
