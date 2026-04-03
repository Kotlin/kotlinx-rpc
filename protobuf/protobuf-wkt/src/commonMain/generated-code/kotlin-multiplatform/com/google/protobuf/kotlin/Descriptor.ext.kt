@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
* Constructs a new message.
* ```
* val message = FileDescriptorSet {
*    file = ...
* }
* ```
*/
public operator fun FileDescriptorSet.Companion.invoke(body: FileDescriptorSet.Builder.() -> Unit): FileDescriptorSet {
    val msg = FileDescriptorSetInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    file = ...
* }
* ```
*/
public fun FileDescriptorSet.copy(body: FileDescriptorSet.Builder.() -> Unit = {}): FileDescriptorSet {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.FileDescriptorSet] instance.
*/
public val FileDescriptorSet.presence: FileDescriptorSetPresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = FileDescriptorProto {
*    name = ...
* }
* ```
*/
public operator fun FileDescriptorProto.Companion.invoke(body: FileDescriptorProto.Builder.() -> Unit): FileDescriptorProto {
    val msg = FileDescriptorProtoInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    name = ...
* }
* ```
*/
public fun FileDescriptorProto.copy(body: FileDescriptorProto.Builder.() -> Unit = {}): FileDescriptorProto {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.FileDescriptorProto] instance.
*/
public val FileDescriptorProto.presence: FileDescriptorProtoPresence get() = this.asInternal()._presence

/**
* Returns the value of the `name` field if present, otherwise null.
*/
public val FileDescriptorProto.nameOrNull: String? get() = if (this.presence.hasName) this.name else null

/**
* Returns the value of the `package` field if present, otherwise null.
*/
public val FileDescriptorProto.packageOrNull: String? get() = if (this.presence.hasPackage) this.`package` else null

/**
* Returns the value of the `options` field if present, otherwise null.
*/
public val FileDescriptorProto.optionsOrNull: FileOptions? get() = if (this.presence.hasOptions) this.options else null

/**
* Returns the value of the `sourceCodeInfo` field if present, otherwise null.
*/
public val FileDescriptorProto.sourceCodeInfoOrNull: SourceCodeInfo? get() = if (this.presence.hasSourceCodeInfo) this.sourceCodeInfo else null

/**
* Returns the value of the `syntax` field if present, otherwise null.
*/
public val FileDescriptorProto.syntaxOrNull: String? get() = if (this.presence.hasSyntax) this.syntax else null

/**
* Returns the value of the `edition` field if present, otherwise null.
*/
public val FileDescriptorProto.editionOrNull: Edition? get() = if (this.presence.hasEdition) this.edition else null

/**
* Constructs a new message.
* ```
* val message = DescriptorProto {
*    name = ...
* }
* ```
*/
public operator fun DescriptorProto.Companion.invoke(body: DescriptorProto.Builder.() -> Unit): DescriptorProto {
    val msg = DescriptorProtoInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    name = ...
* }
* ```
*/
public fun DescriptorProto.copy(body: DescriptorProto.Builder.() -> Unit = {}): DescriptorProto {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.DescriptorProto] instance.
*/
public val DescriptorProto.presence: DescriptorProtoPresence get() = this.asInternal()._presence

/**
* Returns the value of the `name` field if present, otherwise null.
*/
public val DescriptorProto.nameOrNull: String? get() = if (this.presence.hasName) this.name else null

/**
* Returns the value of the `options` field if present, otherwise null.
*/
public val DescriptorProto.optionsOrNull: MessageOptions? get() = if (this.presence.hasOptions) this.options else null

/**
* Returns the value of the `visibility` field if present, otherwise null.
*/
public val DescriptorProto.visibilityOrNull: SymbolVisibility? get() = if (this.presence.hasVisibility) this.visibility else null

/**
* Constructs a new message.
* ```
* val message = ExtensionRangeOptions {
*    uninterpretedOption = ...
* }
* ```
*/
public operator fun ExtensionRangeOptions.Companion.invoke(body: ExtensionRangeOptions.Builder.() -> Unit): ExtensionRangeOptions {
    val msg = ExtensionRangeOptionsInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    uninterpretedOption = ...
* }
* ```
*/
public fun ExtensionRangeOptions.copy(body: ExtensionRangeOptions.Builder.() -> Unit = {}): ExtensionRangeOptions {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.ExtensionRangeOptions] instance.
*/
public val ExtensionRangeOptions.presence: ExtensionRangeOptionsPresence get() = this.asInternal()._presence

/**
* Returns the value of the `features` field if present, otherwise null.
*/
public val ExtensionRangeOptions.featuresOrNull: FeatureSet? get() = if (this.presence.hasFeatures) this.features else null

/**
* Returns the value of the `verification` field if present, otherwise null.
*/
public val ExtensionRangeOptions.verificationOrNull: ExtensionRangeOptions.VerificationState? get() = if (this.presence.hasVerification) this.verification else null

/**
* Constructs a new message.
* ```
* val message = FieldDescriptorProto {
*    name = ...
* }
* ```
*/
public operator fun FieldDescriptorProto.Companion.invoke(body: FieldDescriptorProto.Builder.() -> Unit): FieldDescriptorProto {
    val msg = FieldDescriptorProtoInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    name = ...
* }
* ```
*/
public fun FieldDescriptorProto.copy(body: FieldDescriptorProto.Builder.() -> Unit = {}): FieldDescriptorProto {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.FieldDescriptorProto] instance.
*/
public val FieldDescriptorProto.presence: FieldDescriptorProtoPresence get() = this.asInternal()._presence

/**
* Returns the value of the `name` field if present, otherwise null.
*/
public val FieldDescriptorProto.nameOrNull: String? get() = if (this.presence.hasName) this.name else null

/**
* Returns the value of the `number` field if present, otherwise null.
*/
public val FieldDescriptorProto.numberOrNull: Int? get() = if (this.presence.hasNumber) this.number else null

/**
* Returns the value of the `label` field if present, otherwise null.
*/
public val FieldDescriptorProto.labelOrNull: FieldDescriptorProto.Label? get() = if (this.presence.hasLabel) this.label else null

/**
* Returns the value of the `type` field if present, otherwise null.
*/
public val FieldDescriptorProto.typeOrNull: FieldDescriptorProto.Type? get() = if (this.presence.hasType) this.type else null

/**
* Returns the value of the `typeName` field if present, otherwise null.
*/
public val FieldDescriptorProto.typeNameOrNull: String? get() = if (this.presence.hasTypeName) this.typeName else null

/**
* Returns the value of the `extendee` field if present, otherwise null.
*/
public val FieldDescriptorProto.extendeeOrNull: String? get() = if (this.presence.hasExtendee) this.extendee else null

/**
* Returns the value of the `defaultValue` field if present, otherwise null.
*/
public val FieldDescriptorProto.defaultValueOrNull: String? get() = if (this.presence.hasDefaultValue) this.defaultValue else null

/**
* Returns the value of the `oneofIndex` field if present, otherwise null.
*/
public val FieldDescriptorProto.oneofIndexOrNull: Int? get() = if (this.presence.hasOneofIndex) this.oneofIndex else null

/**
* Returns the value of the `jsonName` field if present, otherwise null.
*/
public val FieldDescriptorProto.jsonNameOrNull: String? get() = if (this.presence.hasJsonName) this.jsonName else null

/**
* Returns the value of the `options` field if present, otherwise null.
*/
public val FieldDescriptorProto.optionsOrNull: FieldOptions? get() = if (this.presence.hasOptions) this.options else null

/**
* Returns the value of the `proto3Optional` field if present, otherwise null.
*/
public val FieldDescriptorProto.proto3OptionalOrNull: Boolean? get() = if (this.presence.hasProto3Optional) this.proto3Optional else null

