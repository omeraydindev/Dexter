package ma.dexter.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ma.dexter.databinding.DialogProgressSimpleBinding

class ProgressDialog(
    private val context: Context,
    private val title: String,
    private val message: String = "",
    private val cancelable: Boolean = false
) {
    private lateinit var viewBinding: DialogProgressSimpleBinding
    private lateinit var backingDialog: AlertDialog

    fun create(): ProgressDialog {
        viewBinding = DialogProgressSimpleBinding.inflate(LayoutInflater.from(context))
        viewBinding.message.text = message

        backingDialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setCancelable(cancelable)
            .setView(viewBinding.root)
            .create()

        return this
    }

    fun show(): ProgressDialog {
        create()
        backingDialog.show()
        return this
    }

    fun dismiss() {
        backingDialog.dismiss()
    }

    fun setMessage(message: String) {
        viewBinding.message.text = message
    }
}
