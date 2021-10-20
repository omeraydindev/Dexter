package ma.dexter.ui.tree.dex.binder

import android.view.View
import ma.dexter.databinding.ItemDexTreeNodeBinding
import ma.dexter.ui.tree.TreeNode
import ma.dexter.ui.tree.base.BaseNodeViewBinder
import ma.dexter.ui.tree.model.DexClassItem
import ma.dexter.ui.tree.model.DexItem
import ma.dexter.ui.util.dp
import ma.dexter.ui.util.setMargins
import ma.dexter.util.isAnnotation
import ma.dexter.util.isEnum
import ma.dexter.util.isInterface

class DexItemNodeViewBinder(
    itemView: View,
    private val level: Int,
    private val toggleListener: DexItemNodeToggleListener,
    private val longClickListener: DexItemNodeLongClickListener
) : BaseNodeViewBinder<DexItem>(itemView) {

    private lateinit var binding: ItemDexTreeNodeBinding

    override fun bindView(treeNode: TreeNode<DexItem>) {
        binding = ItemDexTreeNodeBinding.bind(itemView)

        binding.root.setMargins(left = level * 20.dp)
        binding.title.text = treeNode.value.path

        binding.icExpand.rotation = if (treeNode.isExpanded) 90F else 0F

        val item = treeNode.value
        if (item is DexClassItem) {
            binding.icExpand.visibility = View.GONE
            binding.cvClassDef.visibility = View.VISIBLE

            binding.txClassDef.text = when {
                item.classDef.isAnnotation() -> {
                    binding.cvClassDef.setCardBackgroundColor(COLOR_INTERFACE)
                    "@"
                }
                item.classDef.isEnum() -> {
                    binding.cvClassDef.setCardBackgroundColor(COLOR_CLASS)
                    "E"
                }
                item.classDef.isInterface() -> {
                    binding.cvClassDef.setCardBackgroundColor(COLOR_INTERFACE)
                    "I"
                }
                else -> { // Class
                    binding.cvClassDef.setCardBackgroundColor(COLOR_CLASS)
                    "C"
                }
            }
        } else {
            binding.icExpand.visibility = View.VISIBLE
            binding.cvClassDef.visibility = View.GONE
        }
    }

    override fun onNodeToggled(treeNode: TreeNode<DexItem>, expand: Boolean) {
        if (binding.icExpand.visibility == View.VISIBLE) {
            binding.icExpand.animate()
                .rotation(if (expand) 90F else 0F)
                .setDuration(150)
                .start()
        }

        toggleListener.onNodeToggled(treeNode)
    }

    override fun onNodeLongClicked(
        view: View,
        treeNode: TreeNode<DexItem>,
        expanded: Boolean
    ): Boolean {
        return longClickListener.onNodeLongClicked(view, treeNode)
    }

    fun interface DexItemNodeToggleListener {
        fun onNodeToggled(treeNode: TreeNode<DexItem>)
    }

    fun interface DexItemNodeLongClickListener {
        fun onNodeLongClicked(view: View, treeNode: TreeNode<DexItem>): Boolean
    }

    companion object {
        const val COLOR_CLASS = 0xff67bed9.toInt()
        const val COLOR_INTERFACE = 0xff487c39.toInt()
    }
}