/**
* Constructs a new message.
* ```
* val message = OneofDescriptorProto {
*    name = ...
* }
* ```
*/
public operator fun OneofDescriptorProto.Companion.invoke(body: OneofDescriptorProto.Builder.() -> Unit): OneofDescriptorProto {
    val msg = OneofDescriptorProtoInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    name = ...
* }
* ```
*/
public fun OneofDescriptorProto.copy(body: OneofDescriptorProto.Builder.() -> Unit = {}): OneofDescriptorProto {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.OneofDescriptorProto] instance.
*/
public val OneofDescriptorProto.presence: OneofDescriptorProtoPresence get() = this.asInternal()._presence

/**
* Returns the value of the `name` field if present, otherwise null.
*/
public val OneofDescriptorProto.nameOrNull: String? get() = if (this.presence.hasName) this.name else null

/**
* Returns the value of the `options` field if present, otherwise null.
*/
public val OneofDescriptorProto.optionsOrNull: OneofOptions? get() = if (this.presence.hasOptions) this.options else null

/**
* Constructs a new message.
* ```
* val message = EnumDescriptorProto {
*    name = ...
* }
* ```
*/
public operator fun EnumDescriptorProto.Companion.invoke(body: EnumDescriptorProto.Builder.() -> Unit): EnumDescriptorProto {
    val msg = EnumDescriptorProtoInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    name = ...
* }
* ```
*/
public fun EnumDescriptorProto.copy(body: EnumDescriptorProto.Builder.() -> Unit = {}): EnumDescriptorProto {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.EnumDescriptorProto] instance.
*/
public val EnumDescriptorProto.presence: EnumDescriptorProtoPresence get() = this.asInternal()._presence

/**
* Returns the value of the `name` field if present, otherwise null.
*/
public val EnumDescriptorProto.nameOrNull: String? get() = if (this.presence.hasName) this.name else null

/**
* Returns the value of the `options` field if present, otherwise null.
*/
public val EnumDescriptorProto.optionsOrNull: EnumOptions? get() = if (this.presence.hasOptions) this.options else null

/**
* Returns the value of the `visibility` field if present, otherwise null.
*/
public val EnumDescriptorProto.visibilityOrNull: SymbolVisibility? get() = if (this.presence.hasVisibility) this.visibility else null

/**
* Constructs a new message.
* ```
* val message = EnumValueDescriptorProto {
*    name = ...
* }
* ```
*/
public operator fun EnumValueDescriptorProto.Companion.invoke(body: EnumValueDescriptorProto.Builder.() -> Unit): EnumValueDescriptorProto {
    val msg = EnumValueDescriptorProtoInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    name = ...
* }
* ```
*/
public fun EnumValueDescriptorProto.copy(body: EnumValueDescriptorProto.Builder.() -> Unit = {}): EnumValueDescriptorProto {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.EnumValueDescriptorProto] instance.
*/
public val EnumValueDescriptorProto.presence: EnumValueDescriptorProtoPresence get() = this.asInternal()._presence

/**
* Returns the value of the `name` field if present, otherwise null.
*/
public val EnumValueDescriptorProto.nameOrNull: String? get() = if (this.presence.hasName) this.name else null

/**
* Returns the value of the `number` field if present, otherwise null.
*/
public val EnumValueDescriptorProto.numberOrNull: Int? get() = if (this.presence.hasNumber) this.number else null

/**
* Returns the value of the `options` field if present, otherwise null.
*/
public val EnumValueDescriptorProto.optionsOrNull: EnumValueOptions? get() = if (this.presence.hasOptions) this.options else null

/**
* Constructs a new message.
* ```
* val message = ServiceDescriptorProto {
*    name = ...
* }
* ```
*/
public operator fun ServiceDescriptorProto.Companion.invoke(body: ServiceDescriptorProto.Builder.() -> Unit): ServiceDescriptorProto {
    val msg = ServiceDescriptorProtoInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    name = ...
* }
* ```
*/
public fun ServiceDescriptorProto.copy(body: ServiceDescriptorProto.Builder.() -> Unit = {}): ServiceDescriptorProto {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.ServiceDescriptorProto] instance.
*/
public val ServiceDescriptorProto.presence: ServiceDescriptorProtoPresence get() = this.asInternal()._presence

/**
* Returns the value of the `name` field if present, otherwise null.
*/
public val ServiceDescriptorProto.nameOrNull: String? get() = if (this.presence.hasName) this.name else null

/**
* Returns the value of the `options` field if present, otherwise null.
*/
public val ServiceDescriptorProto.optionsOrNull: ServiceOptions? get() = if (this.presence.hasOptions) this.options else null

/**
* Constructs a new message.
* ```
* val message = MethodDescriptorProto {
*    name = ...
* }
* ```
*/
public operator fun MethodDescriptorProto.Companion.invoke(body: MethodDescriptorProto.Builder.() -> Unit): MethodDescriptorProto {
    val msg = MethodDescriptorProtoInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    name = ...
* }
* ```
*/
public fun MethodDescriptorProto.copy(body: MethodDescriptorProto.Builder.() -> Unit = {}): MethodDescriptorProto {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.MethodDescriptorProto] instance.
*/
public val MethodDescriptorProto.presence: MethodDescriptorProtoPresence get() = this.asInternal()._presence

/**
* Returns the value of the `name` field if present, otherwise null.
*/
public val MethodDescriptorProto.nameOrNull: String? get() = if (this.presence.hasName) this.name else null

/**
* Returns the value of the `inputType` field if present, otherwise null.
*/
public val MethodDescriptorProto.inputTypeOrNull: String? get() = if (this.presence.hasInputType) this.inputType else null

/**
* Returns the value of the `outputType` field if present, otherwise null.
*/
public val MethodDescriptorProto.outputTypeOrNull: String? get() = if (this.presence.hasOutputType) this.outputType else null

/**
* Returns the value of the `options` field if present, otherwise null.
*/
public val MethodDescriptorProto.optionsOrNull: MethodOptions? get() = if (this.presence.hasOptions) this.options else null

/**
* Returns the value of the `clientStreaming` field if present, otherwise null.
*/
public val MethodDescriptorProto.clientStreamingOrNull: Boolean? get() = if (this.presence.hasClientStreaming) this.clientStreaming else null

/**
* Returns the value of the `serverStreaming` field if present, otherwise null.
*/
public val MethodDescriptorProto.serverStreamingOrNull: Boolean? get() = if (this.presence.hasServerStreaming) this.serverStreaming else null

/**
* Constructs a new message.
* ```
* val message = FileOptions {
*    javaPackage = ...
* }
* ```
*/
public operator fun FileOptions.Companion.invoke(body: FileOptions.Builder.() -> Unit): FileOptions {
    val msg = FileOptionsInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    javaPackage = ...
* }
* ```
*/
public fun FileOptions.copy(body: FileOptions.Builder.() -> Unit = {}): FileOptions {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.FileOptions] instance.
*/
public val FileOptions.presence: FileOptionsPresence get() = this.asInternal()._presence

/**
* Returns the value of the `javaPackage` field if present, otherwise null.
*/
public val FileOptions.javaPackageOrNull: String? get() = if (this.presence.hasJavaPackage) this.javaPackage else null

/**
* Returns the value of the `javaOuterClassname` field if present, otherwise null.
*/
public val FileOptions.javaOuterClassnameOrNull: String? get() = if (this.presence.hasJavaOuterClassname) this.javaOuterClassname else null

/**
* Returns the value of the `javaMultipleFiles` field if present, otherwise null.
*/
public val FileOptions.javaMultipleFilesOrNull: Boolean? get() = if (this.presence.hasJavaMultipleFiles) this.javaMultipleFiles else null

/**
* Returns the value of the `javaGenerateEqualsAndHash` field if present, otherwise null.
*/
public val FileOptions.javaGenerateEqualsAndHashOrNull: Boolean? get() = if (this.presence.hasJavaGenerateEqualsAndHash) this.javaGenerateEqualsAndHash else null

