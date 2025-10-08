package pl.bartpos24.shopmobile.api

import pl.bartpos24.shopmobile.repositories.TokenRepository
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import pl.bartpos24.shopmobile.utilities.createAuthorizationHeader
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import pl.bartpos24.shopmobile.MainActivity
import pl.bartpos24.shopmobile.utilities.LoginStatus
import timber.log.Timber

class OauthRefreshAuthenticator(private val tokenRepository: Lazy<TokenRepository>) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        Timber.d("Detected authentication error ${response.code } on ${response.request.url}")
        return reAuthenticateRequestUsingRefreshToken(response.request).also {
            Timber.d("New request ${it?.body} ${it?.url} ")
        }
    }

    @Synchronized
    private fun reAuthenticateRequestUsingRefreshToken(staleRequest: Request): Request? {
        return tokenRepository.get()?.let {
            if (it.getAccessToken().get().isBlank()) {
                return null
                MainActivity.loginAuth.setStatus(LoginStatus.UNAUTHENTICATED)
            }
            if(staleRequest.header("Authorization") == it.getHeaderFormattedAccessToken()) {
                Timber.d("Obtaining new authorization token.")
                runBlocking {
                    it.refreshAccessToken()?.let { newToken ->
                        Timber.d("Obtained $newToken")
                        it.setNewAccessToken(newToken)
                        staleRequest.newBuilder()
                            .header("Authorization", createAuthorizationHeader(newToken))
                            .build()
                    }
                }
            } else {
                Timber.d("New access token was already obtained. Changed from ${staleRequest.header("Authorization")} to ${it.getHeaderFormattedAccessToken()}")
                staleRequest.newBuilder()
                    .header("Authorization", it.getHeaderFormattedAccessToken())
                    .build()
            }
        }
    }
}