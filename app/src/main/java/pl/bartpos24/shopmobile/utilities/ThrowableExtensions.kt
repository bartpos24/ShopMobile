package pl.bartpos24.shopmobile.utilities

import com.google.android.gms.common.api.Api
import org.openapitools.client.infrastructure.ClientError
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.infrastructure.ServerError
import org.openapitools.client.infrastructure.ServerException

fun Throwable.toShopApiMessage() = when (this) {
    is ServerException -> {
        when(this.response) {
            is ServerError<*> -> {
                val body = (this.response as? ServerError<*>)?.body
                when(body) {
                    is String -> body
                    null -> this.message ?: "Undefined error"
                    else -> body.toString()
                }
            }
            else -> this.message ?: "Undefined error"
        }
    }
    is ClientException -> {
        when(this.response) {
            is ClientError<*> ->  {
                val body = (this.response as? ClientError<*>)?.body
                when(body) {
                    is String -> body
                    null -> this.message ?: "Undefined error"
                    else -> body.toString()
                }
            }
            else -> this.message ?: "Undefined error"
        }
    }
//    is ServerException -> this.response?.toString() ?: "Undefined error"
//    is ClientException -> this.response?.toString() ?: "Undefined error"
    else -> { this.message ?: "Undefined error" }
}