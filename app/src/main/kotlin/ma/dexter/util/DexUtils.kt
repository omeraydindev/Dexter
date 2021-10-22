package ma.dexter.util

import ma.dexter.dex.MutableClassDef
import ma.dexter.ui.tree.TreeNode
import ma.dexter.ui.tree.model.DexItem
import org.jf.dexlib2.AccessFlags
import org.jf.dexlib2.iface.ClassDef
import org.jf.dexlib2.immutable.ImmutableClassDef

fun isValidDexFileName(
    fileName: String
): Boolean {
    return fileName.startsWith("classes") && fileName.endsWith(".dex")
}

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

fun TreeNode<DexItem>.getPackageName(): String {
    val treeNode = this

    val list = buildList {
        var node = treeNode
        while (!node.isRoot) {
            add(node.value.path)
            node = node.parent
        }
    }

    return list
        .reversed()
        .joinToString(".")
        .replace(".", "/")
}
