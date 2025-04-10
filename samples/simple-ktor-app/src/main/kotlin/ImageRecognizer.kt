/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext

@Suppress("ArrayInDataClass")
@Serializable
data class Image(val data: ByteArray) {
    @OptIn(ExperimentalStdlibApi::class)
    override fun toString(): String {
        return "Image(${data.joinToString("") { it.toHexString() }})"
    }
}

enum class Category {
    CAT, DOG
}

@Rpc
interface ImageRecognizer : RemoteService {
    fun currentlyProcessedImage(): Flow<Image?>

    suspend fun recognize(image: Image): Category

    fun recognizeAll(images: Flow<Image>): Flow<Category>
}

class ImageRecognizerService(override val coroutineContext: CoroutineContext) : ImageRecognizer {
    private val _currentlyProcessedImage: MutableStateFlow<Image?> = MutableStateFlow(null)

    override fun currentlyProcessedImage(): Flow<Image?> {
        return flow {
            _currentlyProcessedImage.collect { emit(it) }
        }
    }

    override suspend fun recognize(image: Image): Category {
        _currentlyProcessedImage.value = image
        val byte = image.data[0].toInt()
        delay(100) // heavy processing
        val result = if (byte == 0) Category.CAT else Category.DOG
        _currentlyProcessedImage.value = null
        return result
    }

    override fun recognizeAll(images: Flow<Image>): Flow<Category> {
        return images.map { recognize(it) }
    }
}
