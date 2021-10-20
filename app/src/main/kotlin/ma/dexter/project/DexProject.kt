package ma.dexter.project

class DexProject private constructor(
    dexEntries: List<DexEntry>
) {
    val dexContainer = MutableDexContainer(dexEntries)

    val smaliContainer = SmaliContainer()

    companion object {
        private var project: DexProject? = null

        fun getOpenedProject() = project ?: throw IllegalStateException("No project is opened!")

        fun openProject(dexEntries: List<DexEntry>): DexProject {
            project = DexProject(dexEntries)
            return project!!
        }
    }
}
