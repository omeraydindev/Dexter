package ma.dexter.util

class Releasable<T> : AutoCloseable {
    var value: T? = null

    fun release() {
        value = null
    }

    override fun close() = release()
}
