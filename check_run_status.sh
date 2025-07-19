#!/usr/bin/env bash
set -e

echo "==== Проверка статуса последнего запуска stable-build.yml ====" | tee run_status.log
gh run list --workflow=stable-build.yml --limit 1 | tee -a run_status.log