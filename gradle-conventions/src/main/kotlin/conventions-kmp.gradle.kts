import util.configureKotlin
import util.optInForInternalKRPCApi
import kotlin.reflect.KProperty

plugins {
    id("conventions-common")
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    optInForInternalKRPCApi()
}

class OptionalProperty(private val target: Project) {
    operator fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        val propName = "krpc.kmp.${property.name}"
        return when {
            target.hasProperty(propName) -> (target.properties[propName] as String).toBoolean()
            else -> false
        }
    }
}

fun Project.optionalProperty(): OptionalProperty {
    return OptionalProperty(this)
}

val excludeJs: Boolean by optionalProperty()
val excludeJvm: Boolean by optionalProperty()
val excludeNative: Boolean by optionalProperty()

configureKotlin(
    jvm = !excludeJvm,
    js = !excludeJs,
    native = !excludeNative,
)
