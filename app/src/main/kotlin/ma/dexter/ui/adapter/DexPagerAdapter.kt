package ma.dexter.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import ma.dexter.ui.fragment.DexEditorFragment
import ma.dexter.ui.fragment.JavaViewerFragment
import ma.dexter.ui.fragment.SmaliEditorFragment
import ma.dexter.ui.model.DexPageItem
import ma.dexter.ui.model.JavaItem
import ma.dexter.ui.model.MainItem
import ma.dexter.ui.model.SmaliItem

class DexPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    private val data = mutableListOf<DexPageItem>()

    fun updateList(newData: List<DexPageItem>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize() = data.size

            override fun getNewListSize() = newData.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val areSmaliItems =
                    data[oldItemPosition] is SmaliItem && newData[newItemPosition] is SmaliItem
                val areJavaItems =
                    data[oldItemPosition] is JavaItem && newData[newItemPosition] is JavaItem

                return (areJavaItems || areSmaliItems)
                        && itemId(data[oldItemPosition]) == itemId(newData[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return data[oldItemPosition].getGotoDef() == newData[newItemPosition].getGotoDef()
            }
        })
        data.clear()
        data.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    fun getItem(position: Int) = data[position]

    override fun createFragment(position: Int): Fragment {
        return when (
            val item = getItem(position)
        ) {
            is MainItem -> DexEditorFragment()
            is SmaliItem -> SmaliEditorFragment(item.smaliGotoDef)
            is JavaItem -> JavaViewerFragment(item.javaGotoDef)
        }
    }

    override fun getItemId(position: Int): Long {
        return if (data.isEmpty() || position > data.size) {
            -1
        } else itemId(getItem(position))
    }

    fun itemId(dexPageItem: DexPageItem): Long {
        return dexPageItem.typeDef.hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        data.forEach {
            if (it.typeDef.hashCode().toLong() == itemId) {
                return true
            }
        }
        return false
    }

    override fun getItemCount() = data.size
}
