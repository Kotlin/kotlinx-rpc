/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protoc.gen.test

import kotlinx.rpc.protoc.gen.core.FqNameTable
import kotlinx.rpc.protoc.gen.core.Platform
import kotlinx.rpc.protoc.gen.core.model.FqName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

private fun FqNameTable.scoped(packageFqName: FqName.Package) = scoped(packageFqName, mutableSetOf())

class FqNameTableTest {
    @Test
    fun testSamePackageUsesSimpleName() = runTableTest {
        val name = fq("com.example", "MyClass")
        table.register(name)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("MyClass", scoped.resolve(name))
        assertTrue(scoped.requiredImports.isEmpty())
    }

    @Test
    fun testDifferentPackageAddsImport() = runTableTest {
        val name = fq("com.other", "OtherClass")
        table.register(name)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("OtherClass", scoped.resolve(name))
        assertEquals(setOf("com.other.OtherClass"), scoped.requiredImports)
    }

    @Test
    fun testDifferentPackageConflictImport() = runTableTest {
        val other = fq("com.other", "OtherClass")
        val another = fq("com.another", "OtherClass")
        table.register(other)
        table.register(another)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("OtherClass", scoped.resolve(other))
        assertEquals(setOf("com.other.OtherClass"), scoped.requiredImports)
        assertEquals("com.another.OtherClass", scoped.resolve(another))
        assertEquals(setOf("com.other.OtherClass"), scoped.requiredImports)
    }

    @Test
    fun testImplicitImportUsesSimpleName()= runTableTest {
        // String is implicitly imported from kotlin package - no registration needed
        val name = fq("kotlin", "String")

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("String", scoped.resolve(name))
        assertTrue(scoped.requiredImports.isEmpty())
    }

    @Test
    fun testConflictWithImplicitImportUsesFqn()= runTableTest {
        val myStringOther = fq("com.other", "String")
        table.register(myStringOther)

        val kotlinString = fq("kotlin", "String")

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        // First, resolve kotlin.String (implicit)
        assertEquals("String", scoped.resolve(kotlinString))

        // com.other.String must use FQN because "String" is already resolved to kotlin.String
        assertEquals("com.other.String", scoped.resolve(myStringOther))
    }

    @Test
    fun testScopeNameShadowingUsesFqn()= runTableTest {
        val packageString = fq("com.example", "String")
        table.register(packageString)

        // Package-level "String" shadows kotlin.String
        val scoped = table.scoped(FqName.Package.fromString("com.example"))

        val kotlinString = fq("kotlin", "String")
        assertEquals("kotlin.String", scoped.resolve(kotlinString))
    }

    @Test
    fun testNestedClassWithinSamePackage()= runTableTest {
        val nested = fq("com.example", "MyClass.NestedClass")
        table.register(nested)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("MyClass.NestedClass", scoped.resolve(nested))
        assertTrue(scoped.requiredImports.isEmpty())
    }

    @Test
    fun testNestedClassFromDifferentPackage()= runTableTest {
        val nested = fq("com.other", "OuterClass.InnerClass")
        table.register(nested)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("OuterClass.InnerClass", scoped.resolve(nested))
        assertEquals(setOf("com.other.OuterClass"), scoped.requiredImports)
    }

    @Test
    fun testNestedClassNameNotShadowing()= runTableTest {
        val myString = fq("com.example", "MyClass.String")
        table.register(myString)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        // MyClass.String uses package-relative name (not just "String")
        assertEquals("MyClass.String", scoped.resolve(myString))

        // kotlin.String can still use simple name (implicit)
        val kotlinString = fq("kotlin", "String")
        assertEquals("String", scoped.resolve(kotlinString))

        assertTrue(scoped.requiredImports.isEmpty())
    }

    @Test
    fun testNestedClassNameShadowingInProperty()= runTableTest {
        val myString = fq("com.example", "MyClass.String")
        val myClass = fq("com.example", "MyClass")
        table.register(myString)
        table.register(myClass)

        // At package level
        val packageScoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("MyClass.String", packageScoped.resolve(myString))

        // Inside MyClass, "String" refers to the sibling class
        val insideMyClass = packageScoped.nested("MyClass")
        assertEquals("String", insideMyClass.resolve(myString))

        // kotlin.String is shadowed
        val kotlinString = fq("kotlin", "String")
        assertEquals("kotlin.String", insideMyClass.resolve(kotlinString))

        // Inside MyClass.String, "String" refers to the class itself
        val insideMyString = insideMyClass.nested("String")
        assertEquals("String", insideMyString.resolve(myString))

        // kotlin.String is shadowed
        assertEquals("kotlin.String", insideMyClass.resolve(kotlinString))
    }

