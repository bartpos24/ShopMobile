package pl.bartpos24.shopmobile.utilities

inline fun <T> safeApiResult(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        Result.failure(e)
    }
}