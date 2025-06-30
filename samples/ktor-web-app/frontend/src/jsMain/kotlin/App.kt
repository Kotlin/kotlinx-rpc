/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.rpc.withService
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import react.useState

val App = FC<Props> {
    val rpcClient = initRpcClient()

    AppContainer {
        apiService = rpcClient.withService<MyService>()
    }
}

external interface AppContainerProps : Props {
    var apiService: MyService
}

val AppContainer = FC<AppContainerProps> { props ->
    val service by useState(props.apiService)
    var data by useState<WelcomeData?>(null)

    useEffectOnce {
        val greeting = service.hello("Alex", UserData("Berlin", "Smith"))
        data = WelcomeData(greeting)
    }

    data?.also { welcomeData ->
        Welcome {
            this.data = welcomeData
            this.news = service.subscribeToNews()
        }
    } ?: run {
        div {
            +"Loading data..."
        }
    }
}
