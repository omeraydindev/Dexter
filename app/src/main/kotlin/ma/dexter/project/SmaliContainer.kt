package ma.dexter.project

import ma.dexter.tools.smali.BaksmaliInvoker
import org.jf.dexlib2.iface.ClassDef

class SmaliContainer {

    // LsomePackage/someClass;  ->  Smali code
    private val map = mutableMapOf<String, String>()

    fun getSmaliCode(classDef: ClassDef): String {
        val smaliCode = map[classDef.type]

        return if (smaliCode != null) {
            smaliCode
        } else {
            val newCode = BaksmaliInvoker.disassemble(classDef)
            putSmaliCode(classDef, newCode)
            newCode
        }
    }

    fun putSmaliCode(classDef: ClassDef, smaliCode: String) {
        map[classDef.type] = smaliCode
    }

}
