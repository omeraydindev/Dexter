package ma.dexter.managers

import ma.dexter.dex.MutableDex
import org.jf.dexlib2.DexFileFactory
import org.jf.dexlib2.iface.ClassDef
import java.io.File

class MutableDexContainer {

    // TODO: handle multidex properly
    var entries = mutableListOf<MutableDex>()

    // TODO: temporary
    fun replaceClassDef(
        classDefToReplace: ClassDef
    ) {
        entries.forEach {
            if (it.findClassDef(classDefToReplace.type) != null) {
                it.addClassDef(classDefToReplace)
                return
            }
        }
    }

    // TODO: temporary
    fun deleteClassDef(
        classDefToDelete: ClassDef
    ) {
        entries.forEach {
            it.deleteClassDef(classDefToDelete)
        }
    }

    // TODO: temporary
    fun exportTo(
        dir: File
    ) {
        entries.forEachIndexed { index, dex ->
            var name = "classes${index + 1}"
            var path = File(dir, "$name.dex")

            while (path.exists()) {
                name += "_d"
                path = File(dir, "$name.dex")
            }

            DexFileFactory.writeDexFile(path.absolutePath, dex)
        }
    }

}
