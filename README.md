# 🥗 NutriScan

An offline-first, multi-module Android application built with **Jetpack Compose** and **Clean Architecture** that scans food product barcodes in real time to analyze nutritional macros, Nutri-Score grades, ingredients, and allergen warnings using the open-source **Open Food Facts API**.

> **Portfolio Note:** Designed and engineered to demonstrate production-grade Android architecture, lifecycle-aware hardware integration (CameraX + ML Kit), custom Compose `Canvas` graphics, and offline-first caching.

---

## 📱 Screenshots & Demo

| Live Barcode Scanner | Macro Breakdown & Nutri-Score | Offline Scan History |
| :---: | :---: | :---: |
| *Work in Progress* | *Work in Progress* | *Work in Progress* |

---

## ✨ Key Features

- **Real-Time Barcode Scanning:** Powered by **CameraX** and **Google ML Kit** with frame throttling and scan debouncing to prevent duplicate API calls and UI jank.
- **Interactive Macro Charts:** Custom-drawn Jetpack Compose `Canvas` donut charts visualizing Protein, Carbohydrates, and Fat distribution per 100g/serving without third-party charting libraries.
- **Nutri-Score & Allergen Alerts:** Dynamic color-coded Nutri-Score badges (`A` through `E`) and highlighted allergen warnings parsed from product ingredient lists.
- **Offline-First Caching:** Automatically persists scanned products in a local **Room** database so previously scanned items load instantaneously without an internet connection.
- **Paginated Product Search:** Debounced text search (`Kotlin Flow`) with **Paging 3** (`RemoteMediator`) for browsing over 3 million global food products.
- **Torch & Manual Fallback:** Built-in flashlight toggle for low-light scanning and manual barcode entry support.

---

## 🏗️ Architecture & Modularization

NutriScan follows **Google's recommended App Architecture** combined with **Clean Architecture** principles and strict feature-based modularization to optimize build times and enforce separation of concerns:

```text
NutriScan/
├── app/                     # App entry point, Hilt root, Jetpack Navigation graph, Theme
├── core/
│   ├── network/             # Retrofit, OkHttp (UserAgentInterceptor), Kotlinx Serialization
│   ├── database/            # Room database, entities, DAOs, and Paging RemoteKeys
│   ├── model/               # Pure Kotlin domain models & NetworkResult<T> wrappers
│   └── ui/                  # Reusable Compose components, NutriScore badges, MacroChart
└── feature/
    ├── scanner/             # CameraX preview, ImageAnalysis pipeline & ML Kit Barcode client
    ├── details/             # Product nutritional breakdown, macro canvas & allergen parser
    └── history/             # Offline-first scan history & daily intake summary

Engineering Highlights
Unidirectional Data Flow (UDF): ViewModels expose immutable StateFlow<UiState> streams collected lifecycle-safely via collectAsStateWithLifecycle().

Custom OkHttp Interceptor: Automatically injects the mandatory Open Food Facts User-Agent header and handles HTTP error mapping into sealed domain result states.

Lifecycle-Safe Camera Pipeline: Binds ProcessCameraProvider cleanly to the Compose lifecycle with ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST backpressure handling.

🛠️ Tech Stack
Category	Technologies
Language	Kotlin, Coroutines, Flow
UI & Graphics	Jetpack Compose, Material 3, Custom Canvas API, Coil
Architecture	Multi-Module Clean Architecture, MVVM, Unidirectional Data Flow
Dependency Injection	Hilt
Networking	Retrofit 2, OkHttp 4, Kotlinx Serialization
Local Persistence	Room Database, Paging 3 (RemoteMediator)
Camera & Vision	CameraX (camera-core, camera-camera2, camera-lifecycle, camera-view), Google ML Kit Barcode Scanning
Testing & CI	JUnit 4, MockK, Turbine (Flow testing), OkHttp MockWebServer, GitHub Actions

🌐 Open API Integration
NutriScan consumes the free, open-source Open Food Facts API v2 (no API key required):

Barcode Lookup: GET https://world.openfoodfacts.org/api/v2/product/{barcode}.json

Paginated Search: GET https://world.openfoodfacts.org/api/v2/search?categories_tags={query}&page={page}

🚀 Getting Started
Prerequisites
Android Studio Ladybug / Meerkat or newer

JDK 17+

Android SDK 34+ (Min SDK 26)

Physical Android device (recommended for live CameraX barcode scanning)

Installation
Clone the repository: git clone https://github.com/<your-username>/NutriScan.git

Open the project in Android Studio and let Gradle sync all modules.

Run the :app configuration on a physical device or emulator.

🧪 Testing & CI/CD
Unit Tests: ViewModel state transitions tested with Turbine and MainDispatcherRule; Retrofit/OkHttp interceptors tested against MockWebServer.

CI Pipeline: Automated GitHub Actions workflow runs ./gradlew testDebugUnitTest and static code analysis (ktlint / detekt) on every pull request.

📄 License
MIT License — Copyright (c) 2026
