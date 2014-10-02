
Running with vagrant has two prerequisities: vagrant and eclipse. Eclipse will be used for doing headless builds of one of the components.
So before working on this, dowload and put eclipse-cpp-luna-SR1-linux-gtk.tar.gz into the exec/ folder. And install Vagrant on the host system.

The following commands should then work on any host OS.


To set up and start vagrant vm:

   vagrant up

To deploy:

   vagrant ssh -- /vagrant/deploy.sh

To generate mock data:

   vagrant ssh -- /vagrant/gen-data.sh


(TODO find a way to automate download of eclipse in a sane and future proof way. Seriously, why is this hard??)