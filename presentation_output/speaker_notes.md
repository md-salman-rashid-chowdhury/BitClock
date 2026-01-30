# BitClock Presentation Speaker Notes

### Slide 1: BitClock: Modern Android Time Management
Welcome to the presentation of BitClock. This project is a modern Android application designed to handle all your time management needs, from alarms to sleep tracking.

### Slide 2: Table of Contents
Here is our agenda. We will start with the objectives and motivation, move through the technical implementation details like architecture and frontend choices, and finish with our achievements.

### Slide 3: Introduction & Objectives
BitClock aims to replace standard clock apps with a more integrated experience. Our primary objective was to build a robust system using modern Android architecture components like MVVM and Room.

### Slide 4: Motivation & Contribution
We identified that users often need multiple apps for alarms and sleep tracking. BitClock solves this by combining them. It also serves as a clean architectural reference for other developers.

### Slide 5: Structured Approach: SDLC
We followed an Agile approach, allowing us to refine the UI and features iteratively. We started with the basic Alarm feature and progressively added the Stopwatch and Sleep modules.

### Slide 6: Project Timeline (Gantt)
Our timeline spanned approximately three months. The first month focused heavily on the frontend, while the second dealt with the complex backend logic of scheduling alarms reliably.

### Slide 7: Functional & Non-Functional Requirements
Critically, the app must wake the device up, requiring specific permissions like USE_FULL_SCREEN_INTENT. Non-functionally, we prioritized low battery consumption and high reliability.

### Slide 8: System Architecture
We used the recommended Google architecture. The ViewModel survives configuration changes, and the Repository abstracts the data source, making the app testable and modular.

### Slide 9: User Flow: Setting an Alarm
The primary flow involves the user creating an alarm. This data is saved to the DB and simultaneously registered with the Android System AlarmManager to ensure it rings even if the app is killed.

### Slide 10: Wireframes & Mockups
Here you see the evolution from a basic wireframe to the final high-fidelity mockup. We utilize standard Android XML layouts to ensure responsiveness across device sizes.

### Slide 11: Frontend: Android Native (Java)
We chose native Android development with Java. This provides the best performance and access to system-level APIs like AlarmManager, which cross-platform frameworks might struggle with.

### Slide 12: Backend: Local Persistence (Room)
Instead of a remote server, we use a local Room database. This ensures the app works perfectly offline. Room provides safety by verifying our SQL queries at compile time.

### Slide 13: Data Integration
The integration point is the Repository. It wraps the DAO and exposes LiveData. This reactive pattern means our UI never needs to manually 'refresh'; it just reacts to data changes.

### Slide 14: Conclusion & Future Work
In conclusion, BitClock is a solid foundation for personal time management. Future updates will focus on cloud backup and smarter wake-up algorithms using accelerometer data.
