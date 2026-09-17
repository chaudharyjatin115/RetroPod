# RetroPod 🎧

RetroPod is a modern Android music application built with Kotlin Multiplatform and Jetpack Compose that brings the nostalgia of classic click-wheel iPods into a sleek, feature-packed digital music player.

Whether streaming from YouTube Music or listening to your local audio library, RetroPod delivers an authentic retro iPod experience complete with tactile wheel controls, customizable hardware shell finishes, and expressive LCD display themes.

---

## ✨ Features

- **Classic Click Wheel Interface**: Intuitive wheel scrolling with haptic feedback and tactile hardware controls.
- **YouTube Music Integration**: Search, stream, and sync online YouTube Music playlists and songs.
- **Local Library Audio Player**: Automatically scan and play local audio files stored on your device.
- **Smart Offline Downloads**: Save online tracks for offline listening while preserving local files.
- **Custom Hardware Shell Themes**: Choose from iconic finishes including *RetroPod U2 Edition*, *Special Edition PRODUCT (RED)*, *Original 2001 Scroll Wheel*, *Neon Cyberpunk 2077*, *80s Synthwave*, *Atomic Purple Translucent*, *Champagne Gold*, *Space Black Titanium*, and more.
- **Expressive LCD Display Themes**: Customize screen styles from retro *2001 Green LCD* to *1980s Amber CRT*, *Hacker Emerald Matrix*, *Vintage Parchment E-Ink*, and *Modern OLED Dark*.
- **Tactile Finishes & Details**: Select hardware finishes (Glossy, Satin, Matte, Brushed Aluminum, Polished Metal, Transparent Glass), hold switch toggle, and optional stickers.

---

## 🛠️ Tech Stack

- **Language**: Kotlin 2.x
- **UI Framework**: Jetpack Compose / Compose Multiplatform
- **Architecture**: Clean Architecture with Koin Dependency Injection
- **Audio Service**: AndroidX Media3 (ExoPlayer) with background playback service
- **Database**: Room Database with Kotlin Coroutines & Flow

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug (or newer)
- JDK 17+
- Android SDK 24+

### Build & Run

1. Clone the repository:
   ```bash
   git clone https://github.com/simpmusic/RetroPod.git
   cd RetroPod
   ```

2. Build debug APK:
   ```bash
   ./gradlew :androidApp:assembleDebug
   ```

3. Deploy to a connected device:
   ```bash
   ./gradlew :androidApp:installDebug
   ```

---

## 📄 License

Open source under the MIT License.
