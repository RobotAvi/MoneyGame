#!/usr/bin/env bash
set -e

RUN_ID="$1"
if [ -z "$RUN_ID" ]; then
  echo "Usage: $0 <run_id>" >&2
  exit 1
fi

echo "==== Ожидание завершения workflow run $RUN_ID ====" | tee wait_run.log
gh run watch "$RUN_ID" | tee -a wait_run.log