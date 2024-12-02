/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.rpc.annotations.CheckedTypeAnnotation

// annotations

@CheckedTypeAnnotation
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.CLASS)
annotation class Checked

@Checked
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.CLASS)
annotation class SubChecked

@SubChecked
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.CLASS)
annotation class SubSubChecked

@CheckedTypeAnnotation
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.CLASS)
annotation class Checked2

// annotated

@Checked
open class AnnotatedChecked

class AnnotatedCheckedImpl : AnnotatedChecked()

@SubChecked
class AnnotatedSubChecked

@SubSubChecked
class AnnotatedSubSubChecked

@Checked2
class AnnotatedChecked2

@Checked
@Checked2
class AnnotatedCheckedMulti

// functions

fun <@Checked T> checked(arg: T) {}
fun <@SubChecked T> subChecked(arg: T) {}
fun <@SubSubChecked T> subSubChecked(arg: T) {}
fun <@Checked2 T> checked2(arg: T) {}
fun <@Checked @Checked2 T> checkedMulti(arg: T) {}
fun <@Checked T1, @Checked2 T2> checkedTwoParams(arg1: T1, arg2: T2) {}
fun <@Checked T1, T2> checkedTwoParamsSingleAnnotation(arg1: T1, arg2: T2) {}

// classes

open class CheckedClass<@Checked T>(arg: T)
@Checked
class CheckedClassChild<@Checked T>(arg: T) : CheckedClass<CheckedClassChild<T>>(CheckedClassChild(arg))
class CheckedClassChildFail<@Checked T>(arg: T) : CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>CheckedClassChildFail<T><!>>(CheckedClassChildFail(arg))
class CheckedClassChildFail2<T>(arg: T) : CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>CheckedClassChildFail2<T><!>>(CheckedClassChildFail2(arg))
class CheckedClassChildFail3<T>(arg: T) : CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>(arg)
class CheckedClassChildOk<@Checked T>(arg: T) : CheckedClass<T>(arg)
@Checked
class CheckedClassChildOk2<T>(arg: T) : CheckedClass<CheckedClassChildOk2<T>>(CheckedClassChildOk2(arg))

class SubCheckedClass<@SubChecked T>(arg: T)
class SubSubCheckedClass<@SubSubChecked T>(arg: T)
class Checked2Class<@Checked2 T>(arg: T)
class CheckedMultiClass<@Checked @Checked2 T>(arg: T)
class CheckedTwoParamsClass<@Checked T1, @Checked2 T2>(arg1: T1, arg2: T2)
class CheckedTwoParamsSingleAnnotationClass<@Checked T1, T2>(arg1: T1, arg2: T2)

// nested

class GenericClass<T>(arg: T)

fun <@Checked T : CheckedClass<T>> nestedCheckedOk() {}
fun <@Checked T : CheckedTwoParamsClass<T, *>> nestedCheckedThoParamsOk() {}
fun <@SubChecked T : CheckedClass<T>> nestedSubCheckedOk() {}
fun <@SubSubChecked T : CheckedClass<T>> nestedSubSubCheckedOk() {}
fun <@Checked T, C : CheckedClass<T>> nestedTwoCheckedOk() {}
fun <@Checked T, C : GenericClass<GenericClass<CheckedClass<T>>>> deeplyNestedTwoCheckedOk() {}
fun <T, C : CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>> nestedTwoCheckedFail() {}
fun <@Checked2 T : CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>> nestedChecked2Fail() {}
fun <T : CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>> nestedCheckedFail() {}

fun <@Checked T> nestedArgumetTypeCheckedOk(arg: CheckedClass<T>) {}
fun <T> nestedArgumetTypeCheckedFail(arg: CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>) {}

fun <@Checked T> doubleNestedArgumetTypeCheckedOk(arg: GenericClass<CheckedClass<T>>) {}
fun <T> doubleNestedArgumetTypeCheckedFail(arg: GenericClass<CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>>) {}

class NestedArgumetTypeCheckedOk<@Checked T>(arg: CheckedClass<T>) {}
class NestedArgumetTypeCheckedFail<T>(arg: CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>) {}

class DoubleNestedArgumetTypeCheckedOk<@Checked T> (arg: GenericClass<CheckedClass<T>>) {}
class DoubleNestedArgumetTypeCheckedFail<T>(arg: GenericClass<CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>>) {}

