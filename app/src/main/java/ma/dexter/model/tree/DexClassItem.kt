package ma.dexter.model.tree

import org.jf.dexlib2.dexbacked.DexBackedClassDef

/**
 * path: somepackage/SomeClass
 */
class DexClassItem(
    path: String,
    val dexClassDef: DexBackedClassDef
): DexItem(path)
