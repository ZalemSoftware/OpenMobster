/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.cmdline;

import org.apache.log4j.Logger;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.openmobster.core.console.cmdline.shell.ConsoleShell;

import org.openmobster.cloudConnector.api.Configuration;
import org.openmobster.cloudConnector.api.SecurityConfig;

/**
 * @author openmobster@gmail.com
 */
public class Launch 
{
	private static Logger log = Logger.getLogger(Launch.class);
	
	private final ClassWorld classWorld;
	
	public Launch(ClassWorld classWorld)
	{
		this.classWorld = classWorld;
	}
	
	public static void main(final String[] args, final ClassWorld world) throws Exception 
	{
        Launch main = new Launch(world);
        main.boot(args);
    }

    public static void main(final String[] args) throws Exception 
    {
        main(args, new ClassWorld("gshell", Thread.currentThread().getContextClassLoader()));
    }
    
    public void boot(final String[] args)
    {
    	//Run the shell
    	try
		{
    		//Bootstrap the Console Container
        	ServiceManager.bootstrap();
        	Configuration configuration = Configuration.getInstance();
    		configuration.setSecurityConfig((SecurityConfig)ServiceManager.locate("/cloudConnector/securityConfig"));
    		
			ConsoleShell.getInstance().getShell().run(new String[0]);
		}
		catch(Exception e)
		{
			log.error("OpenMobster Management Console failed to bootup.....");
			throw new RuntimeException(e);
		}
    }
    //-------------------------------------------------------------------------------------------------------------
}
