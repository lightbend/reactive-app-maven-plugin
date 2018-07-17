#!/usr/bin/env bash

set -o pipefail

die() { echo "Aborting: $*"; exit 1; }

# prerequisite: brew install xmlstarlet

# TODO: Handle potential xmlstarlet failure
project_version=$(xmlstarlet sel -N pom=http://maven.apache.org/POM/4.0.0 -t -m "/pom:project/pom:version" -v '.' pom.xml)

declare -r g='com.lightbend.rp'
declare -r a='reactive-app-maven-plugin'

find src/it -name 'pom.xml' -exec \
  xmlstarlet ed -S --inplace -N pom=http://maven.apache.org/POM/4.0.0 \
  --update "/pom:project/pom:build/pom:plugins/pom:plugin[.//pom:groupId='$g' and .//pom:artifactId='$a']/pom:version" \
  -v "$project_version" {} + || die "Failed to sync integration tests plugin version"
