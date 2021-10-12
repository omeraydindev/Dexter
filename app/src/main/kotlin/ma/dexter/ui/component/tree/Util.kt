package ma.dexter.ui.component.tree

fun <D> TreeNode<D>.sort(comparator: Comparator<TreeNode<D>>) {
    children.forEach {
        it.sort(comparator)
    }

    children = children.sortedWith(comparator)
}

fun <D> TreeNode<D>.findChildByValue(value: D): TreeNode<D>? {
    this.children.forEach {
        if (it.value == value) {
            return it
        }
    }

    return null
}
