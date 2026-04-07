@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.internal.GeneratedProtoMessage

/**
* The protocol compiler can output a FileDescriptorSet containing the .proto
* files it parses.
*/
@GeneratedProtoMessage
public interface FileDescriptorSet {
    public val file: List<FileDescriptorProto>
}

/**
* Describes a complete .proto file.
*/
@GeneratedProtoMessage
public interface FileDescriptorProto {
    /**
    * file name, relative to root of source tree
    */
    public val name: String?
    /**
    * e.g. "foo", "foo.bar", etc.
    */
    public val `package`: String?
    /**
    * Names of files imported by this file.
    */
    public val dependency: List<String>
    /**
    * Indexes of the public imported files in the dependency list above.
    */
    public val publicDependency: List<Int>
    /**
    * Indexes of the weak imported files in the dependency list.
    * For Google-internal migration only. Do not use.
    */
    public val weakDependency: List<Int>
    /**
    * Names of files imported by this file purely for the purpose of providing
    * option extensions. These are excluded from the dependency list above.
    */
    public val optionDependency: List<String>
    /**
    * All top-level definitions in this file.
    */
    public val messageType: List<DescriptorProto>
    public val enumType: List<EnumDescriptorProto>
    public val service: List<ServiceDescriptorProto>
    public val extension: List<FieldDescriptorProto>
    public val options: FileOptions
    /**
    * This field contains optional information about the original source code.
    * You may safely remove this entire field without harming runtime
    * functionality of the descriptors -- the information is needed only by
    * development tools.
    */
    public val sourceCodeInfo: SourceCodeInfo
    /**
    * The syntax of the proto file.
    * The supported values are "proto2", "proto3", and "editions".
    * 
    * If `edition` is present, this value must be "editions".
    * WARNING: This field should only be used by protobuf plugins or special
    * cases like the proto compiler. Other uses are discouraged and
    * developers should rely on the protoreflect APIs for their client language.
    */
    public val syntax: String?
    /**
    * The edition of the proto file.
    * WARNING: This field should only be used by protobuf plugins or special
    * cases like the proto compiler. Other uses are discouraged and
    * developers should rely on the protoreflect APIs for their client language.
    */
    public val edition: Edition?
}

/**
* Describes a message type.
*/
@GeneratedProtoMessage
public interface DescriptorProto {
    public val name: String?
    public val field: List<FieldDescriptorProto>
    public val extension: List<FieldDescriptorProto>
    public val nestedType: List<DescriptorProto>
    public val enumType: List<EnumDescriptorProto>
    public val extensionRange: List<ExtensionRange>
    public val oneofDecl: List<OneofDescriptorProto>
    public val options: MessageOptions
    public val reservedRange: List<ReservedRange>
    /**
    * Reserved field names, which may not be used by fields in the same message.
    * A given name may only be reserved once.
    */
    public val reservedName: List<String>
    /**
    * Support for `export` and `local` keywords on enums.
    */
    public val visibility: SymbolVisibility?

    @GeneratedProtoMessage
    public interface ExtensionRange {
        /**
        * Inclusive.
        */
        public val start: Int?
        /**
        * Exclusive.
        */
        public val end: Int?
        public val options: ExtensionRangeOptions
    }

    /**
    * Range of reserved tag numbers. Reserved tag numbers may not be used by
    * fields or extension ranges in the same message. Reserved ranges may
    * not overlap.
    */
    @GeneratedProtoMessage
    public interface ReservedRange {
        /**
        * Inclusive.
        */
        public val start: Int?
        /**
        * Exclusive.
        */
        public val end: Int?
    }
}

@GeneratedProtoMessage
public interface ExtensionRangeOptions {
    /**
    * The parser stores options it doesn't recognize here. See above.
    */
    public val uninterpretedOption: List<UninterpretedOption>
    /**
    * For external users: DO NOT USE. We are in the process of open sourcing
    * extension declaration and executing internal cleanups before it can be
    * used externally.
    */
    public val declaration: List<Declaration>
    /**
    * Any features defined in the specific edition.
    */
    public val features: FeatureSet
    /**
    * The verification state of the range.
    * TODO: flip the default to DECLARATION once all empty ranges
    * are marked as UNVERIFIED.
    */
    public val verification: VerificationState

    @GeneratedProtoMessage
    public interface Declaration {
        /**
        * The extension number declared within the extension range.
        */
        public val number: Int?
        /**
        * The fully-qualified name of the extension field. There must be a leading
        * dot in front of the full name.
        */
        public val fullName: String?
        /**
        * The fully-qualified type name of the extension field. Unlike
        * Metadata.type, Declaration.type must have a leading dot for messages
        * and enums.
        */
        public val type: String?
        /**
        * If true, indicates that the number is reserved in the extension range,
        * and any extension field with the number will fail to compile. Set this
        * when a declared extension field is deleted.
        */
        public val reserved: Boolean?
        /**
        * If true, indicates that the extension must be defined as repeated.
        * Otherwise the extension must be defined as optional.
        */
        public val repeated: Boolean?
    }

    /**
    * The verification state of the extension range.
    */
    public sealed class VerificationState(public open val number: Int) {
        /**
        * All the extensions of the range must be declared.
        */
        public data object DECLARATION: VerificationState(number = 0)

        public data object UNVERIFIED: VerificationState(number = 1)

        public data class UNRECOGNIZED(override val number: Int): VerificationState(number)

        public companion object {
            public val entries: List<VerificationState> by lazy { listOf(DECLARATION, UNVERIFIED) }
        }
    }
}

/**
* Describes a field within a message.
*/
@GeneratedProtoMessage
public interface FieldDescriptorProto {
    public val name: String?
    public val number: Int?
    public val label: Label?
    /**
    * If type_name is set, this need not be set.  If both this and type_name
    * are set, this must be one of TYPE_ENUM, TYPE_MESSAGE or TYPE_GROUP.
    */
    public val type: Type?
    /**
    * For message and enum types, this is the name of the type.  If the name
    * starts with a '.', it is fully-qualified.  Otherwise, C++-like scoping
    * rules are used to find the type (i.e. first the nested types within this
    * message are searched, then within the parent, on up to the root
    * namespace).
    */
    public val typeName: String?
    /**
    * For extensions, this is the name of the type being extended.  It is
    * resolved in the same manner as type_name.
    */
    public val extendee: String?
    /**
    * For numeric types, contains the original text representation of the value.
    * For booleans, "true" or "false".
    * For strings, contains the default text contents (not escaped in any way).
    * For bytes, contains the C escaped value.  All bytes >= 128 are escaped.
    */
    public val defaultValue: String?
    /**
    * If set, gives the index of a oneof in the containing type's oneof_decl
    * list.  This field is a member of that oneof.
    */
    public val oneofIndex: Int?
    /**
    * JSON name of this field. The value is set by protocol compiler. If the
    * user has set a "json_name" option on this field, that option's value
    * will be used. Otherwise, it's deduced from the field's name by converting
    * it to camelCase.
    */
    public val jsonName: String?
    public val options: FieldOptions
    /**
    * If true, this is a proto3 "optional". When a proto3 field is optional, it
    * tracks presence regardless of field type.
    * 
    * When proto3_optional is true, this field must belong to a oneof to signal
    * to old proto3 clients that presence is tracked for this field. This oneof
    * is known as a "synthetic" oneof, and this field must be its sole member
    * (each proto3 optional field gets its own synthetic oneof). Synthetic oneofs
    * exist in the descriptor only, and do not generate any API. Synthetic oneofs
    * must be ordered after all "real" oneofs.
    * 
    * For message fields, proto3_optional doesn't create any semantic change,
    * since non-repeated message fields always track presence. However it still
    * indicates the semantic detail of whether the user wrote "optional" or not.
    * This can be useful for round-tripping the .proto file. For consistency we
    * give message fields a synthetic oneof also, even though it is not required
    * to track presence. This is especially important because the parser can't
    * tell if a field is a message or an enum, so it must always create a
    * synthetic oneof.
    * 
    * Proto2 optional fields do not set this flag, because they already indicate
    * optional with `LABEL_OPTIONAL`.
    */
    public val proto3Optional: Boolean?