    @Test
    fun testWithScopeNames()= runTableTest {
        val name1 = fq("com.other", "OtherClass")
        val myClass = fq("com.example", "MyClass")
        val packageString = fq("com.example", "String")
        table.register(name1)
        table.register(myClass)
        table.register(packageString)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("OtherClass", scoped.resolve(name1))
        assertEquals(setOf("com.other.OtherClass"), scoped.requiredImports)

        // kotlin.String is shadowed by package-level String
        val kotlinString = fq("kotlin", "String")
        assertEquals("kotlin.String", scoped.resolve(kotlinString))
    }

    @Test
    fun testMultipleUsagesOfSameName() = runTableTest {
        val kotlinString1 = fq("kotlin", "String")
        val kotlinString2 = fq("kotlin", "String")

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("String", scoped.resolve(kotlinString1))
        assertEquals("String", scoped.resolve(kotlinString2))
        assertTrue(scoped.requiredImports.isEmpty())
    }

    @Test
    fun testJvmPlatformImplicitImports()= runTableTest(Platform.Jvm) {
        val javaObject = fq("java.lang", "Object")

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("Object", scoped.resolve(javaObject))
        assertTrue(scoped.requiredImports.isEmpty())
    }

    @Test
    fun testJsPlatformImplicitImports() = runTableTest(Platform.Js) {
        val console = fq("kotlin.js", "Console")

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("Console", scoped.resolve(console))
        assertTrue(scoped.requiredImports.isEmpty())
    }

    @Test
    fun testDeepNestedClasses()  = runTableTest {
        val deepNested = fq("com.example", "A.B.C.D")
        table.register(deepNested)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("A.B.C.D", scoped.resolve(deepNested))
        assertTrue(scoped.requiredImports.isEmpty())
    }

    @Test
    fun testConflictingNestedClasses() = runTableTest {
        val myClass = fq("com.example", "MyClass")
        val inner1 = fq("com.example", "MyClass.Inner")
        val myClass2 = fq("com.other", "MyClass")
        val inner2 = fq("com.other", "MyClass.Inner")
        table.register(myClass)
        table.register(inner1)
        table.register(myClass2)
        table.register(inner2)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("MyClass.Inner", scoped.resolve(inner1))
        assertEquals("com.other.MyClass.Inner", scoped.resolve(inner2))
        assertEquals("com.other.MyClass", scoped.resolve(myClass2))

        assertTrue(scoped.requiredImports.isEmpty())
    }

    @Test
    fun testDeepNestedScopesWithMultipleLevels()  = runTableTest {
        val myClass = fq("com.example", "MyClass")
        val myClassString = fq("com.example", "MyClass.String")
        val level8Path = fq("com.example", "MyClass.String.String.String.String.Int.Float.String")
        val level4Path = fq("com.example", "MyClass.String.String.String")
        table.register(myClass)
        table.register(myClassString)
        table.register(level8Path)
        table.register(level4Path)

        // Inside deeply nested class
        val scoped = table.scoped(FqName.Package.fromString("com.example"))
            .nested("MyClass")
            .nested("String")
            .nested("String")
            .nested("String")
            .nested("String")
            .nested("Int")
            .nested("Float")
            .nested("String")

        assertEquals("MyClass", scoped.resolve(myClass))
        assertEquals("MyClass.String", scoped.resolve(myClassString))
        assertEquals("String", scoped.resolve(level8Path))
        assertEquals("MyClass.String.String.String", scoped.resolve(level4Path))

        val kotlinString = fq("kotlin", "String")
        val kotlinInt = fq("kotlin", "Int")
        val kotlinFloat = fq("kotlin", "Float")

        assertEquals("kotlin.String", scoped.resolve(kotlinString))
        assertEquals("kotlin.Int", scoped.resolve(kotlinInt))
        assertEquals("kotlin.Float", scoped.resolve(kotlinFloat))

        assertTrue(scoped.requiredImports.isEmpty())
    }

    @Test
    fun testNestedClassWithSameNameAsPackageLevelClass() = runTableTest {
        val myClass = fq("com.example", "MyClass")
        val packageString = fq("com.example", "String")
        val nestedString = fq("com.example", "MyClass.String")
        table.register(myClass)
        table.register(packageString)
        table.register(nestedString)

        val packageScoped = table.scoped(FqName.Package.fromString("com.example"))

        assertEquals("MyClass", packageScoped.resolve(myClass))
        assertEquals("String", packageScoped.resolve(packageString))

        val kotlinString = fq("kotlin", "String")
        assertEquals("kotlin.String", packageScoped.resolve(kotlinString))

        assertEquals("MyClass.String", packageScoped.resolve(nestedString))

        // Inside MyClass.String, the nested String shadows the package-level one
        val insideNestedString = packageScoped.nested("MyClass").nested("String")
        assertEquals("String", insideNestedString.resolve(nestedString))
        assertEquals("com.example.String", insideNestedString.resolve(packageString))
        assertEquals("kotlin.String", insideNestedString.resolve(kotlinString))

        assertTrue(insideNestedString.requiredImports.isEmpty())
    }

