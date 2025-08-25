/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.MessageDigest
import javax.inject.Inject

abstract class PublishCLibDependencyTask : DefaultTask() {
    private val PACKAGES_BASE_URL = "https://packages.jetbrains.team/files/p"
    private val TEAM = "krpc"
    private val PACKAGE_DIR = "bazel-build-deps"

    @get:InputFile
    abstract val file: Property<File>

    @get:Input
    abstract val version: Property<String>

    @get:Input
    abstract val namespace: Property<String>

    @get:Input
    abstract val artifactName: Property<String>

    @get:Input
    abstract val token: Property<String>

    init {
        group = "publishing"
    }

    @TaskAction
    fun publish() {
        val cLibFile = file.get()
        if (!cLibFile.exists()) {
            throw GradleException("File ${cLibFile.absolutePath} doesn't exist")
        }

        val artifactName = artifactName.get()

        publishFile(artifactName, HttpRequest.BodyPublishers.ofFile(cLibFile.toPath()))

        val sha256 = computeSha256(cLibFile)
        publishFile("$artifactName.sha256", HttpRequest.BodyPublishers.ofString(sha256))

        logger.lifecycle("Published $artifactName")
        logger.lifecycle("SHA256: $sha256")
    }

    private fun publishFile(name: String, body: HttpRequest.BodyPublisher) {
        val namespace = namespace.get()
        val version = version.get()
        val token = token.get()
        val destUrl = "$PACKAGES_BASE_URL/$TEAM/$PACKAGE_DIR/$namespace/$version/$name"

        val client = HttpClient.newHttpClient()
        val req = HttpRequest.newBuilder()
            .uri(URI.create(destUrl))
            .header("Authorization", "Bearer $token")
            .PUT(body)
            .build()

        val res = client.send(req, HttpResponse.BodyHandlers.ofString())
        if (res.statusCode() !in 200..299) {
            throw GradleException("Upload failed (${res.statusCode()}): ${res.body()}")
        }
    }

    private fun computeSha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.forEachBlock { buffer, bytesRead ->
            digest.update(buffer, 0, bytesRead)
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}

abstract class BazelBuildTask @Inject constructor(
    private val execOps: ExecOperations,
    private val fsOps: FileSystemOperations,
) : DefaultTask() {

    @get:InputDirectory
    abstract val bazelProjectDir: Property<File>

    @get:Input
    abstract val bazelBuildTask: Property<String>

    @get:OutputFile
    abstract val outputFile: Property<File>

    init {
        group = "build"
    }

    @TaskAction
    fun run() {
        val bazelProjectDir = bazelProjectDir.get()
        val bazelBuildTask = bazelBuildTask.get()

        // run the build task
        execOps.exec {
            workingDir = bazelProjectDir
            commandLine("bash", "-c", "bazel build $bazelBuildTask")
        }

        val outFiles = getOutputFiles()

        if (outFiles.isEmpty()) {
            throw GradleException("No output for $bazelBuildTask")
        }

        if (outFiles.size > 1) {
            throw GradleException("Multiple output files for $bazelBuildTask: $outFiles")
        }

        // copy the output file to the output directory
        val dest = outputFile.get()
        val outFile = outFiles.single()
        fsOps.delete { delete(dest) }
        fsOps.copy {
            from(outFile)
            into(dest.parentFile)
            rename { dest.name }
        }
    }

    private fun getOutputFiles(): List<File> {
        val bazelProjectDir = bazelProjectDir.get()
        val bazelBuildTask = bazelBuildTask.get()

        val buf = ByteArrayOutputStream()
        execOps.exec {
            workingDir = bazelProjectDir
            commandLine("bash", "-c", "bazel cquery $bazelBuildTask --output=files")
            standardOutput = buf
        }

        val outFiles = buf.toString().lineSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { File(bazelProjectDir, it) }
            .toList()

        return outFiles
    }
}
