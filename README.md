# BitClock 🕒

BitClock is a feature-rich, high-performance native Android alarm and time management application. Built with a focus on reliability and modern architectural patterns, it demonstrates advanced Android development concepts including Dependency Injection, reactive persistence, and optimized background processing.

## 🚀 Key Features

- **Reliable Alarms**: Precise scheduling using `AlarmManager` with specialized handling for Android 12+ exact alarm permissions.
- **Persistent Storage**: Robust data management with **Room Database** for alarms and timers.
- **Smart Ringing**: Foreground service with full-screen intent support, ensuring alarms trigger even when the device is locked.
- **Time Management Suite**: Includes a Stopwatch with lap tracking, a customizable Timer, and a World Clock.
- **Material 3 UI**: Clean, adaptive interface following the latest Material Design guidelines.

## 🛠 Tech Stack & Architecture

- **Language**: Java 17
- **Architecture**: MVVM (Model-View-ViewModel) with Repository Pattern.
- **Dependency Injection**: **Hilt** for clean, decoupled code and easier testing.
- **Local Persistence**: **Room** (SQLite) for structured, reactive data storage.
- **UI Components**: ViewBinding, Navigation Component, ConstraintLayout, and Material 3.
- **Optimization**: Custom `AppStateManager` cache layer to minimize disk I/O and CPU overhead.
- **Background Work**: BroadcastReceivers and Foreground Services for critical alarm reliability.

## 🏗 Technical Highlights

- **Reliability First**: Implements `setAlarmClock` API to ensure alarms are prioritized by the system even during Doze mode.
- **Modern DI**: Full Hilt integration from Application class to ViewModels and non-Hilt components (using EntryPoints).
- **Reactive UI**: Leveraging `LiveData` for real-time UI updates when the database state changes.
- **Adaptive Design**: Designed to be responsive across different screen sizes and Android versions (minSdk 26).

## 📥 Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/BitClock.git
   ```
2. Open the project in **Android Studio (Ladybug or newer)**.
3. Build the project and run it on an emulator or physical device (Android 8.0+).

## 📈 Future Roadmap

- [ ] Implementation of a "Sleep Tracking" module.
- [ ] Integration with Wear OS for remote alarm control.
- [ ] Migration to Kotlin and Jetpack Compose.

---
*Developed as a showcase of native Android engineering excellence.*
