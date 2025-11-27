@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*

/**
* Api is a light-weight descriptor for an API Interface.
* 
* Interfaces are also described as "protocol buffer services" in some contexts,
* such as by the "service" keyword in a .proto file, but they are different
* from API Services, which represent a concrete implementation of an interface
* as opposed to simply a description of methods and bindings. They are also
* sometimes simply referred to as "APIs" in other contexts, such as the name of
* this message itself. See https://cloud.google.com/apis/design/glossary for
* detailed terminology.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.ApiInternal.CODEC::class)
public interface Api { 
    /**
    * The fully qualified name of this interface, including package name
    * followed by the interface's simple name.
    */
    public val name: String
    /**
    * The methods of this interface, in unspecified order.
    */
    public val methods: List<com.google.protobuf.kotlin.Method>
    /**
    * Any metadata attached to the interface.
    */
    public val options: List<com.google.protobuf.kotlin.Option>
    /**
    * A version string for this interface. If specified, must have the form
    * `major-version.minor-version`, as in `1.10`. If the minor version is
    * omitted, it defaults to zero. If the entire version field is empty, the
    * major version is derived from the package name, as outlined below. If the
    * field is not empty, the version in the package name will be verified to be
    * consistent with what is provided here.
    * 
    * The versioning schema uses [semantic
    * versioning](http://semver.org) where the major version number
    * indicates a breaking change and the minor version an additive,
    * non-breaking change. Both version numbers are signals to users
    * what to expect from different versions, and should be carefully
    * chosen based on the product plan.
    * 
    * The major version is also reflected in the package name of the
    * interface, which must end in `v<major-version>`, as in
    * `google.feature.v1`. For major versions 0 and 1, the suffix can
    * be omitted. Zero major versions must only be used for
    * experimental, non-GA interfaces.
    */
    public val version: String
    /**
    * Source context for the protocol buffer service represented by this
    * message.
    */
    public val sourceContext: com.google.protobuf.kotlin.SourceContext
    /**
    * Included interfaces. See [Mixin][].
    */
    public val mixins: List<com.google.protobuf.kotlin.Mixin>
    /**
    * The source syntax of the service.
    */
    public val syntax: com.google.protobuf.kotlin.Syntax

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.ApiInternal.() -> Unit = {}): com.google.protobuf.kotlin.Api

    public companion object
}

/**
* Method represents a method of an API interface.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.MethodInternal.CODEC::class)
public interface Method { 
    /**
    * The simple name of this method.
    */
    public val name: String
    /**
    * A URL of the input message type.
    */
    public val requestTypeUrl: String
    /**
    * If true, the request is streamed.
    */
    public val requestStreaming: Boolean
    /**
    * The URL of the output message type.
    */
    public val responseTypeUrl: String
    /**
    * If true, the response is streamed.
    */
    public val responseStreaming: Boolean
    /**
    * Any metadata attached to the method.
    */
    public val options: List<com.google.protobuf.kotlin.Option>
    /**
    * The source syntax of this method.
    */
    public val syntax: com.google.protobuf.kotlin.Syntax

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.MethodInternal.() -> Unit = {}): com.google.protobuf.kotlin.Method

    public companion object
}

/**
* Declares an API Interface to be included in this interface. The including
* interface must redeclare all the methods from the included interface, but
* documentation and options are inherited as follows:
* 
* - If after comment and whitespace stripping, the documentation
*   string of the redeclared method is empty, it will be inherited
*   from the original method.
* 
* - Each annotation belonging to the service config (http,
*   visibility) which is not set in the redeclared method will be
*   inherited.
* 
* - If an http annotation is inherited, the path pattern will be
*   modified as follows. Any version prefix will be replaced by the
*   version of the including interface plus the [root][] path if
*   specified.
* 
* Example of a simple mixin:
* 
*     package google.acl.v1;
*     service AccessControl {
*       // Get the underlying ACL object.
*       rpc GetAcl(GetAclRequest) returns (Acl) {
*         option (google.api.http).get = "/v1/{resource=**}:getAcl";
*       }
*     }
* 
*     package google.storage.v2;
*     service Storage {
*       rpc GetAcl(GetAclRequest) returns (Acl);
* 
*       // Get a data record.
*       rpc GetData(GetDataRequest) returns (Data) {
*         option (google.api.http).get = "/v2/{resource=**}";
*       }
*     }
* 
* Example of a mixin configuration:
* 
*     apis:
*     - name: google.storage.v2.Storage
*       mixins:
*       - name: google.acl.v1.AccessControl
* 
* The mixin construct implies that all methods in `AccessControl` are
* also declared with same name and request/response types in
* `Storage`. A documentation generator or annotation processor will
* see the effective `Storage.GetAcl` method after inheriting
* documentation and annotations as follows:
* 
*     service Storage {
*       // Get the underlying ACL object.
*       rpc GetAcl(GetAclRequest) returns (Acl) {
*         option (google.api.http).get = "/v2/{resource=**}:getAcl";
*       }
*       ...
*     }
* 
* Note how the version in the path pattern changed from `v1` to `v2`.
* 
* If the `root` field in the mixin is specified, it should be a
* relative path under which inherited HTTP paths are placed. Example:
* 
*     apis:
*     - name: google.storage.v2.Storage
*       mixins:
*       - name: google.acl.v1.AccessControl
*         root: acls
* 
* This implies the following inherited HTTP annotation:
* 
*     service Storage {
*       // Get the underlying ACL object.
*       rpc GetAcl(GetAclRequest) returns (Acl) {
*         option (google.api.http).get = "/v2/acls/{resource=**}:getAcl";
*       }
*       ...
*     }
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.kotlin.MixinInternal.CODEC::class)
public interface Mixin { 
    /**
    * The fully qualified name of the interface which is included.
    */
    public val name: String
    /**
    * If non-empty specifies a path under which inherited HTTP paths
    * are rooted.
    */
    public val root: String

    /**
    * Copies the original message, including unknown fields.
    */
    public fun copy(body: com.google.protobuf.kotlin.MixinInternal.() -> Unit = {}): com.google.protobuf.kotlin.Mixin

    public companion object
}
