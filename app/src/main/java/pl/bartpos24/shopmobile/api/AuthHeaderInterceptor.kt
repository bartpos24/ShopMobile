package pl.bartpos24.shopmobile.api

import dagger.Lazy
import okhttp3.Interceptor
import okhttp3.Response
import pl.bartpos24.shopmobile.repositories.TokenRepository

class AuthHeaderInterceptor(private val tokenRepository: Lazy<TokenRepository>) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            //.addHeader("Authorization", tokenRepository.get().getHeaderFormattedAccessToken())
            .build()
        return chain.proceed(request)
    }
}