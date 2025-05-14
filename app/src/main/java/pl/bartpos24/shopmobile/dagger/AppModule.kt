package pl.bartpos24.shopmobile.dagger

import android.app.Application
import dagger.Module
import dagger.Provides
import pl.bartpos24.shopmobile.ShopMobileApplication
import javax.inject.Singleton

@Module(
    //includes = [ViewModelModule::class],
    subcomponents = [IWorkerFactoryComponent::class]
)
class AppModule {
    @Singleton
    @Provides
    fun provideApplicationContext(app: Application): ShopMobileApplication = app as ShopMobileApplication
}