    @Test
    fun testMultiplePackageLevelClassesShadowingImplicits() = runTableTest {
        val packageString = fq("com.example", "String")
        val packageInt = fq("com.example", "Int")
        val myClass = fq("com.example", "MyClass")
        table.register(packageString)
        table.register(packageInt)
        table.register(myClass)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))

        assertEquals("String", scoped.resolve(packageString))
        assertEquals("Int", scoped.resolve(packageInt))
        assertEquals("MyClass", scoped.resolve(myClass))

        val kotlinString = fq("kotlin", "String")
        val kotlinInt = fq("kotlin", "Int")

        assertEquals("kotlin.String", scoped.resolve(kotlinString))
        assertEquals("kotlin.Int", scoped.resolve(kotlinInt))

        assertTrue(scoped.requiredImports.isEmpty())
    }

    @Test
    fun testNestedClassShadowsImplicitInDeeperNesting() = runTableTest {
        // class MyClass {
        //     class String
        //     class Nested {
        //         val s: String // MyClass.String
        //     }
        // }

        val myClass = fq("com.example", "MyClass")
        val myClassString = fq("com.example", "MyClass.String")
        val myClassNested = fq("com.example", "MyClass.Nested")
        table.register(myClass)
        table.register(myClassString)
        table.register(myClassNested)

        // Inside MyClass.Nested, String is a sibling (child of enclosing MyClass)
        // Sibling classes are automatically detected from registered names
        val insideNested = table.scoped(FqName.Package.fromString("com.example"))
            .nested("MyClass")
            .nested("Nested")

        // MyClass.String is accessible as "String" because it's a sibling
        assertEquals("String", insideNested.resolve(myClassString))

        // kotlin.String must use FQN because String is visible/shadowed
        val kotlinString = fq("kotlin", "String")
        assertEquals("kotlin.String", insideNested.resolve(kotlinString))

        assertTrue(insideNested.requiredImports.isEmpty())
    }

    @Test
    fun testNestedClassShadowsImplicitInDeeperNestingOnMultipleLevels() = runTableTest {
        // class MyClass {
        //     class String {
        //         class String {
        //              val innermost: String // MyClass.String.String
        //         }
        //         val nestedTwice: String // MyClass.String.String
        //     }
        //     val nestedOnce: String // MyClass.String
        //     val nestedOnce2: String.String // MyClass.String.String
        // }

        val myClass = fq("com.example", "MyClass")
        val myClassString = fq("com.example", "MyClass.String")
        val myClassStringString = fq("com.example", "MyClass.String.String")
        table.register(myClass)
        table.register(myClassString)
        table.register(myClassStringString)

        val kotlinString = fq("kotlin", "String")

        val packageLevelScope = table.scoped(FqName.Package.fromString("com.example"))

        assertEquals("String", packageLevelScope.resolve(kotlinString))
        assertEquals("MyClass.String", packageLevelScope.resolve(myClassString))
        assertEquals("MyClass.String.String", packageLevelScope.resolve(myClassStringString))

        val insideMyClass = packageLevelScope.nested("MyClass")

        assertEquals("kotlin.String", insideMyClass.resolve(kotlinString))
        assertEquals("String", insideMyClass.resolve(myClassString))
        assertEquals("String.String", insideMyClass.resolve(myClassStringString))

        val insideMyClassString = insideMyClass.nested("String")

        assertEquals("kotlin.String", insideMyClassString.resolve(kotlinString))
        assertEquals("MyClass.String", insideMyClassString.resolve(myClassString))
        assertEquals("String", insideMyClassString.resolve(myClassStringString))

        val insideMyClassStringString = insideMyClassString.nested("String")

        assertEquals("kotlin.String", insideMyClassStringString.resolve(kotlinString))
        assertEquals("MyClass.String", insideMyClassStringString.resolve(myClassString))
        assertEquals("String", insideMyClassStringString.resolve(myClassStringString))
    }

    @Test
    fun testNestedClassShadowsImplicitInDeeperNestingOnMultipleLevelsAndSeparatePaths() = runTableTest {
        // class MyClass {
        //     class A {
        //         class B
        //     }
        //
        //     class Int {
        //         class String {
        //             class Inner {
        //                 val innermost: Int // MyClass.String.String.Int
        //             }
        //
        //             class Int
        //         }
        //
        //         val nestedTwice: Int // MyClass.Int
        //         val nestedTwice2: A.B // MyClass.A.B
        //     }
        //
        //     val nested: Int.String // MyClass.Int.String
        // }

        val myClass = fq("com.example", "MyClass")
        val myClassInt = fq("com.example", "MyClass.Int")
        val myClassIntString = fq("com.example", "MyClass.Int.String")
        val myClassIntStringInner = fq("com.example", "MyClass.Int.String.Inner")
        val myClassIntStringInt = fq("com.example", "MyClass.Int.String.Int")
        val myClassA = fq("com.example", "MyClass.A")
        val myClassAB = fq("com.example", "MyClass.A.B")
        table.register(myClass)
        table.register(myClassInt)
        table.register(myClassIntString)
        table.register(myClassIntStringInner)
        table.register(myClassIntStringInt)
        table.register(myClassA)
        table.register(myClassAB)

        val kotlinString = fq("kotlin", "String")
        val kotlinInt = fq("kotlin", "Int")

        val packageLevelScope = table.scoped(FqName.Package.fromString("com.example"))

        assertEquals("String", packageLevelScope.resolve(kotlinString))
        assertEquals("Int", packageLevelScope.resolve(kotlinInt))
        assertEquals("MyClass", packageLevelScope.resolve(myClass))
        assertEquals("MyClass.Int", packageLevelScope.resolve(myClassInt))
        assertEquals("MyClass.Int.String", packageLevelScope.resolve(myClassIntString))
        assertEquals("MyClass.Int.String.Inner", packageLevelScope.resolve(myClassIntStringInner))
        assertEquals("MyClass.Int.String.Int", packageLevelScope.resolve(myClassIntStringInt))
        assertEquals("MyClass.A", packageLevelScope.resolve(myClassA))
        assertEquals("MyClass.A.B", packageLevelScope.resolve(myClassAB))

        val insideMyClass = packageLevelScope.nested("MyClass")

        assertEquals("String", insideMyClass.resolve(kotlinString))
        assertEquals("kotlin.Int", insideMyClass.resolve(kotlinInt))
        assertEquals("MyClass", insideMyClass.resolve(myClass))
        assertEquals("Int", insideMyClass.resolve(myClassInt))
        assertEquals("Int.String", insideMyClass.resolve(myClassIntString))
        assertEquals("Int.String.Inner", insideMyClass.resolve(myClassIntStringInner))
        assertEquals("Int.String.Int", insideMyClass.resolve(myClassIntStringInt))
        assertEquals("A", insideMyClass.resolve(myClassA))
        assertEquals("A.B", insideMyClass.resolve(myClassAB))

        val insideMyClassInt = insideMyClass.nested("Int")

        assertEquals("kotlin.String", insideMyClassInt.resolve(kotlinString))
        assertEquals("kotlin.Int", insideMyClassInt.resolve(kotlinInt))
        assertEquals("MyClass", insideMyClassInt.resolve(myClass))
        assertEquals("Int", insideMyClassInt.resolve(myClassInt))
        assertEquals("String", insideMyClassInt.resolve(myClassIntString))
        assertEquals("String.Inner", insideMyClassInt.resolve(myClassIntStringInner))
        assertEquals("String.Int", insideMyClassInt.resolve(myClassIntStringInt))
        assertEquals("A", insideMyClassInt.resolve(myClassA))
        assertEquals("A.B", insideMyClassInt.resolve(myClassAB))

        val insideMyClassIntString = insideMyClassInt.nested("String")

        assertEquals("kotlin.String", insideMyClassIntString.resolve(kotlinString))
        assertEquals("kotlin.Int", insideMyClassIntString.resolve(kotlinInt))
        assertEquals("MyClass", insideMyClassIntString.resolve(myClass))
        assertEquals("MyClass.Int", insideMyClassIntString.resolve(myClassInt))
        assertEquals("String", insideMyClassIntString.resolve(myClassIntString))
        assertEquals("Inner", insideMyClassIntString.resolve(myClassIntStringInner))
        assertEquals("Int", insideMyClassIntString.resolve(myClassIntStringInt))
        assertEquals("A", insideMyClassIntString.resolve(myClassA))
        assertEquals("A.B", insideMyClassIntString.resolve(myClassAB))

        val insideMyClassIntStringInner = insideMyClassIntString.nested("Inner")

        assertEquals("kotlin.String", insideMyClassIntStringInner.resolve(kotlinString))
        assertEquals("kotlin.Int", insideMyClassIntStringInner.resolve(kotlinInt))
        assertEquals("MyClass", insideMyClassIntStringInner.resolve(myClass))
        assertEquals("MyClass.Int", insideMyClassIntStringInner.resolve(myClassInt))
        assertEquals("String", insideMyClassIntStringInner.resolve(myClassIntString))
        assertEquals("Inner", insideMyClassIntStringInner.resolve(myClassIntStringInner))
        assertEquals("Int", insideMyClassIntStringInner.resolve(myClassIntStringInt))
        assertEquals("A", insideMyClassIntStringInner.resolve(myClassA))
        assertEquals("A.B", insideMyClassIntStringInner.resolve(myClassAB))
    }

    @Test
    fun testUnregisteredNameFails() = runTableTest {
        val unregistered = fq("com.example", "Unregistered")

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertFailsWith<IllegalArgumentException> {
            scoped.resolve(unregistered)
        }
    }

    @Test
    fun testSiblingClassAccessFromEnclosingScope() = runTableTest {
        val myClass = fq("com.example", "MyClass")
        val sibling1 = fq("com.example", "MyClass.Sibling1")
        val sibling2 = fq("com.example", "MyClass.Sibling2")
        table.register(myClass)
        table.register(sibling1)
        table.register(sibling2)

        // Inside MyClass.Sibling1, we can access Sibling2 by simple name
        val insideSibling1 = table.scoped(FqName.Package.fromString("com.example"))
            .nested("MyClass")
            .nested("Sibling1")

        // Sibling2 is accessible as "Sibling2" because its parent (MyClass) is enclosing us
        assertEquals("Sibling2", insideSibling1.resolve(sibling2))
    }

    @Test
    fun testNativePlatformImplicitImports() = runTableTest(Platform.Native) {
        // Native platform uses only common implicit imports (same as Common)
        // kotlin.String is implicitly available
        val kotlinString = fq("kotlin", "String")

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("String", scoped.resolve(kotlinString))
        assertTrue(scoped.requiredImports.isEmpty())

        // java.lang types are NOT implicitly available on Native (unlike JVM)
        val javaObject = fq("java.lang", "Object")
        table.register(javaObject)
        assertEquals("Object", scoped.resolve(javaObject))
        assertEquals(setOf("java.lang.Object"), scoped.requiredImports)
    }

    @Test
    fun testWasmPlatformImplicitImports() = runTableTest(Platform.Wasm) {
        // Wasm platform uses only common implicit imports (same as Common)
        // kotlin.Int is implicitly available
        val kotlinInt = fq("kotlin", "Int")

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("Int", scoped.resolve(kotlinInt))
        assertTrue(scoped.requiredImports.isEmpty())

        // kotlin.js types are NOT implicitly available on Wasm (unlike JS)
        val console = fq("kotlin.js", "Console")
        table.register(console)
        assertEquals("Console", scoped.resolve(console))
        assertEquals(setOf("kotlin.js.Console"), scoped.requiredImports)
    }

    @Test
    fun testNestedClassFromDifferentPackageShadowingImplicit() = runTableTest {
        // class MyClass {
        //     class String
        // }
        // in com.other package
        // Accessing from com.example.Outer.Inner

        val otherMyClass = fq("com.other", "MyClass")
        val otherMyClassString = fq("com.other", "MyClass.String")
        val outer = fq("com.example", "Outer")
        val inner = fq("com.example", "Outer.Inner")
        table.register(otherMyClass)
        table.register(otherMyClassString)
        table.register(outer)
        table.register(inner)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))

        // At package level, can import MyClass and access MyClass.String
        assertEquals("MyClass.String", scoped.resolve(otherMyClassString))
        assertEquals(setOf("com.other.MyClass"), scoped.requiredImports)

        // kotlin.String should still be accessible as simple name at package level
        val kotlinString = fq("kotlin", "String")
        assertEquals("String", scoped.resolve(kotlinString))
    }

    @Test
    fun testRegisterSameNameTwice() = runTableTest {
        // Registering the same name twice should be idempotent
        val myClass = fq("com.example", "MyClass")
        table.register(myClass)
        table.register(myClass)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("MyClass", scoped.resolve(myClass))
        assertTrue(scoped.requiredImports.isEmpty())
    }

    @Test
    fun testEmptyPackageName() = runTableTest {
        // Class in default/root package
        val myClass = fq("", "MyClass")
        table.register(myClass)

        val scoped = table.scoped(FqName.Package.fromString(""))
        assertEquals("MyClass", scoped.resolve(myClass))
        assertTrue(scoped.requiredImports.isEmpty())

        // From a different package, accessing root package class
        val otherScoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("MyClass", otherScoped.resolve(myClass))
        assertEquals(setOf("MyClass"), otherScoped.requiredImports)
    }

    @Test
    fun testResolvingEnclosingClassFromNestedScope() = runTableTest {
        // class MyClass {
        //     class Inner {
        //         val parent: MyClass // reference to enclosing class
        //     }
        // }

        val myClass = fq("com.example", "MyClass")
        val inner = fq("com.example", "MyClass.Inner")
        table.register(myClass)
        table.register(inner)

        val insideInner = table.scoped(FqName.Package.fromString("com.example"))
            .nested("MyClass")
            .nested("Inner")

        // MyClass should be accessible by simple name from inside Inner
        assertEquals("MyClass", insideInner.resolve(myClass))
        assertTrue(insideInner.requiredImports.isEmpty())
    }

    @Test
    fun testChildClassWithSameNameAsParent() = runTableTest {
        // class MyClass {
        //     class MyClass // child with same name as parent
        // }

        val myClass = fq("com.example", "MyClass")
        val myClassMyClass = fq("com.example", "MyClass.MyClass")
        table.register(myClass)
        table.register(myClassMyClass)

        val packageScoped = table.scoped(FqName.Package.fromString("com.example"))

        assertEquals("MyClass", packageScoped.resolve(myClass))
        assertEquals("MyClass.MyClass", packageScoped.resolve(myClassMyClass))

        val insideMyClass = packageScoped.nested("MyClass")

        // Inside MyClass, "MyClass" refers to the child MyClass.MyClass
        assertEquals("MyClass", insideMyClass.resolve(myClassMyClass))
        // The parent MyClass needs a qualified reference
        assertEquals("com.example.MyClass", insideMyClass.resolve(myClass))

        val insideInnerMyClass = insideMyClass.nested("MyClass")

        // Inside MyClass.MyClass, "MyClass" refers to itself
        assertEquals("MyClass", insideInnerMyClass.resolve(myClassMyClass))
        assertEquals("com.example.MyClass", insideInnerMyClass.resolve(myClass))
    }

    @Test
    fun testCousinClasses() = runTableTest {
        // class Parent1 {
        //     class Child
        // }
        // class Parent2 {
        //     class Child
        //     fun test(): Parent1.Child  // cousin reference
        // }

        val parent1 = fq("com.example", "Parent1")
        val parent1Child = fq("com.example", "Parent1.Child")
        val parent2 = fq("com.example", "Parent2")
        val parent2Child = fq("com.example", "Parent2.Child")
        table.register(parent1)
        table.register(parent1Child)
        table.register(parent2)
        table.register(parent2Child)

        // Inside Parent2.Child, accessing Parent1.Child
        val insideParent2Child = table.scoped(FqName.Package.fromString("com.example"))
            .nested("Parent2")
            .nested("Child")

        // Parent1.Child requires qualification because "Child" refers to Parent2.Child
        assertEquals("Parent1.Child", insideParent2Child.resolve(parent1Child))
        assertEquals("Child", insideParent2Child.resolve(parent2Child))
        assertEquals("Parent1", insideParent2Child.resolve(parent1))
        assertEquals("Parent2", insideParent2Child.resolve(parent2))
    }

    @Test
    fun testMultipleImportsFromDifferentPackages() = runTableTest {
        val classA = fq("com.package1", "ClassA")
        val classB = fq("com.package2", "ClassB")
        val classC = fq("com.package3", "ClassC")
        table.register(classA)
        table.register(classB)
        table.register(classC)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))

        assertEquals("ClassA", scoped.resolve(classA))
        assertEquals("ClassB", scoped.resolve(classB))
        assertEquals("ClassC", scoped.resolve(classC))

        assertEquals(
            setOf("com.package1.ClassA", "com.package2.ClassB", "com.package3.ClassC"),
            scoped.requiredImports
        )
    }

    @Test
    fun testPackageLevelClassConflictingWithNestedFromDifferentPackage() = runTableTest {
        // com.example.String (package-level)
        // com.other.MyClass.String (nested)

        val packageString = fq("com.example", "String")
        val otherMyClass = fq("com.other", "MyClass")
        val otherNestedString = fq("com.other", "MyClass.String")
        table.register(packageString)
        table.register(otherMyClass)
        table.register(otherNestedString)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))

        // Package-level String uses simple name
        assertEquals("String", scoped.resolve(packageString))

        // kotlin.String is shadowed by package-level String
        val kotlinString = fq("kotlin", "String")
        assertEquals("kotlin.String", scoped.resolve(kotlinString))

        // com.other.MyClass can be imported
        assertEquals("MyClass", scoped.resolve(otherMyClass))
        assertEquals(setOf("com.other.MyClass"), scoped.requiredImports)

        // com.other.MyClass.String - "String" part doesn't conflict because it's nested
        assertEquals("MyClass.String", scoped.resolve(otherNestedString))
    }

    @Test
    fun testSiblingAndChildWithSameName() = runTableTest {
        // class MyClass {
        //     class Inner {
        //         class Nested // child of Inner named "Nested"
        //     }
        //     class Nested // sibling of Inner named "Nested"
        // }

        val myClass = fq("com.example", "MyClass")
        val inner = fq("com.example", "MyClass.Inner")
        val innerNested = fq("com.example", "MyClass.Inner.Nested")
        val siblingNested = fq("com.example", "MyClass.Nested")
        table.register(myClass)
        table.register(inner)
        table.register(innerNested)
        table.register(siblingNested)

        // Inside MyClass.Inner
        val insideInner = table.scoped(FqName.Package.fromString("com.example"))
            .nested("MyClass")
            .nested("Inner")

        // "Nested" should refer to the child MyClass.Inner.Nested (inner scope takes priority)
        assertEquals("Nested", insideInner.resolve(innerNested))
        // The sibling MyClass.Nested needs qualification
        assertEquals("MyClass.Nested", insideInner.resolve(siblingNested))
    }

    @Test
    fun testDeeplyNestedSiblingsAtDifferentLevels() = runTableTest {
        // class A {
        //     class B {
        //         class C {
        //             class D
        //         }
        //     }
        //     class E // sibling of B at level 1
        // }

        val a = fq("com.example", "A")
        val b = fq("com.example", "A.B")
        val c = fq("com.example", "A.B.C")
        val d = fq("com.example", "A.B.C.D")
        val e = fq("com.example", "A.E")
        table.register(a)
        table.register(b)
        table.register(c)
        table.register(d)
        table.register(e)

        // Inside A.B.C.D, can we access E?
        val insideD = table.scoped(FqName.Package.fromString("com.example"))
            .nested("A")
            .nested("B")
            .nested("C")
            .nested("D")

        // E is a sibling of B, both children of A (which encloses us)
        assertEquals("E", insideD.resolve(e))
        assertEquals("D", insideD.resolve(d))
        assertEquals("C", insideD.resolve(c))
        assertEquals("B", insideD.resolve(b))
        assertEquals("A", insideD.resolve(a))
    }

    @Test
    fun testSameSimpleNameAcrossMultipleNestedLevels() = runTableTest {
        // class A {
        //     class X
        //     class B {
        //         class X
        //         class C {
        //             class X
        //             val innerX: X        // A.B.C.X
        //             val parentX: B.X     // A.B.X
        //             val grandparentX: A.X // A.X
        //         }
        //     }
        // }

        val a = fq("com.example", "A")
        val ax = fq("com.example", "A.X")
        val ab = fq("com.example", "A.B")
        val abx = fq("com.example", "A.B.X")
        val abc = fq("com.example", "A.B.C")
        val abcx = fq("com.example", "A.B.C.X")
        table.register(a)
        table.register(ax)
        table.register(ab)
        table.register(abx)
        table.register(abc)
        table.register(abcx)

        val insideC = table.scoped(FqName.Package.fromString("com.example"))
            .nested("A")
            .nested("B")
            .nested("C")

        // Inside C, "X" refers to A.B.C.X (direct child)
        assertEquals("X", insideC.resolve(abcx))
        // A.B.X needs qualified path
        assertEquals("B.X", insideC.resolve(abx))
        // A.X needs qualified path
        assertEquals("A.X", insideC.resolve(ax))
    }

    @Test
    fun testCrossPackageNestedClassWhenTopLevelNameConflicts() = runTableTest {
        // com.example.MyClass exists
        // com.other.MyClass.Inner needs to be accessed from com.example

        val exampleMyClass = fq("com.example", "MyClass")
        val otherMyClass = fq("com.other", "MyClass")
        val otherInner = fq("com.other", "MyClass.Inner")
        table.register(exampleMyClass)
        table.register(otherMyClass)
        table.register(otherInner)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))

        // com.example.MyClass uses simple name
        assertEquals("MyClass", scoped.resolve(exampleMyClass))

        // com.other.MyClass must use FQN because MyClass is taken
        assertEquals("com.other.MyClass", scoped.resolve(otherMyClass))

        // com.other.MyClass.Inner must also use FQN
        assertEquals("com.other.MyClass.Inner", scoped.resolve(otherInner))

        assertTrue(scoped.requiredImports.isEmpty())
    }

    @Test
    fun testNestedClassNameMatchesParentPackageSegment() = runTableTest {
        // package com.example
        // class MyClass {
        //     class example // same name as package segment "example"
        // }

        val myClass = fq("com.example", "MyClass")
        val nestedExample = fq("com.example", "MyClass.example")
        table.register(myClass)
        table.register(nestedExample)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("MyClass.example", scoped.resolve(nestedExample))

        val insideMyClass = scoped.nested("MyClass")
        assertEquals("example", insideMyClass.resolve(nestedExample))
    }

    @Test
    fun testAccessingClassFromDifferentPackageWithSamePackageStructure() = runTableTest {
        // com.example.MyClass
        // com.example.other.MyClass (different package, same simple name)

        val myClass1 = fq("com.example", "MyClass")
        val myClass2 = fq("com.example.other", "MyClass")
        table.register(myClass1)
        table.register(myClass2)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))

        // Local MyClass uses simple name
        assertEquals("MyClass", scoped.resolve(myClass1))
        // MyClass from subpackage requires FQN
        assertEquals("com.example.other.MyClass", scoped.resolve(myClass2))

        assertTrue(scoped.requiredImports.isEmpty())
    }

    @Test
    fun testRegisterAllVariant() = runTableTest {
        val class1 = fq("com.example", "Class1")
        val class2 = fq("com.example", "Class2")
        val class3 = fq("com.example", "Class3")

        table.registerAll(class1 as FqName.Declaration, class2 as FqName.Declaration, class3 as FqName.Declaration)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))
        assertEquals("Class1", scoped.resolve(class1))
        assertEquals("Class2", scoped.resolve(class2))
        assertEquals("Class3", scoped.resolve(class3))
    }

    @Test
    fun testNestedFromOtherPackageImportedThenAccessChild() = runTableTest {
        // com.other.Parent
        // com.other.Parent.Child
        // com.other.Parent.Child.GrandChild

        val parent = fq("com.other", "Parent")
        val child = fq("com.other", "Parent.Child")
        val grandChild = fq("com.other", "Parent.Child.GrandChild")
        table.register(parent)
        table.register(child)
        table.register(grandChild)

        val scoped = table.scoped(FqName.Package.fromString("com.example"))

        // Parent can be imported
        assertEquals("Parent", scoped.resolve(parent))
        assertEquals(setOf("com.other.Parent"), scoped.requiredImports)

        // Child and GrandChild use the imported Parent
        assertEquals("Parent.Child", scoped.resolve(child))
        assertEquals("Parent.Child.GrandChild", scoped.resolve(grandChild))

        // Imports shouldn't change
        assertEquals(setOf("com.other.Parent"), scoped.requiredImports)
    }

    @Test
    fun testScopePathDoesNotAffectUnrelatedResolution() = runTableTest {
        // Verify that being inside a nested scope doesn't incorrectly
        // affect resolution of unrelated classes

        val myClass = fq("com.example", "MyClass")
        val myClassInner = fq("com.example", "MyClass.Inner")
        val otherClass = fq("com.example", "OtherClass")
        table.register(myClass)
        table.register(myClassInner)
        table.register(otherClass)

        val insideInner = table.scoped(FqName.Package.fromString("com.example"))
            .nested("MyClass")
            .nested("Inner")

        // OtherClass should still be accessible by simple name
        assertEquals("OtherClass", insideInner.resolve(otherClass))
    }

    @Test
    fun testImplicitImportAccessibleFromDeepNestedScope() = runTableTest {
        // Verify kotlin.Int is accessible from deep nesting when not shadowed

        val a = fq("com.example", "A")
        val b = fq("com.example", "A.B")
        val c = fq("com.example", "A.B.C")
        table.register(a)
        table.register(b)
        table.register(c)

        val kotlinInt = fq("kotlin", "Int")

        val insideC = table.scoped(FqName.Package.fromString("com.example"))
            .nested("A")
            .nested("B")
            .nested("C")

        // kotlin.Int should be accessible by simple name since nothing shadows it
        assertEquals("Int", insideC.resolve(kotlinInt))
        assertTrue(insideC.requiredImports.isEmpty())
    }

    @Test
    fun testParentClassShadowedByChildWithSameName() = runTableTest {
        // class Outer {
        //     class Inner {
        //         class Outer // shadows the enclosing Outer
        //     }
        // }

        val outer = fq("com.example", "Outer")
        val inner = fq("com.example", "Outer.Inner")
        val innerOuter = fq("com.example", "Outer.Inner.Outer")
        table.register(outer)
        table.register(inner)
        table.register(innerOuter)

        val insideInner = table.scoped(FqName.Package.fromString("com.example"))
            .nested("Outer")
            .nested("Inner")

        // Inside Outer.Inner, "Outer" refers to the child Outer.Inner.Outer
        assertEquals("Outer", insideInner.resolve(innerOuter))
        // The enclosing Outer requires FQN or qualified reference
        assertEquals("com.example.Outer", insideInner.resolve(outer))
    }

    private class TestTable(platform: Platform) {
        val table: FqNameTable = FqNameTable(platform = platform)
    }

    private fun runTableTest(platform: Platform = Platform.Common, body: TestTable.() -> Unit) {
        val table = TestTable(platform)
        body(table)
    }
}

private fun FqNameTable.register(fqName: FqName) {
    require(fqName is FqName.Declaration) { "Only declaration names can be registered" }
    register(fqName)
}
