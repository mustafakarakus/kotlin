// !USE_EXPERIMENTAL: kotlin.ExperimentalStdlibApi
// TARGET_BACKEND: JVM
// IGNORE_BACKEND: JVM_IR
// IGNORE_BACKEND_FIR: JVM_IR
// WITH_RUNTIME

package test

import kotlin.reflect.typeOf
import kotlin.reflect.KTypeParameter
import kotlin.test.assertEquals

class Container<T>

fun <X> test() = typeOf<Container<X>>()

fun box(): String {
    val type = test<Any>()
    val x = type.arguments.single().type!!.classifier as KTypeParameter
    assertEquals("java.lang.Object? (Kotlin reflection is not available)", x.upperBounds.joinToString())

    return "OK"
}
