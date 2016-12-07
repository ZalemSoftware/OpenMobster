									*************************************************************
									* OpenMobster - Mobile Backend as a Service Platform
									*************************************************************

********************									
Project Layout:    *
********************									

Each generated project has the following 2 modules:

* app-android - Contains an Eclipse based App for the Android OS (API Level 11 and higher)

* cloud - Contains the "OpenMobster Cloud Server" based artifacts which will be deployed on the server side


**********************************************									
Android Project Setup                        *
**********************************************
Step 1: Import the Android Project stored under "app-android" into your Eclipse workspace.
Make sure you select, Import > Existing Android Code into Workspace. Rest of the instructions should be self-explanatory

Step 2: "Run As" Android Application from Eclipse. This will compile and install the Android App on the connected device or simulator


**********************************************									
Cloud Project Setup                          *
**********************************************

On the Cloud-side of things, there is a fully functional Standalone "Development Mode" Cloud Server provided 
that you can run right inside your Maven environment.

The "Cloud" artifacts are provided in the "cloud" directory. Here are some maven commands you can execute from the commandline in the "cloud"
directory.

Command to build all the artifacts:
--------------------------------------------------------------
mvn clean install


Command to run the standalone "Development Mode" Cloud Server:
---------------------------------------------------------------
mvn -PrunCloud integration-test


Command to run the standalone "Development Mode" Cloud Server in *debug mode*:
-------------------------------------------------------------------------------
mvn -PdebugCloud integration-test


Eclipse Project Setup
------------------------------------------------------------------
Import the Cloud project stored under "cloud" into your Eclipse workspace.
Make sure you select, Import > Maven > Existing Maven Projects

In case, you do not have the Eclipse Maven Plugin installed, you can open the "cloud" project as a regular Java Project.
But, before you do that, you execute the following command from the commandline:
-----------------------------------------------------------------------------------
mvn eclipse:eclipse


************************************
JBoss AS Deployment				   *
************************************

Once your "cloud" jar file is tested end-to-end in the Maven based Cloud Server, you must deploy the jar file into a JBoss 5.1.0.GA App Server.
The deployment is quite simple. You just copy the "cloud" jar file from the "cloud/target" folder into the "JBOSS_HOME/server/openmobster/deploy" folder.