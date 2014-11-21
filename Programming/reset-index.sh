#!/usr/bin/env bash
set -e

echo "[reset-index.sh] removing solr data folders"
rm -rf /vagrant/bundle/solr-instrucs/names/data
rm -rf /vagrant/bundle/solr-instrucs/trees/data
rm -rf /vagrant/bundle/solr-instrucs/relations/data

rm -rf /home/vagrant/indexes

# tomcat7 needs to own the index folders
# but chown:ing doesn't work in the shared folder (/vagrant)
# so we create data folders in /home/vagrant/ instead and 
# then we create symlinks to them
mkdir -p /home/vagrant/indexes/names
mkdir -p /home/vagrant/indexes/trees
mkdir -p /home/vagrant/indexes/relations
ln -sfn /home/vagrant/indexes/names /vagrant/bundle/solr-instrucs/names/data
ln -sfn /home/vagrant/indexes/trees /vagrant/bundle/solr-instrucs/trees/data
ln -sfn /home/vagrant/indexes/relations /vagrant/bundle/solr-instrucs/relations/data
sudo chown -R tomcat7: /home/vagrant/indexes

# chown -R tomcat7 /vagrant/bundle/solr-instrucs
sudo service tomcat7 restart
