package ma.dexter.tools.d2j

/**
 * Adopted from [com.googlecode.dex2jar.tools.Dex2jarCmd]
 */
class D2JOptions(
    var reuseReg: Boolean = false,
    var debugInfo: Boolean = true, //TODO: doesn't work?
    var optimizeSynchronized: Boolean = false,
    var skipMethodBodies: Boolean = false,
    var skipExceptions: Boolean = false
)
