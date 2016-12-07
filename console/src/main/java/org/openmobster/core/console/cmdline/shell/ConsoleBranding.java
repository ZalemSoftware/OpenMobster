/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.cmdline.shell;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

import jline.Terminal;
import org.codehaus.plexus.util.StringUtils;
import org.apache.geronimo.gshell.ansi.Buffer;
import org.apache.geronimo.gshell.ansi.Code;
import org.apache.geronimo.gshell.ansi.RenderWriter;
import org.apache.geronimo.gshell.branding.BrandingSupport;
import org.apache.geronimo.gshell.branding.VersionLoader;

/**
 * Provides Branding for the OpenMobster Management Console
 * 
 * @author openmobster@gmail.com
 */
public class ConsoleBranding extends BrandingSupport
{
	private static final String[] BANNER = {
		"***********************************************",
		"OpenMobster Management Console (version {0})",
		"***********************************************"
	};
	
	private VersionLoader versionLoader;

	public ConsoleBranding()
	{
	}

	public VersionLoader getVersionLoader()
	{
		return versionLoader;
	}

	public void setVersionLoader(VersionLoader versionLoader)
	{
		this.versionLoader = versionLoader;
	}

	public String getName()
	{
		return "gshell";
	}

	public String getDisplayName()
	{
		return "OpenMobster Management Console";
	}

	public String getProgramName()
	{
		return System.getProperty("program.name", "gsh");
	}

	public String getAbout()
	{
		StringWriter writer = new StringWriter();
		PrintWriter out = new RenderWriter(writer);

		out.println("For information about @|cyan OpenMobster|, visit:");
		out.println("    @|bold http://code.google.com/p/openmobster| ");
		out.flush();

		return writer.toString();
	}

	public String getVersion()
	{
		return versionLoader.getVersion();
	}

	public String getWelcomeBanner()
	{
		StringWriter writer = new StringWriter();
		PrintWriter out = new RenderWriter(writer);
		Buffer buff = new Buffer();

		for (String line : BANNER)
		{
			buff.attrib(MessageFormat.format(line, this.getVersion()), Code.CYAN);
			out.println(buff);
		}

		out.println();
		out.println(" @|bold OpenMobster| (" + this.getVersion() + ")");
		out.println();
		out.println("Type '@|bold help|' for more information.");

		// If we can't tell, or have something bogus then use a reasonable
		// default
		int width = Terminal.getTerminal().getTerminalWidth();
		if (width < 1)
		{
			width = 80;
		}

		out.print(StringUtils.repeat("-", width - 1));

		out.flush();

		return writer.toString();
	}
}