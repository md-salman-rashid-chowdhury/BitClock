# BitClock 🕒

BitClock is a high-performance, modern native Android alarm and time management application. This project has undergone a complete architectural evolution, transitioning from a legacy Java/XML foundation to a 100% **Kotlin** and **Jetpack Compose** powerhouse.

## 💎 Modernization Showcase

This application demonstrates the cutting edge of Android development (MAD - Modern Android Development):

- **Declarative UI**: Built entirely with **Jetpack Compose** and **Material Design 3**, eliminating XML layouts for a fluid, state-driven user experience.
- **Reactive Architecture**: Implements **MVVM** with **Kotlin Flow** and **StateFlow**, ensuring the UI is always a perfect reflection of the underlying data.
- **Pure Kotlin**: 100% Kotlin codebase, utilizing Coroutines for asynchronous operations and KSP for optimized code generation.
- **Premium Startup**: Modern **Splash Screen API** integration with high-quality vector adaptive icons.

## 🚀 Key Features

- **Reliable Alarms**: Precise scheduling using `AlarmManager.setAlarmClock()` with robust handling for Android 12+ exact alarm permissions.
- **Persistent Storage**: Reactive data management with **Room Database** generating pure Kotlin code.
- **Smart Ringing**: Robust foreground service with full-screen intent support, ensuring alarms trigger even when the device is locked.
- **Time Management Suite**: Features a real-time World Clock, customizable Timer, and a reactive Stopwatch.

## 🛠 Tech Stack

- **Language**: Kotlin 2.1.10
- **UI Framework**: Jetpack Compose (Material 3)
- **Dependency Injection**: Hilt
- **Persistence**: Room (KSP)
- **Concurrency**: Kotlin Coroutines & Flow
- **Architecture**: MVVM + Repository Pattern
- **Asset Management**: Modern Vector Adaptive Icons

## 🏗 Technical Highlights

- **Reliability First**: Prioritizes system triggers during Doze mode for guaranteed alarm delivery.
- **Performance**: Zero-java overhead and KSP-optimized processing for faster build times and runtime execution.
- **Modern DI**: Full Hilt integration extending to EntryPoints for non-composable components (Receivers/Services).

## 📥 Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/md-salman-rashid-chowdhury/BitClock.git
   ```
2. Open the project in **Android Studio (Ladybug or newer)**.
3. Build and run (minSdk 26).

---
*Rebuilt with excellence to showcase modern Android engineering.*
