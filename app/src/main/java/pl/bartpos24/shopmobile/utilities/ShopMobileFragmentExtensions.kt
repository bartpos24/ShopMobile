package pl.bartpos24.shopmobile.utilities

import androidx.annotation.IdRes
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.fragment.findNavController
import okhttp3.Credentials
import pl.bartpos24.shopmobile.ShopMobileApplication
import pl.bartpos24.shopmobile.viewmodels.ShopMobileViewModelFactory

@MainThread
inline fun <reified VM : ViewModel> Fragment.navGraphShopMobileViewModels(
    @IdRes navGraphId: Int,
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val backStackEntry by lazy {
        findNavController().getBackStackEntry(navGraphId)
    }

    val storeProducer: () -> ViewModelStore = {
        backStackEntry.viewModelStore
    }

//    val viewModelFactory by lazy {
//        ShopMobileViewModelFactory(
//            (requireActivity().application as ShopMobileApplication).appComponent,
//            backStackEntry,
//            backStackEntry.arguments
//        )
//    }
    val defaultFactoryProducer: () -> ViewModelProvider.Factory = {
        ShopMobileViewModelFactory(
            (requireActivity().application as ShopMobileApplication).appComponent,
            backStackEntry,
            backStackEntry.arguments
        )
    }
    val extrasProducer: () -> CreationExtras = {
        backStackEntry.defaultViewModelCreationExtras
    }
    return createViewModelLazy(
        VM::class,
        storeProducer,
        extrasProducer,
        factoryProducer ?: defaultFactoryProducer
    )
}