package ma.dexter.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import ma.dexter.R
import ma.dexter.core.SmaliTree
import ma.dexter.databinding.FragmentDexEditorBinding
import ma.dexter.databinding.ItemDexTreeNodeBinding
import ma.dexter.managers.DexProjectManager
import ma.dexter.model.tree.DexClassItem
import ma.dexter.model.tree.DexItem
import ma.dexter.ui.component.tree.TreeNode
import ma.dexter.ui.component.tree.TreeView
import ma.dexter.ui.component.tree.base.BaseNodeViewBinder
import ma.dexter.ui.component.tree.base.BaseNodeViewFactory
import ma.dexter.ui.util.dp
import ma.dexter.ui.util.setMargins
import org.jf.dexlib2.dexbacked.DexBackedClassDef
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.lang.ref.WeakReference
import java.util.zip.ZipFile

class DexEditorFragment : Fragment() {
    private lateinit var binding: FragmentDexEditorBinding
    private var dexItemClickListener: DexItemClickListener? = null

    interface DexItemClickListener {
        fun onDexClassItemClick(dexClassDef: DexBackedClassDef)
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
        val apk = ZipFile(apkPath)
        val list = mutableListOf<ByteArray>()

        for (entry in apk.entries()) {
            if (!entry.isDirectory && entry.name.startsWith("classes") && entry.name.endsWith(".dex")) {
                list += BufferedInputStream(apk.getInputStream(entry))
                    .use(InputStream::readBytes)
            }
        }

        loadDexes(list)
    }

    fun loadDexes(dexPaths: Array<String>) {
        val list = mutableListOf<ByteArray>()

        dexPaths.forEach {
            list += BufferedInputStream(FileInputStream(it))
                .use(InputStream::readBytes)
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
                                    dexItemClickListener?.onDexClassItemClick(dexItem.dexClassDef)
                                    return
                                }
                            }

                        }
                    }
            })

        binding.root.removeAllViews()
        binding.root.addView(treeView.view, ViewGroup.LayoutParams(-1, -1))
    }

}
