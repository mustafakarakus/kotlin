// !USE_EXPERIMENTAL: kotlin.ExperimentalStdlibApi
// IGNORE_BACKEND: JVM_IR, JS, JS_IR, NATIVE
// IGNORE_BACKEND_FIR: JVM_IR
// WITH_REFLECT

package test

import kotlin.reflect.typeOf
import kotlin.reflect.KTypeParameter
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class Container<T>

fun <X> createX(): KTypeParameter =
    typeOf<Container<X>>().arguments.single().type!!.classifier as KTypeParameter

fun <X> createOtherX(): KTypeParameter =
    typeOf<Container<X>>().arguments.single().type!!.classifier as KTypeParameter

fun box(): String {
    assertEquals(createX<Any>(), createX<Any>())
    assertEquals(createX<Any>().hashCode(), createX<Any>().hashCode())

    assertNotEquals(createX<Any>(), createOtherX<Any>())
    return "OK"
}
