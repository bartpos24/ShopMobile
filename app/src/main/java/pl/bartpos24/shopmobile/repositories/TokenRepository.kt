package pl.bartpos24.shopmobile.repositories

import android.annotation.SuppressLint
import android.app.Application
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import android.provider.Settings
import kotlinx.coroutines.withContext
import org.openapitools.client.infrastructure.ClientException
import pl.bartpos24.shopmobile.MainActivity
import pl.bartpos24.shopmobile.utilities.LoginStatus
import pl.bartpos24.shopmobile.utilities.TokenCache
import pl.bartpos24.shopmobile.utilities.createAuthorizationHeader
import pl.bartpos24.shopmobile.utilities.validateRefreshToken
import kotlin.coroutines.CoroutineContext

class TokenRepository(private val tokenCache: TokenCache, private val context: Application) : ShopMobileRepository() {
    fun getHeaderFormattedAccessToken() = createAuthorizationHeader(tokenCache.accessToken.get())
    fun getAccessToken() = tokenCache.accessToken
    fun getRefreshToken() = tokenCache.refreshToken
    fun setNewRefreshToken(newRefreshToken: String) = tokenCache.setNewRefreshToken(newRefreshToken)

//    @SuppressLint("HardwareIds")
//    suspend fun refreshAccessToken(): TokenPair? {
//        if (validateRefreshToken(tokenCache.refreshToken.get())) {
//            // times: Int = 10, initialDelay: Long = 250L, factor: Double = 1.2
//            return flow {
//                emit(
//                    refreshTokenApi(
//                        refreshToken = tokenCache.refreshToken.get(),
//                        ssaid = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
//                    )
//                )
//            }.retry(10) {
//                if (it is ClientException)
//                    false
//                else {
//                    delay(100L)
//                    true
//                }
//            }.catch {
//                tokenCache.clearTokenCache()
//                MainActivity.loginAuth.setStatus(LoginStatus.UNAUTHENTICATED)
//            }
//                .singleOrNull()
//        } else {
//            tokenCache.clearTokenCache()
//            MainActivity.loginAuth.setStatus(LoginStatus.UNAUTHENTICATED)
//        }
//        return null
//    }

    private suspend fun refreshTokenApi(refreshToken: String, ssaid: String, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        //tokenApi.apiTokenRefreshPost(body = refreshToken, SSAID = ssaid, apiVersion = null)
    }
}