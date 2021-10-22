package ma.dexter.project

import ma.dexter.dex.MutableDexFile

class DexProject private constructor(
    dexEntries: List<MutableDexFile>
) {
    val dexContainer = MutableDexContainer(dexEntries)

    val smaliContainer = SmaliContainer()

    companion object {
        private var project: DexProject? = null

        fun getOpenedProject() = project ?: throw IllegalStateException("No project is opened!")

        fun openProject(dexEntries: List<MutableDexFile>): DexProject {
            project = DexProject(dexEntries)
            return project!!
        }
    }
}
