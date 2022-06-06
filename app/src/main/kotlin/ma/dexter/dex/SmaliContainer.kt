package ma.dexter.dex

import ma.dexter.tools.smali.BaksmaliInvoker

class SmaliContainer {

    // LsomePackage/someClass;  ->  Smali code
    private val map = mutableMapOf<String, String>()

    fun getSmaliCode(classDef: MutableClassDef): String {
        val smaliCode = map[classDef.type]

        return if (smaliCode != null) {
            smaliCode
        } else {
            val newCode = BaksmaliInvoker().disassemble(classDef)
            putSmaliCode(classDef.type, newCode)
            newCode
        }
    }

    fun putSmaliCode(classDescriptor: String, smaliCode: String) {
        map[classDescriptor] = smaliCode
    }

}
