#!/usr/bin/env bash
set -euo pipefail
MIN=${1:-15}
shift || true
if command -v timeout >/dev/null 2>&1; then
  timeout -k 2m "${MIN}m" "$@"
else
  "$@"
fi
