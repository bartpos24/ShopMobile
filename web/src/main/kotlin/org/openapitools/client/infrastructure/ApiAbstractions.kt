package org.openapitools.client.infrastructure

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import java.io.IOException

typealias MultiValueMap = MutableMap<String, List<String>>

fun collectionDelimiter(collectionFormat: String) = when (collectionFormat) {
    "csv" -> ","
    "tsv" -> "\t"
    "pipe" -> "|"
    "space" -> " "
    else -> ""
}

val defaultMultiValueConverter: (item: Any?) -> String = { item -> "$item" }

fun <T : Any?> toMultiValue(items: Array<T>, collectionFormat: String, map: (item: T) -> String = defaultMultiValueConverter) = toMultiValue(items.asIterable(), collectionFormat, map)

fun <T : Any?> toMultiValue(items: Iterable<T>, collectionFormat: String, map: (item: T) -> String = defaultMultiValueConverter): List<String> {
    return when (collectionFormat) {
        "multi" -> items.map(map)
        else -> listOf(items.joinToString(separator = collectionDelimiter(collectionFormat), transform = map))
    }
}

//suspend fun Call.await(): Response = suspendCancellableCoroutine { cancellableContinuation ->
//    cancellableContinuation.invokeOnCancellation {
//        cancel()
//    }
//    enqueue(object : Callback {
//        override fun onResponse(call: Call, response: Response) {
//            cancellableContinuation.resumeWith(Result.success(response))
//        }
//        override fun onFailure(call: Call, e: IOException) {
//            cancellableContinuation.resumeWith(Result.failure(e))
//        }
//    })
//}
