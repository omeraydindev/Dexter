package ma.dexter.project

import ma.dexter.dex.MutableDexContainer
import ma.dexter.model.SmaliModel
import ma.dexter.tools.smali.BaksmaliInvoker
import ma.dexter.ui.tree.model.DexClassItem
import ma.dexter.util.getClassDefPath
import org.jf.dexlib2.iface.ClassDef

object DexProjectManager {
    val dexContainer = MutableDexContainer()

    // Smali

    private var smalis = mutableMapOf<DexClassItem, SmaliModel>()

    fun putSmali(dexClassItem: DexClassItem, smaliCode: String) {
        smalis[dexClassItem] = SmaliModel.create(smaliCode)
    }

    fun getSmaliModel(smaliCode: String): SmaliModel {
        return SmaliModel.create(smaliCode)
    }

    fun getSmaliModel(classDef: ClassDef): SmaliModel {
        return getSmaliModel(
            DexClassItem(
                getClassDefPath(classDef.type),
                classDef
            )
        )
    }

    private fun getSmaliModel(dexClassItem: DexClassItem): SmaliModel {
        smalis[dexClassItem]?.let {
            return it
        }

        val smaliFile = SmaliModel.create(
            BaksmaliInvoker.disassemble(dexClassItem.classDef)
        )

        smalis[dexClassItem] = smaliFile
        return smaliFile
    }

}
