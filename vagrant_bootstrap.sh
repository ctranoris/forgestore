#!/usr/bin/env bash


echo -e "\n--- Updating packages list ---\n"
apt-get -qq update

echo -e "\n--- Install base packages ---\n"
sudo apt-get -y install vim curl build-essential software-properties-common python-software-properties git maven subversion

echo "Done installing, putting in place bootstrap content.."

echo -e "\n--- Installing java ---\n"

sudo add-apt-repository -y ppa:webupd8team/java
sudo apt-get update

# automatic install of the Oracle JDK 7
echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections

sudo apt-get -y install oracle-java7-set-default

export JAVA_HOME="/usr/lib/jvm/java-7-oracle/jre"


echo -e "\n--- Installing mysql with a user ---\n"

# Variables
APPENV=local
DBHOST=localhost
DBNAME=bakerdb
DBUSER=bakerdbuser
DBPASSWD=b@k3rp@zz


echo -e "\n--- Install MySQL specific packages and settings ---\n"
echo "mysql-server mysql-server/root_password password $DBPASSWD" | debconf-set-selections
echo "mysql-server mysql-server/root_password_again password $DBPASSWD" | debconf-set-selections
apt-get -y install mysql-server-5.5

echo -e "\n--- Setting up our MySQL user and db ---\n"
mysql -uroot -p$DBPASSWD -e "CREATE DATABASE $DBNAME"
mysql -uroot -p$DBPASSWD -e "grant all privileges on $DBNAME.* to '$DBUSER'@'localhost' identified by '$DBPASSWD'"





