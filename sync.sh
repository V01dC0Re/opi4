#!/bin/sh
mvn clean package && rsync -v --progress target/web3.war helios:~/wildfly-39.0.1.Final/standalone/deployments/ROOT.war
