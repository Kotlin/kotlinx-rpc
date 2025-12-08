/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.kotlin.dsl.version
import kotlinx.rpc.buf.tasks.*
import kotlinx.rpc.buf.*
import kotlinx.rpc.protoc.*
import javax.inject.Inject

plugins {
    kotlin("jvm") version "<kotlin-version>"
    id("org.jetbrains.kotlinx.rpc.plugin") version "<rpc-version>"
}

public abstract class BufLintTask @Inject constructor(properties: ProtoTask.Properties) : BufExecTask(properties) {
    init {
        command.set("lint")
        args.set(emptyList())
    }
}

rpc {
    protoc {
        buf {
            tasks {
                registerWorkspaceTask<BufLintTask>("lint")
            }
        }
    }
}

fun Iterable<DefaultTask>.toNames() = map { it.name }.toSet()

fun assertTasks(
    tag: String,
    tasks: Iterable<DefaultTask>,
    vararg expected: String,
) {
    val names = tasks.toNames()
    if (expected.toSet() != names) {
        throw GradleException("[$tag] Expected: ${expected.toSet()}, actual: $names")
    }
}

tasks.register("test_tasks") {
    doLast {
        val genTasks = protoTasks.buf.generate

        assertTasks("gen all", genTasks, "bufGenerateMain", "bufGenerateTest")
        assertTasks("testTasks", genTasks.testTasks(), "bufGenerateTest")
        assertTasks("nonTestTasks", genTasks.nonTestTasks(), "bufGenerateMain")
        assertTasks("nonTestTasks testTasks", genTasks.nonTestTasks().testTasks())
        assertTasks("matchingSourceSet main", genTasks.matchingSourceSet("main"), "bufGenerateMain")
        assertTasks("matchingSourceSet test", genTasks.matchingSourceSet("test"), "bufGenerateTest")

        val allTasks = protoTasks.buf

        assertTasks("all", allTasks, "bufGenerateMain", "bufGenerateTest", "bufLintMain", "bufLintTest")
        assertTasks("all by type generate", allTasks.matchingType<BufGenerateTask>(), "bufGenerateMain", "bufGenerateTest")
        assertTasks("all by type lint", allTasks.matchingType<BufLintTask>(), "bufLintMain", "bufLintTest")
    }
}
