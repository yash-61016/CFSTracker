# CFS Monitor - IoT App for Chronic Fatigue Syndrome Monitoring (Archive Repo)
CFS Monitor is an innovative IoT app built using a clean architecture approach and optimized with the latest technologies. 
This optimized and well-documented codebase serves as an archive reference for the implementation.

## Clean Architecture
The app follows clean architecture principles with separate layers for domain logic, use cases, repositories, controllers, and UI.

- **Domain Layer** - Contains business logic entities like _User_, _SensorReading_, etc.
- **Use Case Layer** - Handles application logic use cases like _GetSensorReadingsUseCase_
- **Repository Layer** - Abstracts data sources. Eg: _UserRepository_, _SensorRepository_
- **Controller Layer** - Contains viewmodels that drive UI. Eg: _DashboardViewModel_
- **UI Layer** - Responsible purely for UI code. Uses Jetpack Compose.
  This separation of concerns makes the app robust, scalable, and testable.

## Optimized Architecture
Various optimizations are implemented:

- **Kotlin** - Used as the primary language for its conciseness, safety and performance.
- **Coroutines** - Handleasync operations and background tasks efficiently.
- **Flow** - Used for data streams and reactive UIs.
- **Hilt** - Fast and lightweight dependency injection framework.
- **Jetpack** - Modern architecture components like Room, ViewModel, Compose etc.
- **Firebase** - Used for real-time data sync, crash analytics, cloud functions etc..
- **Material Design 3** - Implements the latest Material theming capabilities.
- **Testing** - Unit tests and UI tests are implemented for robustness.
## Features
- **Vital Monitoring** - Heart rate and respiratory rate measured using mobile sensors.
- **External Sensors** - Support for integration with professional grade sensors.
- **Symptom Tracking** - Questionnaires for logging daily symptoms and experiences.
- **Cloud Sync** - Securely stores data on Firebase and enables real-time remote monitoring.
- **Graphs & Reports** - Data analyzed and presented through intuitive graphs and reports.
- **Exportable Data** - CSV export for customized analytics.
---
Overall, this well-architected and optimized implementation serves as a robust IoT monitoring solution for chronic fatigue syndrome. The codebase demonstrates various best practices that can be applied to any modern mobile app.
