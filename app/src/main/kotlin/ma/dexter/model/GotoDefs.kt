package ma.dexter.model

import ma.dexter.dex.MutableClassDef

sealed class GotoDef

data class SmaliGotoDef(
    val classDef: MutableClassDef,
    val memberDescriptorToGo: String? = null
) : GotoDef()

data class JavaGotoDef(
    val className: String,
    val javaCode: String
) : GotoDef()
