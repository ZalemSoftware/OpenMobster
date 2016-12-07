									*************************************************************
									* OpenMobster - Mobile Backend as a Service Platform
									*************************************************************
									
									
									
Installation Steps:
----------------------


Step 1:
-------

Download and install JBoss-5.1.0.GA from here: http://www.jboss.org/jbossas/downloads/


Step 2:
--------

Copy 'openmobster' to the JBoss AS 'server' directory. openmobster is a pre-configured/optimized instance for the OpenMobster Cloud Server


Step 3:
--------

In the case of using MySQL5 as the database, modify openmobster-ds.xml according to your own MySql5 instance

Step 4:
--------

Start the JBoss AS instance with the OpenMobster binary installed using: run -c openmobster -b "a real IP address"

Note: A real IP address like "192.168.0.1" or something like that is needed for the mobile device to be able to connect to
the server. Even in the case of a simulation environment, the device's browser does not connect to a loopback address like
"localhost" or "127.0.0.1"

Step 5:
--------

Verify the Cloud Server installation by typing in: http://<cloudserver ip>:<port>/o