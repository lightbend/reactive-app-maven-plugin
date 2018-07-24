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

    mvn -DskipTests=true -Dmaven.javadoc.skip=true -Dgpg.skip=true clean install

    echo "Generating K8s resources for $docker_image and applying with kubectl"
    rp generate-kubernetes-resources --generate-all --registry-use-local "$docker_image" | kubectl apply --validate --dry-run -f - \
      || die "Failed to generate & apply k8s resources for $docker_image"

    if [ -f "check.sh" ]; then
      echo "Running check.sh script"
      if ./check.sh; then
        echo "Dockerfile check successful"
      else
        die "Dockerfile check failed"
      fi
    fi
  )
}

# prerequisite: minikube start & eval $(minikube docker-env)
mvn -DskipTests=true -Dmaven.javadoc.skip=true -Dgpg.skip=true install
bin/sync-integration-tests-plugin-version.sh || die "Failed to sync integration tests plugin version"
it_test src/it/hello           hello:1.0
it_test src/it/akka-quickstart akka-quickstart:1.0
it_test src/it/play-endpoints  play-endpoints:1.0
it_test src/it/lagom-endpoints hello-impl:1.0
it_test src/it/akka-cluster    akka-cluster:1.0
