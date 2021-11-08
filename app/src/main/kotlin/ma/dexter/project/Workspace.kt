package ma.dexter.project

object Workspace {
    private var project: DexProject? = null

    fun getOpenedProject(): DexProject {
        return project
            ?: throw IllegalStateException("No project is opened!")
    }

    fun openProject(project: DexProject) {
        this.project = project
    }

}
