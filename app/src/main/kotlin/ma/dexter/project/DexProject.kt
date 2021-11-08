package ma.dexter.project

import ma.dexter.dex.MutableDexContainer
import ma.dexter.dex.MutableDexFile
import ma.dexter.dex.SmaliContainer

class DexProject(
    dexEntries: List<MutableDexFile>
): Project {
    val dexContainer = MutableDexContainer(dexEntries)

    val smaliContainer = SmaliContainer()
}
