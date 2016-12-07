									*************************************************************
									* OpenMobster - Mobile Backend as a Service Platform
									*************************************************************
									
									
***********************									
Component Description:*
***********************
src - The source code for the PhoneGap Plugins and Sample Apps

bin - The binary artifacts for integrating OpenMobster service with PhoneGap App

Samples - Code samples. Includes both the PhoneGap App and the Cloud runtime to run the Apps end-to-end.



*************************
Running the Samples     *
*************************

JQuery/PhoneGap/OpenMobster based Offline App
----------------------------------------------

Step 1: Compile the 'src/mobilecloudlib' project and link the resulting static library 'libmobilecloudlib.a' to the 'PhoneGapSyncApp' project.
Make sure everything compiles before running the App

Step 2: Run the Cloud Server
Go to plugin-jquery-cloud directory
Run the Cloud Server: mvn -PrunCloud integration-test

Step 3: Run the App. The App will start with the Device Activation screen. Upon successful activation, the App will start