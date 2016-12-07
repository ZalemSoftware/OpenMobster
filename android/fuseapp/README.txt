									*************************************************************
									* OpenMobster - Mobile Cloud Platform (version 2.2-SNAPSHOT)*
									*************************************************************

********************									
Project Layout:    *
********************									

Each generated project has the following 4 maven modules:

* app-android - Contains the App for the Android OS

* app-rimos - Contains the App for the Blackberry OS - version 4.3.0 and higher

* cloud - Contains the "OpenMobster Cloud Server" based artifacts which will be deployed on the server side

* moblet - Represents a "OpenMobster Moblet" which combines both the device side and server side artifacts into one single
artifact. The moblet is deployed as a simple jar file into the "OpenMobster Cloud Server". When the moblet is deployed into the Cloud
server it is registered with the built-in App store. Once registered with the App Store, this moblet can be easily downloaded, installed, and
managed on the actual device via the "App Store" functionality under the "Cloud Manager" app 


****************************									
Helpful Development Tips:  *
****************************

Build All the artifacts:
------------------------------
mvn install

This command builds all the artifacts. 


**********************************************									
Developer Productivity Improvement:          *
**********************************************
Android Development:

For Android, the development mode Cloud Manager app is installed on the simulator.

This Development Mode Cloud Manager app improves developer productivity by automating the manual provisioning/security 
processes by automatically provisioning a Cloud account under the name: "blah2@gmail.com".

Note: This is a strict development stage only optimization and should not be used in a real world setting.

To hot deploy your Android App and Cloud Manager app, use the following command:
cd app-android
mvn -Phot-deploy install

Once the Cloud Manager app is deployed, only the Android App needs to be deployed as development progresses.

To hot deploy just your Android App, use the following command:
cd app-android
mvn -Papp-hot-deploy install



BlackBerry Development:

To improve development productivity "mvn package or mvn install" command installs the required "OpenMobster MobileCloud" 
into the specified Blackeberry Simulator. 

The location of the simulator is specified in the "RIM_JDE_HOME" environment variable.


**********************************************									
Standalone "Development Mode" Cloud Server:  *
**********************************************

On the Cloud-side of things, there is a fully functional Standalone "Development Mode" Cloud Server provided 
that you can run right from inside your Maven environment.

Command to run the standalone "Development Mode" Cloud Server:
---------------------------------------------------------------
mvn -PrunCloud integration-test


Command to run the standalone "Development Mode" Cloud Server in *debug mode*:
-------------------------------------------------------------------------------
mvn -PdebugCloud integration-test


************************************
JBoss AS Deployment				   *
************************************

Once the App and its corresponding Cloud artifacts are developed and tested end-to-end, you can aggregate all these artifacts into
a single moblet jar ready for deployment into a JBoss AS based Cloud Server.

This single artifact when deployed into the JBoss AS based Cloud Server performs all necessary registrations with the system
and is ready for deploying the app onto a real phone via the Internet.

Commands:

	* Generate an the app and deploy into the JBoss AS instance:
		cd moblet
		mvn -Pjboss-install install