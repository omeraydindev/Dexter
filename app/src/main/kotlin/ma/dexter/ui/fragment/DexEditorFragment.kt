package ma.dexter.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ma.dexter.R
import ma.dexter.databinding.FragmentDexEditorBinding
import ma.dexter.databinding.ItemDexTreeNodeBinding
import ma.dexter.managers.DexProjectManager
import ma.dexter.ui.activity.BaseActivity
import ma.dexter.ui.tree.SmaliTree
import ma.dexter.ui.tree.TreeNode
import ma.dexter.ui.tree.TreeView
import ma.dexter.ui.tree.base.BaseNodeViewBinder
import ma.dexter.ui.tree.base.BaseNodeViewFactory
import ma.dexter.ui.tree.model.DexClassItem
import ma.dexter.ui.tree.model.DexItem
import ma.dexter.ui.util.dp
import ma.dexter.ui.util.setMargins
import org.jf.dexlib2.iface.ClassDef
import java.io.File
import java.lang.ref.WeakReference
import java.util.zip.ZipFile

class DexEditorFragment : Fragment() {
    private lateinit var binding: FragmentDexEditorBinding
    private var dexItemClickListener: DexItemClickListener? = null

    interface DexItemClickListener {
        fun onDexClassItemClick(classDef: ClassDef)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDexEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is DexItemClickListener) {
            dexItemClickListener = context
        }
    }

    fun loadApk(apkPath: String) {

        ZipFile(apkPath).use { apk ->
            val list = mutableListOf<ByteArray>()

            for (entry in apk.entries()) {
                if (!entry.isDirectory
                    && entry.name.startsWith("classes")
                    && entry.name.endsWith(".dex")
                ) {
                    list += apk.getInputStream(entry) // this is closed by ZipFile automatically
                        .readBytes()
                }
            }

            loadDexes(list)
        }
    }

    fun loadDexes(dexPaths: Array<String>) {
        val list = mutableListOf<ByteArray>()

        dexPaths.forEach {
            list += File(it).readBytes()
        }

        loadDexes(list)
    }

    private fun loadDexes(byteArrays: List<ByteArray>) {
        val dexTree = SmaliTree()
            .addDexes(byteArrays)

        DexProjectManager.dexList = dexTree.dexList

        val treeView = TreeView(
            dexTree.createTree(),
            requireContext(),
            object :
                BaseNodeViewFactory<DexItem>() {

                override fun getNodeLayoutId(level: Int) = R.layout.item_dex_tree_node

                override fun getNodeViewBinder(view: View, level: Int) =

                    object : BaseNodeViewBinder<DexItem>(view) {
                        lateinit var icon: WeakReference<ImageView>

                        override fun bindView(treeNode: TreeNode<DexItem>) {
                            val binding = ItemDexTreeNodeBinding.bind(view).apply {
                                title.text = treeNode.value.path

                                root.setMargins(
                                    left = level * 20.dp
                                )
                            }

                            icon = WeakReference(binding.icon.apply {
                                rotation = if (treeNode.isExpanded) 90F else 0F

                                visibility =
                                    if (treeNode.isLeaf) View.INVISIBLE else View.VISIBLE
                            })
                        }

                        override fun onNodeToggled(
                            treeNode: TreeNode<DexItem>,
                            expand: Boolean
                        ) {
                            icon.get()
                                ?.animate()
                                ?.rotation(if (expand) 90F else 0F)
                                ?.setDuration(150)
                                ?.start()

                            treeNode.value.also { dexItem ->
                                if (dexItem is DexClassItem) {
                                    dexItemClickListener?.onDexClassItemClick(dexItem.classDef)
                                    return
                                }
                            }

                        }
                    }
            })

        val treeRecyclerView = treeView.view

        binding.root.removeAllViews()
        binding.root.addView(treeRecyclerView, ViewGroup.LayoutParams(-1, -1))

        // TODO: clean-up
        treeRecyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
            val pos = (treeRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

            mutableListOf<String>().let { list ->
                var treeNode = treeView.adapter.expandedNodeList[pos]

                while (!treeNode.isRoot) {
                    treeNode = treeNode.parent

                    if (!treeNode.isLeaf && !treeNode.isRoot) {
                        list.add(0, treeNode.value.toString())
                    }
                }

                (requireActivity() as BaseActivity).subtitle = list.joinToString(".")
            }
        }
    }

}
