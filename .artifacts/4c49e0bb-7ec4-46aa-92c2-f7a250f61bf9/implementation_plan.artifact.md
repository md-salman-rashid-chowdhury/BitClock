# Implementation Plan - Reliability & Stability Upgrade

This plan focuses on fixing potential crashes, improving alarm reliability on modern Android versions, and ensuring the Timer functionality behaves predictably.

## User Review Required

> [!IMPORTANT]
> - **Runtime Permissions**: Users on Android 13+ will now see a notification permission prompt on app launch. This is essential for alarms to show up.
> - **Exact Alarm Permission**: If the "Exact Alarm" permission is revoked in system settings, users will be redirected to the settings page when trying to save an alarm.

## Proposed Changes

### [Permissions & UX]
Ensure the app has the necessary rights to trigger alarms and show notifications.

#### [MODIFY] [MainActivity.java](file:///C:/Users/conta/StudioProjects/BitClock/app/src/main/java/com/salman/bitclock/MainActivity.java)
- Implement `POST_NOTIFICATIONS` runtime permission request for Android 13+.

#### [MODIFY] [AlarmDetailActivity.java](file:///C:/Users/conta/StudioProjects/BitClock/app/src/main/java/com/salman/bitclock/ui/alarm/AlarmDetailActivity.java)
- Add a check for `alarmScheduler.canScheduleExactAlarms()`.
- If permission is missing, show a dialog explaining why it's needed and redirect to system settings.

### [Background Services]
Improve the robustness of the alarm ringing logic.

#### [MODIFY] [AlarmRingingService.java](file:///C:/Users/conta/StudioProjects/BitClock/app/src/main/java/com/salman/bitclock/services/AlarmRingingService.java)
- Add state checks to `MediaPlayer` to prevent `IllegalStateException`.
- Ensure `cleanup()` safely handles the `PREPARING` state.
- Add a fallback to play the default system alarm sound if the user-selected URI fails.

### [Data Models & Consistency]
Standardize the data layer.

#### [MODIFY] [Timer.java](file:///C:/Users/conta/StudioProjects/BitClock/app/src/main/java/com/salman/bitclock/data/models/Timer.java)
- Encapsulate fields (private + getters/setters).
- Add `@ColumnInfo` annotations for consistency with `Alarm.java`.

#### [MODIFY] [TimerAdapter.java](file:///C:/Users/conta/StudioProjects/BitClock/app/src/main/java/com/salman/bitclock/ui/timer/TimerAdapter.java)
- Update to use getters/setters.

## Verification Plan

### Automated Tests
- Run `app:assembleDebug` to ensure no syntax errors.

### Manual Verification
1. **Notifications**: Deploy on an Android 13+ device/emulator and verify the notification prompt appears.
2. **Exact Alarms**: Revoke "Alarms & Reminders" permission for BitClock in system settings, then try to save an alarm. Verify the redirect works.
3. **Alarm Ringing**: Set an alarm for 1 minute in the future. Lock the screen. Verify the activity shows up on top of the lock screen and sound plays correctly.
4. **Timer**: Start a timer, switch to another app, and return after 10 seconds. Verify the countdown didn't lose time (Note: Full service-based backgrounding for timers might be a future task, but basic model consistency will be fixed now).
