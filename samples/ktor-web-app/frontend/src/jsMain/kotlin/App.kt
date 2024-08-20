/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.coroutines.flow.flow
import kotlinx.rpc.RPCClient
import kotlinx.rpc.withService
import kotlinx.rpc.streamScoped
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useState

val App = FC<Props> {
    var rpcClient by useState<RPCClient?>(null)

    useEffectOnceAsync {
        rpcClient = initRpcClient()
    }

    rpcClient?.also { client ->
        AppContainer {
            this.apiService = client.withService<MyService>()
        }
    } ?: run {
        div {
            +"Establishing connection..."
        }
    }
}

external interface AppContainerProps : Props {
    var apiService: MyService
}

val AppContainer = FC<AppContainerProps> { props ->
    val service by useState(props.apiService)
    var data by useState<WelcomeData?>(null)

    useEffectOnceAsync {
        val greeting = service.hello("Alex", UserData("Berlin", "Smith"))
        data = WelcomeData(
            greeting,
            flow {
                streamScoped {
                    service.subscribeToNews().collect {
                        emit(it)
                    }
                }
            },
        )
    }

    data?.also { welcomeData ->
        Welcome {
            this.data = welcomeData
        }
    } ?: run {
        div {
            +"Loading data..."
        }
    }
}
