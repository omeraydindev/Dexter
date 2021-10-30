package ma.dexter.ui.tree.dex

import ma.dexter.dex.MutableDexFile
import ma.dexter.ui.tree.*
import ma.dexter.util.normalizeSmaliPath

class SmaliTree {
    private val dexList = mutableListOf<MutableDexFile>()

    fun createTree(): TreeNode<DexClassNode> {
        val rootTreeNode = TreeNode.root<DexClassNode>()

        dexList.forEach {
            addToTree(rootTreeNode, it)
        }

        rootTreeNode.sort { node1, node2 ->
            if (node1.isLeaf != node2.isLeaf) {
                node1.isLeaf.compareTo(node2.isLeaf)
            } else {
                node1.value.name.compareTo(node2.value.name)
            }
        }

        rootTreeNode.compactMiddlePackages(
            pathGetter = DexClassNode::name,
            pathSetter = { it, path -> it.name = path }
        )
        rootTreeNode.reassignLevels()

        return rootTreeNode
    }

    // TODO: use a faster algorithm
    private fun addToTree(
        rootTreeNode: TreeNode<DexClassNode>,
        dex: MutableDexFile
    ) {
        dex.classes.forEach { classDef ->
            var currentNode = rootTreeNode

            val classDefSegments = normalizeSmaliPath(classDef.type).split("/")

            classDefSegments.forEachIndexed { level, segment ->
                val subNode: TreeNode<DexClassNode>
                val toFind = currentNode.findChildByValue(DexClassNode(segment))

                if (toFind == null) {
                    subNode = TreeNode(DexClassNode(segment))

                    currentNode.addChild(subNode)
                } else {
                    subNode = toFind
                }

                subNode.level = level
                currentNode = subNode
            }
        }
    }

    private fun addDex(dex: MutableDexFile): SmaliTree {
        dexList += dex
        return this
    }

    fun addDexEntries(dexEntries: List<MutableDexFile>): SmaliTree {
        dexEntries.forEach { addDex(it) }
        return this
    }

}
