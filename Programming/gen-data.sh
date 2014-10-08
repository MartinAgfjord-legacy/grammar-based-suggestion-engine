#!/usr/bin/env bash

cd /vagrant/bundle/mock-data/
mvn compile
export MAVEN_OPTS='-Djava.library.path=/usr/local/lib -Xmx1500m'
export PATH=/home/vagrant/.cabal/bin:$PATH
mvn exec:java -Dexec.mainClass="org.agfjord.graph.Main"
