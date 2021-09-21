package ma.dexter.editor.lang.smali.model

data class SmaliAutoCompleteModel(
    val classDescs: Set<SmaliClassDesc>,
    val methods: Set<SmaliMethod>,
    val fields: Set<SmaliField>
)
