/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.coroutines.delay

class ImageRecognizerImpl : ImageRecognizer {
    override suspend fun recognize(image: Image): RecogniseResult {
        val byte = image.data[0].toInt()
        delay(100) // heavy processing
        val result = RecogniseResult {
            category = if (byte == 0) 0 else 1
        }
        return result
    }
}
