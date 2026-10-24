# Modern Alarm App (Native Android)

A sleek, modern native Android Alarm application built with **Jetpack Compose** and **Material 3**.

## ✨ Features
- **Modern Material 3 Design**: Dark mode with glowing neon cyan accents, rounded surfaces, and smooth transitions.
- **Interactive Time Picker**: Native Material 3 clock dial & time input.
- **Repeat Days Support**: Choose repeating days (Mon–Sun) or single-shot alarms.
- **Full-Screen Ringing Screen**: Shows when phone is locked or screen is off with pulsing glow animations, dismiss and snooze options.
- **Background Service**: Reliable ringtone playback and vibration using Android `AlarmManager.setAlarmClock()`.
- **Reboot Rescheduling**: `BootReceiver` automatically restores alarms when phone restarts.
- **Cloud-Built with GitHub Actions**: Built automatically on GitHub's cloud runners without stressing local device resources.

## 🚀 Cloud Build & Download APK
This project includes `.github/workflows/build-apk.yml`. Whenever code is pushed to GitHub:
1. GitHub Cloud Runner (16GB RAM) automatically builds the APK.
2. The compiled `ModernAlarm-Debug-APK` is available for direct download under GitHub's **Actions** tab.
