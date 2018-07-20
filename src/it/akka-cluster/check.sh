#!/usr/bin/env bash

test_name="akka-cluster"
source ../../../bin/test-common.sh

if [ -f $dockerfile ]; then
    check $dockerfile 'com.lightbend.rp.app-name="akka-cluster"'
    check $dockerfile 'com.lightbend.rp.app-type="akka"'
    check $dockerfile 'com.lightbend.rp.modules.common.enabled="true"'
else
    echo "File $dockerfile not found"
    exit 1
fi