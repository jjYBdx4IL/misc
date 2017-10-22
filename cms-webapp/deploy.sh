#!/bin/bash

scriptdir=$(readlink -f "$(dirname "$0")")

rsync --progress $scriptdir/target/cms-webapp-*-SNAPSHOT.war web@gruust.stream:wildfly/standalone/deployments/cms-webapp.war
