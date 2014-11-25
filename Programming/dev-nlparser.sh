#!/usr/bin/env bash
cd /vagrant/bundle/nlparser

# debug opts
export MAVEN_OPTS='-Xmx1500m -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=1044'
#export MAVEN_OPTS='-Xmx1500m'
export LD_LIBRARY_PATH=/usr/local/lib:$LD_LIBRARY_PATH

mvn jetty:run