package pl.bartpos24.shopmobile.dagger

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
//import org.openapitools.client.infrastructure.ByteArrayAdapter
import dagger.Lazy
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import pl.bartpos24.shopmobile.repositories.UserConfigRepository
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
object NetworkInfrastructureModule {

    @Provides
    @Named("apiBasePath")
    @JvmStatic
    fun provideApiBasePath(userConfigRepository: UserConfigRepository): String = userConfigRepository.userConfig.value.webApiUrl

//    @Provides
//    @Singleton
//    @JvmStatic
//    fun provideOkHttpClient(tokenRepository: Lazy<TokenRepository>): OkHttpClient = OkHttpClient.Builder()
//        .connectTimeout(25, TimeUnit.SECONDS)
//        .writeTimeout(25, TimeUnit.SECONDS)
//        .readTimeout(180, TimeUnit.SECONDS)
//        .pingInterval(2, TimeUnit.SECONDS)
//        .authenticator(OauthRefreshAuthenticator(tokenRepository))
//        .addInterceptor(AuthHeaderInterceptor(tokenRepository))
//        .addInterceptor(
//            HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
//                override fun log(message: String) {
//                    Timber.tag("OkHttp").d(message)
//                }
//            }).apply { level = HttpLoggingInterceptor.Level.BODY }
//        )
//        .addInterceptor(BrotliInterceptor)
//        .build()
//
//    @Provides
//    @Singleton
//    @JvmStatic
//    fun provideMoshi(): Moshi = Moshi.Builder()
//        .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
//        .add(org.threeten.bp.LocalDateTime::class.java, LocalDateTimeJsonAdapter().nullSafe())
//        .add(org.threeten.bp.LocalDate::class.java, LocalDateJsonAdapter().nullSafe())
//        .add(UUID::class.java, UuidJsonAdapter().nullSafe())
//        .add(ByteArray::class.java, ByteArrayAdapter())
//        .add(KotlinJsonAdapterFactory())
//        .build()
}