#!/bin/sh

# Description why -Djava.security.egd=file:/dev/./urandom is required on cloud.digitalocean.com
# - https://www.digitalocean.com/community/questions/ubuntu14-tomcat7-application-deployment-time
# - http://security.stackexchange.com/questions/14386/what-do-i-need-to-configure-to-make-sure-my-software-uses-dev-urandom

java -jar -Djava.security.egd=file:/dev/./urandom \
	../build/libs/smserver-0.0.1-SNAPSHOT.war