class CheckedNestedClass<@Checked T : CheckedClass<T>>()
class CheckedNestedClassFail<T : CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>>()

fun <@Checked T : CheckedNestedClass<T>> doubleNestedCheckedOk() {}
fun <T : CheckedNestedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>> doubleNestedCheckedFail() {}

fun <@Checked T : GenericClass<CheckedClass<T>>> tripleNestedCheckedOk() {}
fun <T : GenericClass<CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>>> tripleNestedCheckedFail() {}
fun <@Checked T : GenericClass<GenericClass<CheckedClass<T>>>> quadrupleNestedCheckedOk() {}
fun <T : GenericClass<GenericClass<CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>>>> quadrupleNestedCheckedFail() {}
fun <@SubSubChecked T : GenericClass<GenericClass<GenericClass<CheckedClass<T>>>>> quintupleNestedCheckedOk() {}
fun <T : GenericClass<GenericClass<GenericClass<CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>>>>> quintupleNestedCheckedFail() {}

fun main() {
    // ### Explicit types

    // functions
    checked<AnnotatedChecked>(AnnotatedChecked())
    checked<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedCheckedImpl<!>>(AnnotatedCheckedImpl())
    checked<AnnotatedSubChecked>(AnnotatedSubChecked())
    checked<AnnotatedSubSubChecked>(AnnotatedSubSubChecked())
    checked<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>>(AnnotatedChecked2())

    subChecked<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked<!>>(AnnotatedChecked())
    subChecked<AnnotatedSubChecked>(AnnotatedSubChecked())
    subChecked<AnnotatedSubSubChecked>(AnnotatedSubSubChecked())
    subChecked<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>>(AnnotatedChecked2())

    subSubChecked<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked<!>>(AnnotatedChecked())
    subSubChecked<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedSubChecked<!>>(AnnotatedSubChecked())
    subSubChecked<AnnotatedSubSubChecked>(AnnotatedSubSubChecked())
    subSubChecked<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>>(AnnotatedChecked2())

    checked2<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked<!>>(AnnotatedChecked())
    checked2<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedSubChecked<!>>(AnnotatedSubChecked())
    checked2<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedSubSubChecked<!>>(AnnotatedSubSubChecked())
    checked2<AnnotatedChecked2>(AnnotatedChecked2())

    checkedMulti<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked<!>>(AnnotatedChecked())
    checkedMulti<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>>(AnnotatedChecked2())
    checkedMulti<AnnotatedCheckedMulti>(AnnotatedCheckedMulti())

    checkedTwoParams<AnnotatedChecked, AnnotatedChecked2>(AnnotatedChecked(), AnnotatedChecked2())
    checkedTwoParams<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>, AnnotatedChecked2>(AnnotatedChecked2(), AnnotatedChecked2())
    checkedTwoParams<AnnotatedChecked, <!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked<!>>(AnnotatedChecked(), AnnotatedChecked())
    checkedTwoParams<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>, <!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked<!>>(AnnotatedChecked2(), AnnotatedChecked())

    checkedTwoParamsSingleAnnotation<AnnotatedChecked, AnnotatedChecked2>(AnnotatedChecked(), AnnotatedChecked2())
    checkedTwoParamsSingleAnnotation<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>, AnnotatedChecked2>(AnnotatedChecked2(), AnnotatedChecked2())
    checkedTwoParamsSingleAnnotation<AnnotatedChecked, AnnotatedChecked>(AnnotatedChecked(), AnnotatedChecked())
    checkedTwoParamsSingleAnnotation<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>, AnnotatedChecked>(AnnotatedChecked2(), AnnotatedChecked())

    nestedCheckedOk<CheckedClassChild<AnnotatedChecked>>()
    nestedCheckedOk<CheckedClassChild<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedCheckedImpl<!>>>()
    nestedTwoCheckedOk<AnnotatedChecked, CheckedClass<AnnotatedChecked>>()
    nestedTwoCheckedOk<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedCheckedImpl<!>, CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedCheckedImpl<!>>>()

    deeplyNestedTwoCheckedOk<AnnotatedChecked, GenericClass<GenericClass<CheckedClass<AnnotatedChecked>>>>()
    deeplyNestedTwoCheckedOk<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedCheckedImpl<!>, GenericClass<GenericClass<CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedCheckedImpl<!>>>>>()

    // classes
    CheckedClass<AnnotatedChecked>(AnnotatedChecked())
    CheckedClass<AnnotatedChecked>(AnnotatedCheckedImpl())
    CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedCheckedImpl<!>>(AnnotatedCheckedImpl())
    CheckedClass<AnnotatedSubChecked>(AnnotatedSubChecked())
    CheckedClass<AnnotatedSubSubChecked>(AnnotatedSubSubChecked())
    CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>>(AnnotatedChecked2())

    SubCheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked<!>>(AnnotatedChecked())
    SubCheckedClass<AnnotatedSubChecked>(AnnotatedSubChecked())
    SubCheckedClass<AnnotatedSubSubChecked>(AnnotatedSubSubChecked())
    SubCheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>>(AnnotatedChecked2())

    SubSubCheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked<!>>(AnnotatedChecked())
    SubSubCheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedSubChecked<!>>(AnnotatedSubChecked())
    SubSubCheckedClass<AnnotatedSubSubChecked>(AnnotatedSubSubChecked())
    SubSubCheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>>(AnnotatedChecked2())

    Checked2Class<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked<!>>(AnnotatedChecked())
    Checked2Class<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedSubChecked<!>>(AnnotatedSubChecked())
    Checked2Class<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedSubSubChecked<!>>(AnnotatedSubSubChecked())
    Checked2Class<AnnotatedChecked2>(AnnotatedChecked2())

    CheckedMultiClass<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked<!>>(AnnotatedChecked())
    CheckedMultiClass<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>>(AnnotatedChecked2())
    CheckedMultiClass<AnnotatedCheckedMulti>(AnnotatedCheckedMulti())

    CheckedTwoParamsClass<AnnotatedChecked, AnnotatedChecked2>(AnnotatedChecked(), AnnotatedChecked2())
    CheckedTwoParamsClass<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>, AnnotatedChecked2>(AnnotatedChecked2(), AnnotatedChecked2())
    CheckedTwoParamsClass<AnnotatedChecked, <!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked<!>>(AnnotatedChecked(), AnnotatedChecked())
    CheckedTwoParamsClass<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>, <!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked<!>>(AnnotatedChecked2(), AnnotatedChecked())

    CheckedTwoParamsSingleAnnotationClass<AnnotatedChecked, AnnotatedChecked2>(AnnotatedChecked(), AnnotatedChecked2())
    CheckedTwoParamsSingleAnnotationClass<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>, AnnotatedChecked2>(AnnotatedChecked2(), AnnotatedChecked2())
    CheckedTwoParamsSingleAnnotationClass<AnnotatedChecked, AnnotatedChecked>(AnnotatedChecked(), AnnotatedChecked())
    CheckedTwoParamsSingleAnnotationClass<<!CHECKED_ANNOTATION_VIOLATION!>AnnotatedChecked2<!>, AnnotatedChecked>(AnnotatedChecked2(), AnnotatedChecked())

    // ### implicit types

    // functions

    checked(AnnotatedChecked())
    <!CHECKED_ANNOTATION_VIOLATION!>checked<!>(AnnotatedCheckedImpl())
    checked(AnnotatedSubChecked())
    checked(AnnotatedSubSubChecked())
    <!CHECKED_ANNOTATION_VIOLATION!>checked<!>(AnnotatedChecked2())

    // classes

    CheckedClass(AnnotatedChecked())
    <!CHECKED_ANNOTATION_VIOLATION!>CheckedClass<!>(AnnotatedCheckedImpl())
    CheckedClass(AnnotatedSubChecked())
    CheckedClass(AnnotatedSubSubChecked())
    <!CHECKED_ANNOTATION_VIOLATION!>CheckedClass<!>(AnnotatedChecked2())

    // ### partially implicit types

    // functions

    checkedTwoParams<AnnotatedChecked, _>(AnnotatedChecked(), AnnotatedChecked2())
    checkedTwoParams<_, AnnotatedChecked2>(AnnotatedChecked(), AnnotatedChecked2())
    checkedTwoParams<<!CHECKED_ANNOTATION_VIOLATION!>_<!>, AnnotatedChecked2>(AnnotatedChecked2(), AnnotatedChecked2())
    checkedTwoParams<AnnotatedChecked, <!CHECKED_ANNOTATION_VIOLATION!>_<!>>(AnnotatedChecked(), AnnotatedChecked())
    checkedTwoParams<<!CHECKED_ANNOTATION_VIOLATION!>_<!>, <!CHECKED_ANNOTATION_VIOLATION!>_<!>>(AnnotatedChecked2(), AnnotatedChecked())

    // classes

    CheckedTwoParamsClass<AnnotatedChecked, _>(AnnotatedChecked(), AnnotatedChecked2())
    CheckedTwoParamsClass<_, AnnotatedChecked2>(AnnotatedChecked(), AnnotatedChecked2())
    CheckedTwoParamsClass<<!CHECKED_ANNOTATION_VIOLATION!>_<!>, AnnotatedChecked2>(AnnotatedChecked2(), AnnotatedChecked2())
    CheckedTwoParamsClass<AnnotatedChecked, <!CHECKED_ANNOTATION_VIOLATION!>_<!>>(AnnotatedChecked(), AnnotatedChecked())
    CheckedTwoParamsClass<<!CHECKED_ANNOTATION_VIOLATION!>_<!>, <!CHECKED_ANNOTATION_VIOLATION!>_<!>>(AnnotatedChecked2(), AnnotatedChecked())
}

