package ma.dexter.ui.tree.dex.binder

import android.view.View
import ma.dexter.R
import ma.dexter.ui.tree.base.BaseNodeViewFactory
import ma.dexter.ui.tree.dex.binder.DexItemNodeViewBinder.*
import ma.dexter.ui.tree.dex.DexClassNode

class DexItemNodeViewFactory (
    private val toggleListener: DexItemNodeToggleListener,
    private val longClickListener: DexItemNodeLongClickListener
): BaseNodeViewFactory<DexClassNode>() {

    override fun getNodeViewBinder(view: View, level: Int) =
        DexItemNodeViewBinder(view, level, toggleListener, longClickListener)

    override fun getNodeLayoutId(level: Int) = R.layout.item_dex_tree_node

}
