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
import ma.dexter.dex.DexGotoManager
import ma.dexter.project.Workspace
import ma.dexter.ui.BaseActivity
import ma.dexter.ui.BaseFragment
import ma.dexter.ui.tree.TreeNode
import ma.dexter.ui.tree.TreeView
import ma.dexter.ui.tree.dex.DexClassNode
import ma.dexter.ui.tree.dex.SmaliTree
import ma.dexter.ui.tree.dex.binder.DexItemNodeViewFactory
import ma.dexter.ui.viewmodel.MainViewModel
import ma.dexter.util.createClassDef
import ma.dexter.util.getClassDescriptor
import ma.dexter.util.getPath
import ma.dexter.util.toast

class DexEditorFragment : BaseFragment() {
    private lateinit var binding: FragmentDexEditorBinding
    private lateinit var treeView: TreeView<DexClassNode>

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
        viewModel.dexProject.observe(viewLifecycleOwner) {
            Workspace.openProject(it)
            structureTree()
        }
    }

    private fun structureTree() {
        val dexTree = SmaliTree()
            .addDexEntries(
                Workspace.getOpenedProject()
                    .dexContainer.entries
            )

        val binder = DexItemNodeViewFactory(
            toggleListener = { treeNode ->
                if (treeNode.isLeaf) {
                    DexGotoManager(requireActivity())
                        .gotoClassDef(treeNode.getClassDescriptor())
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
        treeNode: TreeNode<DexClassNode>
    ) {
        val dexContainer = Workspace.getOpenedProject()
            .dexContainer

        if (treeNode.isLeaf) {
            dexContainer.deleteClassDef(treeNode.getClassDescriptor())
        } else {
            dexContainer.deletePackage(treeNode.getPath())
        }

        treeNode.parent.removeChild(treeNode)
        refreshTreeView()
    }

    private fun addClass(
        treeNode: TreeNode<DexClassNode>?
    ) {
        if (treeNode == null) return

        if (treeNode.isLeaf) {
            addClass(treeNode.parent)
            return
        }

        val dialogBinding = DialogCreateSmaliFileBinding.inflate(layoutInflater)
        val biMap = Workspace.getOpenedProject()
            .dexContainer.biMap

        dialogBinding.etDexFile.setAdapter(
            ArrayAdapter(
                requireContext(), android.R.layout.simple_list_item_1,
                biMap.values.toList()
            )
        )

        dialogBinding.etPackageName.setText(treeNode.getPath())

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Create class")
            .setView(dialogBinding.root)
            .setPositiveButton("OK") { _, _ ->
                val className = dialogBinding.etClassName.text.toString()
                var packagePath = dialogBinding.etPackageName.text.toString()

                // ""   -> ""
                // "/"  -> ""
                // "a"  -> "a/"
                // "a/" -> "a/"
                if (packagePath == "/") {
                    packagePath = ""
                } else if (packagePath.isNotEmpty() && !packagePath.endsWith("/")) {
                    packagePath += "/"
                }

                val dexFile = dialogBinding.etDexFile.text.toString()

                if (className.isEmpty() || dexFile.isEmpty()) {
                    toast("Please fill in all fields")
                    return@setPositiveButton
                }

                val classDef = createClassDef("L$packagePath$className;")
                val dex = biMap.inverse()[dexFile]!!

                dex.addClassDef(classDef)

                // costly operation but ensures that the tree is structured correctly
                // TODO: optimize
                structureTree()
            }
            .show()
    }

    private fun refreshTreeView() {
        treeView.refreshTreeView()
    }

}
