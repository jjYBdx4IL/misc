#!/bin/bash

scriptdir=$(readlink -f "$(dirname "$0")")

rsync --progress -c $scriptdir/target/solr-webapp-1.0-SNAPSHOT.war web@geegee.online:wildfly/standalone/deployments/solr-webapp.war
