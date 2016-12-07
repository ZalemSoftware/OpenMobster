									*************************************************************
									* OpenMobster - Mobile Backend as a Service Platform
									*************************************************************
									
									
mobilecloudlib: Contains the XCode project for OpenMobster infrastructure. This is a static library to be linked with Apps wanting to use OpenMobster services.

Sample App: Contains the XCode project for an OpenMobster based Sample App.

showcase: Contains the Cloud side server to use with the Sample App.

docs: Doxygen based API documentation of mobilecloudlib
____________________________________________________________________________________________________________________________________________________________________

How to use the Sample App?
________________________________

Step 1: Open the 'mobilecloudlib' XCode project and Build it.

Step 2: Open the 'SampleApp' XCode project and Build it.

Step 3: Start the Sample App Cloud Server.
cd showcase/cloud
mvn -PrunCloud integration-test

Step 4: Build/Run the 'SampleApp' App. It should startup in a Simulator.

You will need to Activate your device with the Cloud before the App can be used. This is just a one time activation.