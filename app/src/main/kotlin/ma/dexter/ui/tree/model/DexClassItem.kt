package ma.dexter.ui.tree.model

import ma.dexter.dex.MutableClassDef

/**
 * path: somepackage/SomeClass
 */
class DexClassItem(
    path: String,
    val classDef: MutableClassDef
): DexItem(path)