fun <@Checked T, C : CheckedClass<T>> nestedTwoWithArgCheckedOk(arg: C) {}
fun <@Checked T, C : GenericClass<GenericClass<CheckedClass<T>>>> deeplyNestedTwoWithArgCheckedOk(arg: C) {}

fun <@Checked T> unknownTypeOk(arg: T) {
    checked<T>(arg)
    checked(arg)
    CheckedClass<T>(arg)
    CheckedClass(arg)
    nestedTwoCheckedOk<T, CheckedClass<T>>()
    nestedTwoCheckedOk<T, _>()
    deeplyNestedTwoCheckedOk<T, GenericClass<GenericClass<CheckedClass<T>>>>()
    deeplyNestedTwoCheckedOk<T, _>()
    nestedTwoWithArgCheckedOk(CheckedClass(arg))
    deeplyNestedTwoWithArgCheckedOk(GenericClass(GenericClass(CheckedClass(arg))))
}

fun <T> unknownTypeFail(arg: T) {
    checked<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>(arg)
}

fun <T> unknownTypeFail2(arg: T) {
    <!CHECKED_ANNOTATION_VIOLATION!>checked<!>(arg)
}

fun <T> unknownTypeFail3(arg: T) {
    CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>(arg)
}

fun <T> unknownTypeFail4(arg: T) {
    <!CHECKED_ANNOTATION_VIOLATION!>CheckedClass<!>(arg)
}

