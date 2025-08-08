/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlin.io.path.readLines

gradle.afterProject {
    layout.projectDirectory.dir("src").asFileTree.visit {
        if (isDirectory) return@visit
        if (file.name.endsWith(".kt")) {
            file.toPath().readLines().forEach { line ->
                if (!line.trim().startsWith("//") && line.contains("PsiElement")) {
                    throw GradleException(
                        "Kotlin source file $file must not contain `PsiElement` reference " +
                                "in the compiler plugin. Use KtElement instead."
                    )
                }
            }
        }
    }
}
