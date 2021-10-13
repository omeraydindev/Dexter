package ma.dexter.core

import ma.dexter.model.tree.DexClassItem
import ma.dexter.model.tree.DexItem
import ma.dexter.ui.component.tree.TreeNode
import ma.dexter.ui.component.tree.findChildByValue
import ma.dexter.ui.component.tree.sort
import ma.dexter.util.getClassDefPath

class SmaliTree {
    val dexList = mutableListOf<DexBackedDex>()

    fun createTree(): TreeNode<DexItem> {
        val rootTreeNode = TreeNode.root<DexItem>()

        dexList.forEach {
            addToTree(rootTreeNode, it)
        }

        rootTreeNode.sort { node1, node2 ->
            if (node1.isLeaf != node2.isLeaf) {
                node1.isLeaf.compareTo(node2.isLeaf)
            } else {
                node1.value.compareTo(node2.value)
            }
        }

        return rootTreeNode
    }

    // TODO: use a faster algorithm
    private fun addToTree(
        rootTreeNode: TreeNode<DexItem>,
        dexBackedDex: DexBackedDex
    ) {
        dexBackedDex.classSection.forEach { classDef ->
            var currentNode = rootTreeNode

            val classDefSegments = getClassDefPath(classDef.type).split("/")

            classDefSegments.forEachIndexed { level, segment ->
                val subNode: TreeNode<DexItem>
                val toFind = currentNode.findChildByValue(DexItem(segment))

                if (toFind == null) {
                    // deepest level means this is the name of the class
                    // (as in, "String" in "java/lang/String")
                    subNode = if (classDefSegments.size - 1 == level) {
                        TreeNode(
                            DexClassItem(segment, classDef)
                        )
                    } else {
                        TreeNode(
                            DexItem(segment)
                        )
                    }

                    currentNode.addChild(subNode)
                } else {
                    subNode = toFind
                }

                subNode.level = level
                currentNode = subNode
            }
        }
    }

    fun addDex(dexBackedDex: DexBackedDex): SmaliTree {
        dexList += dexBackedDex
        return this
    }

    fun addDex(byteArray: ByteArray): SmaliTree {
        return addDex(DexBackedDex.fromByteArray(byteArray))
    }

    fun addDexes(byteArrays: List<ByteArray>): SmaliTree {
        byteArrays.forEach(this::addDex)
        return this
    }

}
