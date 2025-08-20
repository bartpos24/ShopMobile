package pl.bartpos24.shopmobile.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.bartpos24.shopmobile.utilities.TokenCache
import pl.bartpos24.shopmobile.utilities.safeApiResult
import pl.bartpos24.shopmobile.utilities.validateRefreshToken
import pl.bartpos24.web.api.LoginApi
import org.openapitools.client.infrastructure.ClientException
import org.openapitools.client.infrastructure.ServerException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class LogoutWorker @Inject constructor(context: Context, workerParameters: WorkerParameters, private val tokenApi: LoginApi, private val tokenCache: TokenCache) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        if (!tokenCache.refreshTokenExists() || !validateRefreshToken(tokenCache.refreshToken.get())) {
            tokenCache.clearTokenCache()
            Timber.i("User already logged out")
            return Result.success()
        }
        tokenCache.clearTokenCache()
        return Result.success()

//        val apiResult = safeApiResult { withContext(Dispatchers.IO) { tokenApi.apiTokenLogoutPost(apiVersion = null) } }
//        return with(apiResult) {
//            when {
//                isSuccess -> {
//                    tokenCache.clearTokenCache()
//                    Timber.i("Logged out successfully")
//                    Result.success()
//                }
//                isFailure ->
//                    when (val exception = exceptionOrNull()) {
//                        is IOException -> {
//                            Timber.e("IOException: ${exception.message}")
//                            Result.retry()
//                        }
//                        is ClientException -> {
//                            tokenCache.clearTokenCache()
//                            when (exception.clientError.statusCode) {
//                                404 -> {
//                                    Timber.i("Already logged out by server")
//                                    Result.success()
//                                }
//                                401 -> { // Invalid token, token already expired case(after refresh attempt)
//                                    Timber.i(exception, exception.clientError.body.toString())
//                                    Result.success()
//                                }
//                                else -> {
//                                    Timber.e(exception, "Client error: ${exception.clientError.body} ${exception.clientError.statusCode}")
//                                    Result.failure()
//                                }
//                            }
//                        }
//                        is ServerException -> {
//                            Timber.v(exception, "Server error: ${exception.serverError.body} ${exception.serverError.message}")
//                            Result.retry()
//                        }
//                        else -> {
//                            Timber.e(exception, "Unhandled exception: ${exception?.message}")
//                            Result.failure()
//                        }
//                    }
//                else -> {
//                    Timber.e("Result class error: $apiResult")
//                    Result.failure()
//                }
//            }
//        }
    }
}
