# CityWorld

The great outdoors is all good and fine, but sometimes you just want to go to town. Not just any town—how about one that goes on forever or multiple ones separated by the largest mountains possible in Minecraft? But what is under those mountains? There is an entire world to explore. Have fun!

---

## Updates

This version of **CityWorld** has been updated to support **Minecraft 1.21.4**.

---

## Build Guide

To build this project, follow these steps:

### Prerequisites
- **Java 17**: Ensure you have Java 17 installed. You can check your Java version with:
  ```bash
  java -version
  ```
- **Gradle**: This project uses Gradle for building. Make sure you have gradle installed.
  e.g.
  - Linux:
  ```bash
  apt install gradle
  ```
  - MacOS:
  ```bash
  brew install gradle
  ```

### Steps
- **Clone the Repository**:

  ```bash
  git clone https://github.com/khatulistyr/CityWorld.git
  cd CityWorld
  ```
- **Build the Project**:
  
  ```bash
  ./gradlew build
  ```
- **Locate the Output**:
  The compiled .jar file will be located in the ./build/libs directory.

- **Install the Plugin**:
  Copy the .jar file to your Minecraft server's plugins directory and restart the server.
