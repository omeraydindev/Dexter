package ma.dexter.model

import org.jf.dexlib2.iface.ClassDef

// does "equals" and "hashCode" work well for ClassDef subclasses?
data class SmaliGotoDef(
    val classDef: ClassDef,
    val defDescriptor: String? = null
): GotoDef()
