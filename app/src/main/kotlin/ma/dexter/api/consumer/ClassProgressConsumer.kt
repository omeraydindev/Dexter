package ma.dexter.api.consumer

fun interface ClassProgressConsumer {

    fun consume(
        className: String,
        current: Int,
        total: Int,
    ): Unit

}
