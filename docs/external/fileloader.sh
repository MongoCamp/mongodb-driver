#!/usr/bin/env bash

BASEDIR=$(dirname "$0")
FILEDIR="$BASEDIR/files"

rm -rf "$FILEDIR";

mkdir "$FILEDIR";

#curl --request GET -sL --url 'https://raw.githubusercontent.com/MongoCamp/mongocamp-sample-plugin/main/build.sbt' --output "$FILEDIR/sample-build.sbt"
#curl --request GET -sL --url 'https://raw.githubusercontent.com/MongoCamp/mongocamp-sample-plugin/main/src/main/scala/dev/mongocamp/sample/plugin/SampleRoutes.scala' --output "$FILEDIR/SampleRoutes.scala"