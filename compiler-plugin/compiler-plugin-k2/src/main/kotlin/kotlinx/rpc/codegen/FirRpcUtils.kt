/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen

import kotlinx.rpc.codegen.common.ProtoNames
import org.jetbrains.kotlin.KtSourceElement
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.findArgumentByName
import org.jetbrains.kotlin.fir.declarations.getDeprecationsProvider
import org.jetbrains.kotlin.fir.deserialization.toQualifiedPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.expressions.FirCall
import org.jetbrains.kotlin.fir.expressions.FirLiteralExpression
import org.jetbrains.kotlin.fir.expressions.FirVarargArgumentsExpression
import org.jetbrains.kotlin.fir.expressions.UnresolvedExpressionTypeAccess
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotation
import org.jetbrains.kotlin.fir.expressions.builder.buildAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.expressions.builder.buildEnumEntryDeserializedAccessExpression
import org.jetbrains.kotlin.fir.expressions.builder.buildLiteralExpression
import org.jetbrains.kotlin.fir.extensions.predicate.DeclarationPredicate
import org.jetbrains.kotlin.fir.extensions.predicateBasedProvider
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.fullyExpandedType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.fir.types.ConeClassLikeType
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.types.coneTypeSafe
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.types.ConstantValueKind
import kotlin.collections.plus
import kotlin.collections.set

fun FirBasedSymbol<*>.rpcAnnotationSource(
    session: FirSession,
    predicate: DeclarationPredicate,
    classId: ClassId?,
): KtSourceElement? {
    return rpcAnnotation(session, predicate, classId)?.source
}

fun FirBasedSymbol<*>.rpcAnnotation(
    session: FirSession,
    predicate: DeclarationPredicate,
    classId: ClassId?,
): FirAnnotation? {
    return resolvedCompilerAnnotationsWithClassIds.rpcAnnotation(session, predicate, classId)
}

/**
 * Finds the annotation matching [predicate] or [classId].
 *
 * Annotations with an unresolved type are skipped instead of failing:
 * under the IDE's lazy resolution, only compiler-required annotations (the ones registered via predicates)
 * have their types resolved when declaration generation runs, the others are still `FirUserTypeRef`s.
 */
@OptIn(UnresolvedExpressionTypeAccess::class)
fun List<FirAnnotation>.rpcAnnotation(session: FirSession, predicate: DeclarationPredicate, classId: ClassId?): FirAnnotation? {
    return find {
        vsApi {
            it.coneTypeOrNull?.toClassSymbolVS(session)?.let { declaration ->
                session.predicateBasedProvider.matches(predicate, declaration)
            } == true || (classId != null && it.annotationTypeRef.doesMatchesClassId(session, classId))
        }
    }
}

/**
 * Returns the string literals of the `Array<String>` argument [name] of this annotation.
 *
 * Unlike the compiler's `getStringArrayArgument`, the argument is not evaluated:
 * the literals are read as they are, so the function is safe to call before
 * the annotation arguments are resolved (i.e., during declaration generation).
 * Only string literals are considered; other elements are skipped.
 */
internal fun FirAnnotation.stringArrayArgument(name: Name): List<String> {
    val elements = when (val argument = findArgumentByName(name)) {
        // an array literal, source or deserialized
        is FirCall -> argument.arguments
        // a vararg argument after resolution
        is FirVarargArgumentsExpression -> argument.arguments
        else -> return emptyList()
    }

    return elements.mapNotNull { (it as? FirLiteralExpression)?.value as? String }
}

/**
 * `true` if this type ref is resolved to the class [classId] (or a type alias expanding to it).
 * `false` for unresolved type refs.
 */
@OptIn(SymbolInternals::class)
internal fun FirTypeRef.doesMatchesClassId(session: FirSession, classId: ClassId): Boolean {
    return coneTypeSafe<ConeClassLikeType>()?.fullyExpandedType(session)?.lookupTag?.classId == classId
}

private val deprecatedAnnotationMessageName = Name.identifier("message")
private val deprecatedAnnotationLevelName = Name.identifier("level")
private val deprecatedLevelHiddenName = Name.identifier("HIDDEN")

// stolen from kx.serialization
private fun createDeprecatedHiddenAnnotation(session: FirSession): FirAnnotation = buildAnnotation {
    val deprecatedAnno = session.symbolProvider
        .getClassLikeSymbolByClassId(StandardClassIds.Annotations.Deprecated) as FirRegularClassSymbol

    annotationTypeRef = vsApi { deprecatedAnno.defaultType().toFirResolvedTypeRefVS() }

    argumentMapping = buildAnnotationArgumentMapping {
        mapping[deprecatedAnnotationMessageName] = buildLiteralExpression(
            source = null,
            kind = ConstantValueKind.String,
            value = "This synthesized declaration should not be used directly",
            setType = true,
        )

        mapping[deprecatedAnnotationLevelName] = buildEnumEntryDeserializedAccessExpression {
            enumClassId = StandardClassIds.DeprecationLevel
            enumEntryName = deprecatedLevelHiddenName
        }.toQualifiedPropertyAccessExpression(session)
    }
}

fun FirClassLikeDeclaration.markAsDeprecatedHidden(session: FirSession) {
    replaceAnnotations(annotations + listOf(createDeprecatedHiddenAnnotation(session)))
    replaceDeprecationsProvider(getDeprecationsProvider(session))
}


/**
 * Returns the [ClassId] corresponding to the generated internal message class for this [ClassId].
 */
fun ClassId.internalMessageClassId(): ClassId {
    val names = relativeClassName.pathSegments()
        .map { Name.identifier(ProtoNames.internalName(it.asString())) }

    return ClassId(packageFqName, names.first()).let { topLevel ->
        names.drop(1).fold(topLevel) { acc, name -> acc.createNestedClassId(name) }
    }
}

/**
 * Returns the [ClassId] corresponding to the generated presence interface for this message [ClassId].
 *
 * Only the top-level segment is suffixed with `Presence`; nested messages reuse their simple name nested
 * inside the parent's presence interface (e.g. `Outer.Inner` -> `OuterPresence.Inner`), mirroring the
 * naming used by the protobuf code generator.
 */
fun ClassId.presenceInterfaceClassId(): ClassId {
    val names = relativeClassName.pathSegments()
    val topLevel = ClassId(packageFqName, Name.identifier(ProtoNames.presenceInterfaceName(names.first().asString())))

    return names.drop(1).fold(topLevel) { acc, name -> acc.createNestedClassId(name) }
}
