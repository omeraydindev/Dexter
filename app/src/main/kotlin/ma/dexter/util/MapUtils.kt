package ma.dexter.util

fun <K, V> Map<K, V>.inverseMap() =
    map { it.value to it.key }.toMap()
