									*************************************************************
									* OpenMobster - Mobile Backend as a Service Platform
									*************************************************************
									
									
***********************									
Component Description:*
***********************
cloudServer - Contains the Cloud Server binaries. Supports hsqldb and mysql5

console - Contains the GWT/SmartGWT based "Management Console"

Android - Contains the binaries to be installed on the Android Platform.

iPhone - Contains the XCode projects used to compile and create a Sample App. 'mobilecloudlib' is the static library implementing the OpenMobster infrastructure. 
'SampleApp' is the Sample App.

AppCreator - A Maven-based Mobile App Development Tool along with some sample apps

Samples - Contains Sample Apps to learn how to use the OpenMobster Platform

docs - App Developer Guide and API documentation

src - Source Code of the entire project 


*************************
Quick Start:            *
*************************
Each Component has detailed Installation instructions in README.txt files. Perform the component installations in the following order:

Step 1:
----------

Install and Run the "cloudServer" instance on the JBoss AS 5.1.0.GA server. Details are in: "cloudServer/README.txt"


Step 2:
------------

Install the CRUD Sample App on your Android device or simulator. Go to "Samples" directory and type in

adb install -r crud.apk


Step 3:
-------------

Install the CRUD Sample App on another device using the Step2 instructions. This will help you to see multi-device sync and replication
in action.


Step 4:
-----------

Run the Sample App. At startup you will be asked to activate the App with the Cloud Server. Provide the necessary information 
and activate the App with the Cloud Server.


Step 5:
-----------

Welcome to the OpenMobster Community. Your are officially a "Mobster" ;)
