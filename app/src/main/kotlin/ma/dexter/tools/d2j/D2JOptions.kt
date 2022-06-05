package ma.dexter.tools.d2j

import com.googlecode.d2j.dex.V3
import com.googlecode.d2j.reader.DexFileReader

class D2JOptions(
    var reuseReg: Boolean = false,
    var topoLogicalSort: Boolean = false,
    var noCode: Boolean = false,
    var optimizeSynchronized: Boolean = true,
    var printIR: Boolean = false,
    var skipDebug: Boolean = true, //TODO: doesn't work?
    var skipExceptions: Boolean = false,
) {
    fun getReaderConfig(): Int {
        var readerConfig = 0 or DexFileReader.SKIP_DEBUG

        if (noCode) {
            readerConfig =
                readerConfig or (DexFileReader.SKIP_CODE or DexFileReader.KEEP_CLINIT)
        } else {
            readerConfig =
                readerConfig and (DexFileReader.SKIP_CODE or DexFileReader.KEEP_CLINIT).inv()
        }

        if (skipDebug) {
            readerConfig = readerConfig or DexFileReader.SKIP_DEBUG
        } else {
            readerConfig = readerConfig and DexFileReader.SKIP_DEBUG.inv()
        }

        if (skipExceptions) {
            readerConfig = readerConfig or DexFileReader.SKIP_EXCEPTION
        } else {
            readerConfig = readerConfig and DexFileReader.SKIP_EXCEPTION.inv()
        }

        return readerConfig
    }

    fun getV3Config(): Int {
        var v3Config = 0

        if (reuseReg) {
            v3Config = v3Config or V3.REUSE_REGISTER
        } else {
            v3Config = v3Config and V3.REUSE_REGISTER.inv()
        }

        if (topoLogicalSort) {
            v3Config = v3Config or V3.TOPOLOGICAL_SORT
        } else {
            v3Config = v3Config and V3.TOPOLOGICAL_SORT.inv()
        }

        if (optimizeSynchronized) {
            v3Config = v3Config or V3.OPTIMIZE_SYNCHRONIZED
        } else {
            v3Config = v3Config and V3.OPTIMIZE_SYNCHRONIZED.inv()
        }

        if (printIR) {
            v3Config = v3Config or V3.PRINT_IR
        } else {
            v3Config = v3Config and V3.PRINT_IR.inv()
        }

        return v3Config
    }
}
