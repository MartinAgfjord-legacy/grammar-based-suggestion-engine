#!/bin/sh

export LD_LIBRARY_PATH=/usr/local/lib:$LD_LIBRARY_PATH
# Debug opts (will wait for attaching debugger):
#export JAVA_OPTS='-Dsolr.solr.home=/vagrant/bundle/solr-instrucs -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=1044'

export JAVA_OPTS='-Dsolr.solr.home=/vagrant/bundle/solr-instrucs'
