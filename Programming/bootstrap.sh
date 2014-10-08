#!/usr/bin/env bash

set -e

# apt-get update
# Needed for adding repositories
# apt-get install -y python-software-properties
# add-apt-repository ppa:webupd8team/java
# add-apt-repository ppa:eclipse-team/ppa
apt-get update

apt-get install -y gcc autoconf libtool build-essential g++
apt-get install -y openjdk-7-jdk xvfb # eclipse-cdt
apt-get install -y tomcat7 tomcat7-admin maven

update-java-alternatives -s java-1.7.0-openjdk-i386
# apt-get install -y eclipse-cdt oracle-java8-installer


echo "[bootstrap.sh] Building and installing GF C runtime"
cd /vagrant/GF/src/runtime/c/
autoreconf -i
./configure
make
make install
# Workaround for Makefile above not copying headers properly.
cp /vagrant/GF/src/runtime/c/pgf/*.h /usr/local/include/pgf/

export LD_LIBRARY_PATH=/usr/local/lib:$LD_LIBRARY_PATH

# TODO: Might need to add a this flag to eclipse for the below headless build 
# (stated in report but haven't figured out what it would do yet):
# -fPIC

echo "[bootstrap.sh] Downloading Eclipse (for headless build of GF java runtime)"
mkdir -p /vagrant/exec
cd /vagrant/exec
wget --output-document=eclipse-cpp-luna-SR1-linux-gtk.tar.gz 'http://ftp-stud.fht-esslingen.de/pub/Mirrors/eclipse/technology/epp/downloads/release/luna/SR1/eclipse-cpp-luna-SR1-linux-gtk.tar.gz'
tar xvfz eclipse-cpp-luna-SR1-linux-gtk.tar.gz 

echo "[bootstrap.sh] Building GF java runtime"
cd /vagrant/GF/src/runtime/java/
/vagrant/exec/eclipse/eclipse -nosplash \
    --launcher.suppressErrors \
    -application org.eclipse.cdt.managedbuilder.core.headlessbuild \
    -import ./ \
    -I /usr/lib/jvm/java-7-openjdk-i386/include/ \
    -I /usr/lib/jvm/java-7-openjdk-i386/include/linux \
    -cleanBuild .*/.*posix.*

mv /vagrant/GF/src/runtime/java/Release\ \(posix\)/libjpgf.so /usr/local/lib

echo "[bootstrap.sh] Installing settings files from vagrant-patches for tomcat and maven"
cp /vagrant/vagrant-patches/tomcat-users.xml /etc/tomcat7/
cp /vagrant/vagrant-patches/setenv.sh /usr/share/tomcat7/bin/
cp /vagrant/vagrant-patches/settings.xml /etc/maven/
cp /vagrant/vagrant-patches/.profile /home/vagrant

echo "[bootstrap.sh] Installing Cabal and GF"
apt-get install -y haskell-platform libncurses5-dev
cabal update
cabal install gf

echo "[bootstrap.sh] Making utility scripts executable"
chmod +x /vagrant/deploy.sh
chmod +x /vagrant/gen-data.sh
chmod +x /vagrant/reset-index.sh


