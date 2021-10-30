package ma.dexter.ui.tree.dex.binder

import android.view.View
import androidx.core.content.ContextCompat
import ma.dexter.App
import ma.dexter.R
import ma.dexter.databinding.ItemDexTreeNodeBinding
import ma.dexter.ui.tree.TreeNode
import ma.dexter.ui.tree.base.BaseNodeViewBinder
import ma.dexter.ui.tree.dex.DexClassNode
import ma.dexter.ui.util.dp
import ma.dexter.ui.util.setMargins

class DexItemNodeViewBinder(
    itemView: View,
    private val level: Int,
    private val toggleListener: DexItemNodeToggleListener,
    private val longClickListener: DexItemNodeLongClickListener
) : BaseNodeViewBinder<DexClassNode>(itemView) {

    private lateinit var binding: ItemDexTreeNodeBinding

    override fun bindView(treeNode: TreeNode<DexClassNode>) {
        binding = ItemDexTreeNodeBinding.bind(itemView)

        binding.root.setMargins(left = level * 20.dp)
        binding.title.text = treeNode.value.name

        binding.icExpand.rotation = if (treeNode.isExpanded) 90F else 0F

        if (treeNode.isLeaf) {
            binding.icExpand.visibility = View.GONE
            binding.cvClassDef.visibility = View.VISIBLE

            val colorClass = ContextCompat.getColor(App.context, R.color.colorClass)

            binding.txClassDef.setBackgroundColor(colorClass)
            binding.txClassDef.text = "C"
        } else {
            binding.icExpand.visibility = View.VISIBLE
            binding.cvClassDef.visibility = View.GONE
        }
    }

    override fun onNodeToggled(treeNode: TreeNode<DexClassNode>, expand: Boolean) {
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
        treeNode: TreeNode<DexClassNode>,
        expanded: Boolean
    ): Boolean {
        return longClickListener.onNodeLongClicked(view, treeNode)
    }

    fun interface DexItemNodeToggleListener {
        fun onNodeToggled(treeNode: TreeNode<DexClassNode>)
    }

    fun interface DexItemNodeLongClickListener {
        fun onNodeLongClicked(view: View, treeNode: TreeNode<DexClassNode>): Boolean
    }
}
