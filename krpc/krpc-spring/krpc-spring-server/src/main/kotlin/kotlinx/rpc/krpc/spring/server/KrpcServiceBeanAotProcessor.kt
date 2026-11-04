/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.internal.WithServiceDescriptor
import kotlinx.serialization.Serializable
import org.springframework.aot.generate.GenerationContext
import org.springframework.aot.hint.MemberCategory
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.util.ClassUtils
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.lang.reflect.WildcardType

internal class KrpcServiceBeanAotProcessor : BeanFactoryInitializationAotProcessor {
    override fun processAheadOfTime(
        beanFactory: ConfigurableListableBeanFactory,
    ): BeanFactoryInitializationAotContribution? {
        val serviceClassNames = beanFactory.beanDefinitionNames
            .asSequence()
            .map(beanFactory::getBeanDefinition)
            .filter { it.beanClassName == KrpcServiceBeanHolders::class.java.name }
            .flatMap { beanDefinition ->
                val serviceClassNames = beanDefinition.constructorArgumentValues
                    .getIndexedArgumentValue(0, null)
                    ?.value as? Collection<*>
                    ?: emptyList<Any>()
                serviceClassNames.asSequence().filterIsInstance<String>()
            }
            .distinct()
            .map { ClassUtils.resolveClassName(it, beanFactory.beanClassLoader) }
            .toList()

        if (serviceClassNames.isEmpty()) return null

        return BeanFactoryInitializationAotContribution { generationContext, _ ->
            registerRuntimeHints(generationContext, serviceClassNames)
        }
    }

    private fun registerRuntimeHints(generationContext: GenerationContext, serviceClasses: List<Class<*>>) {
        val reflection = generationContext.runtimeHints.reflection()
        reflection.registerType(Rpc::class.java)
        reflection.registerType(WithServiceDescriptor::class.java)

        serviceClasses.forEach { serviceClass ->
            reflection.registerType(serviceClass)
            serviceClass.rpcInterfaces().forEach { rpcInterface ->
                reflection.registerType(rpcInterface, MemberCategory.INVOKE_PUBLIC_METHODS)
                rpcInterface.methods.forEach { method ->
                    val visitedTypes = mutableSetOf<Type>()
                    method.genericParameterTypes.forEach { registerSerializableTypes(reflection, it, visitedTypes) }
                    registerSerializableTypes(reflection, method.genericReturnType, visitedTypes)
                }

                rpcInterface.getAnnotation(WithServiceDescriptor::class.java)?.stub?.java?.let { stubClass ->
                    reflection.registerType(stubClass, *STUB_FIELD_ACCESS)
                    // The stub is a companion object, and `objectInstance` reads it from the enclosing class.
                    stubClass.enclosingClass?.let { enclosingClass ->
                        reflection.registerType(enclosingClass, *STUB_FIELD_ACCESS)
                    }
                }
            }
        }
    }

    private fun registerSerializableTypes(
        reflection: org.springframework.aot.hint.ReflectionHints,
        type: Type,
        visitedTypes: MutableSet<Type>,
    ) {
        if (!visitedTypes.add(type)) return

        when (type) {
            is Class<*> -> {
                if (type.isAnnotationPresent(Serializable::class.java)) {
                    reflection.registerType(
                        type,
                        MemberCategory.ACCESS_DECLARED_FIELDS,
                        MemberCategory.INVOKE_DECLARED_METHODS,
                    )
                }
                if (type.isArray) registerSerializableTypes(reflection, type.componentType, visitedTypes)
            }

            is ParameterizedType -> {
                registerSerializableTypes(reflection, type.rawType, visitedTypes)
                type.actualTypeArguments.forEach { registerSerializableTypes(reflection, it, visitedTypes) }
            }

            is GenericArrayType -> registerSerializableTypes(reflection, type.genericComponentType, visitedTypes)
            is WildcardType -> {
                type.lowerBounds.forEach { registerSerializableTypes(reflection, it, visitedTypes) }
                type.upperBounds.forEach { registerSerializableTypes(reflection, it, visitedTypes) }
            }

            is TypeVariable<*> -> type.bounds.forEach { registerSerializableTypes(reflection, it, visitedTypes) }
        }
    }

    private companion object {
        val STUB_FIELD_ACCESS = arrayOf(MemberCategory.ACCESS_PUBLIC_FIELDS, MemberCategory.ACCESS_DECLARED_FIELDS)
    }

    private fun Class<*>.rpcInterfaces(): List<Class<*>> =
        ClassUtils.getAllInterfacesForClassAsSet(this)
            .filter { AnnotatedElementUtils.hasAnnotation(it, Rpc::class.java) }
}