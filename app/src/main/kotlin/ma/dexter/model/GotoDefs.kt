package ma.dexter.model

import org.jf.dexlib2.iface.ClassDef

sealed class GotoDef

// do "equals" and "hashCode" work well for ClassDef subclasses?
data class SmaliGotoDef(
    val classDef: ClassDef,
    val defDescriptor: String? = null
): GotoDef()

data class JavaGotoDef(
    val className: String,
    val javaCode: String
): GotoDef()