    public sealed class Type(public open val number: Int) {
        /**
        * 0 is reserved for errors.
        * Order is weird for historical reasons.
        */
        public data object DOUBLE: Type(number = 1)

        public data object FLOAT: Type(number = 2)

        /**
        * Not ZigZag encoded.  Negative numbers take 10 bytes.  Use TYPE_SINT64 if
        * negative values are likely.
        */
        public data object INT64: Type(number = 3)

        public data object UINT64: Type(number = 4)

        /**
        * Not ZigZag encoded.  Negative numbers take 10 bytes.  Use TYPE_SINT32 if
        * negative values are likely.
        */
        public data object INT32: Type(number = 5)

        public data object FIXED64: Type(number = 6)

        public data object FIXED32: Type(number = 7)

        public data object BOOL: Type(number = 8)

        public data object STRING: Type(number = 9)

        /**
        * Tag-delimited aggregate.
        * Group type is deprecated and not supported after google.protobuf. However, Proto3
        * implementations should still be able to parse the group wire format and
        * treat group fields as unknown fields.  In Editions, the group wire format
        * can be enabled via the `message_encoding` feature.
        */
        public data object GROUP: Type(number = 10)

        /**
        * Length-delimited aggregate.
        */
        public data object MESSAGE: Type(number = 11)

        /**
        * New in version 2.
        */
        public data object BYTES: Type(number = 12)

        public data object UINT32: Type(number = 13)

        public data object ENUM: Type(number = 14)

        public data object SFIXED32: Type(number = 15)

        public data object SFIXED64: Type(number = 16)

        /**
        * Uses ZigZag encoding.
        */
        public data object SINT32: Type(number = 17)

        /**
        * Uses ZigZag encoding.
        */
        public data object SINT64: Type(number = 18)

        public data class UNRECOGNIZED(override val number: Int): Type(number)

        public companion object {
            public val entries: List<Type> by lazy { listOf(DOUBLE, FLOAT, INT64, UINT64, INT32, FIXED64, FIXED32, BOOL, STRING, GROUP, MESSAGE, BYTES, UINT32, ENUM, SFIXED32, SFIXED64, SINT32, SINT64) }
        }
    }

    public sealed class Label(public open val number: Int) {
        /**
        * 0 is reserved for errors
        */
        public data object OPTIONAL: Label(number = 1)

        public data object REPEATED: Label(number = 3)

        /**
        * The required label is only allowed in google.protobuf.  In proto3 and Editions
        * it's explicitly prohibited.  In Editions, the `field_presence` feature
        * can be used to get this behavior.
        */
        public data object REQUIRED: Label(number = 2)

        public data class UNRECOGNIZED(override val number: Int): Label(number)

        public companion object {
            public val entries: List<Label> by lazy { listOf(OPTIONAL, REQUIRED, REPEATED) }
        }
    }
}

/**
* Describes a oneof.
*/
@GeneratedProtoMessage
public interface OneofDescriptorProto {
    public val name: String?
    public val options: OneofOptions
}

/**
* Describes an enum type.
*/
@GeneratedProtoMessage
public interface EnumDescriptorProto {
    public val name: String?
    public val value: List<EnumValueDescriptorProto>
    public val options: EnumOptions
    /**
    * Range of reserved numeric values. Reserved numeric values may not be used
    * by enum values in the same enum declaration. Reserved ranges may not
    * overlap.
    */
    public val reservedRange: List<EnumReservedRange>
    /**
    * Reserved enum value names, which may not be reused. A given name may only
    * be reserved once.
    */
    public val reservedName: List<String>
    /**
    * Support for `export` and `local` keywords on enums.
    */
    public val visibility: SymbolVisibility?

    /**
    * Range of reserved numeric values. Reserved values may not be used by
    * entries in the same enum. Reserved ranges may not overlap.
    * 
    * Note that this is distinct from DescriptorProto.ReservedRange in that it
    * is inclusive such that it can appropriately represent the entire int32
    * domain.
    */
    @GeneratedProtoMessage
    public interface EnumReservedRange {
        /**
        * Inclusive.
        */
        public val start: Int?
        /**
        * Inclusive.
        */
        public val end: Int?
    }
}

/**
* Describes a value within an enum.
*/
@GeneratedProtoMessage
public interface EnumValueDescriptorProto {
    public val name: String?
    public val number: Int?
    public val options: EnumValueOptions
}

/**
* Describes a service.
*/
@GeneratedProtoMessage
public interface ServiceDescriptorProto {
    public val name: String?
    public val method: List<MethodDescriptorProto>
    public val options: ServiceOptions
}

/**
* Describes a method of a service.
*/
@GeneratedProtoMessage
public interface MethodDescriptorProto {
    public val name: String?
    /**
    * Input and output type names.  These are resolved in the same way as
    * FieldDescriptorProto.type_name, but must refer to a message type.
    */
    public val inputType: String?
    public val outputType: String?
    public val options: MethodOptions
    /**
    * Identifies if client streams multiple client messages
    */
    public val clientStreaming: Boolean
    /**
    * Identifies if server streams multiple server messages
    */
    public val serverStreaming: Boolean
}

