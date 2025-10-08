package pl.bartpos24.shopmobile.utilities

import android.content.SharedPreferences
import androidx.core.content.edit
import com.fredporciuncula.flow.preferences.FlowSharedPreferences

class TokenCache(private val sharedPreferences: SharedPreferences) {

    private val flowSharedPreferences = FlowSharedPreferences(sharedPreferences)

    val accessToken = flowSharedPreferences.getString(accessTokenKey, "")
    fun setNewAccessToken(newAccessToken: String) {
        sharedPreferences.edit()?.let {
            it.putString(accessTokenKey, newAccessToken)
            it.commit()
        }
    }
    fun clearTokenCache() {
        sharedPreferences.edit {
            remove(accessTokenKey)
        }
    }
    fun accessTokenExists(): Boolean {
        if (!accessToken.isSet())
            return false
        if (accessToken.get().isBlank())
            return false
        return true
    }
}