fun <T> unknownTypeFail5(arg: T) {
    nestedTwoCheckedOk<<!CHECKED_ANNOTATION_VIOLATION!>T<!>, CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>>()
}

fun <T> unknownTypeFail6(arg: T) {
    nestedTwoCheckedOk<<!CHECKED_ANNOTATION_VIOLATION!>T<!>, <!CHECKED_ANNOTATION_VIOLATION!>_<!>>()
}

fun <T> unknownTypeFail7(arg: T) {
    <!CHECKED_ANNOTATION_VIOLATION, CHECKED_ANNOTATION_VIOLATION!>nestedTwoWithArgCheckedOk<!>(<!CHECKED_ANNOTATION_VIOLATION!>CheckedClass<!>(arg))
}

fun <T> unknownTypeFail8(arg: T) {
    deeplyNestedTwoCheckedOk<<!CHECKED_ANNOTATION_VIOLATION!>T<!>, GenericClass<GenericClass<CheckedClass<<!CHECKED_ANNOTATION_VIOLATION!>T<!>>>>>()
}

fun <T> unknownTypeFail9(arg: T) {
    deeplyNestedTwoCheckedOk<<!CHECKED_ANNOTATION_VIOLATION!>T<!>, <!CHECKED_ANNOTATION_VIOLATION!>_<!>>()
}

fun <T> unknownTypeFail10(arg: T) {
    <!CHECKED_ANNOTATION_VIOLATION, CHECKED_ANNOTATION_VIOLATION!>deeplyNestedTwoWithArgCheckedOk<!>(<!CHECKED_ANNOTATION_VIOLATION!>GenericClass<!>(<!CHECKED_ANNOTATION_VIOLATION!>GenericClass<!>(<!CHECKED_ANNOTATION_VIOLATION!>CheckedClass<!>(arg))))
}
