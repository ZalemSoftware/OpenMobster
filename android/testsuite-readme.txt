****************************************
TestSuites *Requiring* an CloudServer  *
****************************************

Test Synchronization Module

This TestModule combines the moblet-runtime and mobileCloud-runtime into a single App

* mobileCloud/android/2_0/test-suite/test-sync-port: 
mvn -Phot-deploy install


* Server Needed: device-agent-simulator : 
mvn -PrunTestCloud test

Special Note: When running this testsuite on the device, make sure there are no other testsuites installed
Also, make sure the DevCloud/CloudManager App are also not installed. This testsuite bundles everything into its app.

---------------------------------------------------------------------------------------------------------
Test API (Full Integration Test)

* mobileCloud/android/2_0/test-suite/test-api-port: 
mvn -Phot-deploy install


* Server Needed: device-agent-simulator : 
mvn -PrunTestCloud test

------------------------------------------------------------------------------------------------------------

****************************************
TestSuites Not Requiring an CloudServer*
****************************************

Test MobileObject Module

* mobileCloud/android/2_0/test-suite/test-mobileObject-port: 
mvn -Phot-deploy install

* Server *Not* Needed

---------------------------------------------------------------------------------------------------------
Test Common Module

* mobileCloud/android/2_0/test-suite/test-common-port: 
mvn -Phot-deploy install

* Server *Not* Needed

---------------------------------------------------------------------------------------------------------
Test Bus Module

* mobileCloud/android/2_0/test-suite/test-bus-port: 
mvn -Phot-deploy install


* Server *Not* Needed