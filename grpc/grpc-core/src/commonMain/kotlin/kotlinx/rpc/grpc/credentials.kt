/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

public expect abstract class ChannelCredentials

public expect class InsecureChannelCredentials : ChannelCredentials

public expect class TlsChannelCredentials : ChannelCredentials


public expect fun InsecureChannelCredentials(): ChannelCredentials
public expect fun TlsChannelCredentials(): ChannelCredentials