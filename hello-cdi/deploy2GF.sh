#!/bin/bash
mvn clean install
$GLASSFISH_HOME/bin/asadmin --port 4848 deploy --force ./target/hello-cdi.war

