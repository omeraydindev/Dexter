package ma.dexter.dex

import com.google.common.truth.Truth.assertThat
import ma.dexter.util.compile.Java2Dex
import ma.dexter.core.DexBackedDex
import ma.dexter.tools.smali.BaksmaliInvoker
import org.jf.smali.SmaliTestUtils
import org.junit.Test

class DexDisassembleTest {

    @Test
    fun `SmaliTestUtils#compileSmali`() {

        val dexFile = Java2Dex.compile(
            fileName = "A.java",
            javaCode = """
                package ma.test;
                
                public class A {}
            """
        )

        val dex = DexBackedDex.fromFile(dexFile)

        val classDef = dex.classes.find { it.type == "Lma/test/A;" }
        val smaliCode = BaksmaliInvoker.disassemble(requireNotNull(classDef))

        val classDefRebuilt = SmaliTestUtils.compileSmali(smaliCode)
        val smaliCodeRebuilt = BaksmaliInvoker.disassemble(classDefRebuilt)

        assertThat(smaliCode).isEqualTo(smaliCodeRebuilt)
        print(smaliCode)
    }

}
