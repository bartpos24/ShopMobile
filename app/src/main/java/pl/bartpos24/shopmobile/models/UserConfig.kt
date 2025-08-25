package pl.bartpos24.shopmobile.models

import android.os.Parcelable
import com.fredporciuncula.flow.preferences.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import pl.bartpos24.shopmobile.utilities.apiBasePath
import pl.bartpos24.shopmobile.utilities.apiBasePathBeta
import pl.bartpos24.shopmobile.utilities.apiBasePathDevelopment

@JsonClass(generateAdapter = true)
@Parcelize
data class UserConfig(
    @Json(name = "webApiUrl")
    val webApiUrl: String = if (BuildConfig.DEBUG) apiBasePathDevelopment else if (BuildConfig.BUILD_TYPE == "beta") apiBasePathBeta else apiBasePath, // initialize with default url
    @Json(name = "dayNightMode")
    val dayNightMode: ThemeMode = ThemeMode.MODE_NIGHT,
    @Json(name = "languageCode")
    val languageCode: String = "pl"
) : Parcelable