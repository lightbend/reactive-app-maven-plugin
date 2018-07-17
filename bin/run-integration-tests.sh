#!/usr/bin/env bash

set -o pipefail

die() { echo "Aborting: $*"; exit 1; }

mvn() {
  echo "Running 'mvn $*' in ${PWD##*/}"
  command mvn "$@" || die "Failed to run 'mvn $*' in ${PWD##*/}"
}

it_test() {
  dir="$1"
  docker_image="$2"

  (
    cd "$dir" || die "Failed to cd into $dir"

    local -r g='com.lightbend.rp'
    local -r a='reactive-app-maven-plugin'
    find . -name 'pom.xml' -exec \
      xmlstarlet ed -S --inplace -N pom=http://maven.apache.org/POM/4.0.0 \
      --update "/pom:project/pom:build/pom:plugins/pom:plugin[.//pom:groupId='$g' and .//pom:artifactId='$a']/pom:version" \
      -v "$project_version" {} +

    mvn clean install

    echo "Generating K8s resources for $docker_image and applying with kubectl"
    rp generate-kubernetes-resources --generate-all --registry-use-local "$docker_image" | kubectl apply --validate --dry-run -f - \
      || die "Failed to generate & apply k8s resources for $docker_image"
  )
}

# prerequisite: minikube start & eval $(minikube docker-env)
# prerequisite: brew install xmlstarlet
# TODO: Handle potential xmlstarlet failure
project_version=$(xmlstarlet sel -N pom=http://maven.apache.org/POM/4.0.0 -t -m "/pom:project/pom:version" -v '.' pom.xml)
mvn install
it_test src/it/hello           hello:1.0
it_test src/it/akka-quickstart akka-quickstart:1.0
it_test src/it/play-endpoints  play-endpoints:1.0
it_test src/it/lagom-endpoints hello-impl:1.0
it_test src/it/akka-cluster    akka-cluster:1.0
