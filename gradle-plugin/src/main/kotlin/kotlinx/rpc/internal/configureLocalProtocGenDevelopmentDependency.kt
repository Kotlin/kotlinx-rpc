/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.rpc.protoc.grpcKotlinMultiplatform
import kotlinx.rpc.protoc.kotlinMultiplatform
import kotlinx.rpc.rpcExtension
import org.gradle.api.Project
import org.gradle.internal.extensions.core.extra
import org.gradle.kotlin.dsl.provideDelegate

@InternalRpcApi
public fun Project.configureLocalProtocGenDevelopmentDependency(
    vararg sourceSetSuffix: String = arrayOf("Test"),
) {
    val globalRootDir: String by extra

    // init
    rpcExtension().protoc {
        plugins {
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

        buf.generate.allTasks()
            .matching { sourceSetSuffix.any { name.endsWith(it) } }
            .configureEach {
                val includedBuild = gradle.includedBuild("protoc-gen")
                dependsOn(includedBuild.task(":grpc:jar"))
                dependsOn(includedBuild.task(":protobuf:jar"))
            }
    }
}
