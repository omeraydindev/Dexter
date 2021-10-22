package ma.dexter.project

import com.google.common.collect.ImmutableBiMap
import ma.dexter.dex.MutableClassDef
import ma.dexter.dex.MutableDexFile

class MutableDexContainer(
    val entries: List<MutableDexFile>
) {
    val biMap: ImmutableBiMap<MutableDexFile, String> = ImmutableBiMap.copyOf(
        entries.associateWith { it.dexFile?.name ?: "" }
    )

    fun findClassDef(classDescriptor: String?): MutableClassDef? {
        if (classDescriptor == null) return null

        entries.forEach { dexEntry ->
            dexEntry.findClassDef(classDescriptor)?.let {
                return it
            }
        }

        return null
    }

    /**
     * Deletes [MutableClassDef]s in the given package.
     *
     * [packagePath] being in the somePackage/someClass format.
     */
    fun deletePackage(packagePath: String) {
        entries.forEach { dex ->
            dex.deletePackage(packagePath)
        }
    }

    fun saveDexFiles() {
        entries.forEach { dexEntry ->
            dexEntry.writeToFile(dexEntry.dexFile!!) // todo: handle APKs
        }
    }

}
