#!/bin/bash

# This script publishes automatically OpenHub artifacts to central maven repository
#   - script is activated only if repo belongs to 'openhub-framework'
#   - it is only for classic merge build to master or develop branch

if [ "$TRAVIS_REPO_SLUG" == "OpenWiseSolutions/openhub-framework" ] && [ $TRAVIS_PULL_REQUEST == "false" ] && [[ ("$TRAVIS_BRANCH" == "master") || ( "$TRAVIS_BRANCH" == "develop" ) ]]; then

  # decrypt GPG keys in memory
  openssl aes-256-cbc -pass pass:$GPG_PASSPHRASE -in .utility/pubring.gpg.enc -out .utility/pubring.gpg -d
  openssl aes-256-cbc -pass pass:$GPG_PASSPHRASE -in .utility/secring.gpg.enc -out .utility/secring.gpg -d

  # variable which resolve actual version of OpenHub framework
  MVN_VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)

  # enable error reporting to the console
  set -e
  
  echo -e "Publishing of artifacts (jars) for OpenHub framework in the version $MVN_VERSION...\n"
  
  if [ "$TRAVIS_BRANCH" == "master" ] ; 
    then
        mvn deploy -DperformRelease=true --settings .utility/settings.xml -DskipTests=true -B -P full-deploy
        echo -e "Published artifacts to Central maven repository.\n"
    else
        mvn deploy --settings .utility/settings.xml -DskipTests=true -B -P full-deploy
        echo -e "Published artifacts to Snapshot maven repository.\n"
  fi
  
fi