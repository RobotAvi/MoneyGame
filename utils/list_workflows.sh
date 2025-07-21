#!/usr/bin/env bash
set -e

echo "==== Список workflow ===="
gh workflow list | tee workflow_list.log