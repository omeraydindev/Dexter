package ma.dexter.util

import org.junit.After
import org.junit.Before
import java.io.File

open class BaseTestClass {
    protected lateinit var testFolder: File

    @Before
    fun setUp() {
        testFolder = File(javaClass.getResource("/")!!.file, javaClass.name)

        testFolder.deleteRecursively()
        testFolder.mkdirs()
        println("Used test folder: ${testFolder.absolutePath}")
    }

    @After
    fun tearDown() {
        testFolder.deleteRecursively()
    }

}
