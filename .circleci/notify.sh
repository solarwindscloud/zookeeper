#!/usr/bin/env bash

set -ex

source ci-scripts/bin/helpers.sh

SLACK_USER=java-commons send_to_slack "$1"
