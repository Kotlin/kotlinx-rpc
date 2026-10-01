@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
@file:Suppress("PropertyName", "CanBeVal", "ConstPropertyName", "LocalVariableName", "DuplicatedCode")

package com.google.protobuf.kotlin

import kotlin.reflect.cast
import kotlinx.io.Buffer
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtoConfig
import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.ProtobufException
import kotlinx.rpc.protobuf.internal.ExtensionValue
import kotlinx.rpc.protobuf.internal.InternalExtensionDescriptor
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.InternalPresenceObject
import kotlinx.rpc.protobuf.internal.MsgFieldDelegate
import kotlinx.rpc.protobuf.internal.ProtoDescriptor
import kotlinx.rpc.protobuf.internal.ProtoGrpcMarshaller
import kotlinx.rpc.protobuf.internal.WireDecoder
import kotlinx.rpc.protobuf.internal.WireEncoder
import kotlinx.rpc.protobuf.internal.WireSize
import kotlinx.rpc.protobuf.internal.WireType
import kotlinx.rpc.protobuf.internal.bool
import kotlinx.rpc.protobuf.internal.bytes
import kotlinx.rpc.protobuf.internal.double
import kotlinx.rpc.protobuf.internal.enum
import kotlinx.rpc.protobuf.internal.int32
import kotlinx.rpc.protobuf.internal.int64
import kotlinx.rpc.protobuf.internal.packedInt32
import kotlinx.rpc.protobuf.internal.protoToString
import kotlinx.rpc.protobuf.internal.string
import kotlinx.rpc.protobuf.internal.tag
import kotlinx.rpc.protobuf.internal.uInt64

