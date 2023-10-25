package org.jetbrains.krpc

/**
 * When applied to an RPC service field - the field will be initialized eagerly rather then lazily
 */
@Target(AnnotationTarget.PROPERTY)
annotation class RPCEagerField
