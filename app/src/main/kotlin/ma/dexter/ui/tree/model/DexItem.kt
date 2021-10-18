package ma.dexter.ui.tree.model

/**
 * Could be a package (i.e android/widget)
 * Could be a class   (i.e android/widget/Toast)
 */
open class DexItem(
    var path: String
): Comparable<DexItem> {

    override fun compareTo(other: DexItem) = path.compareTo(other.path)

    override fun toString() = path

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DexItem

        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}
