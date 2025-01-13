/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.sample.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.rpc.sample.data.createRpcClient
import kotlinx.rpc.sample.ui.state.WelcomeData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.rpc.RpcClient
import kotlinx.rpc.krpc.streamScoped
import kotlinx.rpc.withService
import kotlinx.rpc.sample.MyService
import kotlinx.rpc.sample.UserData

class AppViewModel : ViewModel() {
    private var rpcClient: RpcClient? = null
    private var apiService: MyService? = null

    private val _uiState = MutableStateFlow<WelcomeData?>(null)
    val uiState: StateFlow<WelcomeData?> = _uiState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            rpcClient = createRpcClient()
            rpcClient?.let {
                apiService = it.withService()
                fetchData()
            }
        }
    }

    private fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            streamScoped {
                delay(2000)
                val greetingDeferred = async {
                    apiService?.hello(
                        "Alex",
                        UserData("Berlin", "Smith")
                    )
                }
                val newsDeferred = async { apiService?.subscribeToNews() }

                val serverGreeting = greetingDeferred.await()
                val news = newsDeferred.await()

                val allNews: MutableList<String> = mutableListOf()
                news?.collect {
                    allNews += it

                    val sendNews = allNews.toMutableList() // fix ConcurrentModificationException
                    serverGreeting?.let {
                        _uiState.value = WelcomeData(serverGreeting, sendNews)
                    }
                }
            }
        }
    }
}
