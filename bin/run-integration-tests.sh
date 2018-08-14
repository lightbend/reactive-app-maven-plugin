#!/usr/bin/env bash

# Usage
# `bin/run-integration-tests.sh`          for dry run
# `bin/run-integration-tests.sh --deploy` to run it on minikube

set -o pipefail

dry_run=true;

case "$1" in
  -x|--deploy) dry_run=false; echo "deploy mode!";;
  *) ;;
esac

die() { echo "Aborting: $*"; exit 1; }

mvn() {
  echo "Running 'mvn $*' in ${PWD##*/}"
  command mvn "$@" || die "Failed to run 'mvn $*' in ${PWD##*/}"
}

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

# WORKAROUND to:
#     /usr/bin/rp: line 23: /usr/share/reactive-cli/bin/rp: No such file or directory
if [ "$TRAVIS" = true ]; then
  RP="$DIR/rp-from-source.sh"
else
  RP=rp
fi

it_test() {
  dir="$1"
  docker_image="$2"

  (
    cd "$dir" || die "Failed to cd into $dir"

    if ! (($dry_run)); then
      echo "Resetting minikube"
      kubectl get services --no-headers | grep -v "^kube.*" | awk '{print $1}' | xargs kubectl delete service
      kubectl delete ingress --all
      kubectl delete deployment --all

      # minikube delete
      # minikube start
      # eval $(minikube docker-env)
    fi

    mvn -DskipTests=true -Dmaven.javadoc.skip=true -Dgpg.skip=true clean install

    echo "Generating K8s resources for $docker_image and applying with kubectl"
    file=$(mktemp -t k8s-resources.XXXXXX)
    "$RP" generate-kubernetes-resources --generate-all --registry-use-local "$docker_image" > "$file" \
      || die "Failed to generate k8s resources for $docker_image"

    if ($dry_run); then
      kubectl apply --validate --dry-run -f "$file" \
        || die "Failed to apply k8s resources for $docker_image"
    else
      kubectl apply --validate -f "$file" \
        || die "Failed to apply k8s resources for $docker_image"

      while ! (kubectl get pods --no-headers | grep "\bRunning\b"); do
        echo "[info] waiting..."
        sleep 1
      done
      echo "[info] deployed"
      sleep 5
    fi

    if [ -f "check.sh" ]; then
      echo "Running check.sh script"
      if ./check.sh; then
        echo "Dockerfile check successful"
      else
        die "Dockerfile check failed"
      fi
    fi

    if [ -f "test.sh" ]; then
      echo "Running test.sh script"
      if ./test.sh; then
        echo "Integration test successful"
      else
        die "Integration test failed"
      fi
    fi
  )
}

# prerequisite: minikube start & eval $(minikube docker-env)
mvn -DskipTests=true -Dmaven.javadoc.skip=true -Dgpg.skip=true install
bin/sync-integration-tests-plugin-version.sh || die "Failed to sync integration tests plugin version"
# it_test src/it/hello           hello:1.0
it_test src/it/akka-quickstart akka-quickstart:1.0
it_test src/it/play-endpoints  play-endpoints:1.0
it_test src/it/lagom-endpoints hello-impl:1.0
it_test src/it/akka-cluster    akka-cluster:1.0
