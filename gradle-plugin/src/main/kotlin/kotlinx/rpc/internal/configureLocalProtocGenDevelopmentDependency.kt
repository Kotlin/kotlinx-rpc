/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.rpc.RpcExtension
import kotlinx.rpc.buf.tasks.BufGenerateTask
import kotlinx.rpc.protoc.grpcKotlinMultiplatform
import kotlinx.rpc.protoc.kotlinMultiplatform
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType

@InternalRpcApi
public fun Project.configureLocalProtocGenDevelopmentDependency() {
    val globalRootDir: String by extra

    the<RpcExtension>().protoc.plugins {
        kotlinMultiplatform {
            local {
                javaJar("$globalRootDir/protoc-gen/protobuf/build/libs/protobuf-$version-all.jar")
            }
        }

        grpcKotlinMultiplatform {
            local {
                javaJar("$globalRootDir/protoc-gen/grpc/build/libs/grpc-$version-all.jar")
            }
        }
    }

    tasks.withType<BufGenerateTask>().configureEach {
        if (name.endsWith("Test")) {
            val includedBuild = gradle.includedBuild("protoc-gen")
            dependsOn(includedBuild.task(":grpc:jar"))
            dependsOn(includedBuild.task(":protobuf:jar"))
        }
    }
}
