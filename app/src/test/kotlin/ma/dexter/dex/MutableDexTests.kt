package ma.dexter.dex

import com.google.common.truth.Truth.assertThat
import ma.dexter.core.DexBackedDex
import ma.dexter.tools.smali.BaksmaliInvoker
import ma.dexter.util.compile.Java2Dex
import org.jf.dexlib2.DexFileFactory
import org.jf.smali.SmaliTestUtils
import org.junit.Test

class MutableDexTests {

    @Test
    fun `disassemble smali and edit it, then reassemble the dex`() {
        val dexFile = Java2Dex.compile(
            fileName = "A.java",
            javaCode = """
                package m;
                
                class A {
                    static class B {
                        void replaceMe() {}
                    }
                }
            """
        )

        val dex = MutableDex(DexBackedDex.fromFile(dexFile))

        val newClassDef = SmaliTestUtils.compileSmali(
            dex.getSmali("Lm/A\$B;")
                .replace("replaceMe", "replacedYou")
        )
        dex.addClassDef(newClassDef) // will replace the existing ClassDef

        val otherClassDef = SmaliTestUtils.compileSmali(".class Lm/C; .super Lm/A;")
        dex.addClassDef(otherClassDef)

        DexFileFactory.writeDexFile(dexFile.absolutePath, dex) // overwrite the dex


        // check if changes are applied
        val overwrittenDex = MutableDex(DexBackedDex.fromFile(dexFile))

        assertThat(overwrittenDex.findClassDef("Lm/A\$B;"))
            .isNotNull()

        assertThat(overwrittenDex.findClassDef("Lm/A\$B;")!!.methods.find { it.name == "replaceMe" })
            .isNull()

        assertThat(overwrittenDex.findClassDef("Lm/A\$B;")!!.methods.find { it.name == "replacedYou" })
            .isNotNull()

        assertThat(overwrittenDex.findClassDef("Lm/C;"))
            .isNotNull()
    }

    private fun MutableDex.getSmali(classDescriptor: String): String {
        val classDef = findClassDef(classDescriptor)!!

        return BaksmaliInvoker.disassemble(classDef)
    }

}
