# Overview

kRPC is the custom bidirectional RPC protocol of kotlinx-rpc. It sends messages over a transport that the user
selects. The reference transport is Ktor WebSockets.

[codegen.md](codegen.md) shows what the compiler plugin generates.

## Tests

| Task | Runs |
|---|---|
| `:krpc:krpc-core:jvmTest`, `:krpc:krpc-client:jvmTest`, `:krpc:krpc-test:jvmTest` | The JVM tests of one module |
| `:krpc:krpc-test:allTests` | All targets. Run it after a change in platform-specific code. |
| `:krpc:krpc-core:jvmTest -PstressTests=true` | Also the stress tests, which don't run by default |
| `:tests:krpc-compatibility-tests:jvmTest` | Old and new API versions in separate class loaders: a client of one version with a server of the other |
| `:tests:krpc-protocol-compatibility-tests:jvmTest` | All pairs of protocol versions, with code that a template generates for each version |
