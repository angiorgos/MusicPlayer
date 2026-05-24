# MusicPlayer 🎵

A feature-rich desktop music player built with **JavaFX** and **Maven**. This application provides a modern interface for managing and playing your local music library, with integrated metadata support and Discord Rich Presence.

## 📺 Demo Video
Check out the project in action:

[![MusicPlayer Demo](https://img.youtube.com/vi/6HD0O6TSg8E/0.jpg)](https://www.youtube.com/watch?v=6HD0O6TSg8E)

[Watch on YouTube](https://www.youtube.com/watch?v=6HD0O6TSg8E)

---

## ✨ Features

- **MP3 Playback:** Smooth playback of MP3 files with play/pause, next, and previous controls.
- **Library Management:** Select and load music from any local directory.
- **Metadata Extraction:** Automatically displays song titles, artists, and album art using `mp3agic`.
- **Discord Rich Presence:** Show your friends what you're listening to on Discord in real-time.
- **Interactive UI:**
  - Progress bar with seeking functionality.
  - Volume control with mute option.
  - Repeat song toggle.
  - Search functionality for quick song access.
- **Modern Design:** Styled with custom CSS and enhanced with libraries like `BootstrapFX` and `ControlsFX`.

---

## 🏗️ Project Structure

The project follows a standard Maven directory structure:

```text
MusicPlayer/
├── src/
│   └── main/
│       ├── java/
│       │   └── anagnostou/musicplayer/
│       │       ├── HelloApplication.java  # Main entry point
│       │       ├── FakeMain.java          # Wrapper for JAR execution
│       │       ├── HelloController.java     # Core logic for the music player
│       │       ├── SettingsController.java  # Management of application settings
│       │       └── HelpController.java      # Help and about information
│       └── resources/
│           └── anagnostou/musicplayer/
│               ├── hello-view.fxml          # Main UI layout
│               ├── settingsPane.fxml        # Settings UI layout
│               ├── help.fxml                # Help UI layout
│               ├── style.css                # Custom application styling
│               └── [images]                 # Icons and default assets
├── pom.xml                                  # Maven dependencies and configuration
└── module-info.java                         # Java Module System configuration
```

---

## 🛠️ Technologies Used

- **Java 21**
- **JavaFX 23** (Controls, FXML, Media, Graphics)
- **Maven** (Build Tool)
- **mp3agic** (ID3 tag processing)
- **Discord RPC** (Integration with Discord)
- **ControlsFX** & **BootstrapFX** (UI components and styling)

---

## 🚀 Getting Started

### Prerequisites
- JDK 21 or higher
- Maven 3.6 or higher

### Installation & Running
1. Clone the repository:
   ```bash
   git clone <repository-url>
   ```
2. Navigate to the project directory:
   ```bash
   cd MusicPlayer
   ```
3. Run the application using Maven:
   ```bash
   mvn clean javafx:run
   ```

---

## 📝 License
This project is for educational purposes. Feel free to use and modify it.
