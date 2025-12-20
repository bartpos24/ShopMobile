package pl.bartpos24.shopmobile.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import pl.bartpos24.shopmobile.ui.BarcodeScannerFragment
import pl.bartpos24.shopmobile.ui.FindProductFragment
import pl.bartpos24.shopmobile.ui.inventory.InventoryPositionFragment
import pl.bartpos24.shopmobile.ui.LoginFragment
import pl.bartpos24.shopmobile.ui.SettingsFragment
import pl.bartpos24.shopmobile.ui.home.HomeFragment
import pl.bartpos24.shopmobile.ui.inventory.CommonInventoryPositionFragment
import pl.bartpos24.shopmobile.ui.inventory.InventoryFragment

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract  fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract  fun contributeLoginFragment(): LoginFragment
    @ContributesAndroidInjector
    abstract  fun contributeBarcodeScannerFragment(): BarcodeScannerFragment
    @ContributesAndroidInjector
    abstract  fun contributeFindProductFragment(): FindProductFragment
    @ContributesAndroidInjector
    abstract  fun contributeInventoryPositionFragment(): InventoryPositionFragment
    @ContributesAndroidInjector
    abstract  fun contributeSettingsFragment(): SettingsFragment
    @ContributesAndroidInjector
    abstract fun contributeInventoryFragment(): InventoryFragment
    @ContributesAndroidInjector
    abstract  fun contributeCommonInventoryPositionFragment(): CommonInventoryPositionFragment
}