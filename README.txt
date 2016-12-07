Build Help
-----------------------------------------------------------------------------------------------
Full Build including testsuite:
mvn -Pbuild-all install


Full Build skip testsuite:
mvn -DskipTests -Pbuild-all install


Note:

Before building, you must follow the directions below to setup for environment correctly. Without this the build will fail.


Step 1: Install the Android SDK 

Step 2: Point an environment variable called ANDROID_HOME to the directory where the SDK is installed

These steps will make sure all the artifacts are fully built.


If you want to do a binary release of the project the best place to get step-by-step directions is:
http://code.google.com/p/openmobster/wiki/Building_OpenMobster_Ubuntu_Maven304

