#!/bin/sh

export LD_LIBRARY_PATH=/usr/local/lib:$LD_LIBRARY_PATH
export JAVA_OPTS='-Dsolr.solr.home=/vagrant/bundle/solr-instrucs'
