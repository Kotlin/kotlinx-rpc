/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc

import kotlinx.rpc.buf.BufDepsExtension
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

/**
 * Configures BSR dependencies for a proto source set.
 *
 * @see <a href="https://buf.build/docs/configuration/v2/buf-yaml/#deps">buf.yaml deps reference</a>
 */
public open class ProtocBufDeps @Inject internal constructor(
    project: Project,
    sourceSetName: String
) : BufDepsExtension(project) {
    /**
     * Path to the buf.lock file. Used as input if it exists, or generated here if it doesn't.
     * Default: buf/<sourceSet>/buf.lock.
     */
    public val lockFile: Property<String> = project.objects.property<String>().convention(
        "buf/$sourceSetName/buf.lock"
    )
}
