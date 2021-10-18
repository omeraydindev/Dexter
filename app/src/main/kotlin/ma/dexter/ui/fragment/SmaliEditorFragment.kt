package ma.dexter.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.github.zawadz88.materialpopupmenu.MaterialPopupMenuBuilder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ma.dexter.R
import ma.dexter.editor.lang.smali.SmaliLanguage
import ma.dexter.editor.scheme.smali.SchemeLightSmali
import ma.dexter.editor.util.smali.SmaliActionPopupWindow
import ma.dexter.managers.DexGotoManager
import ma.dexter.managers.DexProjectManager
import ma.dexter.model.JavaGotoDef
import ma.dexter.model.SmaliGotoDef
import ma.dexter.tasks.BaksmaliTask
import ma.dexter.tasks.Smali2JavaTask
import ma.dexter.tools.decompilers.BaseDecompiler
import ma.dexter.util.getClassDefPath

class SmaliEditorFragment(
    private val smaliGotoDef: SmaliGotoDef
) : BaseCodeEditorFragment() {

    private val classDef = smaliGotoDef.classDef

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(codeEditor) {
            colorScheme = SchemeLightSmali()
            textActionPresenter = SmaliActionPopupWindow(requireActivity(), this)

            setEditorLanguage(SmaliLanguage())
        }

        loadSmali(smaliGotoDef.defDescriptor)
    }

    override fun beforeBuildMoreMenu(popupMenuBuilder: MaterialPopupMenuBuilder) {
        popupMenuBuilder.section {
            item {
                label = "Navigation"
                iconDrawable = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_view_stream_24
                )
                callback = {
                    showNavigationDialog()
                }
            }

            item {
                label = "Smali to Java"
                iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_java)
                callback = {
                    showSmali2JavaDialog()
                }
            }
        }
    }

    private fun loadSmali(
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
            val smaliFile = DexProjectManager.getSmaliModel(
                codeEditor.text.toString()
            )

            val line = smaliFile.smaliMembers.firstOrNull { member ->
                member.descriptor == desc
            }?.line ?: 0

            codeEditor.jumpToLine(line)
        }
    }

    private fun showNavigationDialog() {
        val smaliFile = DexProjectManager.getSmaliModel(
            codeEditor.text.toString()
        )

        val navItems = smaliFile.smaliMembers

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Navigation")
            .setItems(navItems.map { it.descriptor }.toTypedArray()) { _, pos ->
                codeEditor.jumpToLine(navItems[pos].line)
            }
            .show()
    }

    private fun showSmali2JavaDialog() {
        val decompilers = BaseDecompiler.getDecompilers()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Decompiler")
            .setItems(decompilers.map { it.getName() }.toTypedArray()) { _, pos ->
                runSmali2Java(
                    smaliCode = codeEditor.text.toString(),
                    className = getClassDefPath(classDef.type),
                    decompiler = decompilers[pos]
                )
            }.show()
    }

    private fun runSmali2Java(
        smaliCode: String,
        className: String,
        decompiler: BaseDecompiler
    ) {
        val dialog = ProgressDialog.show(requireContext(), "Loading", "")
        dialog.setCancelable(false)

        Smali2JavaTask.execute(
            smaliCode, className, decompiler, progress = dialog::setMessage
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
