/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.dokka

import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.base.transformers.documentables.SuppressedByConditionDocumentableFilterTransformer
import org.jetbrains.dokka.model.Annotations
import org.jetbrains.dokka.model.Documentable
import org.jetbrains.dokka.model.properties.WithExtraProperties
import org.jetbrains.dokka.plugability.DokkaContext

class HideInternalRpcApiTransformer(context: DokkaContext) : SuppressedByConditionDocumentableFilterTransformer(context) {
    override fun shouldBeSuppressed(d: Documentable): Boolean {
        DokkaBaseConfiguration
        val annotations: List<Annotations.Annotation> =
            (d as? WithExtraProperties<*>)
                ?.extra
                ?.allOfType<Annotations>()
                ?.flatMap { it.directAnnotations.values.flatten() }
                ?: emptyList()

        return annotations.any { isInternalRpcAnnotation(it) }
    }

    private fun isInternalRpcAnnotation(annotation: Annotations.Annotation): Boolean {
        return annotation.dri.packageName == "kotlinx.rpc.internal.utils"
                && annotation.dri.classNames == "InternalRpcApi"
    }
}
