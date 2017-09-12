#!/bin/bash

rsync --progress target/cms-webapp-1.0-SNAPSHOT.war web@gruust.stream:wildfly/standalone/deployments/ROOT.war
