package ma.dexter.ui.tree.model

/**
 * Could be a package (i.e android/widget)
 * Could be a class   (i.e android/widget/Toast)
 */
open class DexItem(
    var path: String
): Comparable<DexItem> {

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is DexItem) return false

        return path == other.path
    }

    override fun compareTo(other: DexItem) = path.compareTo(other.path)

    override fun hashCode() = path.hashCode()

    override fun toString() = path
}
