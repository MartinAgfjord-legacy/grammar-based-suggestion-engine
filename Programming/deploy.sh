#!/usr/bin/env bash
set -e

#source /vagrant/reset-index.sh

cd /vagrant/bundle/solr-mvn/
mvn tomcat7:deploy

# Why not part of provisioning?
cd /vagrant/bundle/
mvn install:install-file -Dfile=org.grammaticalframework.pgf.jar \
    -DgroupId=org.grammaticalframework \
    -DartifactId=pgf -Dversion=1.0 -Dpackaging=jar


cd /vagrant/bundle/nlparser/
mvn tomcat7:deploy
