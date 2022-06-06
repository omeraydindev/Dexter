package ma.dexter.dex

import com.google.common.truth.Truth.assertThat
import ma.dexter.tools.smali.BaksmaliInvoker
import ma.dexter.util.compile.Java2Dex
import org.jf.smali.SmaliTestUtils
import org.junit.Test

class MutableDexTests {

    @Test
    fun `find & delete ClassDefs`() {
        val dexFile = Java2Dex.compile(
            fileName = "A.java",
            javaCode = """
                class A { static class B {} }
            """
        )

        val dex = DexFactory.fromFile(dexFile)

        val cd = dex.findClassDef("LA;")
        assert(cd != null)

        dex.deleteClassDef(cd!!)
        assert(dex.findClassDef("LA;") == null)
    }

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

        val dex = DexFactory.fromFile(dexFile)

        val newClassDef = SmaliTestUtils.compileSmali(
            dex.getSmali("Lm/A\$B;")
                .replace("replaceMe", "replacedYou")
        )
        dex.replaceClassDef(newClassDef)

        val otherClassDef = SmaliTestUtils.compileSmali(".class Lm/C; .super Lm/A;")
        dex.addClassDef(otherClassDef)

        dex.writeToFile(dex.dexFile!!)


        // check if changes are applied
        val overwrittenDex = DexFactory.fromFile(dexFile)

        assertThat(overwrittenDex.findClassDef("Lm/A\$B;"))
            .isNotNull()

        assertThat(overwrittenDex.findClassDef("Lm/A\$B;")!!.methods.find { it.name == "replaceMe" })
            .isNull()

        assertThat(overwrittenDex.findClassDef("Lm/A\$B;")!!.methods.find { it.name == "replacedYou" })
            .isNotNull()

        assertThat(overwrittenDex.findClassDef("Lm/C;"))
            .isNotNull()
    }

    private fun MutableDexFile.getSmali(classDescriptor: String): String {
        val classDef = findClassDef(classDescriptor)!!

        return BaksmaliInvoker().disassemble(classDef)
    }

}
