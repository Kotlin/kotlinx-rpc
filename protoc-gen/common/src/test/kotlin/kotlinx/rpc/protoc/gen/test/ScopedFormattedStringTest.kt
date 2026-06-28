package kotlinx.rpc.protoc.gen.test

import kotlinx.rpc.protoc.gen.core.FqNameTable
import kotlinx.rpc.protoc.gen.core.ScopedFqNameTable
import kotlinx.rpc.protoc.gen.core.merge
import kotlinx.rpc.protoc.gen.core.model.FqName
import kotlinx.rpc.protoc.gen.core.scoped
import kotlinx.rpc.protoc.gen.core.wrapIn
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class ScopedFormattedStringTest {
    @Test
    fun testUsedOnceWorks() = withTable {
        val scoped1 = "1".scoped()
        val scoped2 = "2".scoped()

        assertEquals("a = 1", scoped1.wrapIn { "a = $it" }.resolve(table))
        assertEquals("a = 2", scoped2.wrapIn { "a = $it" }.resolve(table))

        val merged = scoped1.merge(scoped2) { a, b ->
            "$a$b"
        }
        assertEquals("12", merged.resolve(table))
    }

    @Test
    fun testNotUsed() = withTable {
        val scoped1 = "1".scoped()

        assertThrows<IllegalStateException> {
            scoped1.wrapIn { "a" }
        }
    }

    @Test
    fun testUsedTwice() = withTable {
        val scoped1 = "1".scoped()

        assertThrows<IllegalStateException> {
            scoped1.wrapIn { "$it $it" }
        }
    }

    @Test
    fun testWrongOrder() = withTable {
        val scoped1 = "1".scoped()
        val scoped2 = "2".scoped()

        assertThrows<IllegalStateException> {
            scoped1.merge(scoped2) { a, b ->
                "$b$a"
            }
        }
    }

    private class Env(
        val table: ScopedFqNameTable
    )

    private fun withTable(test: Env.() -> Unit) {
        test(Env(FqNameTable().scoped(FqName.Package.Root, mutableSetOf())))
    }
}
