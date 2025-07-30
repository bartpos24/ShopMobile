package pl.bartpos24.shopmobile.utilities

import android.content.SharedPreferences
import android.system.Os.remove
import androidx.core.content.edit
import com.fredporciuncula.flow.preferences.FlowSharedPreferences

class TokenCache(private val sharedPreferences: SharedPreferences) {

    private val flowSharedPreferences = FlowSharedPreferences(sharedPreferences)

    val accessToken = flowSharedPreferences.getString(accessTokenKey, "")
    val refreshToken = flowSharedPreferences.getString(refreshTokenKey, "")

    fun setNewRefreshToken(newRefreshToken: String) {
        sharedPreferences.edit()?.let {
            it.putString(refreshTokenKey, newRefreshToken)
            it.commit()
        }
    }

    fun setNewAccessToken(newAccessToken: String) {
        sharedPreferences.edit()?.let {
            it.putString(accessTokenKey, newAccessToken)
            it.commit()
        }
    }

    fun clearTokenCache() {
        sharedPreferences.edit {
            remove(refreshTokenKey)
            remove(accessTokenKey)
        }
    }

    fun refreshTokenExists(): Boolean {
        if (!refreshToken.isSet())
            return false
        if (refreshToken.get().isBlank())
            return false
        return true
    }

    fun accessTokenExists(): Boolean {
        if (!accessToken.isSet())
            return false
        if (accessToken.get().isBlank())
            return false
        return true
    }
}