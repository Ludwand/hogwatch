## Getting Started

Follow these steps in order to set up your environment and run the application.

### 1. Database Setup

1. Start your local **PostgreSQL** instance and create a new database.
2. Locate the initialization script at:
   ```path
   Backend/src/main/resources/schema.sql
   ```
3. Run `schema.sql` against your PostgreSQL database via terminal:
   ```bash
   psql -U <your_username> -d <your_database_name> -f Backend/src/main/resources/schema.sql
   ```

---

### 2. Running the Server

#### Configure Local Environment
Before starting the backend, check or create your local network configuration at:
```
shared/src/main/kotlin/local_config.kt
```

Ensure the contents match your local setup, updating `FRONTEND_URL` to your local IP address:

```kotlin
object LocalConfig {
    const val BACKEND_URL = "0.0.0.0"
    const val FRONTEND_URL = "0.0.0.0" // Replace with your local IP
    const val PORT = 8080
}
```

#### Run the Command
Open your terminal in the root directory of the project and execute the Gradle run command:

**Windows (Command Prompt / PowerShell)**
```cmd
.\gradlew.bat :backend:run
```

**macOS / Linux**
```bash
./gradlew :backend:run
```
---

### 3. Running the Mobile Application 📱

1. Open **Android Studio**.
2. Open the project.
3. Launch an Android Virtual Device from the **Device Manager**.
4. Click the **Run** button in Android Studio to build and deploy the app to your emulator.
