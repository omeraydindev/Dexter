package ma.dexter.ui.activity.code

import android.app.ProgressDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ma.dexter.R
import ma.dexter.core.model.SmaliMethod
import ma.dexter.managers.DexProjectManager
import ma.dexter.databinding.ActivitySmaliEditorBinding
import ma.dexter.editor.lang.smali.SmaliLanguage
import ma.dexter.editor.scheme.smali.SchemeLightSmali
import ma.dexter.editor.util.SmaliActionPopupWindow
import ma.dexter.model.tree.DexClassItem
import ma.dexter.tasks.BaksmaliTask
import ma.dexter.tasks.Smali2JavaTask
import ma.dexter.tools.decompilers.BaseDecompiler
import ma.dexter.ui.activity.BaseActivity
import ma.dexter.ui.util.setDefaults
import ma.dexter.util.getClassDefPath
import org.jf.dexlib2.dexbacked.DexBackedClassDef

class SmaliEditorActivity : BaseActivity() {
    private lateinit var binding: ActivitySmaliEditorBinding
    private lateinit var dexClassDef: DexBackedClassDef

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmaliEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DexProjectManager.currentGotoDef.use {
            it.value?.let { def ->
                dexClassDef = def.dexBackedClassDef
                loadSmali(def.defDescriptor)
                return@use
            }

            Toast.makeText(this, "Dex was null", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadSmali(
        defDescriptorToGo: String? = null
    ) {
        val path = getClassDefPath(dexClassDef.type)

        title    = path.substringAfterLast("/")
        subtitle = path.substringBeforeLast("/")

        with(binding.codeEditor) {
            colorScheme = SchemeLightSmali()

            setDefaults()
            setEditorLanguage(SmaliLanguage())
            setTextActionPresenter(SmaliActionPopupWindow(this))
        }

        val dialog = ProgressDialog.show(
            this,
            "Loading", "Running baksmali..."
        )
        dialog.setCancelable(false)

        BaksmaliTask.execute(dexClassDef) {
            dialog.dismiss()

            binding.codeEditor.setText(it)
            gotoDefDescriptor(defDescriptorToGo)
        }
    }

    private fun gotoDefDescriptor(
        defDescriptorToGo: String? = null
    ) {
        defDescriptorToGo?.let { desc ->
            val smaliFile = DexProjectManager.getSmaliModel(
                binding.codeEditor.text.toString()
            )

            val members = smaliFile.smaliFields + smaliFile.smaliMethods

            val line = members.firstOrNull { member ->
                member.descriptor == desc
            }?.line

            binding.codeEditor.jumpToLine(line ?: 0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_smali_editor, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.decompile_to_java -> {
                val decompilers = BaseDecompiler.getDecompilers()

                MaterialAlertDialogBuilder(this)
                    .setTitle("Decompiler")
                    .setItems(decompilers.map { it.getName() }.toTypedArray()) { _, pos ->
                        runSmali2Java(decompilers[pos])
                    }.show()
            }

            R.id.navigate_smali -> {
                val smaliFile = DexProjectManager.getSmaliModel(
                    binding.codeEditor.text.toString()
                )

                val navItems = smaliFile.smaliFields + smaliFile.smaliMethods

                MaterialAlertDialogBuilder(this)
                    .setTitle("Navigation")
                    .setItems(navItems.map { it.descriptor }.toTypedArray()) { _, pos ->
                        val navItem = navItems[pos]

                        binding.codeEditor.jumpToLine(navItem.line)

                        if (navItem is SmaliMethod) {
                            Toast.makeText(
                                this,
                                navItem.methodBody,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun runSmali2Java(decompiler: BaseDecompiler) {
        val dialog = ProgressDialog.show(this, "Loading", "")
        dialog.setCancelable(false)

        Smali2JavaTask.execute(
            binding.codeEditor.text.toString(), decompiler, dialog::setMessage
        ) {
            dialog.dismiss()

            when (it) {
                is Smali2JavaTask.Success -> {
                    DexProjectManager.gotoJavaViewer(this, it.javaCode)
                }
                is Smali2JavaTask.Error -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("Error: ${it.title}")
                        .setMessage(it.message)
                        .show()
                }
            }
        }
    }

}
