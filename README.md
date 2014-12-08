[![Build Status](https://travis-ci.org/ctranoris/forgestore.svg?branch=master)](https://travis-ci.org/ctranoris/forgestore)
[![Documentation Status](https://readthedocs.org/projects/forgestore/badge/?version=latest)](https://readthedocs.org/projects/forgestore/?badge=latest)

FORGEStore Web Service
==========

FORGEStore is a RESTful marketplace service for a FORGEBox installation.
FORGEStore contains shared widgets, FORGEBox services, FIRE adapters and shared interactive courses.

A running instance can be found at http://www.forgestore.eu

Features
--------

- REST API based on CXF
- Manage artifacts through assets
- Role access through Apache Shiro
- Open JPA for persistence

Installation
------------

Current version needs a mysql database called fsdb already created:

	CREATE DATABASE IF NOT EXISTS fsdb DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
	GRANT ALL PRIVILEGES ON fsdb.* TO 'forgestoredbuser'@'localhost' IDENTIFIED BY 'forgestorerp@zz'

(remember to change these values for a production environment, also on [/src/main/resources/META-INF/persistence.xml] persistence.xml)

Download and build project by running:

    mvn clean -Pjetty.integration  verify


This will perform some unit and integration test and create a jar to be installed in a jetty web server.

For a quick smoke run:

	 mvn clean -Pjetty.integration jetty:run
	 
and will listen on port 13000
	 
There is also a cargo profile. To prepare a deployment description use:

	mvn -Pcargo.run cargo:configure
	mvn -Pcargo.run cargo:package
	
and get the jetty-packaged folder
	
	
Contribute
----------

- Issue Tracker: https://github.com/ctranoris/forgestore/issues
- Source Code: https://github.com/ctranoris/forgestore


Copyright
---------

The source code in this distribution is © Copyright 2014 University of Patras

Licenses
--------

The license for this software is [Apache 2 v2.1](./src/license/header.txt).

A complete list of licenses for this software and associated third party software 
can be found in the [license](./src/license) folder.

Contact
-------

For further information on collaboration, support or alternative licensing, please contact:

* Website: http://nam.ece.upatras.gr
* Email: tranoris@ece.upatras.gr

Building with Vagrant
-------

[Vagrant](http://vagrantup.com) along with a suitable virtual machine system (such as Oracle VirtualBox) can be used to simply build and deploy the FORGEStore
and all its dependencies.  This is particularly useful for development and testing.

If you are familiar with vagrant then just execute `vagrant up`.