/**
* Returns the value of the `javaStringCheckUtf8` field if present, otherwise null.
*/
public val FileOptions.javaStringCheckUtf8OrNull: Boolean? get() = if (this.presence.hasJavaStringCheckUtf8) this.javaStringCheckUtf8 else null

/**
* Returns the value of the `optimizeFor` field if present, otherwise null.
*/
public val FileOptions.optimizeForOrNull: FileOptions.OptimizeMode? get() = if (this.presence.hasOptimizeFor) this.optimizeFor else null

/**
* Returns the value of the `goPackage` field if present, otherwise null.
*/
public val FileOptions.goPackageOrNull: String? get() = if (this.presence.hasGoPackage) this.goPackage else null

/**
* Returns the value of the `ccGenericServices` field if present, otherwise null.
*/
public val FileOptions.ccGenericServicesOrNull: Boolean? get() = if (this.presence.hasCcGenericServices) this.ccGenericServices else null

/**
* Returns the value of the `javaGenericServices` field if present, otherwise null.
*/
public val FileOptions.javaGenericServicesOrNull: Boolean? get() = if (this.presence.hasJavaGenericServices) this.javaGenericServices else null

/**
* Returns the value of the `pyGenericServices` field if present, otherwise null.
*/
public val FileOptions.pyGenericServicesOrNull: Boolean? get() = if (this.presence.hasPyGenericServices) this.pyGenericServices else null

/**
* Returns the value of the `deprecated` field if present, otherwise null.
*/
public val FileOptions.deprecatedOrNull: Boolean? get() = if (this.presence.hasDeprecated) this.deprecated else null

/**
* Returns the value of the `ccEnableArenas` field if present, otherwise null.
*/
public val FileOptions.ccEnableArenasOrNull: Boolean? get() = if (this.presence.hasCcEnableArenas) this.ccEnableArenas else null

/**
* Returns the value of the `objcClassPrefix` field if present, otherwise null.
*/
public val FileOptions.objcClassPrefixOrNull: String? get() = if (this.presence.hasObjcClassPrefix) this.objcClassPrefix else null

/**
* Returns the value of the `csharpNamespace` field if present, otherwise null.
*/
public val FileOptions.csharpNamespaceOrNull: String? get() = if (this.presence.hasCsharpNamespace) this.csharpNamespace else null

/**
* Returns the value of the `swiftPrefix` field if present, otherwise null.
*/
public val FileOptions.swiftPrefixOrNull: String? get() = if (this.presence.hasSwiftPrefix) this.swiftPrefix else null

/**
* Returns the value of the `phpClassPrefix` field if present, otherwise null.
*/
public val FileOptions.phpClassPrefixOrNull: String? get() = if (this.presence.hasPhpClassPrefix) this.phpClassPrefix else null

/**
* Returns the value of the `phpNamespace` field if present, otherwise null.
*/
public val FileOptions.phpNamespaceOrNull: String? get() = if (this.presence.hasPhpNamespace) this.phpNamespace else null

/**
* Returns the value of the `phpMetadataNamespace` field if present, otherwise null.
*/
public val FileOptions.phpMetadataNamespaceOrNull: String? get() = if (this.presence.hasPhpMetadataNamespace) this.phpMetadataNamespace else null

/**
* Returns the value of the `rubyPackage` field if present, otherwise null.
*/
public val FileOptions.rubyPackageOrNull: String? get() = if (this.presence.hasRubyPackage) this.rubyPackage else null

/**
* Returns the value of the `features` field if present, otherwise null.
*/
public val FileOptions.featuresOrNull: FeatureSet? get() = if (this.presence.hasFeatures) this.features else null

/**
* Constructs a new message.
* ```
* val message = MessageOptions {
*    messageSetWireFormat = ...
* }
* ```
*/
public operator fun MessageOptions.Companion.invoke(body: MessageOptions.Builder.() -> Unit): MessageOptions {
    val msg = MessageOptionsInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    messageSetWireFormat = ...
* }
* ```
*/
public fun MessageOptions.copy(body: MessageOptions.Builder.() -> Unit = {}): MessageOptions {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.MessageOptions] instance.
*/
public val MessageOptions.presence: MessageOptionsPresence get() = this.asInternal()._presence

/**
* Returns the value of the `messageSetWireFormat` field if present, otherwise null.
*/
public val MessageOptions.messageSetWireFormatOrNull: Boolean? get() = if (this.presence.hasMessageSetWireFormat) this.messageSetWireFormat else null

/**
* Returns the value of the `noStandardDescriptorAccessor` field if present, otherwise null.
*/
public val MessageOptions.noStandardDescriptorAccessorOrNull: Boolean? get() = if (this.presence.hasNoStandardDescriptorAccessor) this.noStandardDescriptorAccessor else null

/**
* Returns the value of the `deprecated` field if present, otherwise null.
*/
public val MessageOptions.deprecatedOrNull: Boolean? get() = if (this.presence.hasDeprecated) this.deprecated else null

/**
* Returns the value of the `mapEntry` field if present, otherwise null.
*/
public val MessageOptions.mapEntryOrNull: Boolean? get() = if (this.presence.hasMapEntry) this.mapEntry else null

/**
* Returns the value of the `deprecatedLegacyJsonFieldConflicts` field if present, otherwise null.
*/
public val MessageOptions.deprecatedLegacyJsonFieldConflictsOrNull: Boolean? get() = if (this.presence.hasDeprecatedLegacyJsonFieldConflicts) this.deprecatedLegacyJsonFieldConflicts else null

/**
* Returns the value of the `features` field if present, otherwise null.
*/
public val MessageOptions.featuresOrNull: FeatureSet? get() = if (this.presence.hasFeatures) this.features else null

/**
* Constructs a new message.
* ```
* val message = FieldOptions {
*    ctype = ...
* }
* ```
*/
public operator fun FieldOptions.Companion.invoke(body: FieldOptions.Builder.() -> Unit): FieldOptions {
    val msg = FieldOptionsInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    ctype = ...
* }
* ```
*/
public fun FieldOptions.copy(body: FieldOptions.Builder.() -> Unit = {}): FieldOptions {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.FieldOptions] instance.
*/
public val FieldOptions.presence: FieldOptionsPresence get() = this.asInternal()._presence

/**
* Returns the value of the `ctype` field if present, otherwise null.
*/
public val FieldOptions.ctypeOrNull: FieldOptions.CType? get() = if (this.presence.hasCtype) this.ctype else null

/**
* Returns the value of the `packed` field if present, otherwise null.
*/
public val FieldOptions.packedOrNull: Boolean? get() = if (this.presence.hasPacked) this.packed else null

/**
* Returns the value of the `jstype` field if present, otherwise null.
*/
public val FieldOptions.jstypeOrNull: FieldOptions.JSType? get() = if (this.presence.hasJstype) this.jstype else null

/**
* Returns the value of the `lazy` field if present, otherwise null.
*/
public val FieldOptions.lazyOrNull: Boolean? get() = if (this.presence.hasLazy) this.lazy else null

/**
* Returns the value of the `unverifiedLazy` field if present, otherwise null.
*/
public val FieldOptions.unverifiedLazyOrNull: Boolean? get() = if (this.presence.hasUnverifiedLazy) this.unverifiedLazy else null

/**
* Returns the value of the `deprecated` field if present, otherwise null.
*/
public val FieldOptions.deprecatedOrNull: Boolean? get() = if (this.presence.hasDeprecated) this.deprecated else null

/**
* Returns the value of the `weak` field if present, otherwise null.
*/
public val FieldOptions.weakOrNull: Boolean? get() = if (this.presence.hasWeak) this.weak else null

/**
* Returns the value of the `debugRedact` field if present, otherwise null.
*/
public val FieldOptions.debugRedactOrNull: Boolean? get() = if (this.presence.hasDebugRedact) this.debugRedact else null

/**
* Returns the value of the `retention` field if present, otherwise null.
*/
public val FieldOptions.retentionOrNull: FieldOptions.OptionRetention? get() = if (this.presence.hasRetention) this.retention else null

