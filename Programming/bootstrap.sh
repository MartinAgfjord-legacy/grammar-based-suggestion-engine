#!/usr/bin/env bash

set -e

# apt-get update
# Needed for adding repositories
# apt-get install -y python-software-properties
# add-apt-repository ppa:webupd8team/java
# add-apt-repository ppa:eclipse-team/ppa
apt-get update

apt-get install -y gcc autoconf libtool build-essential g++
apt-get install -y openjdk-7-jdk # xvfb # eclipse-cdt
apt-get install -y tomcat7 tomcat7-admin maven

update-java-alternatives -s java-1.7.0-openjdk-i386
# apt-get install -y eclipse-cdt oracle-java8-installer

:' TODO Seems that c stuff hasnt been installed properly!

vagrant@precise32:/vagrant/GF/src/runtime/java/Release (posix)$ ldd libjpgf.so
	linux-gate.so.1 =>  (0xb77b1000)
	libpgf.so.0 => not found
	libgu.so.0 => not found
	libc.so.6 => /lib/i386-linux-gnu/libc.so.6 (0xb75f9000)
	/lib/ld-linux.so.2 (0xb77b2000)
'

cd /vagrant/GF/src/runtime/c/
autoreconf -i
./configure
make
make install

# Workaround for Makefile above not copying headers properly.
cp /vagrant/GF/src/runtime/c/pgf/*.h /usr/local/include/pgf/

# Probably need to set this globaly somehow?
export LD_LIBRARY_PATH=/usr/local/lib:$LD_LIBRARY_PATH

# Might need to add a this flag to the below build 
# (stated in report but haven't figured out wjhat it would do yet):
# -fPIC

cd /vagrant/GF/src/runtime/java/
/vagrant/exec/eclipse/eclipse -nosplash \
    -application org.eclipse.cdt.managedbuilder.core.headlessbuild \
    -import ./ \
    -I /usr/lib/jvm/java-7-openjdk-i386/include/ \
    -I /usr/lib/jvm/java-7-openjdk-i386/include/linux \
    -cleanBuild all

mv /vagrant/GF/src/runtime/java/Release\ \(posix\)/libjpgf.so /usr/local/lib


# Add settings files for tomcat and maven
cp /vagrant/vagrant-patches/tomcat-users.xml /etc/tomcat7/
cp /vagrant/vagrant-patches/setenv.sh /usr/share/tomcat7/bin/
cp /vagrant/vagrant-patches/settings.xml /etc/maven/

sudo service tomcat7 restart

chmod +x /vagrant/deploy.sh
chmod +x /vagrant/gen-data.sh
