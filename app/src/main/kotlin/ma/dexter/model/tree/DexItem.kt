package ma.dexter.model.tree

/**
 * Could be a package (i.e android/widget)
 * Could be a class   (i.e android/widget/Toast)
 */
open class DexItem(
    val path: String
) : Comparable<DexItem> {

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is DexItem) return false

        return path == other.path
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }

    override fun compareTo(other: DexItem): Int = path.compareTo(other.path)
}