/**
* Returns the value of the `features` field if present, otherwise null.
*/
public val FieldOptions.featuresOrNull: FeatureSet? get() = if (this.presence.hasFeatures) this.features else null

/**
* Returns the value of the `featureSupport` field if present, otherwise null.
*/
public val FieldOptions.featureSupportOrNull: FieldOptions.FeatureSupport? get() = if (this.presence.hasFeatureSupport) this.featureSupport else null

/**
* Constructs a new message.
* ```
* val message = OneofOptions {
*    features = ...
* }
* ```
*/
public operator fun OneofOptions.Companion.invoke(body: OneofOptions.Builder.() -> Unit): OneofOptions {
    val msg = OneofOptionsInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    features = ...
* }
* ```
*/
public fun OneofOptions.copy(body: OneofOptions.Builder.() -> Unit = {}): OneofOptions {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.OneofOptions] instance.
*/
public val OneofOptions.presence: OneofOptionsPresence get() = this.asInternal()._presence

/**
* Returns the value of the `features` field if present, otherwise null.
*/
public val OneofOptions.featuresOrNull: FeatureSet? get() = if (this.presence.hasFeatures) this.features else null

/**
* Constructs a new message.
* ```
* val message = EnumOptions {
*    allowAlias = ...
* }
* ```
*/
public operator fun EnumOptions.Companion.invoke(body: EnumOptions.Builder.() -> Unit): EnumOptions {
    val msg = EnumOptionsInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    allowAlias = ...
* }
* ```
*/
public fun EnumOptions.copy(body: EnumOptions.Builder.() -> Unit = {}): EnumOptions {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.EnumOptions] instance.
*/
public val EnumOptions.presence: EnumOptionsPresence get() = this.asInternal()._presence

/**
* Returns the value of the `allowAlias` field if present, otherwise null.
*/
public val EnumOptions.allowAliasOrNull: Boolean? get() = if (this.presence.hasAllowAlias) this.allowAlias else null

/**
* Returns the value of the `deprecated` field if present, otherwise null.
*/
public val EnumOptions.deprecatedOrNull: Boolean? get() = if (this.presence.hasDeprecated) this.deprecated else null

/**
* Returns the value of the `deprecatedLegacyJsonFieldConflicts` field if present, otherwise null.
*/
public val EnumOptions.deprecatedLegacyJsonFieldConflictsOrNull: Boolean? get() = if (this.presence.hasDeprecatedLegacyJsonFieldConflicts) this.deprecatedLegacyJsonFieldConflicts else null

/**
* Returns the value of the `features` field if present, otherwise null.
*/
public val EnumOptions.featuresOrNull: FeatureSet? get() = if (this.presence.hasFeatures) this.features else null

/**
* Constructs a new message.
* ```
* val message = EnumValueOptions {
*    deprecated = ...
* }
* ```
*/
public operator fun EnumValueOptions.Companion.invoke(body: EnumValueOptions.Builder.() -> Unit): EnumValueOptions {
    val msg = EnumValueOptionsInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    deprecated = ...
* }
* ```
*/
public fun EnumValueOptions.copy(body: EnumValueOptions.Builder.() -> Unit = {}): EnumValueOptions {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.EnumValueOptions] instance.
*/
public val EnumValueOptions.presence: EnumValueOptionsPresence get() = this.asInternal()._presence

/**
* Returns the value of the `deprecated` field if present, otherwise null.
*/
public val EnumValueOptions.deprecatedOrNull: Boolean? get() = if (this.presence.hasDeprecated) this.deprecated else null

/**
* Returns the value of the `features` field if present, otherwise null.
*/
public val EnumValueOptions.featuresOrNull: FeatureSet? get() = if (this.presence.hasFeatures) this.features else null

/**
* Returns the value of the `debugRedact` field if present, otherwise null.
*/
public val EnumValueOptions.debugRedactOrNull: Boolean? get() = if (this.presence.hasDebugRedact) this.debugRedact else null

/**
* Returns the value of the `featureSupport` field if present, otherwise null.
*/
public val EnumValueOptions.featureSupportOrNull: FieldOptions.FeatureSupport? get() = if (this.presence.hasFeatureSupport) this.featureSupport else null

/**
* Constructs a new message.
* ```
* val message = ServiceOptions {
*    features = ...
* }
* ```
*/
public operator fun ServiceOptions.Companion.invoke(body: ServiceOptions.Builder.() -> Unit): ServiceOptions {
    val msg = ServiceOptionsInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    features = ...
* }
* ```
*/
public fun ServiceOptions.copy(body: ServiceOptions.Builder.() -> Unit = {}): ServiceOptions {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.ServiceOptions] instance.
*/
public val ServiceOptions.presence: ServiceOptionsPresence get() = this.asInternal()._presence

/**
* Returns the value of the `features` field if present, otherwise null.
*/
public val ServiceOptions.featuresOrNull: FeatureSet? get() = if (this.presence.hasFeatures) this.features else null

/**
* Returns the value of the `deprecated` field if present, otherwise null.
*/
public val ServiceOptions.deprecatedOrNull: Boolean? get() = if (this.presence.hasDeprecated) this.deprecated else null

/**
* Constructs a new message.
* ```
* val message = MethodOptions {
*    deprecated = ...
* }
* ```
*/
public operator fun MethodOptions.Companion.invoke(body: MethodOptions.Builder.() -> Unit): MethodOptions {
    val msg = MethodOptionsInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    deprecated = ...
* }
* ```
*/
public fun MethodOptions.copy(body: MethodOptions.Builder.() -> Unit = {}): MethodOptions {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.MethodOptions] instance.
*/
public val MethodOptions.presence: MethodOptionsPresence get() = this.asInternal()._presence

/**
* Returns the value of the `deprecated` field if present, otherwise null.
*/
public val MethodOptions.deprecatedOrNull: Boolean? get() = if (this.presence.hasDeprecated) this.deprecated else null

/**
* Returns the value of the `idempotencyLevel` field if present, otherwise null.
*/
public val MethodOptions.idempotencyLevelOrNull: MethodOptions.IdempotencyLevel? get() = if (this.presence.hasIdempotencyLevel) this.idempotencyLevel else null

/**
* Returns the value of the `features` field if present, otherwise null.
*/
public val MethodOptions.featuresOrNull: FeatureSet? get() = if (this.presence.hasFeatures) this.features else null

/**
* Constructs a new message.
* ```
* val message = UninterpretedOption {
*    name = ...
* }
* ```
*/
public operator fun UninterpretedOption.Companion.invoke(body: UninterpretedOption.Builder.() -> Unit): UninterpretedOption {
    val msg = UninterpretedOptionInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    name = ...
* }
* ```
*/
public fun UninterpretedOption.copy(body: UninterpretedOption.Builder.() -> Unit = {}): UninterpretedOption {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.UninterpretedOption] instance.
*/
public val UninterpretedOption.presence: UninterpretedOptionPresence get() = this.asInternal()._presence

/**
* Returns the value of the `identifierValue` field if present, otherwise null.
*/
public val UninterpretedOption.identifierValueOrNull: String? get() = if (this.presence.hasIdentifierValue) this.identifierValue else null

/**
* Returns the value of the `positiveIntValue` field if present, otherwise null.
*/
public val UninterpretedOption.positiveIntValueOrNull: ULong? get() = if (this.presence.hasPositiveIntValue) this.positiveIntValue else null

/**
* Returns the value of the `negativeIntValue` field if present, otherwise null.
*/
public val UninterpretedOption.negativeIntValueOrNull: Long? get() = if (this.presence.hasNegativeIntValue) this.negativeIntValue else null

/**
* Returns the value of the `doubleValue` field if present, otherwise null.
*/
public val UninterpretedOption.doubleValueOrNull: Double? get() = if (this.presence.hasDoubleValue) this.doubleValue else null

