package ma.dexter.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ma.dexter.project.DexProject
import ma.dexter.ui.model.DexPageItem
import ma.dexter.ui.model.MainItem

class MainViewModel : ViewModel() {
    private val dexPageItems = MutableLiveData(mutableListOf<DexPageItem>())

    val currentPosition = MutableLiveData(0)
    val dexProject = MutableLiveData<DexProject>()
    val viewPagerScrolled = MutableLiveData(0)

    fun getPageItems(): LiveData<out List<DexPageItem>> {
        return dexPageItems
    }

    fun addMainItem() {
        val items = dexPageItems.value ?: return
        items.add(MainItem())
        dexPageItems.value = items
    }

    fun gotoPageItem(dexPageItem: DexPageItem) {
        val items = dexPageItems.value ?: return

        items.forEachIndexed { position, item ->
            if (item.typeDef == dexPageItem.typeDef) {
                items[position] = dexPageItem

                dexPageItems.value = items
                currentPosition.value = position
                return
            }
        }

        items.add(dexPageItem)
        dexPageItems.value = items
        currentPosition.value = items.lastIndex
    }

    fun removePageItem(position: Int) {
        val items = dexPageItems.value ?: return
        items.removeAt(position)
        dexPageItems.value = items
    }

    fun removeAllPageItems(
        excludePos: Int = -1
    ) {
        val items = dexPageItems.value ?: return
        var index = 0
        val iterator = items.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (index != excludePos && item !is MainItem) {
                iterator.remove()
            }
            index++
        }
        dexPageItems.value = items
    }

}
