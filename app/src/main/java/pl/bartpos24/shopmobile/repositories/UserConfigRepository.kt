package pl.bartpos24.shopmobile.repositories

import android.content.SharedPreferences
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.bartpos24.shopmobile.models.UserConfig

class UserConfigRepository(private val sharedPreferences: SharedPreferences, private val moshi: Moshi) {
    private val flowSharedPreferences = FlowSharedPreferences(sharedPreferences)
    private val userConfigPreference = flowSharedPreferences.getString("userConfigJson", "{}")
    private val userConfigAdapter = moshi.adapter(UserConfig::class.java)
    private val _userConfig = MutableStateFlow(userConfigAdapter.fromJson(userConfigPreference.get()) ?: UserConfig())
    val userConfig: StateFlow<UserConfig> get() = _userConfig
}