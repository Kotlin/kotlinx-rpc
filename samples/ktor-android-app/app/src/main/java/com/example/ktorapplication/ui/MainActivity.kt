package com.example.ktorapplication.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ktorapplication.ui.state.WelcomeData
import com.example.ktorapplication.ui.theme.KtorApplicationTheme
import io.ktor.server.application.Application
import io.ktor.server.engine.*

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ktorapplication.server.appRouting
import io.ktor.server.cio.*

class MainActivity : ComponentActivity() {

    private lateinit var server: CIOApplicationEngine
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            KtorApplicationTheme {
                Screen(uiState)
            }
        }
        startServer()
    }

    @Composable
    private fun Screen(welcomeData: WelcomeData?) {
        Surface(modifier = Modifier.fillMaxSize()) {
            if (welcomeData == null) {
                Text(
                    text = "Establishing connection...",
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                SendGreetings(welcomeData = welcomeData)
            }
        }
    }

    override fun onDestroy() {
        server.stop(0, 0)
        super.onDestroy()
    }
}

@Composable
fun SendGreetings(welcomeData: WelcomeData) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = "Kotlin kRPC Sample",
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Hello! Message from server to you: ${welcomeData.serverGreeting}",
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "Latest news:",
                color = MaterialTheme.colorScheme.secondary
            )
            welcomeData.news.forEach {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

private fun startServer() {
    val server =
        embeddedServer(CIO, port = 8080, host = "127.0.0.1", module = Application::appRouting)
    server.start()
}