package ma.dexter.tasks

sealed interface ITask<T>

abstract class Task<T> : ITask<T> {
    abstract fun run(): Result<T>
}

abstract class ProgressTask<T> : ITask<T> {
    abstract fun run(progress: (String) -> Unit): Result<T>
}

class Result<T> private constructor(
    val value: T? = null,
    val success: Boolean,
    val error: Error = Error()
) {
    companion object {
        fun <T> success(value: T): Result<T> {
            return Result(success = true, value = value)
        }

        fun <T> failure(title: String, message: String): Result<T> {
            return Result(success = false, error = Error(title, message))
        }
    }
}

class Error(
    val title: String = "",
    val message: String = ""
)
