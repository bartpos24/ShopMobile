package pl.bartpos24.shopmobile.dagger

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import pl.bartpos24.shopmobile.repositories.TokenRepository
import pl.bartpos24.shopmobile.repositories.UserConfigRepository
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
    fun provideTokenRepository(): TokenRepository = TokenRepository()
}