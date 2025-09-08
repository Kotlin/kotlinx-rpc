/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

public expect abstract class ChannelCredentials
public expect abstract class ServerCredentials

public expect class InsecureChannelCredentials : ChannelCredentials
public expect class InsecureServerCredentials : ServerCredentials

public expect class TlsChannelCredentials : ChannelCredentials
public expect class TlsServerCredentials : ServerCredentials

public expect fun TlsChannelCredentials(): ChannelCredentials
public expect fun TlsServerCredentials(certChain: String, privateKey: String): ServerCredentials

public interface TlsChannelCredentialsBuilder {
    public fun trustManager(rootCertsPem: String): TlsChannelCredentialsBuilder
    public fun build(): ChannelCredentials
}

public interface TlsServerCredentialsBuilder {
    public fun keyManager(certChainPem: String, privateKeyPem: String): TlsServerCredentialsBuilder
    public fun build(): ServerCredentials
}


public expect fun TlsChannelCredentialsBuilder(): TlsChannelCredentialsBuilder
public expect fun TlsServerCredentialsBuilder(): TlsServerCredentialsBuilder
