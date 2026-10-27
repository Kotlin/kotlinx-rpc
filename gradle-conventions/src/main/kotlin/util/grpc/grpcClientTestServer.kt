/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.grpc

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.ExtensionAware
import org.gradle.process.ProcessForkOptions
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import util.withBackgroundTask
import java.net.InetAddress
import java.net.ServerSocket

private const val TEST_SERVER_PROJECT_PATH = ":tests:grpc-test-server"
private const val TEST_SERVER_DISTRIBUTION_NAME = "grpc-test-server"
private const val READY_LINE_PREFIX = "[GRPC-TEST-SERVER] Server started on"

/** The environment variable with the port of the server. The tests read it. */
private const val PORT_ENV = "GRPC_TEST_SERVER_PORT"

private const val PORT_PROPERTY = "grpcTestServerPort"

/**
 * One free port for the whole build, so that all test tasks use the same server process.
 */
private fun Project.grpcTestServerPort(): Int {
    val extra = (gradle as ExtensionAware).extensions.extraProperties
    if (!extra.has(PORT_PROPERTY)) {
        extra[PORT_PROPERTY] = ServerSocket(0, 0, InetAddress.getLoopbackAddress()).use { it.localPort }
    }
    return extra[PORT_PROPERTY] as Int
}

/**
 * Runs this test task against the gRPC test server from `:tests:grpc-test-server`.
 * All test tasks of the build use one server process, which starts once.
 */
fun Task.withGrpcClientTestServer() {
    val installation = project.project(TEST_SERVER_PROJECT_PATH).layout.buildDirectory
        .dir("install/$TEST_SERVER_DISTRIBUTION_NAME")
    val port = project.grpcTestServerPort().toString()
    val windowsHost = System.getProperty("os.name").orEmpty().startsWith("Windows", ignoreCase = true)

    dependsOn("$TEST_SERVER_PROJECT_PATH:installDist")
    withBackgroundTask("grpc-test-server") {
        command = installation.map { directory ->
            if (windowsHost) {
                val script = directory.file("bin/$TEST_SERVER_DISTRIBUTION_NAME.bat").asFile.absolutePath
                listOf("cmd.exe", "/d", "/c", script, port)
            } else {
                listOf(directory.file("bin/$TEST_SERVER_DISTRIBUTION_NAME").asFile.absolutePath, port)
            }
        }
        workingDirectory = installation
        // Must be the same as the ready line that `TestServer.kt` in `:tests:grpc-test-server` prints.
        readyLine = "$READY_LINE_PREFIX 127.0.0.1:$port"
        readyLinePrefix = READY_LINE_PREFIX
        outputPrefix = "[grpc-test-server]"
    }

    doFirst("passGrpcTestServerPort") {
        when (this) {
            // `simctl spawn` passes only the variables with the `SIMCTL_CHILD_` prefix to the simulator process
            is KotlinNativeSimulatorTest -> environment("SIMCTL_CHILD_$PORT_ENV", port)
            is KotlinNativeTest -> environment(PORT_ENV, port)
            // ProcessForkOptions covers KotlinJvmTest
            is ProcessForkOptions -> environment(PORT_ENV, port)
        }
    }
}
