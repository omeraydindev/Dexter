package ma.dexter.ui.tree

import com.google.common.truth.Truth.assertThat
import ma.dexter.ui.tree.model.DexItem
import org.junit.Test

class TreeUtilTest {

    @Test
    fun compactMiddlePackages() {
        val tree = root<DexItem> {
            child(DexItem("a"))
            child(DexItem("b"))
            child(DexItem("c")) {
                child(DexItem("d")) {
                    child(DexItem("e")) {
                        child(DexItem("f"))
                    }
                }
            }
            child(DexItem("g")) {
                child(DexItem("h")) {
                    child(DexItem("i")) {
                        child(DexItem("j"))
                    }
                }
                child(DexItem("k"))
            }
        }

        assertThat(treeToString(tree)).isEqualTo(
            """
                a
                b
                c
                - d
                - - e
                - - - f
                g
                - h
                - - i
                - - - j
                - k
            """.trimIndent())

        tree.compactMiddlePackages(
            pathGetter = DexItem::path,
            pathSetter = { it, path -> it.path = path }
        )
        assertThat(treeToString(tree)).isEqualTo(
            """
                a
                b
                c.d.e
                - f
                g
                - h.i
                - - j
                - k
            """.trimIndent())
    }

    private fun <T> root(
        block: TreeNode<T>.() -> Unit
    ): TreeNode<T> {
        val root = TreeNode.root<T>()
        block(root)
        return root
    }

    private fun <T> TreeNode<T>.child(
        t: T,
        block: TreeNode<T>.() -> Unit = {}
    ) {
        addChild(TreeNode(t).apply(block))
    }

    private fun <T> treeToString(
        tree: TreeNode<T>,
        prefix: String = "",
        map: (TreeNode<T>) -> String = { it.value.toString() }
    ): String = buildString {

        tree.children.forEach { child ->
            append(prefix + map(child) + "\n")

            if (!child.isLeaf) {
                append(treeToString(child, "$prefix- ", map) + "\n")
            }
        }

    }.trim()
}
