// FILE: A.java
// ANDROID_ANNOTATIONS

import kotlin.annotations.jvm.internal.*;

public class A {
    public void first(@DefaultValue("0x1F") Long value) {
    }

    public void second(@DefaultValue("0X1F") Long value) {
    }

    public void third(@DefaultValue("0b1010") Long value) {
    }

    public void fourth(@DefaultValue("0B1010") Long value) {
    }
}

// FILE: B.java
import kotlin.annotations.jvm.internal.*;

public class B {
    public void first(@DefaultValue("0x") Long value) {
    }

    public void second(@DefaultValue("0xZZ") Long value) {
    }

    public void third(@DefaultValue("0b") Long value) {
    }

    public void fourth(@DefaultValue("0B1234") Long value) {
    }
}

// FILE: test.kt
fun main(a: A, b: B) {
    a.first()
    a.second()
    a.third()
    a.fourth()

    b.<!INAPPLICABLE_CANDIDATE!>first<!>()
    b.<!INAPPLICABLE_CANDIDATE!>second<!>()
    b.<!INAPPLICABLE_CANDIDATE!>third<!>()
    b.<!INAPPLICABLE_CANDIDATE!>fourth<!>()
}

