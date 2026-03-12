package kotlinx.rpc.sample

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.status
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.sample.messages.invoke
import kotlinx.rpc.sample.messages.ChatEntry
import kotlinx.rpc.sample.messages.MessageService
import kotlinx.rpc.sample.messages.ReceiveMessagesRequest
import kotlinx.rpc.sample.messages.SendMessageRequest
import kotlinx.rpc.withService
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
@Preview
@OptIn(ExperimentalRpcApi::class)
fun App() {

    val grpcClient = remember {
        GrpcClient(SERVER_HOST, 8080) {
            credentials = plaintext()
        }
    }

    val service = remember { grpcClient.withService<MessageService>() }

    val httpClient = remember { HttpClient() }

    MaterialTheme {
        ChatScreen(service, httpClient)
    }
}

private sealed class RestStatus {
    data object Idle : RestStatus()
    data object Loading : RestStatus()
    data class Success(val message: String) : RestStatus()
    data object Unavailable : RestStatus()
}

@Composable
@OptIn(ExperimentalTime::class)
private fun ChatScreen(service: MessageService, httpClient: HttpClient) {
    val scope = rememberCoroutineScope()

    var me by remember { mutableStateOf("user-${Random.nextInt(until = 999)}" ) }
    var input by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatEntry>() }
    var error by remember { mutableStateOf<String?>(null) }
    var isSending by remember { mutableStateOf(false) }
    var retryCountDown by remember { mutableStateOf(0L) }
    var restStatus by remember { mutableStateOf<RestStatus>(RestStatus.Idle) }

    fun sendMessage() {
        val currentUser = me.trim()
        val draftText = input
        val messageText = draftText.trim()

        if (isSending) return
        if (currentUser.isBlank()) {
            error = "Enter a user name before sending."
            return
        }
        if (messageText.isBlank()) {
            error = "Enter a message before sending."
            return
        }

        scope.launch {
            isSending = true
            try {
                val result = service.SendMessage(
                    SendMessageRequest {
                        user = currentUser
                        text = messageText
                    }
                )
                if (result.success) {
                    messages += ChatEntry {
                        user = currentUser
                        text = messageText
                        tsMillis = Clock.System.now().toEpochMilliseconds()
                    }
                    if (input == draftText) {
                        input = ""
                    }
                    error = null
                } else {
                    error = "Message not accepted by server."
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                error = e.toRpcError("sending")
            } finally {
                isSending = false
            }
        }
    }

    fun checkHealth() {
        scope.launch {
            restStatus = RestStatus.Loading
            try {
                val response = httpClient.get("http://$SERVER_HOST:8081/health")
                restStatus = RestStatus.Success(response.bodyAsText())
            } catch (e: CancellationException) {
                throw e
            } catch (_: Throwable) {
                restStatus = RestStatus.Unavailable
            }
        }
    }

    LaunchedEffect(me.trim()) {
        val currentUser = me.trim()
        messages.clear()
        error = null
        if (currentUser.isBlank()) {
            error = "Enter a user name to connect."
            return@LaunchedEffect
        }

        val req = ReceiveMessagesRequest { user = currentUser }
        try {
            service.ReceiveMessages(req)
                .retryWhen { cause, attempt ->
                    if (cause is CancellationException) return@retryWhen false

                    error = cause.toRpcError("receiving")
                    if (!cause.isRetryableRpcFailure()) {
                        return@retryWhen false
                    }

                    val seconds = minOf(attempt + 1, 5)
                    for (i in 0 until seconds) {
                        retryCountDown = seconds - i
                        delay(1.seconds)
                    }
                    error = null
                    true
                }
                .collect { msg -> messages += msg; error = null }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            error = e.toRpcError("receiving")
        }
    }

    Column(Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing)
        .padding(16.dp)
    ) {

        if (error != null) {
            ErrorBanner(error!!, retryCountDown)
        }

        OutlinedTextField(
            value = me,
            onValueChange = { me = it },
            label = { Text("Your name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        HealthCheckButton(restStatus, ::checkHealth)

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(messages) { m ->
               MessageBubble(m, me)
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                label = { Text("Message") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = ::sendMessage,
                enabled = !isSending && me.isNotBlank() && input.isNotBlank()
            ) {
                Text(if (isSending) "Sending..." else "Send")
            }
        }
    }
}

@Composable
private fun HealthCheckButton(
    status: RestStatus,
    onCheck: () -> Unit,
) {
    val containerColor by animateColorAsState(
        targetValue = when (status) {
            is RestStatus.Success -> MaterialTheme.colorScheme.primaryContainer
            is RestStatus.Unavailable -> MaterialTheme.colorScheme.surfaceVariant
            else -> MaterialTheme.colorScheme.secondaryContainer
        }
    )

    val contentColor by animateColorAsState(
        targetValue = when (status) {
            is RestStatus.Success -> MaterialTheme.colorScheme.onPrimaryContainer
            is RestStatus.Unavailable -> MaterialTheme.colorScheme.onSurfaceVariant
            else -> MaterialTheme.colorScheme.onSecondaryContainer
        }
    )

    FilledTonalButton(
        onClick = onCheck,
        enabled = status !is RestStatus.Loading,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = when (status) {
                is RestStatus.Idle -> "Send REST"
                is RestStatus.Loading -> "Checking..."
                is RestStatus.Success -> status.message
                is RestStatus.Unavailable -> "No REST — tap to retry"
            }
        )
    }
}

private fun Throwable.toRpcError(action: String): String {
    val status = (this as? GrpcStatusException)?.status
    return status?.statusCode?.toErrorMessage(action)
        ?: message
        ?: "Unknown error while $action."
}

private fun Throwable.isRetryableRpcFailure(): Boolean {
    return when ((this as? GrpcStatusException)?.status?.statusCode) {
        GrpcStatusCode.UNAVAILABLE,
        GrpcStatusCode.CANCELLED,
        GrpcStatusCode.DEADLINE_EXCEEDED,
        GrpcStatusCode.UNKNOWN,
        GrpcStatusCode.INTERNAL -> true
        else -> false
    }
}

private fun GrpcStatusCode.toErrorMessage(action: String): String {
    return when (this) {
        GrpcStatusCode.UNAVAILABLE -> "Server is unavailable."
        else -> "Unknown error occurred while $action."
    }
}

@Composable
fun ErrorBanner(
    error: String,
    retrySeconds: Long,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(8.dp),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.error
        ) {
            Text(error)

            if (retrySeconds > 0) {
                Text("Retry in ${retrySeconds}s")
            }
        }
    }
}

@Composable
@OptIn(ExperimentalTime::class)
fun MessageBubble(
    message: ChatEntry,
    me: String,
    modifier: Modifier = Modifier
) {
    val isMe = message.user == me

    val bubbleColor = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMe) Color.White else Color.Black

    val local = Instant.fromEpochMilliseconds(message.tsMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    // e.g. "14:05"
    val timeText = "${local.hour.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')}"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 250.dp)
                .background(color = bubbleColor, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
        ) {
            if (!isMe) {
                Text(
                    text = message.user,
                    style = MaterialTheme.typography.bodySmall,         // small username line
                    color = if (isMe) textColor.copy(alpha = 0.9f) else textColor.copy(alpha = 0.9f)
                )
            }
            Spacer(Modifier.height(2.dp))

            Text(
                text = message.text,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = timeText,
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.7f)
            )
        }
    }
}
