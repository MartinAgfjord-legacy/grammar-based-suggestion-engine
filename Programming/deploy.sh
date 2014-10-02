#!/usr/bin/env bash
set -e

cd /vagrant/bundle/solr-mvn/
mvn tomcat7:deploy


cd /vagrant/bundle/
mvn install:install-file -Dfile=org.grammaticalframework.pgf.jar
                           -DgroupId=org.grammaticalframework
                           -DartifactId=pgf -Dversion=1.0 -Dpackaging=jar


cd /vagrant/bundle/nlparser/
mvn tomcat7:deploy
