#!/usr/bin/env bash
set -euo pipefail

# Agar emulator allaqachon ishlayotgan bo'lsa foydalanamiz, bo'lmasa runpixel dan foydalanamiz
if adb get-state >/dev/null 2>&1; then
  echo "Emulator topildi, davom etamiz."
else
  if command -v runpixel >/dev/null 2>&1; then
    echo "runpixel bilan emulatorni ishga tushiryapman..."
    runpixel &
    EMU_PID=$!
    echo "Emulator yuklanishi uchun kutilyapti..."
    sleep 10
  else
    echo "runpixel komandasi topilmadi va emulator ham ishlamayapti. Iltimos SDK/emulatorni ishga tushiring." >&2
    exit 1
  fi
fi

# Build va qurilmaga o'rnatish
./gradlew installDebug

# Ilovani ishga tushirish
adb shell am start -n "com.example.farrux.weatherapp/.ui.MainActivity" >/dev/null 2>&1 || true

echo "Dastur emulatorga o'rnatildi va ishga tushirildi."

# Agar runpixel fon rejimida qoldirilgan bo'lsa, foydalanuvchi yopishi mumkin
if [[ -n "${EMU_PID:-}" ]]; then
  wait $EMU_PID || true
fi
