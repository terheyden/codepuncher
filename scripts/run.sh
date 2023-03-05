#!/usr/bin/env bash

java -jar ../target/templates.jar -s template.txt -o output.txt -v vars.yaml

echo
cat output.txt
echo

