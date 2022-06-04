package ma.dexter.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.fragment.app.activityViewModels
import com.github.zawadz88.materialpopupmenu.MaterialPopupMenuBuilder
import io.github.rosemoe.sora.widget.CodeEditor
import ma.dexter.R
import ma.dexter.databinding.FragmentBaseCodeEditorBinding
import ma.dexter.ui.BaseFragment
import ma.dexter.ui.editor.util.setDefaults
import ma.dexter.ui.editor.util.setOnTextChangedListener
import ma.dexter.ui.util.checkableItem
import ma.dexter.ui.viewmodel.MainViewModel

open class BaseCodeEditorFragment : BaseFragment() {
    private lateinit var binding: FragmentBaseCodeEditorBinding
    private val viewModel: MainViewModel by activityViewModels()

    protected lateinit var codeEditor: CodeEditor
    private var isEdited = false

    private val exportResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data ?: return@registerForActivityResult
                val contentResolver = activity?.contentResolver ?: return@registerForActivityResult

                contentResolver.openOutputStream(uri)?.use {
                    val code = codeEditor.text.toString()
                    it.write(code.toByteArray())
                }
            }
        }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaseCodeEditorBinding.inflate(inflater, container, false)
        codeEditor = binding.codeEditor
        return binding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        codeEditor.setDefaults()

        codeEditor.setOnTextChangedListener {
            isEdited = true
        }

        viewModel.viewPagerScrolled.observe(viewLifecycleOwner) {
            codeEditor.hideAutoCompleteWindow()
            codeEditor.textActionPresenter.onExit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_base_code_editor, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.it_undo -> codeEditor.undo()
            R.id.it_redo -> codeEditor.redo()
            R.id.it_save -> save()
            R.id.it_more -> showMoreMenu(requireActivity().findViewById(R.id.it_more))
        }

        return true
    }

    // @CallSuper
    protected open fun save() {

    }

    private fun export() {
        val currentTabTitle = viewModel.currentPosition.value?.let { pos ->
            val pageItems = viewModel.getPageItems().value
            pageItems?.get(pos)?.getTitle()
        }

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_TITLE, currentTabTitle.orEmpty())
        }

        exportResultLauncher.launch(intent)
    }

    private fun showMoreMenu(anchorView: View) {
        val builder = MaterialPopupMenuBuilder()

        beforeBuildMoreMenu(builder)
        builder.section {
            title = "File"

            item {
                label = "Export.."
                iconDrawable = drawable(R.drawable.ic_baseline_save_alt_24)
                callback = ::export
            }
        }
        builder.section {
            title = "Editor"

            item {
                label = "Search"
                iconDrawable = drawable(R.drawable.ic_baseline_search_24)
                callback = codeEditor::beginSearchMode
            }

            customItem {
                checkableItem {
                    label = "Word wrap"
                    iconDrawable = drawable(R.drawable.ic_baseline_wrap_text_24)
                    checked = codeEditor.isWordwrap
                    callback = {
                        codeEditor.isWordwrap = it
                    }
                }
            }

            customItem {
                checkableItem {
                    label = "Auto complete"
                    iconDrawable = drawable(R.drawable.ic_baseline_copyright_24)
                    checked = codeEditor.isAutoCompletionEnabled
                    callback = {
                        codeEditor.isAutoCompletionEnabled = it
                    }
                }
            }

            customItem {
                checkableItem {
                    label = "Magnifier"
                    iconDrawable = drawable(R.drawable.ic_baseline_zoom_in_24)
                    checked = codeEditor.isMagnifierEnabled
                    callback = {
                        codeEditor.isMagnifierEnabled = it
                    }
                }
            }
        }
        afterBuildMoreMenu(builder)

        builder.build()
            .show(requireContext(), anchorView)
    }

    protected open fun beforeBuildMoreMenu(popupMenuBuilder: MaterialPopupMenuBuilder) {
        // no-op
    }

    protected open fun afterBuildMoreMenu(popupMenuBuilder: MaterialPopupMenuBuilder) {
        // no-op
    }

}
