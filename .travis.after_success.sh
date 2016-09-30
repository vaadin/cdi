#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" = "false" ] && [ "$TRAVIS_BRANCH" = "master" ] && [ "$TRAVIS_SECURE_ENV_VARS" = "true" ]
then
	mvn deploy --settings ../.travis-settings.xml -DrepositoryId=vaadin-snapshots -DaltDeploymentRepository=vaadin-snapshots::default::https://oss.sonatype.org/content/repositories/vaadin-snapshots/ -DskipTests=true -DskipITs -B
fi

