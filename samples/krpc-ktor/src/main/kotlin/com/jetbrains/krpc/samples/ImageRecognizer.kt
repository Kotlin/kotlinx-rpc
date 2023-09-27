package com.jetbrains.krpc.samples

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
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
    suspend fun recognize(image: Image): Category

    suspend fun recognizeAll(images: Flow<Image>): Flow<Category>
}

class ImageRecognizerService : ImageRecognizer {
    override val coroutineContext: CoroutineContext = Job()

    override suspend fun recognize(image: Image): Category {
        val byte = image.data[0].toInt()
        return if (byte == 0) Category.CAT else Category.DOG
    }

    override suspend fun recognizeAll(images: Flow<Image>): Flow<Category> {
        return images.map { recognize(it) }
    }
}
