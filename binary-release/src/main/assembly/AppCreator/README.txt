									*************************************************************
									* OpenMobster - Mobile Backend as a Service Platform
									*************************************************************
									
******************************									
Start Mobile App Development:*
******************************

On Windows:
-----------------------
appcreator.bat

On Linux and Mac:
-----------------------
appcreator.sh

This will generate a Maven-based skeleton for the Mobile App. Each generated project has the following maven modules:

* app-android - Contains the device side application code for the Android OS - version 3.0 and higher

* cloud - Contains the "OpenMobster Cloud Server" based artifacts which will be deployed on the server side

* moblet - Represents a "OpenMobster Moblet" which combines both the device side and server side artifacts into one single
artifact. The moblet is deployed as a simple jar file into the "OpenMobster Cloud Server". When the moblet is deployed into the Cloud
server it is registered with the built-in App store. Once registered with the App Store, this moblet can be easily downloaded, installed, and
managed on the actual device via the "App Store" functionality under the "Cloud Manager" app 