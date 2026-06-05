# OpenScholar

[![Build](https://github.com/uks89/OpenScholar/actions/workflows/android-build.yml/badge.svg)](https://github.com/uks89/OpenScholar/actions/workflows/android-build.yml)

Open-source, privacy-first AI research companion and second-brain system for students, researchers, and lifelong learners.

## Features

- **AI Second Brain**: Store notes, PDFs, images, voice recordings, and web articles with automatic embedding and semantic linking
- **Research Assistant**: Upload papers for automatic metadata extraction, summarization, and analysis
- **Literature Review Generator**: Generate comprehensive reviews from your paper collection
- **Research Gap Detection**: Identify missing connections and underexplored topics
- **Knowledge Graph**: Visualize relationships between papers, concepts, authors, and topics
- **AI Chat**: Chat with your entire library with source citations
- **Study Coach**: Generate learning roadmaps and study plans
- **Voice Research Assistant**: Speak commands and queries
- **Academic Writing Assistant**: Generate literature reviews, proposals, and outlines
- **Smart Memory**: Short-term, long-term, and project-specific memory layers

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Clean Architecture + Repository Pattern
- **DI**: Hilt
- **Database**: Room (SQLite) with 7 entities
- **AI Providers**: OpenRouter, HuggingFace, Ollama (pluggable)
- **Vector Store**: Abstraction layer with Room-based implementation
- **Agent Framework**: Modular agent architecture with orchestrator

## Project Structure

```
app/src/main/java/org/openscholar/app/
├── agent/              # AI agent framework (Librarian, Research, Orchestrator)
├── data/
│   ├── local/          # Room database (entities, DAOs, converters)
│   ├── remote/ai/      # AI provider implementations
│   ├── repository/     # Repository implementations
│   └── vector/         # Vector store abstraction
├── di/                 # Hilt DI modules
├── domain/
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Use cases (business logic)
├── model/              # Domain models
└── presentation/       # Compose UI (screens, viewmodels, navigation, theme)
```

## Build Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34

### Setup
1. Clone the repository
2. Open the `OpenScholar` directory in Android Studio
3. Sync Gradle (File → Sync Project with Gradle Files)
4. Build (Build → Make Project)

### Configuration
1. Obtain an API key from [OpenRouter](https://openrouter.ai)
2. In the app, navigate to Settings → AI Provider → enter your API key
3. (Optional) Install [Ollama](https://ollama.ai) for local inference

### Running
- Select a device/emulator (API 28+)
- Click Run (Shift+F10)

### Build on GitHub
Every push to `main` or `develop` automatically builds via GitHub Actions. The CI pipeline:
1. Builds the debug APK
2. Runs unit tests
3. Uploads the APK and test reports as artifacts

Download the latest build from the [Actions tab](https://github.com/uks89/OpenScholar/actions).

## Architecture Overview

The app follows Clean Architecture with three layers:

1. **Presentation** (Compose UI + ViewModels)
2. **Domain** (Use cases + Repository interfaces)
3. **Data** (Room + AI Providers + Vector Store)

Data flows unidirectionally: UI → ViewModel → UseCase → Repository → Data Source

## License

Open source under MIT License.
