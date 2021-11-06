package ma.dexter.ui.editor.lang.smali.model

data class SmaliAutoCompleteModel(
    val classDescs: Set<SmaliClassDesc>
)

data class SmaliClassDesc(
    val name: String
)
