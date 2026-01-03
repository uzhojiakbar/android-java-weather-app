#!/usr/bin/env bash
set -euo pipefail

# Debug APK yasash
./gradlew assembleDebug

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

if [[ -f "$APK_PATH" ]]; then
  echo "APK tayyor: $APK_PATH"
else
  echo "APK topilmadi. Qurilish jarayonini loglardan tekshiring." >&2
  exit 1
fi
