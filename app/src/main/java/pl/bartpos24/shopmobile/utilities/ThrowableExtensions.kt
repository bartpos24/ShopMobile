package pl.bartpos24.shopmobile.utilities

import org.openapitools.client.infrastructure.ClientError
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.infrastructure.ServerError
import org.openapitools.client.infrastructure.ServerException

fun Throwable.toShopApiMessage() = when (this) {
    is ServerException -> {
        when(this.response) {
            is ServerError<*> -> (this.response as ServerError<*>).body.toString() ?: this.message ?: "Undefined error"
            else -> this.message ?: "Undefined error"
        }
    }
    is ClientException -> {
        when(this.response) {
            is ClientError<*> -> (this.response as ClientError<*>).body.toString() ?: this.message ?: "Undefined error"
            else -> this.message ?: "Undefined error"
        }
    }
//    is ServerException -> this.response?.toString() ?: "Undefined error"
//    is ClientException -> this.response?.toString() ?: "Undefined error"
    else -> { this.message ?: "Undefined error" }
}