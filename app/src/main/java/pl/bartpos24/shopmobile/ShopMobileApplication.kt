package pl.bartpos24.shopmobile

import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class ShopMobileApplication : Application(), HasAndroidInjector {
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    //lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        //appComponent = AppInjector.init(this)

        //AndroidThreeTen.init(this)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector
}