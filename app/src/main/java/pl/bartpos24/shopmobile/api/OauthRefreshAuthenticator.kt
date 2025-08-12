package pl.bartpos24.shopmobile.api

import pl.bartpos24.shopmobile.repositories.TokenRepository
import dagger.Lazy
import kotlinx.coroutines.runBlocking
//import net.aspekt.rewistamobile.MainActivity
//import net.aspekt.rewistamobile.repositories.TokenRepository
//import net.aspekt.rewistamobile.utilities.LoginStatus
//import net.aspekt.rewistamobile.utilities.createAuthorizationHeader
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import pl.bartpos24.shopmobile.MainActivity
import pl.bartpos24.shopmobile.utilities.LoginStatus
import timber.log.Timber
//class OauthRefreshAuthenticator(private val tokenRepository: Lazy<TokenRepository>) : Authenticator {
//    override fun authenticate(route: Route?, response: Response): Request? {
//        TODO("Not yet implemented")
//    }
//}

//class OauthRefreshAuthenticator(private val tokenRepository: Lazy<TokenRepository>) : Authenticator {
//    override fun authenticate(route: Route?, response: Response): Request? {
//        Timber.d("Detected authentication error ${response.code } on ${response.request.url}")
//        return reAuthenticateRequestUsingRefreshToken(response.request).also {
//            Timber.d("New request ${it?.body} ${it?.url} ")
//        }
//    }

//    @Synchronized
//    private fun reAuthenticateRequestUsingRefreshToken(staleRequest: Request): Request? {
//        return tokenRepository.get()?.let {
//            if (it.getRefreshToken().get().isBlank()) {
//                return null
//                MainActivity.loginAuth.setStatus(LoginStatus.UNAUTHENTICATED)
//            }
//            if (staleRequest.header("Authorization") == it.getHeaderFormattedAccessToken()) {
//                Timber.d("Obtaining new authorization token.")
//                runBlocking {
//                    it.refreshAccessToken()?.let { newTokenPair ->
//                        Timber.d("Obtained $newTokenPair")
//                        newTokenPair.refreshToken?.let { refreshToken -> it.setNewRefreshToken(refreshToken) }
//                        staleRequest.newBuilder()
//                            .header("Authorization", createAuthorizationHeader(newTokenPair.accessToken!!))
//                            .build()
//                    }
//                }
//            } else {
//                Timber.d("New access token was already obtained. Changed from ${staleRequest.header("Authorization")} to ${it.getHeaderFormattedAccessToken()}")
//                staleRequest.newBuilder()
//                    .header("Authorization", it.getHeaderFormattedAccessToken())
//                    .build()
//            }
//        }
//    }
//}