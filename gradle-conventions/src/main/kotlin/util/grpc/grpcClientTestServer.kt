/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.grpc

import org.gradle.api.Task
import util.withBackgroundTask

private const val TEST_SERVER_PROJECT_PATH = ":tests:grpc-test-server"
private const val TEST_SERVER_DISTRIBUTION_NAME = "grpc-test-server"
private const val READY_LINE_PREFIX = "[GRPC-TEST-SERVER] Server started on"

/** Must match the line printed by `TestServer.kt` in `:tests:grpc-test-server`. */
private const val READY_LINE = "$READY_LINE_PREFIX 127.0.0.1:50051; control protocol v1"

/**
 * Runs this test task against the gRPC test server from `:tests:grpc-test-server`,
 * which is started once and shared by all test tasks of the build.
 */
fun Task.withGrpcClientTestServer() {
    val installation = project.project(TEST_SERVER_PROJECT_PATH).layout.buildDirectory
        .dir("install/$TEST_SERVER_DISTRIBUTION_NAME")
    val windowsHost = System.getProperty("os.name").orEmpty().startsWith("Windows", ignoreCase = true)

    dependsOn("$TEST_SERVER_PROJECT_PATH:installDist")
    withBackgroundTask("grpc-test-server") {
        command = installation.map { directory ->
            if (windowsHost) {
                val script = directory.file("bin/$TEST_SERVER_DISTRIBUTION_NAME.bat").asFile.absolutePath
                listOf("cmd.exe", "/d", "/c", script)
            } else {
                listOf(directory.file("bin/$TEST_SERVER_DISTRIBUTION_NAME").asFile.absolutePath)
            }
        }
        workingDirectory = installation
        readyLine = READY_LINE
        readyLinePrefix = READY_LINE_PREFIX
        outputPrefix = "[grpc-test-server]"
    }
}
