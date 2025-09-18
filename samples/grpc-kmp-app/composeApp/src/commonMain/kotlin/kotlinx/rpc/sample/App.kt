package kotlinx.rpc.sample

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.sample.messages.ChatEntry
import kotlinx.rpc.sample.messages.MessageService
import kotlinx.rpc.sample.messages.ReceiveMessagesRequest
import kotlinx.rpc.sample.messages.SendMessageRequest
import kotlinx.rpc.sample.messages.invoke
import kotlinx.rpc.withService
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
@Preview
@OptIn(ExperimentalRpcApi::class)
fun App() {

    val grpcClient = remember {
        GrpcClient("localhost", 8080) {
            usePlaintext()
        }
    }

    val service = remember { grpcClient.withService<MessageService>() }

    MaterialTheme() {
        ChatScreen(service)
    }
}

@Composable
@OptIn(ExperimentalTime::class)
private fun ChatScreen(service: MessageService) {
    val scope = rememberCoroutineScope()

    var me by remember { mutableStateOf("me") }
    var input by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatEntry>() }

    fun sendMessage() {
        scope.launch {
            val result = service.SendMessage(
                SendMessageRequest{
                    user = me
                    text = input
                }
            )
            if (result.success) {
                messages += ChatEntry {
                    user = me
                    text = input
                    tsMillis = Clock.System.now().toEpochMilliseconds()
                }
                input = ""
            }
        }
    }

    // Start/refresh the server stream whenever "me" changes
    LaunchedEffect(me) {
        messages.clear()
        val req = ReceiveMessagesRequest { user = me }
        service.ReceiveMessages(req).collect { msg ->
            messages += msg
        }
    }

    Column(Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.safeDrawing)
        .padding(16.dp)
    ) {
        OutlinedTextField(
            value = me,
            onValueChange = { me = it },
            label = { Text("Your name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

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
            Button(onClick = ::sendMessage) { Text("Send") }
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

    // bubble colors
    val bubbleColor = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMe) Color.White else Color.Black

    // formatted time
    val local = Instant.fromEpochMilliseconds(message.tsMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    // e.g. "14:05"
    val timeText = "${local.hour.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')}"

    // alignment: messages from me align to right, others to left
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