/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package com.jetbrains.krpc.samples

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import org.jetbrains.krpc.RPC
import kotlin.coroutines.CoroutineContext

@Serializable
class Image(val data: ByteArray)

enum class Category {
    CAT, DOG
}

interface ImageRecognizer : RPC {
    val currentlyProcessedImage: StateFlow<Image?>

    suspend fun recognize(image: Image): Category

    suspend fun recognizeAll(images: Flow<Image>): Flow<Category>
}

class ImageRecognizerService : ImageRecognizer {
    override val coroutineContext: CoroutineContext = Job()

    override val currentlyProcessedImage: MutableStateFlow<Image?> = MutableStateFlow(null)

    override suspend fun recognize(image: Image): Category {
        currentlyProcessedImage.value = image
        val byte = image.data[0].toInt()
        delay(100) // heavy processing
        val result = if (byte == 0) Category.CAT else Category.DOG
        currentlyProcessedImage.value = null
        return result
    }

    override suspend fun recognizeAll(images: Flow<Image>): Flow<Category> {
        return images.map { recognize(it) }
    }
}
