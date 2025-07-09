package pl.bartpos24.shopmobile.dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.Reusable
import pl.bartpos24.shopmobile.ShopMobileApplication
import javax.inject.Singleton

@Module(
    includes = [ViewModelModule::class],
    subcomponents = [IWorkerFactoryComponent::class]
)
class AppModule {
    @Reusable
    @Provides
    fun provideSharedPreferences(app: Application): SharedPreferences = app.getSharedPreferences("pl.bartpos24.shopmobile", Context.MODE_PRIVATE)
    @Singleton
    @Provides
    fun provideApplicationContext(app: Application): ShopMobileApplication = app as ShopMobileApplication
}