/**
* Returns the value of the `stringValue` field if present, otherwise null.
*/
public val UninterpretedOption.stringValueOrNull: ByteString? get() = if (this.presence.hasStringValue) this.stringValue else null

/**
* Returns the value of the `aggregateValue` field if present, otherwise null.
*/
public val UninterpretedOption.aggregateValueOrNull: String? get() = if (this.presence.hasAggregateValue) this.aggregateValue else null

/**
* Constructs a new message.
* ```
* val message = FeatureSet {
*    fieldPresence = ...
* }
* ```
*/
public operator fun FeatureSet.Companion.invoke(body: FeatureSet.Builder.() -> Unit): FeatureSet {
    val msg = FeatureSetInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    fieldPresence = ...
* }
* ```
*/
public fun FeatureSet.copy(body: FeatureSet.Builder.() -> Unit = {}): FeatureSet {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.FeatureSet] instance.
*/
public val FeatureSet.presence: FeatureSetPresence get() = this.asInternal()._presence

/**
* Returns the value of the `fieldPresence` field if present, otherwise null.
*/
public val FeatureSet.fieldPresenceOrNull: FeatureSet.FieldPresence? get() = if (this.presence.hasFieldPresence) this.fieldPresence else null

/**
* Returns the value of the `enumType` field if present, otherwise null.
*/
public val FeatureSet.enumTypeOrNull: FeatureSet.EnumType? get() = if (this.presence.hasEnumType) this.enumType else null

/**
* Returns the value of the `repeatedFieldEncoding` field if present, otherwise null.
*/
public val FeatureSet.repeatedFieldEncodingOrNull: FeatureSet.RepeatedFieldEncoding? get() = if (this.presence.hasRepeatedFieldEncoding) this.repeatedFieldEncoding else null

/**
* Returns the value of the `utf8Validation` field if present, otherwise null.
*/
public val FeatureSet.utf8ValidationOrNull: FeatureSet.Utf8Validation? get() = if (this.presence.hasUtf8Validation) this.utf8Validation else null

/**
* Returns the value of the `messageEncoding` field if present, otherwise null.
*/
public val FeatureSet.messageEncodingOrNull: FeatureSet.MessageEncoding? get() = if (this.presence.hasMessageEncoding) this.messageEncoding else null

/**
* Returns the value of the `jsonFormat` field if present, otherwise null.
*/
public val FeatureSet.jsonFormatOrNull: FeatureSet.JsonFormat? get() = if (this.presence.hasJsonFormat) this.jsonFormat else null

/**
* Returns the value of the `enforceNamingStyle` field if present, otherwise null.
*/
public val FeatureSet.enforceNamingStyleOrNull: FeatureSet.EnforceNamingStyle? get() = if (this.presence.hasEnforceNamingStyle) this.enforceNamingStyle else null

/**
* Returns the value of the `defaultSymbolVisibility` field if present, otherwise null.
*/
public val FeatureSet.defaultSymbolVisibilityOrNull: FeatureSet.VisibilityFeature.DefaultSymbolVisibility? get() = if (this.presence.hasDefaultSymbolVisibility) this.defaultSymbolVisibility else null

/**
* Constructs a new message.
* ```
* val message = FeatureSetDefaults {
*    defaults = ...
* }
* ```
*/
public operator fun FeatureSetDefaults.Companion.invoke(body: FeatureSetDefaults.Builder.() -> Unit): FeatureSetDefaults {
    val msg = FeatureSetDefaultsInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    defaults = ...
* }
* ```
*/
public fun FeatureSetDefaults.copy(body: FeatureSetDefaults.Builder.() -> Unit = {}): FeatureSetDefaults {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.FeatureSetDefaults] instance.
*/
public val FeatureSetDefaults.presence: FeatureSetDefaultsPresence get() = this.asInternal()._presence

/**
* Returns the value of the `minimumEdition` field if present, otherwise null.
*/
public val FeatureSetDefaults.minimumEditionOrNull: Edition? get() = if (this.presence.hasMinimumEdition) this.minimumEdition else null

/**
* Returns the value of the `maximumEdition` field if present, otherwise null.
*/
public val FeatureSetDefaults.maximumEditionOrNull: Edition? get() = if (this.presence.hasMaximumEdition) this.maximumEdition else null

/**
* Constructs a new message.
* ```
* val message = SourceCodeInfo {
*    location = ...
* }
* ```
*/
public operator fun SourceCodeInfo.Companion.invoke(body: SourceCodeInfo.Builder.() -> Unit): SourceCodeInfo {
    val msg = SourceCodeInfoInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    location = ...
* }
* ```
*/
public fun SourceCodeInfo.copy(body: SourceCodeInfo.Builder.() -> Unit = {}): SourceCodeInfo {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.SourceCodeInfo] instance.
*/
public val SourceCodeInfo.presence: SourceCodeInfoPresence get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = GeneratedCodeInfo {
*    annotation = ...
* }
* ```
*/
public operator fun GeneratedCodeInfo.Companion.invoke(body: GeneratedCodeInfo.Builder.() -> Unit): GeneratedCodeInfo {
    val msg = GeneratedCodeInfoInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    annotation = ...
* }
* ```
*/
public fun GeneratedCodeInfo.copy(body: GeneratedCodeInfo.Builder.() -> Unit = {}): GeneratedCodeInfo {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = ExtensionRange {
*    start = ...
* }
* ```
*/
public operator fun DescriptorProto.ExtensionRange.Companion.invoke(body: DescriptorProto.ExtensionRange.Builder.() -> Unit): DescriptorProto.ExtensionRange {
    val msg = DescriptorProtoInternal.ExtensionRangeInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    start = ...
* }
* ```
*/
public fun DescriptorProto.ExtensionRange.copy(body: DescriptorProto.ExtensionRange.Builder.() -> Unit = {}): DescriptorProto.ExtensionRange {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.DescriptorProto.ExtensionRange] instance.
*/
public val DescriptorProto.ExtensionRange.presence: DescriptorProtoPresence.ExtensionRange get() = this.asInternal()._presence

/**
* Returns the value of the `start` field if present, otherwise null.
*/
public val DescriptorProto.ExtensionRange.startOrNull: Int? get() = if (this.presence.hasStart) this.start else null

/**
* Returns the value of the `end` field if present, otherwise null.
*/
public val DescriptorProto.ExtensionRange.endOrNull: Int? get() = if (this.presence.hasEnd) this.end else null

/**
* Returns the value of the `options` field if present, otherwise null.
*/
public val DescriptorProto.ExtensionRange.optionsOrNull: ExtensionRangeOptions? get() = if (this.presence.hasOptions) this.options else null

/**
* Constructs a new message.
* ```
* val message = ReservedRange {
*    start = ...
* }
* ```
*/
public operator fun DescriptorProto.ReservedRange.Companion.invoke(body: DescriptorProto.ReservedRange.Builder.() -> Unit): DescriptorProto.ReservedRange {
    val msg = DescriptorProtoInternal.ReservedRangeInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    start = ...
* }
* ```
*/
public fun DescriptorProto.ReservedRange.copy(body: DescriptorProto.ReservedRange.Builder.() -> Unit = {}): DescriptorProto.ReservedRange {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.DescriptorProto.ReservedRange] instance.
*/
public val DescriptorProto.ReservedRange.presence: DescriptorProtoPresence.ReservedRange get() = this.asInternal()._presence

/**
* Returns the value of the `start` field if present, otherwise null.
*/
public val DescriptorProto.ReservedRange.startOrNull: Int? get() = if (this.presence.hasStart) this.start else null

/**
* Returns the value of the `end` field if present, otherwise null.
*/
public val DescriptorProto.ReservedRange.endOrNull: Int? get() = if (this.presence.hasEnd) this.end else null

