#!/usr/bin/env bash
set -e

RUN_ID="$1"
if [ -z "$RUN_ID" ]; then
  echo "Usage: $0 <run_id>" >&2
  exit 1
fi

echo "==== Логи неудачных jobs для workflow run $RUN_ID ====" | tee failed_logs.log
gh run view "$RUN_ID" --log-failed | tee -a failed_logs.log