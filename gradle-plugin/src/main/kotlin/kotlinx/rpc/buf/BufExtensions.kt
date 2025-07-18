/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.buf

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import java.io.File
import javax.inject.Inject

public open class BufExtension @Inject constructor(internal val project: Project) {
    public val configFile: Property<File> = project.objects.property<File>()

    public val generate: BufGenerateExtension = project.objects.newInstance(BufGenerateExtension::class.java, project)

    public fun generate(configure: Action<BufGenerateExtension>) {
        configure.execute(generate)
    }
}

public open class BufGenerateExtension @Inject constructor(internal val project: Project) {
    public val includeImports: Property<Boolean> = project.objects.property<Boolean>().convention(false)
}
