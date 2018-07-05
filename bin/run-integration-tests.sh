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
    mvn clean install

    echo "Generating K8s resources for $docker_image and applying with kubectl"
    rp generate-kubernetes-resources --generate-all "$docker_image" | kubectl apply -f - \
      || die "Failed to generate & apply k8s resources for $docker_image"
  )
}

# prerequisite: minikube start & eval $(minikube docker-env)
mvn install
it_test src/it/hello           hello:1.0-SNAPSHOT
it_test src/it/akka-quickstart akka-quickstart:1.0
#it_test src/it/play-endpoints  play-endpoints:1.0
#it_test src/it/lagom-endpoints lagom-endpoints:1.0
it_test src/it/akka-cluster    akka-cluster:1.0
