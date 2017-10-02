#!/bin/bash

scriptdir=$(readlink -f "$(dirname "$0")")

rsync --progress $scriptdir/target/cms-webapp-1.0-SNAPSHOT.war web@gruust.stream:wildfly/standalone/deployments/ROOT.war
