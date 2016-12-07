/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.testapp.app;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.openmobster.android.api.sync.MobileBean;
import org.openmobster.core.mobileCloud.android_native.framework.BaseCloudActivity;
import org.openmobster.core.mobileCloud.android_native.framework.ViewHelper;

import org.openmobster.core.mobileCloud.api.ui.framework.Services;
import org.openmobster.core.mobileCloud.api.ui.framework.command.CommandContext;
import org.openmobster.core.mobileCloud.jscript.bridge.MobileBeanBridge;
import org.openmobster.core.mobileCloud.jscript.bridge.MobileRPC;

/**
 * Android Activity that integrates the OpenMobster Cloud as a service. This activity displays its HTML5 based GUI via the
 * standard 'WebView' Android component
 * 
 * @author openmobster@gmail.com
 */
public class SyncWebApp extends BaseCloudActivity
{
    private static final String LOG_TAG = "SyncWebApp";
    private WebView webView;
    
    @Override
    public void displayMainScreen() 
    { 
        //Checks if the sync channel in the Cloud has data loaded on the device
        /*if(!MobileBean.isBooted(Constants.channel))
        {
            CommandContext commandContext = new CommandContext();
            commandContext.setTarget("/channel/bootup/helper");
            Services.getInstance().getCommandService().execute(commandContext);
            return;
        }*/
        
        //Layout the home screen
        setContentView(ViewHelper.findLayoutId(this, "home"));
        
        //Get the activity's WebView instance
        this.webView = (WebView)ViewHelper.findViewById(this, "webview");
        
        //Configure the webview
        WebSettings webSettings = this.webView.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setSupportZoom(false);
        
        //Javascript must be enabled to take advantage of HTML5/Javascript based UI layer
        webSettings.setJavaScriptEnabled(true); 
        
        this.webView.setWebChromeClient(new MyWebChromeClient());
        
        //Javascript bridge to the OpenMobster MobileBean service. This provides access to data loaded in via the sync channel
        this.webView.addJavascriptInterface(new MobileBeanBridge(), "mobileBean");
        this.webView.addJavascriptInterface(new MobileRPC(), "rpc");
        
        //The application's main content specified in index.html file bundled with the App in the asset folder
        SyncWebApp.this.webView.loadUrl("file:///android_asset/html/api/api.html");
    }
    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
    	if(menu.size() > 0)
    	{
    		return true;
    	}
    	
		//Add the 'Back' Menu Item
		MenuItem back = menu.add(Menu.NONE, Menu.NONE, 0, "Back");
		back.setOnMenuItemClickListener(new OnMenuItemClickListener()
		{
			public boolean onMenuItemClick(MenuItem clickedItem)
			{
				//Load the New Ticket screen
				SyncWebApp.this.webView.loadUrl("file:///android_asset/html/api/api.html");
				return true;
			}
		});
		
		return true;
	}

	/**
     * Provides a hook for calling "alert" from javascript. Useful for
     * debugging your javascript.
     */
    final class MyWebChromeClient extends WebChromeClient 
    {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) 
        {
            Log.d(LOG_TAG, message);
            result.confirm();
            return true;
        }
    }
}
