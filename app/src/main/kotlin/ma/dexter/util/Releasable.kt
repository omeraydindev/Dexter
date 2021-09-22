package ma.dexter.util

/**
 * Temporary solution.
 */
class Releasable<T> : AutoCloseable {
    var value: T? = null

    override fun close() {
        value = null
    }
}
