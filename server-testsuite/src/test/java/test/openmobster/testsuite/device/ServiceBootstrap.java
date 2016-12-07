/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.openmobster.testsuite.device;

import java.net.URL;
import java.util.Enumeration;

import org.jboss.kernel.plugins.bootstrap.basic.BasicBootstrap;
import org.jboss.kernel.plugins.deployment.xml.BasicXMLDeployer;

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
      	      
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      for (Enumeration e = cl.getResources("META-INF/openmobster-config.xml"); e.hasMoreElements(); )
      {
         URL url = (URL) e.nextElement();
         
         String urlName = url.getPath();         
         
         //Exclude some artifacts
         if(
        		 urlName.contains("synchronizer") ||
        		 urlName.contains("dataService") ||
        		 urlName.contains("services") ||
        		 urlName.contains("mobileObject") ||
        		 urlName.contains("mobileContainer")
        )
         {
        	 continue;
         }
         
         /*System.out.println("About To Deploy-------------------------------------------------");
         System.out.println(urlName);
         System.out.println("-------------------------------------------------");*/
         
         deploy(url);
      }	      
      
      // Validate that everything is ok
      deployer.validate();
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
}
