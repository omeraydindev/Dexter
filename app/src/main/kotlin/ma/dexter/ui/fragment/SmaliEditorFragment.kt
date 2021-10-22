package ma.dexter.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.github.zawadz88.materialpopupmenu.MaterialPopupMenuBuilder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ma.dexter.R
import ma.dexter.dex.MutableClassDef
import ma.dexter.editor.lang.smali.SmaliLanguage
import ma.dexter.editor.scheme.smali.SchemeLightSmali
import ma.dexter.editor.util.smali.SmaliActionPopupWindow
import ma.dexter.model.JavaGotoDef
import ma.dexter.model.SmaliGotoDef
import ma.dexter.parsers.smali.SmaliMember
import ma.dexter.parsers.smali.parseSmali
import ma.dexter.project.DexGotoManager
import ma.dexter.project.DexProject
import ma.dexter.tasks.BaksmaliTask
import ma.dexter.tasks.Smali2JavaTask
import ma.dexter.tasks.SmaliTask
import ma.dexter.tools.decompilers.BaseDecompiler
import ma.dexter.util.*

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

        loadSmaliCode(smaliGotoDef.defDescriptor)
    }

    override fun beforeBuildMoreMenu(popupMenuBuilder: MaterialPopupMenuBuilder) {
        popupMenuBuilder.section {
            item {
                label = "Navigation"
                iconDrawable = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_view_stream_24
                )
                callback = ::showNavigationDialog
            }
            item {
                label = "Smali to Java"
                iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_java)
                callback = ::showSmali2JavaDialog
            }
        }
    }

    override fun save() {
        val dialog = ProgressDialog.show(
            requireContext(), "Loading", "Running smali...", true, false
        )

        SmaliTask.execute(codeEditor.text.toString()) {
            dialog.dismiss()

            if (it.success && it.value != null) {
                classDef = MutableClassDef(classDef.parentDex, it.value)

                classDef.parentDex.replaceClassDef(it.value)

                DexProject.getOpenedProject()
                    .smaliContainer.putSmaliCode(it.value.type, codeEditor.text.toString())

                debugToast("Success")
            } else {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Error")
                    .setMessage(it.error)
                    .show()
            }
        }
    }

    private fun loadSmaliCode(
        defDescriptorToGo: String? = null
    ) {
        val dialog = ProgressDialog.show(
            requireContext(), "Loading", "Running baksmali...", true, false
        )

        BaksmaliTask.execute(classDef) {
            dialog.dismiss()

            codeEditor.setText(it)
            gotoDefDescriptor(defDescriptorToGo)
        }
    }

    private fun gotoDefDescriptor(
        defDescriptorToGo: String? = null
    ) {
        defDescriptorToGo?.let { desc ->
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
        val innerClasses =
            DexProject.getOpenedProject().dexContainer.getInnerClasses(classDef)

        if (innerClasses.isNotEmpty()) {
            val innerClassesArray = innerClasses.map { getNameFromSmaliPath(it.type) }.toTypedArray()
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
                    className = getClassDefPath(classDef.type),
                    decompiler = decompilers[pos]
                )
            }.show()
    }

    private fun runSmali2Java(
        innerClassDefs: List<MutableClassDef>,
        className: String,
        decompiler: BaseDecompiler
    ) {
        val dialog = ProgressDialog.show(requireContext(), "Loading", "")
        dialog.setCancelable(false)

        Smali2JavaTask.execute(
            innerClassDefs + classDef,
            className, decompiler, progress = dialog::setMessage
        ) {
            dialog.dismiss()

            when (it) {
                is Smali2JavaTask.Success -> {
                    DexGotoManager(requireActivity())
                        .gotoJavaViewer(JavaGotoDef(className, it.javaCode))
                }
                is Smali2JavaTask.Error -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Error: ${it.title}")
                        .setMessage(it.message)
                        .show()
                }
            }
        }
    }

}