/**
* ===================================================================
* Options
* 
* Each of the definitions above may have "options" attached.  These are
* just annotations which may cause code to be generated slightly differently
* or may contain hints for code that manipulates protocol messages.
* 
* Clients may define custom options as extensions of the *Options messages.
* These extensions may not yet be known at parsing time, so the parser cannot
* store the values in them.  Instead it stores them in a field in the *Options
* message called uninterpreted_option. This field must have the same name
* across all *Options messages. We then use this field to populate the
* extensions when we build a descriptor, at which point all protos have been
* parsed and so all extensions are known.
* 
* Extension numbers for custom options may be chosen as follows:
* * For options which will only be used within a single application or
*   organization, or for experimental options, use field numbers 50000
*   through 99999.  It is up to you to ensure that you do not use the
*   same number for multiple options.
* * For options which will be published and used publicly by multiple
*   independent entities, e-mail protobuf-global-extension-registry@google.com
*   to reserve extension numbers. Simply provide your project name (e.g.
*   Objective-C plugin) and your project website (if available) -- there's no
*   need to explain how you intend to use them. Usually you only need one
*   extension number. You can declare multiple options with only one extension
*   number by putting them in a sub-message. See the Custom Options section of
*   the docs for examples:
*   https://developers.google.com/protocol-buffers/docs/proto#options
*   If this turns out to be popular, a web service will be set up
*   to automatically assign option numbers.
*/
@GeneratedProtoMessage
public interface FileOptions {
    /**
    * Sets the Java package where classes generated from this .proto will be
    * placed.  By default, the proto package is used, but this is often
    * inappropriate because proto packages do not normally start with backwards
    * domain names.
    */
    public val javaPackage: String?
    /**
    * Controls the name of the wrapper Java class generated for the .proto file.
    * That class will always contain the .proto file's getDescriptor() method as
    * well as any top-level extensions defined in the .proto file.
    * If java_multiple_files is disabled, then all the other classes from the
    * .proto file will be nested inside the single wrapper outer class.
    */
    public val javaOuterClassname: String?
    /**
    * If enabled, then the Java code generator will generate a separate .java
    * file for each top-level message, enum, and service defined in the .proto
    * file.  Thus, these types will *not* be nested inside the wrapper class
    * named by java_outer_classname.  However, the wrapper class will still be
    * generated to contain the file's getDescriptor() method as well as any
    * top-level extensions defined in the file.
    */
    public val javaMultipleFiles: Boolean
    /**
    * This option does nothing.
    */
    @Deprecated("This declaration is deprecated in .proto file")
    public val javaGenerateEqualsAndHash: Boolean?
    /**
    * A proto2 file can set this to true to opt in to UTF-8 checking for Java,
    * which will throw an exception if invalid UTF-8 is parsed from the wire or
    * assigned to a string field.
    * 
    * TODO: clarify exactly what kinds of field types this option
    * applies to, and update these docs accordingly.
    * 
    * Proto3 files already perform these checks. Setting the option explicitly to
    * false has no effect: it cannot be used to opt proto3 files out of UTF-8
    * checks.
    */
    public val javaStringCheckUtf8: Boolean
    public val optimizeFor: OptimizeMode
    /**
    * Sets the Go package where structs generated from this .proto will be
    * placed. If omitted, the Go package will be derived from the following:
    *   - The basename of the package import path, if provided.
    *   - Otherwise, the package statement in the .proto file, if present.
    *   - Otherwise, the basename of the .proto file, without extension.
    */
    public val goPackage: String?
    /**
    * Should generic services be generated in each language?  "Generic" services
    * are not specific to any particular RPC system.  They are generated by the
    * main code generators in each language (without additional plugins).
    * Generic services were the only kind of service generation supported by
    * early versions of google.protobuf.
    * 
    * Generic services are now considered deprecated in favor of using plugins
    * that generate code specific to your particular RPC system.  Therefore,
    * these default to false.  Old code which depends on generic services should
    * explicitly set them to true.
    */
    public val ccGenericServices: Boolean
    public val javaGenericServices: Boolean
    public val pyGenericServices: Boolean
    /**
    * Is this file deprecated?
    * Depending on the target platform, this can emit Deprecated annotations
    * for everything in the file, or it will be completely ignored; in the very
    * least, this is a formalization for deprecating files.
    */
    public val deprecated: Boolean
    /**
    * Enables the use of arenas for the proto messages in this file. This applies
    * only to generated classes for C++.
    */
    public val ccEnableArenas: Boolean
    /**
    * Sets the objective c class prefix which is prepended to all objective c
    * generated classes from this .proto. There is no default.
    */
    public val objcClassPrefix: String?
    /**
    * Namespace for generated classes; defaults to the package.
    */
    public val csharpNamespace: String?
    /**
    * By default Swift generators will take the proto package and CamelCase it
    * replacing '.' with underscore and use that to prefix the types/symbols
    * defined. When this options is provided, they will use this value instead
    * to prefix the types/symbols defined.
    */
    public val swiftPrefix: String?
    /**
    * Sets the php class prefix which is prepended to all php generated classes
    * from this .proto. Default is empty.
    */
    public val phpClassPrefix: String?
    /**
    * Use this option to change the namespace of php generated classes. Default
    * is empty. When this option is empty, the package name will be used for
    * determining the namespace.
    */
    public val phpNamespace: String?
    /**
    * Use this option to change the namespace of php generated metadata classes.
    * Default is empty. When this option is empty, the proto file name will be
    * used for determining the namespace.
    */
    public val phpMetadataNamespace: String?
    /**
    * Use this option to change the package of ruby generated classes. Default
    * is empty. When this option is not set, the package name will be used for
    * determining the ruby package.
    */
    public val rubyPackage: String?
    /**
    * Any features defined in the specific edition.
    * WARNING: This field should only be used by protobuf plugins or special
    * cases like the proto compiler. Other uses are discouraged and
    * developers should rely on the protoreflect APIs for their client language.
    */
    public val features: FeatureSet
    /**
    * The parser stores options it doesn't recognize here.
    * See the documentation for the "Options" section above.
    */
    public val uninterpretedOption: List<UninterpretedOption>

    /**
    * Generated classes can be optimized for speed or code size.
    */
    public sealed class OptimizeMode(public open val number: Int) {
        /**
        * Generate complete code for parsing, serialization,
        */
        public data object SPEED: OptimizeMode(number = 1)

        /**
        * etc.
        * 
        * Use ReflectionOps to implement these methods.
        */
        public data object CODE_SIZE: OptimizeMode(number = 2)

        /**
        * Generate code using MessageLite and the lite runtime.
        */
        public data object LITE_RUNTIME: OptimizeMode(number = 3)

        public data class UNRECOGNIZED(override val number: Int): OptimizeMode(number)

        public companion object {
            public val entries: List<OptimizeMode> by lazy { listOf(SPEED, CODE_SIZE, LITE_RUNTIME) }
        }
    }
}

