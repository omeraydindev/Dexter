package ma.dexter.ui.editor.scheme.smali

import io.github.rosemoe.sora.widget.EditorColorScheme

class SchemeLightSmali : EditorColorScheme() {

    override fun applyDefault() {
        super.applyDefault()

        // Smali specific colors
        run {
            color(SmaliBaseScheme.DIRECTIVE, 0xff800000)
            color(SmaliBaseScheme.ACCESS_MODIFIER, 0xff001BA3)
            color(SmaliBaseScheme.CLASS_DESCRIPTOR, 0xff808000)
            color(SmaliBaseScheme.INT_LITERAL, 0xff1750EB)
            color(SmaliBaseScheme.REGISTER, 0xff1750EB)
            color(SmaliBaseScheme.STRING_LITERAL, 0xff067D17)
            color(SmaliBaseScheme.SIMPLE_NAME, 0xff205060)
            color(SmaliBaseScheme.LINE_COMMENT, 0xff8C8C8C)
        }

        color(WHOLE_BACKGROUND, 0xffffffff)
        color(TEXT_NORMAL, 0xff000000)

        color(LINE_NUMBER_BACKGROUND, 0xffF0F0F0)
        color(LINE_NUMBER, 0xffB0B0B0)
        color(LINE_DIVIDER, 0xffB0B0B0)

        color(SCROLL_BAR_THUMB, 0xffa6a6a6)
        color(SCROLL_BAR_THUMB_PRESSED, 0xff565656)

        color(SELECTED_TEXT_BACKGROUND, 0xff3676b8)
        color(MATCHED_TEXT_BACKGROUND, 0xff32593d)

        color(CURRENT_LINE, 0xffFFFAE3)
        color(SELECTION_INSERT, 0xff42A5F5)
        color(SELECTION_HANDLE, 0xff42A5F5)

        color(BLOCK_LINE, 0xffdddddd)
        color(BLOCK_LINE_CURRENT, 0xff999999)
        color(NON_PRINTABLE_CHAR, 0xffdddddd)

        color(PROBLEM_ERROR, 0xFFFF0000)
    }

    // Quick workaround for Kotlin's naiveness
    private fun color(type: Int, color: Long) {
        setColor(type, color.toInt())
    }

}
