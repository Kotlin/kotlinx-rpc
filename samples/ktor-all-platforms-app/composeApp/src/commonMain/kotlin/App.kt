/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.ktor.client.*
import io.ktor.http.*
import kotlinx.rpc.client.withService
import kotlinx.rpc.internal.streamScoped
import kotlinx.rpc.serialization.json
import kotlinx.rpc.transport.ktor.client.installRPC
import kotlinx.rpc.transport.ktor.client.rpc
import kotlinx.rpc.transport.ktor.client.rpcConfig
import ktor_all_platforms_app.composeapp.generated.resources.Res
import ktor_all_platforms_app.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

expect val DEV_SERVER_HOST: String

val client by lazy {
    HttpClient {
        installRPC()
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    var serviceOrNull: UserService? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        serviceOrNull = client.rpc {
            url {
                host = DEV_SERVER_HOST
                port = 8080
                encodedPath = "/api"
            }

            rpcConfig {
                serialization {
                    json()
                }
            }
        }.withService()
    }

    val service = serviceOrNull // for smart casting

    if (service != null) {
        var greeting by remember { mutableStateOf<String?>(null) }
        val news = remember { mutableStateListOf<String>() }

        LaunchedEffect(service) {
            greeting = service.hello("User from ${getPlatform().name} platform", UserData("Berlin", "Smith"))
        }

        LaunchedEffect(service) {
            streamScoped {
                service.subscribeToNews().collect { article ->
                    news.add(article)
                }
            }
        }

        MaterialTheme {
            var showIcon by remember { mutableStateOf(false) }

            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                greeting?.let {
                    Text(it)
                } ?: run {
                    Text("Establishing server connection...")
                }

                news.forEach {
                    Text("Article: $it")
                }

                Button(onClick = { showIcon = !showIcon }) {
                    Text("Click me!")
                }

                AnimatedVisibility(showIcon) {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painterResource(Res.drawable.compose_multiplatform), null)
                    }
                }
            }
        }
    }
}
