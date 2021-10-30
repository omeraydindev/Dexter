package ma.dexter.ui.tree.dex

import ma.dexter.ui.tree.compactMiddlePackages

/**
 * Base class to represent nodes in a Dex tree.
 *
 * For example, [name] could be "android" in "android/app/Activity"
 * (or "android.app" if [compactMiddlePackages] was run.)
 */
class DexClassNode(
    var name: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DexClassNode

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString() = name

}
