package ma.dexter.core.model

import org.jf.dexlib2.dexbacked.DexBackedClassDef

class SmaliGotoDef(
    val dexBackedClassDef: DexBackedClassDef,
    val defDescriptor: String? = null
)
