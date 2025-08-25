package pl.bartpos24.shopmobile.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import pl.bartpos24.shopmobile.MainActivity

@Module
abstract class ActivityBuildersModule {
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
}