@GeneratedProtoMessage
public interface MessageOptions {
    /**
    * Set true to use the old proto1 MessageSet wire format for extensions.
    * This is provided for backwards-compatibility with the MessageSet wire
    * format.  You should not use this for any other reason:  It's less
    * efficient, has fewer features, and is more complicated.
    * 
    * The message must be defined exactly as follows:
    *   message Foo {
    *     option message_set_wire_format = true;
    *     extensions 4 to max;
    *   }
    * Note that the message cannot have any defined fields; MessageSets only
    * have extensions.
    * 
    * All extensions of your type must be singular messages; e.g. they cannot
    * be int32s, enums, or repeated messages.
    * 
    * Because this is an option, the above two restrictions are not enforced by
    * the protocol compiler.
    */
    public val messageSetWireFormat: Boolean
    /**
    * Disables the generation of the standard "descriptor()" accessor, which can
    * conflict with a field of the same name.  This is meant to make migration
    * from proto1 easier; new code should avoid fields named "descriptor".
    */
    public val noStandardDescriptorAccessor: Boolean
    /**
    * Is this message deprecated?
    * Depending on the target platform, this can emit Deprecated annotations
    * for the message, or it will be completely ignored; in the very least,
    * this is a formalization for deprecating messages.
    */
    public val deprecated: Boolean
    /**
    * Whether the message is an automatically generated map entry type for the
    * maps field.
    * 
    * For maps fields:
    *     map<KeyType, ValueType> map_field = 1;
    * The parsed descriptor looks like:
    *     message MapFieldEntry {
    *         option map_entry = true;
    *         optional KeyType key = 1;
    *         optional ValueType value = 2;
    *     }
    *     repeated MapFieldEntry map_field = 1;
    * 
    * Implementations may choose not to generate the map_entry=true message, but
    * use a native map in the target language to hold the keys and values.
    * The reflection APIs in such implementations still need to work as
    * if the field is a repeated message field.
    * 
    * NOTE: Do not set the option in .proto files. Always use the maps syntax
    * instead. The option should only be implicitly set by the proto compiler
    * parser.
    */
    public val mapEntry: Boolean?
    /**
    * Enable the legacy handling of JSON field name conflicts.  This lowercases
    * and strips underscored from the fields before comparison in proto3 only.
    * The new behavior takes `json_name` into account and applies to proto2 as
    * well.
    * 
    * This should only be used as a temporary measure against broken builds due
    * to the change in behavior for JSON field name conflicts.
    * 
    * TODO This is legacy behavior we plan to remove once downstream
    * teams have had time to migrate.
    */
    @Deprecated("This declaration is deprecated in .proto file")
    public val deprecatedLegacyJsonFieldConflicts: Boolean?
    /**
    * Any features defined in the specific edition.
    * WARNING: This field should only be used by protobuf plugins or special
    * cases like the proto compiler. Other uses are discouraged and
    * developers should rely on the protoreflect APIs for their client language.
    */
    public val features: FeatureSet
    /**
    * The parser stores options it doesn't recognize here. See above.
    */
    public val uninterpretedOption: List<UninterpretedOption>
}

@GeneratedProtoMessage
public interface FieldOptions {
    /**
    * NOTE: ctype is deprecated. Use `features.(pb.cpp).string_type` instead.
    * The ctype option instructs the C++ code generator to use a different
    * representation of the field than it normally would.  See the specific
    * options below.  This option is only implemented to support use of
    * [ctype=CORD] and [ctype=STRING] (the default) on non-repeated fields of
    * type "bytes" in the open source release.
    * TODO: make ctype actually deprecated.
    */
    public val ctype: CType
    /**
    * The packed option can be enabled for repeated primitive fields to enable
    * a more efficient representation on the wire. Rather than repeatedly
    * writing the tag and type for each element, the entire array is encoded as
    * a single length-delimited blob. In proto3, only explicit setting it to
    * false will avoid using packed encoding.  This option is prohibited in
    * Editions, but the `repeated_field_encoding` feature can be used to control
    * the behavior.
    */
    public val packed: Boolean?
    /**
    * The jstype option determines the JavaScript type used for values of the
    * field.  The option is permitted only for 64 bit integral and fixed types
    * (int64, uint64, sint64, fixed64, sfixed64).  A field with jstype JS_STRING
    * is represented as JavaScript string, which avoids loss of precision that
    * can happen when a large value is converted to a floating point JavaScript.
    * Specifying JS_NUMBER for the jstype causes the generated JavaScript code to
    * use the JavaScript "number" type.  The behavior of the default option
    * JS_NORMAL is implementation dependent.
    * 
    * This option is an enum to permit additional types to be added, e.g.
    * goog.math.Integer.
    */
    public val jstype: JSType
    /**
    * Should this field be parsed lazily?  Lazy applies only to message-type
    * fields.  It means that when the outer message is initially parsed, the
    * inner message's contents will not be parsed but instead stored in encoded
    * form.  The inner message will actually be parsed when it is first accessed.
    * 
    * This is only a hint.  Implementations are free to choose whether to use
    * eager or lazy parsing regardless of the value of this option.  However,
    * setting this option true suggests that the protocol author believes that
    * using lazy parsing on this field is worth the additional bookkeeping
    * overhead typically needed to implement it.
    * 
    * This option does not affect the public interface of any generated code;
    * all method signatures remain the same.  Furthermore, thread-safety of the
    * interface is not affected by this option; const methods remain safe to
    * call from multiple threads concurrently, while non-const methods continue
    * to require exclusive access.
    * 
    * Note that lazy message fields are still eagerly verified to check
    * ill-formed wireformat or missing required fields. Calling IsInitialized()
    * on the outer message would fail if the inner message has missing required
    * fields. Failed verification would result in parsing failure (except when
    * uninitialized messages are acceptable).
    */
    public val lazy: Boolean
    /**
    * unverified_lazy does no correctness checks on the byte stream. This should
    * only be used where lazy with verification is prohibitive for performance
    * reasons.
    */
    public val unverifiedLazy: Boolean
    /**
    * Is this field deprecated?
    * Depending on the target platform, this can emit Deprecated annotations
    * for accessors, or it will be completely ignored; in the very least, this
    * is a formalization for deprecating fields.
    */
    public val deprecated: Boolean
    /**
    * For Google-internal migration only. Do not use.
    */
    public val weak: Boolean
    /**
    * Indicate that the field value should not be printed out when using debug
    * formats, e.g. when the field contains sensitive credentials.
    */
    public val debugRedact: Boolean
    public val retention: OptionRetention?
    public val targets: List<OptionTargetType>
    public val editionDefaults: List<EditionDefault>
    /**
    * Any features defined in the specific edition.
    * WARNING: This field should only be used by protobuf plugins or special
    * cases like the proto compiler. Other uses are discouraged and
    * developers should rely on the protoreflect APIs for their client language.
    */
    public val features: FeatureSet
    public val featureSupport: FeatureSupport
    /**
    * The parser stores options it doesn't recognize here. See above.
    */
    public val uninterpretedOption: List<UninterpretedOption>

    @GeneratedProtoMessage
    public interface EditionDefault {
        public val edition: Edition?
        /**
        * Textproto value.
        */
        public val value: String?
    }

    /**
    * Information about the support window of a feature.
    */
    @GeneratedProtoMessage
    public interface FeatureSupport {
        /**
        * The edition that this feature was first available in.  In editions
        * earlier than this one, the default assigned to EDITION_LEGACY will be
        * used, and proto files will not be able to override it.
        */
        public val editionIntroduced: Edition?
        /**
        * The edition this feature becomes deprecated in.  Using this after this
        * edition may trigger warnings.
        */
        public val editionDeprecated: Edition?
        /**
        * The deprecation warning text if this feature is used after the edition it
        * was marked deprecated in.
        */
        public val deprecationWarning: String?
        /**
        * The edition this feature is no longer available in.  In editions after
        * this one, the last default assigned will be used, and proto files will
        * not be able to override it.
        */
        public val editionRemoved: Edition?
    }

    public sealed class CType(public open val number: Int) {
        /**
        * Default mode.
        */
        public data object STRING: CType(number = 0)

