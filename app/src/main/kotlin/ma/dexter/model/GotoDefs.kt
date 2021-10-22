package ma.dexter.model

import ma.dexter.dex.MutableClassDef
import org.jf.dexlib2.iface.ClassDef

sealed class GotoDef

data class SmaliGotoDef(
    val classDef: MutableClassDef,
    val defDescriptor: String? = null
): GotoDef()

data class JavaGotoDef(
    val className: String,
    val javaCode: String
): GotoDef()
