package kotlinx.rpc.sample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform