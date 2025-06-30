/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.checkers

import kotlinx.rpc.codegen.common.RpcClassId
import kotlinx.rpc.codegen.vsApi
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.caches.FirCache
import org.jetbrains.kotlin.fir.caches.createCache
import org.jetbrains.kotlin.fir.caches.firCachesFactory
import org.jetbrains.kotlin.fir.caches.getValue
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.getAnnotationByClassId
import org.jetbrains.kotlin.fir.declarations.utils.correspondingValueParameterFromPrimaryConstructor
import org.jetbrains.kotlin.fir.declarations.utils.hasBackingField
import org.jetbrains.kotlin.fir.declarations.utils.visibility
import org.jetbrains.kotlin.fir.deserialization.registeredInSerializationPluginMetadataExtension
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.extensions.FirExtensionSessionComponent
import org.jetbrains.kotlin.fir.resolve.fullyExpandedType
import org.jetbrains.kotlin.fir.scopes.impl.declaredMemberScope
import org.jetbrains.kotlin.fir.scopes.processAllProperties
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.utils.addIfNotNull

internal val FirSession.serializablePropertiesProvider: FirSerializablePropertiesProvider by FirSession.sessionComponentAccessor()

internal class FirSerializablePropertiesProvider(session: FirSession) : FirExtensionSessionComponent(session) {
    private val cache: FirCache<FirClassSymbol<*>, List<FirPropertySymbol>, Nothing?> =
        session.firCachesFactory.createCache(this::createSerializableProperties)

    fun getSerializablePropertiesForClass(classSymbol: FirClassSymbol<*>): List<FirPropertySymbol> {
        return cache.getValue(classSymbol)
    }

    private fun createSerializableProperties(classSymbol: FirClassSymbol<*>): List<FirPropertySymbol> {
        val allPropertySymbols = buildList {
            classSymbol
                .declaredMemberScope(session, memberRequiredPhase = null)
                .processAllProperties {
                    addIfNotNull(it as? FirPropertySymbol)
                }
        }

        val primaryConstructorProperties = allPropertySymbols.mapNotNull {
            val parameterSymbol = it.correspondingValueParameterFromPrimaryConstructor ?: return@mapNotNull null
            it to parameterSymbol.hasDefaultValue
        }.toMap().withDefault { false }

        fun isPropertySerializable(propertySymbol: FirPropertySymbol): Boolean {
            return when {
                propertySymbol in primaryConstructorProperties -> true
                propertySymbol.isVal -> !propertySymbol.hasSerialTransient(session)
                propertySymbol.visibility == Visibilities.Private -> false
                else -> (propertySymbol.isVar && propertySymbol.hasSerialTransient(session))
            }
        }

        val serializableProperties = allPropertySymbols.asSequence()
            .filter { isPropertySerializable(it) }
            .map { FirSerializableProperty(session, it) }
            .filterNot { it.transient }
            .map { it.propertySymbol }
            .let { own ->
                val superClassSymbol = classSymbol.superClassNotAny(session)
                buildList {
                    if (superClassSymbol != null) {
                        addAll(getSerializablePropertiesForClass(superClassSymbol))
                    }
                    addAll(own)
                }
            }

        return serializableProperties
    }
}

private class FirSerializableProperty(
    session: FirSession,
    val propertySymbol: FirPropertySymbol,
) {
    val transient: Boolean = run {
        if (propertySymbol.hasSerialTransient(session)) return@run true
        val hasBackingField = when (propertySymbol.origin) {
            FirDeclarationOrigin.Library -> propertySymbol.registeredInSerializationPluginMetadataExtension
            else -> propertySymbol.hasBackingField
        }
        !hasBackingField
    }
}

private fun FirBasedSymbol<*>.hasSerialTransient(session: FirSession): Boolean =
    getSerialTransientAnnotation(session) != null

private fun FirBasedSymbol<*>.getSerialTransientAnnotation(session: FirSession): FirAnnotation? =
    getAnnotationByClassId(RpcClassId.serializationTransient, session)

private fun FirClassSymbol<*>.superClassNotAny(session: FirSession): FirRegularClassSymbol? {
    return superClassOrAny(session).takeUnless { it.classId == StandardClassIds.Any }
}

private fun FirClassSymbol<*>.superClassOrAny(session: FirSession): FirRegularClassSymbol {
    return resolvedSuperTypes.firstNotNullOfOrNull { superType ->
        vsApi {
            superType
                .fullyExpandedType(session)
                .toRegularClassSymbolVS(session)
        }?.takeIf { it.classKind == ClassKind.CLASS }
    }
        ?: vsApi { session.builtinTypes.anyType.toRegularClassSymbolVS(session) }
        ?: error("Symbol for kotlin/Any not found")
}
