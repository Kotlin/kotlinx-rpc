package com.example.ktorapplication.server

import com.example.ktorapplication.data.UserData
import kotlinx.coroutines.flow.Flow
import org.jetbrains.krpc.RPC

interface MyService : RPC {
    suspend fun hello(user: String, userData: UserData): String

    suspend fun subscribeToNews(): Flow<String>
}