        /**
        * The option [ctype=CORD] may be applied to a non-repeated field of type
        * "bytes". It indicates that in C++, the data should be stored in a Cord
        * instead of a string.  For very large strings, this may reduce memory
        * fragmentation. It may also allow better performance when parsing from a
        * Cord, or when parsing with aliasing enabled, as the parsed Cord may then
        * alias the original buffer.
        */
        public data object CORD: CType(number = 1)

        public data object STRING_PIECE: CType(number = 2)

        public data class UNRECOGNIZED(override val number: Int): CType(number)

        public companion object {
            public val entries: List<CType> by lazy { listOf(STRING, CORD, STRING_PIECE) }
        }
    }

    public sealed class JSType(public open val number: Int) {
        /**
        * Use the default type.
        */
        public data object JS_NORMAL: JSType(number = 0)

        /**
        * Use JavaScript strings.
        */
        public data object JS_STRING: JSType(number = 1)

        /**
        * Use JavaScript numbers.
        */
        public data object JS_NUMBER: JSType(number = 2)

        public data class UNRECOGNIZED(override val number: Int): JSType(number)

        public companion object {
            public val entries: List<JSType> by lazy { listOf(JS_NORMAL, JS_STRING, JS_NUMBER) }
        }
    }

    /**
    * If set to RETENTION_SOURCE, the option will be omitted from the binary.
    */
    public sealed class OptionRetention(public open val number: Int) {
        public data object RETENTION_UNKNOWN: OptionRetention(number = 0)

        public data object RETENTION_RUNTIME: OptionRetention(number = 1)

        public data object RETENTION_SOURCE: OptionRetention(number = 2)

        public data class UNRECOGNIZED(override val number: Int): OptionRetention(number)

        public companion object {
            public val entries: List<OptionRetention> by lazy { listOf(RETENTION_UNKNOWN, RETENTION_RUNTIME, RETENTION_SOURCE) }
        }
    }

    /**
    * This indicates the types of entities that the field may apply to when used
    * as an option. If it is unset, then the field may be freely used as an
    * option on any kind of entity.
    */
    public sealed class OptionTargetType(public open val number: Int) {
        public data object TARGET_TYPE_UNKNOWN: OptionTargetType(number = 0)

        public data object TARGET_TYPE_FILE: OptionTargetType(number = 1)

        public data object TARGET_TYPE_EXTENSION_RANGE: OptionTargetType(number = 2)

        public data object TARGET_TYPE_MESSAGE: OptionTargetType(number = 3)

        public data object TARGET_TYPE_FIELD: OptionTargetType(number = 4)

        public data object TARGET_TYPE_ONEOF: OptionTargetType(number = 5)

        public data object TARGET_TYPE_ENUM: OptionTargetType(number = 6)

        public data object TARGET_TYPE_ENUM_ENTRY: OptionTargetType(number = 7)

        public data object TARGET_TYPE_SERVICE: OptionTargetType(number = 8)

        public data object TARGET_TYPE_METHOD: OptionTargetType(number = 9)

        public data class UNRECOGNIZED(override val number: Int): OptionTargetType(number)

        public companion object {
            public val entries: List<OptionTargetType> by lazy { listOf(TARGET_TYPE_UNKNOWN, TARGET_TYPE_FILE, TARGET_TYPE_EXTENSION_RANGE, TARGET_TYPE_MESSAGE, TARGET_TYPE_FIELD, TARGET_TYPE_ONEOF, TARGET_TYPE_ENUM, TARGET_TYPE_ENUM_ENTRY, TARGET_TYPE_SERVICE, TARGET_TYPE_METHOD) }
        }
    }
}

@GeneratedProtoMessage
public interface OneofOptions {
    /**
    * Any features defined in the specific edition.
    * WARNING: This field should only be used by protobuf plugins or special
    * cases like the proto compiler. Other uses are discouraged and
    * developers should rely on the protoreflect APIs for their client language.
    */
    public val features: FeatureSet
    /**
    * The parser stores options it doesn't recognize here. See above.
    */
    public val uninterpretedOption: List<UninterpretedOption>
}

@GeneratedProtoMessage
public interface EnumOptions {
    /**
    * Set this option to true to allow mapping different tag names to the same
    * value.
    */
    public val allowAlias: Boolean?
    /**
    * Is this enum deprecated?
    * Depending on the target platform, this can emit Deprecated annotations
    * for the enum, or it will be completely ignored; in the very least, this
    * is a formalization for deprecating enums.
    */
    public val deprecated: Boolean
    /**
    * Enable the legacy handling of JSON field name conflicts.  This lowercases
    * and strips underscored from the fields before comparison in proto3 only.
    * The new behavior takes `json_name` into account and applies to proto2 as
    * well.
    * TODO Remove this legacy behavior once downstream teams have
    * had time to migrate.
    */
    @Deprecated("This declaration is deprecated in .proto file")
    public val deprecatedLegacyJsonFieldConflicts: Boolean?
    /**
    * Any features defined in the specific edition.
    * WARNING: This field should only be used by protobuf plugins or special
    * cases like the proto compiler. Other uses are discouraged and
    * developers should rely on the protoreflect APIs for their client language.
    */
    public val features: FeatureSet
    /**
    * The parser stores options it doesn't recognize here. See above.
    */
    public val uninterpretedOption: List<UninterpretedOption>
}

@GeneratedProtoMessage
public interface EnumValueOptions {
    /**
    * Is this enum value deprecated?
    * Depending on the target platform, this can emit Deprecated annotations
    * for the enum value, or it will be completely ignored; in the very least,
    * this is a formalization for deprecating enum values.
    */
    public val deprecated: Boolean
    /**
    * Any features defined in the specific edition.
    * WARNING: This field should only be used by protobuf plugins or special
    * cases like the proto compiler. Other uses are discouraged and
    * developers should rely on the protoreflect APIs for their client language.
    */
    public val features: FeatureSet
    /**
    * Indicate that fields annotated with this enum value should not be printed
    * out when using debug formats, e.g. when the field contains sensitive
    * credentials.
    */
    public val debugRedact: Boolean
    /**
    * Information about the support window of a feature value.
    */
    public val featureSupport: FieldOptions.FeatureSupport
    /**
    * The parser stores options it doesn't recognize here. See above.
    */
    public val uninterpretedOption: List<UninterpretedOption>
}

@GeneratedProtoMessage
public interface ServiceOptions {
    /**
    * Any features defined in the specific edition.
    * WARNING: This field should only be used by protobuf plugins or special
    * cases like the proto compiler. Other uses are discouraged and
    * developers should rely on the protoreflect APIs for their client language.
    */
    public val features: FeatureSet
    /**
    * Note:  Field numbers 1 through 32 are reserved for Google's internal RPC
    *   framework.  We apologize for hoarding these numbers to ourselves, but
    *   we were already using them long before we decided to release Protocol
    *   Buffers.
    * 
    * Is this service deprecated?
    * Depending on the target platform, this can emit Deprecated annotations
    * for the service, or it will be completely ignored; in the very least,
    * this is a formalization for deprecating services.
    */
    public val deprecated: Boolean
    /**
    * The parser stores options it doesn't recognize here. See above.
    */
    public val uninterpretedOption: List<UninterpretedOption>
}

