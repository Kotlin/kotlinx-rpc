/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import org.jetbrains.krpc.RPCClient
import org.jetbrains.krpc.client.withService
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
        val greeting = async(Ui) {
            service.hello("Alex", UserData("Berlin", "Smith"))
        }
        val news = async(Ui) { service.subscribeToNews() }
        data = WelcomeData(
            greeting.await(),
            news.await(),
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
