package pl.bartpos24.shopmobile.dagger

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import pl.bartpos24.shopmobile.ShopMobileApplication
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, WorkerBuildersModule::class, RepositoryModule::class, NetworkInfrastructureModule::class, NetworkApiModule::class, ActivityBuildersModule::class, ScannerModule::class])
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
    fun inject(application: ShopMobileApplication)

    fun workerFactoryComponent(): IWorkerFactoryComponent.Factory

    fun viewModelFactoryComponent(): IViewModelFactoryComponent.Factory
}