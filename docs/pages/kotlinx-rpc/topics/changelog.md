# Changelog

This page contains all changes throughout releases of the library.

## 0.11.0-grpc-185
> Published 12 March 2026

**Full Changelog**: [0.10.2...0.11.0-grpc-185](https://github.com/Kotlin/kotlinx-rpc/compare/0.10.2...0.11.0-grpc-185)

> **This is a development preview release.** APIs are subject to change and may break without notice in future versions.

### Overview {id=Overview_0_11_0-grpc-185}

This release introduces **gRPC and Protocol Buffers support** for kotlinx-rpc — a Kotlin Multiplatform
implementation of gRPC with native protobuf serialization.

Two approaches are supported:
- **Schema-first** — define services and messages in `.proto` files, and the Gradle plugin generates
  idiomatic Kotlin code with `suspend` functions, `Flow`-based streaming, and type-safe message builders.
  See the [gRPC configuration](https://kotlin.github.io/kotlinx-rpc/grpc-configuration.html) and
  [generated code](https://kotlin.github.io/kotlinx-rpc/grpc-generated-code.html) docs.
- **Proto-less** — write gRPC service interfaces directly in Kotlin with any serialization format
  (`kotlinx.serialization`, custom `GrpcMarshaller`, etc.), no `.proto` files needed.
  See the [gRPC services](https://kotlin.github.io/kotlinx-rpc/grpc-services.html) docs.

**Supported gRPC workflows:**
- Unary RPC (request-response)
- Server streaming (server returns `Flow`)
- Client streaming (client sends `Flow`)
- Bidirectional streaming (both sides use `Flow`)
- Client and server interceptors
- TLS encryption and call credentials
- Call metadata (headers and trailers)
- Timeout, gzip compression, and keepalive support
- Ktor server integration
- Status exception handling
- proto-less gRPC services

**Supported protobuf features:**
- Proto2, proto3, and editions 2023 syntax
- All scalar types, enums, repeated fields, maps, oneof, nested messages
- Optional types and field presence tracking
- Extensions and groups (deprecated proto2 feature)
- Well-Known Types (bundled with the runtime)
- Unknown field propagation
- Full protobuf conformance test compliance
- Custom serialization via `kotlinx.serialization` or custom `GrpcMarshaller`

**Supported platforms:** JVM, Android, iOS, macOS, Linux.

#### Features 🎉 {id=Features_0_11_0-grpc-185}
* gRPC initial implementation by [@Mr3zee](https://github.com/Mr3zee) in [#262](https://github.com/Kotlin/kotlinx-rpc/pull/262)
* Simplified gRPC Gradle configuration by [@Mr3zee](https://github.com/Mr3zee) in [#281](https://github.com/Kotlin/kotlinx-rpc/pull/281)
* Internal vs public codegen separation in gRPC (KRPC-162) by [@Mr3zee](https://github.com/Mr3zee) in [#285](https://github.com/Kotlin/kotlinx-rpc/pull/285)
* Reference types by [@Mr3zee](https://github.com/Mr3zee) in [#291](https://github.com/Kotlin/kotlinx-rpc/pull/291)
* Enum support by [@Mr3zee](https://github.com/Mr3zee) in [#294](https://github.com/Kotlin/kotlinx-rpc/pull/294)
* Optional types support (KRPC-142) by [@Mr3zee](https://github.com/Mr3zee) in [#295](https://github.com/Kotlin/kotlinx-rpc/pull/295)
* Repeated types (KRPC-143) by [@Mr3zee](https://github.com/Mr3zee) in [#328](https://github.com/Kotlin/kotlinx-rpc/pull/328)
* Nested types (KRPC-146) by [@Mr3zee](https://github.com/Mr3zee) in [#331](https://github.com/Kotlin/kotlinx-rpc/pull/331)
* OneOf types (KRPC-147) by [@Mr3zee](https://github.com/Mr3zee) in [#332](https://github.com/Kotlin/kotlinx-rpc/pull/332)
* Map types (KRPC-145) by [@Mr3zee](https://github.com/Mr3zee) in [#334](https://github.com/Kotlin/kotlinx-rpc/pull/334)
* Streaming support (unary, server, client, bidirectional) by [@Mr3zee](https://github.com/Mr3zee) in [#389](https://github.com/Kotlin/kotlinx-rpc/pull/389)
* gRPC Ktor server plugin by [@Mr3zee](https://github.com/Mr3zee) in [#393](https://github.com/Kotlin/kotlinx-rpc/pull/393)
* Native initial setup with C++/C/Kotlin interface by [@Jozott00](https://github.com/Jozott00) in [#400](https://github.com/Kotlin/kotlinx-rpc/pull/400)
* Wire format encoder and decoder by [@Jozott00](https://github.com/Jozott00) in [#412](https://github.com/Kotlin/kotlinx-rpc/pull/412)
* Proto generation for common (JVM and Native) by [@Jozott00](https://github.com/Jozott00) in [#415](https://github.com/Kotlin/kotlinx-rpc/pull/415)
* Common APIs for gRPC Services, Client, and Server by [@Mr3zee](https://github.com/Mr3zee) in [#419](https://github.com/Kotlin/kotlinx-rpc/pull/419)
* Field presence tracking, required field enforcing, and MessageCodec generation by [@Jozott00](https://github.com/Jozott00) in [#421](https://github.com/Kotlin/kotlinx-rpc/pull/421)
* Enum and oneof field generation by [@Jozott00](https://github.com/Jozott00) in [#426](https://github.com/Kotlin/kotlinx-rpc/pull/426)
* gRPC service generation in the compiler plugin by [@Mr3zee](https://github.com/Mr3zee) in [#428](https://github.com/Kotlin/kotlinx-rpc/pull/428)
* Sub-messages (recursive, nested, repeated) by [@Jozott00](https://github.com/Jozott00) in [#430](https://github.com/Kotlin/kotlinx-rpc/pull/430)
* Map support in protobuf codec by [@Jozott00](https://github.com/Jozott00) in [#434](https://github.com/Kotlin/kotlinx-rpc/pull/434)
* Exceptions and skip unknown fields by [@Jozott00](https://github.com/Jozott00) in [#437](https://github.com/Kotlin/kotlinx-rpc/pull/437)
* Native client calls support by [@Jozott00](https://github.com/Jozott00) in [#452](https://github.com/Kotlin/kotlinx-rpc/pull/452)
* iOS support (arm64, x64, simulatorArm64) by [@Jozott00](https://github.com/Jozott00) in [#458](https://github.com/Kotlin/kotlinx-rpc/pull/458)
* Linux support (arm64, x64) by [@Jozott00](https://github.com/Jozott00) in [#466](https://github.com/Kotlin/kotlinx-rpc/pull/466)
* Native server implementation by [@Jozott00](https://github.com/Jozott00) in [#469](https://github.com/Kotlin/kotlinx-rpc/pull/469)
* TLS credentials support (client and server) by [@Jozott00](https://github.com/Jozott00) in [#471](https://github.com/Kotlin/kotlinx-rpc/pull/471)
* Deprecated groups support by [@Jozott00](https://github.com/Jozott00) in [#472](https://github.com/Kotlin/kotlinx-rpc/pull/472)
* Enable all conformance tests by [@Jozott00](https://github.com/Jozott00) in [#473](https://github.com/Kotlin/kotlinx-rpc/pull/473)
* Client and server interceptor API by [@Jozott00](https://github.com/Jozott00) in [#484](https://github.com/Kotlin/kotlinx-rpc/pull/484)
* `Grpc.Method` annotation by [@Mr3zee](https://github.com/Mr3zee) in [#487](https://github.com/Kotlin/kotlinx-rpc/pull/487)
* Comments support in generated code by [@Mr3zee](https://github.com/Mr3zee) in [#488](https://github.com/Kotlin/kotlinx-rpc/pull/488)
* Deprecated options support by [@Mr3zee](https://github.com/Mr3zee) in [#489](https://github.com/Kotlin/kotlinx-rpc/pull/489)
* `hashCode`, `equals` and `toString` for generated messages by [@Mr3zee](https://github.com/Mr3zee) in [#490](https://github.com/Kotlin/kotlinx-rpc/pull/490)
* Call metadata — headers and trailers by [@Jozott00](https://github.com/Jozott00) in [#517](https://github.com/Kotlin/kotlinx-rpc/pull/517)
* Timeout support by [@Jozott00](https://github.com/Jozott00) in [#525](https://github.com/Kotlin/kotlinx-rpc/pull/525)
* gzip compression support by [@Jozott00](https://github.com/Jozott00) in [#527](https://github.com/Kotlin/kotlinx-rpc/pull/527)
* Keepalive support by [@Jozott00](https://github.com/Jozott00) in [#528](https://github.com/Kotlin/kotlinx-rpc/pull/528)
* Call credentials by [@Jozott00](https://github.com/Jozott00) in [#530](https://github.com/Kotlin/kotlinx-rpc/pull/530)
* Add missing Apple targets by [@Jozott00](https://github.com/Jozott00) in [#533](https://github.com/Kotlin/kotlinx-rpc/pull/533)
* Full KMP support for the Gradle Plugin by [@Mr3zee](https://github.com/Mr3zee) in [#534](https://github.com/Kotlin/kotlinx-rpc/pull/534)
* Status exception API refactoring by [@Jozott00](https://github.com/Jozott00) in [#536](https://github.com/Kotlin/kotlinx-rpc/pull/536)
* `copy` method for generated messages by [@Jozott00](https://github.com/Jozott00) in [#539](https://github.com/Kotlin/kotlinx-rpc/pull/539)
* Presence check support by [@Jozott00](https://github.com/Jozott00) in [#540](https://github.com/Kotlin/kotlinx-rpc/pull/540)
* Android source sets support by [@Mr3zee](https://github.com/Mr3zee) in [#542](https://github.com/Kotlin/kotlinx-rpc/pull/542)
* Unknown field propagation by [@Jozott00](https://github.com/Jozott00) in [#557](https://github.com/Kotlin/kotlinx-rpc/pull/557)
* Add `protoServiceName` to `[@Grpc](https://github.com/Grpc)` annotation by [@Jozott00](https://github.com/Jozott00) in [#563](https://github.com/Kotlin/kotlinx-rpc/pull/563)
* Add `CodecConfig` and `codec` function by [@Jozott00](https://github.com/Jozott00) in [#565](https://github.com/Kotlin/kotlinx-rpc/pull/565)
* `ProtobufConfig` with `discardUnknownFields` option by [@Jozott00](https://github.com/Jozott00) in [#569](https://github.com/Kotlin/kotlinx-rpc/pull/569)
* `[@GeneratedProtoMessage](https://github.com/GeneratedProtoMessage)` annotation and descriptor object by [@Jozott00](https://github.com/Jozott00) in [#572](https://github.com/Kotlin/kotlinx-rpc/pull/572)
* Shortest unambiguous names in generated code by [@Mr3zee](https://github.com/Mr3zee) in [#573](https://github.com/Kotlin/kotlinx-rpc/pull/573) and [#575](https://github.com/Kotlin/kotlinx-rpc/pull/575)
* Platform options for the Gradle plugin by [@Mr3zee](https://github.com/Mr3zee) in [#574](https://github.com/Kotlin/kotlinx-rpc/pull/574)
* Well-known `Any` extension functions by [@Jozott00](https://github.com/Jozott00) in [#577](https://github.com/Kotlin/kotlinx-rpc/pull/577)
* Companion object generation moved to the compiler plugin by [@Mr3zee](https://github.com/Mr3zee) in [#586](https://github.com/Kotlin/kotlinx-rpc/pull/586)
* Extensions support by [@Jozott00](https://github.com/Jozott00) in [#587](https://github.com/Kotlin/kotlinx-rpc/pull/587)
* MARSHALLER and DESCRIPTOR objects for group messages by [@Jozott00](https://github.com/Jozott00) in [#589](https://github.com/Kotlin/kotlinx-rpc/pull/589)
* Message recursion limit (KRPC-192) by [@Mr3zee](https://github.com/Mr3zee) in [#594](https://github.com/Kotlin/kotlinx-rpc/pull/594)
* `descriptor.proto` generation by [@Mr3zee](https://github.com/Mr3zee) in [#600](https://github.com/Kotlin/kotlinx-rpc/pull/600)

#### Bug fixes 🐛 {id=Bug_fixes_0_11_0-grpc-185}
* Fix JPMS for gRPC by [@Mr3zee](https://github.com/Mr3zee) in [#390](https://github.com/Kotlin/kotlinx-rpc/pull/390)
* Fix generation run for imported files by [@Mr3zee](https://github.com/Mr3zee) in [#409](https://github.com/Kotlin/kotlinx-rpc/pull/409)
* Tasks caching by [@Mr3zee](https://github.com/Mr3zee) in [#410](https://github.com/Kotlin/kotlinx-rpc/pull/410)
* Protobuf conformance fixes by [@Mr3zee](https://github.com/Mr3zee) in [#454](https://github.com/Kotlin/kotlinx-rpc/pull/454)
* Fix service names in the generated descriptor by [@Jozott00](https://github.com/Jozott00) in [#457](https://github.com/Kotlin/kotlinx-rpc/pull/457)
* Fix `onReady()` listener delegation — native server streaming fix (KRPC-220) by [@Jozott00](https://github.com/Jozott00) in [#477](https://github.com/Kotlin/kotlinx-rpc/pull/477)
* Fix 32-bit target APIs by [@Jozott00](https://github.com/Jozott00) in [#537](https://github.com/Kotlin/kotlinx-rpc/pull/537)
* Fix retry behavior for client RPC calls by [@Jozott00](https://github.com/Jozott00) in [#545](https://github.com/Kotlin/kotlinx-rpc/pull/545)
* Fix protobuf size calculation for repeated fields and maps by [@fabiocarneiro](https://github.com/fabiocarneiro) in [#566](https://github.com/Kotlin/kotlinx-rpc/pull/566)
* Add `grpc-marshaller` dependency to `protobuf-core` (KRPC-219) by [@Mr3zee](https://github.com/Mr3zee) in [#595](https://github.com/Kotlin/kotlinx-rpc/pull/595)
* AGP 9.0 support by [@Mr3zee](https://github.com/Mr3zee) in [#596](https://github.com/Kotlin/kotlinx-rpc/pull/596)
* Fix unknown fields in nested messages by [@Jozott00](https://github.com/Jozott00) in [#599](https://github.com/Kotlin/kotlinx-rpc/pull/599)

#### Documentation 📗 {id=Documentation_0_11_0-grpc-185}
* KMP sample project by [@Jozott00](https://github.com/Jozott00) in [#474](https://github.com/Kotlin/kotlinx-rpc/pull/474)
* gRPC and Protobuf documentation by [@Mr3zee](https://github.com/Mr3zee) in [#598](https://github.com/Kotlin/kotlinx-rpc/pull/598) and [#604](https://github.com/Kotlin/kotlinx-rpc/pull/604)

#### Infra 🚧 {id=Infra_0_11_0-grpc-185}
* Set up protobuf conformance tests by [@Mr3zee](https://github.com/Mr3zee) in [#447](https://github.com/Kotlin/kotlinx-rpc/pull/447)
* Pre-built gRPC library to avoid long compilation times by [@Jozott00](https://github.com/Jozott00) in [#461](https://github.com/Kotlin/kotlinx-rpc/pull/461)
* Splitting core module into client and server by [@Jozott00](https://github.com/Jozott00) in [#504](https://github.com/Kotlin/kotlinx-rpc/pull/504)
* Konan LLVM bundle resolution pinning by [@Jozott00](https://github.com/Jozott00) in [#579](https://github.com/Kotlin/kotlinx-rpc/pull/579)
* Gradle 9.4 support by [@Mr3zee](https://github.com/Mr3zee) in [#597](https://github.com/Kotlin/kotlinx-rpc/pull/597)

#### Other Changes 🧹 {id=Other_Changes_0_11_0-grpc-185}
* Protoc-gen-kotlin-multiplatform as an included build by [@Mr3zee](https://github.com/Mr3zee) in [#411](https://github.com/Kotlin/kotlinx-rpc/pull/411)
* Gradle plugin tests by [@Mr3zee](https://github.com/Mr3zee) in [#413](https://github.com/Kotlin/kotlinx-rpc/pull/413)
* Fix condition for non-JVM projects by [@Mr3zee](https://github.com/Mr3zee) in [#417](https://github.com/Kotlin/kotlinx-rpc/pull/417)
* Protoc-plugin intermediate model refactoring by [@Jozott00](https://github.com/Jozott00) in [#418](https://github.com/Kotlin/kotlinx-rpc/pull/418)
* Integration tests for gRPC + Protoc generated files by [@Mr3zee](https://github.com/Mr3zee) in [#444](https://github.com/Kotlin/kotlinx-rpc/pull/444)
* Remove `InputStream` abstraction in common by [@Jozott00](https://github.com/Jozott00) in [#571](https://github.com/Kotlin/kotlinx-rpc/pull/571)
* Konan LLVM bundle mapping update by [@Jozott00](https://github.com/Jozott00) in [#585](https://github.com/Kotlin/kotlinx-rpc/pull/585)


## 0.10.2
> Published 15 February 2026

**Full Changelog**: [0.10.1...0.10.2](https://github.com/Kotlin/kotlinx-rpc/compare/0.10.1...0.10.2)

#### Features 🎉 {id=Features_0_10_2}
* Kotlin 2.3.0 and 2.3.20-Beta1 by [@Mr3zee](https://github.com/Mr3zee) in [#553](https://github.com/Kotlin/kotlinx-rpc/pull/553)

#### Bug fixes 🐛 {id=Bug_fixes_0_10_2}
* Hide compiler generated code by [@Mr3zee](https://github.com/Mr3zee) in [#555](https://github.com/Kotlin/kotlinx-rpc/pull/555)

#### Infra 🚧 {id=Infra_0_10_2}
* Fixes by [@Mr3zee](https://github.com/Mr3zee) in [#532](https://github.com/Kotlin/kotlinx-rpc/pull/532)
* Update space links by [@Mr3zee](https://github.com/Mr3zee) in [#554](https://github.com/Kotlin/kotlinx-rpc/pull/554)

#### Other Changes 🧹 {id=Other_Changes_0_10_2}
* Added KEFS xml to git by [@Mr3zee](https://github.com/Mr3zee) in [#531](https://github.com/Kotlin/kotlinx-rpc/pull/531)


## 0.10.1
> Published 27 October 2025

**Full Changelog**: [0.10.0...0.10.1](https://github.com/Kotlin/kotlinx-rpc/compare/0.10.0...0.10.1)

#### Features 🎉 {id=Features_0_10_1}
* Kotlin 2.2.21 Support by [@Mr3zee](https://github.com/Mr3zee) in [#519](https://github.com/Kotlin/kotlinx-rpc/pull/519) 
* Expose RpcServiceDescriptor.callables by [@rnett](https://github.com/rnett) in [#516](https://github.com/Kotlin/kotlinx-rpc/pull/516)
* Added watchosDeviceArm64 and watchosArm32 targets to kRPC by [@Mr3zee](https://github.com/Mr3zee) in [#513](https://github.com/Kotlin/kotlinx-rpc/pull/513)

#### Bug fixes 🐛 {id=Bug_fixes_0_10_1}
* Fix one more ClosedSendChannelException by [@Mr3zee](https://github.com/Mr3zee) in [#514](https://github.com/Kotlin/kotlinx-rpc/pull/514)
* Fix scope initialization on the kRPC Client by [@Mr3zee](https://github.com/Mr3zee) in [#505](https://github.com/Kotlin/kotlinx-rpc/pull/505)
* Fix wording for the `perCallBufferSize` docs by [@Mr3zee](https://github.com/Mr3zee) in [#500](https://github.com/Kotlin/kotlinx-rpc/pull/500)

#### Infra 🚧 {id=Infra_0_10_1}
* Added readme safeguard by [@Mr3zee](https://github.com/Mr3zee) in [#515](https://github.com/Kotlin/kotlinx-rpc/pull/515)
* Fixed kRPC compatibility tests by [@Mr3zee](https://github.com/Mr3zee) in [#518](https://github.com/Kotlin/kotlinx-rpc/pull/518)
* Update compiler tests infra by [@Mr3zee](https://github.com/Mr3zee) in [#510](https://github.com/Kotlin/kotlinx-rpc/pull/510)

### New Contributors {id=New_Contributors_0_10_1}
* [@rnett](https://github.com/rnett) made their first contribution in [#516](https://github.com/Kotlin/kotlinx-rpc/pull/516)


## 0.10.0
> Published 7 October 2025

**Full Changelog**: [0.9.1...0.10.0](https://github.com/Kotlin/kotlinx-rpc/compare/0.9.1...0.10.0)

### Overview {id=Overview_0_10_0}
This release brings a lot of changes, work:
- Kotlin 2.2.20 and 2.2.10 support
- kRPC: Backpressure

To read about the backpressure feature, 
see the updated [kRPC Configuration](https://kotlin.github.io/kotlinx-rpc/configuration.html#connector-dsl) page.

#### Breaking Changes 🔴 {id=Breaking_Changes_0_10_0}
* Allow suspend calls inside ktor rpc builder #433 by [@Mr3zee](https://github.com/Mr3zee) in [#439](https://github.com/Kotlin/kotlinx-rpc/pull/439)

#### Features 🎉 {id=Features_0_10_0}
* Kotlin 2.2.20 by [@Mr3zee](https://github.com/Mr3zee) in [#478](https://github.com/Kotlin/kotlinx-rpc/pull/478)
* Kotlin 2.2.10 by [@Mr3zee](https://github.com/Mr3zee) in [#456](https://github.com/Kotlin/kotlinx-rpc/pull/456)
* kRPC: Backpressure by [@Mr3zee](https://github.com/Mr3zee) in [#462](https://github.com/Kotlin/kotlinx-rpc/pull/462)
* Add support for Wasm/Wasi to krpc #465 by [@Mr3zee](https://github.com/Mr3zee) in [#480](https://github.com/Kotlin/kotlinx-rpc/pull/480)

#### Bug fixes 🐛 {id=Bug_fixes_0_10_0}
* Add collect once check for client streams by [@Mr3zee](https://github.com/Mr3zee) in [#431](https://github.com/Kotlin/kotlinx-rpc/pull/431)
* Fix diagnostic rendering for compiler plugins checkers by [@Mr3zee](https://github.com/Mr3zee) in [#432](https://github.com/Kotlin/kotlinx-rpc/pull/432)
* fix wrong unchecked null cast (potential NPE) by [@y9maly](https://github.com/y9maly) in [#445](https://github.com/Kotlin/kotlinx-rpc/pull/445)

#### Documentation 📗 {id=Documentation_0_10_0}
* Docs for gRPC with Ktor by [@Mr3zee](https://github.com/Mr3zee) in [#394](https://github.com/Kotlin/kotlinx-rpc/pull/394)
* Add a doc for KMP source sets with gRPC by [@Mr3zee](https://github.com/Mr3zee) in [#405](https://github.com/Kotlin/kotlinx-rpc/pull/405)
* Update strict-mode.topic by [@BierDav](https://github.com/BierDav) in [#440](https://github.com/Kotlin/kotlinx-rpc/pull/440)
* Update grpc-configuration.topic by [@flockbastian](https://github.com/flockbastian) in [#450](https://github.com/Kotlin/kotlinx-rpc/pull/450)
* Added docs for release by [@Mr3zee](https://github.com/Mr3zee) in [#482](https://github.com/Kotlin/kotlinx-rpc/pull/482)
* Fix typo in docs grpc-configuration.topic by [@sebaslogen](https://github.com/sebaslogen) in [#495](https://github.com/Kotlin/kotlinx-rpc/pull/495)

#### Infra 🚧 {id=Infra_0_10_0}
* Fix docs yaml and signing tasks by [@Mr3zee](https://github.com/Mr3zee) in [#404](https://github.com/Kotlin/kotlinx-rpc/pull/404)
* Fix jdk resolution problems on CI by [@Mr3zee](https://github.com/Mr3zee) in [#406](https://github.com/Kotlin/kotlinx-rpc/pull/406)
* Use compat-patrouille for compatibility settings by [@Mr3zee](https://github.com/Mr3zee) in [#438](https://github.com/Kotlin/kotlinx-rpc/pull/438)

#### Other Changes 🧹 {id=Other_Changes_0_10_0}
* Fix how we create 'publishMavenArtifact' tasks by [@Mr3zee](https://github.com/Mr3zee) in [#416](https://github.com/Kotlin/kotlinx-rpc/pull/416)
* Update grpc-sample app by [@Mr3zee](https://github.com/Mr3zee) in [#425](https://github.com/Kotlin/kotlinx-rpc/pull/425)
* Fix LV and signing by [@Mr3zee](https://github.com/Mr3zee) in [#424](https://github.com/Kotlin/kotlinx-rpc/pull/424)
* Update ktor-all-platforms-app sample to sync service creation by [@Mr3zee](https://github.com/Mr3zee) in [#455](https://github.com/Kotlin/kotlinx-rpc/pull/455)
* Added Ktor closure tests and Cancellation tests, + minor fixes  by [@Mr3zee](https://github.com/Mr3zee) in [#479](https://github.com/Kotlin/kotlinx-rpc/pull/479)
* Fix flaky tests by [@Mr3zee](https://github.com/Mr3zee) in [#481](https://github.com/Kotlin/kotlinx-rpc/pull/481)

### New Contributors {id=New_Contributors_0_10_0}
* [@flockbastian](https://github.com/flockbastian) made their first contribution in [#450](https://github.com/Kotlin/kotlinx-rpc/pull/450)
* [@y9maly](https://github.com/y9maly) made their first contribution in [#445](https://github.com/Kotlin/kotlinx-rpc/pull/445)
* [@sebaslogen](https://github.com/sebaslogen) made their first contribution in [#495](https://github.com/Kotlin/kotlinx-rpc/pull/495)


## 0.9.1
> Published 17 July 2025

**Full Changelog**: [0.8.1...0.9.1](https://github.com/Kotlin/kotlinx-rpc/compare/0.8.1...0.9.1)

#### Bug fixes 🐛 {id=Bug_fixes_0_9_1}
* Support nullable contextual serializers by [@yakivy](https://github.com/yakivy) in [#392](https://github.com/Kotlin/kotlinx-rpc/pull/392)
* Make WS plugin installation for Ktor server more flexible by [@Mr3zee](https://github.com/Mr3zee) in [#398](https://github.com/Kotlin/kotlinx-rpc/pull/398)
* Make KtorRpcClient inherit KrpcClient by [@Mr3zee](https://github.com/Mr3zee) in [#396](https://github.com/Kotlin/kotlinx-rpc/pull/396)

#### Documentation 📗 {id=Documentation_0_9_1}
* Update gRPC doc by [@Mr3zee](https://github.com/Mr3zee) in [#391](https://github.com/Kotlin/kotlinx-rpc/pull/391)
* Add stub targets tags for platforms table by [@Mr3zee](https://github.com/Mr3zee) in [#397](https://github.com/Kotlin/kotlinx-rpc/pull/397)

#### Infra 🚧 {id=Infra_0_9_1}
* Remove the monitor application by [@Mr3zee](https://github.com/Mr3zee) in [#388](https://github.com/Kotlin/kotlinx-rpc/pull/388)

#### Other Changes 🧹 {id=Other_Changes_0_9_1}
* Update version for 0.9.0-SNAPSHOT by [@Mr3zee](https://github.com/Mr3zee) in [#387](https://github.com/Kotlin/kotlinx-rpc/pull/387)


## 0.8.1
> Published 9 July 2025

**Full Changelog**: [0.8.0...0.8.1](https://github.com/Kotlin/kotlinx-rpc/compare/0.8.0...0.8.1)

#### Bug fixes 🐛 {id=Bug_fixes_0_8_1}
* Propagate transport coroutine context by [@yakivy](https://github.com/yakivy) in [#374](https://github.com/Kotlin/kotlinx-rpc/pull/374)
* Fix compiler bugs by [@Mr3zee](https://github.com/Mr3zee) in [#382](https://github.com/Kotlin/kotlinx-rpc/pull/382)
* kRPC client initialization is not single shot by [@Mr3zee](https://github.com/Mr3zee) in [#385](https://github.com/Kotlin/kotlinx-rpc/pull/385)

#### Documentation 📗 {id=Documentation_0_8_1}
* Grpc sample update by [@Mr3zee](https://github.com/Mr3zee) in [#371](https://github.com/Kotlin/kotlinx-rpc/pull/371)
* Fix dokka API button for all modules by [@Mr3zee](https://github.com/Mr3zee) in [#381](https://github.com/Kotlin/kotlinx-rpc/pull/381)

#### Infra 🚧 {id=Infra_0_8_1}
* Update pages publication and api link by [@Mr3zee](https://github.com/Mr3zee) in [#375](https://github.com/Kotlin/kotlinx-rpc/pull/375)
* Added artifacts validation by [@Mr3zee](https://github.com/Mr3zee) in [#377](https://github.com/Kotlin/kotlinx-rpc/pull/377)

#### Other Changes 🧹 {id=Other_Changes_0_8_1}
* Ci updates by [@Mr3zee](https://github.com/Mr3zee) in [#370](https://github.com/Kotlin/kotlinx-rpc/pull/370)
* Updated CSM sources for template generation by [@Mr3zee](https://github.com/Mr3zee) in [#372](https://github.com/Kotlin/kotlinx-rpc/pull/372)
* Update strict-mode.topic by [@BierDav](https://github.com/BierDav) in [#380](https://github.com/Kotlin/kotlinx-rpc/pull/380)
* Update Ktor to 3.2.1 and samples by [@Mr3zee](https://github.com/Mr3zee) in [#383](https://github.com/Kotlin/kotlinx-rpc/pull/383)

### New Contributors {id=New_Contributors_0_8_1}
* [@yakivy](https://github.com/yakivy) made their first contribution in [#374](https://github.com/Kotlin/kotlinx-rpc/pull/374)
* [@BierDav](https://github.com/BierDav) made their first contribution in [#380](https://github.com/Kotlin/kotlinx-rpc/pull/380)


## 0.8.0
> Published 30 June 2025

**Full Changelog**: [0.7.0...0.8.0](https://github.com/Kotlin/kotlinx-rpc/compare/0.7.0...0.8.0)

### Overview {id=Overview_0_8_0}
This release brings a lot of changes, including breaking changes:
- Kotlin 2.2.0 Update
- Decoupling of `kotlinx.serialization` from the core functionality
- Simplifying lifetime schema (services lost their `CoroutineScope`)
- Irreversible enforcement of the [strict mode](https://kotlin.github.io/kotlinx-rpc/strict-mode.html)

These changes significantly reduce the number of footguns and improve the overall usability of the library.

Additionally, the internal structure of kRPC protocol and our compiler plugin reduced its complexity. 
That allows us to provide better quality in future releases
(and this also applies to gRPC, even though in this particular release it was not a priority).

For the full list of changes that require migration,
see the [Migration Guide](https://kotlin.github.io/kotlinx-rpc/0-8-0.html).

### Docs update {id=Docs_update_0_8_0}

We added three new documentation sections:
- [API Reference](https://kotlin.github.io/kotlinx-rpc/api/index.html) 
- [Platform compatibility overview](https://kotlin.github.io/kotlinx-rpc/platforms.html) 
- [Changelog](https://kotlin.github.io/kotlinx-rpc/changelog.html) in the web

#### Features 🎉 {id=Features_0_8_0}
* Serialization decoupling by [@Mr3zee](https://github.com/Mr3zee) in [#354](https://github.com/Kotlin/kotlinx-rpc/pull/354)
* Update Kotlin to 2.2.0 by [@Mr3zee](https://github.com/Mr3zee) in [#359](https://github.com/Kotlin/kotlinx-rpc/pull/359)

#### Breaking Changes 🔴 {id=Breaking_Changes_0_8_0}
* [Meta] Strict mode, deprecations, lifetime by [@Mr3zee](https://github.com/Mr3zee) in [#353](https://github.com/Kotlin/kotlinx-rpc/pull/353)

#### Bug fixes 🐛 {id=Bug_fixes_0_8_0}
* Fix compilation for standalone k2 module by [@Mr3zee](https://github.com/Mr3zee) in [#350](https://github.com/Kotlin/kotlinx-rpc/pull/350)
* Fix Pupperteer Version by [@Mr3zee](https://github.com/Mr3zee) in [#355](https://github.com/Kotlin/kotlinx-rpc/pull/355)

#### Documentation 📗 {id=Documentation_0_8_0}
* Platforms Table and docs by [@Mr3zee](https://github.com/Mr3zee) in [#356](https://github.com/Kotlin/kotlinx-rpc/pull/356)
* Dokka  by [@Mr3zee](https://github.com/Mr3zee) in [#357](https://github.com/Kotlin/kotlinx-rpc/pull/357)
* Add Changelog.md to Docs by [@Mr3zee](https://github.com/Mr3zee) in [#358](https://github.com/Kotlin/kotlinx-rpc/pull/358)
* Onboarding by [@Mr3zee](https://github.com/Mr3zee) in [#363](https://github.com/Kotlin/kotlinx-rpc/pull/363)

#### Other Changes 🧹 {id=Other_Changes_0_8_0}
* Advance version to 0.8.0-SNAPSHOT by [@Mr3zee](https://github.com/Mr3zee) in [#340](https://github.com/Kotlin/kotlinx-rpc/pull/340)
* Update README.md to use non-suspend flows by [@brokenhappy](https://github.com/brokenhappy) in [#342](https://github.com/Kotlin/kotlinx-rpc/pull/342)
* Fix kotlin master builds by [@Mr3zee](https://github.com/Mr3zee) in [#345](https://github.com/Kotlin/kotlinx-rpc/pull/345)
* Simplify Gradle Configs by [@Mr3zee](https://github.com/Mr3zee) in [#360](https://github.com/Kotlin/kotlinx-rpc/pull/360)

### New Contributors {id=New_Contributors_0_8_0}
* [@brokenhappy](https://github.com/brokenhappy) made their first contribution in [#342](https://github.com/Kotlin/kotlinx-rpc/pull/342)


## 0.7.0
> Published 13 May 2025

**Full Changelog**: [0.6.2...0.7.0](https://github.com/Kotlin/kotlinx-rpc/compare/0.6.2...0.7.0)

### Announcement {id=Announcement_0_7_0}
This release enforces ERROR as a default reporting level for APIs that are forbidden by the strict mode.
You can still change the level manually, but in `0.8.0` strict mode will be enforced irreversibly.

#### Breaking Changes 🔴 {id=Breaking_Changes_0_7_0}
* Change strict mode to level ERROR by default by [@Mr3zee](https://github.com/Mr3zee) in [#338](https://github.com/Kotlin/kotlinx-rpc/pull/338)

#### Other Changes 🧹 {id=Other_Changes_0_7_0}
* Update Kotlin to 2.1.21 by [@Mr3zee](https://github.com/Mr3zee) in [#337](https://github.com/Kotlin/kotlinx-rpc/pull/337)
* Version 0.7.0-SNAPSHOT by [@Mr3zee](https://github.com/Mr3zee) in [#325](https://github.com/Kotlin/kotlinx-rpc/pull/325)
* Samples: version 0.6.2 by [@Mr3zee](https://github.com/Mr3zee) in [#326](https://github.com/Kotlin/kotlinx-rpc/pull/326)
* 2.2.0 compiler migration by [@Mr3zee](https://github.com/Mr3zee) in [#335](https://github.com/Kotlin/kotlinx-rpc/pull/335)


## 0.6.2
> Published 17 April 2025

**Full Changelog**: [0.6.1...0.6.2](https://github.com/Kotlin/kotlinx-rpc/compare/0.6.1...0.6.2)

#### Bug fixes 🐛 {id=Bug_fixes_0_6_2}
* Fix flows that emit Units by [@Mr3zee](https://github.com/Mr3zee) in [#323](https://github.com/Kotlin/kotlinx-rpc/pull/323)


## 0.6.1
> Published 11 April 2025

**Full Changelog**: [0.6.0...0.6.1](https://github.com/Kotlin/kotlinx-rpc/compare/0.6.0...0.6.1)

#### Bug fixes 🐛 {id=Bug_fixes_0_6_1}
* Fix bidirectional flows in non-suspend streams by [@Mr3zee](https://github.com/Mr3zee) in [#316](https://github.com/Kotlin/kotlinx-rpc/pull/316)
* Fix KRPC-173 (#315) by [@Mr3zee](https://github.com/Mr3zee) in [#317](https://github.com/Kotlin/kotlinx-rpc/pull/317)


## 0.6.0
> Published 4 April 2025

**Full Changelog**: [0.5.1...0.6.0](https://github.com/Kotlin/kotlinx-rpc/compare/0.5.1...0.6.0)

#### Features 🎉 {id=Features_0_6_0}
* 2.1.20 by [@Mr3zee](https://github.com/Mr3zee) in [#309](https://github.com/Kotlin/kotlinx-rpc/pull/309)
* Non suspend flow by [@Mr3zee](https://github.com/Mr3zee) in [#299](https://github.com/Kotlin/kotlinx-rpc/pull/299)

#### Documentation 📗 {id=Documentation_0_6_0}
* Update gRPC Docs and Sample by [@Mr3zee](https://github.com/Mr3zee) in [#284](https://github.com/Kotlin/kotlinx-rpc/pull/284)

#### Infra 🚧 {id=Infra_0_6_0}
* Update monitior by [@Mr3zee](https://github.com/Mr3zee) in [#282](https://github.com/Kotlin/kotlinx-rpc/pull/282)
* Fix build config for for-ide builds by [@Mr3zee](https://github.com/Mr3zee) in [#287](https://github.com/Kotlin/kotlinx-rpc/pull/287)
* Update build for custom KC versions by [@Mr3zee](https://github.com/Mr3zee) in [#293](https://github.com/Kotlin/kotlinx-rpc/pull/293)
* Fix kotlin master compilation by [@Mr3zee](https://github.com/Mr3zee) in [#306](https://github.com/Kotlin/kotlinx-rpc/pull/306)

#### Other Changes 🧹 {id=Other_Changes_0_6_0}
* Upgrade Gradle to 8.12.1 by [@Mr3zee](https://github.com/Mr3zee) in [#286](https://github.com/Kotlin/kotlinx-rpc/pull/286)
* Version 0.6.0-SNAPSHOT by [@Mr3zee](https://github.com/Mr3zee) in [#283](https://github.com/Kotlin/kotlinx-rpc/pull/283)
* Update leftover sources from jvm-only to kmp by [@Mr3zee](https://github.com/Mr3zee) in [#300](https://github.com/Kotlin/kotlinx-rpc/pull/300)
* KRPC-129 Move compatibility tests from Toolbox to Kotlin RPC repo by [@Mr3zee](https://github.com/Mr3zee) in [#304](https://github.com/Kotlin/kotlinx-rpc/pull/304)
* Dependency bump by [@Mr3zee](https://github.com/Mr3zee) in [#303](https://github.com/Kotlin/kotlinx-rpc/pull/303)
* Better compiler error message for checked annotations by [@Mr3zee](https://github.com/Mr3zee) in [#302](https://github.com/Kotlin/kotlinx-rpc/pull/302)


## 0.5.1
> Published 12 February 2025

**Full Changelog**: [0.5.0...0.5.1](https://github.com/Kotlin/kotlinx-rpc/compare/0.5.0...0.5.1)

#### Features 🎉 {id=Features_0_5_1}
* 2.1.10 by [@Mr3zee](https://github.com/Mr3zee) in [#271](https://github.com/Kotlin/kotlinx-rpc/pull/271)

#### Documentation 📗 {id=Documentation_0_5_1}
* Fix typo in README.md by [@SebastianAigner](https://github.com/SebastianAigner) in [#266](https://github.com/Kotlin/kotlinx-rpc/pull/266)
* Added IDE plugin docs by [@Mr3zee](https://github.com/Mr3zee) in [#273](https://github.com/Kotlin/kotlinx-rpc/pull/273)

#### Infra 🚧 {id=Infra_0_5_1}
* Update for IDE configs by [@Mr3zee](https://github.com/Mr3zee) in [#272](https://github.com/Kotlin/kotlinx-rpc/pull/272)
* Added python deps automerge by [@Mr3zee](https://github.com/Mr3zee) in [#276](https://github.com/Kotlin/kotlinx-rpc/pull/276)

#### Other Changes 🧹 {id=Other_Changes_0_5_1}
* Update README.md by [@pambrose](https://github.com/pambrose) in [#268](https://github.com/Kotlin/kotlinx-rpc/pull/268)
* Added monitor app for deps publication by [@Mr3zee](https://github.com/Mr3zee) in [#265](https://github.com/Kotlin/kotlinx-rpc/pull/265)
* Fix kotlin master by [@Mr3zee](https://github.com/Mr3zee) in [#274](https://github.com/Kotlin/kotlinx-rpc/pull/274)
* Fix println tests by [@Mr3zee](https://github.com/Mr3zee) in [#277](https://github.com/Kotlin/kotlinx-rpc/pull/277)
* Update Python Monitor Dependencies by [@renovate](https://github.com/renovate) in [#278](https://github.com/Kotlin/kotlinx-rpc/pull/278)

### New Contributors {id=New_Contributors_0_5_1}
* [@SebastianAigner](https://github.com/SebastianAigner) made their first contribution in [#266](https://github.com/Kotlin/kotlinx-rpc/pull/266)
* [@pambrose](https://github.com/pambrose) made their first contribution in [#268](https://github.com/Kotlin/kotlinx-rpc/pull/268)


## 0.5.0
> Published 27 January 2025

**Full Changelog**: [0.4.0...0.5.0](https://github.com/Kotlin/kotlinx-rpc/compare/0.4.0...0.5.0)

#### Features 🎉 {id=Features_0_5_0}
* Update Service Descriptors Generation by [@Mr3zee](https://github.com/Mr3zee) in [#227](https://github.com/Kotlin/kotlinx-rpc/pull/227)
* Kotlin 2.1.0 by [@Mr3zee](https://github.com/Mr3zee) in [#244](https://github.com/Kotlin/kotlinx-rpc/pull/244)
* Added basic CheckedTypeAnnotation impl with compiler plugin check by [@Mr3zee](https://github.com/Mr3zee) in [#240](https://github.com/Kotlin/kotlinx-rpc/pull/240)
* Strict mode by [@Mr3zee](https://github.com/Mr3zee) in [#243](https://github.com/Kotlin/kotlinx-rpc/pull/243)

#### Breaking Changes 🔴 {id=Breaking_Changes_0_5_0}
* Api naming by [@Mr3zee](https://github.com/Mr3zee) in [#236](https://github.com/Kotlin/kotlinx-rpc/pull/236)
* Update Service Descriptors Generation by [@Mr3zee](https://github.com/Mr3zee) in [#227](https://github.com/Kotlin/kotlinx-rpc/pull/227)
* Added basic CheckedTypeAnnotation impl with compiler plugin check by [@Mr3zee](https://github.com/Mr3zee) in [#240](https://github.com/Kotlin/kotlinx-rpc/pull/240)

#### Deprecations ⚠️ {id=Deprecations_0_5_0}
* Api naming by [@Mr3zee](https://github.com/Mr3zee) in [#236](https://github.com/Kotlin/kotlinx-rpc/pull/236)
* Strict mode by [@Mr3zee](https://github.com/Mr3zee) in [#243](https://github.com/Kotlin/kotlinx-rpc/pull/243)

#### Infra 🚧 {id=Infra_0_5_0}
* Update the project structure to work with kotlin-master by [@Mr3zee](https://github.com/Mr3zee) in [#234](https://github.com/Kotlin/kotlinx-rpc/pull/234)
* Fixed version formatting with ENV vars by [@Mr3zee](https://github.com/Mr3zee) in [#235](https://github.com/Kotlin/kotlinx-rpc/pull/235)
* Fix Kotlin master compilation by [@Mr3zee](https://github.com/Mr3zee) in [#245](https://github.com/Kotlin/kotlinx-rpc/pull/245)
* Opt-out from annotations type safety analysis by [@Mr3zee](https://github.com/Mr3zee) in [#246](https://github.com/Kotlin/kotlinx-rpc/pull/246)

#### Other Changes 🧹 {id=Other_Changes_0_5_0}
* Added test for non-serializable params by [@Mr3zee](https://github.com/Mr3zee) in [#237](https://github.com/Kotlin/kotlinx-rpc/pull/237)
* Updated descriptor to use `RpcType` instead of `KType` directly by [@Mr3zee](https://github.com/Mr3zee) in [#239](https://github.com/Kotlin/kotlinx-rpc/pull/239)


## 0.4.0
> Published 5 November 2024

**Full Changelog**: [0.3.0...0.4.0](https://github.com/Kotlin/kotlinx-rpc/compare/0.3.0...0.4.0)

#### Features 🎉 {id=Features_0_4_0}
* Experimental support for: KRPC-125 Manual stream scope management by [@Mr3zee](https://github.com/Mr3zee) in [#214](https://github.com/Kotlin/kotlinx-rpc/pull/214)
* Introduce [@Rpc](https://github.com/Rpc) annotation by [@Mr3zee](https://github.com/Mr3zee) in [#216](https://github.com/Kotlin/kotlinx-rpc/pull/216)
* Support Kotlin 2.0.21 and 2.0.20 by [@Mr3zee](https://github.com/Mr3zee) in [#217](https://github.com/Kotlin/kotlinx-rpc/pull/217)

#### Breaking Changes 🔴 {id=Breaking_Changes_0_4_0}
* Introduce [@Rpc](https://github.com/Rpc) annotation by [@Mr3zee](https://github.com/Mr3zee) in [#216](https://github.com/Kotlin/kotlinx-rpc/pull/216)
* Remove support for Kotlin versions prior to 2.0 by [@Mr3zee](https://github.com/Mr3zee) in [#218](https://github.com/Kotlin/kotlinx-rpc/pull/218)

#### Infra 🚧 {id=Infra_0_4_0}
* Added proxy repository settings by [@Mr3zee](https://github.com/Mr3zee) in [#191](https://github.com/Kotlin/kotlinx-rpc/pull/191)
* Added Kotlin for ide configs to project by [@Mr3zee](https://github.com/Mr3zee) in [#219](https://github.com/Kotlin/kotlinx-rpc/pull/219)

#### Bug fixes 🐛 {id=Bug_fixes_0_4_0}
* Fix for KT-41082 by [@Mr3zee](https://github.com/Mr3zee) in [#225](https://github.com/Kotlin/kotlinx-rpc/pull/225)

#### Other Changes 🧹 {id=Other_Changes_0_4_0}
* Bump core deps by [@Mr3zee](https://github.com/Mr3zee) in [#220](https://github.com/Kotlin/kotlinx-rpc/pull/220)


## 0.3.0
> Published 1 October 2024

**Full Changelog**: [0.2.4...0.3.0](https://github.com/Kotlin/kotlinx-rpc/compare/0.2.4...0.3.0)

#### Features 🎉 {id=Features_0_3_0}
* Wasm Support by [@Mr3zee](https://github.com/Mr3zee) in [#190](https://github.com/Kotlin/kotlinx-rpc/pull/190)

#### Breaking Changes 🔴 {id=Breaking_Changes_0_3_0}
* Move kRPC declarations from core by [@Mr3zee](https://github.com/Mr3zee) in [#186](https://github.com/Kotlin/kotlinx-rpc/pull/186)

#### Bug fixes 🐛 {id=Bug_fixes_0_3_0}
* Fix kotlin/js code and samples by [@Mr3zee](https://github.com/Mr3zee) in [#179](https://github.com/Kotlin/kotlinx-rpc/pull/179)
* Fix regression of nested declarations in RPC interfaces by [@Mr3zee](https://github.com/Mr3zee) in [#181](https://github.com/Kotlin/kotlinx-rpc/pull/181)

#### Documentation 📗 {id=Documentation_0_3_0}
* Wording fixes by [@Mr3zee](https://github.com/Mr3zee) in [#175](https://github.com/Kotlin/kotlinx-rpc/pull/175)

#### Infra 🚧 {id=Infra_0_3_0}
* Infra enhancements by [@Mr3zee](https://github.com/Mr3zee) in [#206](https://github.com/Kotlin/kotlinx-rpc/pull/206)
* Fixed Wasm Publication by [@Mr3zee](https://github.com/Mr3zee) in [#208](https://github.com/Kotlin/kotlinx-rpc/pull/208)
* Update renovate configs by [@Mr3zee](https://github.com/Mr3zee) in [#185](https://github.com/Kotlin/kotlinx-rpc/pull/185)
* Configure JPMS checks by [@Mr3zee](https://github.com/Mr3zee) in [#187](https://github.com/Kotlin/kotlinx-rpc/pull/187)
* Update some ide configs by [@Mr3zee](https://github.com/Mr3zee) in [#188](https://github.com/Kotlin/kotlinx-rpc/pull/188)
* Added Gradle Doctor to the build by [@Mr3zee](https://github.com/Mr3zee) in [#189](https://github.com/Kotlin/kotlinx-rpc/pull/189)
* Added build cache and develocity plugin by [@Mr3zee](https://github.com/Mr3zee) in [#192](https://github.com/Kotlin/kotlinx-rpc/pull/192)
* Support variable Kotlin and project versions by [@Mr3zee](https://github.com/Mr3zee) in [#201](https://github.com/Kotlin/kotlinx-rpc/pull/201)
* Update renovate and some deps by [@Mr3zee](https://github.com/Mr3zee) in [#198](https://github.com/Kotlin/kotlinx-rpc/pull/198)

#### Other Changes 🧹 {id=Other_Changes_0_3_0}
* Bump version to 0.3.0-SNAPSHOT by [@Mr3zee](https://github.com/Mr3zee) in [#174](https://github.com/Kotlin/kotlinx-rpc/pull/174)
* Fix compiler tests after #172 by [@Mr3zee](https://github.com/Mr3zee) in [#180](https://github.com/Kotlin/kotlinx-rpc/pull/180)
* Remove unused and inactive annotation by [@kez-lab](https://github.com/kez-lab) in [#182](https://github.com/Kotlin/kotlinx-rpc/pull/182)
* Use built-in JsClass getter by [@Mr3zee](https://github.com/Mr3zee) in [#184](https://github.com/Kotlin/kotlinx-rpc/pull/184)
* Update actions/configure-pages action to v5 by [@renovate](https://github.com/renovate) in [#200](https://github.com/Kotlin/kotlinx-rpc/pull/200)
* Update Samples dependencies by [@renovate](https://github.com/renovate) in [#199](https://github.com/Kotlin/kotlinx-rpc/pull/199)
* Update Core dependencies (non-major) by [@renovate](https://github.com/renovate) in [#194](https://github.com/Kotlin/kotlinx-rpc/pull/194)
* Update Core dependencies (non-major) by [@renovate](https://github.com/renovate) in [#205](https://github.com/Kotlin/kotlinx-rpc/pull/205)

### New Contributors {id=New_Contributors_0_3_0}
* [@kez-lab](https://github.com/kez-lab) made their first contribution in [#182](https://github.com/Kotlin/kotlinx-rpc/pull/182)


## 0.2.4
> Published 20 August 2024

**Full Changelog**: [0.2.2...0.2.4](https://github.com/Kotlin/kotlinx-rpc/compare/0.2.2...0.2.4)

#### Features {id=Features_0_2_4}
* KRPC-18 Add K2 and IR code generation plugins, preserve KSP for K1 by [@Mr3zee](https://github.com/Mr3zee) in [#105](https://github.com/Kotlin/kotlinx-rpc/pull/105)
* Added 1.9.25 and 2.0.10 Kotlin Versions by [@Mr3zee](https://github.com/Mr3zee) in [#168](https://github.com/Kotlin/kotlinx-rpc/pull/168)

#### Bug fixes {id=Bug_fixes_0_2_4}
* KRPC-101 Check if the entire stream is not already closed. by [@pikinier20](https://github.com/pikinier20) in [#158](https://github.com/Kotlin/kotlinx-rpc/pull/158)
* KRPC-119 Exception Deserialization by [@Mr3zee](https://github.com/Mr3zee) in [#170](https://github.com/Kotlin/kotlinx-rpc/pull/170)
* Fix compilation on Kotlin/Native by [@Mr3zee](https://github.com/Mr3zee) in [#172](https://github.com/Kotlin/kotlinx-rpc/pull/172)

#### Infra {id=Infra_0_2_4}
* Add issue templates by [@Mr3zee](https://github.com/Mr3zee) in [#167](https://github.com/Kotlin/kotlinx-rpc/pull/167)

### New Contributors {id=New_Contributors_0_2_4}
* [@pikinier20](https://github.com/pikinier20) made their first contribution in [#158](https://github.com/Kotlin/kotlinx-rpc/pull/158)


## 0.2.2
> Published 5 August 2024

**Full Changelog**: [0.2.1...0.2.2](https://github.com/Kotlin/kotlinx-rpc/compare/0.2.1...0.2.2)

#### Bug fixes {id=Bug_fixes_0_2_2}
* Fix log error messages by [@Mr3zee](https://github.com/Mr3zee) in [#139](https://github.com/Kotlin/kotlinx-rpc/pull/139)
* KRPC-97 Race condition in stream cancellation locks the transport by [@Mr3zee](https://github.com/Mr3zee) in [#138](https://github.com/Kotlin/kotlinx-rpc/pull/138)
* Fix call cancellation by [@Mr3zee](https://github.com/Mr3zee) in [#141](https://github.com/Kotlin/kotlinx-rpc/pull/141)



## 0.2.1
> Published 1 July 2024

**Full Changelog**: [0.1.0...0.2.1](https://github.com/Kotlin/kotlinx-rpc/compare/0.1.0...0.2.1)

#### Breaking changes {id=Breaking_changes_0_2_1}
Release contains breaking changes, see the [migration guide](https://kotlin.github.io/kotlinx-rpc/0-2-0.html)

* Update the project structure to use a new versioning by [@Mr3zee](https://github.com/Mr3zee) in [#92](https://github.com/Kotlin/kotlinx-rpc/pull/92)
* Consistent module structure by [@Mr3zee](https://github.com/Mr3zee) in [#114](https://github.com/Kotlin/kotlinx-rpc/pull/114)
* KRPC-63 Reorganize modules into a logical structure by [@Mr3zee](https://github.com/Mr3zee) in [#110](https://github.com/Kotlin/kotlinx-rpc/pull/110)

#### Features {id=Features_0_2_1}
* KRPC-62 WebSocketSession KtorRPCClient by [@Mr3zee](https://github.com/Mr3zee) in [#107](https://github.com/Kotlin/kotlinx-rpc/pull/107)

#### Bug fixes {id=Bug_fixes_0_2_1}
* Fix typo by [@fatalistt](https://github.com/fatalistt) in [#76](https://github.com/Kotlin/kotlinx-rpc/pull/76)
* Fix README by [@Mr3zee](https://github.com/Mr3zee) in [#79](https://github.com/Kotlin/kotlinx-rpc/pull/79)
* Remove todos from repo links by [@Mr3zee](https://github.com/Mr3zee) in [#85](https://github.com/Kotlin/kotlinx-rpc/pull/85)
* Fix typo in README.md by [@zhelenskiy](https://github.com/zhelenskiy) in [#87](https://github.com/Kotlin/kotlinx-rpc/pull/87)
* Fix KDoc reference by [@Mr3zee](https://github.com/Mr3zee) in [#103](https://github.com/Kotlin/kotlinx-rpc/pull/103)
* Fix native targets compiler plugins (#93) by [@Mr3zee](https://github.com/Mr3zee) in [#112](https://github.com/Kotlin/kotlinx-rpc/pull/112)

#### Deprecations {id=Deprecations_0_2_1}
* KRPC-59 streamScoped has internal package by [@Mr3zee](https://github.com/Mr3zee) in [#104](https://github.com/Kotlin/kotlinx-rpc/pull/104)

#### Documentation {id=Documentation_0_2_1}
* Updated links on the docs website by [@Mr3zee](https://github.com/Mr3zee) in [#71](https://github.com/Kotlin/kotlinx-rpc/pull/71)
* Added search indexes publication to Algolia action by [@Mr3zee](https://github.com/Mr3zee) in [#73](https://github.com/Kotlin/kotlinx-rpc/pull/73)
* Fix algolia artifact in GH Actions by [@Mr3zee](https://github.com/Mr3zee) in [#80](https://github.com/Kotlin/kotlinx-rpc/pull/80)
* Docs: Add structure to existing content by [@vnikolova](https://github.com/vnikolova) in [#86](https://github.com/Kotlin/kotlinx-rpc/pull/86)
* Added build and test docs on PR by [@Mr3zee](https://github.com/Mr3zee) in [#97](https://github.com/Kotlin/kotlinx-rpc/pull/97)
* Fixed GH Actions for docs by [@Mr3zee](https://github.com/Mr3zee) in [#99](https://github.com/Kotlin/kotlinx-rpc/pull/99)
* Disable docs website autoupdate by [@Mr3zee](https://github.com/Mr3zee) in [#108](https://github.com/Kotlin/kotlinx-rpc/pull/108)
* KRPC-60 Update docs to match the new versioning scheme by [@Mr3zee](https://github.com/Mr3zee) in [#109](https://github.com/Kotlin/kotlinx-rpc/pull/109)

#### Infra {id=Infra_0_2_1}
* Update version to 0.1.1-SNAPSHOT by [@Mr3zee](https://github.com/Mr3zee) in [#64](https://github.com/Kotlin/kotlinx-rpc/pull/64)
* Version updates by [@Mr3zee](https://github.com/Mr3zee) in [#72](https://github.com/Kotlin/kotlinx-rpc/pull/72)
* Signing and Publication by [@Mr3zee](https://github.com/Mr3zee) in [#74](https://github.com/Kotlin/kotlinx-rpc/pull/74)
* IDEA. Issue links by [@turansky](https://github.com/turansky) in [#96](https://github.com/Kotlin/kotlinx-rpc/pull/96)
* IDEA. Vector icon by [@turansky](https://github.com/turansky) in [#98](https://github.com/Kotlin/kotlinx-rpc/pull/98)
* KRPC-71 Type-safe project accessors by [@Mr3zee](https://github.com/Mr3zee) in [#106](https://github.com/Kotlin/kotlinx-rpc/pull/106)

### New Contributors {id=New_Contributors_0_2_1}
* [@jvmusin](https://github.com/jvmusin) made their first contribution in [#69](https://github.com/Kotlin/kotlinx-rpc/pull/69)
* [@fatalistt](https://github.com/fatalistt) made their first contribution in [#76](https://github.com/Kotlin/kotlinx-rpc/pull/76)
* [@zhelenskiy](https://github.com/zhelenskiy) made their first contribution in [#87](https://github.com/Kotlin/kotlinx-rpc/pull/87)
* [@turansky](https://github.com/turansky) made their first contribution in [#96](https://github.com/Kotlin/kotlinx-rpc/pull/96)
* [@vnikolova](https://github.com/vnikolova) made their first contribution in [#86](https://github.com/Kotlin/kotlinx-rpc/pull/86)

