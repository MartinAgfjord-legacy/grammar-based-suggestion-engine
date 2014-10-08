
Running with vagrant has two prerequisities: vagrant and eclipse. Eclipse will be used for doing headless builds of one of the components.
So before working on this, dowload and put eclipse-cpp-luna-SR1-linux-gtk.tar.gz into the exec/ folder. And install Vagrant on the host system.

The following commands should then work on any host OS.


To set up and start vagrant vm:

   vagrant up

To deploy:

   vagrant ssh -- /vagrant/deploy.sh

To generate mock data:

   vagrant ssh -- /vagrant/gen-data.sh

# TODOs

- find a way to automate download of eclipse in a sane and future proof way. Seriously, why is this hard??

- DONE Seems that c stuff hasnt been installed properly!
Problem was insufficiently set lib path (below)

vagrant@precise32:/vagrant/GF/src/runtime/java/Release (posix)$ ldd libjpgf.so
	linux-gate.so.1 =>  (0xb77b1000)
	libpgf.so.0 => not found
	libgu.so.0 => not found
	libc.so.6 => /lib/i386-linux-gnu/libc.so.6 (0xb75f9000)
	/lib/ld-linux.so.2 (0xb77b2000)

