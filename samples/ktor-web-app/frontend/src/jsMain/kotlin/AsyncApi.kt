/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.coroutines.*
import react.EffectBuilder
import react.useEffectOnce

val Ui: CoroutineScope = MainScope()

fun launch(scope: CoroutineScope, start: CoroutineStart = CoroutineStart.DEFAULT, body: suspend () -> Unit): Job {
    with(scope) {
        return launch(start = start) {
            body()
        }
    }
}

fun useEffectOnceAsync(effect: suspend EffectBuilder.() -> Unit) {
    useEffectOnce {
        val job = launch(Ui) {
            effect()
        }

        cleanup {
            job.cancel()
        }
    }
}
