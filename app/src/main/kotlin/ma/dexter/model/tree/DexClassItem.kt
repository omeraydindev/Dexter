package ma.dexter.model.tree

import org.jf.dexlib2.iface.ClassDef

/**
 * path: somepackage/SomeClass
 */
class DexClassItem(
    path: String,
    val classDef: ClassDef
): DexItem(path)
