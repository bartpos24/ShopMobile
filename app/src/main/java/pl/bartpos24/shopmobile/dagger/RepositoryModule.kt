package pl.bartpos24.shopmobile.dagger

import android.app.Application
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import pl.bartpos24.shopmobile.repositories.TokenRepository
import pl.bartpos24.shopmobile.repositories.UserConfigRepository
import pl.bartpos24.shopmobile.utilities.TokenCache
import pl.bartpos24.web.api.LoginApi
import javax.inject.Singleton

@Module
object RepositoryModule {

    @Provides
    @Singleton
    @JvmStatic
    fun provideUserConfigRepository(sharedPreferences: SharedPreferences, moshi: Moshi): UserConfigRepository = UserConfigRepository(sharedPreferences, moshi)

    @Reusable
    @Provides
    @JvmStatic
    fun provideTokenRepository(loginApi : LoginApi, tokenCache: TokenCache, context: Application): TokenRepository = TokenRepository(loginApi, tokenCache, context)
}