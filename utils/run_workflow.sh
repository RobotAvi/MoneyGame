#!/usr/bin/env bash
set -e

echo "==== Запуск workflow stable-build.yml ====" | tee workflow_run.log
gh workflow run stable-build.yml | tee -a workflow_run.log