@InternalRpcApi
public class FileDescriptorSetInternal: FileDescriptorSet.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __fileDelegate: MsgFieldDelegate<List<FileDescriptorProto>> = MsgFieldDelegate { emptyList() }
    public override var file: List<FileDescriptorProto> by __fileDelegate

    private val _owner: FileDescriptorSetInternal = this

    @InternalRpcApi
    public val _presence: FileDescriptorSetPresence = object : FileDescriptorSetPresence, InternalPresenceObject {
        public override val _message: FileDescriptorSetInternal get() = _owner
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.file.hashCode()
        result = 31 * result + extensionsHashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FileDescriptorSetInternal
        other.checkRequiredFields()
        if (this.file != other.file) return false
        return extensionsEqual(other)
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("FileDescriptorSet(")
        builder.appendLine("${nextIndentString}file=${this.file},")
        builder.appendExtensions(nextIndentString)
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): FileDescriptorSetInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: FileDescriptorSetInternal.() -> Unit): FileDescriptorSetInternal {
        val copy = FileDescriptorSetInternal()
        copy.file = this.file.map { it.copy() }
        copy.copyExtensionsFrom(this)
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<FileDescriptorSet, FileDescriptorSetInternal>() {
        public override fun asInternal(value: FileDescriptorSet): FileDescriptorSetInternal {
            return value.asInternal()
        }

        public override fun newInternal(): FileDescriptorSetInternal {
            return FileDescriptorSetInternal()
        }

        public override fun encodeWith(
            message: FileDescriptorSetInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: FileDescriptorSetInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            FileDescriptorSetInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<FileDescriptorSet> {
        public override val fullName: String = "google.protobuf.FileDescriptorSet"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: FileDescriptorSet by lazy { FileDescriptorSetInternal() }
    }
}

@InternalRpcApi
public class FileDescriptorProtoInternal: FileDescriptorProto.Builder, InternalMessage(fieldsWithPresence = 6) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val name: Int = 0
        const val `package`: Int = 1
        const val options: Int = 2
        const val sourceCodeInfo: Int = 3
        const val syntax: Int = 4
        const val edition: Int = 5
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.name) { "" }
    public override var name: String by __nameDelegate
    public override fun clearName() {
        __nameDelegate.clearField(this)
    }

    internal val __packageDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.`package`) { "" }
    public override var `package`: String by __packageDelegate
    public override fun clearPackage() {
        __packageDelegate.clearField(this)
    }

    internal val __dependencyDelegate: MsgFieldDelegate<List<String>> = MsgFieldDelegate { emptyList() }
    public override var dependency: List<String> by __dependencyDelegate
    internal val __publicDependencyDelegate: MsgFieldDelegate<List<Int>> = MsgFieldDelegate { emptyList() }
    public override var publicDependency: List<Int> by __publicDependencyDelegate
    internal val __weakDependencyDelegate: MsgFieldDelegate<List<Int>> = MsgFieldDelegate { emptyList() }
    public override var weakDependency: List<Int> by __weakDependencyDelegate
    internal val __optionDependencyDelegate: MsgFieldDelegate<List<String>> = MsgFieldDelegate { emptyList() }
    public override var optionDependency: List<String> by __optionDependencyDelegate
    internal val __messageTypeDelegate: MsgFieldDelegate<List<DescriptorProto>> = MsgFieldDelegate { emptyList() }
    public override var messageType: List<DescriptorProto> by __messageTypeDelegate
    internal val __enumTypeDelegate: MsgFieldDelegate<List<EnumDescriptorProto>> = MsgFieldDelegate { emptyList() }
    public override var enumType: List<EnumDescriptorProto> by __enumTypeDelegate
    internal val __serviceDelegate: MsgFieldDelegate<List<ServiceDescriptorProto>> = MsgFieldDelegate { emptyList() }
    public override var service: List<ServiceDescriptorProto> by __serviceDelegate
    internal val __extensionDelegate: MsgFieldDelegate<List<FieldDescriptorProto>> = MsgFieldDelegate { emptyList() }
    public override var extension: List<FieldDescriptorProto> by __extensionDelegate
    internal val __optionsDelegate: MsgFieldDelegate<FileOptions> = MsgFieldDelegate(PresenceIndices.options) { FileOptionsInternal.DEFAULT }
    public override var options: FileOptions by __optionsDelegate
    public override fun clearOptions() {
        __optionsDelegate.clearField(this)
    }

    internal val __sourceCodeInfoDelegate: MsgFieldDelegate<SourceCodeInfo> = MsgFieldDelegate(PresenceIndices.sourceCodeInfo) { SourceCodeInfoInternal.DEFAULT }
    public override var sourceCodeInfo: SourceCodeInfo by __sourceCodeInfoDelegate
    public override fun clearSourceCodeInfo() {
        __sourceCodeInfoDelegate.clearField(this)
    }

    internal val __syntaxDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.syntax) { "" }
    public override var syntax: String by __syntaxDelegate
    public override fun clearSyntax() {
        __syntaxDelegate.clearField(this)
    }

    internal val __editionDelegate: MsgFieldDelegate<Edition> = MsgFieldDelegate(PresenceIndices.edition) { Edition.EDITION_UNKNOWN }
    public override var edition: Edition by __editionDelegate

    public override fun clearEdition() {
        __editionDelegate.clearField(this)
    }

    private val _owner: FileDescriptorProtoInternal = this

    @InternalRpcApi
    public val _presence: FileDescriptorProtoPresence = object : FileDescriptorProtoPresence, InternalPresenceObject {
        public override val _message: FileDescriptorProtoInternal get() = _owner

        public override val hasName: Boolean get() = presenceMask[PresenceIndices.name]

        public override val hasPackage: Boolean get() = presenceMask[PresenceIndices.`package`]

        public override val hasOptions: Boolean get() = presenceMask[PresenceIndices.options]

        public override val hasSourceCodeInfo: Boolean get() = presenceMask[PresenceIndices.sourceCodeInfo]

        public override val hasSyntax: Boolean get() = presenceMask[PresenceIndices.syntax]

        public override val hasEdition: Boolean get() = presenceMask[PresenceIndices.edition]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.name]) this.name.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.`package`]) this.`package`.hashCode() else 0
        result = 31 * result + this.dependency.hashCode()
        result = 31 * result + this.publicDependency.hashCode()
        result = 31 * result + this.weakDependency.hashCode()
        result = 31 * result + this.optionDependency.hashCode()
        result = 31 * result + this.messageType.hashCode()
        result = 31 * result + this.enumType.hashCode()
        result = 31 * result + this.service.hashCode()
        result = 31 * result + this.extension.hashCode()
        result = 31 * result + if (presenceMask[PresenceIndices.options]) this.options.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.sourceCodeInfo]) this.sourceCodeInfo.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.syntax]) this.syntax.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.edition]) this.edition.hashCode() else 0
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FileDescriptorProtoInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.name] && this.name != other.name) return false
        if (presenceMask[PresenceIndices.`package`] && this.`package` != other.`package`) return false
        if (this.dependency != other.dependency) return false
        if (this.publicDependency != other.publicDependency) return false
        if (this.weakDependency != other.weakDependency) return false
        if (this.optionDependency != other.optionDependency) return false
        if (this.messageType != other.messageType) return false
        if (this.enumType != other.enumType) return false
        if (this.service != other.service) return false
        if (this.extension != other.extension) return false
        if (presenceMask[PresenceIndices.options] && this.options != other.options) return false
        if (presenceMask[PresenceIndices.sourceCodeInfo] && this.sourceCodeInfo != other.sourceCodeInfo) return false
        if (presenceMask[PresenceIndices.syntax] && this.syntax != other.syntax) return false
        return !presenceMask[PresenceIndices.edition] || this.edition == other.edition
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("FileDescriptorProto(")
        if (presenceMask[PresenceIndices.name]) {
            builder.appendLine("${nextIndentString}name=${this.name},")
        } else {
            builder.appendLine("${nextIndentString}name=<unset>,")
        }

        if (presenceMask[PresenceIndices.`package`]) {
            builder.appendLine("${nextIndentString}`package`=${this.`package`},")
        } else {
            builder.appendLine("${nextIndentString}`package`=<unset>,")
        }

        builder.appendLine("${nextIndentString}dependency=${this.dependency},")
        builder.appendLine("${nextIndentString}publicDependency=${this.publicDependency},")
        builder.appendLine("${nextIndentString}weakDependency=${this.weakDependency},")
        builder.appendLine("${nextIndentString}optionDependency=${this.optionDependency},")
        builder.appendLine("${nextIndentString}messageType=${this.messageType},")
        builder.appendLine("${nextIndentString}enumType=${this.enumType},")
        builder.appendLine("${nextIndentString}service=${this.service},")
        builder.appendLine("${nextIndentString}extension=${this.extension},")
        if (presenceMask[PresenceIndices.options]) {
            builder.appendLine("${nextIndentString}options=${this.options.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}options=<unset>,")
        }

        if (presenceMask[PresenceIndices.sourceCodeInfo]) {
            builder.appendLine("${nextIndentString}sourceCodeInfo=${this.sourceCodeInfo.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}sourceCodeInfo=<unset>,")
        }

        if (presenceMask[PresenceIndices.syntax]) {
            builder.appendLine("${nextIndentString}syntax=${this.syntax},")
        } else {
            builder.appendLine("${nextIndentString}syntax=<unset>,")
        }

        if (presenceMask[PresenceIndices.edition]) {
            builder.appendLine("${nextIndentString}edition=${this.edition},")
        } else {
            builder.appendLine("${nextIndentString}edition=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): FileDescriptorProtoInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(
        body: FileDescriptorProtoInternal.() -> Unit,
    ): FileDescriptorProtoInternal {
        val copy = FileDescriptorProtoInternal()
        if (presenceMask[PresenceIndices.name]) {
            copy.name = this.name
        }

        if (presenceMask[PresenceIndices.`package`]) {
            copy.`package` = this.`package`
        }

        copy.dependency = this.dependency.map { it }
        copy.publicDependency = this.publicDependency.map { it }
        copy.weakDependency = this.weakDependency.map { it }
        copy.optionDependency = this.optionDependency.map { it }
        copy.messageType = this.messageType.map { it.copy() }
        copy.enumType = this.enumType.map { it.copy() }
        copy.service = this.service.map { it.copy() }
        copy.extension = this.extension.map { it.copy() }
        if (presenceMask[PresenceIndices.options]) {
            copy.options = this.options.copy()
        }

        if (presenceMask[PresenceIndices.sourceCodeInfo]) {
            copy.sourceCodeInfo = this.sourceCodeInfo.copy()
        }

        if (presenceMask[PresenceIndices.syntax]) {
            copy.syntax = this.syntax
        }

        if (presenceMask[PresenceIndices.edition]) {
            copy.edition = this.edition
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<FileDescriptorProto, FileDescriptorProtoInternal>() {
        public override fun asInternal(value: FileDescriptorProto): FileDescriptorProtoInternal {
            return value.asInternal()
        }

        public override fun newInternal(): FileDescriptorProtoInternal {
            return FileDescriptorProtoInternal()
        }

        public override fun encodeWith(
            message: FileDescriptorProtoInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: FileDescriptorProtoInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            FileDescriptorProtoInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<FileDescriptorProto> {
        public override val fullName: String = "google.protobuf.FileDescriptorProto"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: FileDescriptorProto by lazy { FileDescriptorProtoInternal() }
    }
}

@InternalRpcApi
public class DescriptorProtoInternal: DescriptorProto.Builder, InternalMessage(fieldsWithPresence = 3) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val name: Int = 0
        const val options: Int = 1
        const val visibility: Int = 2
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.name) { "" }
    public override var name: String by __nameDelegate
    public override fun clearName() {
        __nameDelegate.clearField(this)
    }

    internal val __fieldDelegate: MsgFieldDelegate<List<FieldDescriptorProto>> = MsgFieldDelegate { emptyList() }
    public override var field: List<FieldDescriptorProto> by __fieldDelegate
    internal val __extensionDelegate: MsgFieldDelegate<List<FieldDescriptorProto>> = MsgFieldDelegate { emptyList() }
    public override var extension: List<FieldDescriptorProto> by __extensionDelegate
    internal val __nestedTypeDelegate: MsgFieldDelegate<List<DescriptorProto>> = MsgFieldDelegate { emptyList() }
    public override var nestedType: List<DescriptorProto> by __nestedTypeDelegate
    internal val __enumTypeDelegate: MsgFieldDelegate<List<EnumDescriptorProto>> = MsgFieldDelegate { emptyList() }
    public override var enumType: List<EnumDescriptorProto> by __enumTypeDelegate
    internal val __extensionRangeDelegate: MsgFieldDelegate<List<DescriptorProto.ExtensionRange>> = MsgFieldDelegate { emptyList() }
    public override var extensionRange: List<DescriptorProto.ExtensionRange> by __extensionRangeDelegate
    internal val __oneofDeclDelegate: MsgFieldDelegate<List<OneofDescriptorProto>> = MsgFieldDelegate { emptyList() }
    public override var oneofDecl: List<OneofDescriptorProto> by __oneofDeclDelegate
    internal val __optionsDelegate: MsgFieldDelegate<MessageOptions> = MsgFieldDelegate(PresenceIndices.options) { MessageOptionsInternal.DEFAULT }
    public override var options: MessageOptions by __optionsDelegate
    public override fun clearOptions() {
        __optionsDelegate.clearField(this)
    }

    internal val __reservedRangeDelegate: MsgFieldDelegate<List<DescriptorProto.ReservedRange>> = MsgFieldDelegate { emptyList() }
    public override var reservedRange: List<DescriptorProto.ReservedRange> by __reservedRangeDelegate
    internal val __reservedNameDelegate: MsgFieldDelegate<List<String>> = MsgFieldDelegate { emptyList() }
    public override var reservedName: List<String> by __reservedNameDelegate
    internal val __visibilityDelegate: MsgFieldDelegate<SymbolVisibility> = MsgFieldDelegate(PresenceIndices.visibility) { SymbolVisibility.VISIBILITY_UNSET }
    public override var visibility: SymbolVisibility by __visibilityDelegate

    public override fun clearVisibility() {
        __visibilityDelegate.clearField(this)
    }

    private val _owner: DescriptorProtoInternal = this

    @InternalRpcApi
    public val _presence: DescriptorProtoPresence = object : DescriptorProtoPresence, InternalPresenceObject {
        public override val _message: DescriptorProtoInternal get() = _owner

        public override val hasName: Boolean get() = presenceMask[PresenceIndices.name]

        public override val hasOptions: Boolean get() = presenceMask[PresenceIndices.options]

        public override val hasVisibility: Boolean get() = presenceMask[PresenceIndices.visibility]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.name]) this.name.hashCode() else 0
        result = 31 * result + this.field.hashCode()
        result = 31 * result + this.extension.hashCode()
        result = 31 * result + this.nestedType.hashCode()
        result = 31 * result + this.enumType.hashCode()
        result = 31 * result + this.extensionRange.hashCode()
        result = 31 * result + this.oneofDecl.hashCode()
        result = 31 * result + if (presenceMask[PresenceIndices.options]) this.options.hashCode() else 0
        result = 31 * result + this.reservedRange.hashCode()
        result = 31 * result + this.reservedName.hashCode()
        result = 31 * result + if (presenceMask[PresenceIndices.visibility]) this.visibility.hashCode() else 0
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as DescriptorProtoInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.name] && this.name != other.name) return false
        if (this.field != other.field) return false
        if (this.extension != other.extension) return false
        if (this.nestedType != other.nestedType) return false
        if (this.enumType != other.enumType) return false
        if (this.extensionRange != other.extensionRange) return false
        if (this.oneofDecl != other.oneofDecl) return false
        if (presenceMask[PresenceIndices.options] && this.options != other.options) return false
        if (this.reservedRange != other.reservedRange) return false
        if (this.reservedName != other.reservedName) return false
        return !presenceMask[PresenceIndices.visibility] || this.visibility == other.visibility
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("DescriptorProto(")
        if (presenceMask[PresenceIndices.name]) {
            builder.appendLine("${nextIndentString}name=${this.name},")
        } else {
            builder.appendLine("${nextIndentString}name=<unset>,")
        }

        builder.appendLine("${nextIndentString}field=${this.field},")
        builder.appendLine("${nextIndentString}extension=${this.extension},")
        builder.appendLine("${nextIndentString}nestedType=${this.nestedType},")
        builder.appendLine("${nextIndentString}enumType=${this.enumType},")
        builder.appendLine("${nextIndentString}extensionRange=${this.extensionRange},")
        builder.appendLine("${nextIndentString}oneofDecl=${this.oneofDecl},")
        if (presenceMask[PresenceIndices.options]) {
            builder.appendLine("${nextIndentString}options=${this.options.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}options=<unset>,")
        }

        builder.appendLine("${nextIndentString}reservedRange=${this.reservedRange},")
        builder.appendLine("${nextIndentString}reservedName=${this.reservedName},")
        if (presenceMask[PresenceIndices.visibility]) {
            builder.appendLine("${nextIndentString}visibility=${this.visibility},")
        } else {
            builder.appendLine("${nextIndentString}visibility=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): DescriptorProtoInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: DescriptorProtoInternal.() -> Unit): DescriptorProtoInternal {
        val copy = DescriptorProtoInternal()
        if (presenceMask[PresenceIndices.name]) {
            copy.name = this.name
        }

        copy.field = this.field.map { it.copy() }
        copy.extension = this.extension.map { it.copy() }
        copy.nestedType = this.nestedType.map { it.copy() }
        copy.enumType = this.enumType.map { it.copy() }
        copy.extensionRange = this.extensionRange.map { it.copy() }
        copy.oneofDecl = this.oneofDecl.map { it.copy() }
        if (presenceMask[PresenceIndices.options]) {
            copy.options = this.options.copy()
        }

        copy.reservedRange = this.reservedRange.map { it.copy() }
        copy.reservedName = this.reservedName.map { it }
        if (presenceMask[PresenceIndices.visibility]) {
            copy.visibility = this.visibility
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public class ExtensionRangeInternal: DescriptorProto.ExtensionRange.Builder, InternalMessage(fieldsWithPresence = 3) {
        @InternalRpcApi
        internal object PresenceIndices {
            const val start: Int = 0
            const val end: Int = 1
            const val options: Int = 2
        }

        @InternalRpcApi
        public override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        public override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __startDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.start) { 0 }
        public override var start: Int by __startDelegate
        public override fun clearStart() {
            __startDelegate.clearField(this)
        }

        internal val __endDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.end) { 0 }
        public override var end: Int by __endDelegate
        public override fun clearEnd() {
            __endDelegate.clearField(this)
        }

        internal val __optionsDelegate: MsgFieldDelegate<ExtensionRangeOptions> = MsgFieldDelegate(PresenceIndices.options) { ExtensionRangeOptionsInternal.DEFAULT }
        public override var options: ExtensionRangeOptions by __optionsDelegate

        public override fun clearOptions() {
            __optionsDelegate.clearField(this)
        }

        private val _owner: ExtensionRangeInternal = this

        @InternalRpcApi
        public val _presence: DescriptorProtoPresence.ExtensionRange = object : DescriptorProtoPresence.ExtensionRange, InternalPresenceObject {
            public override val _message: ExtensionRangeInternal get() = _owner

            public override val hasStart: Boolean get() = presenceMask[PresenceIndices.start]

            public override val hasEnd: Boolean get() = presenceMask[PresenceIndices.end]

            public override val hasOptions: Boolean get() = presenceMask[PresenceIndices.options]
        }

        public override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[PresenceIndices.start]) this.start.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.end]) this.end.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.options]) this.options.hashCode() else 0
            return result
        }

        public override fun equals(other: kotlin.Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as ExtensionRangeInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[PresenceIndices.start] && this.start != other.start) return false
            if (presenceMask[PresenceIndices.end] && this.end != other.end) return false
            return !presenceMask[PresenceIndices.options] || this.options == other.options
        }

        public override fun toString(): String {
            return asString()
        }

        public fun asString(indent: Int = 0): String {
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("DescriptorProto.ExtensionRange(")
            if (presenceMask[PresenceIndices.start]) {
                builder.appendLine("${nextIndentString}start=${this.start},")
            } else {
                builder.appendLine("${nextIndentString}start=<unset>,")
            }

            if (presenceMask[PresenceIndices.end]) {
                builder.appendLine("${nextIndentString}end=${this.end},")
            } else {
                builder.appendLine("${nextIndentString}end=<unset>,")
            }

            if (presenceMask[PresenceIndices.options]) {
                builder.appendLine("${nextIndentString}options=${this.options.asInternal().asString(indent = indent + 4)},")
            } else {
                builder.appendLine("${nextIndentString}options=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        public override fun copyInternal(): ExtensionRangeInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        public fun copyInternal(body: ExtensionRangeInternal.() -> Unit): ExtensionRangeInternal {
            val copy = ExtensionRangeInternal()
            if (presenceMask[PresenceIndices.start]) {
                copy.start = this.start
            }

            if (presenceMask[PresenceIndices.end]) {
                copy.end = this.end
            }

            if (presenceMask[PresenceIndices.options]) {
                copy.options = this.options.copy()
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        public object MARSHALLER: ProtoGrpcMarshaller<DescriptorProto.ExtensionRange, ExtensionRangeInternal>() {
            public override fun asInternal(
                value: DescriptorProto.ExtensionRange,
            ): ExtensionRangeInternal {
                return value.asInternal()
            }

            public override fun newInternal(): ExtensionRangeInternal {
                return ExtensionRangeInternal()
            }

            public override fun encodeWith(
                message: ExtensionRangeInternal,
                encoder: WireEncoder,
                config: ProtoConfig?,
            ) {
                message.encodeWith(encoder, config)
            }

            public override fun decodeWith(
                message: ExtensionRangeInternal,
                decoder: WireDecoder,
                config: ProtoConfig?,
            ) {
                ExtensionRangeInternal.decodeWith(message, decoder, config)
                message.checkRequiredFields()
            }
        }

        @InternalRpcApi
        public object DESCRIPTOR: ProtoDescriptor<DescriptorProto.ExtensionRange> {
            public override val fullName: String = "google.protobuf.DescriptorProto.ExtensionRange"
        }

        @InternalRpcApi
        public companion object {
            public val DEFAULT: DescriptorProto.ExtensionRange by lazy { ExtensionRangeInternal() }
        }
    }

    @InternalRpcApi
    public class ReservedRangeInternal: DescriptorProto.ReservedRange.Builder, InternalMessage(fieldsWithPresence = 2) {
        @InternalRpcApi
        internal object PresenceIndices {
            const val start: Int = 0
            const val end: Int = 1
        }

        @InternalRpcApi
        public override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        public override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __startDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.start) { 0 }
        public override var start: Int by __startDelegate
        public override fun clearStart() {
            __startDelegate.clearField(this)
        }

        internal val __endDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.end) { 0 }
        public override var end: Int by __endDelegate

        public override fun clearEnd() {
            __endDelegate.clearField(this)
        }

        private val _owner: ReservedRangeInternal = this

        @InternalRpcApi
        public val _presence: DescriptorProtoPresence.ReservedRange = object : DescriptorProtoPresence.ReservedRange, InternalPresenceObject {
            public override val _message: ReservedRangeInternal get() = _owner

            public override val hasStart: Boolean get() = presenceMask[PresenceIndices.start]

            public override val hasEnd: Boolean get() = presenceMask[PresenceIndices.end]
        }

        public override fun hashCode(): Int {
            var result = if (presenceMask[PresenceIndices.start]) this.start.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.end]) this.end.hashCode() else 0
            return result
        }

        public override fun equals(other: kotlin.Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as ReservedRangeInternal
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[PresenceIndices.start] && this.start != other.start) return false
            return !presenceMask[PresenceIndices.end] || this.end == other.end
        }

        public override fun toString(): String {
            return asString()
        }

        public fun asString(indent: Int = 0): String {
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("DescriptorProto.ReservedRange(")
            if (presenceMask[PresenceIndices.start]) {
                builder.appendLine("${nextIndentString}start=${this.start},")
            } else {
                builder.appendLine("${nextIndentString}start=<unset>,")
            }

            if (presenceMask[PresenceIndices.end]) {
                builder.appendLine("${nextIndentString}end=${this.end},")
            } else {
                builder.appendLine("${nextIndentString}end=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        public override fun copyInternal(): ReservedRangeInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        public fun copyInternal(body: ReservedRangeInternal.() -> Unit): ReservedRangeInternal {
            val copy = ReservedRangeInternal()
            if (presenceMask[PresenceIndices.start]) {
                copy.start = this.start
            }

            if (presenceMask[PresenceIndices.end]) {
                copy.end = this.end
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        public object MARSHALLER: ProtoGrpcMarshaller<DescriptorProto.ReservedRange, ReservedRangeInternal>() {
            public override fun asInternal(
                value: DescriptorProto.ReservedRange,
            ): ReservedRangeInternal {
                return value.asInternal()
            }

            public override fun newInternal(): ReservedRangeInternal {
                return ReservedRangeInternal()
            }

            public override fun encodeWith(
                message: ReservedRangeInternal,
                encoder: WireEncoder,
                config: ProtoConfig?,
            ) {
                message.encodeWith(encoder, config)
            }

            public override fun decodeWith(
                message: ReservedRangeInternal,
                decoder: WireDecoder,
                config: ProtoConfig?,
            ) {
                ReservedRangeInternal.decodeWith(message, decoder, config)
            }
        }

        @InternalRpcApi
        public object DESCRIPTOR: ProtoDescriptor<DescriptorProto.ReservedRange> {
            public override val fullName: String = "google.protobuf.DescriptorProto.ReservedRange"
        }

        @InternalRpcApi
        public companion object {
            public val DEFAULT: DescriptorProto.ReservedRange by lazy { ReservedRangeInternal() }
        }
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<DescriptorProto, DescriptorProtoInternal>() {
        public override fun asInternal(value: DescriptorProto): DescriptorProtoInternal {
            return value.asInternal()
        }

        public override fun newInternal(): DescriptorProtoInternal {
            return DescriptorProtoInternal()
        }

        public override fun encodeWith(
            message: DescriptorProtoInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: DescriptorProtoInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            DescriptorProtoInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<DescriptorProto> {
        public override val fullName: String = "google.protobuf.DescriptorProto"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: DescriptorProto by lazy { DescriptorProtoInternal() }
    }
}

@InternalRpcApi
public class ExtensionRangeOptionsInternal: ExtensionRangeOptions.Builder, InternalMessage(fieldsWithPresence = 2) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val features: Int = 0
        const val verification: Int = 1
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __uninterpretedOptionDelegate: MsgFieldDelegate<List<UninterpretedOption>> = MsgFieldDelegate { emptyList() }
    public override var uninterpretedOption: List<UninterpretedOption> by __uninterpretedOptionDelegate
    internal val __declarationDelegate: MsgFieldDelegate<List<ExtensionRangeOptions.Declaration>> = MsgFieldDelegate { emptyList() }
    public override var declaration: List<ExtensionRangeOptions.Declaration> by __declarationDelegate
    internal val __featuresDelegate: MsgFieldDelegate<FeatureSet> = MsgFieldDelegate(PresenceIndices.features) { FeatureSetInternal.DEFAULT }
    public override var features: FeatureSet by __featuresDelegate
    public override fun clearFeatures() {
        __featuresDelegate.clearField(this)
    }

    internal val __verificationDelegate: MsgFieldDelegate<ExtensionRangeOptions.VerificationState> = MsgFieldDelegate(PresenceIndices.verification) { ExtensionRangeOptions.VerificationState.UNVERIFIED }
    public override var verification: ExtensionRangeOptions.VerificationState by __verificationDelegate

    public override fun clearVerification() {
        __verificationDelegate.clearField(this)
    }

    private val _owner: ExtensionRangeOptionsInternal = this

    @InternalRpcApi
    public val _presence: ExtensionRangeOptionsPresence = object : ExtensionRangeOptionsPresence, InternalPresenceObject {
        public override val _message: ExtensionRangeOptionsInternal get() = _owner

        public override val hasFeatures: Boolean get() = presenceMask[PresenceIndices.features]

        public override val hasVerification: Boolean get() = presenceMask[PresenceIndices.verification]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.uninterpretedOption.hashCode()
        result = 31 * result + this.declaration.hashCode()
        result = 31 * result + if (presenceMask[PresenceIndices.features]) this.features.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.verification]) this.verification.hashCode() else 0
        result = 31 * result + extensionsHashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ExtensionRangeOptionsInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (this.uninterpretedOption != other.uninterpretedOption) return false
        if (this.declaration != other.declaration) return false
        if (presenceMask[PresenceIndices.features] && this.features != other.features) return false
        if (presenceMask[PresenceIndices.verification] && this.verification != other.verification) return false
        return extensionsEqual(other)
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("ExtensionRangeOptions(")
        builder.appendLine("${nextIndentString}uninterpretedOption=${this.uninterpretedOption},")
        builder.appendLine("${nextIndentString}declaration=${this.declaration},")
        if (presenceMask[PresenceIndices.features]) {
            builder.appendLine("${nextIndentString}features=${this.features.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}features=<unset>,")
        }

        if (presenceMask[PresenceIndices.verification]) {
            builder.appendLine("${nextIndentString}verification=${this.verification},")
        } else {
            builder.appendLine("${nextIndentString}verification=<unset>,")
        }

        builder.appendExtensions(nextIndentString)
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): ExtensionRangeOptionsInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(
        body: ExtensionRangeOptionsInternal.() -> Unit,
    ): ExtensionRangeOptionsInternal {
        val copy = ExtensionRangeOptionsInternal()
        copy.uninterpretedOption = this.uninterpretedOption.map { it.copy() }
        copy.declaration = this.declaration.map { it.copy() }
        if (presenceMask[PresenceIndices.features]) {
            copy.features = this.features.copy()
        }

        if (presenceMask[PresenceIndices.verification]) {
            copy.verification = this.verification
        }

        copy.copyExtensionsFrom(this)
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public class DeclarationInternal: ExtensionRangeOptions.Declaration.Builder, InternalMessage(fieldsWithPresence = 5) {
        @InternalRpcApi
        internal object PresenceIndices {
            const val number: Int = 0
            const val fullName: Int = 1
            const val type: Int = 2
            const val reserved: Int = 3
            const val repeated: Int = 4
        }

        @InternalRpcApi
        public override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        public override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __numberDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.number) { 0 }
        public override var number: Int by __numberDelegate
        public override fun clearNumber() {
            __numberDelegate.clearField(this)
        }

        internal val __fullNameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.fullName) { "" }
        public override var fullName: String by __fullNameDelegate
        public override fun clearFullName() {
            __fullNameDelegate.clearField(this)
        }

        internal val __typeDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.type) { "" }
        public override var type: String by __typeDelegate
        public override fun clearType() {
            __typeDelegate.clearField(this)
        }

        internal val __reservedDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.reserved) { false }
        public override var reserved: Boolean by __reservedDelegate
        public override fun clearReserved() {
            __reservedDelegate.clearField(this)
        }

        internal val __repeatedDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.repeated) { false }
        public override var repeated: Boolean by __repeatedDelegate

        public override fun clearRepeated() {
            __repeatedDelegate.clearField(this)
        }

        private val _owner: DeclarationInternal = this

        @InternalRpcApi
        public val _presence: ExtensionRangeOptionsPresence.Declaration = object : ExtensionRangeOptionsPresence.Declaration, InternalPresenceObject {
            public override val _message: DeclarationInternal get() = _owner

            public override val hasNumber: Boolean get() = presenceMask[PresenceIndices.number]

            public override val hasFullName: Boolean get() = presenceMask[PresenceIndices.fullName]

            public override val hasType: Boolean get() = presenceMask[PresenceIndices.type]

            public override val hasReserved: Boolean get() = presenceMask[PresenceIndices.reserved]

            public override val hasRepeated: Boolean get() = presenceMask[PresenceIndices.repeated]
        }

        public override fun hashCode(): Int {
            var result = if (presenceMask[PresenceIndices.number]) this.number.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.fullName]) this.fullName.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.type]) this.type.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.reserved]) this.reserved.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.repeated]) this.repeated.hashCode() else 0
            return result
        }

        public override fun equals(other: kotlin.Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as DeclarationInternal
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[PresenceIndices.number] && this.number != other.number) return false
            if (presenceMask[PresenceIndices.fullName] && this.fullName != other.fullName) return false
            if (presenceMask[PresenceIndices.type] && this.type != other.type) return false
            if (presenceMask[PresenceIndices.reserved] && this.reserved != other.reserved) return false
            return !presenceMask[PresenceIndices.repeated] || this.repeated == other.repeated
        }

        public override fun toString(): String {
            return asString()
        }

        public fun asString(indent: Int = 0): String {
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("ExtensionRangeOptions.Declaration(")
            if (presenceMask[PresenceIndices.number]) {
                builder.appendLine("${nextIndentString}number=${this.number},")
            } else {
                builder.appendLine("${nextIndentString}number=<unset>,")
            }

            if (presenceMask[PresenceIndices.fullName]) {
                builder.appendLine("${nextIndentString}fullName=${this.fullName},")
            } else {
                builder.appendLine("${nextIndentString}fullName=<unset>,")
            }

            if (presenceMask[PresenceIndices.type]) {
                builder.appendLine("${nextIndentString}type=${this.type},")
            } else {
                builder.appendLine("${nextIndentString}type=<unset>,")
            }

            if (presenceMask[PresenceIndices.reserved]) {
                builder.appendLine("${nextIndentString}reserved=${this.reserved},")
            } else {
                builder.appendLine("${nextIndentString}reserved=<unset>,")
            }

            if (presenceMask[PresenceIndices.repeated]) {
                builder.appendLine("${nextIndentString}repeated=${this.repeated},")
            } else {
                builder.appendLine("${nextIndentString}repeated=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        public override fun copyInternal(): DeclarationInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        public fun copyInternal(body: DeclarationInternal.() -> Unit): DeclarationInternal {
            val copy = DeclarationInternal()
            if (presenceMask[PresenceIndices.number]) {
                copy.number = this.number
            }

            if (presenceMask[PresenceIndices.fullName]) {
                copy.fullName = this.fullName
            }

            if (presenceMask[PresenceIndices.type]) {
                copy.type = this.type
            }

            if (presenceMask[PresenceIndices.reserved]) {
                copy.reserved = this.reserved
            }

            if (presenceMask[PresenceIndices.repeated]) {
                copy.repeated = this.repeated
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        public object MARSHALLER: ProtoGrpcMarshaller<ExtensionRangeOptions.Declaration, DeclarationInternal>() {
            public override fun asInternal(
                value: ExtensionRangeOptions.Declaration,
            ): DeclarationInternal {
                return value.asInternal()
            }

            public override fun newInternal(): DeclarationInternal {
                return DeclarationInternal()
            }

            public override fun encodeWith(
                message: DeclarationInternal,
                encoder: WireEncoder,
                config: ProtoConfig?,
            ) {
                message.encodeWith(encoder, config)
            }

            public override fun decodeWith(
                message: DeclarationInternal,
                decoder: WireDecoder,
                config: ProtoConfig?,
            ) {
                DeclarationInternal.decodeWith(message, decoder, config)
            }
        }

        @InternalRpcApi
        public object DESCRIPTOR: ProtoDescriptor<ExtensionRangeOptions.Declaration> {
            public override val fullName: String = "google.protobuf.ExtensionRangeOptions.Declaration"
        }

        @InternalRpcApi
        public companion object {
            public val DEFAULT: ExtensionRangeOptions.Declaration by lazy { DeclarationInternal() }
        }
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<ExtensionRangeOptions, ExtensionRangeOptionsInternal>() {
        public override fun asInternal(
            value: ExtensionRangeOptions,
        ): ExtensionRangeOptionsInternal {
            return value.asInternal()
        }

        public override fun newInternal(): ExtensionRangeOptionsInternal {
            return ExtensionRangeOptionsInternal()
        }

        public override fun encodeWith(
            message: ExtensionRangeOptionsInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: ExtensionRangeOptionsInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            ExtensionRangeOptionsInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<ExtensionRangeOptions> {
        public override val fullName: String = "google.protobuf.ExtensionRangeOptions"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: ExtensionRangeOptions by lazy { ExtensionRangeOptionsInternal() }
    }
}

@InternalRpcApi
public class FieldDescriptorProtoInternal: FieldDescriptorProto.Builder, InternalMessage(fieldsWithPresence = 11) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val name: Int = 0
        const val number: Int = 1
        const val label: Int = 2
        const val type: Int = 3
        const val typeName: Int = 4
        const val extendee: Int = 5
        const val defaultValue: Int = 6
        const val oneofIndex: Int = 7
        const val jsonName: Int = 8
        const val options: Int = 9
        const val proto3Optional: Int = 10
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.name) { "" }
    public override var name: String by __nameDelegate
    public override fun clearName() {
        __nameDelegate.clearField(this)
    }

    internal val __numberDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.number) { 0 }
    public override var number: Int by __numberDelegate
    public override fun clearNumber() {
        __numberDelegate.clearField(this)
    }

    internal val __labelDelegate: MsgFieldDelegate<FieldDescriptorProto.Label> = MsgFieldDelegate(PresenceIndices.label) { FieldDescriptorProto.Label.OPTIONAL }
    public override var label: FieldDescriptorProto.Label by __labelDelegate
    public override fun clearLabel() {
        __labelDelegate.clearField(this)
    }

    internal val __typeDelegate: MsgFieldDelegate<FieldDescriptorProto.Type> = MsgFieldDelegate(PresenceIndices.type) { FieldDescriptorProto.Type.DOUBLE }
    public override var type: FieldDescriptorProto.Type by __typeDelegate
    public override fun clearType() {
        __typeDelegate.clearField(this)
    }

    internal val __typeNameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.typeName) { "" }
    public override var typeName: String by __typeNameDelegate
    public override fun clearTypeName() {
        __typeNameDelegate.clearField(this)
    }

    internal val __extendeeDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.extendee) { "" }
    public override var extendee: String by __extendeeDelegate
    public override fun clearExtendee() {
        __extendeeDelegate.clearField(this)
    }

    internal val __defaultValueDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.defaultValue) { "" }
    public override var defaultValue: String by __defaultValueDelegate
    public override fun clearDefaultValue() {
        __defaultValueDelegate.clearField(this)
    }

    internal val __oneofIndexDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.oneofIndex) { 0 }
    public override var oneofIndex: Int by __oneofIndexDelegate
    public override fun clearOneofIndex() {
        __oneofIndexDelegate.clearField(this)
    }

    internal val __jsonNameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.jsonName) { "" }
    public override var jsonName: String by __jsonNameDelegate
    public override fun clearJsonName() {
        __jsonNameDelegate.clearField(this)
    }

    internal val __optionsDelegate: MsgFieldDelegate<FieldOptions> = MsgFieldDelegate(PresenceIndices.options) { FieldOptionsInternal.DEFAULT }
    public override var options: FieldOptions by __optionsDelegate
    public override fun clearOptions() {
        __optionsDelegate.clearField(this)
    }

    internal val __proto3OptionalDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.proto3Optional) { false }
    public override var proto3Optional: Boolean by __proto3OptionalDelegate

    public override fun clearProto3Optional() {
        __proto3OptionalDelegate.clearField(this)
    }

    private val _owner: FieldDescriptorProtoInternal = this

    @InternalRpcApi
    public val _presence: FieldDescriptorProtoPresence = object : FieldDescriptorProtoPresence, InternalPresenceObject {
        public override val _message: FieldDescriptorProtoInternal get() = _owner

        public override val hasName: Boolean get() = presenceMask[PresenceIndices.name]

        public override val hasNumber: Boolean get() = presenceMask[PresenceIndices.number]

        public override val hasLabel: Boolean get() = presenceMask[PresenceIndices.label]

        public override val hasType: Boolean get() = presenceMask[PresenceIndices.type]

        public override val hasTypeName: Boolean get() = presenceMask[PresenceIndices.typeName]

        public override val hasExtendee: Boolean get() = presenceMask[PresenceIndices.extendee]

        public override val hasDefaultValue: Boolean get() = presenceMask[PresenceIndices.defaultValue]

        public override val hasOneofIndex: Boolean get() = presenceMask[PresenceIndices.oneofIndex]

        public override val hasJsonName: Boolean get() = presenceMask[PresenceIndices.jsonName]

        public override val hasOptions: Boolean get() = presenceMask[PresenceIndices.options]

        public override val hasProto3Optional: Boolean get() = presenceMask[PresenceIndices.proto3Optional]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.name]) this.name.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.number]) this.number.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.label]) this.label.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.type]) this.type.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.typeName]) this.typeName.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.extendee]) this.extendee.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.defaultValue]) this.defaultValue.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.oneofIndex]) this.oneofIndex.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.jsonName]) this.jsonName.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.options]) this.options.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.proto3Optional]) this.proto3Optional.hashCode() else 0
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FieldDescriptorProtoInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.name] && this.name != other.name) return false
        if (presenceMask[PresenceIndices.number] && this.number != other.number) return false
        if (presenceMask[PresenceIndices.label] && this.label != other.label) return false
        if (presenceMask[PresenceIndices.type] && this.type != other.type) return false
        if (presenceMask[PresenceIndices.typeName] && this.typeName != other.typeName) return false
        if (presenceMask[PresenceIndices.extendee] && this.extendee != other.extendee) return false
        if (presenceMask[PresenceIndices.defaultValue] && this.defaultValue != other.defaultValue) return false
        if (presenceMask[PresenceIndices.oneofIndex] && this.oneofIndex != other.oneofIndex) return false
        if (presenceMask[PresenceIndices.jsonName] && this.jsonName != other.jsonName) return false
        if (presenceMask[PresenceIndices.options] && this.options != other.options) return false
        return !presenceMask[PresenceIndices.proto3Optional] || this.proto3Optional == other.proto3Optional
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("FieldDescriptorProto(")
        if (presenceMask[PresenceIndices.name]) {
            builder.appendLine("${nextIndentString}name=${this.name},")
        } else {
            builder.appendLine("${nextIndentString}name=<unset>,")
        }

        if (presenceMask[PresenceIndices.number]) {
            builder.appendLine("${nextIndentString}number=${this.number},")
        } else {
            builder.appendLine("${nextIndentString}number=<unset>,")
        }

        if (presenceMask[PresenceIndices.label]) {
            builder.appendLine("${nextIndentString}label=${this.label},")
        } else {
            builder.appendLine("${nextIndentString}label=<unset>,")
        }

        if (presenceMask[PresenceIndices.type]) {
            builder.appendLine("${nextIndentString}type=${this.type},")
        } else {
            builder.appendLine("${nextIndentString}type=<unset>,")
        }

        if (presenceMask[PresenceIndices.typeName]) {
            builder.appendLine("${nextIndentString}typeName=${this.typeName},")
        } else {
            builder.appendLine("${nextIndentString}typeName=<unset>,")
        }

        if (presenceMask[PresenceIndices.extendee]) {
            builder.appendLine("${nextIndentString}extendee=${this.extendee},")
        } else {
            builder.appendLine("${nextIndentString}extendee=<unset>,")
        }

        if (presenceMask[PresenceIndices.defaultValue]) {
            builder.appendLine("${nextIndentString}defaultValue=${this.defaultValue},")
        } else {
            builder.appendLine("${nextIndentString}defaultValue=<unset>,")
        }

        if (presenceMask[PresenceIndices.oneofIndex]) {
            builder.appendLine("${nextIndentString}oneofIndex=${this.oneofIndex},")
        } else {
            builder.appendLine("${nextIndentString}oneofIndex=<unset>,")
        }

        if (presenceMask[PresenceIndices.jsonName]) {
            builder.appendLine("${nextIndentString}jsonName=${this.jsonName},")
        } else {
            builder.appendLine("${nextIndentString}jsonName=<unset>,")
        }

        if (presenceMask[PresenceIndices.options]) {
            builder.appendLine("${nextIndentString}options=${this.options.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}options=<unset>,")
        }

        if (presenceMask[PresenceIndices.proto3Optional]) {
            builder.appendLine("${nextIndentString}proto3Optional=${this.proto3Optional},")
        } else {
            builder.appendLine("${nextIndentString}proto3Optional=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): FieldDescriptorProtoInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(
        body: FieldDescriptorProtoInternal.() -> Unit,
    ): FieldDescriptorProtoInternal {
        val copy = FieldDescriptorProtoInternal()
        if (presenceMask[PresenceIndices.name]) {
            copy.name = this.name
        }

        if (presenceMask[PresenceIndices.number]) {
            copy.number = this.number
        }

        if (presenceMask[PresenceIndices.label]) {
            copy.label = this.label
        }

        if (presenceMask[PresenceIndices.type]) {
            copy.type = this.type
        }

        if (presenceMask[PresenceIndices.typeName]) {
            copy.typeName = this.typeName
        }

        if (presenceMask[PresenceIndices.extendee]) {
            copy.extendee = this.extendee
        }

        if (presenceMask[PresenceIndices.defaultValue]) {
            copy.defaultValue = this.defaultValue
        }

        if (presenceMask[PresenceIndices.oneofIndex]) {
            copy.oneofIndex = this.oneofIndex
        }

        if (presenceMask[PresenceIndices.jsonName]) {
            copy.jsonName = this.jsonName
        }

        if (presenceMask[PresenceIndices.options]) {
            copy.options = this.options.copy()
        }

        if (presenceMask[PresenceIndices.proto3Optional]) {
            copy.proto3Optional = this.proto3Optional
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<FieldDescriptorProto, FieldDescriptorProtoInternal>() {
        public override fun asInternal(value: FieldDescriptorProto): FieldDescriptorProtoInternal {
            return value.asInternal()
        }

        public override fun newInternal(): FieldDescriptorProtoInternal {
            return FieldDescriptorProtoInternal()
        }

        public override fun encodeWith(
            message: FieldDescriptorProtoInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: FieldDescriptorProtoInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            FieldDescriptorProtoInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<FieldDescriptorProto> {
        public override val fullName: String = "google.protobuf.FieldDescriptorProto"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: FieldDescriptorProto by lazy { FieldDescriptorProtoInternal() }
    }
}

@InternalRpcApi
public class OneofDescriptorProtoInternal: OneofDescriptorProto.Builder, InternalMessage(fieldsWithPresence = 2) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val name: Int = 0
        const val options: Int = 1
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.name) { "" }
    public override var name: String by __nameDelegate
    public override fun clearName() {
        __nameDelegate.clearField(this)
    }

    internal val __optionsDelegate: MsgFieldDelegate<OneofOptions> = MsgFieldDelegate(PresenceIndices.options) { OneofOptionsInternal.DEFAULT }
    public override var options: OneofOptions by __optionsDelegate

    public override fun clearOptions() {
        __optionsDelegate.clearField(this)
    }

    private val _owner: OneofDescriptorProtoInternal = this

    @InternalRpcApi
    public val _presence: OneofDescriptorProtoPresence = object : OneofDescriptorProtoPresence, InternalPresenceObject {
        public override val _message: OneofDescriptorProtoInternal get() = _owner

        public override val hasName: Boolean get() = presenceMask[PresenceIndices.name]

        public override val hasOptions: Boolean get() = presenceMask[PresenceIndices.options]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.name]) this.name.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.options]) this.options.hashCode() else 0
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as OneofDescriptorProtoInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.name] && this.name != other.name) return false
        return !presenceMask[PresenceIndices.options] || this.options == other.options
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("OneofDescriptorProto(")
        if (presenceMask[PresenceIndices.name]) {
            builder.appendLine("${nextIndentString}name=${this.name},")
        } else {
            builder.appendLine("${nextIndentString}name=<unset>,")
        }

        if (presenceMask[PresenceIndices.options]) {
            builder.appendLine("${nextIndentString}options=${this.options.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}options=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): OneofDescriptorProtoInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(
        body: OneofDescriptorProtoInternal.() -> Unit,
    ): OneofDescriptorProtoInternal {
        val copy = OneofDescriptorProtoInternal()
        if (presenceMask[PresenceIndices.name]) {
            copy.name = this.name
        }

        if (presenceMask[PresenceIndices.options]) {
            copy.options = this.options.copy()
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<OneofDescriptorProto, OneofDescriptorProtoInternal>() {
        public override fun asInternal(value: OneofDescriptorProto): OneofDescriptorProtoInternal {
            return value.asInternal()
        }

        public override fun newInternal(): OneofDescriptorProtoInternal {
            return OneofDescriptorProtoInternal()
        }

        public override fun encodeWith(
            message: OneofDescriptorProtoInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: OneofDescriptorProtoInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            OneofDescriptorProtoInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<OneofDescriptorProto> {
        public override val fullName: String = "google.protobuf.OneofDescriptorProto"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: OneofDescriptorProto by lazy { OneofDescriptorProtoInternal() }
    }
}

@InternalRpcApi
public class EnumDescriptorProtoInternal: EnumDescriptorProto.Builder, InternalMessage(fieldsWithPresence = 3) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val name: Int = 0
        const val options: Int = 1
        const val visibility: Int = 2
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.name) { "" }
    public override var name: String by __nameDelegate
    public override fun clearName() {
        __nameDelegate.clearField(this)
    }

    internal val __valueDelegate: MsgFieldDelegate<List<EnumValueDescriptorProto>> = MsgFieldDelegate { emptyList() }
    public override var value: List<EnumValueDescriptorProto> by __valueDelegate
    internal val __optionsDelegate: MsgFieldDelegate<EnumOptions> = MsgFieldDelegate(PresenceIndices.options) { EnumOptionsInternal.DEFAULT }
    public override var options: EnumOptions by __optionsDelegate
    public override fun clearOptions() {
        __optionsDelegate.clearField(this)
    }

    internal val __reservedRangeDelegate: MsgFieldDelegate<List<EnumDescriptorProto.EnumReservedRange>> = MsgFieldDelegate { emptyList() }
    public override var reservedRange: List<EnumDescriptorProto.EnumReservedRange> by __reservedRangeDelegate
    internal val __reservedNameDelegate: MsgFieldDelegate<List<String>> = MsgFieldDelegate { emptyList() }
    public override var reservedName: List<String> by __reservedNameDelegate
    internal val __visibilityDelegate: MsgFieldDelegate<SymbolVisibility> = MsgFieldDelegate(PresenceIndices.visibility) { SymbolVisibility.VISIBILITY_UNSET }
    public override var visibility: SymbolVisibility by __visibilityDelegate

    public override fun clearVisibility() {
        __visibilityDelegate.clearField(this)
    }

    private val _owner: EnumDescriptorProtoInternal = this

    @InternalRpcApi
    public val _presence: EnumDescriptorProtoPresence = object : EnumDescriptorProtoPresence, InternalPresenceObject {
        public override val _message: EnumDescriptorProtoInternal get() = _owner

        public override val hasName: Boolean get() = presenceMask[PresenceIndices.name]

        public override val hasOptions: Boolean get() = presenceMask[PresenceIndices.options]

        public override val hasVisibility: Boolean get() = presenceMask[PresenceIndices.visibility]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.name]) this.name.hashCode() else 0
        result = 31 * result + this.value.hashCode()
        result = 31 * result + if (presenceMask[PresenceIndices.options]) this.options.hashCode() else 0
        result = 31 * result + this.reservedRange.hashCode()
        result = 31 * result + this.reservedName.hashCode()
        result = 31 * result + if (presenceMask[PresenceIndices.visibility]) this.visibility.hashCode() else 0
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EnumDescriptorProtoInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.name] && this.name != other.name) return false
        if (this.value != other.value) return false
        if (presenceMask[PresenceIndices.options] && this.options != other.options) return false
        if (this.reservedRange != other.reservedRange) return false
        if (this.reservedName != other.reservedName) return false
        return !presenceMask[PresenceIndices.visibility] || this.visibility == other.visibility
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("EnumDescriptorProto(")
        if (presenceMask[PresenceIndices.name]) {
            builder.appendLine("${nextIndentString}name=${this.name},")
        } else {
            builder.appendLine("${nextIndentString}name=<unset>,")
        }

        builder.appendLine("${nextIndentString}value=${this.value},")
        if (presenceMask[PresenceIndices.options]) {
            builder.appendLine("${nextIndentString}options=${this.options.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}options=<unset>,")
        }

        builder.appendLine("${nextIndentString}reservedRange=${this.reservedRange},")
        builder.appendLine("${nextIndentString}reservedName=${this.reservedName},")
        if (presenceMask[PresenceIndices.visibility]) {
            builder.appendLine("${nextIndentString}visibility=${this.visibility},")
        } else {
            builder.appendLine("${nextIndentString}visibility=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): EnumDescriptorProtoInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(
        body: EnumDescriptorProtoInternal.() -> Unit,
    ): EnumDescriptorProtoInternal {
        val copy = EnumDescriptorProtoInternal()
        if (presenceMask[PresenceIndices.name]) {
            copy.name = this.name
        }

        copy.value = this.value.map { it.copy() }
        if (presenceMask[PresenceIndices.options]) {
            copy.options = this.options.copy()
        }

        copy.reservedRange = this.reservedRange.map { it.copy() }
        copy.reservedName = this.reservedName.map { it }
        if (presenceMask[PresenceIndices.visibility]) {
            copy.visibility = this.visibility
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public class EnumReservedRangeInternal: EnumDescriptorProto.EnumReservedRange.Builder, InternalMessage(fieldsWithPresence = 2) {
        @InternalRpcApi
        internal object PresenceIndices {
            const val start: Int = 0
            const val end: Int = 1
        }

        @InternalRpcApi
        public override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        public override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __startDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.start) { 0 }
        public override var start: Int by __startDelegate
        public override fun clearStart() {
            __startDelegate.clearField(this)
        }

        internal val __endDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.end) { 0 }
        public override var end: Int by __endDelegate

        public override fun clearEnd() {
            __endDelegate.clearField(this)
        }

        private val _owner: EnumReservedRangeInternal = this

        @InternalRpcApi
        public val _presence: EnumDescriptorProtoPresence.EnumReservedRange = object : EnumDescriptorProtoPresence.EnumReservedRange, InternalPresenceObject {
            public override val _message: EnumReservedRangeInternal get() = _owner

            public override val hasStart: Boolean get() = presenceMask[PresenceIndices.start]

            public override val hasEnd: Boolean get() = presenceMask[PresenceIndices.end]
        }

        public override fun hashCode(): Int {
            var result = if (presenceMask[PresenceIndices.start]) this.start.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.end]) this.end.hashCode() else 0
            return result
        }

        public override fun equals(other: kotlin.Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as EnumReservedRangeInternal
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[PresenceIndices.start] && this.start != other.start) return false
            return !presenceMask[PresenceIndices.end] || this.end == other.end
        }

        public override fun toString(): String {
            return asString()
        }

        public fun asString(indent: Int = 0): String {
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("EnumDescriptorProto.EnumReservedRange(")
            if (presenceMask[PresenceIndices.start]) {
                builder.appendLine("${nextIndentString}start=${this.start},")
            } else {
                builder.appendLine("${nextIndentString}start=<unset>,")
            }

            if (presenceMask[PresenceIndices.end]) {
                builder.appendLine("${nextIndentString}end=${this.end},")
            } else {
                builder.appendLine("${nextIndentString}end=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        public override fun copyInternal(): EnumReservedRangeInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        public fun copyInternal(
            body: EnumReservedRangeInternal.() -> Unit,
        ): EnumReservedRangeInternal {
            val copy = EnumReservedRangeInternal()
            if (presenceMask[PresenceIndices.start]) {
                copy.start = this.start
            }

            if (presenceMask[PresenceIndices.end]) {
                copy.end = this.end
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        public object MARSHALLER: ProtoGrpcMarshaller<EnumDescriptorProto.EnumReservedRange, EnumReservedRangeInternal>() {
            public override fun asInternal(
                value: EnumDescriptorProto.EnumReservedRange,
            ): EnumReservedRangeInternal {
                return value.asInternal()
            }

            public override fun newInternal(): EnumReservedRangeInternal {
                return EnumReservedRangeInternal()
            }

            public override fun encodeWith(
                message: EnumReservedRangeInternal,
                encoder: WireEncoder,
                config: ProtoConfig?,
            ) {
                message.encodeWith(encoder, config)
            }

            public override fun decodeWith(
                message: EnumReservedRangeInternal,
                decoder: WireDecoder,
                config: ProtoConfig?,
            ) {
                EnumReservedRangeInternal.decodeWith(message, decoder, config)
            }
        }

        @InternalRpcApi
        public object DESCRIPTOR: ProtoDescriptor<EnumDescriptorProto.EnumReservedRange> {
            public override val fullName: String = "google.protobuf.EnumDescriptorProto.EnumReservedRange"
        }

        @InternalRpcApi
        public companion object {
            public val DEFAULT: EnumDescriptorProto.EnumReservedRange by lazy { EnumReservedRangeInternal() }
        }
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<EnumDescriptorProto, EnumDescriptorProtoInternal>() {
        public override fun asInternal(value: EnumDescriptorProto): EnumDescriptorProtoInternal {
            return value.asInternal()
        }

        public override fun newInternal(): EnumDescriptorProtoInternal {
            return EnumDescriptorProtoInternal()
        }

        public override fun encodeWith(
            message: EnumDescriptorProtoInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: EnumDescriptorProtoInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            EnumDescriptorProtoInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<EnumDescriptorProto> {
        public override val fullName: String = "google.protobuf.EnumDescriptorProto"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: EnumDescriptorProto by lazy { EnumDescriptorProtoInternal() }
    }
}

@InternalRpcApi
public class EnumValueDescriptorProtoInternal: EnumValueDescriptorProto.Builder, InternalMessage(fieldsWithPresence = 3) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val name: Int = 0
        const val number: Int = 1
        const val options: Int = 2
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.name) { "" }
    public override var name: String by __nameDelegate
    public override fun clearName() {
        __nameDelegate.clearField(this)
    }

    internal val __numberDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.number) { 0 }
    public override var number: Int by __numberDelegate
    public override fun clearNumber() {
        __numberDelegate.clearField(this)
    }

    internal val __optionsDelegate: MsgFieldDelegate<EnumValueOptions> = MsgFieldDelegate(PresenceIndices.options) { EnumValueOptionsInternal.DEFAULT }
    public override var options: EnumValueOptions by __optionsDelegate

    public override fun clearOptions() {
        __optionsDelegate.clearField(this)
    }

    private val _owner: EnumValueDescriptorProtoInternal = this

    @InternalRpcApi
    public val _presence: EnumValueDescriptorProtoPresence = object : EnumValueDescriptorProtoPresence, InternalPresenceObject {
        public override val _message: EnumValueDescriptorProtoInternal get() = _owner

        public override val hasName: Boolean get() = presenceMask[PresenceIndices.name]

        public override val hasNumber: Boolean get() = presenceMask[PresenceIndices.number]

        public override val hasOptions: Boolean get() = presenceMask[PresenceIndices.options]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.name]) this.name.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.number]) this.number.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.options]) this.options.hashCode() else 0
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EnumValueDescriptorProtoInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.name] && this.name != other.name) return false
        if (presenceMask[PresenceIndices.number] && this.number != other.number) return false
        return !presenceMask[PresenceIndices.options] || this.options == other.options
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("EnumValueDescriptorProto(")
        if (presenceMask[PresenceIndices.name]) {
            builder.appendLine("${nextIndentString}name=${this.name},")
        } else {
            builder.appendLine("${nextIndentString}name=<unset>,")
        }

        if (presenceMask[PresenceIndices.number]) {
            builder.appendLine("${nextIndentString}number=${this.number},")
        } else {
            builder.appendLine("${nextIndentString}number=<unset>,")
        }

        if (presenceMask[PresenceIndices.options]) {
            builder.appendLine("${nextIndentString}options=${this.options.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}options=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): EnumValueDescriptorProtoInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(
        body: EnumValueDescriptorProtoInternal.() -> Unit,
    ): EnumValueDescriptorProtoInternal {
        val copy = EnumValueDescriptorProtoInternal()
        if (presenceMask[PresenceIndices.name]) {
            copy.name = this.name
        }

        if (presenceMask[PresenceIndices.number]) {
            copy.number = this.number
        }

        if (presenceMask[PresenceIndices.options]) {
            copy.options = this.options.copy()
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<EnumValueDescriptorProto, EnumValueDescriptorProtoInternal>() {
        public override fun asInternal(
            value: EnumValueDescriptorProto,
        ): EnumValueDescriptorProtoInternal {
            return value.asInternal()
        }

        public override fun newInternal(): EnumValueDescriptorProtoInternal {
            return EnumValueDescriptorProtoInternal()
        }

        public override fun encodeWith(
            message: EnumValueDescriptorProtoInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: EnumValueDescriptorProtoInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            EnumValueDescriptorProtoInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<EnumValueDescriptorProto> {
        public override val fullName: String = "google.protobuf.EnumValueDescriptorProto"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: EnumValueDescriptorProto by lazy { EnumValueDescriptorProtoInternal() }
    }
}

@InternalRpcApi
public class ServiceDescriptorProtoInternal: ServiceDescriptorProto.Builder, InternalMessage(fieldsWithPresence = 2) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val name: Int = 0
        const val options: Int = 1
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.name) { "" }
    public override var name: String by __nameDelegate
    public override fun clearName() {
        __nameDelegate.clearField(this)
    }

    internal val __methodDelegate: MsgFieldDelegate<List<MethodDescriptorProto>> = MsgFieldDelegate { emptyList() }
    public override var method: List<MethodDescriptorProto> by __methodDelegate
    internal val __optionsDelegate: MsgFieldDelegate<ServiceOptions> = MsgFieldDelegate(PresenceIndices.options) { ServiceOptionsInternal.DEFAULT }
    public override var options: ServiceOptions by __optionsDelegate

    public override fun clearOptions() {
        __optionsDelegate.clearField(this)
    }

    private val _owner: ServiceDescriptorProtoInternal = this

    @InternalRpcApi
    public val _presence: ServiceDescriptorProtoPresence = object : ServiceDescriptorProtoPresence, InternalPresenceObject {
        public override val _message: ServiceDescriptorProtoInternal get() = _owner

        public override val hasName: Boolean get() = presenceMask[PresenceIndices.name]

        public override val hasOptions: Boolean get() = presenceMask[PresenceIndices.options]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.name]) this.name.hashCode() else 0
        result = 31 * result + this.method.hashCode()
        result = 31 * result + if (presenceMask[PresenceIndices.options]) this.options.hashCode() else 0
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ServiceDescriptorProtoInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.name] && this.name != other.name) return false
        if (this.method != other.method) return false
        return !presenceMask[PresenceIndices.options] || this.options == other.options
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("ServiceDescriptorProto(")
        if (presenceMask[PresenceIndices.name]) {
            builder.appendLine("${nextIndentString}name=${this.name},")
        } else {
            builder.appendLine("${nextIndentString}name=<unset>,")
        }

        builder.appendLine("${nextIndentString}method=${this.method},")
        if (presenceMask[PresenceIndices.options]) {
            builder.appendLine("${nextIndentString}options=${this.options.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}options=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): ServiceDescriptorProtoInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(
        body: ServiceDescriptorProtoInternal.() -> Unit,
    ): ServiceDescriptorProtoInternal {
        val copy = ServiceDescriptorProtoInternal()
        if (presenceMask[PresenceIndices.name]) {
            copy.name = this.name
        }

        copy.method = this.method.map { it.copy() }
        if (presenceMask[PresenceIndices.options]) {
            copy.options = this.options.copy()
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<ServiceDescriptorProto, ServiceDescriptorProtoInternal>() {
        public override fun asInternal(
            value: ServiceDescriptorProto,
        ): ServiceDescriptorProtoInternal {
            return value.asInternal()
        }

        public override fun newInternal(): ServiceDescriptorProtoInternal {
            return ServiceDescriptorProtoInternal()
        }

        public override fun encodeWith(
            message: ServiceDescriptorProtoInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: ServiceDescriptorProtoInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            ServiceDescriptorProtoInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<ServiceDescriptorProto> {
        public override val fullName: String = "google.protobuf.ServiceDescriptorProto"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: ServiceDescriptorProto by lazy { ServiceDescriptorProtoInternal() }
    }
}

@InternalRpcApi
public class MethodDescriptorProtoInternal: MethodDescriptorProto.Builder, InternalMessage(fieldsWithPresence = 6) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val name: Int = 0
        const val inputType: Int = 1
        const val outputType: Int = 2
        const val options: Int = 3
        const val clientStreaming: Int = 4
        const val serverStreaming: Int = 5
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.name) { "" }
    public override var name: String by __nameDelegate
    public override fun clearName() {
        __nameDelegate.clearField(this)
    }

    internal val __inputTypeDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.inputType) { "" }
    public override var inputType: String by __inputTypeDelegate
    public override fun clearInputType() {
        __inputTypeDelegate.clearField(this)
    }

    internal val __outputTypeDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.outputType) { "" }
    public override var outputType: String by __outputTypeDelegate
    public override fun clearOutputType() {
        __outputTypeDelegate.clearField(this)
    }

    internal val __optionsDelegate: MsgFieldDelegate<MethodOptions> = MsgFieldDelegate(PresenceIndices.options) { MethodOptionsInternal.DEFAULT }
    public override var options: MethodOptions by __optionsDelegate
    public override fun clearOptions() {
        __optionsDelegate.clearField(this)
    }

    internal val __clientStreamingDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.clientStreaming) { false }
    public override var clientStreaming: Boolean by __clientStreamingDelegate
    public override fun clearClientStreaming() {
        __clientStreamingDelegate.clearField(this)
    }

    internal val __serverStreamingDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.serverStreaming) { false }
    public override var serverStreaming: Boolean by __serverStreamingDelegate

    public override fun clearServerStreaming() {
        __serverStreamingDelegate.clearField(this)
    }

    private val _owner: MethodDescriptorProtoInternal = this

    @InternalRpcApi
    public val _presence: MethodDescriptorProtoPresence = object : MethodDescriptorProtoPresence, InternalPresenceObject {
        public override val _message: MethodDescriptorProtoInternal get() = _owner

        public override val hasName: Boolean get() = presenceMask[PresenceIndices.name]

        public override val hasInputType: Boolean get() = presenceMask[PresenceIndices.inputType]

        public override val hasOutputType: Boolean get() = presenceMask[PresenceIndices.outputType]

        public override val hasOptions: Boolean get() = presenceMask[PresenceIndices.options]

        public override val hasClientStreaming: Boolean get() = presenceMask[PresenceIndices.clientStreaming]

        public override val hasServerStreaming: Boolean get() = presenceMask[PresenceIndices.serverStreaming]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.name]) this.name.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.inputType]) this.inputType.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.outputType]) this.outputType.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.options]) this.options.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.clientStreaming]) this.clientStreaming.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.serverStreaming]) this.serverStreaming.hashCode() else 0
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as MethodDescriptorProtoInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.name] && this.name != other.name) return false
        if (presenceMask[PresenceIndices.inputType] && this.inputType != other.inputType) return false
        if (presenceMask[PresenceIndices.outputType] && this.outputType != other.outputType) return false
        if (presenceMask[PresenceIndices.options] && this.options != other.options) return false
        if (presenceMask[PresenceIndices.clientStreaming] && this.clientStreaming != other.clientStreaming) return false
        return !presenceMask[PresenceIndices.serverStreaming] || this.serverStreaming == other.serverStreaming
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("MethodDescriptorProto(")
        if (presenceMask[PresenceIndices.name]) {
            builder.appendLine("${nextIndentString}name=${this.name},")
        } else {
            builder.appendLine("${nextIndentString}name=<unset>,")
        }

        if (presenceMask[PresenceIndices.inputType]) {
            builder.appendLine("${nextIndentString}inputType=${this.inputType},")
        } else {
            builder.appendLine("${nextIndentString}inputType=<unset>,")
        }

        if (presenceMask[PresenceIndices.outputType]) {
            builder.appendLine("${nextIndentString}outputType=${this.outputType},")
        } else {
            builder.appendLine("${nextIndentString}outputType=<unset>,")
        }

        if (presenceMask[PresenceIndices.options]) {
            builder.appendLine("${nextIndentString}options=${this.options.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}options=<unset>,")
        }

        if (presenceMask[PresenceIndices.clientStreaming]) {
            builder.appendLine("${nextIndentString}clientStreaming=${this.clientStreaming},")
        } else {
            builder.appendLine("${nextIndentString}clientStreaming=<unset>,")
        }

        if (presenceMask[PresenceIndices.serverStreaming]) {
            builder.appendLine("${nextIndentString}serverStreaming=${this.serverStreaming},")
        } else {
            builder.appendLine("${nextIndentString}serverStreaming=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): MethodDescriptorProtoInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(
        body: MethodDescriptorProtoInternal.() -> Unit,
    ): MethodDescriptorProtoInternal {
        val copy = MethodDescriptorProtoInternal()
        if (presenceMask[PresenceIndices.name]) {
            copy.name = this.name
        }

        if (presenceMask[PresenceIndices.inputType]) {
            copy.inputType = this.inputType
        }

        if (presenceMask[PresenceIndices.outputType]) {
            copy.outputType = this.outputType
        }

        if (presenceMask[PresenceIndices.options]) {
            copy.options = this.options.copy()
        }

        if (presenceMask[PresenceIndices.clientStreaming]) {
            copy.clientStreaming = this.clientStreaming
        }

        if (presenceMask[PresenceIndices.serverStreaming]) {
            copy.serverStreaming = this.serverStreaming
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<MethodDescriptorProto, MethodDescriptorProtoInternal>() {
        public override fun asInternal(
            value: MethodDescriptorProto,
        ): MethodDescriptorProtoInternal {
            return value.asInternal()
        }

        public override fun newInternal(): MethodDescriptorProtoInternal {
            return MethodDescriptorProtoInternal()
        }

        public override fun encodeWith(
            message: MethodDescriptorProtoInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: MethodDescriptorProtoInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            MethodDescriptorProtoInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<MethodDescriptorProto> {
        public override val fullName: String = "google.protobuf.MethodDescriptorProto"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: MethodDescriptorProto by lazy { MethodDescriptorProtoInternal() }
    }
}

@InternalRpcApi
public class FileOptionsInternal: FileOptions.Builder, InternalMessage(fieldsWithPresence = 20) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val javaPackage: Int = 0
        const val javaOuterClassname: Int = 1
        const val javaMultipleFiles: Int = 2
        const val javaGenerateEqualsAndHash: Int = 3
        const val javaStringCheckUtf8: Int = 4
        const val optimizeFor: Int = 5
        const val goPackage: Int = 6
        const val ccGenericServices: Int = 7
        const val javaGenericServices: Int = 8
        const val pyGenericServices: Int = 9
        const val deprecated: Int = 10
        const val ccEnableArenas: Int = 11
        const val objcClassPrefix: Int = 12
        const val csharpNamespace: Int = 13
        const val swiftPrefix: Int = 14
        const val phpClassPrefix: Int = 15
        const val phpNamespace: Int = 16
        const val phpMetadataNamespace: Int = 17
        const val rubyPackage: Int = 18
        const val features: Int = 19
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __javaPackageDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.javaPackage) { "" }
    public override var javaPackage: String by __javaPackageDelegate
    public override fun clearJavaPackage() {
        __javaPackageDelegate.clearField(this)
    }

    internal val __javaOuterClassnameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.javaOuterClassname) { "" }
    public override var javaOuterClassname: String by __javaOuterClassnameDelegate
    public override fun clearJavaOuterClassname() {
        __javaOuterClassnameDelegate.clearField(this)
    }

    internal val __javaMultipleFilesDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.javaMultipleFiles) { false }
    public override var javaMultipleFiles: Boolean by __javaMultipleFilesDelegate
    public override fun clearJavaMultipleFiles() {
        __javaMultipleFilesDelegate.clearField(this)
    }

    internal val __javaGenerateEqualsAndHashDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.javaGenerateEqualsAndHash) { false }
    public override var javaGenerateEqualsAndHash: Boolean by __javaGenerateEqualsAndHashDelegate
    public override fun clearJavaGenerateEqualsAndHash() {
        __javaGenerateEqualsAndHashDelegate.clearField(this)
    }

    internal val __javaStringCheckUtf8Delegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.javaStringCheckUtf8) { false }
    public override var javaStringCheckUtf8: Boolean by __javaStringCheckUtf8Delegate
    public override fun clearJavaStringCheckUtf8() {
        __javaStringCheckUtf8Delegate.clearField(this)
    }

    internal val __optimizeForDelegate: MsgFieldDelegate<FileOptions.OptimizeMode> = MsgFieldDelegate(PresenceIndices.optimizeFor) { FileOptions.OptimizeMode.SPEED }
    public override var optimizeFor: FileOptions.OptimizeMode by __optimizeForDelegate
    public override fun clearOptimizeFor() {
        __optimizeForDelegate.clearField(this)
    }

    internal val __goPackageDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.goPackage) { "" }
    public override var goPackage: String by __goPackageDelegate
    public override fun clearGoPackage() {
        __goPackageDelegate.clearField(this)
    }

    internal val __ccGenericServicesDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.ccGenericServices) { false }
    public override var ccGenericServices: Boolean by __ccGenericServicesDelegate
    public override fun clearCcGenericServices() {
        __ccGenericServicesDelegate.clearField(this)
    }

    internal val __javaGenericServicesDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.javaGenericServices) { false }
    public override var javaGenericServices: Boolean by __javaGenericServicesDelegate
    public override fun clearJavaGenericServices() {
        __javaGenericServicesDelegate.clearField(this)
    }

    internal val __pyGenericServicesDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.pyGenericServices) { false }
    public override var pyGenericServices: Boolean by __pyGenericServicesDelegate
    public override fun clearPyGenericServices() {
        __pyGenericServicesDelegate.clearField(this)
    }

    internal val __deprecatedDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.deprecated) { false }
    public override var deprecated: Boolean by __deprecatedDelegate
    public override fun clearDeprecated() {
        __deprecatedDelegate.clearField(this)
    }

    internal val __ccEnableArenasDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.ccEnableArenas) { true }
    public override var ccEnableArenas: Boolean by __ccEnableArenasDelegate
    public override fun clearCcEnableArenas() {
        __ccEnableArenasDelegate.clearField(this)
    }

    internal val __objcClassPrefixDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.objcClassPrefix) { "" }
    public override var objcClassPrefix: String by __objcClassPrefixDelegate
    public override fun clearObjcClassPrefix() {
        __objcClassPrefixDelegate.clearField(this)
    }

    internal val __csharpNamespaceDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.csharpNamespace) { "" }
    public override var csharpNamespace: String by __csharpNamespaceDelegate
    public override fun clearCsharpNamespace() {
        __csharpNamespaceDelegate.clearField(this)
    }

    internal val __swiftPrefixDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.swiftPrefix) { "" }
    public override var swiftPrefix: String by __swiftPrefixDelegate
    public override fun clearSwiftPrefix() {
        __swiftPrefixDelegate.clearField(this)
    }

    internal val __phpClassPrefixDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.phpClassPrefix) { "" }
    public override var phpClassPrefix: String by __phpClassPrefixDelegate
    public override fun clearPhpClassPrefix() {
        __phpClassPrefixDelegate.clearField(this)
    }

    internal val __phpNamespaceDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.phpNamespace) { "" }
    public override var phpNamespace: String by __phpNamespaceDelegate
    public override fun clearPhpNamespace() {
        __phpNamespaceDelegate.clearField(this)
    }

    internal val __phpMetadataNamespaceDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.phpMetadataNamespace) { "" }
    public override var phpMetadataNamespace: String by __phpMetadataNamespaceDelegate
    public override fun clearPhpMetadataNamespace() {
        __phpMetadataNamespaceDelegate.clearField(this)
    }

    internal val __rubyPackageDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.rubyPackage) { "" }
    public override var rubyPackage: String by __rubyPackageDelegate
    public override fun clearRubyPackage() {
        __rubyPackageDelegate.clearField(this)
    }

    internal val __featuresDelegate: MsgFieldDelegate<FeatureSet> = MsgFieldDelegate(PresenceIndices.features) { FeatureSetInternal.DEFAULT }
    public override var features: FeatureSet by __featuresDelegate
    public override fun clearFeatures() {
        __featuresDelegate.clearField(this)
    }

    internal val __uninterpretedOptionDelegate: MsgFieldDelegate<List<UninterpretedOption>> = MsgFieldDelegate { emptyList() }
    public override var uninterpretedOption: List<UninterpretedOption> by __uninterpretedOptionDelegate

    private val _owner: FileOptionsInternal = this

    @InternalRpcApi
    public val _presence: FileOptionsPresence = object : FileOptionsPresence, InternalPresenceObject {
        public override val _message: FileOptionsInternal get() = _owner

        public override val hasJavaPackage: Boolean get() = presenceMask[PresenceIndices.javaPackage]

        public override val hasJavaOuterClassname: Boolean get() = presenceMask[PresenceIndices.javaOuterClassname]

        public override val hasJavaMultipleFiles: Boolean get() = presenceMask[PresenceIndices.javaMultipleFiles]

        public override val hasJavaGenerateEqualsAndHash: Boolean get() = presenceMask[PresenceIndices.javaGenerateEqualsAndHash]

        public override val hasJavaStringCheckUtf8: Boolean get() = presenceMask[PresenceIndices.javaStringCheckUtf8]

        public override val hasOptimizeFor: Boolean get() = presenceMask[PresenceIndices.optimizeFor]

        public override val hasGoPackage: Boolean get() = presenceMask[PresenceIndices.goPackage]

        public override val hasCcGenericServices: Boolean get() = presenceMask[PresenceIndices.ccGenericServices]

        public override val hasJavaGenericServices: Boolean get() = presenceMask[PresenceIndices.javaGenericServices]

        public override val hasPyGenericServices: Boolean get() = presenceMask[PresenceIndices.pyGenericServices]

        public override val hasDeprecated: Boolean get() = presenceMask[PresenceIndices.deprecated]

        public override val hasCcEnableArenas: Boolean get() = presenceMask[PresenceIndices.ccEnableArenas]

        public override val hasObjcClassPrefix: Boolean get() = presenceMask[PresenceIndices.objcClassPrefix]

        public override val hasCsharpNamespace: Boolean get() = presenceMask[PresenceIndices.csharpNamespace]

        public override val hasSwiftPrefix: Boolean get() = presenceMask[PresenceIndices.swiftPrefix]

        public override val hasPhpClassPrefix: Boolean get() = presenceMask[PresenceIndices.phpClassPrefix]

        public override val hasPhpNamespace: Boolean get() = presenceMask[PresenceIndices.phpNamespace]

        public override val hasPhpMetadataNamespace: Boolean get() = presenceMask[PresenceIndices.phpMetadataNamespace]

        public override val hasRubyPackage: Boolean get() = presenceMask[PresenceIndices.rubyPackage]

        public override val hasFeatures: Boolean get() = presenceMask[PresenceIndices.features]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.javaPackage]) this.javaPackage.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.javaOuterClassname]) this.javaOuterClassname.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.javaMultipleFiles]) this.javaMultipleFiles.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.javaGenerateEqualsAndHash]) this.javaGenerateEqualsAndHash.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.javaStringCheckUtf8]) this.javaStringCheckUtf8.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.optimizeFor]) this.optimizeFor.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.goPackage]) this.goPackage.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.ccGenericServices]) this.ccGenericServices.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.javaGenericServices]) this.javaGenericServices.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.pyGenericServices]) this.pyGenericServices.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.deprecated]) this.deprecated.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.ccEnableArenas]) this.ccEnableArenas.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.objcClassPrefix]) this.objcClassPrefix.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.csharpNamespace]) this.csharpNamespace.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.swiftPrefix]) this.swiftPrefix.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.phpClassPrefix]) this.phpClassPrefix.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.phpNamespace]) this.phpNamespace.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.phpMetadataNamespace]) this.phpMetadataNamespace.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.rubyPackage]) this.rubyPackage.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.features]) this.features.hashCode() else 0
        result = 31 * result + this.uninterpretedOption.hashCode()
        result = 31 * result + extensionsHashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FileOptionsInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.javaPackage] && this.javaPackage != other.javaPackage) return false
        if (presenceMask[PresenceIndices.javaOuterClassname] && this.javaOuterClassname != other.javaOuterClassname) return false
        if (presenceMask[PresenceIndices.javaMultipleFiles] && this.javaMultipleFiles != other.javaMultipleFiles) return false
        if (presenceMask[PresenceIndices.javaGenerateEqualsAndHash] && this.javaGenerateEqualsAndHash != other.javaGenerateEqualsAndHash) return false
        if (presenceMask[PresenceIndices.javaStringCheckUtf8] && this.javaStringCheckUtf8 != other.javaStringCheckUtf8) return false
        if (presenceMask[PresenceIndices.optimizeFor] && this.optimizeFor != other.optimizeFor) return false
        if (presenceMask[PresenceIndices.goPackage] && this.goPackage != other.goPackage) return false
        if (presenceMask[PresenceIndices.ccGenericServices] && this.ccGenericServices != other.ccGenericServices) return false
        if (presenceMask[PresenceIndices.javaGenericServices] && this.javaGenericServices != other.javaGenericServices) return false
        if (presenceMask[PresenceIndices.pyGenericServices] && this.pyGenericServices != other.pyGenericServices) return false
        if (presenceMask[PresenceIndices.deprecated] && this.deprecated != other.deprecated) return false
        if (presenceMask[PresenceIndices.ccEnableArenas] && this.ccEnableArenas != other.ccEnableArenas) return false
        if (presenceMask[PresenceIndices.objcClassPrefix] && this.objcClassPrefix != other.objcClassPrefix) return false
        if (presenceMask[PresenceIndices.csharpNamespace] && this.csharpNamespace != other.csharpNamespace) return false
        if (presenceMask[PresenceIndices.swiftPrefix] && this.swiftPrefix != other.swiftPrefix) return false
        if (presenceMask[PresenceIndices.phpClassPrefix] && this.phpClassPrefix != other.phpClassPrefix) return false
        if (presenceMask[PresenceIndices.phpNamespace] && this.phpNamespace != other.phpNamespace) return false
        if (presenceMask[PresenceIndices.phpMetadataNamespace] && this.phpMetadataNamespace != other.phpMetadataNamespace) return false
        if (presenceMask[PresenceIndices.rubyPackage] && this.rubyPackage != other.rubyPackage) return false
        if (presenceMask[PresenceIndices.features] && this.features != other.features) return false
        if (this.uninterpretedOption != other.uninterpretedOption) return false
        return extensionsEqual(other)
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("FileOptions(")
        if (presenceMask[PresenceIndices.javaPackage]) {
            builder.appendLine("${nextIndentString}javaPackage=${this.javaPackage},")
        } else {
            builder.appendLine("${nextIndentString}javaPackage=<unset>,")
        }

        if (presenceMask[PresenceIndices.javaOuterClassname]) {
            builder.appendLine("${nextIndentString}javaOuterClassname=${this.javaOuterClassname},")
        } else {
            builder.appendLine("${nextIndentString}javaOuterClassname=<unset>,")
        }

        if (presenceMask[PresenceIndices.javaMultipleFiles]) {
            builder.appendLine("${nextIndentString}javaMultipleFiles=${this.javaMultipleFiles},")
        } else {
            builder.appendLine("${nextIndentString}javaMultipleFiles=<unset>,")
        }

        if (presenceMask[PresenceIndices.javaGenerateEqualsAndHash]) {
            builder.appendLine("${nextIndentString}javaGenerateEqualsAndHash=${this.javaGenerateEqualsAndHash},")
        } else {
            builder.appendLine("${nextIndentString}javaGenerateEqualsAndHash=<unset>,")
        }

        if (presenceMask[PresenceIndices.javaStringCheckUtf8]) {
            builder.appendLine("${nextIndentString}javaStringCheckUtf8=${this.javaStringCheckUtf8},")
        } else {
            builder.appendLine("${nextIndentString}javaStringCheckUtf8=<unset>,")
        }

        if (presenceMask[PresenceIndices.optimizeFor]) {
            builder.appendLine("${nextIndentString}optimizeFor=${this.optimizeFor},")
        } else {
            builder.appendLine("${nextIndentString}optimizeFor=<unset>,")
        }

        if (presenceMask[PresenceIndices.goPackage]) {
            builder.appendLine("${nextIndentString}goPackage=${this.goPackage},")
        } else {
            builder.appendLine("${nextIndentString}goPackage=<unset>,")
        }

        if (presenceMask[PresenceIndices.ccGenericServices]) {
            builder.appendLine("${nextIndentString}ccGenericServices=${this.ccGenericServices},")
        } else {
            builder.appendLine("${nextIndentString}ccGenericServices=<unset>,")
        }

        if (presenceMask[PresenceIndices.javaGenericServices]) {
            builder.appendLine("${nextIndentString}javaGenericServices=${this.javaGenericServices},")
        } else {
            builder.appendLine("${nextIndentString}javaGenericServices=<unset>,")
        }

        if (presenceMask[PresenceIndices.pyGenericServices]) {
            builder.appendLine("${nextIndentString}pyGenericServices=${this.pyGenericServices},")
        } else {
            builder.appendLine("${nextIndentString}pyGenericServices=<unset>,")
        }

        if (presenceMask[PresenceIndices.deprecated]) {
            builder.appendLine("${nextIndentString}deprecated=${this.deprecated},")
        } else {
            builder.appendLine("${nextIndentString}deprecated=<unset>,")
        }

        if (presenceMask[PresenceIndices.ccEnableArenas]) {
            builder.appendLine("${nextIndentString}ccEnableArenas=${this.ccEnableArenas},")
        } else {
            builder.appendLine("${nextIndentString}ccEnableArenas=<unset>,")
        }

        if (presenceMask[PresenceIndices.objcClassPrefix]) {
            builder.appendLine("${nextIndentString}objcClassPrefix=${this.objcClassPrefix},")
        } else {
            builder.appendLine("${nextIndentString}objcClassPrefix=<unset>,")
        }

        if (presenceMask[PresenceIndices.csharpNamespace]) {
            builder.appendLine("${nextIndentString}csharpNamespace=${this.csharpNamespace},")
        } else {
            builder.appendLine("${nextIndentString}csharpNamespace=<unset>,")
        }

        if (presenceMask[PresenceIndices.swiftPrefix]) {
            builder.appendLine("${nextIndentString}swiftPrefix=${this.swiftPrefix},")
        } else {
            builder.appendLine("${nextIndentString}swiftPrefix=<unset>,")
        }

        if (presenceMask[PresenceIndices.phpClassPrefix]) {
            builder.appendLine("${nextIndentString}phpClassPrefix=${this.phpClassPrefix},")
        } else {
            builder.appendLine("${nextIndentString}phpClassPrefix=<unset>,")
        }

        if (presenceMask[PresenceIndices.phpNamespace]) {
            builder.appendLine("${nextIndentString}phpNamespace=${this.phpNamespace},")
        } else {
            builder.appendLine("${nextIndentString}phpNamespace=<unset>,")
        }

        if (presenceMask[PresenceIndices.phpMetadataNamespace]) {
            builder.appendLine("${nextIndentString}phpMetadataNamespace=${this.phpMetadataNamespace},")
        } else {
            builder.appendLine("${nextIndentString}phpMetadataNamespace=<unset>,")
        }

        if (presenceMask[PresenceIndices.rubyPackage]) {
            builder.appendLine("${nextIndentString}rubyPackage=${this.rubyPackage},")
        } else {
            builder.appendLine("${nextIndentString}rubyPackage=<unset>,")
        }

        if (presenceMask[PresenceIndices.features]) {
            builder.appendLine("${nextIndentString}features=${this.features.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}features=<unset>,")
        }

        builder.appendLine("${nextIndentString}uninterpretedOption=${this.uninterpretedOption},")
        builder.appendExtensions(nextIndentString)
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): FileOptionsInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: FileOptionsInternal.() -> Unit): FileOptionsInternal {
        val copy = FileOptionsInternal()
        if (presenceMask[PresenceIndices.javaPackage]) {
            copy.javaPackage = this.javaPackage
        }

        if (presenceMask[PresenceIndices.javaOuterClassname]) {
            copy.javaOuterClassname = this.javaOuterClassname
        }

        if (presenceMask[PresenceIndices.javaMultipleFiles]) {
            copy.javaMultipleFiles = this.javaMultipleFiles
        }

        if (presenceMask[PresenceIndices.javaGenerateEqualsAndHash]) {
            copy.javaGenerateEqualsAndHash = this.javaGenerateEqualsAndHash
        }

        if (presenceMask[PresenceIndices.javaStringCheckUtf8]) {
            copy.javaStringCheckUtf8 = this.javaStringCheckUtf8
        }

        if (presenceMask[PresenceIndices.optimizeFor]) {
            copy.optimizeFor = this.optimizeFor
        }

        if (presenceMask[PresenceIndices.goPackage]) {
            copy.goPackage = this.goPackage
        }

        if (presenceMask[PresenceIndices.ccGenericServices]) {
            copy.ccGenericServices = this.ccGenericServices
        }

        if (presenceMask[PresenceIndices.javaGenericServices]) {
            copy.javaGenericServices = this.javaGenericServices
        }

        if (presenceMask[PresenceIndices.pyGenericServices]) {
            copy.pyGenericServices = this.pyGenericServices
        }

        if (presenceMask[PresenceIndices.deprecated]) {
            copy.deprecated = this.deprecated
        }

        if (presenceMask[PresenceIndices.ccEnableArenas]) {
            copy.ccEnableArenas = this.ccEnableArenas
        }

        if (presenceMask[PresenceIndices.objcClassPrefix]) {
            copy.objcClassPrefix = this.objcClassPrefix
        }

        if (presenceMask[PresenceIndices.csharpNamespace]) {
            copy.csharpNamespace = this.csharpNamespace
        }

        if (presenceMask[PresenceIndices.swiftPrefix]) {
            copy.swiftPrefix = this.swiftPrefix
        }

        if (presenceMask[PresenceIndices.phpClassPrefix]) {
            copy.phpClassPrefix = this.phpClassPrefix
        }

        if (presenceMask[PresenceIndices.phpNamespace]) {
            copy.phpNamespace = this.phpNamespace
        }

        if (presenceMask[PresenceIndices.phpMetadataNamespace]) {
            copy.phpMetadataNamespace = this.phpMetadataNamespace
        }

        if (presenceMask[PresenceIndices.rubyPackage]) {
            copy.rubyPackage = this.rubyPackage
        }

        if (presenceMask[PresenceIndices.features]) {
            copy.features = this.features.copy()
        }

        copy.uninterpretedOption = this.uninterpretedOption.map { it.copy() }
        copy.copyExtensionsFrom(this)
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<FileOptions, FileOptionsInternal>() {
        public override fun asInternal(value: FileOptions): FileOptionsInternal {
            return value.asInternal()
        }

        public override fun newInternal(): FileOptionsInternal {
            return FileOptionsInternal()
        }

        public override fun encodeWith(
            message: FileOptionsInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: FileOptionsInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            FileOptionsInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<FileOptions> {
        public override val fullName: String = "google.protobuf.FileOptions"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: FileOptions by lazy { FileOptionsInternal() }
    }
}

@InternalRpcApi
public class MessageOptionsInternal: MessageOptions.Builder, InternalMessage(fieldsWithPresence = 6) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val messageSetWireFormat: Int = 0
        const val noStandardDescriptorAccessor: Int = 1
        const val deprecated: Int = 2
        const val mapEntry: Int = 3
        const val deprecatedLegacyJsonFieldConflicts: Int = 4
        const val features: Int = 5
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __messageSetWireFormatDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.messageSetWireFormat) { false }
    public override var messageSetWireFormat: Boolean by __messageSetWireFormatDelegate
    public override fun clearMessageSetWireFormat() {
        __messageSetWireFormatDelegate.clearField(this)
    }

    internal val __noStandardDescriptorAccessorDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.noStandardDescriptorAccessor) { false }
    public override var noStandardDescriptorAccessor: Boolean by __noStandardDescriptorAccessorDelegate
    public override fun clearNoStandardDescriptorAccessor() {
        __noStandardDescriptorAccessorDelegate.clearField(this)
    }

    internal val __deprecatedDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.deprecated) { false }
    public override var deprecated: Boolean by __deprecatedDelegate
    public override fun clearDeprecated() {
        __deprecatedDelegate.clearField(this)
    }

    internal val __mapEntryDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.mapEntry) { false }
    public override var mapEntry: Boolean by __mapEntryDelegate
    public override fun clearMapEntry() {
        __mapEntryDelegate.clearField(this)
    }

    internal val __deprecatedLegacyJsonFieldConflictsDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.deprecatedLegacyJsonFieldConflicts) { false }
    public override var deprecatedLegacyJsonFieldConflicts: Boolean by __deprecatedLegacyJsonFieldConflictsDelegate
    public override fun clearDeprecatedLegacyJsonFieldConflicts() {
        __deprecatedLegacyJsonFieldConflictsDelegate.clearField(this)
    }

    internal val __featuresDelegate: MsgFieldDelegate<FeatureSet> = MsgFieldDelegate(PresenceIndices.features) { FeatureSetInternal.DEFAULT }
    public override var features: FeatureSet by __featuresDelegate
    public override fun clearFeatures() {
        __featuresDelegate.clearField(this)
    }

    internal val __uninterpretedOptionDelegate: MsgFieldDelegate<List<UninterpretedOption>> = MsgFieldDelegate { emptyList() }
    public override var uninterpretedOption: List<UninterpretedOption> by __uninterpretedOptionDelegate

    private val _owner: MessageOptionsInternal = this

    @InternalRpcApi
    public val _presence: MessageOptionsPresence = object : MessageOptionsPresence, InternalPresenceObject {
        public override val _message: MessageOptionsInternal get() = _owner

        public override val hasMessageSetWireFormat: Boolean get() = presenceMask[PresenceIndices.messageSetWireFormat]

        public override val hasNoStandardDescriptorAccessor: Boolean get() = presenceMask[PresenceIndices.noStandardDescriptorAccessor]

        public override val hasDeprecated: Boolean get() = presenceMask[PresenceIndices.deprecated]

        public override val hasMapEntry: Boolean get() = presenceMask[PresenceIndices.mapEntry]

        public override val hasDeprecatedLegacyJsonFieldConflicts: Boolean get() = presenceMask[PresenceIndices.deprecatedLegacyJsonFieldConflicts]

        public override val hasFeatures: Boolean get() = presenceMask[PresenceIndices.features]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.messageSetWireFormat]) this.messageSetWireFormat.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.noStandardDescriptorAccessor]) this.noStandardDescriptorAccessor.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.deprecated]) this.deprecated.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.mapEntry]) this.mapEntry.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.deprecatedLegacyJsonFieldConflicts]) this.deprecatedLegacyJsonFieldConflicts.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.features]) this.features.hashCode() else 0
        result = 31 * result + this.uninterpretedOption.hashCode()
        result = 31 * result + extensionsHashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as MessageOptionsInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.messageSetWireFormat] && this.messageSetWireFormat != other.messageSetWireFormat) return false
        if (presenceMask[PresenceIndices.noStandardDescriptorAccessor] && this.noStandardDescriptorAccessor != other.noStandardDescriptorAccessor) return false
        if (presenceMask[PresenceIndices.deprecated] && this.deprecated != other.deprecated) return false
        if (presenceMask[PresenceIndices.mapEntry] && this.mapEntry != other.mapEntry) return false
        if (presenceMask[PresenceIndices.deprecatedLegacyJsonFieldConflicts] && this.deprecatedLegacyJsonFieldConflicts != other.deprecatedLegacyJsonFieldConflicts) return false
        if (presenceMask[PresenceIndices.features] && this.features != other.features) return false
        if (this.uninterpretedOption != other.uninterpretedOption) return false
        return extensionsEqual(other)
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("MessageOptions(")
        if (presenceMask[PresenceIndices.messageSetWireFormat]) {
            builder.appendLine("${nextIndentString}messageSetWireFormat=${this.messageSetWireFormat},")
        } else {
            builder.appendLine("${nextIndentString}messageSetWireFormat=<unset>,")
        }

        if (presenceMask[PresenceIndices.noStandardDescriptorAccessor]) {
            builder.appendLine("${nextIndentString}noStandardDescriptorAccessor=${this.noStandardDescriptorAccessor},")
        } else {
            builder.appendLine("${nextIndentString}noStandardDescriptorAccessor=<unset>,")
        }

        if (presenceMask[PresenceIndices.deprecated]) {
            builder.appendLine("${nextIndentString}deprecated=${this.deprecated},")
        } else {
            builder.appendLine("${nextIndentString}deprecated=<unset>,")
        }

        if (presenceMask[PresenceIndices.mapEntry]) {
            builder.appendLine("${nextIndentString}mapEntry=${this.mapEntry},")
        } else {
            builder.appendLine("${nextIndentString}mapEntry=<unset>,")
        }

        if (presenceMask[PresenceIndices.deprecatedLegacyJsonFieldConflicts]) {
            builder.appendLine("${nextIndentString}deprecatedLegacyJsonFieldConflicts=${this.deprecatedLegacyJsonFieldConflicts},")
        } else {
            builder.appendLine("${nextIndentString}deprecatedLegacyJsonFieldConflicts=<unset>,")
        }

        if (presenceMask[PresenceIndices.features]) {
            builder.appendLine("${nextIndentString}features=${this.features.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}features=<unset>,")
        }

        builder.appendLine("${nextIndentString}uninterpretedOption=${this.uninterpretedOption},")
        builder.appendExtensions(nextIndentString)
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): MessageOptionsInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: MessageOptionsInternal.() -> Unit): MessageOptionsInternal {
        val copy = MessageOptionsInternal()
        if (presenceMask[PresenceIndices.messageSetWireFormat]) {
            copy.messageSetWireFormat = this.messageSetWireFormat
        }

        if (presenceMask[PresenceIndices.noStandardDescriptorAccessor]) {
            copy.noStandardDescriptorAccessor = this.noStandardDescriptorAccessor
        }

        if (presenceMask[PresenceIndices.deprecated]) {
            copy.deprecated = this.deprecated
        }

        if (presenceMask[PresenceIndices.mapEntry]) {
            copy.mapEntry = this.mapEntry
        }

        if (presenceMask[PresenceIndices.deprecatedLegacyJsonFieldConflicts]) {
            copy.deprecatedLegacyJsonFieldConflicts = this.deprecatedLegacyJsonFieldConflicts
        }

        if (presenceMask[PresenceIndices.features]) {
            copy.features = this.features.copy()
        }

        copy.uninterpretedOption = this.uninterpretedOption.map { it.copy() }
        copy.copyExtensionsFrom(this)
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<MessageOptions, MessageOptionsInternal>() {
        public override fun asInternal(value: MessageOptions): MessageOptionsInternal {
            return value.asInternal()
        }

        public override fun newInternal(): MessageOptionsInternal {
            return MessageOptionsInternal()
        }

        public override fun encodeWith(
            message: MessageOptionsInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: MessageOptionsInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            MessageOptionsInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<MessageOptions> {
        public override val fullName: String = "google.protobuf.MessageOptions"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: MessageOptions by lazy { MessageOptionsInternal() }
    }
}

@InternalRpcApi
public class FieldOptionsInternal: FieldOptions.Builder, InternalMessage(fieldsWithPresence = 11) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val ctype: Int = 0
        const val packed: Int = 1
        const val jstype: Int = 2
        const val lazy: Int = 3
        const val unverifiedLazy: Int = 4
        const val deprecated: Int = 5
        const val weak: Int = 6
        const val debugRedact: Int = 7
        const val retention: Int = 8
        const val features: Int = 9
        const val featureSupport: Int = 10
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __ctypeDelegate: MsgFieldDelegate<FieldOptions.CType> = MsgFieldDelegate(PresenceIndices.ctype) { FieldOptions.CType.STRING }
    public override var ctype: FieldOptions.CType by __ctypeDelegate
    public override fun clearCtype() {
        __ctypeDelegate.clearField(this)
    }

    internal val __packedDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.packed) { false }
    public override var packed: Boolean by __packedDelegate
    public override fun clearPacked() {
        __packedDelegate.clearField(this)
    }

    internal val __jstypeDelegate: MsgFieldDelegate<FieldOptions.JSType> = MsgFieldDelegate(PresenceIndices.jstype) { FieldOptions.JSType.JS_NORMAL }
    public override var jstype: FieldOptions.JSType by __jstypeDelegate
    public override fun clearJstype() {
        __jstypeDelegate.clearField(this)
    }

    internal val __lazyDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.lazy) { false }
    public override var lazy: Boolean by __lazyDelegate
    public override fun clearLazy() {
        __lazyDelegate.clearField(this)
    }

    internal val __unverifiedLazyDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.unverifiedLazy) { false }
    public override var unverifiedLazy: Boolean by __unverifiedLazyDelegate
    public override fun clearUnverifiedLazy() {
        __unverifiedLazyDelegate.clearField(this)
    }

    internal val __deprecatedDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.deprecated) { false }
    public override var deprecated: Boolean by __deprecatedDelegate
    public override fun clearDeprecated() {
        __deprecatedDelegate.clearField(this)
    }

    internal val __weakDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.weak) { false }
    public override var weak: Boolean by __weakDelegate
    public override fun clearWeak() {
        __weakDelegate.clearField(this)
    }

    internal val __debugRedactDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.debugRedact) { false }
    public override var debugRedact: Boolean by __debugRedactDelegate
    public override fun clearDebugRedact() {
        __debugRedactDelegate.clearField(this)
    }

    internal val __retentionDelegate: MsgFieldDelegate<FieldOptions.OptionRetention> = MsgFieldDelegate(PresenceIndices.retention) { FieldOptions.OptionRetention.RETENTION_UNKNOWN }
    public override var retention: FieldOptions.OptionRetention by __retentionDelegate
    public override fun clearRetention() {
        __retentionDelegate.clearField(this)
    }

    internal val __targetsDelegate: MsgFieldDelegate<List<FieldOptions.OptionTargetType>> = MsgFieldDelegate { emptyList() }
    public override var targets: List<FieldOptions.OptionTargetType> by __targetsDelegate
    internal val __editionDefaultsDelegate: MsgFieldDelegate<List<FieldOptions.EditionDefault>> = MsgFieldDelegate { emptyList() }
    public override var editionDefaults: List<FieldOptions.EditionDefault> by __editionDefaultsDelegate
    internal val __featuresDelegate: MsgFieldDelegate<FeatureSet> = MsgFieldDelegate(PresenceIndices.features) { FeatureSetInternal.DEFAULT }
    public override var features: FeatureSet by __featuresDelegate
    public override fun clearFeatures() {
        __featuresDelegate.clearField(this)
    }

    internal val __featureSupportDelegate: MsgFieldDelegate<FieldOptions.FeatureSupport> = MsgFieldDelegate(PresenceIndices.featureSupport) { FeatureSupportInternal.DEFAULT }
    public override var featureSupport: FieldOptions.FeatureSupport by __featureSupportDelegate
    public override fun clearFeatureSupport() {
        __featureSupportDelegate.clearField(this)
    }

    internal val __uninterpretedOptionDelegate: MsgFieldDelegate<List<UninterpretedOption>> = MsgFieldDelegate { emptyList() }
    public override var uninterpretedOption: List<UninterpretedOption> by __uninterpretedOptionDelegate

    private val _owner: FieldOptionsInternal = this

    @InternalRpcApi
    public val _presence: FieldOptionsPresence = object : FieldOptionsPresence, InternalPresenceObject {
        public override val _message: FieldOptionsInternal get() = _owner

        public override val hasCtype: Boolean get() = presenceMask[PresenceIndices.ctype]

        public override val hasPacked: Boolean get() = presenceMask[PresenceIndices.packed]

        public override val hasJstype: Boolean get() = presenceMask[PresenceIndices.jstype]

        public override val hasLazy: Boolean get() = presenceMask[PresenceIndices.lazy]

        public override val hasUnverifiedLazy: Boolean get() = presenceMask[PresenceIndices.unverifiedLazy]

        public override val hasDeprecated: Boolean get() = presenceMask[PresenceIndices.deprecated]

        public override val hasWeak: Boolean get() = presenceMask[PresenceIndices.weak]

        public override val hasDebugRedact: Boolean get() = presenceMask[PresenceIndices.debugRedact]

        public override val hasRetention: Boolean get() = presenceMask[PresenceIndices.retention]

        public override val hasFeatures: Boolean get() = presenceMask[PresenceIndices.features]

        public override val hasFeatureSupport: Boolean get() = presenceMask[PresenceIndices.featureSupport]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.ctype]) this.ctype.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.packed]) this.packed.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.jstype]) this.jstype.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.lazy]) this.lazy.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.unverifiedLazy]) this.unverifiedLazy.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.deprecated]) this.deprecated.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.weak]) this.weak.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.debugRedact]) this.debugRedact.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.retention]) this.retention.hashCode() else 0
        result = 31 * result + this.targets.hashCode()
        result = 31 * result + this.editionDefaults.hashCode()
        result = 31 * result + if (presenceMask[PresenceIndices.features]) this.features.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.featureSupport]) this.featureSupport.hashCode() else 0
        result = 31 * result + this.uninterpretedOption.hashCode()
        result = 31 * result + extensionsHashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FieldOptionsInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.ctype] && this.ctype != other.ctype) return false
        if (presenceMask[PresenceIndices.packed] && this.packed != other.packed) return false
        if (presenceMask[PresenceIndices.jstype] && this.jstype != other.jstype) return false
        if (presenceMask[PresenceIndices.lazy] && this.lazy != other.lazy) return false
        if (presenceMask[PresenceIndices.unverifiedLazy] && this.unverifiedLazy != other.unverifiedLazy) return false
        if (presenceMask[PresenceIndices.deprecated] && this.deprecated != other.deprecated) return false
        if (presenceMask[PresenceIndices.weak] && this.weak != other.weak) return false
        if (presenceMask[PresenceIndices.debugRedact] && this.debugRedact != other.debugRedact) return false
        if (presenceMask[PresenceIndices.retention] && this.retention != other.retention) return false
        if (this.targets != other.targets) return false
        if (this.editionDefaults != other.editionDefaults) return false
        if (presenceMask[PresenceIndices.features] && this.features != other.features) return false
        if (presenceMask[PresenceIndices.featureSupport] && this.featureSupport != other.featureSupport) return false
        if (this.uninterpretedOption != other.uninterpretedOption) return false
        return extensionsEqual(other)
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("FieldOptions(")
        if (presenceMask[PresenceIndices.ctype]) {
            builder.appendLine("${nextIndentString}ctype=${this.ctype},")
        } else {
            builder.appendLine("${nextIndentString}ctype=<unset>,")
        }

        if (presenceMask[PresenceIndices.packed]) {
            builder.appendLine("${nextIndentString}packed=${this.packed},")
        } else {
            builder.appendLine("${nextIndentString}packed=<unset>,")
        }

        if (presenceMask[PresenceIndices.jstype]) {
            builder.appendLine("${nextIndentString}jstype=${this.jstype},")
        } else {
            builder.appendLine("${nextIndentString}jstype=<unset>,")
        }

        if (presenceMask[PresenceIndices.lazy]) {
            builder.appendLine("${nextIndentString}lazy=${this.lazy},")
        } else {
            builder.appendLine("${nextIndentString}lazy=<unset>,")
        }

        if (presenceMask[PresenceIndices.unverifiedLazy]) {
            builder.appendLine("${nextIndentString}unverifiedLazy=${this.unverifiedLazy},")
        } else {
            builder.appendLine("${nextIndentString}unverifiedLazy=<unset>,")
        }

        if (presenceMask[PresenceIndices.deprecated]) {
            builder.appendLine("${nextIndentString}deprecated=${this.deprecated},")
        } else {
            builder.appendLine("${nextIndentString}deprecated=<unset>,")
        }

        if (presenceMask[PresenceIndices.weak]) {
            builder.appendLine("${nextIndentString}weak=${this.weak},")
        } else {
            builder.appendLine("${nextIndentString}weak=<unset>,")
        }

        if (presenceMask[PresenceIndices.debugRedact]) {
            builder.appendLine("${nextIndentString}debugRedact=${this.debugRedact},")
        } else {
            builder.appendLine("${nextIndentString}debugRedact=<unset>,")
        }

        if (presenceMask[PresenceIndices.retention]) {
            builder.appendLine("${nextIndentString}retention=${this.retention},")
        } else {
            builder.appendLine("${nextIndentString}retention=<unset>,")
        }

        builder.appendLine("${nextIndentString}targets=${this.targets},")
        builder.appendLine("${nextIndentString}editionDefaults=${this.editionDefaults},")
        if (presenceMask[PresenceIndices.features]) {
            builder.appendLine("${nextIndentString}features=${this.features.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}features=<unset>,")
        }

        if (presenceMask[PresenceIndices.featureSupport]) {
            builder.appendLine("${nextIndentString}featureSupport=${this.featureSupport.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}featureSupport=<unset>,")
        }

        builder.appendLine("${nextIndentString}uninterpretedOption=${this.uninterpretedOption},")
        builder.appendExtensions(nextIndentString)
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): FieldOptionsInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: FieldOptionsInternal.() -> Unit): FieldOptionsInternal {
        val copy = FieldOptionsInternal()
        if (presenceMask[PresenceIndices.ctype]) {
            copy.ctype = this.ctype
        }

        if (presenceMask[PresenceIndices.packed]) {
            copy.packed = this.packed
        }

        if (presenceMask[PresenceIndices.jstype]) {
            copy.jstype = this.jstype
        }

        if (presenceMask[PresenceIndices.lazy]) {
            copy.lazy = this.lazy
        }

        if (presenceMask[PresenceIndices.unverifiedLazy]) {
            copy.unverifiedLazy = this.unverifiedLazy
        }

        if (presenceMask[PresenceIndices.deprecated]) {
            copy.deprecated = this.deprecated
        }

        if (presenceMask[PresenceIndices.weak]) {
            copy.weak = this.weak
        }

        if (presenceMask[PresenceIndices.debugRedact]) {
            copy.debugRedact = this.debugRedact
        }

        if (presenceMask[PresenceIndices.retention]) {
            copy.retention = this.retention
        }

        copy.targets = this.targets.map { it }
        copy.editionDefaults = this.editionDefaults.map { it.copy() }
        if (presenceMask[PresenceIndices.features]) {
            copy.features = this.features.copy()
        }

        if (presenceMask[PresenceIndices.featureSupport]) {
            copy.featureSupport = this.featureSupport.copy()
        }

        copy.uninterpretedOption = this.uninterpretedOption.map { it.copy() }
        copy.copyExtensionsFrom(this)
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public class EditionDefaultInternal: FieldOptions.EditionDefault.Builder, InternalMessage(fieldsWithPresence = 2) {
        @InternalRpcApi
        internal object PresenceIndices {
            const val edition: Int = 0
            const val value: Int = 1
        }

        @InternalRpcApi
        public override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        public override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __editionDelegate: MsgFieldDelegate<Edition> = MsgFieldDelegate(PresenceIndices.edition) { Edition.EDITION_UNKNOWN }
        public override var edition: Edition by __editionDelegate
        public override fun clearEdition() {
            __editionDelegate.clearField(this)
        }

        internal val __valueDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.value) { "" }
        public override var value: String by __valueDelegate

        public override fun clearValue() {
            __valueDelegate.clearField(this)
        }

        private val _owner: EditionDefaultInternal = this

        @InternalRpcApi
        public val _presence: FieldOptionsPresence.EditionDefault = object : FieldOptionsPresence.EditionDefault, InternalPresenceObject {
            public override val _message: EditionDefaultInternal get() = _owner

            public override val hasEdition: Boolean get() = presenceMask[PresenceIndices.edition]

            public override val hasValue: Boolean get() = presenceMask[PresenceIndices.value]
        }

        public override fun hashCode(): Int {
            var result = if (presenceMask[PresenceIndices.edition]) this.edition.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.value]) this.value.hashCode() else 0
            return result
        }

        public override fun equals(other: kotlin.Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as EditionDefaultInternal
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[PresenceIndices.edition] && this.edition != other.edition) return false
            return !presenceMask[PresenceIndices.value] || this.value == other.value
        }

        public override fun toString(): String {
            return asString()
        }

        public fun asString(indent: Int = 0): String {
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("FieldOptions.EditionDefault(")
            if (presenceMask[PresenceIndices.edition]) {
                builder.appendLine("${nextIndentString}edition=${this.edition},")
            } else {
                builder.appendLine("${nextIndentString}edition=<unset>,")
            }

            if (presenceMask[PresenceIndices.value]) {
                builder.appendLine("${nextIndentString}value=${this.value},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        public override fun copyInternal(): EditionDefaultInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        public fun copyInternal(body: EditionDefaultInternal.() -> Unit): EditionDefaultInternal {
            val copy = EditionDefaultInternal()
            if (presenceMask[PresenceIndices.edition]) {
                copy.edition = this.edition
            }

            if (presenceMask[PresenceIndices.value]) {
                copy.value = this.value
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        public object MARSHALLER: ProtoGrpcMarshaller<FieldOptions.EditionDefault, EditionDefaultInternal>() {
            public override fun asInternal(
                value: FieldOptions.EditionDefault,
            ): EditionDefaultInternal {
                return value.asInternal()
            }

            public override fun newInternal(): EditionDefaultInternal {
                return EditionDefaultInternal()
            }

            public override fun encodeWith(
                message: EditionDefaultInternal,
                encoder: WireEncoder,
                config: ProtoConfig?,
            ) {
                message.encodeWith(encoder, config)
            }

            public override fun decodeWith(
                message: EditionDefaultInternal,
                decoder: WireDecoder,
                config: ProtoConfig?,
            ) {
                EditionDefaultInternal.decodeWith(message, decoder, config)
            }
        }

        @InternalRpcApi
        public object DESCRIPTOR: ProtoDescriptor<FieldOptions.EditionDefault> {
            public override val fullName: String = "google.protobuf.FieldOptions.EditionDefault"
        }

        @InternalRpcApi
        public companion object {
            public val DEFAULT: FieldOptions.EditionDefault by lazy { EditionDefaultInternal() }
        }
    }

    @InternalRpcApi
    public class FeatureSupportInternal: FieldOptions.FeatureSupport.Builder, InternalMessage(fieldsWithPresence = 4) {
        @InternalRpcApi
        internal object PresenceIndices {
            const val editionIntroduced: Int = 0
            const val editionDeprecated: Int = 1
            const val deprecationWarning: Int = 2
            const val editionRemoved: Int = 3
        }

        @InternalRpcApi
        public override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        public override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __editionIntroducedDelegate: MsgFieldDelegate<Edition> = MsgFieldDelegate(PresenceIndices.editionIntroduced) { Edition.EDITION_UNKNOWN }
        public override var editionIntroduced: Edition by __editionIntroducedDelegate
        public override fun clearEditionIntroduced() {
            __editionIntroducedDelegate.clearField(this)
        }

        internal val __editionDeprecatedDelegate: MsgFieldDelegate<Edition> = MsgFieldDelegate(PresenceIndices.editionDeprecated) { Edition.EDITION_UNKNOWN }
        public override var editionDeprecated: Edition by __editionDeprecatedDelegate
        public override fun clearEditionDeprecated() {
            __editionDeprecatedDelegate.clearField(this)
        }

        internal val __deprecationWarningDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.deprecationWarning) { "" }
        public override var deprecationWarning: String by __deprecationWarningDelegate
        public override fun clearDeprecationWarning() {
            __deprecationWarningDelegate.clearField(this)
        }

        internal val __editionRemovedDelegate: MsgFieldDelegate<Edition> = MsgFieldDelegate(PresenceIndices.editionRemoved) { Edition.EDITION_UNKNOWN }
        public override var editionRemoved: Edition by __editionRemovedDelegate

        public override fun clearEditionRemoved() {
            __editionRemovedDelegate.clearField(this)
        }

        private val _owner: FeatureSupportInternal = this

        @InternalRpcApi
        public val _presence: FieldOptionsPresence.FeatureSupport = object : FieldOptionsPresence.FeatureSupport, InternalPresenceObject {
            public override val _message: FeatureSupportInternal get() = _owner

            public override val hasEditionIntroduced: Boolean get() = presenceMask[PresenceIndices.editionIntroduced]

            public override val hasEditionDeprecated: Boolean get() = presenceMask[PresenceIndices.editionDeprecated]

            public override val hasDeprecationWarning: Boolean get() = presenceMask[PresenceIndices.deprecationWarning]

            public override val hasEditionRemoved: Boolean get() = presenceMask[PresenceIndices.editionRemoved]
        }

        public override fun hashCode(): Int {
            var result = if (presenceMask[PresenceIndices.editionIntroduced]) this.editionIntroduced.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.editionDeprecated]) this.editionDeprecated.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.deprecationWarning]) this.deprecationWarning.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.editionRemoved]) this.editionRemoved.hashCode() else 0
            return result
        }

        public override fun equals(other: kotlin.Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as FeatureSupportInternal
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[PresenceIndices.editionIntroduced] && this.editionIntroduced != other.editionIntroduced) return false
            if (presenceMask[PresenceIndices.editionDeprecated] && this.editionDeprecated != other.editionDeprecated) return false
            if (presenceMask[PresenceIndices.deprecationWarning] && this.deprecationWarning != other.deprecationWarning) return false
            return !presenceMask[PresenceIndices.editionRemoved] || this.editionRemoved == other.editionRemoved
        }

        public override fun toString(): String {
            return asString()
        }

        public fun asString(indent: Int = 0): String {
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("FieldOptions.FeatureSupport(")
            if (presenceMask[PresenceIndices.editionIntroduced]) {
                builder.appendLine("${nextIndentString}editionIntroduced=${this.editionIntroduced},")
            } else {
                builder.appendLine("${nextIndentString}editionIntroduced=<unset>,")
            }

            if (presenceMask[PresenceIndices.editionDeprecated]) {
                builder.appendLine("${nextIndentString}editionDeprecated=${this.editionDeprecated},")
            } else {
                builder.appendLine("${nextIndentString}editionDeprecated=<unset>,")
            }

            if (presenceMask[PresenceIndices.deprecationWarning]) {
                builder.appendLine("${nextIndentString}deprecationWarning=${this.deprecationWarning},")
            } else {
                builder.appendLine("${nextIndentString}deprecationWarning=<unset>,")
            }

            if (presenceMask[PresenceIndices.editionRemoved]) {
                builder.appendLine("${nextIndentString}editionRemoved=${this.editionRemoved},")
            } else {
                builder.appendLine("${nextIndentString}editionRemoved=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        public override fun copyInternal(): FeatureSupportInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        public fun copyInternal(body: FeatureSupportInternal.() -> Unit): FeatureSupportInternal {
            val copy = FeatureSupportInternal()
            if (presenceMask[PresenceIndices.editionIntroduced]) {
                copy.editionIntroduced = this.editionIntroduced
            }

            if (presenceMask[PresenceIndices.editionDeprecated]) {
                copy.editionDeprecated = this.editionDeprecated
            }

            if (presenceMask[PresenceIndices.deprecationWarning]) {
                copy.deprecationWarning = this.deprecationWarning
            }

            if (presenceMask[PresenceIndices.editionRemoved]) {
                copy.editionRemoved = this.editionRemoved
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        public object MARSHALLER: ProtoGrpcMarshaller<FieldOptions.FeatureSupport, FeatureSupportInternal>() {
            public override fun asInternal(
                value: FieldOptions.FeatureSupport,
            ): FeatureSupportInternal {
                return value.asInternal()
            }

            public override fun newInternal(): FeatureSupportInternal {
                return FeatureSupportInternal()
            }

            public override fun encodeWith(
                message: FeatureSupportInternal,
                encoder: WireEncoder,
                config: ProtoConfig?,
            ) {
                message.encodeWith(encoder, config)
            }

            public override fun decodeWith(
                message: FeatureSupportInternal,
                decoder: WireDecoder,
                config: ProtoConfig?,
            ) {
                FeatureSupportInternal.decodeWith(message, decoder, config)
            }
        }

        @InternalRpcApi
        public object DESCRIPTOR: ProtoDescriptor<FieldOptions.FeatureSupport> {
            public override val fullName: String = "google.protobuf.FieldOptions.FeatureSupport"
        }

        @InternalRpcApi
        public companion object {
            public val DEFAULT: FieldOptions.FeatureSupport by lazy { FeatureSupportInternal() }
        }
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<FieldOptions, FieldOptionsInternal>() {
        public override fun asInternal(value: FieldOptions): FieldOptionsInternal {
            return value.asInternal()
        }

        public override fun newInternal(): FieldOptionsInternal {
            return FieldOptionsInternal()
        }

        public override fun encodeWith(
            message: FieldOptionsInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: FieldOptionsInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            FieldOptionsInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<FieldOptions> {
        public override val fullName: String = "google.protobuf.FieldOptions"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: FieldOptions by lazy { FieldOptionsInternal() }
    }
}

@InternalRpcApi
public class OneofOptionsInternal: OneofOptions.Builder, InternalMessage(fieldsWithPresence = 1) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val features: Int = 0
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __featuresDelegate: MsgFieldDelegate<FeatureSet> = MsgFieldDelegate(PresenceIndices.features) { FeatureSetInternal.DEFAULT }
    public override var features: FeatureSet by __featuresDelegate
    public override fun clearFeatures() {
        __featuresDelegate.clearField(this)
    }

    internal val __uninterpretedOptionDelegate: MsgFieldDelegate<List<UninterpretedOption>> = MsgFieldDelegate { emptyList() }
    public override var uninterpretedOption: List<UninterpretedOption> by __uninterpretedOptionDelegate

    private val _owner: OneofOptionsInternal = this

    @InternalRpcApi
    public val _presence: OneofOptionsPresence = object : OneofOptionsPresence, InternalPresenceObject {
        public override val _message: OneofOptionsInternal get() = _owner

        public override val hasFeatures: Boolean get() = presenceMask[PresenceIndices.features]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.features]) this.features.hashCode() else 0
        result = 31 * result + this.uninterpretedOption.hashCode()
        result = 31 * result + extensionsHashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as OneofOptionsInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.features] && this.features != other.features) return false
        if (this.uninterpretedOption != other.uninterpretedOption) return false
        return extensionsEqual(other)
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("OneofOptions(")
        if (presenceMask[PresenceIndices.features]) {
            builder.appendLine("${nextIndentString}features=${this.features.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}features=<unset>,")
        }

        builder.appendLine("${nextIndentString}uninterpretedOption=${this.uninterpretedOption},")
        builder.appendExtensions(nextIndentString)
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): OneofOptionsInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: OneofOptionsInternal.() -> Unit): OneofOptionsInternal {
        val copy = OneofOptionsInternal()
        if (presenceMask[PresenceIndices.features]) {
            copy.features = this.features.copy()
        }

        copy.uninterpretedOption = this.uninterpretedOption.map { it.copy() }
        copy.copyExtensionsFrom(this)
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<OneofOptions, OneofOptionsInternal>() {
        public override fun asInternal(value: OneofOptions): OneofOptionsInternal {
            return value.asInternal()
        }

        public override fun newInternal(): OneofOptionsInternal {
            return OneofOptionsInternal()
        }

        public override fun encodeWith(
            message: OneofOptionsInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: OneofOptionsInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            OneofOptionsInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<OneofOptions> {
        public override val fullName: String = "google.protobuf.OneofOptions"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: OneofOptions by lazy { OneofOptionsInternal() }
    }
}

@InternalRpcApi
public class EnumOptionsInternal: EnumOptions.Builder, InternalMessage(fieldsWithPresence = 4) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val allowAlias: Int = 0
        const val deprecated: Int = 1
        const val deprecatedLegacyJsonFieldConflicts: Int = 2
        const val features: Int = 3
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __allowAliasDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.allowAlias) { false }
    public override var allowAlias: Boolean by __allowAliasDelegate
    public override fun clearAllowAlias() {
        __allowAliasDelegate.clearField(this)
    }

    internal val __deprecatedDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.deprecated) { false }
    public override var deprecated: Boolean by __deprecatedDelegate
    public override fun clearDeprecated() {
        __deprecatedDelegate.clearField(this)
    }

    internal val __deprecatedLegacyJsonFieldConflictsDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.deprecatedLegacyJsonFieldConflicts) { false }
    public override var deprecatedLegacyJsonFieldConflicts: Boolean by __deprecatedLegacyJsonFieldConflictsDelegate
    public override fun clearDeprecatedLegacyJsonFieldConflicts() {
        __deprecatedLegacyJsonFieldConflictsDelegate.clearField(this)
    }

    internal val __featuresDelegate: MsgFieldDelegate<FeatureSet> = MsgFieldDelegate(PresenceIndices.features) { FeatureSetInternal.DEFAULT }
    public override var features: FeatureSet by __featuresDelegate
    public override fun clearFeatures() {
        __featuresDelegate.clearField(this)
    }

    internal val __uninterpretedOptionDelegate: MsgFieldDelegate<List<UninterpretedOption>> = MsgFieldDelegate { emptyList() }
    public override var uninterpretedOption: List<UninterpretedOption> by __uninterpretedOptionDelegate

    private val _owner: EnumOptionsInternal = this

    @InternalRpcApi
    public val _presence: EnumOptionsPresence = object : EnumOptionsPresence, InternalPresenceObject {
        public override val _message: EnumOptionsInternal get() = _owner

        public override val hasAllowAlias: Boolean get() = presenceMask[PresenceIndices.allowAlias]

        public override val hasDeprecated: Boolean get() = presenceMask[PresenceIndices.deprecated]

        public override val hasDeprecatedLegacyJsonFieldConflicts: Boolean get() = presenceMask[PresenceIndices.deprecatedLegacyJsonFieldConflicts]

        public override val hasFeatures: Boolean get() = presenceMask[PresenceIndices.features]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.allowAlias]) this.allowAlias.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.deprecated]) this.deprecated.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.deprecatedLegacyJsonFieldConflicts]) this.deprecatedLegacyJsonFieldConflicts.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.features]) this.features.hashCode() else 0
        result = 31 * result + this.uninterpretedOption.hashCode()
        result = 31 * result + extensionsHashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EnumOptionsInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.allowAlias] && this.allowAlias != other.allowAlias) return false
        if (presenceMask[PresenceIndices.deprecated] && this.deprecated != other.deprecated) return false
        if (presenceMask[PresenceIndices.deprecatedLegacyJsonFieldConflicts] && this.deprecatedLegacyJsonFieldConflicts != other.deprecatedLegacyJsonFieldConflicts) return false
        if (presenceMask[PresenceIndices.features] && this.features != other.features) return false
        if (this.uninterpretedOption != other.uninterpretedOption) return false
        return extensionsEqual(other)
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("EnumOptions(")
        if (presenceMask[PresenceIndices.allowAlias]) {
            builder.appendLine("${nextIndentString}allowAlias=${this.allowAlias},")
        } else {
            builder.appendLine("${nextIndentString}allowAlias=<unset>,")
        }

        if (presenceMask[PresenceIndices.deprecated]) {
            builder.appendLine("${nextIndentString}deprecated=${this.deprecated},")
        } else {
            builder.appendLine("${nextIndentString}deprecated=<unset>,")
        }

        if (presenceMask[PresenceIndices.deprecatedLegacyJsonFieldConflicts]) {
            builder.appendLine("${nextIndentString}deprecatedLegacyJsonFieldConflicts=${this.deprecatedLegacyJsonFieldConflicts},")
        } else {
            builder.appendLine("${nextIndentString}deprecatedLegacyJsonFieldConflicts=<unset>,")
        }

        if (presenceMask[PresenceIndices.features]) {
            builder.appendLine("${nextIndentString}features=${this.features.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}features=<unset>,")
        }

        builder.appendLine("${nextIndentString}uninterpretedOption=${this.uninterpretedOption},")
        builder.appendExtensions(nextIndentString)
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): EnumOptionsInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: EnumOptionsInternal.() -> Unit): EnumOptionsInternal {
        val copy = EnumOptionsInternal()
        if (presenceMask[PresenceIndices.allowAlias]) {
            copy.allowAlias = this.allowAlias
        }

        if (presenceMask[PresenceIndices.deprecated]) {
            copy.deprecated = this.deprecated
        }

        if (presenceMask[PresenceIndices.deprecatedLegacyJsonFieldConflicts]) {
            copy.deprecatedLegacyJsonFieldConflicts = this.deprecatedLegacyJsonFieldConflicts
        }

        if (presenceMask[PresenceIndices.features]) {
            copy.features = this.features.copy()
        }

        copy.uninterpretedOption = this.uninterpretedOption.map { it.copy() }
        copy.copyExtensionsFrom(this)
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<EnumOptions, EnumOptionsInternal>() {
        public override fun asInternal(value: EnumOptions): EnumOptionsInternal {
            return value.asInternal()
        }

        public override fun newInternal(): EnumOptionsInternal {
            return EnumOptionsInternal()
        }

        public override fun encodeWith(
            message: EnumOptionsInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: EnumOptionsInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            EnumOptionsInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<EnumOptions> {
        public override val fullName: String = "google.protobuf.EnumOptions"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: EnumOptions by lazy { EnumOptionsInternal() }
    }
}

@InternalRpcApi
public class EnumValueOptionsInternal: EnumValueOptions.Builder, InternalMessage(fieldsWithPresence = 4) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val deprecated: Int = 0
        const val features: Int = 1
        const val debugRedact: Int = 2
        const val featureSupport: Int = 3
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __deprecatedDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.deprecated) { false }
    public override var deprecated: Boolean by __deprecatedDelegate
    public override fun clearDeprecated() {
        __deprecatedDelegate.clearField(this)
    }

    internal val __featuresDelegate: MsgFieldDelegate<FeatureSet> = MsgFieldDelegate(PresenceIndices.features) { FeatureSetInternal.DEFAULT }
    public override var features: FeatureSet by __featuresDelegate
    public override fun clearFeatures() {
        __featuresDelegate.clearField(this)
    }

    internal val __debugRedactDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.debugRedact) { false }
    public override var debugRedact: Boolean by __debugRedactDelegate
    public override fun clearDebugRedact() {
        __debugRedactDelegate.clearField(this)
    }

    internal val __featureSupportDelegate: MsgFieldDelegate<FieldOptions.FeatureSupport> = MsgFieldDelegate(PresenceIndices.featureSupport) { FieldOptionsInternal.FeatureSupportInternal.DEFAULT }
    public override var featureSupport: FieldOptions.FeatureSupport by __featureSupportDelegate
    public override fun clearFeatureSupport() {
        __featureSupportDelegate.clearField(this)
    }

    internal val __uninterpretedOptionDelegate: MsgFieldDelegate<List<UninterpretedOption>> = MsgFieldDelegate { emptyList() }
    public override var uninterpretedOption: List<UninterpretedOption> by __uninterpretedOptionDelegate

    private val _owner: EnumValueOptionsInternal = this

    @InternalRpcApi
    public val _presence: EnumValueOptionsPresence = object : EnumValueOptionsPresence, InternalPresenceObject {
        public override val _message: EnumValueOptionsInternal get() = _owner

        public override val hasDeprecated: Boolean get() = presenceMask[PresenceIndices.deprecated]

        public override val hasFeatures: Boolean get() = presenceMask[PresenceIndices.features]

        public override val hasDebugRedact: Boolean get() = presenceMask[PresenceIndices.debugRedact]

        public override val hasFeatureSupport: Boolean get() = presenceMask[PresenceIndices.featureSupport]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.deprecated]) this.deprecated.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.features]) this.features.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.debugRedact]) this.debugRedact.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.featureSupport]) this.featureSupport.hashCode() else 0
        result = 31 * result + this.uninterpretedOption.hashCode()
        result = 31 * result + extensionsHashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EnumValueOptionsInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.deprecated] && this.deprecated != other.deprecated) return false
        if (presenceMask[PresenceIndices.features] && this.features != other.features) return false
        if (presenceMask[PresenceIndices.debugRedact] && this.debugRedact != other.debugRedact) return false
        if (presenceMask[PresenceIndices.featureSupport] && this.featureSupport != other.featureSupport) return false
        if (this.uninterpretedOption != other.uninterpretedOption) return false
        return extensionsEqual(other)
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("EnumValueOptions(")
        if (presenceMask[PresenceIndices.deprecated]) {
            builder.appendLine("${nextIndentString}deprecated=${this.deprecated},")
        } else {
            builder.appendLine("${nextIndentString}deprecated=<unset>,")
        }

        if (presenceMask[PresenceIndices.features]) {
            builder.appendLine("${nextIndentString}features=${this.features.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}features=<unset>,")
        }

        if (presenceMask[PresenceIndices.debugRedact]) {
            builder.appendLine("${nextIndentString}debugRedact=${this.debugRedact},")
        } else {
            builder.appendLine("${nextIndentString}debugRedact=<unset>,")
        }

        if (presenceMask[PresenceIndices.featureSupport]) {
            builder.appendLine("${nextIndentString}featureSupport=${this.featureSupport.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}featureSupport=<unset>,")
        }

        builder.appendLine("${nextIndentString}uninterpretedOption=${this.uninterpretedOption},")
        builder.appendExtensions(nextIndentString)
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): EnumValueOptionsInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: EnumValueOptionsInternal.() -> Unit): EnumValueOptionsInternal {
        val copy = EnumValueOptionsInternal()
        if (presenceMask[PresenceIndices.deprecated]) {
            copy.deprecated = this.deprecated
        }

        if (presenceMask[PresenceIndices.features]) {
            copy.features = this.features.copy()
        }

        if (presenceMask[PresenceIndices.debugRedact]) {
            copy.debugRedact = this.debugRedact
        }

        if (presenceMask[PresenceIndices.featureSupport]) {
            copy.featureSupport = this.featureSupport.copy()
        }

        copy.uninterpretedOption = this.uninterpretedOption.map { it.copy() }
        copy.copyExtensionsFrom(this)
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<EnumValueOptions, EnumValueOptionsInternal>() {
        public override fun asInternal(value: EnumValueOptions): EnumValueOptionsInternal {
            return value.asInternal()
        }

        public override fun newInternal(): EnumValueOptionsInternal {
            return EnumValueOptionsInternal()
        }

        public override fun encodeWith(
            message: EnumValueOptionsInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: EnumValueOptionsInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            EnumValueOptionsInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<EnumValueOptions> {
        public override val fullName: String = "google.protobuf.EnumValueOptions"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: EnumValueOptions by lazy { EnumValueOptionsInternal() }
    }
}

@InternalRpcApi
public class ServiceOptionsInternal: ServiceOptions.Builder, InternalMessage(fieldsWithPresence = 2) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val features: Int = 0
        const val deprecated: Int = 1
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __featuresDelegate: MsgFieldDelegate<FeatureSet> = MsgFieldDelegate(PresenceIndices.features) { FeatureSetInternal.DEFAULT }
    public override var features: FeatureSet by __featuresDelegate
    public override fun clearFeatures() {
        __featuresDelegate.clearField(this)
    }

    internal val __deprecatedDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.deprecated) { false }
    public override var deprecated: Boolean by __deprecatedDelegate
    public override fun clearDeprecated() {
        __deprecatedDelegate.clearField(this)
    }

    internal val __uninterpretedOptionDelegate: MsgFieldDelegate<List<UninterpretedOption>> = MsgFieldDelegate { emptyList() }
    public override var uninterpretedOption: List<UninterpretedOption> by __uninterpretedOptionDelegate

    private val _owner: ServiceOptionsInternal = this

    @InternalRpcApi
    public val _presence: ServiceOptionsPresence = object : ServiceOptionsPresence, InternalPresenceObject {
        public override val _message: ServiceOptionsInternal get() = _owner

        public override val hasFeatures: Boolean get() = presenceMask[PresenceIndices.features]

        public override val hasDeprecated: Boolean get() = presenceMask[PresenceIndices.deprecated]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.features]) this.features.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.deprecated]) this.deprecated.hashCode() else 0
        result = 31 * result + this.uninterpretedOption.hashCode()
        result = 31 * result + extensionsHashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ServiceOptionsInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.features] && this.features != other.features) return false
        if (presenceMask[PresenceIndices.deprecated] && this.deprecated != other.deprecated) return false
        if (this.uninterpretedOption != other.uninterpretedOption) return false
        return extensionsEqual(other)
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("ServiceOptions(")
        if (presenceMask[PresenceIndices.features]) {
            builder.appendLine("${nextIndentString}features=${this.features.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}features=<unset>,")
        }

        if (presenceMask[PresenceIndices.deprecated]) {
            builder.appendLine("${nextIndentString}deprecated=${this.deprecated},")
        } else {
            builder.appendLine("${nextIndentString}deprecated=<unset>,")
        }

        builder.appendLine("${nextIndentString}uninterpretedOption=${this.uninterpretedOption},")
        builder.appendExtensions(nextIndentString)
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): ServiceOptionsInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: ServiceOptionsInternal.() -> Unit): ServiceOptionsInternal {
        val copy = ServiceOptionsInternal()
        if (presenceMask[PresenceIndices.features]) {
            copy.features = this.features.copy()
        }

        if (presenceMask[PresenceIndices.deprecated]) {
            copy.deprecated = this.deprecated
        }

        copy.uninterpretedOption = this.uninterpretedOption.map { it.copy() }
        copy.copyExtensionsFrom(this)
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<ServiceOptions, ServiceOptionsInternal>() {
        public override fun asInternal(value: ServiceOptions): ServiceOptionsInternal {
            return value.asInternal()
        }

        public override fun newInternal(): ServiceOptionsInternal {
            return ServiceOptionsInternal()
        }

        public override fun encodeWith(
            message: ServiceOptionsInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: ServiceOptionsInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            ServiceOptionsInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<ServiceOptions> {
        public override val fullName: String = "google.protobuf.ServiceOptions"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: ServiceOptions by lazy { ServiceOptionsInternal() }
    }
}

@InternalRpcApi
public class MethodOptionsInternal: MethodOptions.Builder, InternalMessage(fieldsWithPresence = 3) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val deprecated: Int = 0
        const val idempotencyLevel: Int = 1
        const val features: Int = 2
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __deprecatedDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.deprecated) { false }
    public override var deprecated: Boolean by __deprecatedDelegate
    public override fun clearDeprecated() {
        __deprecatedDelegate.clearField(this)
    }

    internal val __idempotencyLevelDelegate: MsgFieldDelegate<MethodOptions.IdempotencyLevel> = MsgFieldDelegate(PresenceIndices.idempotencyLevel) { MethodOptions.IdempotencyLevel.IDEMPOTENCY_UNKNOWN }
    public override var idempotencyLevel: MethodOptions.IdempotencyLevel by __idempotencyLevelDelegate
    public override fun clearIdempotencyLevel() {
        __idempotencyLevelDelegate.clearField(this)
    }

    internal val __featuresDelegate: MsgFieldDelegate<FeatureSet> = MsgFieldDelegate(PresenceIndices.features) { FeatureSetInternal.DEFAULT }
    public override var features: FeatureSet by __featuresDelegate
    public override fun clearFeatures() {
        __featuresDelegate.clearField(this)
    }

    internal val __uninterpretedOptionDelegate: MsgFieldDelegate<List<UninterpretedOption>> = MsgFieldDelegate { emptyList() }
    public override var uninterpretedOption: List<UninterpretedOption> by __uninterpretedOptionDelegate

    private val _owner: MethodOptionsInternal = this

    @InternalRpcApi
    public val _presence: MethodOptionsPresence = object : MethodOptionsPresence, InternalPresenceObject {
        public override val _message: MethodOptionsInternal get() = _owner

        public override val hasDeprecated: Boolean get() = presenceMask[PresenceIndices.deprecated]

        public override val hasIdempotencyLevel: Boolean get() = presenceMask[PresenceIndices.idempotencyLevel]

        public override val hasFeatures: Boolean get() = presenceMask[PresenceIndices.features]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = if (presenceMask[PresenceIndices.deprecated]) this.deprecated.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.idempotencyLevel]) this.idempotencyLevel.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.features]) this.features.hashCode() else 0
        result = 31 * result + this.uninterpretedOption.hashCode()
        result = 31 * result + extensionsHashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as MethodOptionsInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.deprecated] && this.deprecated != other.deprecated) return false
        if (presenceMask[PresenceIndices.idempotencyLevel] && this.idempotencyLevel != other.idempotencyLevel) return false
        if (presenceMask[PresenceIndices.features] && this.features != other.features) return false
        if (this.uninterpretedOption != other.uninterpretedOption) return false
        return extensionsEqual(other)
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("MethodOptions(")
        if (presenceMask[PresenceIndices.deprecated]) {
            builder.appendLine("${nextIndentString}deprecated=${this.deprecated},")
        } else {
            builder.appendLine("${nextIndentString}deprecated=<unset>,")
        }

        if (presenceMask[PresenceIndices.idempotencyLevel]) {
            builder.appendLine("${nextIndentString}idempotencyLevel=${this.idempotencyLevel},")
        } else {
            builder.appendLine("${nextIndentString}idempotencyLevel=<unset>,")
        }

        if (presenceMask[PresenceIndices.features]) {
            builder.appendLine("${nextIndentString}features=${this.features.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}features=<unset>,")
        }

        builder.appendLine("${nextIndentString}uninterpretedOption=${this.uninterpretedOption},")
        builder.appendExtensions(nextIndentString)
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): MethodOptionsInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: MethodOptionsInternal.() -> Unit): MethodOptionsInternal {
        val copy = MethodOptionsInternal()
        if (presenceMask[PresenceIndices.deprecated]) {
            copy.deprecated = this.deprecated
        }

        if (presenceMask[PresenceIndices.idempotencyLevel]) {
            copy.idempotencyLevel = this.idempotencyLevel
        }

        if (presenceMask[PresenceIndices.features]) {
            copy.features = this.features.copy()
        }

        copy.uninterpretedOption = this.uninterpretedOption.map { it.copy() }
        copy.copyExtensionsFrom(this)
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<MethodOptions, MethodOptionsInternal>() {
        public override fun asInternal(value: MethodOptions): MethodOptionsInternal {
            return value.asInternal()
        }

        public override fun newInternal(): MethodOptionsInternal {
            return MethodOptionsInternal()
        }

        public override fun encodeWith(
            message: MethodOptionsInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: MethodOptionsInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            MethodOptionsInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<MethodOptions> {
        public override val fullName: String = "google.protobuf.MethodOptions"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: MethodOptions by lazy { MethodOptionsInternal() }
    }
}

@InternalRpcApi
public class UninterpretedOptionInternal: UninterpretedOption.Builder, InternalMessage(fieldsWithPresence = 6) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val identifierValue: Int = 0
        const val positiveIntValue: Int = 1
        const val negativeIntValue: Int = 2
        const val doubleValue: Int = 3
        const val stringValue: Int = 4
        const val aggregateValue: Int = 5
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __nameDelegate: MsgFieldDelegate<List<UninterpretedOption.NamePart>> = MsgFieldDelegate { emptyList() }
    public override var name: List<UninterpretedOption.NamePart> by __nameDelegate
    internal val __identifierValueDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.identifierValue) { "" }
    public override var identifierValue: String by __identifierValueDelegate
    public override fun clearIdentifierValue() {
        __identifierValueDelegate.clearField(this)
    }

    internal val __positiveIntValueDelegate: MsgFieldDelegate<ULong> = MsgFieldDelegate(PresenceIndices.positiveIntValue) { 0uL }
    public override var positiveIntValue: ULong by __positiveIntValueDelegate
    public override fun clearPositiveIntValue() {
        __positiveIntValueDelegate.clearField(this)
    }

    internal val __negativeIntValueDelegate: MsgFieldDelegate<Long> = MsgFieldDelegate(PresenceIndices.negativeIntValue) { 0L }
    public override var negativeIntValue: Long by __negativeIntValueDelegate
    public override fun clearNegativeIntValue() {
        __negativeIntValueDelegate.clearField(this)
    }

    internal val __doubleValueDelegate: MsgFieldDelegate<Double> = MsgFieldDelegate(PresenceIndices.doubleValue) { 0.0 }
    public override var doubleValue: Double by __doubleValueDelegate
    public override fun clearDoubleValue() {
        __doubleValueDelegate.clearField(this)
    }

    internal val __stringValueDelegate: MsgFieldDelegate<ByteString> = MsgFieldDelegate(PresenceIndices.stringValue) { ByteString() }
    public override var stringValue: ByteString by __stringValueDelegate
    public override fun clearStringValue() {
        __stringValueDelegate.clearField(this)
    }

    internal val __aggregateValueDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.aggregateValue) { "" }
    public override var aggregateValue: String by __aggregateValueDelegate

    public override fun clearAggregateValue() {
        __aggregateValueDelegate.clearField(this)
    }

    private val _owner: UninterpretedOptionInternal = this

    @InternalRpcApi
    public val _presence: UninterpretedOptionPresence = object : UninterpretedOptionPresence, InternalPresenceObject {
        public override val _message: UninterpretedOptionInternal get() = _owner

        public override val hasIdentifierValue: Boolean get() = presenceMask[PresenceIndices.identifierValue]

        public override val hasPositiveIntValue: Boolean get() = presenceMask[PresenceIndices.positiveIntValue]

        public override val hasNegativeIntValue: Boolean get() = presenceMask[PresenceIndices.negativeIntValue]

        public override val hasDoubleValue: Boolean get() = presenceMask[PresenceIndices.doubleValue]

        public override val hasStringValue: Boolean get() = presenceMask[PresenceIndices.stringValue]

        public override val hasAggregateValue: Boolean get() = presenceMask[PresenceIndices.aggregateValue]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.name.hashCode()
        result = 31 * result + if (presenceMask[PresenceIndices.identifierValue]) this.identifierValue.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.positiveIntValue]) this.positiveIntValue.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.negativeIntValue]) this.negativeIntValue.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.doubleValue]) this.doubleValue.toBits().hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.stringValue]) this.stringValue.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.aggregateValue]) this.aggregateValue.hashCode() else 0
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as UninterpretedOptionInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (this.name != other.name) return false
        if (presenceMask[PresenceIndices.identifierValue] && this.identifierValue != other.identifierValue) return false
        if (presenceMask[PresenceIndices.positiveIntValue] && this.positiveIntValue != other.positiveIntValue) return false
        if (presenceMask[PresenceIndices.negativeIntValue] && this.negativeIntValue != other.negativeIntValue) return false
        if (presenceMask[PresenceIndices.doubleValue] && this.doubleValue.toBits() != other.doubleValue.toBits()) return false
        if (presenceMask[PresenceIndices.stringValue] && this.stringValue != other.stringValue) return false
        return !presenceMask[PresenceIndices.aggregateValue] || this.aggregateValue == other.aggregateValue
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("UninterpretedOption(")
        builder.appendLine("${nextIndentString}name=${this.name},")
        if (presenceMask[PresenceIndices.identifierValue]) {
            builder.appendLine("${nextIndentString}identifierValue=${this.identifierValue},")
        } else {
            builder.appendLine("${nextIndentString}identifierValue=<unset>,")
        }

        if (presenceMask[PresenceIndices.positiveIntValue]) {
            builder.appendLine("${nextIndentString}positiveIntValue=${this.positiveIntValue},")
        } else {
            builder.appendLine("${nextIndentString}positiveIntValue=<unset>,")
        }

        if (presenceMask[PresenceIndices.negativeIntValue]) {
            builder.appendLine("${nextIndentString}negativeIntValue=${this.negativeIntValue},")
        } else {
            builder.appendLine("${nextIndentString}negativeIntValue=<unset>,")
        }

        if (presenceMask[PresenceIndices.doubleValue]) {
            builder.appendLine("${nextIndentString}doubleValue=${this.doubleValue},")
        } else {
            builder.appendLine("${nextIndentString}doubleValue=<unset>,")
        }

        if (presenceMask[PresenceIndices.stringValue]) {
            builder.appendLine("${nextIndentString}stringValue=${this.stringValue.protoToString()},")
        } else {
            builder.appendLine("${nextIndentString}stringValue=<unset>,")
        }

        if (presenceMask[PresenceIndices.aggregateValue]) {
            builder.appendLine("${nextIndentString}aggregateValue=${this.aggregateValue},")
        } else {
            builder.appendLine("${nextIndentString}aggregateValue=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): UninterpretedOptionInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(
        body: UninterpretedOptionInternal.() -> Unit,
    ): UninterpretedOptionInternal {
        val copy = UninterpretedOptionInternal()
        copy.name = this.name.map { it.copy() }
        if (presenceMask[PresenceIndices.identifierValue]) {
            copy.identifierValue = this.identifierValue
        }

        if (presenceMask[PresenceIndices.positiveIntValue]) {
            copy.positiveIntValue = this.positiveIntValue
        }

        if (presenceMask[PresenceIndices.negativeIntValue]) {
            copy.negativeIntValue = this.negativeIntValue
        }

        if (presenceMask[PresenceIndices.doubleValue]) {
            copy.doubleValue = this.doubleValue
        }

        if (presenceMask[PresenceIndices.stringValue]) {
            copy.stringValue = this.stringValue
        }

        if (presenceMask[PresenceIndices.aggregateValue]) {
            copy.aggregateValue = this.aggregateValue
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public class NamePartInternal: UninterpretedOption.NamePart.Builder, InternalMessage(fieldsWithPresence = 2) {
        @InternalRpcApi
        internal object PresenceIndices {
            const val namePart: Int = 0
            const val isExtension: Int = 1
        }

        @InternalRpcApi
        public override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        public override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __namePartDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.namePart) { "" }
        public override var namePart: String by __namePartDelegate
        public override fun clearNamePart() {
            __namePartDelegate.clearField(this)
        }

        internal val __isExtensionDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate(PresenceIndices.isExtension) { false }
        public override var isExtension: Boolean by __isExtensionDelegate

        public override fun clearIsExtension() {
            __isExtensionDelegate.clearField(this)
        }

        private val _owner: NamePartInternal = this

        @InternalRpcApi
        public val _presence: UninterpretedOptionPresence.NamePart = object : UninterpretedOptionPresence.NamePart, InternalPresenceObject {
            public override val _message: NamePartInternal get() = _owner

            public override val hasNamePart: Boolean get() = presenceMask[PresenceIndices.namePart]

            public override val hasIsExtension: Boolean get() = presenceMask[PresenceIndices.isExtension]
        }

        public override fun hashCode(): Int {
            checkRequiredFields()
            var result = if (presenceMask[PresenceIndices.namePart]) this.namePart.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.isExtension]) this.isExtension.hashCode() else 0
            return result
        }

        public override fun equals(other: kotlin.Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as NamePartInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[PresenceIndices.namePart] && this.namePart != other.namePart) return false
            return !presenceMask[PresenceIndices.isExtension] || this.isExtension == other.isExtension
        }

        public override fun toString(): String {
            return asString()
        }

        public fun asString(indent: Int = 0): String {
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("UninterpretedOption.NamePart(")
            if (presenceMask[PresenceIndices.namePart]) {
                builder.appendLine("${nextIndentString}namePart=${this.namePart},")
            } else {
                builder.appendLine("${nextIndentString}namePart=<unset>,")
            }

            if (presenceMask[PresenceIndices.isExtension]) {
                builder.appendLine("${nextIndentString}isExtension=${this.isExtension},")
            } else {
                builder.appendLine("${nextIndentString}isExtension=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        public override fun copyInternal(): NamePartInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        public fun copyInternal(body: NamePartInternal.() -> Unit): NamePartInternal {
            val copy = NamePartInternal()
            if (presenceMask[PresenceIndices.namePart]) {
                copy.namePart = this.namePart
            }

            if (presenceMask[PresenceIndices.isExtension]) {
                copy.isExtension = this.isExtension
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        public object MARSHALLER: ProtoGrpcMarshaller<UninterpretedOption.NamePart, NamePartInternal>() {
            public override fun asInternal(value: UninterpretedOption.NamePart): NamePartInternal {
                return value.asInternal()
            }

            public override fun newInternal(): NamePartInternal {
                return NamePartInternal()
            }

            public override fun encodeWith(
                message: NamePartInternal,
                encoder: WireEncoder,
                config: ProtoConfig?,
            ) {
                message.encodeWith(encoder, config)
            }

            public override fun decodeWith(
                message: NamePartInternal,
                decoder: WireDecoder,
                config: ProtoConfig?,
            ) {
                NamePartInternal.decodeWith(message, decoder, config)
                message.checkRequiredFields()
            }
        }

        @InternalRpcApi
        public object DESCRIPTOR: ProtoDescriptor<UninterpretedOption.NamePart> {
            public override val fullName: String = "google.protobuf.UninterpretedOption.NamePart"
        }

        @InternalRpcApi
        public companion object {
            public val DEFAULT: UninterpretedOption.NamePart by lazy { NamePartInternal() }
        }
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<UninterpretedOption, UninterpretedOptionInternal>() {
        public override fun asInternal(value: UninterpretedOption): UninterpretedOptionInternal {
            return value.asInternal()
        }

        public override fun newInternal(): UninterpretedOptionInternal {
            return UninterpretedOptionInternal()
        }

        public override fun encodeWith(
            message: UninterpretedOptionInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: UninterpretedOptionInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            UninterpretedOptionInternal.decodeWith(message, decoder, config)
            message.checkRequiredFields()
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<UninterpretedOption> {
        public override val fullName: String = "google.protobuf.UninterpretedOption"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: UninterpretedOption by lazy { UninterpretedOptionInternal() }
    }
}

@InternalRpcApi
public class FeatureSetInternal: FeatureSet.Builder, InternalMessage(fieldsWithPresence = 8) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val fieldPresence: Int = 0
        const val enumType: Int = 1
        const val repeatedFieldEncoding: Int = 2
        const val utf8Validation: Int = 3
        const val messageEncoding: Int = 4
        const val jsonFormat: Int = 5
        const val enforceNamingStyle: Int = 6
        const val defaultSymbolVisibility: Int = 7
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __fieldPresenceDelegate: MsgFieldDelegate<FeatureSet.FieldPresence> = MsgFieldDelegate(PresenceIndices.fieldPresence) { FeatureSet.FieldPresence.FIELD_PRESENCE_UNKNOWN }
    public override var fieldPresence: FeatureSet.FieldPresence by __fieldPresenceDelegate
    public override fun clearFieldPresence() {
        __fieldPresenceDelegate.clearField(this)
    }

    internal val __enumTypeDelegate: MsgFieldDelegate<FeatureSet.EnumType> = MsgFieldDelegate(PresenceIndices.enumType) { FeatureSet.EnumType.ENUM_TYPE_UNKNOWN }
    public override var enumType: FeatureSet.EnumType by __enumTypeDelegate
    public override fun clearEnumType() {
        __enumTypeDelegate.clearField(this)
    }

    internal val __repeatedFieldEncodingDelegate: MsgFieldDelegate<FeatureSet.RepeatedFieldEncoding> = MsgFieldDelegate(PresenceIndices.repeatedFieldEncoding) { FeatureSet.RepeatedFieldEncoding.REPEATED_FIELD_ENCODING_UNKNOWN }
    public override var repeatedFieldEncoding: FeatureSet.RepeatedFieldEncoding by __repeatedFieldEncodingDelegate
    public override fun clearRepeatedFieldEncoding() {
        __repeatedFieldEncodingDelegate.clearField(this)
    }

    internal val __utf8ValidationDelegate: MsgFieldDelegate<FeatureSet.Utf8Validation> = MsgFieldDelegate(PresenceIndices.utf8Validation) { FeatureSet.Utf8Validation.UTF8_VALIDATION_UNKNOWN }
    public override var utf8Validation: FeatureSet.Utf8Validation by __utf8ValidationDelegate
    public override fun clearUtf8Validation() {
        __utf8ValidationDelegate.clearField(this)
    }

    internal val __messageEncodingDelegate: MsgFieldDelegate<FeatureSet.MessageEncoding> = MsgFieldDelegate(PresenceIndices.messageEncoding) { FeatureSet.MessageEncoding.MESSAGE_ENCODING_UNKNOWN }
    public override var messageEncoding: FeatureSet.MessageEncoding by __messageEncodingDelegate
    public override fun clearMessageEncoding() {
        __messageEncodingDelegate.clearField(this)
    }

    internal val __jsonFormatDelegate: MsgFieldDelegate<FeatureSet.JsonFormat> = MsgFieldDelegate(PresenceIndices.jsonFormat) { FeatureSet.JsonFormat.JSON_FORMAT_UNKNOWN }
    public override var jsonFormat: FeatureSet.JsonFormat by __jsonFormatDelegate
    public override fun clearJsonFormat() {
        __jsonFormatDelegate.clearField(this)
    }

    internal val __enforceNamingStyleDelegate: MsgFieldDelegate<FeatureSet.EnforceNamingStyle> = MsgFieldDelegate(PresenceIndices.enforceNamingStyle) { FeatureSet.EnforceNamingStyle.ENFORCE_NAMING_STYLE_UNKNOWN }
    public override var enforceNamingStyle: FeatureSet.EnforceNamingStyle by __enforceNamingStyleDelegate
    public override fun clearEnforceNamingStyle() {
        __enforceNamingStyleDelegate.clearField(this)
    }

    internal val __defaultSymbolVisibilityDelegate: MsgFieldDelegate<FeatureSet.VisibilityFeature.DefaultSymbolVisibility> = MsgFieldDelegate(PresenceIndices.defaultSymbolVisibility) { FeatureSet.VisibilityFeature.DefaultSymbolVisibility.DEFAULT_SYMBOL_VISIBILITY_UNKNOWN }
    public override var defaultSymbolVisibility: FeatureSet.VisibilityFeature.DefaultSymbolVisibility by __defaultSymbolVisibilityDelegate

    public override fun clearDefaultSymbolVisibility() {
        __defaultSymbolVisibilityDelegate.clearField(this)
    }

    private val _owner: FeatureSetInternal = this

    @InternalRpcApi
    public val _presence: FeatureSetPresence = object : FeatureSetPresence, InternalPresenceObject {
        public override val _message: FeatureSetInternal get() = _owner

        public override val hasFieldPresence: Boolean get() = presenceMask[PresenceIndices.fieldPresence]

        public override val hasEnumType: Boolean get() = presenceMask[PresenceIndices.enumType]

        public override val hasRepeatedFieldEncoding: Boolean get() = presenceMask[PresenceIndices.repeatedFieldEncoding]

        public override val hasUtf8Validation: Boolean get() = presenceMask[PresenceIndices.utf8Validation]

        public override val hasMessageEncoding: Boolean get() = presenceMask[PresenceIndices.messageEncoding]

        public override val hasJsonFormat: Boolean get() = presenceMask[PresenceIndices.jsonFormat]

        public override val hasEnforceNamingStyle: Boolean get() = presenceMask[PresenceIndices.enforceNamingStyle]

        public override val hasDefaultSymbolVisibility: Boolean get() = presenceMask[PresenceIndices.defaultSymbolVisibility]
    }

    public override fun hashCode(): Int {
        var result = if (presenceMask[PresenceIndices.fieldPresence]) this.fieldPresence.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.enumType]) this.enumType.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.repeatedFieldEncoding]) this.repeatedFieldEncoding.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.utf8Validation]) this.utf8Validation.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.messageEncoding]) this.messageEncoding.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.jsonFormat]) this.jsonFormat.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.enforceNamingStyle]) this.enforceNamingStyle.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.defaultSymbolVisibility]) this.defaultSymbolVisibility.hashCode() else 0
        result = 31 * result + extensionsHashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FeatureSetInternal
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.fieldPresence] && this.fieldPresence != other.fieldPresence) return false
        if (presenceMask[PresenceIndices.enumType] && this.enumType != other.enumType) return false
        if (presenceMask[PresenceIndices.repeatedFieldEncoding] && this.repeatedFieldEncoding != other.repeatedFieldEncoding) return false
        if (presenceMask[PresenceIndices.utf8Validation] && this.utf8Validation != other.utf8Validation) return false
        if (presenceMask[PresenceIndices.messageEncoding] && this.messageEncoding != other.messageEncoding) return false
        if (presenceMask[PresenceIndices.jsonFormat] && this.jsonFormat != other.jsonFormat) return false
        if (presenceMask[PresenceIndices.enforceNamingStyle] && this.enforceNamingStyle != other.enforceNamingStyle) return false
        if (presenceMask[PresenceIndices.defaultSymbolVisibility] && this.defaultSymbolVisibility != other.defaultSymbolVisibility) return false
        return extensionsEqual(other)
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("FeatureSet(")
        if (presenceMask[PresenceIndices.fieldPresence]) {
            builder.appendLine("${nextIndentString}fieldPresence=${this.fieldPresence},")
        } else {
            builder.appendLine("${nextIndentString}fieldPresence=<unset>,")
        }

        if (presenceMask[PresenceIndices.enumType]) {
            builder.appendLine("${nextIndentString}enumType=${this.enumType},")
        } else {
            builder.appendLine("${nextIndentString}enumType=<unset>,")
        }

        if (presenceMask[PresenceIndices.repeatedFieldEncoding]) {
            builder.appendLine("${nextIndentString}repeatedFieldEncoding=${this.repeatedFieldEncoding},")
        } else {
            builder.appendLine("${nextIndentString}repeatedFieldEncoding=<unset>,")
        }

        if (presenceMask[PresenceIndices.utf8Validation]) {
            builder.appendLine("${nextIndentString}utf8Validation=${this.utf8Validation},")
        } else {
            builder.appendLine("${nextIndentString}utf8Validation=<unset>,")
        }

        if (presenceMask[PresenceIndices.messageEncoding]) {
            builder.appendLine("${nextIndentString}messageEncoding=${this.messageEncoding},")
        } else {
            builder.appendLine("${nextIndentString}messageEncoding=<unset>,")
        }

        if (presenceMask[PresenceIndices.jsonFormat]) {
            builder.appendLine("${nextIndentString}jsonFormat=${this.jsonFormat},")
        } else {
            builder.appendLine("${nextIndentString}jsonFormat=<unset>,")
        }

        if (presenceMask[PresenceIndices.enforceNamingStyle]) {
            builder.appendLine("${nextIndentString}enforceNamingStyle=${this.enforceNamingStyle},")
        } else {
            builder.appendLine("${nextIndentString}enforceNamingStyle=<unset>,")
        }

        if (presenceMask[PresenceIndices.defaultSymbolVisibility]) {
            builder.appendLine("${nextIndentString}defaultSymbolVisibility=${this.defaultSymbolVisibility},")
        } else {
            builder.appendLine("${nextIndentString}defaultSymbolVisibility=<unset>,")
        }

        builder.appendExtensions(nextIndentString)
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): FeatureSetInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: FeatureSetInternal.() -> Unit): FeatureSetInternal {
        val copy = FeatureSetInternal()
        if (presenceMask[PresenceIndices.fieldPresence]) {
            copy.fieldPresence = this.fieldPresence
        }

        if (presenceMask[PresenceIndices.enumType]) {
            copy.enumType = this.enumType
        }

        if (presenceMask[PresenceIndices.repeatedFieldEncoding]) {
            copy.repeatedFieldEncoding = this.repeatedFieldEncoding
        }

        if (presenceMask[PresenceIndices.utf8Validation]) {
            copy.utf8Validation = this.utf8Validation
        }

        if (presenceMask[PresenceIndices.messageEncoding]) {
            copy.messageEncoding = this.messageEncoding
        }

        if (presenceMask[PresenceIndices.jsonFormat]) {
            copy.jsonFormat = this.jsonFormat
        }

        if (presenceMask[PresenceIndices.enforceNamingStyle]) {
            copy.enforceNamingStyle = this.enforceNamingStyle
        }

        if (presenceMask[PresenceIndices.defaultSymbolVisibility]) {
            copy.defaultSymbolVisibility = this.defaultSymbolVisibility
        }

        copy.copyExtensionsFrom(this)
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public class VisibilityFeatureInternal: FeatureSet.VisibilityFeature.Builder, InternalMessage(fieldsWithPresence = 0) {
        @InternalRpcApi
        public override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        public override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        public override fun hashCode(): Int {
            var result = this::class.hashCode()
            return result
        }

        public override fun equals(other: kotlin.Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as VisibilityFeatureInternal
            return true
        }

        public override fun toString(): String {
            return asString()
        }

        public fun asString(indent: Int = 0): String {
            val indentString = " ".repeat(indent)
            val builder = StringBuilder()
            builder.appendLine("FeatureSet.VisibilityFeature(")
            builder.append("${indentString})")
            return builder.toString()
        }

        public override fun copyInternal(): VisibilityFeatureInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        public fun copyInternal(
            body: VisibilityFeatureInternal.() -> Unit,
        ): VisibilityFeatureInternal {
            val copy = VisibilityFeatureInternal()
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        public object MARSHALLER: ProtoGrpcMarshaller<FeatureSet.VisibilityFeature, VisibilityFeatureInternal>() {
            public override fun asInternal(
                value: FeatureSet.VisibilityFeature,
            ): VisibilityFeatureInternal {
                return value.asInternal()
            }

            public override fun newInternal(): VisibilityFeatureInternal {
                return VisibilityFeatureInternal()
            }

            public override fun encodeWith(
                message: VisibilityFeatureInternal,
                encoder: WireEncoder,
                config: ProtoConfig?,
            ) {
                message.encodeWith(encoder, config)
            }

            public override fun decodeWith(
                message: VisibilityFeatureInternal,
                decoder: WireDecoder,
                config: ProtoConfig?,
            ) {
                VisibilityFeatureInternal.decodeWith(message, decoder, config)
            }
        }

        @InternalRpcApi
        public object DESCRIPTOR: ProtoDescriptor<FeatureSet.VisibilityFeature> {
            public override val fullName: String = "google.protobuf.FeatureSet.VisibilityFeature"
        }

        @InternalRpcApi
        public companion object {
            public val DEFAULT: FeatureSet.VisibilityFeature by lazy { VisibilityFeatureInternal() }
        }
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<FeatureSet, FeatureSetInternal>() {
        public override fun asInternal(value: FeatureSet): FeatureSetInternal {
            return value.asInternal()
        }

        public override fun newInternal(): FeatureSetInternal {
            return FeatureSetInternal()
        }

        public override fun encodeWith(
            message: FeatureSetInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: FeatureSetInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            FeatureSetInternal.decodeWith(message, decoder, config)
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<FeatureSet> {
        public override val fullName: String = "google.protobuf.FeatureSet"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: FeatureSet by lazy { FeatureSetInternal() }
    }
}

@InternalRpcApi
public class FeatureSetDefaultsInternal: FeatureSetDefaults.Builder, InternalMessage(fieldsWithPresence = 2) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val minimumEdition: Int = 0
        const val maximumEdition: Int = 1
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __defaultsDelegate: MsgFieldDelegate<List<FeatureSetDefaults.FeatureSetEditionDefault>> = MsgFieldDelegate { emptyList() }
    public override var defaults: List<FeatureSetDefaults.FeatureSetEditionDefault> by __defaultsDelegate
    internal val __minimumEditionDelegate: MsgFieldDelegate<Edition> = MsgFieldDelegate(PresenceIndices.minimumEdition) { Edition.EDITION_UNKNOWN }
    public override var minimumEdition: Edition by __minimumEditionDelegate
    public override fun clearMinimumEdition() {
        __minimumEditionDelegate.clearField(this)
    }

    internal val __maximumEditionDelegate: MsgFieldDelegate<Edition> = MsgFieldDelegate(PresenceIndices.maximumEdition) { Edition.EDITION_UNKNOWN }
    public override var maximumEdition: Edition by __maximumEditionDelegate

    public override fun clearMaximumEdition() {
        __maximumEditionDelegate.clearField(this)
    }

    private val _owner: FeatureSetDefaultsInternal = this

    @InternalRpcApi
    public val _presence: FeatureSetDefaultsPresence = object : FeatureSetDefaultsPresence, InternalPresenceObject {
        public override val _message: FeatureSetDefaultsInternal get() = _owner

        public override val hasMinimumEdition: Boolean get() = presenceMask[PresenceIndices.minimumEdition]

        public override val hasMaximumEdition: Boolean get() = presenceMask[PresenceIndices.maximumEdition]
    }

    public override fun hashCode(): Int {
        var result = this.defaults.hashCode()
        result = 31 * result + if (presenceMask[PresenceIndices.minimumEdition]) this.minimumEdition.hashCode() else 0
        result = 31 * result + if (presenceMask[PresenceIndices.maximumEdition]) this.maximumEdition.hashCode() else 0
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FeatureSetDefaultsInternal
        if (presenceMask != other.presenceMask) return false
        if (this.defaults != other.defaults) return false
        if (presenceMask[PresenceIndices.minimumEdition] && this.minimumEdition != other.minimumEdition) return false
        return !presenceMask[PresenceIndices.maximumEdition] || this.maximumEdition == other.maximumEdition
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("FeatureSetDefaults(")
        builder.appendLine("${nextIndentString}defaults=${this.defaults},")
        if (presenceMask[PresenceIndices.minimumEdition]) {
            builder.appendLine("${nextIndentString}minimumEdition=${this.minimumEdition},")
        } else {
            builder.appendLine("${nextIndentString}minimumEdition=<unset>,")
        }

        if (presenceMask[PresenceIndices.maximumEdition]) {
            builder.appendLine("${nextIndentString}maximumEdition=${this.maximumEdition},")
        } else {
            builder.appendLine("${nextIndentString}maximumEdition=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): FeatureSetDefaultsInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(
        body: FeatureSetDefaultsInternal.() -> Unit,
    ): FeatureSetDefaultsInternal {
        val copy = FeatureSetDefaultsInternal()
        copy.defaults = this.defaults.map { it.copy() }
        if (presenceMask[PresenceIndices.minimumEdition]) {
            copy.minimumEdition = this.minimumEdition
        }

        if (presenceMask[PresenceIndices.maximumEdition]) {
            copy.maximumEdition = this.maximumEdition
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public class FeatureSetEditionDefaultInternal: FeatureSetDefaults.FeatureSetEditionDefault.Builder, InternalMessage(fieldsWithPresence = 3) {
        @InternalRpcApi
        internal object PresenceIndices {
            const val edition: Int = 0
            const val overridableFeatures: Int = 1
            const val fixedFeatures: Int = 2
        }

        @InternalRpcApi
        public override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        public override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __editionDelegate: MsgFieldDelegate<Edition> = MsgFieldDelegate(PresenceIndices.edition) { Edition.EDITION_UNKNOWN }
        public override var edition: Edition by __editionDelegate
        public override fun clearEdition() {
            __editionDelegate.clearField(this)
        }

        internal val __overridableFeaturesDelegate: MsgFieldDelegate<FeatureSet> = MsgFieldDelegate(PresenceIndices.overridableFeatures) { FeatureSetInternal.DEFAULT }
        public override var overridableFeatures: FeatureSet by __overridableFeaturesDelegate
        public override fun clearOverridableFeatures() {
            __overridableFeaturesDelegate.clearField(this)
        }

        internal val __fixedFeaturesDelegate: MsgFieldDelegate<FeatureSet> = MsgFieldDelegate(PresenceIndices.fixedFeatures) { FeatureSetInternal.DEFAULT }
        public override var fixedFeatures: FeatureSet by __fixedFeaturesDelegate

        public override fun clearFixedFeatures() {
            __fixedFeaturesDelegate.clearField(this)
        }

        private val _owner: FeatureSetEditionDefaultInternal = this

        @InternalRpcApi
        public val _presence: FeatureSetDefaultsPresence.FeatureSetEditionDefault = object : FeatureSetDefaultsPresence.FeatureSetEditionDefault, InternalPresenceObject {
            public override val _message: FeatureSetEditionDefaultInternal get() = _owner

            public override val hasEdition: Boolean get() = presenceMask[PresenceIndices.edition]

            public override val hasOverridableFeatures: Boolean get() = presenceMask[PresenceIndices.overridableFeatures]

            public override val hasFixedFeatures: Boolean get() = presenceMask[PresenceIndices.fixedFeatures]
        }

        public override fun hashCode(): Int {
            var result = if (presenceMask[PresenceIndices.edition]) this.edition.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.overridableFeatures]) this.overridableFeatures.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.fixedFeatures]) this.fixedFeatures.hashCode() else 0
            return result
        }

        public override fun equals(other: kotlin.Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as FeatureSetEditionDefaultInternal
            if (presenceMask != other.presenceMask) return false
            if (presenceMask[PresenceIndices.edition] && this.edition != other.edition) return false
            if (presenceMask[PresenceIndices.overridableFeatures] && this.overridableFeatures != other.overridableFeatures) return false
            return !presenceMask[PresenceIndices.fixedFeatures] || this.fixedFeatures == other.fixedFeatures
        }

        public override fun toString(): String {
            return asString()
        }

        public fun asString(indent: Int = 0): String {
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("FeatureSetDefaults.FeatureSetEditionDefault(")
            if (presenceMask[PresenceIndices.edition]) {
                builder.appendLine("${nextIndentString}edition=${this.edition},")
            } else {
                builder.appendLine("${nextIndentString}edition=<unset>,")
            }

            if (presenceMask[PresenceIndices.overridableFeatures]) {
                builder.appendLine("${nextIndentString}overridableFeatures=${this.overridableFeatures.asInternal().asString(indent = indent + 4)},")
            } else {
                builder.appendLine("${nextIndentString}overridableFeatures=<unset>,")
            }

            if (presenceMask[PresenceIndices.fixedFeatures]) {
                builder.appendLine("${nextIndentString}fixedFeatures=${this.fixedFeatures.asInternal().asString(indent = indent + 4)},")
            } else {
                builder.appendLine("${nextIndentString}fixedFeatures=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        public override fun copyInternal(): FeatureSetEditionDefaultInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        public fun copyInternal(
            body: FeatureSetEditionDefaultInternal.() -> Unit,
        ): FeatureSetEditionDefaultInternal {
            val copy = FeatureSetEditionDefaultInternal()
            if (presenceMask[PresenceIndices.edition]) {
                copy.edition = this.edition
            }

            if (presenceMask[PresenceIndices.overridableFeatures]) {
                copy.overridableFeatures = this.overridableFeatures.copy()
            }

            if (presenceMask[PresenceIndices.fixedFeatures]) {
                copy.fixedFeatures = this.fixedFeatures.copy()
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        public object MARSHALLER: ProtoGrpcMarshaller<FeatureSetDefaults.FeatureSetEditionDefault, FeatureSetEditionDefaultInternal>() {
            public override fun asInternal(
                value: FeatureSetDefaults.FeatureSetEditionDefault,
            ): FeatureSetEditionDefaultInternal {
                return value.asInternal()
            }

            public override fun newInternal(): FeatureSetEditionDefaultInternal {
                return FeatureSetEditionDefaultInternal()
            }

            public override fun encodeWith(
                message: FeatureSetEditionDefaultInternal,
                encoder: WireEncoder,
                config: ProtoConfig?,
            ) {
                message.encodeWith(encoder, config)
            }

            public override fun decodeWith(
                message: FeatureSetEditionDefaultInternal,
                decoder: WireDecoder,
                config: ProtoConfig?,
            ) {
                FeatureSetEditionDefaultInternal.decodeWith(message, decoder, config)
            }
        }

        @InternalRpcApi
        public object DESCRIPTOR: ProtoDescriptor<FeatureSetDefaults.FeatureSetEditionDefault> {
            public override val fullName: String = "google.protobuf.FeatureSetDefaults.FeatureSetEditionDefault"
        }

        @InternalRpcApi
        public companion object {
            public val DEFAULT: FeatureSetDefaults.FeatureSetEditionDefault by lazy { FeatureSetEditionDefaultInternal() }
        }
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<FeatureSetDefaults, FeatureSetDefaultsInternal>() {
        public override fun asInternal(value: FeatureSetDefaults): FeatureSetDefaultsInternal {
            return value.asInternal()
        }

        public override fun newInternal(): FeatureSetDefaultsInternal {
            return FeatureSetDefaultsInternal()
        }

        public override fun encodeWith(
            message: FeatureSetDefaultsInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: FeatureSetDefaultsInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            FeatureSetDefaultsInternal.decodeWith(message, decoder, config)
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<FeatureSetDefaults> {
        public override val fullName: String = "google.protobuf.FeatureSetDefaults"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: FeatureSetDefaults by lazy { FeatureSetDefaultsInternal() }
    }
}

@InternalRpcApi
public class SourceCodeInfoInternal: SourceCodeInfo.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __locationDelegate: MsgFieldDelegate<List<SourceCodeInfo.Location>> = MsgFieldDelegate { emptyList() }
    public override var location: List<SourceCodeInfo.Location> by __locationDelegate

    private val _owner: SourceCodeInfoInternal = this

    @InternalRpcApi
    public val _presence: SourceCodeInfoPresence = object : SourceCodeInfoPresence, InternalPresenceObject {
        public override val _message: SourceCodeInfoInternal get() = _owner
    }

    public override fun hashCode(): Int {
        var result = this.location.hashCode()
        result = 31 * result + extensionsHashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SourceCodeInfoInternal
        if (this.location != other.location) return false
        return extensionsEqual(other)
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("SourceCodeInfo(")
        builder.appendLine("${nextIndentString}location=${this.location},")
        builder.appendExtensions(nextIndentString)
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): SourceCodeInfoInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: SourceCodeInfoInternal.() -> Unit): SourceCodeInfoInternal {
        val copy = SourceCodeInfoInternal()
        copy.location = this.location.map { it.copy() }
        copy.copyExtensionsFrom(this)
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public class LocationInternal: SourceCodeInfo.Location.Builder, InternalMessage(fieldsWithPresence = 2) {
        @InternalRpcApi
        internal object PresenceIndices {
            const val leadingComments: Int = 0
            const val trailingComments: Int = 1
        }

        @InternalRpcApi
        public override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        public override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __pathDelegate: MsgFieldDelegate<List<Int>> = MsgFieldDelegate { emptyList() }
        public override var path: List<Int> by __pathDelegate
        internal val __spanDelegate: MsgFieldDelegate<List<Int>> = MsgFieldDelegate { emptyList() }
        public override var span: List<Int> by __spanDelegate
        internal val __leadingCommentsDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.leadingComments) { "" }
        public override var leadingComments: String by __leadingCommentsDelegate
        public override fun clearLeadingComments() {
            __leadingCommentsDelegate.clearField(this)
        }

        internal val __trailingCommentsDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.trailingComments) { "" }
        public override var trailingComments: String by __trailingCommentsDelegate
        public override fun clearTrailingComments() {
            __trailingCommentsDelegate.clearField(this)
        }

        internal val __leadingDetachedCommentsDelegate: MsgFieldDelegate<List<String>> = MsgFieldDelegate { emptyList() }
        public override var leadingDetachedComments: List<String> by __leadingDetachedCommentsDelegate

        private val _owner: LocationInternal = this

        @InternalRpcApi
        public val _presence: SourceCodeInfoPresence.Location = object : SourceCodeInfoPresence.Location, InternalPresenceObject {
            public override val _message: LocationInternal get() = _owner

            public override val hasLeadingComments: Boolean get() = presenceMask[PresenceIndices.leadingComments]

            public override val hasTrailingComments: Boolean get() = presenceMask[PresenceIndices.trailingComments]
        }

        public override fun hashCode(): Int {
            var result = this.path.hashCode()
            result = 31 * result + this.span.hashCode()
            result = 31 * result + if (presenceMask[PresenceIndices.leadingComments]) this.leadingComments.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.trailingComments]) this.trailingComments.hashCode() else 0
            result = 31 * result + this.leadingDetachedComments.hashCode()
            return result
        }

        public override fun equals(other: kotlin.Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as LocationInternal
            if (presenceMask != other.presenceMask) return false
            if (this.path != other.path) return false
            if (this.span != other.span) return false
            if (presenceMask[PresenceIndices.leadingComments] && this.leadingComments != other.leadingComments) return false
            if (presenceMask[PresenceIndices.trailingComments] && this.trailingComments != other.trailingComments) return false
            return this.leadingDetachedComments == other.leadingDetachedComments
        }

        public override fun toString(): String {
            return asString()
        }

        public fun asString(indent: Int = 0): String {
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("SourceCodeInfo.Location(")
            builder.appendLine("${nextIndentString}path=${this.path},")
            builder.appendLine("${nextIndentString}span=${this.span},")
            if (presenceMask[PresenceIndices.leadingComments]) {
                builder.appendLine("${nextIndentString}leadingComments=${this.leadingComments},")
            } else {
                builder.appendLine("${nextIndentString}leadingComments=<unset>,")
            }

            if (presenceMask[PresenceIndices.trailingComments]) {
                builder.appendLine("${nextIndentString}trailingComments=${this.trailingComments},")
            } else {
                builder.appendLine("${nextIndentString}trailingComments=<unset>,")
            }

            builder.appendLine("${nextIndentString}leadingDetachedComments=${this.leadingDetachedComments},")
            builder.append("${indentString})")
            return builder.toString()
        }

        public override fun copyInternal(): LocationInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        public fun copyInternal(body: LocationInternal.() -> Unit): LocationInternal {
            val copy = LocationInternal()
            copy.path = this.path.map { it }
            copy.span = this.span.map { it }
            if (presenceMask[PresenceIndices.leadingComments]) {
                copy.leadingComments = this.leadingComments
            }

            if (presenceMask[PresenceIndices.trailingComments]) {
                copy.trailingComments = this.trailingComments
            }

            copy.leadingDetachedComments = this.leadingDetachedComments.map { it }
            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        public object MARSHALLER: ProtoGrpcMarshaller<SourceCodeInfo.Location, LocationInternal>() {
            public override fun asInternal(value: SourceCodeInfo.Location): LocationInternal {
                return value.asInternal()
            }

            public override fun newInternal(): LocationInternal {
                return LocationInternal()
            }

            public override fun encodeWith(
                message: LocationInternal,
                encoder: WireEncoder,
                config: ProtoConfig?,
            ) {
                message.encodeWith(encoder, config)
            }

            public override fun decodeWith(
                message: LocationInternal,
                decoder: WireDecoder,
                config: ProtoConfig?,
            ) {
                LocationInternal.decodeWith(message, decoder, config)
            }
        }

        @InternalRpcApi
        public object DESCRIPTOR: ProtoDescriptor<SourceCodeInfo.Location> {
            public override val fullName: String = "google.protobuf.SourceCodeInfo.Location"
        }

        @InternalRpcApi
        public companion object {
            public val DEFAULT: SourceCodeInfo.Location by lazy { LocationInternal() }
        }
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<SourceCodeInfo, SourceCodeInfoInternal>() {
        public override fun asInternal(value: SourceCodeInfo): SourceCodeInfoInternal {
            return value.asInternal()
        }

        public override fun newInternal(): SourceCodeInfoInternal {
            return SourceCodeInfoInternal()
        }

        public override fun encodeWith(
            message: SourceCodeInfoInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: SourceCodeInfoInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            SourceCodeInfoInternal.decodeWith(message, decoder, config)
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<SourceCodeInfo> {
        public override val fullName: String = "google.protobuf.SourceCodeInfo"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: SourceCodeInfo by lazy { SourceCodeInfoInternal() }
    }
}

@InternalRpcApi
public class GeneratedCodeInfoInternal: GeneratedCodeInfo.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __annotationDelegate: MsgFieldDelegate<List<GeneratedCodeInfo.Annotation>> = MsgFieldDelegate { emptyList() }
    public override var annotation: List<GeneratedCodeInfo.Annotation> by __annotationDelegate

    public override fun hashCode(): Int {
        var result = this.annotation.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as GeneratedCodeInfoInternal
        return this.annotation == other.annotation
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("GeneratedCodeInfo(")
        builder.appendLine("${nextIndentString}annotation=${this.annotation},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): GeneratedCodeInfoInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: GeneratedCodeInfoInternal.() -> Unit): GeneratedCodeInfoInternal {
        val copy = GeneratedCodeInfoInternal()
        copy.annotation = this.annotation.map { it.copy() }
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public class AnnotationInternal: GeneratedCodeInfo.Annotation.Builder, InternalMessage(fieldsWithPresence = 4) {
        @InternalRpcApi
        internal object PresenceIndices {
            const val sourceFile: Int = 0
            const val begin: Int = 1
            const val end: Int = 2
            const val semantic: Int = 3
        }

        @InternalRpcApi
        public override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        public override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __pathDelegate: MsgFieldDelegate<List<Int>> = MsgFieldDelegate { emptyList() }
        public override var path: List<Int> by __pathDelegate
        internal val __sourceFileDelegate: MsgFieldDelegate<String> = MsgFieldDelegate(PresenceIndices.sourceFile) { "" }
        public override var sourceFile: String by __sourceFileDelegate
        public override fun clearSourceFile() {
            __sourceFileDelegate.clearField(this)
        }

        internal val __beginDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.begin) { 0 }
        public override var begin: Int by __beginDelegate
        public override fun clearBegin() {
            __beginDelegate.clearField(this)
        }

        internal val __endDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate(PresenceIndices.end) { 0 }
        public override var end: Int by __endDelegate
        public override fun clearEnd() {
            __endDelegate.clearField(this)
        }

        internal val __semanticDelegate: MsgFieldDelegate<GeneratedCodeInfo.Annotation.Semantic> = MsgFieldDelegate(PresenceIndices.semantic) { GeneratedCodeInfo.Annotation.Semantic.NONE }
        public override var semantic: GeneratedCodeInfo.Annotation.Semantic by __semanticDelegate

        public override fun clearSemantic() {
            __semanticDelegate.clearField(this)
        }

        private val _owner: AnnotationInternal = this

        @InternalRpcApi
        public val _presence: GeneratedCodeInfoPresence.Annotation = object : GeneratedCodeInfoPresence.Annotation, InternalPresenceObject {
            public override val _message: AnnotationInternal get() = _owner

            public override val hasSourceFile: Boolean get() = presenceMask[PresenceIndices.sourceFile]

            public override val hasBegin: Boolean get() = presenceMask[PresenceIndices.begin]

            public override val hasEnd: Boolean get() = presenceMask[PresenceIndices.end]

            public override val hasSemantic: Boolean get() = presenceMask[PresenceIndices.semantic]
        }

        public override fun hashCode(): Int {
            var result = this.path.hashCode()
            result = 31 * result + if (presenceMask[PresenceIndices.sourceFile]) this.sourceFile.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.begin]) this.begin.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.end]) this.end.hashCode() else 0
            result = 31 * result + if (presenceMask[PresenceIndices.semantic]) this.semantic.hashCode() else 0
            return result
        }

        public override fun equals(other: kotlin.Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as AnnotationInternal
            if (presenceMask != other.presenceMask) return false
            if (this.path != other.path) return false
            if (presenceMask[PresenceIndices.sourceFile] && this.sourceFile != other.sourceFile) return false
            if (presenceMask[PresenceIndices.begin] && this.begin != other.begin) return false
            if (presenceMask[PresenceIndices.end] && this.end != other.end) return false
            return !presenceMask[PresenceIndices.semantic] || this.semantic == other.semantic
        }

        public override fun toString(): String {
            return asString()
        }

        public fun asString(indent: Int = 0): String {
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("GeneratedCodeInfo.Annotation(")
            builder.appendLine("${nextIndentString}path=${this.path},")
            if (presenceMask[PresenceIndices.sourceFile]) {
                builder.appendLine("${nextIndentString}sourceFile=${this.sourceFile},")
            } else {
                builder.appendLine("${nextIndentString}sourceFile=<unset>,")
            }

            if (presenceMask[PresenceIndices.begin]) {
                builder.appendLine("${nextIndentString}begin=${this.begin},")
            } else {
                builder.appendLine("${nextIndentString}begin=<unset>,")
            }

            if (presenceMask[PresenceIndices.end]) {
                builder.appendLine("${nextIndentString}end=${this.end},")
            } else {
                builder.appendLine("${nextIndentString}end=<unset>,")
            }

            if (presenceMask[PresenceIndices.semantic]) {
                builder.appendLine("${nextIndentString}semantic=${this.semantic},")
            } else {
                builder.appendLine("${nextIndentString}semantic=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        public override fun copyInternal(): AnnotationInternal {
            return copyInternal { }
        }

        @InternalRpcApi
        public fun copyInternal(body: AnnotationInternal.() -> Unit): AnnotationInternal {
            val copy = AnnotationInternal()
            copy.path = this.path.map { it }
            if (presenceMask[PresenceIndices.sourceFile]) {
                copy.sourceFile = this.sourceFile
            }

            if (presenceMask[PresenceIndices.begin]) {
                copy.begin = this.begin
            }

            if (presenceMask[PresenceIndices.end]) {
                copy.end = this.end
            }

            if (presenceMask[PresenceIndices.semantic]) {
                copy.semantic = this.semantic
            }

            copy.apply(body)
            this._unknownFields.copyTo(copy._unknownFields)
            return copy
        }

        @InternalRpcApi
        public object MARSHALLER: ProtoGrpcMarshaller<GeneratedCodeInfo.Annotation, AnnotationInternal>() {
            public override fun asInternal(
                value: GeneratedCodeInfo.Annotation,
            ): AnnotationInternal {
                return value.asInternal()
            }

            public override fun newInternal(): AnnotationInternal {
                return AnnotationInternal()
            }

            public override fun encodeWith(
                message: AnnotationInternal,
                encoder: WireEncoder,
                config: ProtoConfig?,
            ) {
                message.encodeWith(encoder, config)
            }

            public override fun decodeWith(
                message: AnnotationInternal,
                decoder: WireDecoder,
                config: ProtoConfig?,
            ) {
                AnnotationInternal.decodeWith(message, decoder, config)
            }
        }

        @InternalRpcApi
        public object DESCRIPTOR: ProtoDescriptor<GeneratedCodeInfo.Annotation> {
            public override val fullName: String = "google.protobuf.GeneratedCodeInfo.Annotation"
        }

        @InternalRpcApi
        public companion object {
            public val DEFAULT: GeneratedCodeInfo.Annotation by lazy { AnnotationInternal() }
        }
    }

    @InternalRpcApi
    public object MARSHALLER: ProtoGrpcMarshaller<GeneratedCodeInfo, GeneratedCodeInfoInternal>() {
        public override fun asInternal(value: GeneratedCodeInfo): GeneratedCodeInfoInternal {
            return value.asInternal()
        }

        public override fun newInternal(): GeneratedCodeInfoInternal {
            return GeneratedCodeInfoInternal()
        }

        public override fun encodeWith(
            message: GeneratedCodeInfoInternal,
            encoder: WireEncoder,
            config: ProtoConfig?,
        ) {
            message.encodeWith(encoder, config)
        }

        public override fun decodeWith(
            message: GeneratedCodeInfoInternal,
            decoder: WireDecoder,
            config: ProtoConfig?,
        ) {
            GeneratedCodeInfoInternal.decodeWith(message, decoder, config)
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<GeneratedCodeInfo> {
        public override val fullName: String = "google.protobuf.GeneratedCodeInfo"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: GeneratedCodeInfo by lazy { GeneratedCodeInfoInternal() }
    }
}

@InternalRpcApi
public fun FileDescriptorSetInternal.checkRequiredFields() {
    this.file.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun FileDescriptorSetInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.file.isNotEmpty()) {
        this.file.forEach {
            encoder.writeMessage(fieldNr = 1, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun FileDescriptorSetInternal.Companion.decodeWith(
    msg: FileDescriptorSetInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(FileDescriptorSet::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__fileDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = FileDescriptorProtoInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> FileDescriptorProtoInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun FileDescriptorSetInternal.computeSize(): Int {
    var __result = 0
    if (this.file.isNotEmpty()) {
        __result += this.file.sumOf { element -> element.asInternal()._size.let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun FileDescriptorSet.asInternal(): FileDescriptorSetInternal {
    return this as? FileDescriptorSetInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun FileDescriptorProtoInternal.checkRequiredFields() {
    if (presenceMask[2]) {
        this.options.asInternal().checkRequiredFields()
    }

    this.messageType.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.enumType.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.service.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.extension.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun FileDescriptorProtoInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[FileDescriptorProtoInternal.PresenceIndices.name]) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (presenceMask[FileDescriptorProtoInternal.PresenceIndices.`package`]) {
        encoder.writeString(fieldNr = 2, value = this.`package`)
    }

    if (this.dependency.isNotEmpty()) {
        this.dependency.forEach {
            encoder.writeString(3, it)
        }
    }

    if (this.publicDependency.isNotEmpty()) {
        this.publicDependency.forEach {
            encoder.writeInt32(10, it)
        }
    }

    if (this.weakDependency.isNotEmpty()) {
        this.weakDependency.forEach {
            encoder.writeInt32(11, it)
        }
    }

    if (this.optionDependency.isNotEmpty()) {
        this.optionDependency.forEach {
            encoder.writeString(15, it)
        }
    }

    if (this.messageType.isNotEmpty()) {
        this.messageType.forEach {
            encoder.writeMessage(fieldNr = 4, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (this.enumType.isNotEmpty()) {
        this.enumType.forEach {
            encoder.writeMessage(fieldNr = 5, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (this.service.isNotEmpty()) {
        this.service.forEach {
            encoder.writeMessage(fieldNr = 6, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (this.extension.isNotEmpty()) {
        this.extension.forEach {
            encoder.writeMessage(fieldNr = 7, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (presenceMask[FileDescriptorProtoInternal.PresenceIndices.options]) {
        encoder.writeMessage(fieldNr = 8, value = this.options.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (presenceMask[FileDescriptorProtoInternal.PresenceIndices.sourceCodeInfo]) {
        encoder.writeMessage(fieldNr = 9, value = this.sourceCodeInfo.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (presenceMask[FileDescriptorProtoInternal.PresenceIndices.syntax]) {
        encoder.writeString(fieldNr = 12, value = this.syntax)
    }

    if (presenceMask[FileDescriptorProtoInternal.PresenceIndices.edition]) {
        encoder.writeEnum(fieldNr = 14, value = this.edition.number)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun FileDescriptorProtoInternal.Companion.decodeWith(
    msg: FileDescriptorProtoInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.`package` = decoder.readString()
            }
            3 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__dependencyDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readString()
                target.add(elem)
            }
            10 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__publicDependencyDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedInt32())
            }
            10 if tag.wireType == WireType.VARINT -> {
                val target = msg.__publicDependencyDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readInt32()
                target.add(elem)
            }
            11 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__weakDependencyDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedInt32())
            }
            11 if tag.wireType == WireType.VARINT -> {
                val target = msg.__weakDependencyDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readInt32()
                target.add(elem)
            }
            15 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionDependencyDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readString()
                target.add(elem)
            }
            4 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__messageTypeDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = DescriptorProtoInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> DescriptorProtoInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            5 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__enumTypeDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = EnumDescriptorProtoInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> EnumDescriptorProtoInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            6 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__serviceDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = ServiceDescriptorProtoInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> ServiceDescriptorProtoInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            7 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__extensionDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = FieldDescriptorProtoInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> FieldDescriptorProtoInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            8 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionsDelegate.getOrCreate(msg) { FileOptionsInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> FileOptionsInternal.decodeWith(msg, decoder, config) }
            }
            9 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__sourceCodeInfoDelegate.getOrCreate(msg) { SourceCodeInfoInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> SourceCodeInfoInternal.decodeWith(msg, decoder, config) }
            }
            12 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.syntax = decoder.readString()
            }
            14 if tag.wireType == WireType.VARINT -> {
                msg.edition = Edition.fromNumber(decoder.readEnum())
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun FileDescriptorProtoInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[FileDescriptorProtoInternal.PresenceIndices.name]) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FileDescriptorProtoInternal.PresenceIndices.`package`]) {
        __result += WireSize.string(this.`package`).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.dependency.isNotEmpty()) {
        __result += this.dependency.sumOf { element -> WireSize.string(element).let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.publicDependency.isNotEmpty()) {
        __result += this.publicDependency.sumOf { element -> WireSize.tag(10, WireType.VARINT) + WireSize.int32(element) }
    }

    if (this.weakDependency.isNotEmpty()) {
        __result += this.weakDependency.sumOf { element -> WireSize.tag(11, WireType.VARINT) + WireSize.int32(element) }
    }

    if (this.optionDependency.isNotEmpty()) {
        __result += this.optionDependency.sumOf { element -> WireSize.string(element).let { WireSize.tag(15, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.messageType.isNotEmpty()) {
        __result += this.messageType.sumOf { element -> element.asInternal()._size.let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.enumType.isNotEmpty()) {
        __result += this.enumType.sumOf { element -> element.asInternal()._size.let { WireSize.tag(5, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.service.isNotEmpty()) {
        __result += this.service.sumOf { element -> element.asInternal()._size.let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.extension.isNotEmpty()) {
        __result += this.extension.sumOf { element -> element.asInternal()._size.let { WireSize.tag(7, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (presenceMask[FileDescriptorProtoInternal.PresenceIndices.options]) {
        __result += this.options.asInternal()._size.let { WireSize.tag(8, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FileDescriptorProtoInternal.PresenceIndices.sourceCodeInfo]) {
        __result += this.sourceCodeInfo.asInternal()._size.let { WireSize.tag(9, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FileDescriptorProtoInternal.PresenceIndices.syntax]) {
        __result += WireSize.string(this.syntax).let { WireSize.tag(12, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FileDescriptorProtoInternal.PresenceIndices.edition]) {
        __result += WireSize.tag(14, WireType.VARINT) + WireSize.enum(this.edition.number)
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun FileDescriptorProto.asInternal(): FileDescriptorProtoInternal {
    return this as? FileDescriptorProtoInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun DescriptorProtoInternal.checkRequiredFields() {
    if (presenceMask[1]) {
        this.options.asInternal().checkRequiredFields()
    }

    this.field.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.extension.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.nestedType.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.enumType.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.extensionRange.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.oneofDecl.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun DescriptorProtoInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[DescriptorProtoInternal.PresenceIndices.name]) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (this.field.isNotEmpty()) {
        this.field.forEach {
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (this.extension.isNotEmpty()) {
        this.extension.forEach {
            encoder.writeMessage(fieldNr = 6, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (this.nestedType.isNotEmpty()) {
        this.nestedType.forEach {
            encoder.writeMessage(fieldNr = 3, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (this.enumType.isNotEmpty()) {
        this.enumType.forEach {
            encoder.writeMessage(fieldNr = 4, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (this.extensionRange.isNotEmpty()) {
        this.extensionRange.forEach {
            encoder.writeMessage(fieldNr = 5, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (this.oneofDecl.isNotEmpty()) {
        this.oneofDecl.forEach {
            encoder.writeMessage(fieldNr = 8, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (presenceMask[DescriptorProtoInternal.PresenceIndices.options]) {
        encoder.writeMessage(fieldNr = 7, value = this.options.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (this.reservedRange.isNotEmpty()) {
        this.reservedRange.forEach {
            encoder.writeMessage(fieldNr = 9, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (this.reservedName.isNotEmpty()) {
        this.reservedName.forEach {
            encoder.writeString(10, it)
        }
    }

    if (presenceMask[DescriptorProtoInternal.PresenceIndices.visibility]) {
        encoder.writeEnum(fieldNr = 11, value = this.visibility.number)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun DescriptorProtoInternal.Companion.decodeWith(
    msg: DescriptorProtoInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__fieldDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = FieldDescriptorProtoInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> FieldDescriptorProtoInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            6 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__extensionDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = FieldDescriptorProtoInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> FieldDescriptorProtoInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            3 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__nestedTypeDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = DescriptorProtoInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> DescriptorProtoInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            4 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__enumTypeDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = EnumDescriptorProtoInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> EnumDescriptorProtoInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            5 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__extensionRangeDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = DescriptorProtoInternal.ExtensionRangeInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> DescriptorProtoInternal.ExtensionRangeInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            8 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__oneofDeclDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = OneofDescriptorProtoInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> OneofDescriptorProtoInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            7 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionsDelegate.getOrCreate(msg) { MessageOptionsInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> MessageOptionsInternal.decodeWith(msg, decoder, config) }
            }
            9 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__reservedRangeDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = DescriptorProtoInternal.ReservedRangeInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> DescriptorProtoInternal.ReservedRangeInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            10 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__reservedNameDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readString()
                target.add(elem)
            }
            11 if tag.wireType == WireType.VARINT -> {
                msg.visibility = SymbolVisibility.fromNumber(decoder.readEnum())
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun DescriptorProtoInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[DescriptorProtoInternal.PresenceIndices.name]) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.field.isNotEmpty()) {
        __result += this.field.sumOf { element -> element.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.extension.isNotEmpty()) {
        __result += this.extension.sumOf { element -> element.asInternal()._size.let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.nestedType.isNotEmpty()) {
        __result += this.nestedType.sumOf { element -> element.asInternal()._size.let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.enumType.isNotEmpty()) {
        __result += this.enumType.sumOf { element -> element.asInternal()._size.let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.extensionRange.isNotEmpty()) {
        __result += this.extensionRange.sumOf { element -> element.asInternal()._size.let { WireSize.tag(5, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.oneofDecl.isNotEmpty()) {
        __result += this.oneofDecl.sumOf { element -> element.asInternal()._size.let { WireSize.tag(8, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (presenceMask[DescriptorProtoInternal.PresenceIndices.options]) {
        __result += this.options.asInternal()._size.let { WireSize.tag(7, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.reservedRange.isNotEmpty()) {
        __result += this.reservedRange.sumOf { element -> element.asInternal()._size.let { WireSize.tag(9, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.reservedName.isNotEmpty()) {
        __result += this.reservedName.sumOf { element -> WireSize.string(element).let { WireSize.tag(10, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (presenceMask[DescriptorProtoInternal.PresenceIndices.visibility]) {
        __result += WireSize.tag(11, WireType.VARINT) + WireSize.enum(this.visibility.number)
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun DescriptorProto.asInternal(): DescriptorProtoInternal {
    return this as? DescriptorProtoInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun ExtensionRangeOptionsInternal.checkRequiredFields() {
    this.uninterpretedOption.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun ExtensionRangeOptionsInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.uninterpretedOption.isNotEmpty()) {
        this.uninterpretedOption.forEach {
            encoder.writeMessage(fieldNr = 999, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (this.declaration.isNotEmpty()) {
        this.declaration.forEach {
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (presenceMask[ExtensionRangeOptionsInternal.PresenceIndices.features]) {
        encoder.writeMessage(fieldNr = 50, value = this.features.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (presenceMask[ExtensionRangeOptionsInternal.PresenceIndices.verification]) {
        encoder.writeEnum(fieldNr = 3, value = this.verification.number)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun ExtensionRangeOptionsInternal.Companion.decodeWith(
    msg: ExtensionRangeOptionsInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(ExtensionRangeOptions::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            999 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__uninterpretedOptionDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = UninterpretedOptionInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> UninterpretedOptionInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__declarationDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = ExtensionRangeOptionsInternal.DeclarationInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> ExtensionRangeOptionsInternal.DeclarationInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            50 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__featuresDelegate.getOrCreate(msg) { FeatureSetInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> FeatureSetInternal.decodeWith(msg, decoder, config) }
            }
            3 if tag.wireType == WireType.VARINT -> {
                msg.verification = ExtensionRangeOptions.VerificationState.fromNumber(decoder.readEnum())
            }
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun ExtensionRangeOptionsInternal.computeSize(): Int {
    var __result = 0
    if (this.uninterpretedOption.isNotEmpty()) {
        __result += this.uninterpretedOption.sumOf { element -> element.asInternal()._size.let { WireSize.tag(999, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.declaration.isNotEmpty()) {
        __result += this.declaration.sumOf { element -> element.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (presenceMask[ExtensionRangeOptionsInternal.PresenceIndices.features]) {
        __result += this.features.asInternal()._size.let { WireSize.tag(50, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ExtensionRangeOptionsInternal.PresenceIndices.verification]) {
        __result += WireSize.tag(3, WireType.VARINT) + WireSize.enum(this.verification.number)
    }

    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun ExtensionRangeOptions.asInternal(): ExtensionRangeOptionsInternal {
    return this as? ExtensionRangeOptionsInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun FieldDescriptorProtoInternal.checkRequiredFields() {
    if (presenceMask[9]) {
        this.options.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun FieldDescriptorProtoInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.name]) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.number]) {
        encoder.writeInt32(fieldNr = 3, value = this.number)
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.label]) {
        encoder.writeEnum(fieldNr = 4, value = this.label.number)
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.type]) {
        encoder.writeEnum(fieldNr = 5, value = this.type.number)
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.typeName]) {
        encoder.writeString(fieldNr = 6, value = this.typeName)
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.extendee]) {
        encoder.writeString(fieldNr = 2, value = this.extendee)
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.defaultValue]) {
        encoder.writeString(fieldNr = 7, value = this.defaultValue)
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.oneofIndex]) {
        encoder.writeInt32(fieldNr = 9, value = this.oneofIndex)
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.jsonName]) {
        encoder.writeString(fieldNr = 10, value = this.jsonName)
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.options]) {
        encoder.writeMessage(fieldNr = 8, value = this.options.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.proto3Optional]) {
        encoder.writeBool(fieldNr = 17, value = this.proto3Optional)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun FieldDescriptorProtoInternal.Companion.decodeWith(
    msg: FieldDescriptorProtoInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            3 if tag.wireType == WireType.VARINT -> {
                msg.number = decoder.readInt32()
            }
            4 if tag.wireType == WireType.VARINT -> {
                msg.label = FieldDescriptorProto.Label.fromNumber(decoder.readEnum())
            }
            5 if tag.wireType == WireType.VARINT -> {
                msg.type = FieldDescriptorProto.Type.fromNumber(decoder.readEnum())
            }
            6 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.typeName = decoder.readString()
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.extendee = decoder.readString()
            }
            7 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.defaultValue = decoder.readString()
            }
            9 if tag.wireType == WireType.VARINT -> {
                msg.oneofIndex = decoder.readInt32()
            }
            10 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.jsonName = decoder.readString()
            }
            8 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionsDelegate.getOrCreate(msg) { FieldOptionsInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> FieldOptionsInternal.decodeWith(msg, decoder, config) }
            }
            17 if tag.wireType == WireType.VARINT -> {
                msg.proto3Optional = decoder.readBool()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun FieldDescriptorProtoInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.name]) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.number]) {
        __result += WireSize.tag(3, WireType.VARINT) + WireSize.int32(this.number)
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.label]) {
        __result += WireSize.tag(4, WireType.VARINT) + WireSize.enum(this.label.number)
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.type]) {
        __result += WireSize.tag(5, WireType.VARINT) + WireSize.enum(this.type.number)
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.typeName]) {
        __result += WireSize.string(this.typeName).let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.extendee]) {
        __result += WireSize.string(this.extendee).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.defaultValue]) {
        __result += WireSize.string(this.defaultValue).let { WireSize.tag(7, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.oneofIndex]) {
        __result += WireSize.tag(9, WireType.VARINT) + WireSize.int32(this.oneofIndex)
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.jsonName]) {
        __result += WireSize.string(this.jsonName).let { WireSize.tag(10, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.options]) {
        __result += this.options.asInternal()._size.let { WireSize.tag(8, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FieldDescriptorProtoInternal.PresenceIndices.proto3Optional]) {
        __result += WireSize.tag(17, WireType.VARINT) + WireSize.bool(this.proto3Optional)
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun FieldDescriptorProto.asInternal(): FieldDescriptorProtoInternal {
    return this as? FieldDescriptorProtoInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun OneofDescriptorProtoInternal.checkRequiredFields() {
    if (presenceMask[1]) {
        this.options.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun OneofDescriptorProtoInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[OneofDescriptorProtoInternal.PresenceIndices.name]) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (presenceMask[OneofDescriptorProtoInternal.PresenceIndices.options]) {
        encoder.writeMessage(fieldNr = 2, value = this.options.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun OneofDescriptorProtoInternal.Companion.decodeWith(
    msg: OneofDescriptorProtoInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionsDelegate.getOrCreate(msg) { OneofOptionsInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> OneofOptionsInternal.decodeWith(msg, decoder, config) }
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun OneofDescriptorProtoInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[OneofDescriptorProtoInternal.PresenceIndices.name]) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[OneofDescriptorProtoInternal.PresenceIndices.options]) {
        __result += this.options.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun OneofDescriptorProto.asInternal(): OneofDescriptorProtoInternal {
    return this as? OneofDescriptorProtoInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun EnumDescriptorProtoInternal.checkRequiredFields() {
    if (presenceMask[1]) {
        this.options.asInternal().checkRequiredFields()
    }

    this.value.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun EnumDescriptorProtoInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[EnumDescriptorProtoInternal.PresenceIndices.name]) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (this.value.isNotEmpty()) {
        this.value.forEach {
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (presenceMask[EnumDescriptorProtoInternal.PresenceIndices.options]) {
        encoder.writeMessage(fieldNr = 3, value = this.options.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (this.reservedRange.isNotEmpty()) {
        this.reservedRange.forEach {
            encoder.writeMessage(fieldNr = 4, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (this.reservedName.isNotEmpty()) {
        this.reservedName.forEach {
            encoder.writeString(5, it)
        }
    }

    if (presenceMask[EnumDescriptorProtoInternal.PresenceIndices.visibility]) {
        encoder.writeEnum(fieldNr = 6, value = this.visibility.number)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun EnumDescriptorProtoInternal.Companion.decodeWith(
    msg: EnumDescriptorProtoInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__valueDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = EnumValueDescriptorProtoInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> EnumValueDescriptorProtoInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            3 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionsDelegate.getOrCreate(msg) { EnumOptionsInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> EnumOptionsInternal.decodeWith(msg, decoder, config) }
            }
            4 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__reservedRangeDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = EnumDescriptorProtoInternal.EnumReservedRangeInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> EnumDescriptorProtoInternal.EnumReservedRangeInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            5 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__reservedNameDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readString()
                target.add(elem)
            }
            6 if tag.wireType == WireType.VARINT -> {
                msg.visibility = SymbolVisibility.fromNumber(decoder.readEnum())
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun EnumDescriptorProtoInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[EnumDescriptorProtoInternal.PresenceIndices.name]) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.value.isNotEmpty()) {
        __result += this.value.sumOf { element -> element.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (presenceMask[EnumDescriptorProtoInternal.PresenceIndices.options]) {
        __result += this.options.asInternal()._size.let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.reservedRange.isNotEmpty()) {
        __result += this.reservedRange.sumOf { element -> element.asInternal()._size.let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.reservedName.isNotEmpty()) {
        __result += this.reservedName.sumOf { element -> WireSize.string(element).let { WireSize.tag(5, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (presenceMask[EnumDescriptorProtoInternal.PresenceIndices.visibility]) {
        __result += WireSize.tag(6, WireType.VARINT) + WireSize.enum(this.visibility.number)
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun EnumDescriptorProto.asInternal(): EnumDescriptorProtoInternal {
    return this as? EnumDescriptorProtoInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun EnumValueDescriptorProtoInternal.checkRequiredFields() {
    if (presenceMask[2]) {
        this.options.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun EnumValueDescriptorProtoInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[EnumValueDescriptorProtoInternal.PresenceIndices.name]) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (presenceMask[EnumValueDescriptorProtoInternal.PresenceIndices.number]) {
        encoder.writeInt32(fieldNr = 2, value = this.number)
    }

    if (presenceMask[EnumValueDescriptorProtoInternal.PresenceIndices.options]) {
        encoder.writeMessage(fieldNr = 3, value = this.options.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun EnumValueDescriptorProtoInternal.Companion.decodeWith(
    msg: EnumValueDescriptorProtoInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            2 if tag.wireType == WireType.VARINT -> {
                msg.number = decoder.readInt32()
            }
            3 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionsDelegate.getOrCreate(msg) { EnumValueOptionsInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> EnumValueOptionsInternal.decodeWith(msg, decoder, config) }
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun EnumValueDescriptorProtoInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[EnumValueDescriptorProtoInternal.PresenceIndices.name]) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[EnumValueDescriptorProtoInternal.PresenceIndices.number]) {
        __result += WireSize.tag(2, WireType.VARINT) + WireSize.int32(this.number)
    }

    if (presenceMask[EnumValueDescriptorProtoInternal.PresenceIndices.options]) {
        __result += this.options.asInternal()._size.let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun EnumValueDescriptorProto.asInternal(): EnumValueDescriptorProtoInternal {
    return this as? EnumValueDescriptorProtoInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun ServiceDescriptorProtoInternal.checkRequiredFields() {
    if (presenceMask[1]) {
        this.options.asInternal().checkRequiredFields()
    }

    this.method.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun ServiceDescriptorProtoInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[ServiceDescriptorProtoInternal.PresenceIndices.name]) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (this.method.isNotEmpty()) {
        this.method.forEach {
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (presenceMask[ServiceDescriptorProtoInternal.PresenceIndices.options]) {
        encoder.writeMessage(fieldNr = 3, value = this.options.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun ServiceDescriptorProtoInternal.Companion.decodeWith(
    msg: ServiceDescriptorProtoInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__methodDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = MethodDescriptorProtoInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> MethodDescriptorProtoInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            3 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionsDelegate.getOrCreate(msg) { ServiceOptionsInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> ServiceOptionsInternal.decodeWith(msg, decoder, config) }
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun ServiceDescriptorProtoInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[ServiceDescriptorProtoInternal.PresenceIndices.name]) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.method.isNotEmpty()) {
        __result += this.method.sumOf { element -> element.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (presenceMask[ServiceDescriptorProtoInternal.PresenceIndices.options]) {
        __result += this.options.asInternal()._size.let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun ServiceDescriptorProto.asInternal(): ServiceDescriptorProtoInternal {
    return this as? ServiceDescriptorProtoInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun MethodDescriptorProtoInternal.checkRequiredFields() {
    if (presenceMask[3]) {
        this.options.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun MethodDescriptorProtoInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[MethodDescriptorProtoInternal.PresenceIndices.name]) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (presenceMask[MethodDescriptorProtoInternal.PresenceIndices.inputType]) {
        encoder.writeString(fieldNr = 2, value = this.inputType)
    }

    if (presenceMask[MethodDescriptorProtoInternal.PresenceIndices.outputType]) {
        encoder.writeString(fieldNr = 3, value = this.outputType)
    }

    if (presenceMask[MethodDescriptorProtoInternal.PresenceIndices.options]) {
        encoder.writeMessage(fieldNr = 4, value = this.options.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (presenceMask[MethodDescriptorProtoInternal.PresenceIndices.clientStreaming]) {
        encoder.writeBool(fieldNr = 5, value = this.clientStreaming)
    }

    if (presenceMask[MethodDescriptorProtoInternal.PresenceIndices.serverStreaming]) {
        encoder.writeBool(fieldNr = 6, value = this.serverStreaming)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun MethodDescriptorProtoInternal.Companion.decodeWith(
    msg: MethodDescriptorProtoInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.inputType = decoder.readString()
            }
            3 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.outputType = decoder.readString()
            }
            4 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionsDelegate.getOrCreate(msg) { MethodOptionsInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> MethodOptionsInternal.decodeWith(msg, decoder, config) }
            }
            5 if tag.wireType == WireType.VARINT -> {
                msg.clientStreaming = decoder.readBool()
            }
            6 if tag.wireType == WireType.VARINT -> {
                msg.serverStreaming = decoder.readBool()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun MethodDescriptorProtoInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[MethodDescriptorProtoInternal.PresenceIndices.name]) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[MethodDescriptorProtoInternal.PresenceIndices.inputType]) {
        __result += WireSize.string(this.inputType).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[MethodDescriptorProtoInternal.PresenceIndices.outputType]) {
        __result += WireSize.string(this.outputType).let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[MethodDescriptorProtoInternal.PresenceIndices.options]) {
        __result += this.options.asInternal()._size.let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[MethodDescriptorProtoInternal.PresenceIndices.clientStreaming]) {
        __result += WireSize.tag(5, WireType.VARINT) + WireSize.bool(this.clientStreaming)
    }

    if (presenceMask[MethodDescriptorProtoInternal.PresenceIndices.serverStreaming]) {
        __result += WireSize.tag(6, WireType.VARINT) + WireSize.bool(this.serverStreaming)
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun MethodDescriptorProto.asInternal(): MethodDescriptorProtoInternal {
    return this as? MethodDescriptorProtoInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun FileOptionsInternal.checkRequiredFields() {
    this.uninterpretedOption.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun FileOptionsInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[FileOptionsInternal.PresenceIndices.javaPackage]) {
        encoder.writeString(fieldNr = 1, value = this.javaPackage)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.javaOuterClassname]) {
        encoder.writeString(fieldNr = 8, value = this.javaOuterClassname)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.javaMultipleFiles]) {
        encoder.writeBool(fieldNr = 10, value = this.javaMultipleFiles)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.javaGenerateEqualsAndHash]) {
        encoder.writeBool(fieldNr = 20, value = this.javaGenerateEqualsAndHash)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.javaStringCheckUtf8]) {
        encoder.writeBool(fieldNr = 27, value = this.javaStringCheckUtf8)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.optimizeFor]) {
        encoder.writeEnum(fieldNr = 9, value = this.optimizeFor.number)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.goPackage]) {
        encoder.writeString(fieldNr = 11, value = this.goPackage)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.ccGenericServices]) {
        encoder.writeBool(fieldNr = 16, value = this.ccGenericServices)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.javaGenericServices]) {
        encoder.writeBool(fieldNr = 17, value = this.javaGenericServices)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.pyGenericServices]) {
        encoder.writeBool(fieldNr = 18, value = this.pyGenericServices)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.deprecated]) {
        encoder.writeBool(fieldNr = 23, value = this.deprecated)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.ccEnableArenas]) {
        encoder.writeBool(fieldNr = 31, value = this.ccEnableArenas)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.objcClassPrefix]) {
        encoder.writeString(fieldNr = 36, value = this.objcClassPrefix)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.csharpNamespace]) {
        encoder.writeString(fieldNr = 37, value = this.csharpNamespace)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.swiftPrefix]) {
        encoder.writeString(fieldNr = 39, value = this.swiftPrefix)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.phpClassPrefix]) {
        encoder.writeString(fieldNr = 40, value = this.phpClassPrefix)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.phpNamespace]) {
        encoder.writeString(fieldNr = 41, value = this.phpNamespace)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.phpMetadataNamespace]) {
        encoder.writeString(fieldNr = 44, value = this.phpMetadataNamespace)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.rubyPackage]) {
        encoder.writeString(fieldNr = 45, value = this.rubyPackage)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.features]) {
        encoder.writeMessage(fieldNr = 50, value = this.features.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        this.uninterpretedOption.forEach {
            encoder.writeMessage(fieldNr = 999, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun FileOptionsInternal.Companion.decodeWith(
    msg: FileOptionsInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(FileOptions::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.javaPackage = decoder.readString()
            }
            8 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.javaOuterClassname = decoder.readString()
            }
            10 if tag.wireType == WireType.VARINT -> {
                msg.javaMultipleFiles = decoder.readBool()
            }
            20 if tag.wireType == WireType.VARINT -> {
                msg.javaGenerateEqualsAndHash = decoder.readBool()
            }
            27 if tag.wireType == WireType.VARINT -> {
                msg.javaStringCheckUtf8 = decoder.readBool()
            }
            9 if tag.wireType == WireType.VARINT -> {
                msg.optimizeFor = FileOptions.OptimizeMode.fromNumber(decoder.readEnum())
            }
            11 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.goPackage = decoder.readString()
            }
            16 if tag.wireType == WireType.VARINT -> {
                msg.ccGenericServices = decoder.readBool()
            }
            17 if tag.wireType == WireType.VARINT -> {
                msg.javaGenericServices = decoder.readBool()
            }
            18 if tag.wireType == WireType.VARINT -> {
                msg.pyGenericServices = decoder.readBool()
            }
            23 if tag.wireType == WireType.VARINT -> {
                msg.deprecated = decoder.readBool()
            }
            31 if tag.wireType == WireType.VARINT -> {
                msg.ccEnableArenas = decoder.readBool()
            }
            36 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.objcClassPrefix = decoder.readString()
            }
            37 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.csharpNamespace = decoder.readString()
            }
            39 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.swiftPrefix = decoder.readString()
            }
            40 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.phpClassPrefix = decoder.readString()
            }
            41 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.phpNamespace = decoder.readString()
            }
            44 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.phpMetadataNamespace = decoder.readString()
            }
            45 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.rubyPackage = decoder.readString()
            }
            50 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__featuresDelegate.getOrCreate(msg) { FeatureSetInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> FeatureSetInternal.decodeWith(msg, decoder, config) }
            }
            999 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__uninterpretedOptionDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = UninterpretedOptionInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> UninterpretedOptionInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun FileOptionsInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[FileOptionsInternal.PresenceIndices.javaPackage]) {
        __result += WireSize.string(this.javaPackage).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.javaOuterClassname]) {
        __result += WireSize.string(this.javaOuterClassname).let { WireSize.tag(8, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.javaMultipleFiles]) {
        __result += WireSize.tag(10, WireType.VARINT) + WireSize.bool(this.javaMultipleFiles)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.javaGenerateEqualsAndHash]) {
        __result += WireSize.tag(20, WireType.VARINT) + WireSize.bool(this.javaGenerateEqualsAndHash)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.javaStringCheckUtf8]) {
        __result += WireSize.tag(27, WireType.VARINT) + WireSize.bool(this.javaStringCheckUtf8)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.optimizeFor]) {
        __result += WireSize.tag(9, WireType.VARINT) + WireSize.enum(this.optimizeFor.number)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.goPackage]) {
        __result += WireSize.string(this.goPackage).let { WireSize.tag(11, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.ccGenericServices]) {
        __result += WireSize.tag(16, WireType.VARINT) + WireSize.bool(this.ccGenericServices)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.javaGenericServices]) {
        __result += WireSize.tag(17, WireType.VARINT) + WireSize.bool(this.javaGenericServices)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.pyGenericServices]) {
        __result += WireSize.tag(18, WireType.VARINT) + WireSize.bool(this.pyGenericServices)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.deprecated]) {
        __result += WireSize.tag(23, WireType.VARINT) + WireSize.bool(this.deprecated)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.ccEnableArenas]) {
        __result += WireSize.tag(31, WireType.VARINT) + WireSize.bool(this.ccEnableArenas)
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.objcClassPrefix]) {
        __result += WireSize.string(this.objcClassPrefix).let { WireSize.tag(36, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.csharpNamespace]) {
        __result += WireSize.string(this.csharpNamespace).let { WireSize.tag(37, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.swiftPrefix]) {
        __result += WireSize.string(this.swiftPrefix).let { WireSize.tag(39, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.phpClassPrefix]) {
        __result += WireSize.string(this.phpClassPrefix).let { WireSize.tag(40, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.phpNamespace]) {
        __result += WireSize.string(this.phpNamespace).let { WireSize.tag(41, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.phpMetadataNamespace]) {
        __result += WireSize.string(this.phpMetadataNamespace).let { WireSize.tag(44, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.rubyPackage]) {
        __result += WireSize.string(this.rubyPackage).let { WireSize.tag(45, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FileOptionsInternal.PresenceIndices.features]) {
        __result += this.features.asInternal()._size.let { WireSize.tag(50, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        __result += this.uninterpretedOption.sumOf { element -> element.asInternal()._size.let { WireSize.tag(999, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun FileOptions.asInternal(): FileOptionsInternal {
    return this as? FileOptionsInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun MessageOptionsInternal.checkRequiredFields() {
    this.uninterpretedOption.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun MessageOptionsInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[MessageOptionsInternal.PresenceIndices.messageSetWireFormat]) {
        encoder.writeBool(fieldNr = 1, value = this.messageSetWireFormat)
    }

    if (presenceMask[MessageOptionsInternal.PresenceIndices.noStandardDescriptorAccessor]) {
        encoder.writeBool(fieldNr = 2, value = this.noStandardDescriptorAccessor)
    }

    if (presenceMask[MessageOptionsInternal.PresenceIndices.deprecated]) {
        encoder.writeBool(fieldNr = 3, value = this.deprecated)
    }

    if (presenceMask[MessageOptionsInternal.PresenceIndices.mapEntry]) {
        encoder.writeBool(fieldNr = 7, value = this.mapEntry)
    }

    if (presenceMask[MessageOptionsInternal.PresenceIndices.deprecatedLegacyJsonFieldConflicts]) {
        encoder.writeBool(fieldNr = 11, value = this.deprecatedLegacyJsonFieldConflicts)
    }

    if (presenceMask[MessageOptionsInternal.PresenceIndices.features]) {
        encoder.writeMessage(fieldNr = 12, value = this.features.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        this.uninterpretedOption.forEach {
            encoder.writeMessage(fieldNr = 999, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun MessageOptionsInternal.Companion.decodeWith(
    msg: MessageOptionsInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(MessageOptions::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.VARINT -> {
                msg.messageSetWireFormat = decoder.readBool()
            }
            2 if tag.wireType == WireType.VARINT -> {
                msg.noStandardDescriptorAccessor = decoder.readBool()
            }
            3 if tag.wireType == WireType.VARINT -> {
                msg.deprecated = decoder.readBool()
            }
            7 if tag.wireType == WireType.VARINT -> {
                msg.mapEntry = decoder.readBool()
            }
            11 if tag.wireType == WireType.VARINT -> {
                msg.deprecatedLegacyJsonFieldConflicts = decoder.readBool()
            }
            12 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__featuresDelegate.getOrCreate(msg) { FeatureSetInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> FeatureSetInternal.decodeWith(msg, decoder, config) }
            }
            999 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__uninterpretedOptionDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = UninterpretedOptionInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> UninterpretedOptionInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun MessageOptionsInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[MessageOptionsInternal.PresenceIndices.messageSetWireFormat]) {
        __result += WireSize.tag(1, WireType.VARINT) + WireSize.bool(this.messageSetWireFormat)
    }

    if (presenceMask[MessageOptionsInternal.PresenceIndices.noStandardDescriptorAccessor]) {
        __result += WireSize.tag(2, WireType.VARINT) + WireSize.bool(this.noStandardDescriptorAccessor)
    }

    if (presenceMask[MessageOptionsInternal.PresenceIndices.deprecated]) {
        __result += WireSize.tag(3, WireType.VARINT) + WireSize.bool(this.deprecated)
    }

    if (presenceMask[MessageOptionsInternal.PresenceIndices.mapEntry]) {
        __result += WireSize.tag(7, WireType.VARINT) + WireSize.bool(this.mapEntry)
    }

    if (presenceMask[MessageOptionsInternal.PresenceIndices.deprecatedLegacyJsonFieldConflicts]) {
        __result += WireSize.tag(11, WireType.VARINT) + WireSize.bool(this.deprecatedLegacyJsonFieldConflicts)
    }

    if (presenceMask[MessageOptionsInternal.PresenceIndices.features]) {
        __result += this.features.asInternal()._size.let { WireSize.tag(12, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        __result += this.uninterpretedOption.sumOf { element -> element.asInternal()._size.let { WireSize.tag(999, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun MessageOptions.asInternal(): MessageOptionsInternal {
    return this as? MessageOptionsInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun FieldOptionsInternal.checkRequiredFields() {
    this.uninterpretedOption.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun FieldOptionsInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[FieldOptionsInternal.PresenceIndices.ctype]) {
        encoder.writeEnum(fieldNr = 1, value = this.ctype.number)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.packed]) {
        encoder.writeBool(fieldNr = 2, value = this.packed)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.jstype]) {
        encoder.writeEnum(fieldNr = 6, value = this.jstype.number)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.lazy]) {
        encoder.writeBool(fieldNr = 5, value = this.lazy)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.unverifiedLazy]) {
        encoder.writeBool(fieldNr = 15, value = this.unverifiedLazy)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.deprecated]) {
        encoder.writeBool(fieldNr = 3, value = this.deprecated)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.weak]) {
        encoder.writeBool(fieldNr = 10, value = this.weak)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.debugRedact]) {
        encoder.writeBool(fieldNr = 16, value = this.debugRedact)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.retention]) {
        encoder.writeEnum(fieldNr = 17, value = this.retention.number)
    }

    if (this.targets.isNotEmpty()) {
        this.targets.forEach {
            encoder.writeEnum(19, it.number)
        }
    }

    if (this.editionDefaults.isNotEmpty()) {
        this.editionDefaults.forEach {
            encoder.writeMessage(fieldNr = 20, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.features]) {
        encoder.writeMessage(fieldNr = 21, value = this.features.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.featureSupport]) {
        encoder.writeMessage(fieldNr = 22, value = this.featureSupport.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        this.uninterpretedOption.forEach {
            encoder.writeMessage(fieldNr = 999, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun FieldOptionsInternal.Companion.decodeWith(
    msg: FieldOptionsInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(FieldOptions::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.VARINT -> {
                msg.ctype = FieldOptions.CType.fromNumber(decoder.readEnum())
            }
            2 if tag.wireType == WireType.VARINT -> {
                msg.packed = decoder.readBool()
            }
            6 if tag.wireType == WireType.VARINT -> {
                msg.jstype = FieldOptions.JSType.fromNumber(decoder.readEnum())
            }
            5 if tag.wireType == WireType.VARINT -> {
                msg.lazy = decoder.readBool()
            }
            15 if tag.wireType == WireType.VARINT -> {
                msg.unverifiedLazy = decoder.readBool()
            }
            3 if tag.wireType == WireType.VARINT -> {
                msg.deprecated = decoder.readBool()
            }
            10 if tag.wireType == WireType.VARINT -> {
                msg.weak = decoder.readBool()
            }
            16 if tag.wireType == WireType.VARINT -> {
                msg.debugRedact = decoder.readBool()
            }
            17 if tag.wireType == WireType.VARINT -> {
                msg.retention = FieldOptions.OptionRetention.fromNumber(decoder.readEnum())
            }
            19 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__targetsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedEnum().map { FieldOptions.OptionTargetType.fromNumber(it) })
            }
            19 if tag.wireType == WireType.VARINT -> {
                val target = msg.__targetsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = FieldOptions.OptionTargetType.fromNumber(decoder.readEnum())
                target.add(elem)
            }
            20 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__editionDefaultsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = FieldOptionsInternal.EditionDefaultInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> FieldOptionsInternal.EditionDefaultInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            21 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__featuresDelegate.getOrCreate(msg) { FeatureSetInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> FeatureSetInternal.decodeWith(msg, decoder, config) }
            }
            22 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__featureSupportDelegate.getOrCreate(msg) { FieldOptionsInternal.FeatureSupportInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> FieldOptionsInternal.FeatureSupportInternal.decodeWith(msg, decoder, config) }
            }
            999 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__uninterpretedOptionDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = UninterpretedOptionInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> UninterpretedOptionInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun FieldOptionsInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[FieldOptionsInternal.PresenceIndices.ctype]) {
        __result += WireSize.tag(1, WireType.VARINT) + WireSize.enum(this.ctype.number)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.packed]) {
        __result += WireSize.tag(2, WireType.VARINT) + WireSize.bool(this.packed)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.jstype]) {
        __result += WireSize.tag(6, WireType.VARINT) + WireSize.enum(this.jstype.number)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.lazy]) {
        __result += WireSize.tag(5, WireType.VARINT) + WireSize.bool(this.lazy)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.unverifiedLazy]) {
        __result += WireSize.tag(15, WireType.VARINT) + WireSize.bool(this.unverifiedLazy)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.deprecated]) {
        __result += WireSize.tag(3, WireType.VARINT) + WireSize.bool(this.deprecated)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.weak]) {
        __result += WireSize.tag(10, WireType.VARINT) + WireSize.bool(this.weak)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.debugRedact]) {
        __result += WireSize.tag(16, WireType.VARINT) + WireSize.bool(this.debugRedact)
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.retention]) {
        __result += WireSize.tag(17, WireType.VARINT) + WireSize.enum(this.retention.number)
    }

    if (this.targets.isNotEmpty()) {
        __result += this.targets.sumOf { element -> WireSize.tag(19, WireType.VARINT) + WireSize.enum(element.number) }
    }

    if (this.editionDefaults.isNotEmpty()) {
        __result += this.editionDefaults.sumOf { element -> element.asInternal()._size.let { WireSize.tag(20, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.features]) {
        __result += this.features.asInternal()._size.let { WireSize.tag(21, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FieldOptionsInternal.PresenceIndices.featureSupport]) {
        __result += this.featureSupport.asInternal()._size.let { WireSize.tag(22, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        __result += this.uninterpretedOption.sumOf { element -> element.asInternal()._size.let { WireSize.tag(999, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun FieldOptions.asInternal(): FieldOptionsInternal {
    return this as? FieldOptionsInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun OneofOptionsInternal.checkRequiredFields() {
    this.uninterpretedOption.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun OneofOptionsInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[OneofOptionsInternal.PresenceIndices.features]) {
        encoder.writeMessage(fieldNr = 1, value = this.features.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        this.uninterpretedOption.forEach {
            encoder.writeMessage(fieldNr = 999, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun OneofOptionsInternal.Companion.decodeWith(
    msg: OneofOptionsInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(OneofOptions::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__featuresDelegate.getOrCreate(msg) { FeatureSetInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> FeatureSetInternal.decodeWith(msg, decoder, config) }
            }
            999 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__uninterpretedOptionDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = UninterpretedOptionInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> UninterpretedOptionInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun OneofOptionsInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[OneofOptionsInternal.PresenceIndices.features]) {
        __result += this.features.asInternal()._size.let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        __result += this.uninterpretedOption.sumOf { element -> element.asInternal()._size.let { WireSize.tag(999, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun OneofOptions.asInternal(): OneofOptionsInternal {
    return this as? OneofOptionsInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun EnumOptionsInternal.checkRequiredFields() {
    this.uninterpretedOption.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun EnumOptionsInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[EnumOptionsInternal.PresenceIndices.allowAlias]) {
        encoder.writeBool(fieldNr = 2, value = this.allowAlias)
    }

    if (presenceMask[EnumOptionsInternal.PresenceIndices.deprecated]) {
        encoder.writeBool(fieldNr = 3, value = this.deprecated)
    }

    if (presenceMask[EnumOptionsInternal.PresenceIndices.deprecatedLegacyJsonFieldConflicts]) {
        encoder.writeBool(fieldNr = 6, value = this.deprecatedLegacyJsonFieldConflicts)
    }

    if (presenceMask[EnumOptionsInternal.PresenceIndices.features]) {
        encoder.writeMessage(fieldNr = 7, value = this.features.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        this.uninterpretedOption.forEach {
            encoder.writeMessage(fieldNr = 999, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun EnumOptionsInternal.Companion.decodeWith(
    msg: EnumOptionsInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(EnumOptions::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            2 if tag.wireType == WireType.VARINT -> {
                msg.allowAlias = decoder.readBool()
            }
            3 if tag.wireType == WireType.VARINT -> {
                msg.deprecated = decoder.readBool()
            }
            6 if tag.wireType == WireType.VARINT -> {
                msg.deprecatedLegacyJsonFieldConflicts = decoder.readBool()
            }
            7 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__featuresDelegate.getOrCreate(msg) { FeatureSetInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> FeatureSetInternal.decodeWith(msg, decoder, config) }
            }
            999 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__uninterpretedOptionDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = UninterpretedOptionInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> UninterpretedOptionInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun EnumOptionsInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[EnumOptionsInternal.PresenceIndices.allowAlias]) {
        __result += WireSize.tag(2, WireType.VARINT) + WireSize.bool(this.allowAlias)
    }

    if (presenceMask[EnumOptionsInternal.PresenceIndices.deprecated]) {
        __result += WireSize.tag(3, WireType.VARINT) + WireSize.bool(this.deprecated)
    }

    if (presenceMask[EnumOptionsInternal.PresenceIndices.deprecatedLegacyJsonFieldConflicts]) {
        __result += WireSize.tag(6, WireType.VARINT) + WireSize.bool(this.deprecatedLegacyJsonFieldConflicts)
    }

    if (presenceMask[EnumOptionsInternal.PresenceIndices.features]) {
        __result += this.features.asInternal()._size.let { WireSize.tag(7, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        __result += this.uninterpretedOption.sumOf { element -> element.asInternal()._size.let { WireSize.tag(999, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun EnumOptions.asInternal(): EnumOptionsInternal {
    return this as? EnumOptionsInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun EnumValueOptionsInternal.checkRequiredFields() {
    this.uninterpretedOption.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun EnumValueOptionsInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[EnumValueOptionsInternal.PresenceIndices.deprecated]) {
        encoder.writeBool(fieldNr = 1, value = this.deprecated)
    }

    if (presenceMask[EnumValueOptionsInternal.PresenceIndices.features]) {
        encoder.writeMessage(fieldNr = 2, value = this.features.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (presenceMask[EnumValueOptionsInternal.PresenceIndices.debugRedact]) {
        encoder.writeBool(fieldNr = 3, value = this.debugRedact)
    }

    if (presenceMask[EnumValueOptionsInternal.PresenceIndices.featureSupport]) {
        encoder.writeMessage(fieldNr = 4, value = this.featureSupport.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        this.uninterpretedOption.forEach {
            encoder.writeMessage(fieldNr = 999, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun EnumValueOptionsInternal.Companion.decodeWith(
    msg: EnumValueOptionsInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(EnumValueOptions::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.VARINT -> {
                msg.deprecated = decoder.readBool()
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__featuresDelegate.getOrCreate(msg) { FeatureSetInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> FeatureSetInternal.decodeWith(msg, decoder, config) }
            }
            3 if tag.wireType == WireType.VARINT -> {
                msg.debugRedact = decoder.readBool()
            }
            4 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__featureSupportDelegate.getOrCreate(msg) { FieldOptionsInternal.FeatureSupportInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> FieldOptionsInternal.FeatureSupportInternal.decodeWith(msg, decoder, config) }
            }
            999 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__uninterpretedOptionDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = UninterpretedOptionInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> UninterpretedOptionInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun EnumValueOptionsInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[EnumValueOptionsInternal.PresenceIndices.deprecated]) {
        __result += WireSize.tag(1, WireType.VARINT) + WireSize.bool(this.deprecated)
    }

    if (presenceMask[EnumValueOptionsInternal.PresenceIndices.features]) {
        __result += this.features.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[EnumValueOptionsInternal.PresenceIndices.debugRedact]) {
        __result += WireSize.tag(3, WireType.VARINT) + WireSize.bool(this.debugRedact)
    }

    if (presenceMask[EnumValueOptionsInternal.PresenceIndices.featureSupport]) {
        __result += this.featureSupport.asInternal()._size.let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        __result += this.uninterpretedOption.sumOf { element -> element.asInternal()._size.let { WireSize.tag(999, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun EnumValueOptions.asInternal(): EnumValueOptionsInternal {
    return this as? EnumValueOptionsInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun ServiceOptionsInternal.checkRequiredFields() {
    this.uninterpretedOption.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun ServiceOptionsInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[ServiceOptionsInternal.PresenceIndices.features]) {
        encoder.writeMessage(fieldNr = 34, value = this.features.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (presenceMask[ServiceOptionsInternal.PresenceIndices.deprecated]) {
        encoder.writeBool(fieldNr = 33, value = this.deprecated)
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        this.uninterpretedOption.forEach {
            encoder.writeMessage(fieldNr = 999, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun ServiceOptionsInternal.Companion.decodeWith(
    msg: ServiceOptionsInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(ServiceOptions::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            34 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__featuresDelegate.getOrCreate(msg) { FeatureSetInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> FeatureSetInternal.decodeWith(msg, decoder, config) }
            }
            33 if tag.wireType == WireType.VARINT -> {
                msg.deprecated = decoder.readBool()
            }
            999 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__uninterpretedOptionDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = UninterpretedOptionInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> UninterpretedOptionInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun ServiceOptionsInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[ServiceOptionsInternal.PresenceIndices.features]) {
        __result += this.features.asInternal()._size.let { WireSize.tag(34, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ServiceOptionsInternal.PresenceIndices.deprecated]) {
        __result += WireSize.tag(33, WireType.VARINT) + WireSize.bool(this.deprecated)
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        __result += this.uninterpretedOption.sumOf { element -> element.asInternal()._size.let { WireSize.tag(999, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun ServiceOptions.asInternal(): ServiceOptionsInternal {
    return this as? ServiceOptionsInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun MethodOptionsInternal.checkRequiredFields() {
    this.uninterpretedOption.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun MethodOptionsInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[MethodOptionsInternal.PresenceIndices.deprecated]) {
        encoder.writeBool(fieldNr = 33, value = this.deprecated)
    }

    if (presenceMask[MethodOptionsInternal.PresenceIndices.idempotencyLevel]) {
        encoder.writeEnum(fieldNr = 34, value = this.idempotencyLevel.number)
    }

    if (presenceMask[MethodOptionsInternal.PresenceIndices.features]) {
        encoder.writeMessage(fieldNr = 35, value = this.features.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        this.uninterpretedOption.forEach {
            encoder.writeMessage(fieldNr = 999, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun MethodOptionsInternal.Companion.decodeWith(
    msg: MethodOptionsInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(MethodOptions::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            33 if tag.wireType == WireType.VARINT -> {
                msg.deprecated = decoder.readBool()
            }
            34 if tag.wireType == WireType.VARINT -> {
                msg.idempotencyLevel = MethodOptions.IdempotencyLevel.fromNumber(decoder.readEnum())
            }
            35 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__featuresDelegate.getOrCreate(msg) { FeatureSetInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> FeatureSetInternal.decodeWith(msg, decoder, config) }
            }
            999 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__uninterpretedOptionDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = UninterpretedOptionInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> UninterpretedOptionInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun MethodOptionsInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[MethodOptionsInternal.PresenceIndices.deprecated]) {
        __result += WireSize.tag(33, WireType.VARINT) + WireSize.bool(this.deprecated)
    }

    if (presenceMask[MethodOptionsInternal.PresenceIndices.idempotencyLevel]) {
        __result += WireSize.tag(34, WireType.VARINT) + WireSize.enum(this.idempotencyLevel.number)
    }

    if (presenceMask[MethodOptionsInternal.PresenceIndices.features]) {
        __result += this.features.asInternal()._size.let { WireSize.tag(35, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.uninterpretedOption.isNotEmpty()) {
        __result += this.uninterpretedOption.sumOf { element -> element.asInternal()._size.let { WireSize.tag(999, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun MethodOptions.asInternal(): MethodOptionsInternal {
    return this as? MethodOptionsInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun UninterpretedOptionInternal.checkRequiredFields() {
    this.name.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun UninterpretedOptionInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.name.isNotEmpty()) {
        this.name.forEach {
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (presenceMask[UninterpretedOptionInternal.PresenceIndices.identifierValue]) {
        encoder.writeString(fieldNr = 3, value = this.identifierValue)
    }

    if (presenceMask[UninterpretedOptionInternal.PresenceIndices.positiveIntValue]) {
        encoder.writeUInt64(fieldNr = 4, value = this.positiveIntValue)
    }

    if (presenceMask[UninterpretedOptionInternal.PresenceIndices.negativeIntValue]) {
        encoder.writeInt64(fieldNr = 5, value = this.negativeIntValue)
    }

    if (presenceMask[UninterpretedOptionInternal.PresenceIndices.doubleValue]) {
        encoder.writeDouble(fieldNr = 6, value = this.doubleValue)
    }

    if (presenceMask[UninterpretedOptionInternal.PresenceIndices.stringValue]) {
        encoder.writeBytes(fieldNr = 7, value = this.stringValue)
    }

    if (presenceMask[UninterpretedOptionInternal.PresenceIndices.aggregateValue]) {
        encoder.writeString(fieldNr = 8, value = this.aggregateValue)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun UninterpretedOptionInternal.Companion.decodeWith(
    msg: UninterpretedOptionInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__nameDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = UninterpretedOptionInternal.NamePartInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> UninterpretedOptionInternal.NamePartInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            3 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.identifierValue = decoder.readString()
            }
            4 if tag.wireType == WireType.VARINT -> {
                msg.positiveIntValue = decoder.readUInt64()
            }
            5 if tag.wireType == WireType.VARINT -> {
                msg.negativeIntValue = decoder.readInt64()
            }
            6 if tag.wireType == WireType.FIXED64 -> {
                msg.doubleValue = decoder.readDouble()
            }
            7 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.stringValue = decoder.readBytes()
            }
            8 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.aggregateValue = decoder.readString()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun UninterpretedOptionInternal.computeSize(): Int {
    var __result = 0
    if (this.name.isNotEmpty()) {
        __result += this.name.sumOf { element -> element.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (presenceMask[UninterpretedOptionInternal.PresenceIndices.identifierValue]) {
        __result += WireSize.string(this.identifierValue).let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[UninterpretedOptionInternal.PresenceIndices.positiveIntValue]) {
        __result += WireSize.tag(4, WireType.VARINT) + WireSize.uInt64(this.positiveIntValue)
    }

    if (presenceMask[UninterpretedOptionInternal.PresenceIndices.negativeIntValue]) {
        __result += WireSize.tag(5, WireType.VARINT) + WireSize.int64(this.negativeIntValue)
    }

    if (presenceMask[UninterpretedOptionInternal.PresenceIndices.doubleValue]) {
        __result += WireSize.tag(6, WireType.FIXED64) + WireSize.double(this.doubleValue)
    }

    if (presenceMask[UninterpretedOptionInternal.PresenceIndices.stringValue]) {
        __result += WireSize.bytes(this.stringValue).let { WireSize.tag(7, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[UninterpretedOptionInternal.PresenceIndices.aggregateValue]) {
        __result += WireSize.string(this.aggregateValue).let { WireSize.tag(8, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun UninterpretedOption.asInternal(): UninterpretedOptionInternal {
    return this as? UninterpretedOptionInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun FeatureSetInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[FeatureSetInternal.PresenceIndices.fieldPresence]) {
        encoder.writeEnum(fieldNr = 1, value = this.fieldPresence.number)
    }

    if (presenceMask[FeatureSetInternal.PresenceIndices.enumType]) {
        encoder.writeEnum(fieldNr = 2, value = this.enumType.number)
    }

    if (presenceMask[FeatureSetInternal.PresenceIndices.repeatedFieldEncoding]) {
        encoder.writeEnum(fieldNr = 3, value = this.repeatedFieldEncoding.number)
    }

    if (presenceMask[FeatureSetInternal.PresenceIndices.utf8Validation]) {
        encoder.writeEnum(fieldNr = 4, value = this.utf8Validation.number)
    }

    if (presenceMask[FeatureSetInternal.PresenceIndices.messageEncoding]) {
        encoder.writeEnum(fieldNr = 5, value = this.messageEncoding.number)
    }

    if (presenceMask[FeatureSetInternal.PresenceIndices.jsonFormat]) {
        encoder.writeEnum(fieldNr = 6, value = this.jsonFormat.number)
    }

    if (presenceMask[FeatureSetInternal.PresenceIndices.enforceNamingStyle]) {
        encoder.writeEnum(fieldNr = 7, value = this.enforceNamingStyle.number)
    }

    if (presenceMask[FeatureSetInternal.PresenceIndices.defaultSymbolVisibility]) {
        encoder.writeEnum(fieldNr = 8, value = this.defaultSymbolVisibility.number)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun FeatureSetInternal.Companion.decodeWith(
    msg: FeatureSetInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(FeatureSet::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.VARINT -> {
                msg.fieldPresence = FeatureSet.FieldPresence.fromNumber(decoder.readEnum())
            }
            2 if tag.wireType == WireType.VARINT -> {
                msg.enumType = FeatureSet.EnumType.fromNumber(decoder.readEnum())
            }
            3 if tag.wireType == WireType.VARINT -> {
                msg.repeatedFieldEncoding = FeatureSet.RepeatedFieldEncoding.fromNumber(decoder.readEnum())
            }
            4 if tag.wireType == WireType.VARINT -> {
                msg.utf8Validation = FeatureSet.Utf8Validation.fromNumber(decoder.readEnum())
            }
            5 if tag.wireType == WireType.VARINT -> {
                msg.messageEncoding = FeatureSet.MessageEncoding.fromNumber(decoder.readEnum())
            }
            6 if tag.wireType == WireType.VARINT -> {
                msg.jsonFormat = FeatureSet.JsonFormat.fromNumber(decoder.readEnum())
            }
            7 if tag.wireType == WireType.VARINT -> {
                msg.enforceNamingStyle = FeatureSet.EnforceNamingStyle.fromNumber(decoder.readEnum())
            }
            8 if tag.wireType == WireType.VARINT -> {
                msg.defaultSymbolVisibility = FeatureSet.VisibilityFeature.DefaultSymbolVisibility.fromNumber(decoder.readEnum())
            }
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun FeatureSetInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[FeatureSetInternal.PresenceIndices.fieldPresence]) {
        __result += WireSize.tag(1, WireType.VARINT) + WireSize.enum(this.fieldPresence.number)
    }

    if (presenceMask[FeatureSetInternal.PresenceIndices.enumType]) {
        __result += WireSize.tag(2, WireType.VARINT) + WireSize.enum(this.enumType.number)
    }

    if (presenceMask[FeatureSetInternal.PresenceIndices.repeatedFieldEncoding]) {
        __result += WireSize.tag(3, WireType.VARINT) + WireSize.enum(this.repeatedFieldEncoding.number)
    }

    if (presenceMask[FeatureSetInternal.PresenceIndices.utf8Validation]) {
        __result += WireSize.tag(4, WireType.VARINT) + WireSize.enum(this.utf8Validation.number)
    }

    if (presenceMask[FeatureSetInternal.PresenceIndices.messageEncoding]) {
        __result += WireSize.tag(5, WireType.VARINT) + WireSize.enum(this.messageEncoding.number)
    }

    if (presenceMask[FeatureSetInternal.PresenceIndices.jsonFormat]) {
        __result += WireSize.tag(6, WireType.VARINT) + WireSize.enum(this.jsonFormat.number)
    }

    if (presenceMask[FeatureSetInternal.PresenceIndices.enforceNamingStyle]) {
        __result += WireSize.tag(7, WireType.VARINT) + WireSize.enum(this.enforceNamingStyle.number)
    }

    if (presenceMask[FeatureSetInternal.PresenceIndices.defaultSymbolVisibility]) {
        __result += WireSize.tag(8, WireType.VARINT) + WireSize.enum(this.defaultSymbolVisibility.number)
    }

    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun FeatureSet.asInternal(): FeatureSetInternal {
    return this as? FeatureSetInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun FeatureSetDefaultsInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.defaults.isNotEmpty()) {
        this.defaults.forEach {
            encoder.writeMessage(fieldNr = 1, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    if (presenceMask[FeatureSetDefaultsInternal.PresenceIndices.minimumEdition]) {
        encoder.writeEnum(fieldNr = 4, value = this.minimumEdition.number)
    }

    if (presenceMask[FeatureSetDefaultsInternal.PresenceIndices.maximumEdition]) {
        encoder.writeEnum(fieldNr = 5, value = this.maximumEdition.number)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun FeatureSetDefaultsInternal.Companion.decodeWith(
    msg: FeatureSetDefaultsInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__defaultsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = FeatureSetDefaultsInternal.FeatureSetEditionDefaultInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> FeatureSetDefaultsInternal.FeatureSetEditionDefaultInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            4 if tag.wireType == WireType.VARINT -> {
                msg.minimumEdition = Edition.fromNumber(decoder.readEnum())
            }
            5 if tag.wireType == WireType.VARINT -> {
                msg.maximumEdition = Edition.fromNumber(decoder.readEnum())
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun FeatureSetDefaultsInternal.computeSize(): Int {
    var __result = 0
    if (this.defaults.isNotEmpty()) {
        __result += this.defaults.sumOf { element -> element.asInternal()._size.let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (presenceMask[FeatureSetDefaultsInternal.PresenceIndices.minimumEdition]) {
        __result += WireSize.tag(4, WireType.VARINT) + WireSize.enum(this.minimumEdition.number)
    }

    if (presenceMask[FeatureSetDefaultsInternal.PresenceIndices.maximumEdition]) {
        __result += WireSize.tag(5, WireType.VARINT) + WireSize.enum(this.maximumEdition.number)
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun FeatureSetDefaults.asInternal(): FeatureSetDefaultsInternal {
    return this as? FeatureSetDefaultsInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun SourceCodeInfoInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.location.isNotEmpty()) {
        this.location.forEach {
            encoder.writeMessage(fieldNr = 1, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun SourceCodeInfoInternal.Companion.decodeWith(
    msg: SourceCodeInfoInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    val knownExtensions = config?.extensionRegistry?.getAllExtensionsForMessage(SourceCodeInfo::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__locationDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = SourceCodeInfoInternal.LocationInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> SourceCodeInfoInternal.LocationInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            else -> {
                val extension = knownExtensions[tag.fieldNr] as? InternalExtensionDescriptor
                if (extension != null && tag.wireType in extension.acceptedWireTypes) {
                    val currentExtension = msg._extensions[tag.fieldNr]?.takeIf { it.descriptor == extension }?.value
                    val decodedExtension = if (extension.isPacked && tag.wireType == WireType.LENGTH_DELIMITED) extension.decodePacked!!(currentExtension, decoder, config) else extension.decode(currentExtension, decoder, config)
                    msg._extensions[tag.fieldNr] = ExtensionValue(decodedExtension, extension)
                    continue // with next tag
                }

                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun SourceCodeInfoInternal.computeSize(): Int {
    var __result = 0
    if (this.location.isNotEmpty()) {
        __result += this.location.sumOf { element -> element.asInternal()._size.let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += extensionsSize()
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun SourceCodeInfo.asInternal(): SourceCodeInfoInternal {
    return this as? SourceCodeInfoInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun GeneratedCodeInfoInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.annotation.isNotEmpty()) {
        this.annotation.forEach {
            encoder.writeMessage(fieldNr = 1, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun GeneratedCodeInfoInternal.Companion.decodeWith(
    msg: GeneratedCodeInfoInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__annotationDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = GeneratedCodeInfoInternal.AnnotationInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> GeneratedCodeInfoInternal.AnnotationInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun GeneratedCodeInfoInternal.computeSize(): Int {
    var __result = 0
    if (this.annotation.isNotEmpty()) {
        __result += this.annotation.sumOf { element -> element.asInternal()._size.let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun GeneratedCodeInfo.asInternal(): GeneratedCodeInfoInternal {
    return this as? GeneratedCodeInfoInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun DescriptorProtoInternal.ExtensionRangeInternal.checkRequiredFields() {
    if (presenceMask[2]) {
        this.options.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun DescriptorProtoInternal.ExtensionRangeInternal.encodeWith(
    encoder: WireEncoder,
    config: ProtoConfig?,
) {
    if (presenceMask[DescriptorProtoInternal.ExtensionRangeInternal.PresenceIndices.start]) {
        encoder.writeInt32(fieldNr = 1, value = this.start)
    }

    if (presenceMask[DescriptorProtoInternal.ExtensionRangeInternal.PresenceIndices.end]) {
        encoder.writeInt32(fieldNr = 2, value = this.end)
    }

    if (presenceMask[DescriptorProtoInternal.ExtensionRangeInternal.PresenceIndices.options]) {
        encoder.writeMessage(fieldNr = 3, value = this.options.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun DescriptorProtoInternal.ExtensionRangeInternal.Companion.decodeWith(
    msg: DescriptorProtoInternal.ExtensionRangeInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.VARINT -> {
                msg.start = decoder.readInt32()
            }
            2 if tag.wireType == WireType.VARINT -> {
                msg.end = decoder.readInt32()
            }
            3 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionsDelegate.getOrCreate(msg) { ExtensionRangeOptionsInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> ExtensionRangeOptionsInternal.decodeWith(msg, decoder, config) }
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun DescriptorProtoInternal.ExtensionRangeInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[DescriptorProtoInternal.ExtensionRangeInternal.PresenceIndices.start]) {
        __result += WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.start)
    }

    if (presenceMask[DescriptorProtoInternal.ExtensionRangeInternal.PresenceIndices.end]) {
        __result += WireSize.tag(2, WireType.VARINT) + WireSize.int32(this.end)
    }

    if (presenceMask[DescriptorProtoInternal.ExtensionRangeInternal.PresenceIndices.options]) {
        __result += this.options.asInternal()._size.let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun DescriptorProto.ExtensionRange.asInternal(): DescriptorProtoInternal.ExtensionRangeInternal {
    return this as? DescriptorProtoInternal.ExtensionRangeInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun DescriptorProtoInternal.ReservedRangeInternal.encodeWith(
    encoder: WireEncoder,
    config: ProtoConfig?,
) {
    if (presenceMask[DescriptorProtoInternal.ReservedRangeInternal.PresenceIndices.start]) {
        encoder.writeInt32(fieldNr = 1, value = this.start)
    }

    if (presenceMask[DescriptorProtoInternal.ReservedRangeInternal.PresenceIndices.end]) {
        encoder.writeInt32(fieldNr = 2, value = this.end)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun DescriptorProtoInternal.ReservedRangeInternal.Companion.decodeWith(
    msg: DescriptorProtoInternal.ReservedRangeInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.VARINT -> {
                msg.start = decoder.readInt32()
            }
            2 if tag.wireType == WireType.VARINT -> {
                msg.end = decoder.readInt32()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun DescriptorProtoInternal.ReservedRangeInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[DescriptorProtoInternal.ReservedRangeInternal.PresenceIndices.start]) {
        __result += WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.start)
    }

    if (presenceMask[DescriptorProtoInternal.ReservedRangeInternal.PresenceIndices.end]) {
        __result += WireSize.tag(2, WireType.VARINT) + WireSize.int32(this.end)
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun DescriptorProto.ReservedRange.asInternal(): DescriptorProtoInternal.ReservedRangeInternal {
    return this as? DescriptorProtoInternal.ReservedRangeInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun ExtensionRangeOptionsInternal.DeclarationInternal.encodeWith(
    encoder: WireEncoder,
    config: ProtoConfig?,
) {
    if (presenceMask[ExtensionRangeOptionsInternal.DeclarationInternal.PresenceIndices.number]) {
        encoder.writeInt32(fieldNr = 1, value = this.number)
    }

    if (presenceMask[ExtensionRangeOptionsInternal.DeclarationInternal.PresenceIndices.fullName]) {
        encoder.writeString(fieldNr = 2, value = this.fullName)
    }

    if (presenceMask[ExtensionRangeOptionsInternal.DeclarationInternal.PresenceIndices.type]) {
        encoder.writeString(fieldNr = 3, value = this.type)
    }

    if (presenceMask[ExtensionRangeOptionsInternal.DeclarationInternal.PresenceIndices.reserved]) {
        encoder.writeBool(fieldNr = 5, value = this.reserved)
    }

    if (presenceMask[ExtensionRangeOptionsInternal.DeclarationInternal.PresenceIndices.repeated]) {
        encoder.writeBool(fieldNr = 6, value = this.repeated)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun ExtensionRangeOptionsInternal.DeclarationInternal.Companion.decodeWith(
    msg: ExtensionRangeOptionsInternal.DeclarationInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.VARINT -> {
                msg.number = decoder.readInt32()
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.fullName = decoder.readString()
            }
            3 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.type = decoder.readString()
            }
            5 if tag.wireType == WireType.VARINT -> {
                msg.reserved = decoder.readBool()
            }
            6 if tag.wireType == WireType.VARINT -> {
                msg.repeated = decoder.readBool()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun ExtensionRangeOptionsInternal.DeclarationInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[ExtensionRangeOptionsInternal.DeclarationInternal.PresenceIndices.number]) {
        __result += WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.number)
    }

    if (presenceMask[ExtensionRangeOptionsInternal.DeclarationInternal.PresenceIndices.fullName]) {
        __result += WireSize.string(this.fullName).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ExtensionRangeOptionsInternal.DeclarationInternal.PresenceIndices.type]) {
        __result += WireSize.string(this.type).let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ExtensionRangeOptionsInternal.DeclarationInternal.PresenceIndices.reserved]) {
        __result += WireSize.tag(5, WireType.VARINT) + WireSize.bool(this.reserved)
    }

    if (presenceMask[ExtensionRangeOptionsInternal.DeclarationInternal.PresenceIndices.repeated]) {
        __result += WireSize.tag(6, WireType.VARINT) + WireSize.bool(this.repeated)
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun ExtensionRangeOptions.Declaration.asInternal(): ExtensionRangeOptionsInternal.DeclarationInternal {
    return this as? ExtensionRangeOptionsInternal.DeclarationInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun EnumDescriptorProtoInternal.EnumReservedRangeInternal.encodeWith(
    encoder: WireEncoder,
    config: ProtoConfig?,
) {
    if (presenceMask[EnumDescriptorProtoInternal.EnumReservedRangeInternal.PresenceIndices.start]) {
        encoder.writeInt32(fieldNr = 1, value = this.start)
    }

    if (presenceMask[EnumDescriptorProtoInternal.EnumReservedRangeInternal.PresenceIndices.end]) {
        encoder.writeInt32(fieldNr = 2, value = this.end)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun EnumDescriptorProtoInternal.EnumReservedRangeInternal.Companion.decodeWith(
    msg: EnumDescriptorProtoInternal.EnumReservedRangeInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.VARINT -> {
                msg.start = decoder.readInt32()
            }
            2 if tag.wireType == WireType.VARINT -> {
                msg.end = decoder.readInt32()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun EnumDescriptorProtoInternal.EnumReservedRangeInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[EnumDescriptorProtoInternal.EnumReservedRangeInternal.PresenceIndices.start]) {
        __result += WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.start)
    }

    if (presenceMask[EnumDescriptorProtoInternal.EnumReservedRangeInternal.PresenceIndices.end]) {
        __result += WireSize.tag(2, WireType.VARINT) + WireSize.int32(this.end)
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun EnumDescriptorProto.EnumReservedRange.asInternal(): EnumDescriptorProtoInternal.EnumReservedRangeInternal {
    return this as? EnumDescriptorProtoInternal.EnumReservedRangeInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun FieldOptionsInternal.EditionDefaultInternal.encodeWith(
    encoder: WireEncoder,
    config: ProtoConfig?,
) {
    if (presenceMask[FieldOptionsInternal.EditionDefaultInternal.PresenceIndices.edition]) {
        encoder.writeEnum(fieldNr = 3, value = this.edition.number)
    }

    if (presenceMask[FieldOptionsInternal.EditionDefaultInternal.PresenceIndices.value]) {
        encoder.writeString(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun FieldOptionsInternal.EditionDefaultInternal.Companion.decodeWith(
    msg: FieldOptionsInternal.EditionDefaultInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            3 if tag.wireType == WireType.VARINT -> {
                msg.edition = Edition.fromNumber(decoder.readEnum())
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.value = decoder.readString()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun FieldOptionsInternal.EditionDefaultInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[FieldOptionsInternal.EditionDefaultInternal.PresenceIndices.edition]) {
        __result += WireSize.tag(3, WireType.VARINT) + WireSize.enum(this.edition.number)
    }

    if (presenceMask[FieldOptionsInternal.EditionDefaultInternal.PresenceIndices.value]) {
        __result += WireSize.string(this.value).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun FieldOptions.EditionDefault.asInternal(): FieldOptionsInternal.EditionDefaultInternal {
    return this as? FieldOptionsInternal.EditionDefaultInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun FieldOptionsInternal.FeatureSupportInternal.encodeWith(
    encoder: WireEncoder,
    config: ProtoConfig?,
) {
    if (presenceMask[FieldOptionsInternal.FeatureSupportInternal.PresenceIndices.editionIntroduced]) {
        encoder.writeEnum(fieldNr = 1, value = this.editionIntroduced.number)
    }

    if (presenceMask[FieldOptionsInternal.FeatureSupportInternal.PresenceIndices.editionDeprecated]) {
        encoder.writeEnum(fieldNr = 2, value = this.editionDeprecated.number)
    }

    if (presenceMask[FieldOptionsInternal.FeatureSupportInternal.PresenceIndices.deprecationWarning]) {
        encoder.writeString(fieldNr = 3, value = this.deprecationWarning)
    }

    if (presenceMask[FieldOptionsInternal.FeatureSupportInternal.PresenceIndices.editionRemoved]) {
        encoder.writeEnum(fieldNr = 4, value = this.editionRemoved.number)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun FieldOptionsInternal.FeatureSupportInternal.Companion.decodeWith(
    msg: FieldOptionsInternal.FeatureSupportInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.VARINT -> {
                msg.editionIntroduced = Edition.fromNumber(decoder.readEnum())
            }
            2 if tag.wireType == WireType.VARINT -> {
                msg.editionDeprecated = Edition.fromNumber(decoder.readEnum())
            }
            3 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.deprecationWarning = decoder.readString()
            }
            4 if tag.wireType == WireType.VARINT -> {
                msg.editionRemoved = Edition.fromNumber(decoder.readEnum())
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun FieldOptionsInternal.FeatureSupportInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[FieldOptionsInternal.FeatureSupportInternal.PresenceIndices.editionIntroduced]) {
        __result += WireSize.tag(1, WireType.VARINT) + WireSize.enum(this.editionIntroduced.number)
    }

    if (presenceMask[FieldOptionsInternal.FeatureSupportInternal.PresenceIndices.editionDeprecated]) {
        __result += WireSize.tag(2, WireType.VARINT) + WireSize.enum(this.editionDeprecated.number)
    }

    if (presenceMask[FieldOptionsInternal.FeatureSupportInternal.PresenceIndices.deprecationWarning]) {
        __result += WireSize.string(this.deprecationWarning).let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FieldOptionsInternal.FeatureSupportInternal.PresenceIndices.editionRemoved]) {
        __result += WireSize.tag(4, WireType.VARINT) + WireSize.enum(this.editionRemoved.number)
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun FieldOptions.FeatureSupport.asInternal(): FieldOptionsInternal.FeatureSupportInternal {
    return this as? FieldOptionsInternal.FeatureSupportInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun UninterpretedOptionInternal.NamePartInternal.checkRequiredFields() {
    if (!presenceMask[UninterpretedOptionInternal.NamePartInternal.PresenceIndices.namePart]) {
        throw ProtobufException.missingRequiredField("NamePart", "namePart")
    }

    if (!presenceMask[UninterpretedOptionInternal.NamePartInternal.PresenceIndices.isExtension]) {
        throw ProtobufException.missingRequiredField("NamePart", "isExtension")
    }
}

@InternalRpcApi
public fun UninterpretedOptionInternal.NamePartInternal.encodeWith(
    encoder: WireEncoder,
    config: ProtoConfig?,
) {
    if (presenceMask[UninterpretedOptionInternal.NamePartInternal.PresenceIndices.namePart]) {
        encoder.writeString(fieldNr = 1, value = this.namePart)
    }

    if (presenceMask[UninterpretedOptionInternal.NamePartInternal.PresenceIndices.isExtension]) {
        encoder.writeBool(fieldNr = 2, value = this.isExtension)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun UninterpretedOptionInternal.NamePartInternal.Companion.decodeWith(
    msg: UninterpretedOptionInternal.NamePartInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.namePart = decoder.readString()
            }
            2 if tag.wireType == WireType.VARINT -> {
                msg.isExtension = decoder.readBool()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun UninterpretedOptionInternal.NamePartInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[UninterpretedOptionInternal.NamePartInternal.PresenceIndices.namePart]) {
        __result += WireSize.string(this.namePart).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[UninterpretedOptionInternal.NamePartInternal.PresenceIndices.isExtension]) {
        __result += WireSize.tag(2, WireType.VARINT) + WireSize.bool(this.isExtension)
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun UninterpretedOption.NamePart.asInternal(): UninterpretedOptionInternal.NamePartInternal {
    return this as? UninterpretedOptionInternal.NamePartInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun FeatureSetInternal.VisibilityFeatureInternal.encodeWith(
    encoder: WireEncoder,
    config: ProtoConfig?,
) {
    // no fields to encode
    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun FeatureSetInternal.VisibilityFeatureInternal.Companion.decodeWith(
    msg: FeatureSetInternal.VisibilityFeatureInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun FeatureSetInternal.VisibilityFeatureInternal.computeSize(): Int {
    var __result = 0
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun FeatureSet.VisibilityFeature.asInternal(): FeatureSetInternal.VisibilityFeatureInternal {
    return this as? FeatureSetInternal.VisibilityFeatureInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun FeatureSetDefaultsInternal.FeatureSetEditionDefaultInternal.encodeWith(
    encoder: WireEncoder,
    config: ProtoConfig?,
) {
    if (presenceMask[FeatureSetDefaultsInternal.FeatureSetEditionDefaultInternal.PresenceIndices.edition]) {
        encoder.writeEnum(fieldNr = 3, value = this.edition.number)
    }

    if (presenceMask[FeatureSetDefaultsInternal.FeatureSetEditionDefaultInternal.PresenceIndices.overridableFeatures]) {
        encoder.writeMessage(fieldNr = 4, value = this.overridableFeatures.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (presenceMask[FeatureSetDefaultsInternal.FeatureSetEditionDefaultInternal.PresenceIndices.fixedFeatures]) {
        encoder.writeMessage(fieldNr = 5, value = this.fixedFeatures.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun FeatureSetDefaultsInternal.FeatureSetEditionDefaultInternal.Companion.decodeWith(
    msg: FeatureSetDefaultsInternal.FeatureSetEditionDefaultInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            3 if tag.wireType == WireType.VARINT -> {
                msg.edition = Edition.fromNumber(decoder.readEnum())
            }
            4 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__overridableFeaturesDelegate.getOrCreate(msg) { FeatureSetInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> FeatureSetInternal.decodeWith(msg, decoder, config) }
            }
            5 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__fixedFeaturesDelegate.getOrCreate(msg) { FeatureSetInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> FeatureSetInternal.decodeWith(msg, decoder, config) }
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun FeatureSetDefaultsInternal.FeatureSetEditionDefaultInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[FeatureSetDefaultsInternal.FeatureSetEditionDefaultInternal.PresenceIndices.edition]) {
        __result += WireSize.tag(3, WireType.VARINT) + WireSize.enum(this.edition.number)
    }

    if (presenceMask[FeatureSetDefaultsInternal.FeatureSetEditionDefaultInternal.PresenceIndices.overridableFeatures]) {
        __result += this.overridableFeatures.asInternal()._size.let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[FeatureSetDefaultsInternal.FeatureSetEditionDefaultInternal.PresenceIndices.fixedFeatures]) {
        __result += this.fixedFeatures.asInternal()._size.let { WireSize.tag(5, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun FeatureSetDefaults.FeatureSetEditionDefault.asInternal(): FeatureSetDefaultsInternal.FeatureSetEditionDefaultInternal {
    return this as? FeatureSetDefaultsInternal.FeatureSetEditionDefaultInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun SourceCodeInfoInternal.LocationInternal.encodeWith(
    encoder: WireEncoder,
    config: ProtoConfig?,
) {
    if (this.path.isNotEmpty()) {
        encoder.writePackedInt32(fieldNr = 1, value = this.path, fieldSize = WireSize.packedInt32(this.path))
    }

    if (this.span.isNotEmpty()) {
        encoder.writePackedInt32(fieldNr = 2, value = this.span, fieldSize = WireSize.packedInt32(this.span))
    }

    if (presenceMask[SourceCodeInfoInternal.LocationInternal.PresenceIndices.leadingComments]) {
        encoder.writeString(fieldNr = 3, value = this.leadingComments)
    }

    if (presenceMask[SourceCodeInfoInternal.LocationInternal.PresenceIndices.trailingComments]) {
        encoder.writeString(fieldNr = 4, value = this.trailingComments)
    }

    if (this.leadingDetachedComments.isNotEmpty()) {
        this.leadingDetachedComments.forEach {
            encoder.writeString(6, it)
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun SourceCodeInfoInternal.LocationInternal.Companion.decodeWith(
    msg: SourceCodeInfoInternal.LocationInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__pathDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedInt32())
            }
            1 if tag.wireType == WireType.VARINT -> {
                val target = msg.__pathDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readInt32()
                target.add(elem)
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__spanDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedInt32())
            }
            2 if tag.wireType == WireType.VARINT -> {
                val target = msg.__spanDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readInt32()
                target.add(elem)
            }
            3 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.leadingComments = decoder.readString()
            }
            4 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.trailingComments = decoder.readString()
            }
            6 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__leadingDetachedCommentsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readString()
                target.add(elem)
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun SourceCodeInfoInternal.LocationInternal.computeSize(): Int {
    var __result = 0
    if (this.path.isNotEmpty()) {
        __result += WireSize.packedInt32(this.path).let { WireSize.tag(1, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (this.span.isNotEmpty()) {
        __result += WireSize.packedInt32(this.span).let { WireSize.tag(2, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (presenceMask[SourceCodeInfoInternal.LocationInternal.PresenceIndices.leadingComments]) {
        __result += WireSize.string(this.leadingComments).let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[SourceCodeInfoInternal.LocationInternal.PresenceIndices.trailingComments]) {
        __result += WireSize.string(this.trailingComments).let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.leadingDetachedComments.isNotEmpty()) {
        __result += this.leadingDetachedComments.sumOf { element -> WireSize.string(element).let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun SourceCodeInfo.Location.asInternal(): SourceCodeInfoInternal.LocationInternal {
    return this as? SourceCodeInfoInternal.LocationInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun GeneratedCodeInfoInternal.AnnotationInternal.encodeWith(
    encoder: WireEncoder,
    config: ProtoConfig?,
) {
    if (this.path.isNotEmpty()) {
        encoder.writePackedInt32(fieldNr = 1, value = this.path, fieldSize = WireSize.packedInt32(this.path))
    }

    if (presenceMask[GeneratedCodeInfoInternal.AnnotationInternal.PresenceIndices.sourceFile]) {
        encoder.writeString(fieldNr = 2, value = this.sourceFile)
    }

    if (presenceMask[GeneratedCodeInfoInternal.AnnotationInternal.PresenceIndices.begin]) {
        encoder.writeInt32(fieldNr = 3, value = this.begin)
    }

    if (presenceMask[GeneratedCodeInfoInternal.AnnotationInternal.PresenceIndices.end]) {
        encoder.writeInt32(fieldNr = 4, value = this.end)
    }

    if (presenceMask[GeneratedCodeInfoInternal.AnnotationInternal.PresenceIndices.semantic]) {
        encoder.writeEnum(fieldNr = 5, value = this.semantic.number)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun GeneratedCodeInfoInternal.AnnotationInternal.Companion.decodeWith(
    msg: GeneratedCodeInfoInternal.AnnotationInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__pathDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                target.addAll(decoder.readPackedInt32())
            }
            1 if tag.wireType == WireType.VARINT -> {
                val target = msg.__pathDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readInt32()
                target.add(elem)
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.sourceFile = decoder.readString()
            }
            3 if tag.wireType == WireType.VARINT -> {
                msg.begin = decoder.readInt32()
            }
            4 if tag.wireType == WireType.VARINT -> {
                msg.end = decoder.readInt32()
            }
            5 if tag.wireType == WireType.VARINT -> {
                msg.semantic = GeneratedCodeInfo.Annotation.Semantic.fromNumber(decoder.readEnum())
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun GeneratedCodeInfoInternal.AnnotationInternal.computeSize(): Int {
    var __result = 0
    if (this.path.isNotEmpty()) {
        __result += WireSize.packedInt32(this.path).let { WireSize.tag(1, WireType.VARINT) + WireSize.int32(it) + it }
    }

    if (presenceMask[GeneratedCodeInfoInternal.AnnotationInternal.PresenceIndices.sourceFile]) {
        __result += WireSize.string(this.sourceFile).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[GeneratedCodeInfoInternal.AnnotationInternal.PresenceIndices.begin]) {
        __result += WireSize.tag(3, WireType.VARINT) + WireSize.int32(this.begin)
    }

    if (presenceMask[GeneratedCodeInfoInternal.AnnotationInternal.PresenceIndices.end]) {
        __result += WireSize.tag(4, WireType.VARINT) + WireSize.int32(this.end)
    }

    if (presenceMask[GeneratedCodeInfoInternal.AnnotationInternal.PresenceIndices.semantic]) {
        __result += WireSize.tag(5, WireType.VARINT) + WireSize.enum(this.semantic.number)
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun GeneratedCodeInfo.Annotation.asInternal(): GeneratedCodeInfoInternal.AnnotationInternal {
    return this as? GeneratedCodeInfoInternal.AnnotationInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun Edition.Companion.fromNumber(number: Int): Edition {
    return when (number) {
        0 -> {
            Edition.EDITION_UNKNOWN
        }
        900 -> {
            Edition.EDITION_LEGACY
        }
        998 -> {
            Edition.EDITION_PROTO2
        }
        999 -> {
            Edition.EDITION_PROTO3
        }
        1000 -> {
            Edition.EDITION_2023
        }
        1001 -> {
            Edition.EDITION_2024
        }
        9999 -> {
            Edition.EDITION_UNSTABLE
        }
        1 -> {
            Edition.EDITION_1_TEST_ONLY
        }
        2 -> {
            Edition.EDITION_2_TEST_ONLY
        }
        99997 -> {
            Edition.EDITION_99997_TEST_ONLY
        }
        99998 -> {
            Edition.EDITION_99998_TEST_ONLY
        }
        99999 -> {
            Edition.EDITION_99999_TEST_ONLY
        }
        2147483647 -> {
            Edition.EDITION_MAX
        }
        else -> {
            Edition.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun SymbolVisibility.Companion.fromNumber(number: Int): SymbolVisibility {
    return when (number) {
        0 -> {
            SymbolVisibility.VISIBILITY_UNSET
        }
        1 -> {
            SymbolVisibility.VISIBILITY_LOCAL
        }
        2 -> {
            SymbolVisibility.VISIBILITY_EXPORT
        }
        else -> {
            SymbolVisibility.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun ExtensionRangeOptions.VerificationState.Companion.fromNumber(
    number: Int,
): ExtensionRangeOptions.VerificationState {
    return when (number) {
        0 -> {
            ExtensionRangeOptions.VerificationState.DECLARATION
        }
        1 -> {
            ExtensionRangeOptions.VerificationState.UNVERIFIED
        }
        else -> {
            ExtensionRangeOptions.VerificationState.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun FieldDescriptorProto.Type.Companion.fromNumber(number: Int): FieldDescriptorProto.Type {
    return when (number) {
        1 -> {
            FieldDescriptorProto.Type.DOUBLE
        }
        2 -> {
            FieldDescriptorProto.Type.FLOAT
        }
        3 -> {
            FieldDescriptorProto.Type.INT64
        }
        4 -> {
            FieldDescriptorProto.Type.UINT64
        }
        5 -> {
            FieldDescriptorProto.Type.INT32
        }
        6 -> {
            FieldDescriptorProto.Type.FIXED64
        }
        7 -> {
            FieldDescriptorProto.Type.FIXED32
        }
        8 -> {
            FieldDescriptorProto.Type.BOOL
        }
        9 -> {
            FieldDescriptorProto.Type.STRING
        }
        10 -> {
            FieldDescriptorProto.Type.GROUP
        }
        11 -> {
            FieldDescriptorProto.Type.MESSAGE
        }
        12 -> {
            FieldDescriptorProto.Type.BYTES
        }
        13 -> {
            FieldDescriptorProto.Type.UINT32
        }
        14 -> {
            FieldDescriptorProto.Type.ENUM
        }
        15 -> {
            FieldDescriptorProto.Type.SFIXED32
        }
        16 -> {
            FieldDescriptorProto.Type.SFIXED64
        }
        17 -> {
            FieldDescriptorProto.Type.SINT32
        }
        18 -> {
            FieldDescriptorProto.Type.SINT64
        }
        else -> {
            FieldDescriptorProto.Type.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun FieldDescriptorProto.Label.Companion.fromNumber(
    number: Int,
): FieldDescriptorProto.Label {
    return when (number) {
        1 -> {
            FieldDescriptorProto.Label.OPTIONAL
        }
        3 -> {
            FieldDescriptorProto.Label.REPEATED
        }
        2 -> {
            FieldDescriptorProto.Label.REQUIRED
        }
        else -> {
            FieldDescriptorProto.Label.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun FileOptions.OptimizeMode.Companion.fromNumber(number: Int): FileOptions.OptimizeMode {
    return when (number) {
        1 -> {
            FileOptions.OptimizeMode.SPEED
        }
        2 -> {
            FileOptions.OptimizeMode.CODE_SIZE
        }
        3 -> {
            FileOptions.OptimizeMode.LITE_RUNTIME
        }
        else -> {
            FileOptions.OptimizeMode.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun FieldOptions.CType.Companion.fromNumber(number: Int): FieldOptions.CType {
    return when (number) {
        0 -> {
            FieldOptions.CType.STRING
        }
        1 -> {
            FieldOptions.CType.CORD
        }
        2 -> {
            FieldOptions.CType.STRING_PIECE
        }
        else -> {
            FieldOptions.CType.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun FieldOptions.JSType.Companion.fromNumber(number: Int): FieldOptions.JSType {
    return when (number) {
        0 -> {
            FieldOptions.JSType.JS_NORMAL
        }
        1 -> {
            FieldOptions.JSType.JS_STRING
        }
        2 -> {
            FieldOptions.JSType.JS_NUMBER
        }
        else -> {
            FieldOptions.JSType.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun FieldOptions.OptionRetention.Companion.fromNumber(
    number: Int,
): FieldOptions.OptionRetention {
    return when (number) {
        0 -> {
            FieldOptions.OptionRetention.RETENTION_UNKNOWN
        }
        1 -> {
            FieldOptions.OptionRetention.RETENTION_RUNTIME
        }
        2 -> {
            FieldOptions.OptionRetention.RETENTION_SOURCE
        }
        else -> {
            FieldOptions.OptionRetention.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun FieldOptions.OptionTargetType.Companion.fromNumber(
    number: Int,
): FieldOptions.OptionTargetType {
    return when (number) {
        0 -> {
            FieldOptions.OptionTargetType.TARGET_TYPE_UNKNOWN
        }
        1 -> {
            FieldOptions.OptionTargetType.TARGET_TYPE_FILE
        }
        2 -> {
            FieldOptions.OptionTargetType.TARGET_TYPE_EXTENSION_RANGE
        }
        3 -> {
            FieldOptions.OptionTargetType.TARGET_TYPE_MESSAGE
        }
        4 -> {
            FieldOptions.OptionTargetType.TARGET_TYPE_FIELD
        }
        5 -> {
            FieldOptions.OptionTargetType.TARGET_TYPE_ONEOF
        }
        6 -> {
            FieldOptions.OptionTargetType.TARGET_TYPE_ENUM
        }
        7 -> {
            FieldOptions.OptionTargetType.TARGET_TYPE_ENUM_ENTRY
        }
        8 -> {
            FieldOptions.OptionTargetType.TARGET_TYPE_SERVICE
        }
        9 -> {
            FieldOptions.OptionTargetType.TARGET_TYPE_METHOD
        }
        else -> {
            FieldOptions.OptionTargetType.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun MethodOptions.IdempotencyLevel.Companion.fromNumber(
    number: Int,
): MethodOptions.IdempotencyLevel {
    return when (number) {
        0 -> {
            MethodOptions.IdempotencyLevel.IDEMPOTENCY_UNKNOWN
        }
        1 -> {
            MethodOptions.IdempotencyLevel.NO_SIDE_EFFECTS
        }
        2 -> {
            MethodOptions.IdempotencyLevel.IDEMPOTENT
        }
        else -> {
            MethodOptions.IdempotencyLevel.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun FeatureSet.FieldPresence.Companion.fromNumber(number: Int): FeatureSet.FieldPresence {
    return when (number) {
        0 -> {
            FeatureSet.FieldPresence.FIELD_PRESENCE_UNKNOWN
        }
        1 -> {
            FeatureSet.FieldPresence.EXPLICIT
        }
        2 -> {
            FeatureSet.FieldPresence.IMPLICIT
        }
        3 -> {
            FeatureSet.FieldPresence.LEGACY_REQUIRED
        }
        else -> {
            FeatureSet.FieldPresence.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun FeatureSet.EnumType.Companion.fromNumber(number: Int): FeatureSet.EnumType {
    return when (number) {
        0 -> {
            FeatureSet.EnumType.ENUM_TYPE_UNKNOWN
        }
        1 -> {
            FeatureSet.EnumType.OPEN
        }
        2 -> {
            FeatureSet.EnumType.CLOSED
        }
        else -> {
            FeatureSet.EnumType.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun FeatureSet.RepeatedFieldEncoding.Companion.fromNumber(
    number: Int,
): FeatureSet.RepeatedFieldEncoding {
    return when (number) {
        0 -> {
            FeatureSet.RepeatedFieldEncoding.REPEATED_FIELD_ENCODING_UNKNOWN
        }
        1 -> {
            FeatureSet.RepeatedFieldEncoding.PACKED
        }
        2 -> {
            FeatureSet.RepeatedFieldEncoding.EXPANDED
        }
        else -> {
            FeatureSet.RepeatedFieldEncoding.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun FeatureSet.Utf8Validation.Companion.fromNumber(number: Int): FeatureSet.Utf8Validation {
    return when (number) {
        0 -> {
            FeatureSet.Utf8Validation.UTF8_VALIDATION_UNKNOWN
        }
        2 -> {
            FeatureSet.Utf8Validation.VERIFY
        }
        3 -> {
            FeatureSet.Utf8Validation.NONE
        }
        else -> {
            FeatureSet.Utf8Validation.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun FeatureSet.MessageEncoding.Companion.fromNumber(
    number: Int,
): FeatureSet.MessageEncoding {
    return when (number) {
        0 -> {
            FeatureSet.MessageEncoding.MESSAGE_ENCODING_UNKNOWN
        }
        1 -> {
            FeatureSet.MessageEncoding.LENGTH_PREFIXED
        }
        2 -> {
            FeatureSet.MessageEncoding.DELIMITED
        }
        else -> {
            FeatureSet.MessageEncoding.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun FeatureSet.JsonFormat.Companion.fromNumber(number: Int): FeatureSet.JsonFormat {
    return when (number) {
        0 -> {
            FeatureSet.JsonFormat.JSON_FORMAT_UNKNOWN
        }
        1 -> {
            FeatureSet.JsonFormat.ALLOW
        }
        2 -> {
            FeatureSet.JsonFormat.LEGACY_BEST_EFFORT
        }
        else -> {
            FeatureSet.JsonFormat.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun FeatureSet.EnforceNamingStyle.Companion.fromNumber(
    number: Int,
): FeatureSet.EnforceNamingStyle {
    return when (number) {
        0 -> {
            FeatureSet.EnforceNamingStyle.ENFORCE_NAMING_STYLE_UNKNOWN
        }
        1 -> {
            FeatureSet.EnforceNamingStyle.STYLE2024
        }
        2 -> {
            FeatureSet.EnforceNamingStyle.STYLE_LEGACY
        }
        else -> {
            FeatureSet.EnforceNamingStyle.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun FeatureSet.VisibilityFeature.DefaultSymbolVisibility.Companion.fromNumber(
    number: Int,
): FeatureSet.VisibilityFeature.DefaultSymbolVisibility {
    return when (number) {
        0 -> {
            FeatureSet.VisibilityFeature.DefaultSymbolVisibility.DEFAULT_SYMBOL_VISIBILITY_UNKNOWN
        }
        1 -> {
            FeatureSet.VisibilityFeature.DefaultSymbolVisibility.EXPORT_ALL
        }
        2 -> {
            FeatureSet.VisibilityFeature.DefaultSymbolVisibility.EXPORT_TOP_LEVEL
        }
        3 -> {
            FeatureSet.VisibilityFeature.DefaultSymbolVisibility.LOCAL_ALL
        }
        4 -> {
            FeatureSet.VisibilityFeature.DefaultSymbolVisibility.STRICT
        }
        else -> {
            FeatureSet.VisibilityFeature.DefaultSymbolVisibility.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun GeneratedCodeInfo.Annotation.Semantic.Companion.fromNumber(
    number: Int,
): GeneratedCodeInfo.Annotation.Semantic {
    return when (number) {
        0 -> {
            GeneratedCodeInfo.Annotation.Semantic.NONE
        }
        1 -> {
            GeneratedCodeInfo.Annotation.Semantic.SET
        }
        2 -> {
            GeneratedCodeInfo.Annotation.Semantic.ALIAS
        }
        else -> {
            GeneratedCodeInfo.Annotation.Semantic.UNRECOGNIZED(number)
        }
    }
}
