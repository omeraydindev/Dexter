package ma.dexter.ui.tree

/**
 * Sort children recursively in the TreeNode.
 */
fun <D> TreeNode<D>.sort(comparator: Comparator<TreeNode<D>>) {
    children.forEach {
        it.sort(comparator)
    }

    children = children.sortedWith(comparator)
}

/**
 * Find child by value in the TreeNode.
 *
 * Doesn't do a recursive search.
 */
fun <D> TreeNode<D>.findChildByValue(value: D): TreeNode<D>? {
    this.children.forEach {
        if (it.value == value) {
            return it
        }
    }

    return null
}

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
 * See [TreeUtilTest#compactMiddlePackages] for an example usage.
 */
fun <T> TreeNode<T>.compactMiddlePackages(
    pathGetter: (T) -> String,
    pathSetter: (T, path: String) -> Unit
) {
    this.children.forEach { child ->
        if (child.children.size == 1 && !child.children.first().isLeaf) {
            /*child.value.path += "." + child.children.first().value.path*/
            pathSetter(child.value,
                pathGetter(child.value) + "." + pathGetter(child.children.first().value))

            child.children = child.children.first().children

            child.parent.compactMiddlePackages(pathGetter, pathSetter)
        } else {
            child.compactMiddlePackages(pathGetter, pathSetter)
        }
    }
}

/**
 * Reassigns levels to all nodes recursively from their depth.
 */
fun <T> TreeNode<T>.reassignLevels(
    level: Int = -1
) {
    this.level = level
    this.children.forEach { child ->
        if (child.isLeaf) {
            child.level = this.level + 1
        } else {
            child.reassignLevels(this.level + 1)
        }
    }
}
