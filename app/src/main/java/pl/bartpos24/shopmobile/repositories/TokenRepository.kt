package pl.bartpos24.shopmobile.repositories

import android.annotation.SuppressLint
import android.app.Application
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import android.provider.Settings
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.withContext
import org.openapitools.client.infrastructure.ClientException
import pl.bartpos24.web.model.TokenResponse
import pl.bartpos24.shopmobile.MainActivity
import pl.bartpos24.shopmobile.utilities.LoginStatus
import pl.bartpos24.shopmobile.utilities.TokenCache
import pl.bartpos24.shopmobile.utilities.createAuthorizationHeader
import pl.bartpos24.shopmobile.utilities.logoutWorkerUUID
import pl.bartpos24.shopmobile.utilities.validateRefreshToken
import pl.bartpos24.shopmobile.utilities.workerBackoffDelay
import pl.bartpos24.shopmobile.utilities.refreshTokenWorkerUUID
import pl.bartpos24.shopmobile.workers.LogoutWorker
import pl.bartpos24.web.api.LoginApi
import pl.bartpos24.web.model.ELoginType
import pl.bartpos24.web.model.LoginModel
import pl.bartpos24.shopmobile.workers.RefreshTokenWorker
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class TokenRepository(private val loginApi: LoginApi, private val tokenCache: TokenCache, private val context: Application) : ShopMobileRepository() {
    fun getHeaderFormattedAccessToken() = createAuthorizationHeader(tokenCache.accessToken.get())
    fun getAccessToken() = tokenCache.accessToken
    fun getRefreshToken() = tokenCache.refreshToken
    fun setNewRefreshToken(newRefreshToken: String) = tokenCache.setNewRefreshToken(newRefreshToken)

    fun login(login: String, password: String, ssaid: String) = flow {
        emit(loginApi(login, password, ssaid))
    }.onStart { cancelLogout() }
        .onEach {
            tokenCache.accessToken.asCollector().emit(it.accessToken.orEmpty())
            tokenCache.refreshToken.asCollector().emit(it.refreshToken.orEmpty())
        }

    fun logout() = logoutWorker()

    private fun cancelLogout() {
        WorkManager.getInstance(context).cancelUniqueWork(logoutWorkerUUID)
    }

    @SuppressLint("HardwareIds")
    suspend fun refreshAccessToken(): TokenResponse? {
        if (validateRefreshToken(tokenCache.refreshToken.get())) {
            // times: Int = 10, initialDelay: Long = 250L, factor: Double = 1.2
            return TokenResponse(
                accessToken = tokenCache.accessToken.get(),
                refreshToken = tokenCache.refreshToken.get(),
                tokenType = "Bearer"
            )
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
        } else {
            tokenCache.clearTokenCache()
            MainActivity.loginAuth.setStatus(LoginStatus.UNAUTHENTICATED)
        }
        return null
    }

    private fun logoutWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val worker = OneTimeWorkRequestBuilder<LogoutWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, workerBackoffDelay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(logoutWorkerUUID, ExistingWorkPolicy.KEEP, worker)
    }

    fun createRefreshTokenWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val worker = PeriodicWorkRequestBuilder<RefreshTokenWorker>(repeatInterval = 15, repeatIntervalTimeUnit = TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, workerBackoffDelay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(refreshTokenWorkerUUID, ExistingPeriodicWorkPolicy.KEEP, worker)
    }

    private suspend fun loginApi(login: String, password: String, ssaid: String, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        loginApi.apiLoginLoginPost(loginModel = LoginModel(login, password, ssaid, "Mobile"))
    }

//    private suspend fun refreshTokenApi(refreshToken: String, ssaid: String, context: CoroutineContext = coroutineContext) = withContext(context = context) {
//        loginApi.apiTokenRefreshPost(body = refreshToken, SSAID = ssaid, apiVersion = null)
//    }
}