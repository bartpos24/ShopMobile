package pl.bartpos24.shopmobile.dagger

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import pl.bartpos24.web.api.LoginApi
import pl.bartpos24.web.api.ProductApi
import javax.inject.Named

@Module(includes = [NetworkInfrastructureModule::class])
object NetworkApiModule {
    @Reusable
    @Provides
    @JvmStatic
    fun provideLoginApi(okHttpClient: OkHttpClient, moshi: Moshi, @Named("apiBasePath") apiBasePath: String): LoginApi = LoginApi(apiBasePath, okHttpClient)

    @Reusable
    @Provides
    @JvmStatic
    fun provideProductApi(okHttpClient: OkHttpClient, moshi: Moshi, @Named("apiBasePath") apiBasePath: String): ProductApi = ProductApi(apiBasePath, okHttpClient)
}