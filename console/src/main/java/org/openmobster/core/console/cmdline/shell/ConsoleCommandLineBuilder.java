/**
 * Copyright (c) {2003,2011} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.console.cmdline.shell;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.geronimo.gshell.CommandLine;
import org.apache.geronimo.gshell.CommandLineBuilder;
import org.apache.geronimo.gshell.ErrorNotification;
import org.apache.geronimo.gshell.ExecutingVisitor;
import org.apache.geronimo.gshell.command.CommandExecutor;
import org.apache.geronimo.gshell.parser.ASTCommandLine;
import org.apache.geronimo.gshell.parser.CommandLineParser;
import org.apache.geronimo.gshell.parser.ParseException;
import org.apache.geronimo.gshell.shell.Environment;

/**
 * A CommandLineBuilder that uses a single executor and environment, expecting
 * those to be proxies to some thread local instances.  Use setter injection to
 * avoid a circular dependency with the SpringCommandExecutor.
 */
public class ConsoleCommandLineBuilder implements CommandLineBuilder {

    private CommandLineParser parser = new CommandLineParser();
    private CommandExecutor executor;
    private Environment environment;

    public ConsoleCommandLineBuilder() {
    }

    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    private ASTCommandLine parse(final String input) throws ParseException {
         assert input != null;

         Reader reader = new StringReader(input);
         ASTCommandLine cl;
         try {
             cl = parser.parse(reader);
         }
         finally {
             try {
                 reader.close();
             } catch (IOException e) {
                 // Ignore
             }
         }

         return cl;
     }

     public CommandLine create(final String commandLine) throws ParseException {
         assert commandLine != null;

         if (commandLine.trim().length() == 0) {
             throw new IllegalArgumentException("Command line is empty");
         }

         try {
             final ExecutingVisitor visitor = new ExecutingVisitor(executor, environment);
             final ASTCommandLine root = parse(commandLine);

             return new CommandLine() {
                 public Object execute() throws Exception {
                     return root.jjtAccept(visitor, null);
                 }
             };
         }
         catch (Exception e) {
             throw new ErrorNotification(e);
         }
     }

}
