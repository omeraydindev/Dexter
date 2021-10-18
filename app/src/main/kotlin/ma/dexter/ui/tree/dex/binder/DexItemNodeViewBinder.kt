package ma.dexter.ui.tree.dex.binder

import android.view.View
import ma.dexter.databinding.ItemDexTreeNodeBinding
import ma.dexter.ui.tree.TreeNode
import ma.dexter.ui.tree.base.BaseNodeViewBinder
import ma.dexter.ui.tree.model.DexItem
import ma.dexter.ui.util.dp
import ma.dexter.ui.util.setMargins

class DexItemNodeViewBinder (
    itemView: View,
    private val level: Int,
    private val toggleListener: DexItemNodeToggleListener,
    private val longClickListener: DexItemNodeLongClickListener
): BaseNodeViewBinder<DexItem>(itemView) {

    private lateinit var binding: ItemDexTreeNodeBinding

    override fun bindView(treeNode: TreeNode<DexItem>) {
        binding = ItemDexTreeNodeBinding.bind(itemView)

        binding.root.setMargins(left = level * 20.dp)
        binding.title.text = treeNode.value.path

        binding.icon.rotation = if (treeNode.isExpanded) 90F else 0F

        binding.icon.visibility =
            if (treeNode.isLeaf) View.INVISIBLE else View.VISIBLE
    }

    override fun onNodeToggled(treeNode: TreeNode<DexItem>, expand: Boolean) {
        binding.icon.animate()
            .rotation(if (expand) 90F else 0F)
            .setDuration(150)
            .start()

        toggleListener.onNodeToggled(treeNode)
    }

    override fun onNodeLongClicked(view: View, treeNode: TreeNode<DexItem>, expanded: Boolean): Boolean {
        return longClickListener.onNodeLongClicked(view, treeNode)
    }

    fun interface DexItemNodeToggleListener {
        fun onNodeToggled(treeNode: TreeNode<DexItem>)
    }

    fun interface DexItemNodeLongClickListener {
        fun onNodeLongClicked(view: View, treeNode: TreeNode<DexItem>): Boolean
    }
}
