#!/usr/bin/env bash
set -e

echo "==== Получение ID последнего запуска stable-build.yml ====" | tee last_run_id.log
gh run list --workflow=stable-build.yml --limit 1 | tee -a last_run_id.log