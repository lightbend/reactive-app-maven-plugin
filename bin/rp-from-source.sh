#!/usr/bin/env bash

set -o pipefail

die() { echo "Aborting: $*"; exit 1; }

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

dir="$DIR/reactive-cli"

[[ -d reactive-cli ]] || git clone --depth 1 --branch v1.3.0 https://github.com/lightbend/reactive-cli "$dir"
cd "$dir" || die "Failed to cd into reactive-cli"
sbt "cliNative/run $*"
