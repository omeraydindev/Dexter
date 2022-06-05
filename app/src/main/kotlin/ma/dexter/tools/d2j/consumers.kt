package ma.dexter.tools.d2j

typealias D2JProgressConsumer = (
    className: String,
    current: Int,
    total: Int,
) -> Unit

typealias D2JConversionConsumer = (
    className: String,
    bytes: ByteArray,
) -> Unit
