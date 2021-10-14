package ma.dexter.tools.decompilers.jadx

import com.google.common.truth.Truth.assertThat
import ma.dexter.util.compile.Java2Dex
import org.junit.Test

class JADXDecompilerTest {

    @Test
    fun `JADX decompile class basic`() {
        val dexFile = Java2Dex.compile(
            fileName = "Test.java",
            javaCode = """
                package test.aaa;
                
                public class Test {}
            """
        )

        val result = JADXDecompiler().decompileDex("test.aaa.Test", dexFile)
        assertThat(result).contains("Decompiled")
        println(result)

        val result2 = JADXDecompiler().decompileDex("test/aaa/Test", dexFile)
        assertThat(result2).contains("Decompiled")
        println(result2)
    }

    @Test
    fun `JADX decompile class from defpackage`() {
        val dexFile = Java2Dex.compile(
            fileName = "Test.java",
            javaCode = """
                public class Test {}
            """
        )

        val result = JADXDecompiler().decompileDex("Test", dexFile)
        assertThat(result).contains("Decompiled")
        println(result)
    }

}
