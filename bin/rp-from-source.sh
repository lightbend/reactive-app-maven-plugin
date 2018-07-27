#!/usr/bin/env bash

set -o pipefail

die() { echo "Aborting: $*"; exit 1; }

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

dir="$DIR/reactive-cli"
[[ -d "$dir" ]] || git clone --depth 1 --branch v1.3.0 https://github.com/lightbend/reactive-cli "$dir"
cd "$dir" || die "Failed to cd into reactive-cli"

export BINTRAY_USER=dummy
export BINTRAY_PASS=dummy

sbt -Dsbt.log.noformat=true "cliNative/run $*" < /dev/null 2>&1 \
  | grep -v "\[success\]" \
  | grep -v "\[error\]" \
  | grep -v "\[info\]" \
  | grep -v "\[warn\]"
