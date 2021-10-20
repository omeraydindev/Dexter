package ma.dexter.ui.util

import android.graphics.drawable.Drawable
import com.github.zawadz88.materialpopupmenu.MaterialPopupMenuBuilder.CustomItemHolder
import com.github.zawadz88.materialpopupmenu.ViewBoundCallback
import ma.dexter.R
import ma.dexter.databinding.PopupMenuItemCheckableBinding

fun CustomItemHolder.checkableItem(init: CheckableItemHolder.() -> Unit) {
    val holder = CheckableItemHolder()
    init(holder)

    dismissOnSelect = false
    layoutResId = R.layout.popup_menu_item_checkable
    viewBoundCallback = ViewBoundCallback { v ->
        PopupMenuItemCheckableBinding.bind(v).apply {
            label.text = holder.label
            icon.setImageDrawable(holder.iconDrawable)

            checkbox.isChecked = holder.checked
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                holder.callback(isChecked)
                dismissPopup()
            }
        }
    }
}

class CheckableItemHolder {
    var label = ""
    var iconDrawable: Drawable? = null
    var checked = false
    var callback: (checked: Boolean) -> Unit = {}
}
