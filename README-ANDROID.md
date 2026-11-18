# Sophia native Android prototype
Target device: TECNO Camon 30S, Android 16.

This first native Android foundation replaces the browser-only PWA for assistant-role integration. It provides a launcher activity, a request to become the system default assistant (the user must approve), Android VoiceInteractionService and session-service declarations, text-to-speech testing, and a GitHub Actions debug APK build.

## Important limitations
This prototype does NOT yet implement an always-on "Hey Sophia" hotword engine, speech-to-text conversation, spoken approval workflow, or phone actions. Becoming the default assistant does not automatically grant third-party apps Google's privileged DSP hotword access. Actual wake-word availability depends on the device's Android implementation and supported VoiceInteraction/Hotword APIs. Android also applies microphone foreground-service and privacy restrictions.

Never claim the app is always listening until hotword detection is implemented and tested on the target phone. The microphone should only be active after the user explicitly enables the feature and Android grants permission.

## Build
GitHub Actions builds a debug APK on pushes to the Android project or via Actions > Build Sophia Android APK > Run workflow.
