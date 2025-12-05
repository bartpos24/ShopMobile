package pl.bartpos24.shopmobile.workers

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.bartpos24.shopmobile.MainActivity
import pl.bartpos24.shopmobile.repositories.TokenRepository
import pl.bartpos24.shopmobile.utilities.LoginStatus
import pl.bartpos24.shopmobile.utilities.TokenCache
import pl.bartpos24.shopmobile.utilities.accessTokenKey
import pl.bartpos24.shopmobile.utilities.safeApiResult
import pl.bartpos24.shopmobile.utilities.workerMaxRetryNumber
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.infrastructure.ServerException
import pl.bartpos24.web.api.LoginApi
import java.io.IOException
import javax.inject.Inject

class RefreshTokenWorker @Inject constructor(
    context: Context,
    workerParameters: WorkerParameters,
    private val tokenRepository: TokenRepository,
    private val tokenCache: TokenCache,
    private val loginApi: LoginApi
) : CoroutineWorker(context, workerParameters) {

    @SuppressLint("HardwareIds")
    override suspend fun doWork(): Result {
        //Timber.d("RefreshTokenWorker started")
        val accessToken = tokenRepository.getAccessToken().get()

        if (accessToken.isEmpty() || runAttemptCount > workerMaxRetryNumber)
            return Result.failure()

        val apiResult = safeApiResult {
            withContext(Dispatchers.IO) {
                loginApi.apiLoginRefreshPost(
                    body = accessToken,
                    SSAID = Settings.Secure.getString(
                        applicationContext.contentResolver, Settings.Secure.ANDROID_ID
                    )
                )
            }
        }

        return with(apiResult) {
            when {
                isSuccess -> {
                    /**
                     * If worker successfully obtains a new token pair, it will replace currently stored pair in cache
                     */
                    var accessToken = getOrNull()
                    accessToken?.let { newAccessToken ->
                        withContext(Dispatchers.IO) {
                            tokenCache.setNewAccessToken(newAccessToken)
                        }
                    }
                    Result.success(
                        workDataOf(
                            accessTokenKey to accessToken
                        )
                    )
                }
                isFailure -> {
                    /**
                     * if worker fails to obtain a new token pair for any reason,
                     * it clears current token cache, logouts user from WebApi and exits the app
                     */
                    tokenCache.clearTokenCache()
                    tokenRepository.logout()
                    MainActivity.loginAuth.setStatus(LoginStatus.UNAUTHENTICATED)
                    when (val exception = exceptionOrNull()) {
                        is IOException -> Result.retry()
                        is ClientException -> Result.failure()
                        is ServerException -> Result.retry()
                        else -> Result.retry()
                    }
                    // exitProcess(0)
                }
                else -> Result.retry()
            }
        }
    }
}