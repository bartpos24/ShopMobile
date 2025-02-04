/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport",
)

package pl.bartpos24.web.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

/**
 *
 *
 * @param date
 * @param temperatureC
 * @param temperatureF
 * @param summary
 */
@Parcelize
data class WeatherForecast(

    @Json(name = "date")
    val date: org.threeten.bp.LocalDate? = null,

    @Json(name = "temperatureC")
    val temperatureC: kotlin.Int? = null,

    @Json(name = "temperatureF")
    val temperatureF: kotlin.Int? = null,

    @Json(name = "summary")
    val summary: kotlin.String? = null,

) : Parcelable
