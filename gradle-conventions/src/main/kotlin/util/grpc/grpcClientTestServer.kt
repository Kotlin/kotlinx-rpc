/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.grpc

import org.gradle.api.Task
import util.BackgroundTaskLifetime
import util.BackgroundTaskReadiness
import util.withBackgroundTask

private const val TEST_SERVER_PROJECT_PATH = ":tests:grpc-test-server"
private const val TEST_SERVER_INSTALL_TASK = "$TEST_SERVER_PROJECT_PATH:installDist"
private const val TEST_SERVER_SHARED_NAME = "grpc-client-test-server"
private const val TEST_SERVER_DISTRIBUTION_NAME = "grpc-test-server"
private const val TEST_SERVER_ENDPOINT = "127.0.0.1:50051"
private const val TEST_SERVER_PROTOCOL_VERSION = 1
private const val TEST_SERVER_STARTUP_TIMEOUT_SECONDS = 30
private const val READY_LINE_PREFIX = "[GRPC-TEST-SERVER] Server started on"

/** Runs this test task against the build-shared gRPC client reference server. */
fun Task.withGrpcClientTestServer() {
    val serverProject = project.project(TEST_SERVER_PROJECT_PATH)
    val installationDirectory = serverProject.layout.buildDirectory.dir("install/$TEST_SERVER_DISTRIBUTION_NAME")
    val windowsHost = System.getProperty("os.name").orEmpty().startsWith("Windows", ignoreCase = true)
    val executableName = if (windowsHost) "$TEST_SERVER_DISTRIBUTION_NAME.bat" else TEST_SERVER_DISTRIBUTION_NAME
    val command = installationDirectory.map { directory ->
        val executable = directory.file("bin/$executableName").asFile.absolutePath
        if (windowsHost) listOf("cmd.exe", "/d", "/c", executable) else listOf(executable)
    }

    dependsOn(TEST_SERVER_INSTALL_TASK)
    withBackgroundTask {
        lifetime = BackgroundTaskLifetime.BUILD
        sharedName = TEST_SERVER_SHARED_NAME
        workingDirectory(installationDirectory)
        commandLineProvider(command)
        readyString = "$READY_LINE_PREFIX $TEST_SERVER_ENDPOINT; control protocol v$TEST_SERVER_PROTOCOL_VERSION"
        readiness = BackgroundTaskReadiness.EXACT
        readyLinePrefix = READY_LINE_PREFIX
        readyTimeoutSec = TEST_SERVER_STARTUP_TIMEOUT_SECONDS
        redirectErrorStream = true
        outputPrefix = "[grpc-client-test-server]"
    }
}
