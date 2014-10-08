
# Using Vagrant for running the demo application

Running with vagrant only has vagrant (and the GF git submodule) as prerequisite. All other dependencies should be installed automatically as the vagrant box is initialized using the provisioning script. 

Firstly, initialize the GF submodule from the git root:

   git submodule init
   git submodule update

Then, to setup the application stack, install vagrant and then run, in this folder:

   vagrant up

And then to deploy:

   vagrant ssh -- /vagrant/deploy.sh

To regenerate mock data:

   vagrant ssh -- /vagrant/gen-data.sh

# TODOs

- Generating mock data seems to take forever / does not terminate. Why?