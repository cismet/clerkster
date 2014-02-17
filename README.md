#clerkster

Signing jar service for customer signed configuration files

Clerkster is a web-service written with the Play2 Framework.
Its main functionality is to:
 
 * receive a Java Jar,
 * sign it with another certificate,
 * archive the Jar,
 * and send it back.

The prove that the Jar was sent by a customer, the web-server uses three security measures.

 * the Jar has to be sent from a IP-address, found in a white-list
 * a user-password authentication
 * the Jar has to be signed with a certain certificate
 
The IP white-list and the location of the certificate can be configured in the configuration-file ```conf/certificate.conf```.

The users can be found in the ```data/user-pwd-file```, whereas the file location can also be changed in the configurations.
Entries to this file can be done with [htdigest](http://httpd.apache.org/docs/2.2/programs/htdigest.html).

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




 
 


