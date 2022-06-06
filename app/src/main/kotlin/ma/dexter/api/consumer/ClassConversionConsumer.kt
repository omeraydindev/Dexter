package ma.dexter.api.consumer

fun interface ClassConversionConsumer {

    fun consume(
        className: String,
        bytes: ByteArray,
    ): Unit

}
