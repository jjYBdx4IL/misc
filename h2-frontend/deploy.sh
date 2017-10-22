#!/bin/bash

scriptdir=$(readlink -f "$(dirname "$0")")

rsync --progress -c $scriptdir/target/h2-frontend-*-SNAPSHOT.war web@geegee.online:wildfly/standalone/deployments/h2-frontend.war
