package ma.dexter.tasks

import com.google.common.truth.Truth.assertThat
import ma.dexter.dex.DexFactory
import ma.dexter.dex.MutableDexFile
import ma.dexter.util.BaseTestClass
import ma.dexter.util.createClassDef
import org.jf.dexlib2.iface.ClassDef
import org.junit.Test
import java.io.File

class MergeDexTaskTest : BaseTestClass() {

    @Test
    fun `merge DEX files`() {
        val dex1 = createDex("dex1.dex", createClassDef("La;"))
        val dex2 = createDex("dex2.dex", createClassDef("Lb;"))
        val mergedDex = File(testFolder, "merged.dex")

        MergeDexTask(arrayOf(dex1.absolutePath, dex2.absolutePath), mergedDex)
            .run {}
        assert(mergedDex.exists())

        val mergedDexFile = DexFactory.fromFile(mergedDex)
        assertThat(mergedDexFile.findClassDef("La;"))
            .isNotNull()
        assertThat(mergedDexFile.findClassDef("Lb;"))
            .isNotNull()
    }

    private fun createDex(
        name: String,
        vararg classDefs: ClassDef
    ): File {
        val dex = MutableDexFile()
        classDefs.forEach(dex::addClassDef)

        val file = File(testFolder, name)
        dex.writeToFile(file)

        return file
    }

}
