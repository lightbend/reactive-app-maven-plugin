#!/usr/bin/env bash

test_name="play-endpoints"
source ../../../bin/test-common.sh

if [ -f $dockerfile ]; then
    check $dockerfile 'com.lightbend.rp.app-name="play-endpoints"'
    check $dockerfile 'com.lightbend.rp.app-type="play"'
    check $dockerfile 'com.lightbend.rp.modules.common.enabled="true"'
    check $dockerfile 'com.lightbend.rp.modules.play-http-binding.enabled="true"'

    check $dockerfile 'com.lightbend.rp.endpoints.0.protocol="http"'
    check $dockerfile 'com.lightbend.rp.endpoints.0.ingress.0.type="http"'
    check $dockerfile 'com.lightbend.rp.endpoints.0.ingress.0.paths.0="/"'
    check $dockerfile 'com.lightbend.rp.endpoints.0.ingress.0.ingress-ports.0="9000"'
else
    echo "File $dockerfile not found"
    exit 1
fi