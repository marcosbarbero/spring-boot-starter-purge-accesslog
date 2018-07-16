#!/usr/bin/env bash

echo "Branch: $TRAVIS_BRANCH"
echo "Pull Request? $TRAVIS_PULL_REQUEST"
echo "Commit Message: $TRAVIS_COMMIT_MESSAGE"

if [ "$TRAVIS_BRANCH" = "master" ] && [ "$TRAVIS_PULL_REQUEST" = "false" ] && [[ "$TRAVIS_COMMIT_MESSAGE" == *"[ci deploy]"* ]]; then
  mvn --settings .ci/settings.xml clean deploy -DskipTests -N

  echo "New version released"
fi

echo "Build Finished"