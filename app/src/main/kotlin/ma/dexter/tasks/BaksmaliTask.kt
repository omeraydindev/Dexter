package ma.dexter.tasks

import ma.dexter.dex.MutableClassDef
import ma.dexter.project.DexProject

class BaksmaliTask(
    private val classDef: MutableClassDef
) : Task<String>() {

    override fun run(): Result<String> {
        val smali = DexProject.getOpenedProject()
            .smaliContainer.getSmaliCode(classDef)

        return Result(smali, true)
    }

}
