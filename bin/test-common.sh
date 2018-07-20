#!/bin/env bash

dockerfile="target/docker/${test_name}/1.0/build/Dockerfile"

check() {
    command grep -i $2 $1 > /dev/null 2>&1
    if [[ ! $? -eq 0 ]]; then
        echo "Expected docker label $2 not found"
        exit 1
    fi 
}

