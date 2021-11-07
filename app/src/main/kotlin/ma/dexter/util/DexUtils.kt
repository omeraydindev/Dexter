package ma.dexter.util

import ma.dexter.dex.MutableClassDef
import ma.dexter.ui.tree.TreeNode
import ma.dexter.ui.tree.dex.DexClassNode
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.immutable.ImmutableClassDef

const val DEFAULT_DEX_VERSION = 35

fun MutableClassDef.isInterface(): Boolean {
    return (this.accessFlags and AccessFlags.INTERFACE.value) != 0
}

fun MutableClassDef.isEnum(): Boolean {
    return (this.accessFlags and AccessFlags.ENUM.value) != 0
}

fun MutableClassDef.isAnnotation(): Boolean {
    return (this.accessFlags and AccessFlags.ANNOTATION.value) != 0
}

fun createClassDef(
    classDescriptor: String,
    superClassDescriptor: String = "Ljava/lang/Object;"
): ClassDef {
    return ImmutableClassDef(
        classDescriptor,
        0,
        superClassDescriptor,
        null, null, null, null, null
    )
}

fun TreeNode<DexClassNode>.getPath(): String {
    val treeNode = this

    val list = buildList {
        var node = treeNode
        while (!node.isRoot) {
            add(node.value.name)
            node = node.parent
        }
    }

    return list
        .reversed()
        .joinToString(".")
        .replace(".", "/")
}

fun TreeNode<DexClassNode>.getClassDescriptor(): String {
    return "L" + getPath() + ";"
}
