# grpc-test-server

A standalone grpc-java server for the gRPC client tests. The reference behavior comes from grpc-java, not from the
kotlinx-rpc server. A test can also control the server side of a call: pause the call at chosen points, change how
the call ends, and read what the server observed.

## Run

A test task uses the server with `withGrpcClientTestServer()` (`gradle-conventions/.../util/grpc/grpcClientTestServer.kt`):

```kotlin
tasks.withType<AbstractTestTask>().configureEach {
    withGrpcClientTestServer()
}
```

- All these tasks share one server process. `:tests:grpc-test-server:installDist` installs the server, the server
  starts before the first of these tasks, and it stops when the build finishes.
- Gradle selects a free port for each start. The server gets the port as its first argument and listens on `127.0.0.1`.
  The test task gets the port in the `GRPC_TEST_SERVER_PORT` environment variable. In the tests, use `grpcTestServerPort`.
- When the server accepts calls, it prints `[GRPC-TEST-SERVER] Server started on 127.0.0.1:<port>`.
- A task fails if the server can't start or exits while the task runs.
- For a test binary that runs outside Gradle, run `:tests:grpc-test-server:installDist`, start
  `tests/grpc-test-server/build/install/grpc-test-server/bin/grpc-test-server <port>`, and set `GRPC_TEST_SERVER_PORT`
  to the same port.
- `:tests:grpc-test-server:test` runs the tests of the server itself.

## Services

The protos are in `tests/test-grpc-protos/src/commonMain/proto`. The client tests and the server share them.

