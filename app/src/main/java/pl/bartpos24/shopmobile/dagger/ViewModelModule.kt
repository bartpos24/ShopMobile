package pl.bartpos24.shopmobile.dagger

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import pl.bartpos24.shopmobile.viewmodels.BarcodeScannerViewModel
import pl.bartpos24.shopmobile.viewmodels.FindProductViewModel
import pl.bartpos24.shopmobile.viewmodels.LoginViewModel
import pl.bartpos24.shopmobile.viewmodels.MainActivityViewModel

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ClassKey(MainActivityViewModel::class)
    abstract fun bindMainActivityViewModel(mainActivityViewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ClassKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ClassKey(BarcodeScannerViewModel::class)
    abstract fun bindBarcodeScannerViewModel(barcodeScannerViewModel: BarcodeScannerViewModel): ViewModel

    @Binds
    @IntoMap
    @ClassKey(FindProductViewModel::class)
    abstract fun bindFindProductViewModel(findProductViewModel: FindProductViewModel): ViewModel
}