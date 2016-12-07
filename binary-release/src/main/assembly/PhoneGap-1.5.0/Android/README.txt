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

Step 1: Install the "JQueryOfflineApp.apk"
Install the App on the device: adb install -r JQueryOfflineApp.apk

Step 2: Run the Cloud Server
Go to plugin-jquery-cloud directory
Run the Cloud Server: mvn -PrunCloud integration-test
