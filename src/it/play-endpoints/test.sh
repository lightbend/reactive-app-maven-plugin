#!/usr/bin/env bash

curl -kL --fail "https://$(minikube ip)/" | grep "Hello.*"
