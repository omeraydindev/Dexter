package ma.dexter.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog
import ma.dexter.R
import ma.dexter.databinding.FragmentDexEditorBinding
import ma.dexter.dex.DexEntry
import ma.dexter.dex.DexFactory
import ma.dexter.project.DexGotoManager
import ma.dexter.project.DexProjectManager
import ma.dexter.dex.MutableDexContainer
import ma.dexter.model.SmaliGotoDef
import ma.dexter.ui.activity.BaseActivity
import ma.dexter.ui.tree.TreeView
import ma.dexter.ui.tree.dex.SmaliTree
import ma.dexter.ui.tree.dex.binder.DexItemNodeViewFactory
import ma.dexter.ui.tree.model.DexClassItem
import ma.dexter.ui.tree.model.DexItem
import ma.dexter.util.isValidDexFileName
import ma.dexter.util.storagePath
import ma.dexter.util.toast
import java.io.File
import java.util.zip.ZipFile

class DexEditorFragment : BaseFragment() {
    private lateinit var binding: FragmentDexEditorBinding
    private lateinit var treeView: TreeView<DexItem>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDexEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLoadDex.setOnClickListener {
            val properties = DialogProperties().apply {
                root = storagePath
                extensions = arrayOf("dex", "apk")
                selection_mode = DialogConfigs.MULTI_MODE
            }

            FilePickerDialog(requireContext(), properties).run {
                setTitle("Select either .dex file(s) or a single .apk file")
                setDialogSelectionListener { files ->
                    if (files.size == 1 && files[0].endsWith(".apk")) {
                        loadApk(files[0])
                    } else if (files.all { it.endsWith(".dex") }) {
                        loadDexes(files)
                    } else {
                        toast("Please select either .dex file(s) or a single .apk file")
                    }
                }
                show()
            }
        }
    }

    private fun loadApk(
        apkPath: String
    ) {
        ZipFile(apkPath).use { apk ->
            loadDexes(buildList {
                apk.stream().forEach { entry ->
                    if (!entry.isDirectory && isValidDexFileName(entry.name)) {
                        val bytes = apk.getInputStream(entry).readBytes()
                        val dexEntry = DexEntry(entry.name,
                            DexFactory.fromByteArray(bytes))

                        add(dexEntry)
                    }
                }
            })
        }
    }

    private fun loadDexes(
        dexPaths: Array<String>
    ) {
        loadDexes(buildList {
            dexPaths.forEach {
                val file = File(it)
                val dexEntry = DexEntry(file.name,
                    DexFactory.fromFile(file))

                add(dexEntry)
            }
        })
    }

    private fun loadDexes(
        dexEntries: List<DexEntry>
    ) {
        val dexTree = SmaliTree()
            .addDexEntries(dexEntries)

        DexProjectManager.dexContainer.entries.clear()
        DexProjectManager.dexContainer.entries.addAll(dexEntries)

        val binder = DexItemNodeViewFactory(
            toggleListener = { treeNode ->
                treeNode.value.also { dexItem ->
                    if (dexItem is DexClassItem) {
                        DexGotoManager(requireActivity())
                            .gotoClassDef(SmaliGotoDef(dexItem.classDef))
                    }
                }
            },

            longClickListener = { view, treeNode ->
                val popupMenu = PopupMenu(context, view)
                popupMenu.menuInflater.inflate(R.menu.menu_dex_tree_item, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.it_add -> {}
                        R.id.it_copy -> {}
                        R.id.it_delete -> {
                            DexProjectManager.dexContainer.deleteClassDef(
                                (treeNode.value as DexClassItem).classDef
                            )
                            treeNode.parent.removeChild(treeNode)
                            refreshTreeView()
                        }
                    }

                    true
                }
                popupMenu.show()

                true
            }
        )

        treeView = TreeView(
            dexTree.createTree(),
            requireContext(),
            binder
        )

        val treeRecyclerView = treeView.view

        binding.root.removeAllViews()
        binding.root.addView(treeRecyclerView, ViewGroup.LayoutParams(-1, -1))

        // TODO: clean-up
        treeRecyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
            val currentPackage = buildString {
                val pos = treeView.layoutManager.findFirstVisibleItemPosition()
                var treeNode = treeView.adapter.expandedNodeList[pos]

                while (!treeNode.isRoot) {
                    treeNode = treeNode.parent

                    if (!treeNode.isLeaf && !treeNode.isRoot) {
                        insert(0, treeNode.value.toString() + ".")
                    }
                }

                if (length != 0) setLength(length - 1) // strip the last "."
            }

            (requireActivity() as BaseActivity).subtitle = currentPackage
        }
    }

    private fun refreshTreeView() {
        treeView.refreshTreeView()
    }

}
