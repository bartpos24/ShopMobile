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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.withContext
import org.openapitools.client.infrastructure.ClientException
import pl.bartpos24.shopmobile.MainActivity
import pl.bartpos24.shopmobile.utilities.LoginStatus
import pl.bartpos24.shopmobile.utilities.TokenCache
import pl.bartpos24.shopmobile.utilities.createAuthorizationHeader
import pl.bartpos24.shopmobile.utilities.logoutWorkerName
import pl.bartpos24.shopmobile.utilities.workerBackoffDelay
import pl.bartpos24.shopmobile.utilities.refreshTokenWorkerName
import pl.bartpos24.shopmobile.utilities.validateAccessToken
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
    fun setNewAccessToken(newAccessToken: String) = tokenCache.setNewAccessToken(newAccessToken)

    fun login(login: String, password: String, ssaid: String) = flow {
        emit(loginApi(login, password, ssaid))
    }.onStart { cancelLogout() }
        .onEach {
            tokenCache.accessToken.asCollector().emit(it)
        }

    fun refreshToken() = flow {
        emit(refreshTokenApi(
            refreshToken = tokenCache.accessToken.get(),
            ssaid = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        ))
    }

    fun logout() = logoutWorker()

    private fun cancelLogout() {
        WorkManager.getInstance(context).cancelUniqueWork(logoutWorkerName)
    }

    @SuppressLint("HardwareIds")
    suspend fun refreshAccessToken(): String? {
        if (validateAccessToken(tokenCache.accessToken.get())) {
            // times: Int = 10, initialDelay: Long = 250L, factor: Double = 1.2
            return flow {
                emit(refreshTokenApi(
                    refreshToken = tokenCache.accessToken.get(),
                    ssaid = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                ))
            }.retry(10) {
                if (it is ClientException)
                    false
                else {
                    delay(100L)
                    true
                }
            }.catch {
                tokenCache.clearTokenCache()
                MainActivity.loginAuth.setStatus(LoginStatus.UNAUTHENTICATED)
            }.singleOrNull()
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
        WorkManager.getInstance(context).enqueueUniqueWork(logoutWorkerName, ExistingWorkPolicy.KEEP, worker)
    }

    fun createRefreshTokenWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val worker = PeriodicWorkRequestBuilder<RefreshTokenWorker>(repeatInterval = 15, repeatIntervalTimeUnit = TimeUnit.SECONDS)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.LINEAR, workerBackoffDelay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(refreshTokenWorkerName, ExistingPeriodicWorkPolicy.KEEP, worker)
    }

    private suspend fun loginApi(login: String, password: String, ssaid: String, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        loginApi.apiLoginLoginPost(loginModel = LoginModel(login, password, ssaid, ELoginType.Mobile))
    }

    private suspend fun logoutApi(context: CoroutineContext = coroutineContext) = withContext(context = context) {
        loginApi.apiLoginLogoutPost()
    }
    private suspend fun refreshTokenApi(refreshToken: String, ssaid: String, context: CoroutineContext = coroutineContext) = withContext(context = context) {
        loginApi.apiLoginRefreshPost(body = refreshToken, SSAID = ssaid)
    }
}