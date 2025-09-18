This is a Kotlin Multiplatform project targeting iOS, Desktop (JVM), Server, demonstrating the `kotlinx.rpc` library 
for gRPC.

* [/composeApp](./composeApp/src) is for code that will be shared across Compose Multiplatform applications.
  - [commonMain](./composeApp/src/commonMain/kotlin) contains all the relevant gRPC client application code for all platforms.

* [/server](./server/src/main/kotlin) is for the gRPC server application.

* [/shared](./shared/src) is for the code that will be shared between all targets in the project.
  It contains the proto files and the generated code used by both, the server and the client applications.

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

### Build and Run Server

To build and run the server, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :server:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :server:run
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…