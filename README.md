#clerkster

Signing jar service for customer signed configuration files

Clerkster is a web-service written with the Play2 Framework.
Its main functionality is to:
 
 * receive a Java Jar,
 * sign it with another certificate,
 * archive the Jar,
 * and send it back.

Some security features such as HTTPS, Authentification can be realized with a Apache server, which runs in front of Play.

##Installation
The installation has two steps:

* local installation
* deployment

###Local Installation
To install clerkster locally, execute the follwoing steps:

* [Download](http://www.playframework.com/download) the Play Framework
* Extract the Framework to some folder
* Clone this repository to some other folder
* Switch to the newly created clerkster folder: ```cd <clerkster_folder>```
* to open the Play Console, execute ```<path_to_play>/play```
* in Play Console you can for example run clerkster with ```run```

###Deployment
There are different methods to [deploy](http://www.playframework.com/documentation/2.2.x/Production) a Play application.
One method is to create a standalone version of clerkster:

* open the Play Console
* execute ```dist```
* this will create a zip-file in ```<clerkster_folder>/target/universal/<name>```. <name> can be defined in ```build.sbt```.
* extract the zip-file to any folder
* change to that folder and give ```bin/clerservice``` execution rights
* this script can be run with different parameters

Some of these parameters are:

* -Dhttps.port=9443
* -Dhttp.port=disabled
* -Dhttp.address=192.168.100.24
* -Dconfig.file=../conf/application.conf
* -Dlogger.file=conf/prod-logger.xml




 
 


