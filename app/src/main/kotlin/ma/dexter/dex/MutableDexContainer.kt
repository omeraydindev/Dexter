package ma.dexter.dex

import org.jf.dexlib2.DexFileFactory
import org.jf.dexlib2.iface.ClassDef
import java.io.File

class MutableDexContainer{

    // TODO: handle multidex properly
    var entries = mutableListOf<DexEntry>()

    // TODO: temporary
    fun replaceClassDef(
        classDefToReplace: ClassDef
    ) {
        entries.forEach {
            if (it.dex.findClassDef(classDefToReplace.type) != null) {
                it.dex.addClassDef(classDefToReplace)
                return
            }
        }
    }

    // TODO: temporary
    fun deleteClassDef(
        classDefToDelete: ClassDef
    ) {
        entries.forEach {
            it.dex.deleteClassDef(classDefToDelete)
        }
    }

    // TODO: temporary
    fun exportTo(
        dir: File
    ) {
        entries.forEachIndexed { index, dexEntry ->
            var name = "classes${index + 1}"
            var path = File(dir, "$name.dex")

            while (path.exists()) {
                name += "_d"
                path = File(dir, "$name.dex")
            }

            DexFileFactory.writeDexFile(path.absolutePath, dexEntry.dex)
        }
    }

}