@GeneratedProtoMessage
public interface MethodOptions {
    /**
    * Note:  Field numbers 1 through 32 are reserved for Google's internal RPC
    *   framework.  We apologize for hoarding these numbers to ourselves, but
    *   we were already using them long before we decided to release Protocol
    *   Buffers.
    * 
    * Is this method deprecated?
    * Depending on the target platform, this can emit Deprecated annotations
    * for the method, or it will be completely ignored; in the very least,
    * this is a formalization for deprecating methods.
    */
    public val deprecated: Boolean
    public val idempotencyLevel: IdempotencyLevel
    /**
    * Any features defined in the specific edition.
    * WARNING: This field should only be used by protobuf plugins or special
    * cases like the proto compiler. Other uses are discouraged and
    * developers should rely on the protoreflect APIs for their client language.
    */
    public val features: FeatureSet
    /**
    * The parser stores options it doesn't recognize here. See above.
    */
    public val uninterpretedOption: List<UninterpretedOption>

    /**
    * Is this method side-effect-free (or safe in HTTP parlance), or idempotent,
    * or neither? HTTP based RPC implementation may choose GET verb for safe
    * methods, and PUT verb for idempotent methods instead of the default POST.
    */
    public sealed class IdempotencyLevel(public open val number: Int) {
        public data object IDEMPOTENCY_UNKNOWN: IdempotencyLevel(number = 0)

        /**
        * implies idempotent
        */
        public data object NO_SIDE_EFFECTS: IdempotencyLevel(number = 1)

        /**
        * idempotent, but may have side effects
        */
        public data object IDEMPOTENT: IdempotencyLevel(number = 2)

        public data class UNRECOGNIZED(override val number: Int): IdempotencyLevel(number)

        public companion object {
            public val entries: List<IdempotencyLevel> by lazy { listOf(IDEMPOTENCY_UNKNOWN, NO_SIDE_EFFECTS, IDEMPOTENT) }
        }
    }
}

/**
* A message representing a option the parser does not recognize. This only
* appears in options protos created by the compiler::Parser class.
* DescriptorPool resolves these when building Descriptor objects. Therefore,
* options protos in descriptor objects (e.g. returned by Descriptor::options(),
* or produced by Descriptor::CopyTo()) will never have UninterpretedOptions
* in them.
*/
@GeneratedProtoMessage
public interface UninterpretedOption {
    public val name: List<NamePart>
    /**
    * The value of the uninterpreted option, in whatever type the tokenizer
    * identified it as during parsing. Exactly one of these should be set.
    */
    public val identifierValue: String?
    public val positiveIntValue: ULong?
    public val negativeIntValue: Long?
    public val doubleValue: Double?
    public val stringValue: ByteString?
    public val aggregateValue: String?

    /**
    * The name of the uninterpreted option.  Each string represents a segment in
    * a dot-separated name.  is_extension is true iff a segment represents an
    * extension (denoted with parentheses in options specs in .proto files).
    * E.g.,{ ["foo", false], ["bar.baz", true], ["moo", false] } represents
    * "foo.(bar.baz).moo".
    */
    @GeneratedProtoMessage
    public interface NamePart {
        public val namePart: String
        public val isExtension: Boolean
    }
}

/**
* ===================================================================
* Features
* 
* TODO Enums in C++ gencode (and potentially other languages) are
* not well scoped.  This means that each of the feature enums below can clash
* with each other.  The short names we've chosen maximize call-site
* readability, but leave us very open to this scenario.  A future feature will
* be designed and implemented to handle this, hopefully before we ever hit a
* conflict here.
*/
@GeneratedProtoMessage
public interface FeatureSet {
    public val fieldPresence: FieldPresence?
    public val enumType: EnumType?
    public val repeatedFieldEncoding: RepeatedFieldEncoding?
    public val utf8Validation: Utf8Validation?
    public val messageEncoding: MessageEncoding?
    public val jsonFormat: JsonFormat?
    public val enforceNamingStyle: EnforceNamingStyle?
    public val defaultSymbolVisibility: VisibilityFeature.DefaultSymbolVisibility?

    @GeneratedProtoMessage
    public interface VisibilityFeature {
        public sealed class DefaultSymbolVisibility(public open val number: Int) {
            public data object DEFAULT_SYMBOL_VISIBILITY_UNKNOWN: DefaultSymbolVisibility(number = 0)

            /**
            * Default pre-EDITION_2024, all UNSET visibility are export.
            */
            public data object EXPORT_ALL: DefaultSymbolVisibility(number = 1)

            /**
            * All top-level symbols default to export, nested default to local.
            */
            public data object EXPORT_TOP_LEVEL: DefaultSymbolVisibility(number = 2)

            /**
            * All symbols default to local.
            */
            public data object LOCAL_ALL: DefaultSymbolVisibility(number = 3)

            /**
            * All symbols local by default. Nested types cannot be exported.
            * With special case caveat for message { enum {} reserved 1 to max; }
            * This is the recommended setting for new protos.
            */
            public data object STRICT: DefaultSymbolVisibility(number = 4)

            public data class UNRECOGNIZED(override val number: Int): DefaultSymbolVisibility(number)

            public companion object {
                public val entries: List<DefaultSymbolVisibility> by lazy { listOf(DEFAULT_SYMBOL_VISIBILITY_UNKNOWN, EXPORT_ALL, EXPORT_TOP_LEVEL, LOCAL_ALL, STRICT) }
            }
        }
    }

    public sealed class FieldPresence(public open val number: Int) {
        public data object FIELD_PRESENCE_UNKNOWN: FieldPresence(number = 0)

        public data object EXPLICIT: FieldPresence(number = 1)

        public data object IMPLICIT: FieldPresence(number = 2)

        public data object LEGACY_REQUIRED: FieldPresence(number = 3)

        public data class UNRECOGNIZED(override val number: Int): FieldPresence(number)

        public companion object {
            public val entries: List<FieldPresence> by lazy { listOf(FIELD_PRESENCE_UNKNOWN, EXPLICIT, IMPLICIT, LEGACY_REQUIRED) }
        }
    }

    public sealed class EnumType(public open val number: Int) {
        public data object ENUM_TYPE_UNKNOWN: EnumType(number = 0)

        public data object OPEN: EnumType(number = 1)

        public data object CLOSED: EnumType(number = 2)

        public data class UNRECOGNIZED(override val number: Int): EnumType(number)

        public companion object {
            public val entries: List<EnumType> by lazy { listOf(ENUM_TYPE_UNKNOWN, OPEN, CLOSED) }
        }
    }

    public sealed class RepeatedFieldEncoding(public open val number: Int) {
        public data object REPEATED_FIELD_ENCODING_UNKNOWN: RepeatedFieldEncoding(number = 0)

        public data object PACKED: RepeatedFieldEncoding(number = 1)

        public data object EXPANDED: RepeatedFieldEncoding(number = 2)

