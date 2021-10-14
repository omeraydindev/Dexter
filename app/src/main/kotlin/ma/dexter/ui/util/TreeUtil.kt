package ma.dexter.ui.util

import ma.dexter.model.tree.DexItem
import ma.dexter.ui.component.tree.TreeNode

/**
 * Compacts middle packages/folders in the tree structure. Example:
 *
 * Input         Output
 * a             a
 * b             b
 * c             c.d.e
 * - d           - f
 * - - e         g
 * - - - f       - h.i
 * g             - - j
 * - h           - k
 * - - i
 * - - - j
 * - k
 *
 * See [TreeUtilTest#compactMiddlePackages]
 */
fun compactMiddlePackages(
    tree: TreeNode<DexItem>
) {
    tree.children.forEach { child ->
        if (child.children.size == 1 && !child.children.first().isLeaf) {
            child.value.path += "." + child.children.first().value.path
            child.children = child.children.first().children

            compactMiddlePackages(child.parent)
        } else {
            compactMiddlePackages(child)
        }
    }
}

/**
 * Reassigns levels to all nodes recursively from their depth.
 */
fun <T> reassignLevels(
    tree: TreeNode<T>,
    level: Int = -1
) {
    tree.level = level
    tree.children.forEach { child ->
        if (child.isLeaf) {
            child.level = tree.level + 1
        } else {
            reassignLevels(child, tree.level + 1)
        }
    }
}
