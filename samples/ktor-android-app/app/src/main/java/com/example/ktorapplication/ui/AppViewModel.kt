/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.example.ktorapplication.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.MyService
import com.example.common.UserData
import com.example.ktorapplication.data.createRpcClient
import com.example.ktorapplication.ui.state.WelcomeData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.krpc.RPCClient
import org.jetbrains.krpc.client.withService

class AppViewModel : ViewModel() {
    private var rpcClient: RPCClient? = null
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