| Service | Proto | Purpose |
|---|---|---|
| `EchoService`, `GreeterService` | `echo_grpc.proto`, `helloworld_grpc.proto` | Simple services for the `grpc-core` tests |
| `grpc.testing.TestService` | `grpc/testing/test.proto` (vendored) | The service of the gRPC [interoperability tests](https://github.com/grpc/grpc/blob/master/doc/interop-test-descriptions.md), like `TestServiceImpl` of grpc-java: empty, unary, and all streaming calls, with requested sizes, delays, and statuses. `UnimplementedCall` and `UnimplementedService` return `UNIMPLEMENTED`. |
| `kxrpc.testing.InvalidResponseCountService` | `kxrpc/testing/client_control.proto` | Answers a unary or client-streaming call with zero or two responses. A conforming server can't do this. |
| `kxrpc.testing.GrpcClientControlService` | `kxrpc/testing/client_control.proto` | The control plane of the [scenarios](#scenarios) |

- A `TestService` call without scenario metadata behaves exactly like a call to the grpc-java interop server.
- An `InvalidResponseCountService` call without a scenario that sets `invalid_response_count` fails with `INVALID_ARGUMENT`.

## Scenarios

A test makes two kinds of calls:

| Kind | Caller | Service |
|---|---|---|
| Data plane: the calls under test | The kotlinx-rpc client under test | `TestService` or `InvalidResponseCountService` |
| Control plane: prepares and observes a data-plane call | The test, through a separate client | `GrpcClientControlService` |

Both kinds go to `127.0.0.1:<port>`, except the data-plane calls to a disposable endpoint.

A scenario connects the two. It is server state that the test registers under a `call_id` of its choice.
A data-plane call selects the scenario with the ASCII request metadata `kxrpc-test-call-id: <call_id>`.
The server then handles the call as the scenario describes, and records each step.

A typical test:

1. Calls `ConfigureScenario`.
2. Starts the data-plane call with the `kxrpc-test-call-id` metadata.
3. Synchronizes with the server while the call runs: `AwaitEvent`, `ReleaseBarrier`, `AllowInboundReads`.
4. Checks what the server observed: `GetTrace`, `GetScenarioDiagnostics`.
5. Calls `DiscardScenario`.

### Control-plane methods

Each method takes the `call_id` of its scenario.

| Method | Effect |
|---|---|
| `ConfigureScenario` | Registers the scenario and its [configuration](#configuration). Call it before the data-plane call starts. A `call_id` accepts one configuration only. |
| `AwaitEvent` | Blocks until the server records the requested [event](#events), and returns it. With more than one event type, it returns the first event that matches one of them. |
| `ReleaseBarrier` | Releases one barrier, so a data-plane call that is paused at the barrier continues. |
| `AllowInboundReads` | With manual inbound demand: lets the server read the given number of additional request messages. Until then the requests stay unread, so a test can check the backpressure of the client. Needs an active data-plane call. |
| `GetTrace` | Returns all events of the scenario so far, in order. |
| `GetScenarioDiagnostics` | Returns the number of open data-plane calls, the barriers that aren't released, and the number of blocked `AwaitEvent` calls. Use it to check that the test left none of them. |
| `StartDisposableEndpoint` | Opens a separate listener for the scenario on a free port of `127.0.0.1` and returns the port. The listener serves only `TestService`. |
| `StopDisposableEndpoint` | Resets all TCP connections of the listener, which simulates a lost connection, and closes it. |
| `DiscardScenario` | Removes the scenario and stops its disposable endpoint. `AwaitEvent` calls and paused data-plane calls of the scenario fail. |

### Configuration

| Field of `ConfigureScenarioRequest` | Effect |
|---|---|
| `barriers` | Points where the call pauses until the test calls `ReleaseBarrier` |
| `initial_metadata`, `trailing_metadata` | Entries that the server adds to the response headers and trailers, in order and with duplicate keys |
| `terminal_behavior` | Closes the call early with a given `status` and the configured trailers, at one of the stages below |
| `flow_control.manual_inbound_demand` | The server reads request messages only after `AllowInboundReads`. Only for the request-streaming methods of `TestService`. |
| `flow_control.respect_response_readiness` | The server holds back responses while the client isn't ready to receive them. Only for the response-streaming methods of `TestService`. |
| `invalid_response_count` | `OMIT_RESPONSE` (zero responses) or `DUPLICATE_RESPONSE` (two responses), for `InvalidResponseCountService` |

A barrier has a type and a one-based occurrence. For example, `SEND_RESPONSE` with occurrence 2 pauses the call
before its second response. A released barrier stays released, so a call that reaches it later doesn't pause.
Barriers make races reproducible, for example a cancellation between two responses.

| Barrier type | The call pauses before | Occurrence counts |
|---|---|---|
| `SEND_INITIAL_HEADERS` | The server sends the initial headers. A trailers-only close skips it. | Only occurrence 1 |
| `SEND_RESPONSE` | The server sends a response message. | The response messages |
| `CLOSE_CALL` | The server closes the call, also for a close from `terminal_behavior` or a server failure. | Only occurrence 1 |
| `DELIVER_REQUEST_MESSAGE` | The service gets a received request message. | The `REQUEST_MESSAGE_RECEIVED` events |
| `DELIVER_CLIENT_HALF_CLOSE` | The service gets the half-close of the client. | The `CLIENT_HALF_CLOSED` events |

| `terminal_behavior` stage | The call closes |
|---|---|
| `BEFORE_INITIAL_METADATA` | Before the service starts, without headers or responses |
| `AFTER_INITIAL_METADATA` | After the initial headers, before the first response |
| `AFTER_RESPONSE_MESSAGES` | After `response_message_count` responses. If the service completes before that count, the call fails with `INTERNAL`. |
| `AFTER_SERVICE_COMPLETION` | After the service completes, with this status and not the status of the service |

### Events

The server records each step of a data-plane call of a scenario as a `CallEvent`. `client_control.proto` describes
each event type and its data.

| Group | Event types |
|---|---|
| Steps of the call | `CALL_ACCEPTED`, `REQUEST_MESSAGE_RECEIVED`, `CLIENT_HALF_CLOSED`, `INITIAL_HEADERS_SENT`, `RESPONSE_MESSAGE_SENT` |
| End of the call, exactly one | `CALL_CLOSED`, `CLIENT_CANCELLED` |
| Response readiness | `RESPONSE_DELIVERY_BLOCKED`, `RESPONSE_DELIVERY_READY` |

Each event has a one-based occurrence for its type. `AwaitEvent` selects an event by type and occurrence.
To wait for the end of a call that either side can end, give `AwaitEvent` both `CALL_CLOSED` and `CLIENT_CANCELLED`.

### Errors

| Status | Cause |
|---|---|
| `NOT_FOUND` | A control-plane call or a data-plane call has an unknown `call_id`. |
| `INVALID_ARGUMENT` | The configuration or another control-plane request isn't valid. Or a data-plane call has a blank `kxrpc-test-call-id` value or more than one value. |
| `FAILED_PRECONDITION` | The call isn't valid in the current scenario state. For example, the `call_id` already has a configuration, the barrier isn't configured or is already released, or the test discarded the scenario. |
| `DEADLINE_EXCEEDED` | An `AwaitEvent` call or a call paused at a barrier waited for 10 seconds. So a broken test can't block the server. |

## Example

A client that stops the collection of a response stream must also cancel the call on the server.
The barrier keeps the call open after the first response, so the cancellation can't race with a normal completion.

- `control` is the control-plane `GrpcClientControlService` client.
- `testService` is the data-plane `TestService` client. It adds `kxrpc-test-call-id: <id>` to each call, for example
  with a `GrpcClientInterceptor` that adds it to `requestHeaders`.

```kotlin
// Asks the server for two responses of 5 and 7 bytes.
val request = StreamingOutputCallRequest {
    responseParameters = listOf(ResponseParameters { size = 5 }, ResponseParameters { size = 7 })
}

control.configureScenario(ConfigureScenarioRequest {
    callId = id
    barriers = listOf(Barrier { type = BarrierType.SEND_RESPONSE; occurrence = 2u })
})

// Takes the first response and stops the collection. The server is paused before the second response.
testService.streamingOutputCall(request).first()

// The cancellation reaches the server asynchronously.
control.awaitEvent(AwaitEventRequest { callId = id; events = listOf(EventType.CLIENT_CANCELLED); occurrence = 1u })
control.releaseBarrier(ReleaseBarrierRequest { callId = id; barrier = BarrierType.SEND_RESPONSE; occurrence = 2u })

val trace = control.getTrace(GetTraceRequest { callId = id })
assertEquals(EventType.CLIENT_CANCELLED, trace.events.last().type)

val diagnostics = control.getScenarioDiagnostics(GetScenarioDiagnosticsRequest { callId = id })
assertEquals(0u, diagnostics.activeCallCount)
assertEquals(emptyList(), diagnostics.outstandingBarriers)

control.discardScenario(DiscardScenarioRequest { callId = id })
```

To check the client between two responses of a call that completes, collect the stream in an `async` block,
wait for `RESPONSE_MESSAGE_SENT` with occurrence 1, make the assertions, and then release the barrier.

## Code map

| File | Role |
|---|---|
| `TestServer.kt` | Entry point. Starts all services on `127.0.0.1:<port>`. |
| `CallScenarioRegistry.kt` | Scenario state: configuration, trace, barriers, inbound demand, diagnostics |
| `ScenarioInterceptor.kt` | Applies a scenario to the calls with `kxrpc-test-call-id` and records their events |
| `ScenarioStatus.kt` | Maps scenario failures to the status codes in [Errors](#errors) |
| `InteropTestService.kt` | `TestService`. Sends streaming responses at the requested intervals. |
| `ManualInboundDemand.kt` | Manual inbound demand |
| `ResponseReadinessGate.kt` | Response readiness |
| `InvalidResponseCountTestService.kt` | Raw handlers that send zero or two responses |
| `GrpcClientControlService.kt` | The control-plane methods |
| `EchoServiceImpl.kt`, `GreeterServiceImpl.kt` | `EchoService` and `GreeterService` |
| `DisposableEndpointManager.kt`, `ResettingTcpProxy.kt` | Disposable endpoints |
