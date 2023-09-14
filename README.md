# CFS Monitor - IoT App for Chronic Fatigue Syndrome Monitoring (Archive Repo)
CFS Monitor is an innovative IoT app built using a clean architecture approach and optimized with the latest technologies. 
This optimized and well-documented codebase serves as an archive reference for the implementation.
Read more about the project in this [report](https://drive.google.com/file/d/1qaSiJOnCJhrxOXi6WABgE7udTu6eIDo-/view?usp=sharing)

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

## Screenshots
<img src="https://github.com/yash-61016/CFSTracker/assets/73232849/824d45f3-aca0-4198-b1ed-cf8e9d7c9969" height="50%" width="20%">
<img src="https://github.com/yash-61016/CFSTracker/assets/73232849/2444ec6e-ada3-4425-b664-e56662639679" height="50%" width="20%">
<img src="https://github.com/yash-61016/CFSTracker/assets/73232849/b4b059cc-9f5c-45c6-b83a-275bbbfd9ed8" height="50%" width="20%">
<img src="https://github.com/yash-61016/CFSTracker/assets/73232849/52442d68-19a3-4c74-a57a-1e7095f22f81" height="50%" width="20%">
<img src="https://github.com/yash-61016/CFSTracker/assets/73232849/31b49e39-c46a-46c5-8e8c-0daa4fade938" height="50%" width="20%">
<img src="https://github.com/yash-61016/CFSTracker/assets/73232849/92e82118-f241-4e0d-87ea-d275a3a6347e" height="50%" width="20%">
<img src="https://github.com/yash-61016/CFSTracker/assets/73232849/8d524dfc-0379-4c5b-84be-5e0f53498459" height="50%" width="20%">
<img src="https://github.com/yash-61016/CFSTracker/assets/73232849/1234b5f3-8235-4a41-8dac-426a28304d9d" height="50%" width="20%">
<img src="https://github.com/yash-61016/CFSTracker/assets/73232849/a00669d2-64f1-4ad0-b295-379a9e620665" height="50%" width="20%">
<img src="https://github.com/yash-61016/CFSTracker/assets/73232849/54acbe44-8464-4a04-9763-122dffb1f0e1" height="50%" width="20%">
<img src="https://github.com/yash-61016/CFSTracker/assets/73232849/738955b9-8645-4c40-a413-dd87d31c0e6d" height="50%" width="20%">
<img src="https://github.com/yash-61016/CFSTracker/assets/73232849/e3aa8b87-c5ea-443b-ba53-2778ccccb3cc" height="50%" width="20%">



