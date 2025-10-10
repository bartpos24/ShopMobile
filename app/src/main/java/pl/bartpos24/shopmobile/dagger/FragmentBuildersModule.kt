package pl.bartpos24.shopmobile.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import pl.bartpos24.shopmobile.ui.LoginFragment
import pl.bartpos24.shopmobile.ui.home.HomeFragment

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract  fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract  fun contributeLoginFragment(): LoginFragment
}