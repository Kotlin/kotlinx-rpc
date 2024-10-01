# Ktor Web App
Sample application with Kotlin/Js on frontend and Kotlin/Jvm on backend,that uses kRPC with Ktor to communicate.

> Warning: the code is broken due to https://youtrack.jetbrains.com/issue/KT-71757/
> No workarounds for now

### Running frontend
To run frontend in development mode, run this command:
```bash
./gradlew frontend:jsRun
```
The client webpack app will start on port 3000 and it will route API requests to http://localhost:8080

### Running server
To run server without compiling frontend, simply run `main` function in [Application.kt](/server/src/main/kotlin/Application.kt) from IDEA UI.

To Run server with latest frontend use this command:
```bash
./gradlew server:runApp
```
Note that this configuration uses production distribution of frontend app, which makes each build slower, as it takes more time to compile production webpack.
