package ma.dexter.tasks

import ma.dexter.dex.MutableClassDef
import ma.dexter.project.Workspace

class BaksmaliTask(
    private val classDef: MutableClassDef
) : Task<String>() {

    override fun run(): Result<String> {
        val smaliCode = Workspace.getOpenedProject()
            .smaliContainer.getSmaliCode(classDef)

        return Result.success(smaliCode)
    }

}