/**
* Constructs a new message.
* ```
* val message = Declaration {
*    number = ...
* }
* ```
*/
public operator fun ExtensionRangeOptions.Declaration.Companion.invoke(body: ExtensionRangeOptions.Declaration.Builder.() -> Unit): ExtensionRangeOptions.Declaration {
    val msg = ExtensionRangeOptionsInternal.DeclarationInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    number = ...
* }
* ```
*/
public fun ExtensionRangeOptions.Declaration.copy(body: ExtensionRangeOptions.Declaration.Builder.() -> Unit = {}): ExtensionRangeOptions.Declaration {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.ExtensionRangeOptions.Declaration] instance.
*/
public val ExtensionRangeOptions.Declaration.presence: ExtensionRangeOptionsPresence.Declaration get() = this.asInternal()._presence

/**
* Returns the value of the `number` field if present, otherwise null.
*/
public val ExtensionRangeOptions.Declaration.numberOrNull: Int? get() = if (this.presence.hasNumber) this.number else null

/**
* Returns the value of the `fullName` field if present, otherwise null.
*/
public val ExtensionRangeOptions.Declaration.fullNameOrNull: String? get() = if (this.presence.hasFullName) this.fullName else null

/**
* Returns the value of the `type` field if present, otherwise null.
*/
public val ExtensionRangeOptions.Declaration.typeOrNull: String? get() = if (this.presence.hasType) this.type else null

/**
* Returns the value of the `reserved` field if present, otherwise null.
*/
public val ExtensionRangeOptions.Declaration.reservedOrNull: Boolean? get() = if (this.presence.hasReserved) this.reserved else null

/**
* Returns the value of the `repeated` field if present, otherwise null.
*/
public val ExtensionRangeOptions.Declaration.repeatedOrNull: Boolean? get() = if (this.presence.hasRepeated) this.repeated else null

/**
* Constructs a new message.
* ```
* val message = EnumReservedRange {
*    start = ...
* }
* ```
*/
public operator fun EnumDescriptorProto.EnumReservedRange.Companion.invoke(body: EnumDescriptorProto.EnumReservedRange.Builder.() -> Unit): EnumDescriptorProto.EnumReservedRange {
    val msg = EnumDescriptorProtoInternal.EnumReservedRangeInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    start = ...
* }
* ```
*/
public fun EnumDescriptorProto.EnumReservedRange.copy(body: EnumDescriptorProto.EnumReservedRange.Builder.() -> Unit = {}): EnumDescriptorProto.EnumReservedRange {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.EnumDescriptorProto.EnumReservedRange] instance.
*/
public val EnumDescriptorProto.EnumReservedRange.presence: EnumDescriptorProtoPresence.EnumReservedRange get() = this.asInternal()._presence

/**
* Returns the value of the `start` field if present, otherwise null.
*/
public val EnumDescriptorProto.EnumReservedRange.startOrNull: Int? get() = if (this.presence.hasStart) this.start else null

/**
* Returns the value of the `end` field if present, otherwise null.
*/
public val EnumDescriptorProto.EnumReservedRange.endOrNull: Int? get() = if (this.presence.hasEnd) this.end else null

/**
* Constructs a new message.
* ```
* val message = EditionDefault {
*    edition = ...
* }
* ```
*/
public operator fun FieldOptions.EditionDefault.Companion.invoke(body: FieldOptions.EditionDefault.Builder.() -> Unit): FieldOptions.EditionDefault {
    val msg = FieldOptionsInternal.EditionDefaultInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    edition = ...
* }
* ```
*/
public fun FieldOptions.EditionDefault.copy(body: FieldOptions.EditionDefault.Builder.() -> Unit = {}): FieldOptions.EditionDefault {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.FieldOptions.EditionDefault] instance.
*/
public val FieldOptions.EditionDefault.presence: FieldOptionsPresence.EditionDefault get() = this.asInternal()._presence

/**
* Returns the value of the `edition` field if present, otherwise null.
*/
public val FieldOptions.EditionDefault.editionOrNull: Edition? get() = if (this.presence.hasEdition) this.edition else null

/**
* Returns the value of the `value` field if present, otherwise null.
*/
public val FieldOptions.EditionDefault.valueOrNull: String? get() = if (this.presence.hasValue) this.value else null

/**
* Constructs a new message.
* ```
* val message = FeatureSupport {
*    editionIntroduced = ...
* }
* ```
*/
public operator fun FieldOptions.FeatureSupport.Companion.invoke(body: FieldOptions.FeatureSupport.Builder.() -> Unit): FieldOptions.FeatureSupport {
    val msg = FieldOptionsInternal.FeatureSupportInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    editionIntroduced = ...
* }
* ```
*/
public fun FieldOptions.FeatureSupport.copy(body: FieldOptions.FeatureSupport.Builder.() -> Unit = {}): FieldOptions.FeatureSupport {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.FieldOptions.FeatureSupport] instance.
*/
public val FieldOptions.FeatureSupport.presence: FieldOptionsPresence.FeatureSupport get() = this.asInternal()._presence

/**
* Returns the value of the `editionIntroduced` field if present, otherwise null.
*/
public val FieldOptions.FeatureSupport.editionIntroducedOrNull: Edition? get() = if (this.presence.hasEditionIntroduced) this.editionIntroduced else null

/**
* Returns the value of the `editionDeprecated` field if present, otherwise null.
*/
public val FieldOptions.FeatureSupport.editionDeprecatedOrNull: Edition? get() = if (this.presence.hasEditionDeprecated) this.editionDeprecated else null

/**
* Returns the value of the `deprecationWarning` field if present, otherwise null.
*/
public val FieldOptions.FeatureSupport.deprecationWarningOrNull: String? get() = if (this.presence.hasDeprecationWarning) this.deprecationWarning else null

/**
* Returns the value of the `editionRemoved` field if present, otherwise null.
*/
public val FieldOptions.FeatureSupport.editionRemovedOrNull: Edition? get() = if (this.presence.hasEditionRemoved) this.editionRemoved else null

/**
* Constructs a new message.
* ```
* val message = NamePart {
*    namePart = ...
* }
* ```
*/
public operator fun UninterpretedOption.NamePart.Companion.invoke(body: UninterpretedOption.NamePart.Builder.() -> Unit): UninterpretedOption.NamePart {
    val msg = UninterpretedOptionInternal.NamePartInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    namePart = ...
* }
* ```
*/
public fun UninterpretedOption.NamePart.copy(body: UninterpretedOption.NamePart.Builder.() -> Unit = {}): UninterpretedOption.NamePart {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.UninterpretedOption.NamePart] instance.
*/
public val UninterpretedOption.NamePart.presence: UninterpretedOptionPresence.NamePart get() = this.asInternal()._presence

/**
* Constructs a new message.
* ```
* val message = VisibilityFeature { }
* ```
*/
public operator fun FeatureSet.VisibilityFeature.Companion.invoke(body: FeatureSet.VisibilityFeature.Builder.() -> Unit): FeatureSet.VisibilityFeature {
    val msg = FeatureSetInternal.VisibilityFeatureInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy()
* ```
*/
public fun FeatureSet.VisibilityFeature.copy(body: FeatureSet.VisibilityFeature.Builder.() -> Unit = {}): FeatureSet.VisibilityFeature {
    return this.asInternal().copyInternal(body)
}

