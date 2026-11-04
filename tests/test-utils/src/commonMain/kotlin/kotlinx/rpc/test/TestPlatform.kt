/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

/**
 * The platform that a test runs on, given by [currentPlatform].
 */
public enum class TestPlatform {
    JVM,
    JS,
    WASM_JS,
    WASM_WASI,
    IOS,
    MACOS,
    TVOS,
    WATCHOS,
    LINUX,
    MINGW,
    ANDROID_NATIVE,
}

/**
 * The platform that the current test runs on.
 *
 * Use it when a test must branch on the platform, for example when one backend doesn't support a
 * configured feature.
 */
public expect val currentPlatform: TestPlatform
