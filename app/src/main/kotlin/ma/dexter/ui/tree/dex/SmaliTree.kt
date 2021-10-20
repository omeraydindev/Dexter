package ma.dexter.ui.tree.dex

import ma.dexter.project.DexEntry
import ma.dexter.dex.MutableDex
import ma.dexter.ui.tree.*
import ma.dexter.ui.tree.model.DexClassItem
import ma.dexter.ui.tree.model.DexItem
import ma.dexter.util.getClassDefPath

class SmaliTree {
    private val dexList = mutableListOf<MutableDex>()

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

        rootTreeNode.compactMiddlePackages(
            pathGetter = DexItem::path,
            pathSetter = { it, path -> it.path = path }
        )
        rootTreeNode.reassignLevels()

        return rootTreeNode
    }

    // TODO: use a faster algorithm
    private fun addToTree(
        rootTreeNode: TreeNode<DexItem>,
        dex: MutableDex
    ) {
        dex.classes.forEach { classDef ->
            var currentNode = rootTreeNode

            val classDefSegments = getClassDefPath(classDef.type).split("/")

            classDefSegments.forEachIndexed { level, segment ->
                val subNode: TreeNode<DexItem>
                val toFind = currentNode.findChildByValue(DexItem(segment))

                if (toFind == null) {
                    // deepest level means this is the name of the class
                    // (as in, "String" in "java/lang/String")
                    subNode = if (classDefSegments.lastIndex == level) {
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

    private fun addDex(dex: MutableDex): SmaliTree {
        dexList += dex
        return this
    }

    fun addDexEntries(dexEntries: List<DexEntry>): SmaliTree {
        dexEntries.forEach { addDex(it.dex) }
        return this
    }

}
