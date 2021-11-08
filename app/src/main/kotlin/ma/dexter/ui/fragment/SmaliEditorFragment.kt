package ma.dexter.ui.fragment

import android.os.Bundle
import android.view.View
import com.github.zawadz88.materialpopupmenu.MaterialPopupMenuBuilder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ma.dexter.R
import ma.dexter.dex.DexGotoManager
import ma.dexter.dex.MutableClassDef
import ma.dexter.model.JavaGotoDef
import ma.dexter.model.SmaliGotoDef
import ma.dexter.parsers.smali.SmaliMember
import ma.dexter.parsers.smali.parseSmali
import ma.dexter.project.Workspace
import ma.dexter.tasks.BaksmaliTask
import ma.dexter.tasks.Smali2JavaTask
import ma.dexter.tasks.SmaliTask
import ma.dexter.tasks.runWithDialog
import ma.dexter.tools.decompilers.BaseDecompiler
import ma.dexter.ui.editor.lang.smali.SmaliLanguage
import ma.dexter.ui.editor.scheme.smali.SchemeLightSmali
import ma.dexter.ui.editor.util.smali.SmaliActionPopupWindow
import ma.dexter.util.getClassNameFromSmaliPath
import ma.dexter.util.hideKeyboard
import ma.dexter.util.normalizeSmaliPath
import ma.dexter.util.toast

class SmaliEditorFragment(
    private val smaliGotoDef: SmaliGotoDef
) : BaseCodeEditorFragment() {

    private var classDef = smaliGotoDef.classDef

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(codeEditor) {
            colorScheme = SchemeLightSmali()
            textActionPresenter = SmaliActionPopupWindow(requireActivity(), this)

            setEditorLanguage(SmaliLanguage())
        }

        loadSmaliCode(smaliGotoDef.memberDescriptorToGo)
    }

    override fun beforeBuildMoreMenu(popupMenuBuilder: MaterialPopupMenuBuilder) {
        popupMenuBuilder.section {
            item {
                label = "Navigation"
                iconDrawable = drawable(R.drawable.ic_baseline_view_stream_24)
                callback = ::showNavigationDialog
            }
            item {
                label = "Smali to Java"
                iconDrawable = drawable(R.drawable.ic_java)
                callback = ::showSmali2JavaDialog
            }
        }
    }

    override fun save() {
        SmaliTask(codeEditor.text.toString())
            .runWithDialog(requireContext(), "Loading", "Running smali...") {
                if (it.value != null) {
                    classDef = MutableClassDef(classDef.parentDex, it.value)
                    classDef.parentDex.replaceClassDef(it.value)

                    Workspace.getOpenedProject()
                        .smaliContainer.putSmaliCode(it.value.type, codeEditor.text.toString())

                    toast("Saved successfully")
                }
            }
    }

    private fun loadSmaliCode(
        memberDescriptorToGo: String? = null
    ) {
        BaksmaliTask(classDef)
            .runWithDialog(requireContext(), "Loading", "Running baksmali...") {
                if (it.value != null) {
                    codeEditor.setText(it.value)
                    gotoMemberDescriptor(memberDescriptorToGo)
                }
            }
    }

    private fun gotoMemberDescriptor(
        memberDescriptorToGo: String? = null
    ) {
        memberDescriptorToGo?.let { desc ->
            val smaliFile = parseSmali(codeEditor.text.toString())

            val member = smaliFile.members.firstOrNull { member ->
                member.descriptor == desc
            }

            if (member != null) {
                gotoMemberDefinition(member)
            }
        }
    }

    private fun showNavigationDialog() {
        val smaliFile = parseSmali(codeEditor.text.toString())

        val navItems = smaliFile.members

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Navigation")
            .setItems(navItems.map { it.descriptor }.toTypedArray()) { _, pos ->
                gotoMemberDefinition(navItems[pos])
            }
            .show()
    }

    private fun gotoMemberDefinition(member: SmaliMember) {
        hideKeyboard(codeEditor)

        codeEditor.setSelectionRegion(
            member.line, member.nameIndex,
            member.line, member.nameIndex + member.name.length,
            true
        )
    }

    private fun showSmali2JavaDialog() {
        val innerClasses = Workspace.getOpenedProject()
            .dexContainer.getInnerClasses(classDef)

        if (innerClasses.isNotEmpty()) {
            val innerClassesArray =
                innerClasses.map { getClassNameFromSmaliPath(it.type) }.toTypedArray()
            val checkedArray = BooleanArray(innerClassesArray.size) { true }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Select inner classes to decompile")
                .setMultiChoiceItems(innerClassesArray, checkedArray) { _, _, _ -> }
                .setPositiveButton("OK") { _, _ ->

                    showDecompilerDialog(buildList {
                        innerClasses.forEachIndexed { index, mutableClassDef ->
                            if (checkedArray[index]) {
                                add(mutableClassDef)
                            }
                        }
                    })

                }
                .show()
        } else {
            showDecompilerDialog(listOf())
        }
    }

    private fun showDecompilerDialog(innerClassDefs: List<MutableClassDef>) {
        val decompilers = BaseDecompiler.getDecompilers()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Decompiler")
            .setItems(decompilers.map { it.getName() }.toTypedArray()) { _, pos ->
                runSmali2Java(
                    innerClassDefs,
                    className = normalizeSmaliPath(classDef.type),
                    decompiler = decompilers[pos]
                )
            }.show()
    }

    private fun runSmali2Java(
        innerClassDefs: List<MutableClassDef>,
        className: String,
        decompiler: BaseDecompiler
    ) {
        Smali2JavaTask(
            classDefs = innerClassDefs + classDef,
            className, decompiler
        ).runWithDialog(requireContext(), "Decompiling", "") {
            if (it.value != null) {
                DexGotoManager(requireActivity())
                    .gotoJavaViewer(JavaGotoDef(className, it.value))
            }
        }
    }

}
