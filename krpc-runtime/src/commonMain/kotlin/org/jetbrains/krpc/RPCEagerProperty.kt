package org.jetbrains.krpc

/**
 * When applied to an RPC service property - the property will be initialized eagerly rather then lazily
 */
@Target(AnnotationTarget.PROPERTY)
annotation class RPCEagerProperty
