# BitClock Modernization Phase 2: Timer, Stopwatch, and UX Polish

This plan covers the implementation of the remaining modules in BitClock, focusing on a robust Foreground-Service-backed Timer, a feature-rich Stopwatch, and UX enhancements like "Swipe to Delete."

## User Review Required

> [!IMPORTANT]
> **Timer Foreground Service**: The Timer will run in a Foreground Service to ensure accuracy and persistence when the app is backgrounded. This requires a persistent notification.
> **Swipe to Delete**: This will be added to the Alarm list. I'll need to ensure the "Undo" functionality works correctly with the Alarm scheduling logic.

## Proposed Changes

### [Timer Module]

Implement a robust timer that survives app backgrounding and process death.

#### [NEW] [TimerService.kt](file:///C:/Users/conta/StudioProjects/BitClock/app/src/main/kotlin/com/salman/bitclock/services/TimerService.kt)
- Foreground service to manage the countdown logic.
- Updates a persistent notification with the remaining time.
- Handles `START`, `PAUSE`, `STOP`, and `RESET` actions via Intents.
- Syncs remaining time to Room at intervals and on service destruction.

#### [NEW] [TimerViewModel.kt](file:///C:/Users/conta/StudioProjects/BitClock/app/src/main/kotlin/com/salman/bitclock/ui/timer/TimerViewModel.kt)
- Manages UI state for the Timer screen.
- Observes timers from `TimerRepository`.
- Communicates with `TimerService` via Intents.

#### [MODIFY] [TimerScreen.kt](file:///C:/Users/conta/StudioProjects/BitClock/app/src/main/kotlin/com/salman/bitclock/ui/timer/TimerScreen.kt)
- Replace "Coming Soon" placeholder with a list of timers.
- Add a time picker or input for creating new timers.
- Add controls (Start, Pause, Reset, Delete) for each timer.

---

### [Stopwatch Module]

Implement a stopwatch with lap functionality.

#### [NEW] [StopwatchViewModel.kt](file:///C:/Users/conta/StudioProjects/BitClock/app/src/main/kotlin/com/salman/bitclock/ui/stopwatch/StopwatchViewModel.kt)
- Handles the stopwatch logic (Start, Lap, Reset).
- Maintains a list of `Lap` objects.

#### [MODIFY] [StopwatchScreen.kt](file:///C:/Users/conta/StudioProjects/BitClock/app/src/main/kotlin/com/salman/bitclock/ui/stopwatch/StopwatchScreen.kt)
- Replace "Coming Soon" placeholder with the Stopwatch UI.
- Displays elapsed time and a list of laps.

---

### [Clock Module]

Refine the digital clock and add World Clock support.

#### [MODIFY] [ClockScreen.kt](file:///C:/Users/conta/StudioProjects/BitClock/app/src/main/kotlin/com/salman/bitclock/ui/clock/ClockScreen.kt)
- Add a section for World Clocks.
- Allow users to add different time zones.

---

### [UX Polish]

Enhance the Alarm screen with modern interactions.

#### [MODIFY] [AlarmScreen.kt](file:///C:/Users/conta/StudioProjects/BitClock/app/src/main/kotlin/com/salman/bitclock/ui/alarm/AlarmScreen.kt)
- Implement `SwipeToDismiss` for alarm items.
- Show a `Snackbar` with an "Undo" action after deletion.

## Verification Plan

### Automated Tests
- Unit tests for `TimerViewModel` and `StopwatchViewModel` logic.
- Verification of `TimerRepository` CRUD operations.

### Manual Verification
- Deploy to an Android device/emulator.
- **Timer**: Start a timer, move app to background, verify notification updates, verify it rings when finished.
- **Stopwatch**: Start, record laps, verify lap list, reset.
- **Clock**: Verify digital clock and add a world clock.
- **UX**: Swipe to delete an alarm, tap "Undo", verify it reappears and is rescheduled.
