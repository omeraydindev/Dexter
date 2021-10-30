package ma.dexter.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ma.dexter.R
import ma.dexter.databinding.DialogCreateSmaliFileBinding
import ma.dexter.databinding.FragmentDexEditorBinding
import ma.dexter.dex.DexFactory
import ma.dexter.dex.MutableDexFile
import ma.dexter.model.SmaliGotoDef
import ma.dexter.project.DexGotoManager
import ma.dexter.project.DexProject
import ma.dexter.ui.base.BaseActivity
import ma.dexter.ui.base.BaseFragment
import ma.dexter.ui.tree.TreeNode
import ma.dexter.ui.tree.TreeView
import ma.dexter.ui.tree.dex.SmaliTree
import ma.dexter.ui.tree.dex.binder.DexItemNodeViewFactory
import ma.dexter.ui.tree.model.DexClassItem
import ma.dexter.ui.tree.model.DexItem
import ma.dexter.ui.viewmodel.MainViewModel
import ma.dexter.util.createClassDef
import ma.dexter.util.getPackageName
import ma.dexter.util.toast
import java.io.File

class DexEditorFragment : BaseFragment() {
    private lateinit var binding: FragmentDexEditorBinding
    private lateinit var treeView: TreeView<DexItem>

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDexEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.dexPaths.observe(viewLifecycleOwner, ::loadDexes)
    }

    private fun loadDexes(
        dexPaths: Array<String>
    ) {
        loadDexes(dexPaths.map {
            DexFactory.fromFile(File(it))
        })
    }

    private fun loadDexes(
        dexEntries: List<MutableDexFile>
    ) {
        val dexTree = SmaliTree()
            .addDexEntries(dexEntries)

        DexProject.openProject(dexEntries)

        val binder = DexItemNodeViewFactory(
            toggleListener = { treeNode ->
                val dexItem = treeNode.value

                if (dexItem is DexClassItem) {
                    DexGotoManager(requireActivity())
                        .gotoClassDef(SmaliGotoDef(dexItem.classDef))
                }
            },

            longClickListener = { view, treeNode ->
                val popupMenu = PopupMenu(context, view)
                popupMenu.menuInflater.inflate(R.menu.menu_dex_tree_item, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.it_add -> addClass(treeNode)
                        R.id.it_delete -> deleteClass(treeNode)
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

    private fun deleteClass(
        treeNode: TreeNode<DexItem>
    ) {
        val item = treeNode.value

        if (item is DexClassItem) {
            item.classDef.parentDex.deleteClassDef(item.classDef)
        } else {
            DexProject.getOpenedProject()
                .dexContainer.deletePackage(treeNode.getPackageName())
        }

        treeNode.parent.removeChild(treeNode)
        refreshTreeView()
    }

    private fun addClass(
        treeNode: TreeNode<DexItem>?
    ) {
        if (treeNode == null) return

        if (treeNode.value is DexClassItem) {
            addClass(treeNode.parent)
            return
        }

        val dialogBinding = DialogCreateSmaliFileBinding.inflate(layoutInflater)
        val biMap = DexProject.getOpenedProject().dexContainer.biMap

        dialogBinding.etDexFile.setAdapter(
            ArrayAdapter(
                requireContext(), android.R.layout.simple_list_item_1,
                biMap.values.toList()
            )
        )

        dialogBinding.etPackageName.setText(treeNode.getPackageName())

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Create class")
            .setView(dialogBinding.root)
            .setPositiveButton("OK") { _, _ ->
                val className = dialogBinding.etClassName.text.toString()
                val packagePath = dialogBinding.etPackageName.text.toString()
                val dexFile = dialogBinding.etDexFile.text.toString()

                if (className.isEmpty() || dexFile.isEmpty()) {
                    toast("Please fill in all fields")
                    return@setPositiveButton
                }

                val classDef = createClassDef("L$packagePath/$className;")
                val dex = biMap.inverse()[dexFile]!!

                dex.addClassDef(classDef)

                // costly operation but ensures that the tree is structured correctly
                // TODO: optimize
                loadDexes(DexProject.getOpenedProject().dexContainer.entries)
            }
            .show()
    }

    private fun refreshTreeView() {
        treeView.refreshTreeView()
    }

}
