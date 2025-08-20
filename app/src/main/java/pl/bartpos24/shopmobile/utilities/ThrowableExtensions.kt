package pl.bartpos24.shopmobile.utilities

import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.infrastructure.ServerException

fun Throwable.toShopApiMessage() = when (this) {
    is ServerException -> this.response?.toString() ?: "Undefined error"
    is ClientException -> this.response?.toString() ?: "Undefined error"
    else -> { this.message ?: "Undefined error" }
}