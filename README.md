# Cocktail Machine Material UI
A JavaFX application for a cocktail machine interface built with MaterialFX.

## Prerequisites

### For Raspberry Pi

1. **Raspberry Pi OS** (Raspberry Pi OS or any Debian-based Linux distribution)

2. **Install SDKMAN** (SDK Manager - simplifies Java and Maven installation)
   ```bash
   curl -s "https://get.sdkman.io" | bash
   source "$HOME/.sdkman/bin/sdkman-init.sh"
   ```
   Verify installation:
   ```bash
   sdk version
   ```

3. **Install Java 21 using SDKMAN** (required)
   ```bash
   # Install Liberica JDK 21 with JavaFX (required for this project)
   sdk install java 21.0.1fx-librca
   ```
   Verify installation:
   ```bash
   java -version
   javac -version
   ```

4. **Install Maven using SDKMAN**
   ```bash
   sdk install maven
   ```
   Verify installation:
   ```bash
   mvn -version
   ```

## Building the Application

1. **Clone or copy the project** to your Raspberry Pi:
   ```bash
   cd /path/to/project
   ```

2. **Build the project** using Maven:
   ```bash
   mvn clean install
   ```
   
## Running the Application

### Run with Maven
```bash
mvn javafx:run
```

### Run the packaged JAR
```bash
java -jar target/cocktail-machine-material-ui-1.0-SNAPSHOT.jar
```

**Note:** The fat JAR (created by maven-shade-plugin) bundles all Java classes and dependencies, but you still need:
- **JavaFX native libraries** available at runtime
- **Liberica JDK 21.0.1fx-librca is required** because it includes the JavaFX native libraries
- A regular JDK without JavaFX won't work because the native libraries won't be available

The fat JAR simplifies deployment by bundling all Java classes, but JavaFX's native components (for rendering) must still be provided by the JDK or JavaFX installation.

## Troubleshooting

### JavaFX not found
If you get errors about JavaFX modules:
- Ensure JavaFX is installed or use Liberica JDK
- Check module path is correct
- Verify all required modules are added: `javafx.controls`, `javafx.fxml`, `javafx.graphics`

### Display issues
- Check display is connected: `tvservice -s`
- Verify X server is running: `echo $DISPLAY`
- Try running with explicit display: `DISPLAY=:0 java ...`


