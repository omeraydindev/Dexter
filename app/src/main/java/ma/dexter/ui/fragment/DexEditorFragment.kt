package ma.dexter.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import ma.dexter.R
import ma.dexter.managers.DexProjectManager
import ma.dexter.databinding.FragmentDexEditorBinding
import ma.dexter.databinding.ItemDexTreeNodeBinding
import ma.dexter.model.tree.DexClassItem
import ma.dexter.model.tree.DexItem
import ma.dexter.ui.component.tree.*
import ma.dexter.ui.component.tree.base.BaseNodeViewBinder
import ma.dexter.ui.component.tree.base.BaseNodeViewFactory
import ma.dexter.ui.util.dp
import ma.dexter.core.SmaliTree
import ma.dexter.ui.util.setMargins
import org.jf.dexlib2.dexbacked.DexBackedClassDef
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.InputStream
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
        val list = mutableListOf<InputStream>()

        for (entry in apk.entries()) {
            if (!entry.isDirectory && entry.name.startsWith("classes") && entry.name.endsWith(".dex")) {
                list += BufferedInputStream(apk.getInputStream(entry))
            }
        }

        loadDexes(list)
    }

    fun loadDexes(dexPaths: Array<String>) {
        val list = mutableListOf<InputStream>()

        dexPaths.forEach {
            list += BufferedInputStream(FileInputStream(it))
        }

        loadDexes(list)
    }

    private fun loadDexes(inputStreams: List<InputStream>) {
        val dexTree = SmaliTree()
            .addDexes(inputStreams.toTypedArray())

        DexProjectManager.dexList = dexTree.dexList

        val treeView = TreeView(dexTree.createTree(), requireContext(), object : BaseNodeViewFactory<DexItem>() {

            override fun getNodeLayoutId(level: Int) = R.layout.item_dex_tree_node

            override fun getNodeViewBinder(view: View, level: Int) =

                object : BaseNodeViewBinder<DexItem>(view) {
                    lateinit var icon: ImageView

                    override fun bindView(treeNode: TreeNode<DexItem>) {
                        val binding = ItemDexTreeNodeBinding.bind(view).apply {
                            title.text = treeNode.value.path

                            root.setMargins(
                                left = level * 20.dp
                            )
                        }

                        icon = binding.icon
                        icon.rotation = if (treeNode.isExpanded) 90F else 0F
                        icon.visibility = if (treeNode.isLeaf()) View.INVISIBLE else View.VISIBLE
                    }

                    override fun onNodeToggled(treeNode: TreeNode<DexItem>, expand: Boolean) {
                        icon.animate()
                            .rotation(if (expand) 90F else 0F)
                            .setDuration(150)
                            .start()

                        treeNode.value.run {
                            if (this is DexClassItem) {
                                dexItemClickListener?.onDexClassItemClick(this.dexClassDef)
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
