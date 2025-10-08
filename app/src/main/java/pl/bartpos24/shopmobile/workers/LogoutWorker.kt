package pl.bartpos24.shopmobile.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.openapitools.client.infrastructure.ClientError
import pl.bartpos24.shopmobile.utilities.TokenCache
import pl.bartpos24.shopmobile.utilities.safeApiResult
import pl.bartpos24.web.api.LoginApi
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.infrastructure.ServerError
import org.openapitools.client.infrastructure.ServerException
import pl.bartpos24.shopmobile.utilities.validateAccessToken
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class LogoutWorker @Inject constructor(context: Context, workerParameters: WorkerParameters, private val tokenApi: LoginApi, private val tokenCache: TokenCache) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        if (!tokenCache.accessTokenExists() || !validateAccessToken(tokenCache.accessToken.get())) {
            tokenCache.clearTokenCache()
            Timber.i("User already logged out")
            return Result.success()
        }

        val apiResult = safeApiResult { withContext(Dispatchers.IO) { tokenApi.apiLoginLogoutPost() } }
        return with(apiResult) {
            when {
                isSuccess -> {
                    tokenCache.clearTokenCache()
                    Timber.i("Logged out successfully")
                    Result.success()
                }
                isFailure ->
                    when (val exception = exceptionOrNull()) {
                        is IOException -> {
                            Timber.e("IOException: ${exception.message}")
                            Result.retry()
                        }
                        is ClientException -> {
                            tokenCache.clearTokenCache()
                            when (exception.statusCode) {
                                404 -> {
                                    Timber.i("Already logged out by server")
                                    Result.success()
                                }
                                401 -> { // Invalid token, token already expired case(after refresh attempt)
                                    var clientError = exception.response as ClientError<*>
                                    Timber.i(exception, clientError.body.toString())
                                    Result.success()
                                }
                                else -> {
                                    var clientError = exception.response as ClientError<*>
                                    Timber.e(exception, "Client error: ${clientError.body} ${clientError.statusCode}")
                                    Result.failure()
                                }
                            }
                        }
                        is ServerException -> {
                            var serverError = exception.response as ServerError<*>
                            Timber.v(exception, "Server error: ${serverError.body} ${serverError.message}")
                            Result.retry()
                        }
                        else -> {
                            Timber.e(exception, "Unhandled exception: ${exception?.message}")
                            Result.failure()
                        }
                    }
                else -> {
                    Timber.e("Result class error: $apiResult")
                    Result.failure()
                }
            }
        }
    }
}
