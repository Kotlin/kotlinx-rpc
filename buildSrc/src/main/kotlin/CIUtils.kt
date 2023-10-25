package org.jetbrains.krpc.buildutils

@Suppress("unused")
val isCIBuild: Boolean by lazy {
    System.getenv("JB_SPACE_API_URL") != null
}