        public data class UNRECOGNIZED(override val number: Int): RepeatedFieldEncoding(number)

        public companion object {
            public val entries: List<RepeatedFieldEncoding> by lazy { listOf(REPEATED_FIELD_ENCODING_UNKNOWN, PACKED, EXPANDED) }
        }
    }

    public sealed class Utf8Validation(public open val number: Int) {
        public data object UTF8_VALIDATION_UNKNOWN: Utf8Validation(number = 0)

        public data object VERIFY: Utf8Validation(number = 2)

        public data object NONE: Utf8Validation(number = 3)

        public data class UNRECOGNIZED(override val number: Int): Utf8Validation(number)

        public companion object {
            public val entries: List<Utf8Validation> by lazy { listOf(UTF8_VALIDATION_UNKNOWN, VERIFY, NONE) }
        }
    }

    public sealed class MessageEncoding(public open val number: Int) {
        public data object MESSAGE_ENCODING_UNKNOWN: MessageEncoding(number = 0)

        public data object LENGTH_PREFIXED: MessageEncoding(number = 1)

        public data object DELIMITED: MessageEncoding(number = 2)

        public data class UNRECOGNIZED(override val number: Int): MessageEncoding(number)

        public companion object {
            public val entries: List<MessageEncoding> by lazy { listOf(MESSAGE_ENCODING_UNKNOWN, LENGTH_PREFIXED, DELIMITED) }
        }
    }

    public sealed class JsonFormat(public open val number: Int) {
        public data object JSON_FORMAT_UNKNOWN: JsonFormat(number = 0)

        public data object ALLOW: JsonFormat(number = 1)

        public data object LEGACY_BEST_EFFORT: JsonFormat(number = 2)

        public data class UNRECOGNIZED(override val number: Int): JsonFormat(number)

        public companion object {
            public val entries: List<JsonFormat> by lazy { listOf(JSON_FORMAT_UNKNOWN, ALLOW, LEGACY_BEST_EFFORT) }
        }
    }

    public sealed class EnforceNamingStyle(public open val number: Int) {
        public data object ENFORCE_NAMING_STYLE_UNKNOWN: EnforceNamingStyle(number = 0)

        public data object STYLE2024: EnforceNamingStyle(number = 1)

        public data object STYLE_LEGACY: EnforceNamingStyle(number = 2)

        public data class UNRECOGNIZED(override val number: Int): EnforceNamingStyle(number)

        public companion object {
            public val entries: List<EnforceNamingStyle> by lazy { listOf(ENFORCE_NAMING_STYLE_UNKNOWN, STYLE2024, STYLE_LEGACY) }
        }
    }
}

/**
* A compiled specification for the defaults of a set of features.  These
* messages are generated from FeatureSet extensions and can be used to seed
* feature resolution. The resolution with this object becomes a simple search
* for the closest matching edition, followed by proto merges.
*/
@GeneratedProtoMessage
public interface FeatureSetDefaults {
    public val defaults: List<FeatureSetEditionDefault>
    /**
    * The minimum supported edition (inclusive) when this was constructed.
    * Editions before this will not have defaults.
    */
    public val minimumEdition: Edition?
    /**
    * The maximum known edition (inclusive) when this was constructed. Editions
    * after this will not have reliable defaults.
    */
    public val maximumEdition: Edition?

    /**
    * A map from every known edition with a unique set of defaults to its
    * defaults. Not all editions may be contained here.  For a given edition,
    * the defaults at the closest matching edition ordered at or before it should
    * be used.  This field must be in strict ascending order by edition.
    */
    @GeneratedProtoMessage
    public interface FeatureSetEditionDefault {
        public val edition: Edition?
        /**
        * Defaults of features that can be overridden in this edition.
        */
        public val overridableFeatures: FeatureSet
        /**
        * Defaults of features that can't be overridden in this edition.
        */
        public val fixedFeatures: FeatureSet
    }
}

/**
* ===================================================================
* Optional source code info
* 
* Encapsulates information about the original source file from which a
* FileDescriptorProto was generated.
*/
@GeneratedProtoMessage
public interface SourceCodeInfo {
    /**
    * A Location identifies a piece of source code in a .proto file which
    * corresponds to a particular definition.  This information is intended
    * to be useful to IDEs, code indexers, documentation generators, and similar
    * tools.
    * 
    * For example, say we have a file like:
    *   message Foo {
    *     optional string foo = 1;
    *   }
    * Let's look at just the field definition:
    *   optional string foo = 1;
    *   ^       ^^     ^^  ^  ^^^
    *   a       bc     de  f  ghi
    * We have the following locations:
    *   span   path               represents
    *   [a,i)  [ 4, 0, 2, 0 ]     The whole field definition.
    *   [a,b)  [ 4, 0, 2, 0, 4 ]  The label (optional).
    *   [c,d)  [ 4, 0, 2, 0, 5 ]  The type (string).
    *   [e,f)  [ 4, 0, 2, 0, 1 ]  The name (foo).
    *   [g,h)  [ 4, 0, 2, 0, 3 ]  The number (1).
    * 
    * Notes:
    * - A location may refer to a repeated field itself (i.e. not to any
    *   particular index within it).  This is used whenever a set of elements are
    *   logically enclosed in a single code segment.  For example, an entire
    *   extend block (possibly containing multiple extension definitions) will
    *   have an outer location whose path refers to the "extensions" repeated
    *   field without an index.
    * - Multiple locations may have the same path.  This happens when a single
    *   logical declaration is spread out across multiple places.  The most
    *   obvious example is the "extend" block again -- there may be multiple
    *   extend blocks in the same scope, each of which will have the same path.
    * - A location's span is not always a subset of its parent's span.  For
    *   example, the "extendee" of an extension declaration appears at the
    *   beginning of the "extend" block and is shared by all extensions within
    *   the block.
    * - Just because a location's span is a subset of some other location's span
    *   does not mean that it is a descendant.  For example, a "group" defines
    *   both a type and a field in a single declaration.  Thus, the locations
    *   corresponding to the type and field and their components will overlap.
    * - Code which tries to interpret locations should probably be designed to
    *   ignore those that it doesn't understand, as more types of locations could
    *   be recorded in the future.
    */
    public val location: List<Location>

    @GeneratedProtoMessage
    public interface Location {
        /**
        * Identifies which part of the FileDescriptorProto was defined at this
        * location.
        * 
        * Each element is a field number or an index.  They form a path from
        * the root FileDescriptorProto to the place where the definition appears.
        * For example, this path:
        *   [ 4, 3, 2, 7, 1 ]
        * refers to:
        *   file.message_type(3)  // 4, 3
        *       .field(7)         // 2, 7
        *       .name()           // 1
        * This is because FileDescriptorProto.message_type has field number 4:
        *   repeated DescriptorProto message_type = 4;
        * and DescriptorProto.field has field number 2:
        *   repeated FieldDescriptorProto field = 2;
        * and FieldDescriptorProto.name has field number 1:
        *   optional string name = 1;
        * 
        * Thus, the above path gives the location of a field name.  If we removed
        * the last element:
        *   [ 4, 3, 2, 7 ]
        * this path refers to the whole field declaration (from the beginning
        * of the label to the terminating semicolon).
        */
        public val path: List<Int>
        /**
        * Always has exactly three or four elements: start line, start column,
        * end line (optional, otherwise assumed same as start line), end column.
        * These are packed into a single field for efficiency.  Note that line
        * and column numbers are zero-based -- typically you will want to add
        * 1 to each before displaying to a user.
        */
        public val span: List<Int>
        /**
        * If this SourceCodeInfo represents a complete declaration, these are any
        * comments appearing before and after the declaration which appear to be
        * attached to the declaration.
        * 
        * A series of line comments appearing on consecutive lines, with no other
        * tokens appearing on those lines, will be treated as a single comment.
        * 
        * leading_detached_comments will keep paragraphs of comments that appear
        * before (but not connected to) the current element. Each paragraph,
        * separated by empty lines, will be one comment element in the repeated
        * field.
        * 
        * Only the comment content is provided; comment markers (e.g. //) are
        * stripped out.  For block comments, leading whitespace and an asterisk
        * will be stripped from the beginning of each line other than the first.
        * Newlines are included in the output.
        * 
        * Examples:
        * 
        *   optional int32 foo = 1;  // Comment attached to foo.
        *   // Comment attached to bar.
        *   optional int32 bar = 2;
        * 
        *   optional string baz = 3;
        *   // Comment attached to baz.
        *   // Another line attached to baz.
        * 
        *   // Comment attached to moo.
        *   //
        *   // Another line attached to moo.
        *   optional double moo = 4;
        * 
        *   // Detached comment for corge. This is not leading or trailing comments
        *   // to moo or corge because there are blank lines separating it from
        *   // both.
        * 
        *   // Detached comment for corge paragraph 2.
        * 
        *   optional string corge = 5;
        *   /&#42; Block comment attached
        *    * to corge.  Leading asterisks
        *    * will be removed. &#42;/
        *   /&#42; Block comment attached to
        *    * grault. &#42;/
        *   optional int32 grault = 6;
        * 
        *   // ignored detached comments.
        */
        public val leadingComments: String?
        public val trailingComments: String?
        public val leadingDetachedComments: List<String>
    }
}

/**
* Describes the relationship between generated code and its original source
* file. A GeneratedCodeInfo message is associated with only one generated
* source file, but may contain references to different source .proto files.
*/
@GeneratedProtoMessage
public interface GeneratedCodeInfo {
    /**
    * An Annotation connects some span of text in generated code to an element
    * of its generating .proto file.
    */
    public val annotation: List<Annotation>

    @GeneratedProtoMessage
    public interface Annotation {
        /**
        * Identifies the element in the original source .proto file. This field
        * is formatted the same as SourceCodeInfo.Location.path.
        */
        public val path: List<Int>
        /**
        * Identifies the filesystem path to the original source .proto.
        */
        public val sourceFile: String?
        /**
        * Identifies the starting offset in bytes in the generated code
        * that relates to the identified object.
        */
        public val begin: Int?
        /**
        * Identifies the ending offset in bytes in the generated code that
        * relates to the identified object. The end offset should be one past
        * the last relevant byte (so the length of the text = end - begin).
        */
        public val end: Int?
        public val semantic: Semantic?

        /**
        * Represents the identified object's effect on the element in the original
        * .proto file.
        */
        public sealed class Semantic(public open val number: Int) {
            /**
            * There is no effect or the effect is indescribable.
            */
            public data object NONE: Semantic(number = 0)

            /**
            * The element is set or otherwise mutated.
            */
            public data object SET: Semantic(number = 1)

            /**
            * An alias to the element is returned.
            */
            public data object ALIAS: Semantic(number = 2)

            public data class UNRECOGNIZED(override val number: Int): Semantic(number)

            public companion object {
                public val entries: List<Semantic> by lazy { listOf(NONE, SET, ALIAS) }
            }
        }
    }
}

/**
* The full set of known editions.
*/
public sealed class Edition(public open val number: Int) {
    /**
    * A placeholder for an unknown edition value.
    */
    public data object EDITION_UNKNOWN: Edition(number = 0)

    /**
    * A placeholder edition for specifying default behaviors *before* a feature
    * was first introduced.  This is effectively an "infinite past".
    */
    public data object EDITION_LEGACY: Edition(number = 900)

    /**
    * Legacy syntax "editions".  These pre-date editions, but behave much like
    * distinct editions.  These can't be used to specify the edition of proto
    * files, but feature definitions must supply proto2/proto3 defaults for
    * backwards compatibility.
    */
    public data object EDITION_PROTO2: Edition(number = 998)

    public data object EDITION_PROTO3: Edition(number = 999)

    /**
    * Editions that have been released.  The specific values are arbitrary and
    * should not be depended on, but they will always be time-ordered for easy
    * comparison.
    */
    public data object EDITION_2023: Edition(number = 1000)

    public data object EDITION_2024: Edition(number = 1001)

    /**
    * Placeholder editions for testing feature resolution.  These should not be
    * used or relied on outside of tests.
    */
    public data object EDITION_1_TEST_ONLY: Edition(number = 1)

    public data object EDITION_2_TEST_ONLY: Edition(number = 2)

    public data object EDITION_99997_TEST_ONLY: Edition(number = 99997)

    public data object EDITION_99998_TEST_ONLY: Edition(number = 99998)

    public data object EDITION_99999_TEST_ONLY: Edition(number = 99999)

    /**
    * Placeholder for specifying unbounded edition support.  This should only
    * ever be used by plugins that can expect to never require any changes to
    * support a new edition.
    */
    public data object EDITION_MAX: Edition(number = 2147483647)

    public data class UNRECOGNIZED(override val number: Int): Edition(number)

    public companion object {
        public val entries: List<Edition> by lazy { listOf(EDITION_UNKNOWN, EDITION_1_TEST_ONLY, EDITION_2_TEST_ONLY, EDITION_LEGACY, EDITION_PROTO2, EDITION_PROTO3, EDITION_2023, EDITION_2024, EDITION_99997_TEST_ONLY, EDITION_99998_TEST_ONLY, EDITION_99999_TEST_ONLY, EDITION_MAX) }
    }
}

/**
* Describes the 'visibility' of a symbol with respect to the proto import
* system. Symbols can only be imported when the visibility rules do not prevent
* it (ex: local symbols cannot be imported).  Visibility modifiers can only set
* on `message` and `enum` as they are the only types available to be referenced
* from other files.
*/
public sealed class SymbolVisibility(public open val number: Int) {
    public data object VISIBILITY_UNSET: SymbolVisibility(number = 0)

    public data object VISIBILITY_LOCAL: SymbolVisibility(number = 1)

    public data object VISIBILITY_EXPORT: SymbolVisibility(number = 2)

    public data class UNRECOGNIZED(override val number: Int): SymbolVisibility(number)

    public companion object {
        public val entries: List<SymbolVisibility> by lazy { listOf(VISIBILITY_UNSET, VISIBILITY_LOCAL, VISIBILITY_EXPORT) }
    }
}
