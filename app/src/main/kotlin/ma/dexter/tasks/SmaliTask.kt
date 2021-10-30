package ma.dexter.tasks

import ma.dexter.tools.smali.SmaliInvoker
import org.jf.dexlib2.iface.ClassDef

class SmaliTask(
    private val smaliCode: String
) : Task<ClassDef>() {

    override fun run(): Result<ClassDef> {
        return SmaliInvoker.assemble(smaliCode)
    }

}
