#!/usr/bin/env bash

test_name="hello"
source ../../../bin/test-common.sh

if [ -f $dockerfile ]; then
    check $dockerfile 'com.lightbend.rp.app-name="hello"'
    check $dockerfile 'com.lightbend.rp.app-type="basic"'
    check $dockerfile 'com.lightbend.rp.modules.common.enabled="true"'
    check $dockerfile 'com.lightbend.rp.applications.0.name="default"'
    check $dockerfile 'com.lightbend.rp.applications.0.arguments.0="deployments/run-java.sh"'
    check $dockerfile 'com.lightbend.rp.cpu="1.0"'
else
    echo "File $dockerfile not found"
    exit 1
fi