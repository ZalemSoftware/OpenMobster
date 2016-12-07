/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.HashSet;

import org.jboss.kernel.plugins.bootstrap.basic.BasicBootstrap;
import org.jboss.kernel.plugins.deployment.xml.BasicXMLDeployer;
import org.openmobster.core.common.transaction.TransactionHelper;

/**
 * 
 * @author openmobster@gmail.com
 */
public class ServiceBootstrap extends BasicBootstrap
{
	//The deployer
	protected BasicXMLDeployer deployer;
	   
	//The arguments
	protected String[] args;
	   
	public ServiceBootstrap(String[] args) throws Exception
	{
		super();
	    this.args = args;
	}
		
	public void bootstrap() throws Throwable
    {
		super.bootstrap();	      
		deployer = new BasicXMLDeployer(getKernel());
      
		//Deploy the server side components
	    ClassLoader cl = Thread.currentThread().getContextClassLoader();
	    for (Enumeration e = cl.getResources("META-INF/openmobster-config.xml"); e.hasMoreElements(); )
	    {
	    	URL url = (URL) e.nextElement();
	        deploy(url);
	    }	
	    	    
      
	    // Validate that everything is ok
	    deployer.validate();            	
    }
	
	public void bootstrapMobletApps() throws Throwable
	{
		boolean isStartedHere = TransactionHelper.startTx(); 
		try
		{
			Set<URL> appUrls = new HashSet<URL>();
			
			//Deploy the moblet-apps
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
		    for(Enumeration e = cl.getResources("META-INF/moblet-apps.xml");e.hasMoreElements();)
		    {
		    	URL url = (URL) e.nextElement();
		    	appUrls.add(url);
		    	this.deployMobletApps(url);
		    }
		    
		    this.undeployMobletApps(appUrls);
		    
		    if(isStartedHere)
		    {
		    	TransactionHelper.commitTx();
		    }
		}
		catch(Throwable t)
		{
			if(isStartedHere)
			{
				TransactionHelper.rollbackTx();
			}
			throw t;
		}
	}
	
	public synchronized void shutdown()
	{
		deployer.shutdown();
	}
	
	public synchronized void redeploy(URL url) throws Throwable
	{
		this.undeploy(url);
		this.deploy(url);
	}
	//---------------------------------------------------------------------------------------------------------	
    protected void deploy(URL url) throws Throwable
    {
      deployer.deploy(url);
    }
	      
    protected void undeploy(URL url)
    {
      try
      {
         deployer.undeploy(url);
      }
      catch (Throwable t)
      {
      }
    }	
    //----------------------------------------------------------------------------------------------------------
    private void deployMobletApps(URL url) throws Throwable
    {
    	Class mobletDeployerClass = Thread.currentThread().
    	getContextClassLoader().loadClass("org.openmobster.core.moblet.deployment.MobletDeployer");
    	
    	//Invoke static method via reflection
    	Method getInstance = mobletDeployerClass.getMethod("getInstance", null);
    	Object mobletDeployer = getInstance.invoke(null, null);
    	
    	//Deploy the moblet-apps associated with this artifact
    	Method deploy = mobletDeployer.getClass().getMethod("deploy", new Class[]{URL.class});
    	deploy.invoke(mobletDeployer, new Object[]{url});
    }
    
    private void undeployMobletApps(Set<URL> appUrls)
    {
    	try
    	{
	    	Class mobletDeployerClass = Thread.currentThread().
	    	getContextClassLoader().loadClass("org.openmobster.core.moblet.deployment.MobletDeployer");
	    	
	    	//Invoke static method via reflection
	    	Method getInstance = mobletDeployerClass.getMethod("getInstance", null);
	    	Object mobletDeployer = getInstance.invoke(null, null);
	    	
	    	//Undeploy the moblet-apps deleted from the deploy folder
	    	Method undeploy = mobletDeployer.getClass().getMethod("undeploy", new Class[]{Set.class});
	    	undeploy.invoke(mobletDeployer, new Object[]{appUrls});
    	}
    	catch(Throwable t)
    	{
    		//don't worry about undeploying
    	}
    }
}
