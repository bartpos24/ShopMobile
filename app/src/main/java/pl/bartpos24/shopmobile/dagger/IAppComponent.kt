package pl.bartpos24.shopmobile.dagger

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import pl.bartpos24.shopmobile.ShopMobileApplication

@Component(modules = [AndroidInjectionModule::class, AppModule::class, WorkerBuildersModule::class])
interface IAppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): IAppComponent
    }
    fun inject(application: ShopMobileApplication)

    fun workerFactoryComponent(): IWorkerFactoryComponent.Factory

    fun viewModelFactoryComponent(): IViewModelFactoryComponent.Factory
}