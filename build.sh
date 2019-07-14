#!/usr/bin/env bash

set -eu

bazel build //...
cp bazel-bin/stringtemplate/*.md docs/
find docs/*.md -exec chmod u+rw {} +