/**
* Constructs a new message.
* ```
* val message = FeatureSetEditionDefault {
*    edition = ...
* }
* ```
*/
public operator fun FeatureSetDefaults.FeatureSetEditionDefault.Companion.invoke(body: FeatureSetDefaults.FeatureSetEditionDefault.Builder.() -> Unit): FeatureSetDefaults.FeatureSetEditionDefault {
    val msg = FeatureSetDefaultsInternal.FeatureSetEditionDefaultInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    edition = ...
* }
* ```
*/
public fun FeatureSetDefaults.FeatureSetEditionDefault.copy(body: FeatureSetDefaults.FeatureSetEditionDefault.Builder.() -> Unit = {}): FeatureSetDefaults.FeatureSetEditionDefault {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.FeatureSetDefaults.FeatureSetEditionDefault] instance.
*/
public val FeatureSetDefaults.FeatureSetEditionDefault.presence: FeatureSetDefaultsPresence.FeatureSetEditionDefault get() = this.asInternal()._presence

/**
* Returns the value of the `edition` field if present, otherwise null.
*/
public val FeatureSetDefaults.FeatureSetEditionDefault.editionOrNull: Edition? get() = if (this.presence.hasEdition) this.edition else null

/**
* Returns the value of the `overridableFeatures` field if present, otherwise null.
*/
public val FeatureSetDefaults.FeatureSetEditionDefault.overridableFeaturesOrNull: FeatureSet? get() = if (this.presence.hasOverridableFeatures) this.overridableFeatures else null

/**
* Returns the value of the `fixedFeatures` field if present, otherwise null.
*/
public val FeatureSetDefaults.FeatureSetEditionDefault.fixedFeaturesOrNull: FeatureSet? get() = if (this.presence.hasFixedFeatures) this.fixedFeatures else null

/**
* Constructs a new message.
* ```
* val message = Location {
*    path = ...
* }
* ```
*/
public operator fun SourceCodeInfo.Location.Companion.invoke(body: SourceCodeInfo.Location.Builder.() -> Unit): SourceCodeInfo.Location {
    val msg = SourceCodeInfoInternal.LocationInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    path = ...
* }
* ```
*/
public fun SourceCodeInfo.Location.copy(body: SourceCodeInfo.Location.Builder.() -> Unit = {}): SourceCodeInfo.Location {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.SourceCodeInfo.Location] instance.
*/
public val SourceCodeInfo.Location.presence: SourceCodeInfoPresence.Location get() = this.asInternal()._presence

/**
* Returns the value of the `leadingComments` field if present, otherwise null.
*/
public val SourceCodeInfo.Location.leadingCommentsOrNull: String? get() = if (this.presence.hasLeadingComments) this.leadingComments else null

/**
* Returns the value of the `trailingComments` field if present, otherwise null.
*/
public val SourceCodeInfo.Location.trailingCommentsOrNull: String? get() = if (this.presence.hasTrailingComments) this.trailingComments else null

