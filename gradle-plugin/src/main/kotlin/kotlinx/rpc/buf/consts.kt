/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.buf

import org.gradle.api.artifacts.Configuration

/**
 * Name of the buf.gen.yaml file.
 *
 * @see <a href="https://buf.build/docs/configuration/v2/buf-gen-yaml/">buf.gen.yaml reference</a>
 */
public const val BUF_GEN_YAML: String = "buf.gen.yaml"

/**
 * Name of the buf.yaml file.
 *
 * @see <a href="https://buf.build/docs/configuration/v2/buf-yaml/">buf.yaml reference</a>
 */
public const val BUF_YAML: String = "buf.yaml"

/**
 * [Configuration] name for buf executable.
 *
 * @see <a href="https://buf.build/docs/">Buf</a>
 */
public const val BUF_EXECUTABLE_CONFIGURATION: String = "bufExecutable"
