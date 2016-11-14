#!/bin/bash

# This script publishes automatically javadoc to gh-pages in required structure:
#   - script is activated only if repo belongs to 'openhub-framework'
#   - it is only for classic merge build, no by PRs and for master (release) branch
#   - actual generated javadoc is published in /docs/current/javadoc-api/ and also docs/<VERSION>/javadoc-api/

# only proceed script when it is openhub-framework (our repo due to fork), started not by pull request (PR), and is for master branch (stable release)
if [ "$TRAVIS_REPO_SLUG" == "OpenWiseSolutions/openhub-framework" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then

  # variable which resolve actual version of OpenHub framework
  MVN_VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)

  # enable error reporting to the console
  set -e

  echo -e "Publishing of javadoc-api for OpenHub framework in the version $MVN_VERSION...\n"

  cp -R target/site/apidocs $HOME/current

  cd $HOME
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "travis-ci"
  git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/OpenWiseSolutions/openhub-framework gh-pages > /dev/null

  cd gh-pages
  
  # cleanup old content if exists
  if [ -d ./docs/current/javadoc-api ]; then
    git rm -rf ./docs/current/javadoc-api
  fi
  # will create all directories if they don't exist
  mkdir -p ./docs/current/javadoc-api
  cp -Rf $HOME/current/. ./docs/current/javadoc-api
  
  # cleanup old content if exists
  if [ -d ./docs/$MVN_VERSION/javadoc-api ]; then
    git rm -rf ./docs/$MVN_VERSION/javadoc-api
  fi
  # will create all directories if they don't exist
  mkdir -p ./docs/$MVN_VERSION/javadoc-api
  cp -Rf $HOME/current/. ./docs/$MVN_VERSION/javadoc-api

  # git execution
  git add -f .
  git commit -m "Latest javadoc-api of OpenHub framework $MVN_VERSION on successful Travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
  git push -fq origin gh-pages > /dev/null 2>&1

  echo -e "Published Javadoc to gh-pages.\n"
  
fi