/**
* Constructs a new message.
* ```
* val message = Annotation {
*    path = ...
* }
* ```
*/
public operator fun GeneratedCodeInfo.Annotation.Companion.invoke(body: GeneratedCodeInfo.Annotation.Builder.() -> Unit): GeneratedCodeInfo.Annotation {
    val msg = GeneratedCodeInfoInternal.AnnotationInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    path = ...
* }
* ```
*/
public fun GeneratedCodeInfo.Annotation.copy(body: GeneratedCodeInfo.Annotation.Builder.() -> Unit = {}): GeneratedCodeInfo.Annotation {
    return this.asInternal().copyInternal(body)
}

/**
* Returns the field-presence view for this [com.google.protobuf.kotlin.GeneratedCodeInfo.Annotation] instance.
*/
public val GeneratedCodeInfo.Annotation.presence: GeneratedCodeInfoPresence.Annotation get() = this.asInternal()._presence

/**
* Returns the value of the `sourceFile` field if present, otherwise null.
*/
public val GeneratedCodeInfo.Annotation.sourceFileOrNull: String? get() = if (this.presence.hasSourceFile) this.sourceFile else null

/**
* Returns the value of the `begin` field if present, otherwise null.
*/
public val GeneratedCodeInfo.Annotation.beginOrNull: Int? get() = if (this.presence.hasBegin) this.begin else null

/**
* Returns the value of the `end` field if present, otherwise null.
*/
public val GeneratedCodeInfo.Annotation.endOrNull: Int? get() = if (this.presence.hasEnd) this.end else null

/**
* Returns the value of the `semantic` field if present, otherwise null.
*/
public val GeneratedCodeInfo.Annotation.semanticOrNull: GeneratedCodeInfo.Annotation.Semantic? get() = if (this.presence.hasSemantic) this.semantic else null

public interface FileDescriptorSetPresence

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.FileDescriptorProto] messages.
* Retrieve it via the [com.google.protobuf.kotlin.FileDescriptorProto.presence] extension property.
*/
public interface FileDescriptorProtoPresence {
    public val hasName: Boolean

    public val hasPackage: Boolean

    public val hasOptions: Boolean

    public val hasSourceCodeInfo: Boolean

    public val hasSyntax: Boolean

    public val hasEdition: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.DescriptorProto] messages.
* Retrieve it via the [com.google.protobuf.kotlin.DescriptorProto.presence] extension property.
*/
public interface DescriptorProtoPresence {
    public val hasName: Boolean

    public val hasOptions: Boolean

    public val hasVisibility: Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf.kotlin.DescriptorProto.ExtensionRange] messages.
    * Retrieve it via the [com.google.protobuf.kotlin.DescriptorProto.ExtensionRange.presence] extension property.
    */
    public interface ExtensionRange {
        public val hasStart: Boolean

        public val hasEnd: Boolean

        public val hasOptions: Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf.kotlin.DescriptorProto.ReservedRange] messages.
    * Retrieve it via the [com.google.protobuf.kotlin.DescriptorProto.ReservedRange.presence] extension property.
    */
    public interface ReservedRange {
        public val hasStart: Boolean

        public val hasEnd: Boolean
    }
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.ExtensionRangeOptions] messages.
* Retrieve it via the [com.google.protobuf.kotlin.ExtensionRangeOptions.presence] extension property.
*/
public interface ExtensionRangeOptionsPresence {
    public val hasFeatures: Boolean

    public val hasVerification: Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf.kotlin.ExtensionRangeOptions.Declaration] messages.
    * Retrieve it via the [com.google.protobuf.kotlin.ExtensionRangeOptions.Declaration.presence] extension property.
    */
    public interface Declaration {
        public val hasNumber: Boolean

        public val hasFullName: Boolean

        public val hasType: Boolean

        public val hasReserved: Boolean

        public val hasRepeated: Boolean
    }
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.FieldDescriptorProto] messages.
* Retrieve it via the [com.google.protobuf.kotlin.FieldDescriptorProto.presence] extension property.
*/
public interface FieldDescriptorProtoPresence {
    public val hasName: Boolean

    public val hasNumber: Boolean

    public val hasLabel: Boolean

    public val hasType: Boolean

    public val hasTypeName: Boolean

    public val hasExtendee: Boolean

    public val hasDefaultValue: Boolean

    public val hasOneofIndex: Boolean

    public val hasJsonName: Boolean

    public val hasOptions: Boolean

    public val hasProto3Optional: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.OneofDescriptorProto] messages.
* Retrieve it via the [com.google.protobuf.kotlin.OneofDescriptorProto.presence] extension property.
*/
public interface OneofDescriptorProtoPresence {
    public val hasName: Boolean

    public val hasOptions: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.EnumDescriptorProto] messages.
* Retrieve it via the [com.google.protobuf.kotlin.EnumDescriptorProto.presence] extension property.
*/
public interface EnumDescriptorProtoPresence {
    public val hasName: Boolean

    public val hasOptions: Boolean

    public val hasVisibility: Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf.kotlin.EnumDescriptorProto.EnumReservedRange] messages.
    * Retrieve it via the [com.google.protobuf.kotlin.EnumDescriptorProto.EnumReservedRange.presence] extension property.
    */
    public interface EnumReservedRange {
        public val hasStart: Boolean

        public val hasEnd: Boolean
    }
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.EnumValueDescriptorProto] messages.
* Retrieve it via the [com.google.protobuf.kotlin.EnumValueDescriptorProto.presence] extension property.
*/
public interface EnumValueDescriptorProtoPresence {
    public val hasName: Boolean

    public val hasNumber: Boolean

    public val hasOptions: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.ServiceDescriptorProto] messages.
* Retrieve it via the [com.google.protobuf.kotlin.ServiceDescriptorProto.presence] extension property.
*/
public interface ServiceDescriptorProtoPresence {
    public val hasName: Boolean

    public val hasOptions: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.MethodDescriptorProto] messages.
* Retrieve it via the [com.google.protobuf.kotlin.MethodDescriptorProto.presence] extension property.
*/
public interface MethodDescriptorProtoPresence {
    public val hasName: Boolean

    public val hasInputType: Boolean

    public val hasOutputType: Boolean

    public val hasOptions: Boolean

    public val hasClientStreaming: Boolean

    public val hasServerStreaming: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.FileOptions] messages.
* Retrieve it via the [com.google.protobuf.kotlin.FileOptions.presence] extension property.
*/
public interface FileOptionsPresence {
    public val hasJavaPackage: Boolean

    public val hasJavaOuterClassname: Boolean

    public val hasJavaMultipleFiles: Boolean

    public val hasJavaGenerateEqualsAndHash: Boolean

    public val hasJavaStringCheckUtf8: Boolean

    public val hasOptimizeFor: Boolean

    public val hasGoPackage: Boolean

    public val hasCcGenericServices: Boolean

    public val hasJavaGenericServices: Boolean

    public val hasPyGenericServices: Boolean

    public val hasDeprecated: Boolean

    public val hasCcEnableArenas: Boolean

    public val hasObjcClassPrefix: Boolean

    public val hasCsharpNamespace: Boolean

    public val hasSwiftPrefix: Boolean

    public val hasPhpClassPrefix: Boolean

    public val hasPhpNamespace: Boolean

    public val hasPhpMetadataNamespace: Boolean

    public val hasRubyPackage: Boolean

    public val hasFeatures: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.MessageOptions] messages.
* Retrieve it via the [com.google.protobuf.kotlin.MessageOptions.presence] extension property.
*/
public interface MessageOptionsPresence {
    public val hasMessageSetWireFormat: Boolean

    public val hasNoStandardDescriptorAccessor: Boolean

    public val hasDeprecated: Boolean

    public val hasMapEntry: Boolean

    public val hasDeprecatedLegacyJsonFieldConflicts: Boolean

    public val hasFeatures: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.FieldOptions] messages.
* Retrieve it via the [com.google.protobuf.kotlin.FieldOptions.presence] extension property.
*/
public interface FieldOptionsPresence {
    public val hasCtype: Boolean

    public val hasPacked: Boolean

    public val hasJstype: Boolean

    public val hasLazy: Boolean

    public val hasUnverifiedLazy: Boolean

    public val hasDeprecated: Boolean

    public val hasWeak: Boolean

    public val hasDebugRedact: Boolean

    public val hasRetention: Boolean

    public val hasFeatures: Boolean

    public val hasFeatureSupport: Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf.kotlin.FieldOptions.EditionDefault] messages.
    * Retrieve it via the [com.google.protobuf.kotlin.FieldOptions.EditionDefault.presence] extension property.
    */
    public interface EditionDefault {
        public val hasEdition: Boolean

        public val hasValue: Boolean
    }

    /**
    * Interface providing field-presence information for [com.google.protobuf.kotlin.FieldOptions.FeatureSupport] messages.
    * Retrieve it via the [com.google.protobuf.kotlin.FieldOptions.FeatureSupport.presence] extension property.
    */
    public interface FeatureSupport {
        public val hasEditionIntroduced: Boolean

        public val hasEditionDeprecated: Boolean

        public val hasDeprecationWarning: Boolean

        public val hasEditionRemoved: Boolean
    }
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.OneofOptions] messages.
* Retrieve it via the [com.google.protobuf.kotlin.OneofOptions.presence] extension property.
*/
public interface OneofOptionsPresence {
    public val hasFeatures: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.EnumOptions] messages.
* Retrieve it via the [com.google.protobuf.kotlin.EnumOptions.presence] extension property.
*/
public interface EnumOptionsPresence {
    public val hasAllowAlias: Boolean

    public val hasDeprecated: Boolean

    public val hasDeprecatedLegacyJsonFieldConflicts: Boolean

    public val hasFeatures: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.EnumValueOptions] messages.
* Retrieve it via the [com.google.protobuf.kotlin.EnumValueOptions.presence] extension property.
*/
public interface EnumValueOptionsPresence {
    public val hasDeprecated: Boolean

    public val hasFeatures: Boolean

    public val hasDebugRedact: Boolean

    public val hasFeatureSupport: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.ServiceOptions] messages.
* Retrieve it via the [com.google.protobuf.kotlin.ServiceOptions.presence] extension property.
*/
public interface ServiceOptionsPresence {
    public val hasFeatures: Boolean

    public val hasDeprecated: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.MethodOptions] messages.
* Retrieve it via the [com.google.protobuf.kotlin.MethodOptions.presence] extension property.
*/
public interface MethodOptionsPresence {
    public val hasDeprecated: Boolean

    public val hasIdempotencyLevel: Boolean

    public val hasFeatures: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.UninterpretedOption] messages.
* Retrieve it via the [com.google.protobuf.kotlin.UninterpretedOption.presence] extension property.
*/
public interface UninterpretedOptionPresence {
    public val hasIdentifierValue: Boolean

    public val hasPositiveIntValue: Boolean

    public val hasNegativeIntValue: Boolean

    public val hasDoubleValue: Boolean

    public val hasStringValue: Boolean

    public val hasAggregateValue: Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf.kotlin.UninterpretedOption.NamePart] messages.
    * Retrieve it via the [com.google.protobuf.kotlin.UninterpretedOption.NamePart.presence] extension property.
    */
    public interface NamePart {
        public val hasNamePart: Boolean

        public val hasIsExtension: Boolean
    }
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.FeatureSet] messages.
* Retrieve it via the [com.google.protobuf.kotlin.FeatureSet.presence] extension property.
*/
public interface FeatureSetPresence {
    public val hasFieldPresence: Boolean

    public val hasEnumType: Boolean

    public val hasRepeatedFieldEncoding: Boolean

    public val hasUtf8Validation: Boolean

    public val hasMessageEncoding: Boolean

    public val hasJsonFormat: Boolean

    public val hasEnforceNamingStyle: Boolean

    public val hasDefaultSymbolVisibility: Boolean
}

/**
* Interface providing field-presence information for [com.google.protobuf.kotlin.FeatureSetDefaults] messages.
* Retrieve it via the [com.google.protobuf.kotlin.FeatureSetDefaults.presence] extension property.
*/
public interface FeatureSetDefaultsPresence {
    public val hasMinimumEdition: Boolean

    public val hasMaximumEdition: Boolean

    /**
    * Interface providing field-presence information for [com.google.protobuf.kotlin.FeatureSetDefaults.FeatureSetEditionDefault] messages.
    * Retrieve it via the [com.google.protobuf.kotlin.FeatureSetDefaults.FeatureSetEditionDefault.presence] extension property.
    */
    public interface FeatureSetEditionDefault {
        public val hasEdition: Boolean

        public val hasOverridableFeatures: Boolean

        public val hasFixedFeatures: Boolean
    }
}

public interface SourceCodeInfoPresence {
    /**
    * Interface providing field-presence information for [com.google.protobuf.kotlin.SourceCodeInfo.Location] messages.
    * Retrieve it via the [com.google.protobuf.kotlin.SourceCodeInfo.Location.presence] extension property.
    */
    public interface Location {
        public val hasLeadingComments: Boolean

        public val hasTrailingComments: Boolean
    }
}

public interface GeneratedCodeInfoPresence {
    /**
    * Interface providing field-presence information for [com.google.protobuf.kotlin.GeneratedCodeInfo.Annotation] messages.
    * Retrieve it via the [com.google.protobuf.kotlin.GeneratedCodeInfo.Annotation.presence] extension property.
    */
    public interface Annotation {
        public val hasSourceFile: Boolean

        public val hasBegin: Boolean

        public val hasEnd: Boolean

        public val hasSemantic: Boolean
    }
}
